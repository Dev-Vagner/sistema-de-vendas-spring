package br.com.pdv.controller;

import br.com.pdv.dto.LoginDTO;
import br.com.pdv.dto.ResponseDTO;
import br.com.pdv.dto.TokenDTO;
import br.com.pdv.entity.User;
import br.com.pdv.security.UserAuth;
import br.com.pdv.service.TokenService;
import br.com.pdv.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;

    @Value("${security.jwt.expiration}")
    private String expiration;

    @PostMapping()
    public ResponseEntity login(@Valid @RequestBody LoginDTO loginData) {
        userService.verifyUserCredentials(loginData);
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginData.getUsername(), loginData.getPassword());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        String token = tokenService.generateToken((UserAuth) auth.getPrincipal());

        return new ResponseEntity<>(new TokenDTO(token, expiration), HttpStatus.OK);
    }
}
