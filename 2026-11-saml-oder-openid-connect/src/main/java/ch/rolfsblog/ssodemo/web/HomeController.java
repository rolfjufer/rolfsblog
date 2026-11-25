package ch.rolfsblog.ssodemo.web;

import ch.rolfsblog.ssodemo.config.SsoProperties;
import ch.rolfsblog.ssodemo.identity.IdentityView;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** The HTML pages of the demo. */
@Controller
class HomeController {

    private final SsoProperties ssoProperties;

    HomeController(SsoProperties ssoProperties) {
        this.ssoProperties = ssoProperties;
    }

    /** Public. {@code authentication} is {@code null} as long as nobody is logged in. */
    @GetMapping("/")
    String index(Authentication authentication, Model model) {
        model.addAttribute("protocol", ssoProperties.protocol().displayName());
        model.addAttribute("authenticated", authentication != null);
        return "index";
    }

    /** Every claim or attribute of the proof. */
    @GetMapping("/me")
    String me(Authentication authentication, Model model) {
        model.addAttribute("protocol", ssoProperties.protocol().displayName());
        model.addAttribute("identity", IdentityView.from(authentication));
        return "me";
    }

    /** Only for the role DOZIERENDE; the rule is in AccessRules. */
    @GetMapping("/dozierende")
    String lecturers(Authentication authentication, Model model) {
        model.addAttribute("identity", IdentityView.from(authentication));
        return "dozierende";
    }
}
