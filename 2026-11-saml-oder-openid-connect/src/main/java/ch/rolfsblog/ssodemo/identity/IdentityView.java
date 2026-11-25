package ch.rolfsblog.ssodemo.identity;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ch.rolfsblog.ssodemo.config.SsoProtocol;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.saml2.provider.service.authentication.Saml2ResponseAssertionAccessor;

/**
 * View of the logged-in user that does not depend on the protocol. It shows where
 * Spring keeps the proof of the identity provider:
 * <ul>
 *   <li><b>OIDC:</b> the principal is an {@link OidcUser} with the claims of the ID token.</li>
 *   <li><b>SAML:</b> since Spring Security 7 the credentials carry the data of the assertion
 *       ({@link Saml2ResponseAssertionAccessor}).</li>
 * </ul>
 *
 * @param protocol    display name of the protocol
 * @param name        user name according to Spring Security
 * @param attributes  claims (OIDC) or attributes (SAML), sorted by name
 * @param authorities granted authorities
 */
public record IdentityView(String protocol,
                           String name,
                           Map<String, Object> attributes,
                           List<String> authorities) {

    public static IdentityView from(Authentication authentication) {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();

        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            Map<String, Object> claims = new TreeMap<>(oidcUser.getIdToken().getClaims());
            return new IdentityView(SsoProtocol.OIDC.displayName(), authentication.getName(),
                    claims, authorities);
        }

        if (authentication.getCredentials() instanceof Saml2ResponseAssertionAccessor assertion) {
            Map<String, Object> attributes = new TreeMap<>();
            assertion.getAttributes().forEach(attributes::put);
            attributes.put("NameID", assertion.getNameId());
            return new IdentityView(SsoProtocol.SAML.displayName(), authentication.getName(),
                    attributes, authorities);
        }

        return new IdentityView("unbekannt", authentication.getName(), Map.of(), authorities);
    }
}
