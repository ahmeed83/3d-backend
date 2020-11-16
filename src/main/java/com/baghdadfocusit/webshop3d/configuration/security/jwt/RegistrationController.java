package com.baghdadfocusit.webshop3d.configuration.security.jwt;

import com.baghdadfocusit.webshop3d.configuration.security.auth.ApplicationUser;
import com.baghdadfocusit.webshop3d.configuration.security.auth.ApplicationUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class RegistrationController {

    private final ApplicationUserService applicationUserService;

    public RegistrationController(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

    /**
     * Endpoint to register a new user.
     *
     * @param newUser new user
     * @return ResponseEntity
     * @throws Exception Exception
     */
    @PostMapping("sign-up")
    public ResponseEntity registerNewUser(@RequestBody ApplicationUser newUser) throws Exception {
        applicationUserService.saveApplicationUser(newUser);
        return ResponseEntity.ok().build();
    }
}