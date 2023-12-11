package br.com.pdv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    private Long id;
    @NotBlank(message = "O campo nome é obrigatório!")
    private String name;
    @NotBlank(message = "O nome do usuário é obrigatório!")
    private String username;
    @NotBlank(message = "A sennha é obrigatória!")
    private String password;
    private Boolean isEnabled;
}
