package com.tinystop.sjp.Auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByUsername(String username); // username 통해서 유저 찾기
    boolean existsByUsername(String username); // 같은 username을 가진 account가 있는지 확인
    boolean existsByEmail(String email); // 같은 email을 가진 account가 있는지 확인
    Optional<AccountEntity> findByEmail(String email); // email 통해서 유저 찾기
}
