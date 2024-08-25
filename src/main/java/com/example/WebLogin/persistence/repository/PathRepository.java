package com.example.WebLogin.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.WebLogin.persistence.entity.PathEntity;

@Repository
public interface PathRepository extends CrudRepository<PathEntity, Long> {


    
}
