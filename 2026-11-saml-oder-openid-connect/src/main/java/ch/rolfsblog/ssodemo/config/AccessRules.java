package ch.rolfsblog.ssodemo.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * The access rules. Both security configurations use exactly these: from here on
 * it no longer matters which protocol proved the identity.
 */
final class AccessRules {

    private AccessRules() {
    }

    static void apply(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry requests) {
        requests
            .requestMatchers("/", "/error", "/css/**").permitAll()
            .requestMatchers("/dozierende/**").hasRole("DOZIERENDE")
            .anyRequest().authenticated();
    }
}
