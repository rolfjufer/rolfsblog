package ch.rolfsblog.ssodemo.web;

import java.util.List;

import ch.rolfsblog.ssodemo.support.FakeOidcProvider;
import ch.rolfsblog.ssodemo.support.TestOidcClientConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static ch.rolfsblog.ssodemo.support.RedirectAssertions.queryParam;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** The OIDC login without a running Keycloak, including a complete code flow against a fake provider. */
@SpringBootTest(properties = "app.sso.protocol=oidc")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestOidcClientConfig.class)
class OidcLoginFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Startseite ist öffentlich und zeigt das aktive Protokoll")
    void homePageIsPublic() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("OpenID Connect")));
    }

    @Test
    @DisplayName("Geschützte Seite leitet zum OIDC-Login weiter")
    void protectedPageStartsOidcLogin() throws Exception {
        MvcResult result = mockMvc.perform(get("/me"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        assertThat(result.getResponse().getRedirectedUrl()).endsWith("/oauth2/authorization/keycloak");
    }

    @Test
    @DisplayName("Authorization Request: openid-Scope, state, nonce, PKCE und exakte redirect_uri")
    void authorizationRequestIsWellFormed() throws Exception {
        MvcResult result = mockMvc.perform(get("/oauth2/authorization/keycloak"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        String location = result.getResponse().getRedirectedUrl();

        assertThat(location).startsWith(TestOidcClientConfig.AUTHORIZATION_URI);
        assertThat(queryParam(location, "response_type")).isEqualTo("code");
        assertThat(queryParam(location, "client_id")).isEqualTo("saml-vs-oidc");
        // Mistake 1 in the blog post: without "openid" there is no ID token.
        assertThat(queryParam(location, "scope")).contains("openid");
        // Mistake 3 in the blog post: the redirect_uri must match exactly.
        assertThat(queryParam(location, "redirect_uri"))
                .isEqualTo("http://localhost/login/oauth2/code/keycloak");
        assertThat(queryParam(location, "state")).isNotBlank();
        assertThat(queryParam(location, "nonce")).isNotBlank();
        assertThat(queryParam(location, "code_challenge_method")).isEqualTo("S256");
    }

    @Test
    @DisplayName("/dozierende: jane (Gruppe dozierende) darf hinein")
    void lecturerMayEnter() throws Exception {
        mockMvc.perform(get("/dozierende").session(login("jane", "dozierende")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("/dozierende: max (Gruppe studierende) ist angemeldet und erhält 403")
    void studentIsForbidden() throws Exception {
        MockHttpSession session = login("max", "studierende");
        mockMvc.perform(get("/me").session(session)).andExpect(status().isOk());
        mockMvc.perform(get("/dozierende").session(session)).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Unbekannte Gruppen werden nicht zu Rollen")
    void unknownGroupsAreIgnored() throws Exception {
        mockMvc.perform(get("/api/me").session(login("jane", "dozierende", "admin")))
                .andExpect(jsonPath("$.authorities").value(hasItem("ROLE_DOZIERENDE")))
                .andExpect(jsonPath("$.authorities").value(not(hasItem("ROLE_ADMIN"))));
    }

    @Test
    @DisplayName("/api/me liefert die Claims aus dem ID Token")
    void apiReturnsIdTokenClaims() throws Exception {
        mockMvc.perform(get("/api/me")
                        .with(oidcLogin()
                                .clientRegistration(TestOidcClientConfig.KEYCLOAK)
                                .idToken(token -> token.claim("email", "jane@example.ch"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.protocol").value("OpenID Connect"))
                .andExpect(jsonPath("$.attributes.email").value("jane@example.ch"));
    }

    @Test
    @DisplayName("Logout beendet auch die Session beim IdP (RP-Initiated Logout)")
    void logoutRedirectsToEndSessionEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(post("/logout")
                        .with(csrf())
                        .with(oidcLogin().clientRegistration(TestOidcClientConfig.KEYCLOAK)))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        String location = result.getResponse().getRedirectedUrl();

        assertThat(location).startsWith(TestOidcClientConfig.END_SESSION_URI);
        assertThat(queryParam(location, "id_token_hint")).isNotBlank();
        assertThat(queryParam(location, "post_logout_redirect_uri")).isEqualTo("http://localhost/");
    }

    /** A complete code flow: Spring exchanges the code and validates the ID token. */
    private MockHttpSession login(String user, String... groups) throws Exception {
        MockHttpSession session = new MockHttpSession();
        String location = mockMvc.perform(get("/oauth2/authorization/keycloak").session(session))
                .andReturn().getResponse().getRedirectedUrl();
        FakeOidcProvider.nextLogin(user, List.of(groups), queryParam(location, "nonce"));
        mockMvc.perform(get("/login/oauth2/code/keycloak").session(session)
                        .param("code", "code").param("state", queryParam(location, "state")))
                .andExpect(redirectedUrl("/"));
        return session;
    }
}
