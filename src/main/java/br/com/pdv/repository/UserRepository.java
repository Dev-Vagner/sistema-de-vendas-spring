package br.com.pdv.repository;

import br.com.pdv.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u from User u LEFT JOIN FETCH u.sales WHERE u.username = :username")
    User findUserByUsername(@Param("username") String username);
}
