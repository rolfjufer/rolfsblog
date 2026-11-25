package ch.rolfsblog.ssodemo.web;

import ch.rolfsblog.ssodemo.identity.IdentityView;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** The identity page as JSON. */
@RestController
class IdentityApiController {

    @GetMapping("/api/me")
    IdentityView me(Authentication authentication) {
        return IdentityView.from(authentication);
    }
}
