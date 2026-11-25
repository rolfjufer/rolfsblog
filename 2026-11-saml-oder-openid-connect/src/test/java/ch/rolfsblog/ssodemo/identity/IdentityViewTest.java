package ch.rolfsblog.ssodemo.identity;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import ch.rolfsblog.ssodemo.support.FakeSamlAssertion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import static org.assertj.core.api.Assertions.assertThat;

/** How an OIDC or SAML login becomes an {@link IdentityView}, without a Spring context. */
class IdentityViewTest {

    @Test
    @DisplayName("OIDC: Claims stammen aus dem ID Token")
    void mapsOidcUserFromIdToken() {
        OidcIdToken idToken = OidcIdToken.withTokenValue("id-token")
                .issuer("http://localhost:8080/realms/demo")
                .subject("7c1e5a2b-3f4d-4b8e-9a61-0d2f5e8c4b17")
                .audience(List.of("saml-vs-oidc"))
                .issuedAt(Instant.parse("2026-11-20T10:15:00Z"))
                .expiresAt(Instant.parse("2026-11-20T10:20:00Z"))
                .claim("email", "jane@example.ch")
                .build();
        DefaultOidcUser user = new DefaultOidcUser(AuthorityUtils.createAuthorityList("OIDC_USER"), idToken);
        OAuth2AuthenticationToken authentication =
                new OAuth2AuthenticationToken(user, user.getAuthorities(), "keycloak");

        IdentityView view = IdentityView.from(authentication);

        assertThat(view.protocol()).isEqualTo("OpenID Connect");
        assertThat(view.name()).isEqualTo("7c1e5a2b-3f4d-4b8e-9a61-0d2f5e8c4b17");
        assertThat(view.attributes()).containsEntry("email", "jane@example.ch")
                .containsKeys("iss", "sub", "aud", "iat", "exp");
        assertThat(view.authorities()).containsExactly("OIDC_USER");
    }

    @Test
    @DisplayName("SAML: Attribute stammen aus der Assertion (Credentials)")
    void mapsSamlAssertionFromCredentials() {
        FakeSamlAssertion assertion = new FakeSamlAssertion("jane",
                Map.of("email", List.<Object>of("jane@example.ch"),
                       "groups", List.<Object>of("dozierende")));
        TestingAuthenticationToken authentication =
                new TestingAuthenticationToken("jane", assertion, "ROLE_USER");

        IdentityView view = IdentityView.from(authentication);

        assertThat(view.protocol()).isEqualTo("SAML 2.0");
        assertThat(view.name()).isEqualTo("jane");
        assertThat(view.attributes())
                .containsEntry("NameID", "jane")
                .containsEntry("email", List.of("jane@example.ch"))
                .containsEntry("groups", List.of("dozierende"));
        assertThat(view.authorities()).containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Weder OIDC noch SAML: leere Attribute, kein Fehler")
    void handlesUnknownAuthentication() {
        TestingAuthenticationToken authentication =
                new TestingAuthenticationToken("someone", "password", "ROLE_USER");

        IdentityView view = IdentityView.from(authentication);

        assertThat(view.protocol()).isEqualTo("unbekannt");
        assertThat(view.attributes()).isEmpty();
    }
}
