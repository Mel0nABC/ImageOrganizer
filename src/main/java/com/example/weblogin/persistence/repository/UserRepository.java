package com.example.weblogin.persistence.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.weblogin.persistence.entity.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findUserEntityByUsername(String username);

    UserEntity findUserEntityById(Long id);

    UserEntity getUserByUsername(String username);

    void deleteById(Long id);

}