package br.com.pdv.security;

import br.com.pdv.dto.ResponseDTO;
import br.com.pdv.entity.User;
import br.com.pdv.service.TokenService;
import br.com.pdv.service.UserService;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;

    private Gson gson = new Gson();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            var authHeader = request.getHeader("Authorization");

            if(authHeader != null && authHeader.startsWith("Bearer")){
                var token = authHeader.replace("Bearer ", "");

                var username = tokenService.validateToken(token);
                User user = userService.getByUsername(username);

                UserDetails userAuth = new UserAuth(user);

                var authentication = new UsernamePasswordAuthenticationToken(userAuth, null, userAuth.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }

            filterChain.doFilter(request, response);
        } catch (RuntimeException error) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(gson.toJson(new ResponseDTO("Token inválido!")));
        }
    }
}
