package ch.rolfsblog.ssodemo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml5AuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Login with SAML 2.0. In Spring Security this application is the <em>relying
 * party</em> (service provider) and Keycloak the <em>asserting party</em>
 * (identity provider):
 * <ol>
 *   <li>Access to {@code /me} starts the login.</li>
 *   <li>Spring sends the browser to Keycloak with a signed {@code AuthnRequest}.</li>
 *   <li>Keycloak returns a form with the signed response, which the browser posts
 *       to {@code /login/saml2/sso/keycloak}.</li>
 *   <li>Spring validates it with the certificate from Keycloak's metadata.</li>
 * </ol>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "app.sso.protocol", havingValue = "saml")
class SamlSecurityConfig {

    @Bean
    SecurityFilterChain samlSecurityFilterChain(HttpSecurity http) throws Exception {
        OpenSaml5AuthenticationProvider provider = new OpenSaml5AuthenticationProvider();
        provider.setResponseAuthenticationConverter(new SamlGroupAuthoritiesConverter());

        http
            .authorizeHttpRequests(AccessRules::apply)
            .saml2Login(saml2 -> saml2.authenticationManager(new ProviderManager(provider)))
            // POST /logout sends a signed LogoutRequest to Keycloak.
            .saml2Logout(Customizer.withDefaults())
            // /saml2/metadata/keycloak, for the import in Keycloak.
            .saml2Metadata(Customizer.withDefaults())
            .logout(logout -> logout.logoutSuccessUrl("/"));
        return http.build();
    }
}
