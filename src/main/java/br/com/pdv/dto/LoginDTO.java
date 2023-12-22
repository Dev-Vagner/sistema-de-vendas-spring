package br.com.pdv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotBlank(message = "O Login é obrigatório!")
    private String username;
    @NotBlank(message = "A senha é obrigatória!")
    private String password;
}
