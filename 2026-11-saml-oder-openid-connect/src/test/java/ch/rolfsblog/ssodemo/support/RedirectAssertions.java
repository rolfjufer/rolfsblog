package ch.rolfsblog.ssodemo.support;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

/** Helpers for redirect URLs. */
public final class RedirectAssertions {

    private RedirectAssertions() {
    }

    /** The decoded value of a query parameter, or {@code null}. */
    public static String queryParam(String url, String name) {
        String raw = UriComponentsBuilder.fromUriString(url).build().getQueryParams().getFirst(name);
        return raw == null ? null : UriUtils.decode(raw, StandardCharsets.UTF_8);
    }

    /** Path and query of an absolute URL, for MockMvc. */
    public static String pathAndQuery(String url) {
        URI uri = URI.create(url);
        return uri.getRawQuery() == null ? uri.getRawPath() : uri.getRawPath() + "?" + uri.getRawQuery();
    }
}
