package bda.Clinics.rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService; // Optional, if using custom UserDetails

    @GetMapping("/login")
    public String login() {
        return "login"; // Return the login view (Thymeleaf template)
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        try {
            // Attempt to authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // If authentication is successful, store it in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Redirect to the admin panel upon successful login
            return "redirect:/adminpanel";
        } catch (Exception e) {
            // If authentication fails, set an error message
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid username or password");
            return "redirect:/login"; // Redirect back to login page
        }
    }
}
