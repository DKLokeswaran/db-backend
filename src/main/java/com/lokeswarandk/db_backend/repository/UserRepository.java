package com.lokeswarandk.db_backend.repository;

import com.lokeswarandk.db_backend.model.User;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
