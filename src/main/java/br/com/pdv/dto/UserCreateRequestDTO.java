package br.com.pdv.dto;

import br.com.pdv.entity.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequestDTO {
    private Long id;
    @NotBlank(message = "O campo nome é obrigatório!")
    private String name;
    @NotBlank(message = "O nome do usuário é obrigatório!")
    private String username;
    @NotBlank(message = "A sennha é obrigatória!")
    private String password;
    @NotNull(message = "O cargo do usuário é obrigatório")
    private UserRole role;
    private Boolean isEnabled;
}
