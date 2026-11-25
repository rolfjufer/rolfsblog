package ch.rolfsblog.ssodemo.support;

import java.util.Map;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

/** The registration that discovery would load, with the endpoints of {@link FakeOidcProvider}. */
@TestConfiguration(proxyBeanMethods = false)
public class TestOidcClientConfig {

    public static final String AUTHORIZATION_URI = FakeOidcProvider.ISSUER + "/protocol/openid-connect/auth";
    public static final String END_SESSION_URI = FakeOidcProvider.ISSUER + "/protocol/openid-connect/logout";

    public static final ClientRegistration KEYCLOAK = ClientRegistration.withRegistrationId("keycloak")
            .clientId("saml-vs-oidc")
            .clientSecret("test-secret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope("openid", "profile", "email")
            .issuerUri(FakeOidcProvider.ISSUER)
            .authorizationUri(AUTHORIZATION_URI)
            .tokenUri(FakeOidcProvider.url() + "/token")
            .jwkSetUri(FakeOidcProvider.url() + "/certs")
            .userInfoUri(FakeOidcProvider.url() + "/userinfo")
            .userNameAttributeName("preferred_username")
            .providerConfigurationMetadata(Map.of("end_session_endpoint", END_SESSION_URI))
            .build();

    @Bean
    ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(KEYCLOAK);
    }
}
