package com.example.weblogin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.weblogin.persistence.entity.PathEntity;
import com.example.weblogin.persistence.entity.UserEntity;
import com.example.weblogin.persistence.repository.PathRepository;
import com.example.weblogin.persistence.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PathRepository pathRepository;
    private static String ROOT = "root";

    /**
     * Método exclusivamente cuando se inicia la aplicación por primera vez, se
     * obliga a cambiar el usuario root, que es el admin.
     * Se cambia nombre de usuario y contraseña.
     * 
     * @param newUsername
     * @return
     */
    public Boolean setAdminNewSession(String newUsername, String newPassword) {
        UserEntity oldUser = userRepository.findUserEntityByUsername(ROOT).get();
        oldUser.setUsername(newUsername);
        oldUser.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        UserEntity newUser = userRepository.save(oldUser);
        if (newUser.getUsername().equals(ROOT)) {
            return false;
        }
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userEntity.getRoles()
                .forEach(role -> authorityList
                        .add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        userEntity.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        return new User(userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialNoExpired(),
                userEntity.isAccountNoLocked(),
                authorityList);
    }

    public Iterable<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity findUserEntityById(Long id) {
        return userRepository.findUserEntityById(id);
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    public Set<PathEntity> getPathList(String username) {
        UserEntity userEntity = userRepository.findUserEntityByUsername(username).get();
        Set<PathEntity> pathList = new HashSet<>();
        pathList = userEntity.getPatchList();
        return pathList;
    }

    public Set<PathEntity> getAllPathList() {
        Set<PathEntity> listPathComplete = new HashSet<>();
        Iterable<PathEntity> listadepatches = pathRepository.findAll();
        listadepatches.forEach(listPathComplete::add);
        return listPathComplete;
    }

    public Boolean setUSerEntity(UserEntity user) {

        userRepository.save(user);

        return true;
    }

    public boolean deleteById(Long id) {

        Long beforeDel = userRepository.count();
        userRepository.deleteById(id);
        Long afterDel = userRepository.count();
        if (beforeDel == afterDel) {
            return false;
        }
        return true;
    }

    boolean pathUsado = false;

    public void cleanPathDataBase() {
        Iterable<PathEntity> allPathEntityDataBase = pathRepository.findAll();
        Iterable<UserEntity> userListEntity = getAllUsers();

        allPathEntityDataBase.forEach(path -> {
            userListEntity.forEach(user -> {
                Set<PathEntity> userPathList = user.getPatchList();
                userPathList.forEach(userPath -> {
                    if (path.getPath_dir().equals(userPath.getPath_dir())) {
                        pathUsado = true;
                    }
                });
            });

            if (pathUsado) {
                pathUsado = false;
            } else {
                pathRepository.delete(path);
            }

        });
    }


    

}
