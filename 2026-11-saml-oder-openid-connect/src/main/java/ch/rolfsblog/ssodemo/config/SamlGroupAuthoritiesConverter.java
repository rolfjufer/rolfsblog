package ch.rolfsblog.ssodemo.config;

import java.util.List;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml5AuthenticationProvider.ResponseAuthenticationConverter;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml5AuthenticationProvider.ResponseToken;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2ResponseAssertionAccessor;

/** Adds the roles from the attribute {@code groups} of the assertion. */
class SamlGroupAuthoritiesConverter implements Converter<ResponseToken, Saml2Authentication> {

    private final ResponseAuthenticationConverter delegate = new ResponseAuthenticationConverter();

    @Override
    public Saml2Authentication convert(ResponseToken responseToken) {
        Saml2Authentication authentication = delegate.convert(responseToken);
        if (!(authentication.getCredentials() instanceof Saml2ResponseAssertionAccessor assertion)) {
            return authentication;
        }
        List<Object> groups = assertion.getAttributes().getOrDefault("groups", List.of());
        Set<GrantedAuthority> roles = GroupRoles.of(groups);
        return (Saml2Authentication) authentication.toBuilder()
                .authorities(authorities -> authorities.addAll(roles))
                .build();
    }
}
