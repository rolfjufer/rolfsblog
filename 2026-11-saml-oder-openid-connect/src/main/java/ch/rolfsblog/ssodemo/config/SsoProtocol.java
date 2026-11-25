package ch.rolfsblog.ssodemo.config;

/** The two single sign-on protocols this demo compares. */
public enum SsoProtocol {

    OIDC("OpenID Connect"),
    SAML("SAML 2.0");

    private final String displayName;

    SsoProtocol(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
