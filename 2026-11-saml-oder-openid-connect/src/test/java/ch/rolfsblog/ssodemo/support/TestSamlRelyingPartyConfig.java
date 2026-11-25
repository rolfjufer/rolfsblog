package ch.rolfsblog.ssodemo.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;

/** The registration that {@code metadata-uri} would load, with keys of the test run. */
@TestConfiguration(proxyBeanMethods = false)
public class TestSamlRelyingPartyConfig {

    public static final String IDP_ENTITY_ID = "http://idp.test/realms/demo";
    public static final String IDP_SSO_URL = IDP_ENTITY_ID + "/protocol/saml";
    public static final KeyPairs.Signer IDP_KEY = KeyPairs.generate("keycloak");

    @Bean
    RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {
        KeyPairs.Signer application = KeyPairs.generate("application");
        RelyingPartyRegistration registration = RelyingPartyRegistration.withRegistrationId("keycloak")
                .entityId("{baseUrl}/saml2/service-provider-metadata/{registrationId}")
                .signingX509Credentials(credentials -> credentials.add(
                        Saml2X509Credential.signing(application.privateKey(), application.certificate())))
                .singleLogoutServiceLocation("{baseUrl}/logout/saml2/slo")
                .singleLogoutServiceResponseLocation("{baseUrl}/logout/saml2/slo")
                .singleLogoutServiceBinding(Saml2MessageBinding.POST)
                .assertingPartyMetadata(party -> party
                        .entityId(IDP_ENTITY_ID)
                        .singleSignOnServiceLocation(IDP_SSO_URL)
                        .singleSignOnServiceBinding(Saml2MessageBinding.REDIRECT)
                        .singleLogoutServiceLocation(IDP_SSO_URL)
                        .wantAuthnRequestsSigned(true)
                        .verificationX509Credentials(credentials -> credentials.add(
                                Saml2X509Credential.verification(IDP_KEY.certificate()))))
                .build();
        return new InMemoryRelyingPartyRegistrationRepository(registration);
    }
}
