package ch.rolfsblog.ssodemo.support;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/** The back channel of an OpenID provider on a random local port: keys, token endpoint, userinfo. */
public final class FakeOidcProvider {

    public static final String ISSUER = "http://idp.test/realms/demo";

    private static final RSAKey KEY = generateKey();
    private static final HttpServer SERVER = start();

    private static volatile String idToken;
    private static volatile String userInfo;

    private FakeOidcProvider() {
    }

    public static String url() {
        return "http://localhost:" + SERVER.getAddress().getPort();
    }

    /** The next code is exchanged for an ID token of this user. */
    public static void nextLogin(String user, List<String> groups, String nonce) throws JOSEException {
        Instant now = Instant.now();
        SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(KEY.getKeyID()).build(),
                new JWTClaimsSet.Builder()
                        .issuer(ISSUER)
                        .subject("subject-of-" + user)
                        .audience("saml-vs-oidc")
                        .issueTime(Date.from(now))
                        .expirationTime(Date.from(now.plusSeconds(300)))
                        .claim("nonce", nonce)
                        .claim("preferred_username", user)
                        .claim("groups", groups)
                        .build());
        jwt.sign(new RSASSASigner(KEY));
        idToken = jwt.serialize();
        userInfo = "{\"sub\":\"subject-of-" + user + "\",\"preferred_username\":\"" + user + "\"}";
    }

    private static HttpServer start() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            server.createContext("/certs", exchange ->
                    respond(exchange, new JWKSet(KEY.toPublicJWK()).toString()));
            server.createContext("/token", exchange -> respond(exchange,
                    "{\"access_token\":\"access-token\",\"token_type\":\"Bearer\",\"expires_in\":300,"
                            + "\"scope\":\"openid profile email\",\"id_token\":\"" + idToken + "\"}"));
            server.createContext("/userinfo", exchange -> respond(exchange, userInfo));
            server.start();
            return server;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static void respond(HttpExchange exchange, String json) throws IOException {
        exchange.getRequestBody().readAllBytes();
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }

    private static RSAKey generateKey() {
        try {
            return new RSAKeyGenerator(2048).keyID("test-key").generate();
        } catch (JOSEException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
