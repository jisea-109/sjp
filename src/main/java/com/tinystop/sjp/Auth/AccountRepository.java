package com.tinystop.sjp.Auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<AccountEntity> findByEmail(String email);
}
