package ch.rolfsblog.ssodemo.support;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/** Fresh keys with self-signed certificates for every test run. */
public final class KeyPairs {

    public record Signer(PrivateKey privateKey, X509Certificate certificate) {
    }

    private KeyPairs() {
    }

    public static Signer generate(String name) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keys = generator.generateKeyPair();
            X500Principal subject = new X500Principal("CN=" + name);
            Instant now = Instant.now();
            X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(
                    new JcaX509v3CertificateBuilder(subject, BigInteger.ONE,
                            Date.from(now.minus(Duration.ofDays(1))), Date.from(now.plus(Duration.ofDays(1))),
                            subject, keys.getPublic())
                            .build(new JcaContentSignerBuilder("SHA256withRSA").build(keys.getPrivate())));
            return new Signer(keys.getPrivate(), certificate);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
