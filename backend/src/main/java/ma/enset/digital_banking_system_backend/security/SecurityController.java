package ma.enset.digital_banking_system_backend.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

@RestController
@RequestMapping("/auth")
public class SecurityController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    public SecurityController(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    @GetMapping("/profile")
    public Authentication authentication(Authentication auth) {
        return auth;
    }

    @PostMapping("/login")
    public Map<String, String> login(String username, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        Instant instant = Instant.now();
        String scope = auth.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.joining(" "));
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(username)
                .issuedAt(instant)
                .expiresAt(instant.plus(15, ChronoUnit.MINUTES))
                .claim("scope", scope)
                .build();

        JwtEncoderParameters params = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                jwtClaimsSet);
        String jwt = jwtEncoder.encode(params).getTokenValue();
        return Map.of("access_token", jwt);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Authentication authentication, @RequestBody Map<String, String> payload) {
        String oldPassword = payload.get("currentPassword");
        String newPassword = payload.get("newPassword");
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        String username = authentication.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!passwordEncoder.matches(oldPassword, userDetails.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ancien mot de passe incorrect");
        }
        if (userDetailsService instanceof org.springframework.security.provisioning.InMemoryUserDetailsManager manager) {
            manager.updatePassword(userDetails, passwordEncoder.encode(newPassword));
            return ResponseEntity.ok(new HashMap<String, String>() {
                {
                    put("message", "Mot de passe changé avec succès");
                }
            });
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Changement de mot de passe non supporté");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        String role = payload.getOrDefault("role", "USER");
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username et mot de passe requis");
        }
        if (userDetailsService instanceof org.springframework.security.provisioning.InMemoryUserDetailsManager manager) {
            if (manager.userExists(username)) {
                return ResponseEntity.badRequest().body("Utilisateur déjà existant");
            }
            manager.createUser(org.springframework.security.core.userdetails.User.withUsername(username)
                    .password(passwordEncoder.encode(password))
                    .roles(role)
                    .build());
            return ResponseEntity.ok(Map.of("message", "Inscription réussie"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Inscription non supportée");
        }
    }
}
