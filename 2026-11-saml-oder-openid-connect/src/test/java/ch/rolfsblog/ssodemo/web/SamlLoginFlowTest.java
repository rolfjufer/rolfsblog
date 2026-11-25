package ch.rolfsblog.ssodemo.web;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import ch.rolfsblog.ssodemo.support.FakeSamlAssertion;
import ch.rolfsblog.ssodemo.support.KeyPairs;
import ch.rolfsblog.ssodemo.support.SamlResponses;
import ch.rolfsblog.ssodemo.support.TestSamlRelyingPartyConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static ch.rolfsblog.ssodemo.support.RedirectAssertions.pathAndQuery;
import static ch.rolfsblog.ssodemo.support.RedirectAssertions.queryParam;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** The SAML login without a running Keycloak, including signed responses. */
@SpringBootTest(properties = "app.sso.protocol=saml")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSamlRelyingPartyConfig.class)
class SamlLoginFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Startseite ist öffentlich und zeigt das aktive Protokoll")
    void homePageIsPublic() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("SAML 2.0")));
    }

    @Test
    @DisplayName("Geschützte Seite startet die SAML-Authentifizierung")
    void protectedPageStartsSamlAuthentication() throws Exception {
        MvcResult result = mockMvc.perform(get("/me"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        assertThat(result.getResponse().getRedirectedUrl()).contains("/saml2/authenticate");
    }

    @Test
    @DisplayName("AuthnRequest wird signiert per HTTP-Redirect an den IdP geschickt")
    void authnRequestIsSignedAndSentToIdp() throws Exception {
        String authenticateUrl = mockMvc.perform(get("/me"))
                .andReturn().getResponse().getRedirectedUrl();

        MvcResult result = mockMvc.perform(get(pathAndQuery(authenticateUrl)))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        String location = result.getResponse().getRedirectedUrl();

        assertThat(location).startsWith(TestSamlRelyingPartyConfig.IDP_SSO_URL);
        assertThat(queryParam(location, "SAMLRequest")).isNotBlank();
        assertThat(queryParam(location, "SigAlg")).isNotBlank();
        assertThat(queryParam(location, "Signature")).isNotBlank();
    }

    @Test
    @DisplayName("SP-Metadaten enthalten Entity ID und Assertion Consumer Service")
    void metadataEndpointDescribesServiceProvider() throws Exception {
        mockMvc.perform(get("/saml2/metadata/keycloak"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("EntityDescriptor")))
                .andExpect(content().string(containsString(
                        "http://localhost/saml2/service-provider-metadata/keycloak")))
                .andExpect(content().string(containsString(
                        "http://localhost/login/saml2/sso/keycloak")));
    }

    @Test
    @DisplayName("SP-Metadaten enthalten den Single-Logout-Endpunkt der App")
    void metadataContainsSingleLogoutService() throws Exception {
        // Without it Spring answers the LogoutResponse of the IdP with 401.
        mockMvc.perform(get("/saml2/metadata/keycloak"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("SingleLogoutService")))
                .andExpect(content().string(containsString(
                        "http://localhost/logout/saml2/slo")));
    }

    @Test
    @DisplayName("/api/me liefert die Attribute aus der Assertion")
    void apiReturnsAssertionAttributes() throws Exception {
        FakeSamlAssertion assertion = new FakeSamlAssertion("jane",
                Map.of("email", List.<Object>of("jane@example.ch")));
        TestingAuthenticationToken samlLogin =
                new TestingAuthenticationToken("jane", assertion, "ROLE_USER");

        mockMvc.perform(get("/api/me").with(authentication(samlLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.protocol").value("SAML 2.0"))
                .andExpect(jsonPath("$.attributes.NameID").value("jane"))
                .andExpect(jsonPath("$.attributes.email[0]").value("jane@example.ch"));
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
                .andExpect(jsonPath("$.authorities").value(contains(
                        "FACTOR_SAML_RESPONSE", "ROLE_DOZIERENDE", "ROLE_USER")));
    }

    @Test
    @DisplayName("Eine Antwort mit fremder Signatur meldet niemanden an")
    void foreignSignatureIsRejected() throws Exception {
        MockHttpSession session = new MockHttpSession();
        postResponse(session, SamlResponses.signedBy(KeyPairs.generate("attacker"),
                startLogin(session), "jane", List.of("dozierende")))
                .andExpect(redirectedUrl("/login?error"));
    }

    private MockHttpSession login(String user, String... groups) throws Exception {
        MockHttpSession session = new MockHttpSession();
        postResponse(session, SamlResponses.signedBy(TestSamlRelyingPartyConfig.IDP_KEY,
                startLogin(session), user, List.of(groups)))
                .andExpect(redirectedUrl("/"));
        return session;
    }

    /** Returns the ID of the AuthnRequest. */
    private String startLogin(MockHttpSession session) throws Exception {
        String location = mockMvc.perform(get("/saml2/authenticate")
                        .param("registrationId", "keycloak").session(session))
                .andReturn().getResponse().getRedirectedUrl();
        byte[] deflated = Base64.getDecoder().decode(queryParam(location, "SAMLRequest"));
        String request = new String(new InflaterInputStream(new ByteArrayInputStream(deflated),
                new Inflater(true)).readAllBytes(), StandardCharsets.UTF_8);
        Matcher id = Pattern.compile("\\sID=\"([^\"]+)\"").matcher(request);
        assertThat(id.find()).isTrue();
        return id.group(1);
    }

    private ResultActions postResponse(MockHttpSession session, String response) throws Exception {
        return mockMvc.perform(post("/login/saml2/sso/keycloak").session(session)
                .param("SAMLResponse", Base64.getEncoder()
                        .encodeToString(response.getBytes(StandardCharsets.UTF_8))));
    }
}
