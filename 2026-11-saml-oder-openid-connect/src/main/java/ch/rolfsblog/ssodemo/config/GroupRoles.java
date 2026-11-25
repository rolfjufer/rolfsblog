package ch.rolfsblog.ssodemo.config;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Maps the groups of the identity provider to roles of this application. Only the
 * groups listed here count. Turning every group into a role would let the
 * identity provider hand out any authority, for example with a group "admin".
 */
final class GroupRoles {

    private static final Map<String, String> ROLE_BY_GROUP = Map.of(
            "dozierende", "ROLE_DOZIERENDE",
            "studierende", "ROLE_STUDIERENDE");

    private GroupRoles() {
    }

    static Set<GrantedAuthority> of(Collection<?> groups) {
        return groups.stream()
                .map(String::valueOf)
                .map(ROLE_BY_GROUP::get)
                .filter(Objects::nonNull)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
