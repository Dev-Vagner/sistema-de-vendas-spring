package br.com.pdv.service;

import br.com.pdv.dto.UserRequestDTO;
import br.com.pdv.dto.UserResponseDTO;
import br.com.pdv.entity.User;
import br.com.pdv.exceptions.NoItemException;
import br.com.pdv.repository.UserRepository;
import br.com.pdv.security.SecurityConfig;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private ModelMapper mapper = new ModelMapper();

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(this::createUserResponseDTO).collect(Collectors.toList());
    }

    public UserResponseDTO findById(Long id) {
        Optional<User> userValidated = validateById(id);
        if(userValidated.isPresent()) {
            User user = userValidated.get();
            return createUserResponseDTO(user);
        } else {
            throw new NoItemException("Usuário não encontrado, ID inválido!");
        }
    }

    public UserResponseDTO save(UserRequestDTO userRequestDTO) {
        userRequestDTO.setPassword(SecurityConfig.passwordEncoder().encode(userRequestDTO.getPassword()));
        userRequestDTO.setIsEnabled(true);
        User userToSave = mapper.map(userRequestDTO, User.class);
        User userRegistered = userRepository.save(userToSave);

        return createUserResponseDTO(userRegistered);
    }


    public UserResponseDTO edit(UserRequestDTO userRequestDTO) {
        Optional<User> userValidated = validateById(userRequestDTO.getId());
        if (userValidated.isEmpty()) {
            throw new NoItemException("O ID do usuário é inválido!");
        }

        userRequestDTO.setPassword(SecurityConfig.passwordEncoder().encode(userRequestDTO.getPassword()));
        User userToSave = mapper.map(userRequestDTO, User.class);
        User userUpdated = userRepository.save(userToSave);

        return createUserResponseDTO(userUpdated);
    }

    public void deleteById(Long id) {
        Optional<User> userValidated = validateById(id);
        if (userValidated.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new NoItemException("O ID do usuário é inválido!");
        }
    }

    public User getByUsername(String username) {
        User user = userRepository.findUserByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("Login inválido!");
        }
        return user;
    }

    private Optional<User> validateById(Long id) {
        return userRepository.findById(id);
    }

    private UserResponseDTO createUserResponseDTO(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setId(user.getId());
        userResponseDTO.setName(user.getName());
        userResponseDTO.setUsername(user.getUsername());
        userResponseDTO.setIsEnabled(user.getIsEnabled());

        return userResponseDTO;
    }
}
