package br.com.pdv.repository;

import br.com.pdv.dto.SaleInfoDTO;
import br.com.pdv.entity.Sale;
import br.com.pdv.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findAllByUser(User user);
}
