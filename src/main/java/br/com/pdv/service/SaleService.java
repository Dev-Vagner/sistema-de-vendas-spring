package br.com.pdv.service;

import br.com.pdv.dto.ProductSaleDTO;
import br.com.pdv.dto.ProductInfoDTO;
import br.com.pdv.dto.SaleDTO;
import br.com.pdv.dto.SaleInfoDTO;
import br.com.pdv.entity.ItemSale;
import br.com.pdv.entity.Product;
import br.com.pdv.entity.Sale;
import br.com.pdv.entity.User;
import br.com.pdv.exceptions.InvalidOperationException;
import br.com.pdv.exceptions.NoItemException;
import br.com.pdv.repository.ItemSaleRepository;
import br.com.pdv.repository.ProductRepository;
import br.com.pdv.repository.SaleRepository;
import br.com.pdv.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SaleService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final ItemSaleRepository itemSaleRepository;

    public List<SaleInfoDTO> findAll() {
        return saleRepository.findAll().stream().map(sale -> getSaleInfo(sale)).collect(Collectors.toList());
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
        User user = userRepository.findById(sale.getUserId())
                .orElseThrow(() -> new NoItemException("Usuário não encontrado!"));

        List<ItemSale> items = getItemSale(sale.getItems());

        Sale newSale = new Sale();
        newSale.setUser(user);
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
        return getSaleInfo(sale);
    }
}
