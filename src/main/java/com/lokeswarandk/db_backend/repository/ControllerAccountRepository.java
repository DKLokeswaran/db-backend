package com.lokeswarandk.db_backend.repository;

import com.lokeswarandk.db_backend.model.ControllerAccount;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ControllerAccountRepository extends CrudRepository<ControllerAccount, Long> {

    Optional<ControllerAccount> findByUsername(String username);
}
