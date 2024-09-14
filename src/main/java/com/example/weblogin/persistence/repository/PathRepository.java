package com.example.weblogin.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.weblogin.persistence.entity.PathEntity;

@Repository
public interface PathRepository extends CrudRepository<PathEntity, Long> {


    
}
