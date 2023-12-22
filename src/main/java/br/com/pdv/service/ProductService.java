package br.com.pdv.service;

import br.com.pdv.dto.ProductDTO;
import br.com.pdv.entity.Product;
import br.com.pdv.exceptions.NoItemException;
import br.com.pdv.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    private ModelMapper mapper = new ModelMapper();

    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream().map(this::createProductDTO).collect(Collectors.toList());
    }

    public ProductDTO findById(Long id) {
        Optional<Product> productValidated = validateById(id);
        if(productValidated.isPresent()) {
            Product product = productValidated.get();
            return createProductDTO(product);
        } else {
            throw new NoItemException("Produto não encontrado, ID inválido!");
        }
    }

    public ProductDTO save(ProductDTO productDTO) {
        Product productToSave = mapper.map(productDTO, Product.class);
        Product productRegistered = productRepository.save(productToSave);

        return createProductDTO(productRegistered);
    }

    public ProductDTO edit(ProductDTO productDTO) {
        Optional<Product> productValidated = validateById(productDTO.getId());
        if (productValidated.isEmpty()) {
            throw new NoItemException("O ID do produto é inválido!");
        }

        Product productToSave = mapper.map(productDTO, Product.class);
        Product productRegistered = productRepository.save(productToSave);

        return createProductDTO(productRegistered);
    }

    public void deleteById(Long id) {
        Optional<Product> productValidated = validateById(id);
        if (productValidated.isPresent()) {
            productRepository.deleteById(id);
        } else {
            throw new NoItemException("O ID do produto é inválido!");
        }
    }

    private Optional<Product> validateById(Long id) {
        return productRepository.findById(id);
    }

    private ProductDTO createProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();

        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setQuantity(product.getQuantity());

        return productDTO;
    }
}
