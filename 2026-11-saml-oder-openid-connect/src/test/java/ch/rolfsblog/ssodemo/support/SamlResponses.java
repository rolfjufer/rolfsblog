package ch.rolfsblog.ssodemo.support;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/** Builds the response Keycloak would send: one attribute "groups" per group, the whole response signed. */
public final class SamlResponses {

    private static final String ACS = "http://localhost/login/saml2/sso/keycloak";
    private static final String APPLICATION = "http://localhost/saml2/service-provider-metadata/keycloak";

    private SamlResponses() {
    }

    public static String signedBy(KeyPairs.Signer signer, String requestId, String user, List<String> groups) {
        Instant now = Instant.now();
        String attributes = groups.stream()
                .map(("<saml:Attribute Name=\"groups\"><saml:AttributeValue>%s</saml:AttributeValue>"
                        + "</saml:Attribute>")::formatted)
                .collect(Collectors.joining());
        String response = """
                <samlp:Response xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol" \
                xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion" ID="_response" Version="2.0" \
                IssueInstant="%1$s" Destination="%2$s" InResponseTo="%3$s"><saml:Issuer>%4$s</saml:Issuer>\
                <samlp:Status><samlp:StatusCode Value="urn:oasis:names:tc:SAML:2.0:status:Success"/>\
                </samlp:Status><saml:Assertion ID="_assertion" Version="2.0" IssueInstant="%1$s">\
                <saml:Issuer>%4$s</saml:Issuer><saml:Subject><saml:NameID>%5$s</saml:NameID>\
                <saml:SubjectConfirmation Method="urn:oasis:names:tc:SAML:2.0:cm:bearer">\
                <saml:SubjectConfirmationData InResponseTo="%3$s" Recipient="%2$s" NotOnOrAfter="%6$s"/>\
                </saml:SubjectConfirmation></saml:Subject>\
                <saml:Conditions NotBefore="%7$s" NotOnOrAfter="%6$s"><saml:AudienceRestriction>\
                <saml:Audience>%8$s</saml:Audience></saml:AudienceRestriction></saml:Conditions>\
                <saml:AuthnStatement AuthnInstant="%1$s"><saml:AuthnContext><saml:AuthnContextClassRef>\
                urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified</saml:AuthnContextClassRef>\
                </saml:AuthnContext></saml:AuthnStatement>\
                <saml:AttributeStatement>%9$s</saml:AttributeStatement></saml:Assertion></samlp:Response>"""
                .formatted(now, ACS, requestId, TestSamlRelyingPartyConfig.IDP_ENTITY_ID, user,
                        now.plus(Duration.ofMinutes(5)), now.minus(Duration.ofMinutes(1)), APPLICATION, attributes);
        return XmlSignatures.sign(response, "_response", signer);
    }
}
