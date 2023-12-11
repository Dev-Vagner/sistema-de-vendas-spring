package br.com.pdv.controller;

import br.com.pdv.dto.LoginDTO;
import br.com.pdv.dto.ResponseDTO;
import br.com.pdv.dto.TokenDTO;
import br.com.pdv.security.CustomUserDetailService;
import br.com.pdv.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    private final CustomUserDetailService customUserDetailService;
    private final JwtService jwtService;

    @Value("${security.jwt.expiration}")
    private String expiration;

    @PostMapping
    public ResponseEntity post(@Valid @RequestBody LoginDTO loginData) {
        try {
            customUserDetailService.verifyUserCredentials(loginData);
            String token = jwtService.genareteToken(loginData.getUsername());
            return new ResponseEntity<>(new TokenDTO(token, expiration), HttpStatus.OK);
        } catch (Exception error) {
            return new ResponseEntity<>(new ResponseDTO(error.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }
}
