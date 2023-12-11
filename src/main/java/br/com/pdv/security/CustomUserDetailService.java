package br.com.pdv.security;

import br.com.pdv.dto.LoginDTO;
import br.com.pdv.entity.User;
import br.com.pdv.exceptions.PasswordNotFoundException;
import br.com.pdv.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByUsername(username);
        return new UserPrincipal(user);
    }

    public void verifyUserCredentials(LoginDTO login) {
        User user = userService.getByUsername(login.getUsername());

        boolean correctPassword = SecurityConfig.passwordEncoder()
                .matches(login.getPassword(), user.getPassword());
        if(!correctPassword) {
            throw new PasswordNotFoundException("Senha inválida!");
        }
    }
}
