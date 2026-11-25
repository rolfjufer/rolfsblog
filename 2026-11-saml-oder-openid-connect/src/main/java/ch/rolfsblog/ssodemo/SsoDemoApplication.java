package ch.rolfsblog.ssodemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * One application, two protocols. Which one handles the login is decided by the
 * property {@code app.sso.protocol} alone, set by the profiles {@code oidc} and
 * {@code saml}.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class SsoDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsoDemoApplication.class, args);
    }
}
