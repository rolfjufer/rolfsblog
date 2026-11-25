package ch.rolfsblog.ssodemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param protocol the active protocol ({@code oidc} or {@code saml})
 */
@ConfigurationProperties(prefix = "app.sso")
public record SsoProperties(SsoProtocol protocol) {
}
