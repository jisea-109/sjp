package com.tinystop.sjp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tinystop.sjp.entity.AccountEntity;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean findByPassword(String password); // for test, need to be removed because of security issue
    Optional<AccountEntity> findByEmail(String email);
}
