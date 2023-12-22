package br.com.pdv.service;

import br.com.pdv.dto.LoginDTO;
import br.com.pdv.dto.UserCreateRequestDTO;
import br.com.pdv.dto.UserEditRequestDTO;
import br.com.pdv.dto.UserResponseDTO;
import br.com.pdv.entity.User;
import br.com.pdv.entity.UserRole;
import br.com.pdv.exceptions.IdInvalidException;
import br.com.pdv.exceptions.InvalidOperationException;
import br.com.pdv.exceptions.NoItemException;
import br.com.pdv.exceptions.PasswordNotFoundException;
import br.com.pdv.repository.UserRepository;
import br.com.pdv.security.SecurityConfig;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.jdbc.support.JdbcUtils.isNumeric;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private ModelMapper mapper = new ModelMapper();

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(this::createUserResponseDTO).collect(Collectors.toList());
    }

    public UserResponseDTO findById(Long id) {
        if(verifyUserRoleLoggedUser()) {
            verifyIdUserLogged(id);
        };

        Optional<User> userValidated = validateById(id);
        if(userValidated.isPresent()) {
            User user = userValidated.get();
            return createUserResponseDTO(user);
        } else {
            throw new NoItemException("Usuário não encontrado, ID inválido!");
        }
    }

    public UserResponseDTO save(UserCreateRequestDTO userCreateRequestDTO) {
        verifyUsernameNotExistent(userCreateRequestDTO.getUsername());

        userCreateRequestDTO.setPassword(SecurityConfig.passwordEncoder().encode(userCreateRequestDTO.getPassword()));
        userCreateRequestDTO.setIsEnabled(true);
        User userToSave = mapper.map(userCreateRequestDTO, User.class);

        User userRegistered = userRepository.save(userToSave);

        return createUserResponseDTO(userRegistered);
    }


    public UserResponseDTO edit(UserEditRequestDTO userEditRequestDTO) {
        verifyIdUserLogged(userEditRequestDTO.getId());

        String usernameLogged = getUsernameLogged();

        if(!Objects.equals(userEditRequestDTO.getUsername(), usernameLogged)) {
            verifyUsernameNotExistent(userEditRequestDTO.getUsername());
        }

        userEditRequestDTO.setPassword(SecurityConfig.passwordEncoder().encode(userEditRequestDTO.getPassword()));
        User userToSave = mapper.map(userEditRequestDTO, User.class);
        User userUpdated = userRepository.save(userToSave);

        return createUserResponseDTO(userUpdated);
    }

    public void deleteById(Long id) {
        verifyIdUserLogged(id);
        userRepository.deleteById(id);
    }

    public User getByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("Login inválido!");
        }
        return user;
    }

    public void verifyUserCredentials(LoginDTO loginDTO) {
        User user = getByUsername(loginDTO.getUsername());

        boolean correctPassword = SecurityConfig.passwordEncoder()
                .matches(loginDTO.getPassword(), user.getPassword());
        if(!correctPassword) {
            throw new PasswordNotFoundException("Senha inválida!");
        }
    }

    private void verifyUsernameNotExistent(String username) {
        User user = userRepository.findByUsername(username);
        if(user != null) {
            throw new InvalidOperationException("Nome de usuário já cadastrado!");
        }
    }

    public void verifyIdUserLogged(Long id) {
        User user = getByUsername(getUsernameLogged());
        if(user.getId() != id) {
            throw new IdInvalidException("O ID enviado é diferente do ID do usuário logado!");
        }
    }

    public Boolean verifyUserRoleLoggedUser() {
        User userLogged = getByUsername(getUsernameLogged());
        if(userLogged.getRole().equals(UserRole.USER)) return true;
        return false;
    }

    public String getUsernameLogged() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public User getUserLogged() {
        return getByUsername(getUsernameLogged());
    }
    public Optional<User> validateById(Long id) {
        return userRepository.findById(id);
    }

    private UserResponseDTO createUserResponseDTO(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setId(user.getId());
        userResponseDTO.setName(user.getName());
        userResponseDTO.setUsername(user.getUsername());
        userResponseDTO.setRole(user.getRole().toString().toLowerCase());
        userResponseDTO.setIsEnabled(user.getIsEnabled());

        return userResponseDTO;
    }
}
