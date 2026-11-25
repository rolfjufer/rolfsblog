package ch.rolfsblog.ssodemo.support;

import java.util.List;
import java.util.Map;

import org.springframework.security.saml2.provider.service.authentication.Saml2ResponseAssertionAccessor;

/**
 * Minimal assertion for tests that do not need a signed response.
 *
 * @param nameId     NameID of the subject
 * @param attributes attributes of the assertion
 */
public record FakeSamlAssertion(String nameId, Map<String, List<Object>> attributes)
        implements Saml2ResponseAssertionAccessor {

    @Override
    public String getNameId() {
        return nameId;
    }

    @Override
    public Map<String, List<Object>> getAttributes() {
        return attributes;
    }

    @Override
    public List<String> getSessionIndexes() {
        return List.of();
    }

    @Override
    public String getResponseValue() {
        return "<samlp:Response/>";
    }
}
