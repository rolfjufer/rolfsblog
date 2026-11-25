package ch.rolfsblog.ssodemo.config;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

/** Adds the roles from the claim {@code groups} of the ID token. */
class OidcGroupAuthoritiesMapper implements GrantedAuthoritiesMapper {

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        Set<GrantedAuthority> mapped = new LinkedHashSet<>(authorities);
        for (GrantedAuthority authority : authorities) {
            if (authority instanceof OidcUserAuthority oidcAuthority) {
                List<String> groups = oidcAuthority.getIdToken().getClaimAsStringList("groups");
                if (groups != null) {
                    mapped.addAll(GroupRoles.of(groups));
                }
            }
        }
        return mapped;
    }
}
