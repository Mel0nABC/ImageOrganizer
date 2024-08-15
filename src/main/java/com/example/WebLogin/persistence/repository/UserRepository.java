package com.example.WebLogin.persistence.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.WebLogin.persistence.entity.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long>  {

    Optional<UserEntity> findUserEntityByUsername(String username);

    UserEntity findUserEntityById(int id);

    UserEntity getUserByUsername(String username);

}
