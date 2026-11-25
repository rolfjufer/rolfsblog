package ch.rolfsblog.ssodemo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Login with OpenID Connect:
 * <ol>
 *   <li>Access to {@code /me} starts the login at {@code /oauth2/authorization/keycloak}.</li>
 *   <li>Spring redirects to Keycloak with {@code state}, {@code nonce} and PKCE challenge.</li>
 *   <li>The browser returns to {@code /login/oauth2/code/keycloak} with a one-time code.</li>
 *   <li>Spring exchanges the code for tokens in the back channel and validates the ID token.</li>
 * </ol>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "app.sso.protocol", havingValue = "oidc")
class OidcSecurityConfig {

    @Bean
    SecurityFilterChain oidcSecurityFilterChain(HttpSecurity http,
            ClientRegistrationRepository clientRegistrations) throws Exception {
        // Ends the session in Keycloak as well. Otherwise the next click would log
        // the user in again without a password.
        OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrations);
        logoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");

        http
            .authorizeHttpRequests(AccessRules::apply)
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userAuthoritiesMapper(new OidcGroupAuthoritiesMapper())))
            .logout(logout -> logout.logoutSuccessHandler(logoutSuccessHandler));
        return http.build();
    }
}
