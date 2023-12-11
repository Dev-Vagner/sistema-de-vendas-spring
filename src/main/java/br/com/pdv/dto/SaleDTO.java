package br.com.pdv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleDTO {
    @NotNull(message = "O campo usuário é obrigatório!")
    private Long userId;
    @NotNull(message = "Os items da compra são obrigatórios!")
    private List<ProductSaleDTO> items;
}
