package com.example.weblogin.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.cache.CacheType;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "path", uniqueConstraints = @UniqueConstraint(columnNames = "path_dir"))
public class PathEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "path_dir", nullable = false, updatable = false)
    private String path_dir;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath_dir() {
        return path_dir;
    }

    public void setPath_dir(String path_dir) {
        this.path_dir = path_dir;
    }
}
