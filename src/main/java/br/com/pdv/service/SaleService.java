package br.com.pdv.service;

import br.com.pdv.dto.ProductSaleDTO;
import br.com.pdv.dto.ProductInfoDTO;
import br.com.pdv.dto.SaleDTO;
import br.com.pdv.dto.SaleInfoDTO;
import br.com.pdv.entity.*;
import br.com.pdv.exceptions.IdInvalidException;
import br.com.pdv.exceptions.InvalidOperationException;
import br.com.pdv.exceptions.NoItemException;
import br.com.pdv.repository.ItemSaleRepository;
import br.com.pdv.repository.ProductRepository;
import br.com.pdv.repository.SaleRepository;
import br.com.pdv.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class SaleService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private ItemSaleRepository itemSaleRepository;

    public List<SaleInfoDTO> findAll() {
        return saleRepository.findAll().stream().map(sale -> getSaleInfo(sale)).collect(Collectors.toList());
    }

    public List<SaleInfoDTO> findAllByUser(Long id) {
        Optional<User> userValidate = userService.validateById(id);
        if(userValidate.isEmpty()) {
            throw new InvalidOperationException("Usuário não encontrado!");
        }

        User userLogged = userService.getUserLogged();
        if(userLogged.getRole().equals(UserRole.USER) && userLogged.getId() != id) {
            throw new IdInvalidException("Você não tem permissão para acessar as vendas deste usuário!");
        }

        List<Sale> sales = saleRepository.findAllByUser(userValidate.get());

        List<SaleInfoDTO> saleInfoDTOList = new ArrayList<>();
        for(Sale sale : sales) {
            saleInfoDTOList.add(getSaleInfo(sale));
        }

        return saleInfoDTOList;
    }

    private SaleInfoDTO getSaleInfo(Sale sale) {
        var products = getProductInfo(sale.getItems());
        BigDecimal total = getTotal(products);

        return SaleInfoDTO.builder()
                .user(sale.getUser().getName())
                .date(sale.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .products(products)
                .total(total)
                .build();
    }

    private BigDecimal getTotal(List<ProductInfoDTO> productsInfoDTO) {
        BigDecimal total = new BigDecimal(0);

        for(ProductInfoDTO productInfoDTO : productsInfoDTO) {
            BigDecimal productQuantity = new BigDecimal(productInfoDTO.getQuantity());
            total = total.add(productInfoDTO.getPrice().multiply(productQuantity));
        }
        return total;
    }

    private List<ProductInfoDTO> getProductInfo(List<ItemSale> items) {

        if(items.isEmpty()) {
            return Collections.emptyList();
        }

        return items.stream().map(
                item -> ProductInfoDTO
                        .builder()
                        .id(item.getId())
                        .description(item.getProduct().getDescription())
                        .price(item.getProduct().getPrice())
                        .quantity(item.getQuantity())
                        .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    public Long save(SaleDTO sale) {
        User userLogged = userService.getUserLogged();
        if(userLogged.getId() != sale.getUserId()) {
            throw new IdInvalidException("Você está usando um ID diferente do ID do usuário logado!");
        }

        List<ItemSale> items = getItemSale(sale.getItems());

        Sale newSale = new Sale();
        newSale.setUser(userLogged);
        newSale.setDate(LocalDate.now());
        newSale.setItems(items);

        newSale = saleRepository.save(newSale);

        saveItemSale(items, newSale);

        return newSale.getId();
    }

    private void saveItemSale(List<ItemSale> items, Sale newSale) {
        for (ItemSale item : items) {
            item.setSale(newSale);
            itemSaleRepository.save(item);
        }
    }

    private List<ItemSale> getItemSale(List<ProductSaleDTO> items) {

        if(items.isEmpty()) {
            throw new InvalidOperationException("Não é possível realizar a venda sem produtos");
        }

        return items.stream().map(item -> {
            if(item.getProductId() == null) {
                throw new NoItemException("O item da venda é obrigatório!");
            } else if(item.getQuantity() == null) {
                throw new InvalidOperationException("O campo quantidade é obrigatório!");
            }

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new NoItemException("O item da venda não está cadastrado!"));

            if(product.getQuantity() == 0) {
                throw new NoItemException("Produto sem estoque!");
            } else if (product.getQuantity() < item.getQuantity()){
                throw new InvalidOperationException(
                        String.format("A quantidade de itens da venda (%s) " +
                                "é maior do que a quantidade disponível em estoque (%s)",
                                item.getQuantity(), product.getQuantity())
                );
            }

            ItemSale itemSale = new ItemSale();
            itemSale.setProduct(product);
            itemSale.setQuantity(item.getQuantity());

            updateProductQuantity(product, itemSale.getQuantity());

            return itemSale;
        }).collect(Collectors.toList());
    }

    public void updateProductQuantity(Product product, Integer quantityItemSale) {
        product.setQuantity(product.getQuantity() - quantityItemSale);
        productRepository.save(product);
    }

    public SaleInfoDTO getById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new NoItemException("Venda não encontrada!"));

        User userLogged = userService.getUserLogged();
        User userSale = sale.getUser();

        if(userLogged.getRole() == UserRole.USER && userSale.getId() != userLogged.getId()) {
            throw new IdInvalidException("Você não tem permissão para acessar os dados dessa venda!");
        }

        return getSaleInfo(sale);
    }
}
