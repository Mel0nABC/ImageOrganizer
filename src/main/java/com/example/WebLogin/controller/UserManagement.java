package com.example.WebLogin.controller;

import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.WebLogin.persistence.entity.RoleEntity;
import com.example.WebLogin.persistence.entity.RoleEnum;
import com.example.WebLogin.persistence.entity.UserEntity;
import com.example.WebLogin.persistence.repository.UserRepository;
import com.example.WebLogin.service.UserDetailServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
public class UserManagement {

    private ObjectMapper mapper = new ObjectMapper();
    private UserDetailServiceImpl userDetailsService;
    private UserRepository userRepository;

    public UserManagement(UserDetailServiceImpl userDetailsService, UserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    /**
     * Cuando accedemos a la sección gestión de usuarios, proporcionamos todos los
     * usuarios de la aplicación
     * 
     * @return
     */
    @RequestMapping("/getAllUsersManagement")
    @ResponseBody
    public ObjectNode getAllUsersManagement() {
        Iterable<UserEntity> listUsers = userDetailsService.getAllUsers();
        ObjectNode json = mapper.createObjectNode();
        json.putPOJO("userList", listUsers);
        json.putPOJO("username", DashBoardController.username);
        return json;
    }

    /**
     * Cuando hacemos una edición de cualquier campo de cualquier usuario, se guarda
     * directamente
     * 
     * @param json
     * @return
     */
    @PostMapping("/editUser")
    @ResponseBody
    public void editUser(@RequestBody String jsonData, Model model) {
        UserEntity userChanges = null;
        UserEntity actualUser = null;
        try {

            JsonNode jsonNode = mapper.readTree(jsonData);
            String roleType = jsonNode.get("roleEnum").asText();

            userChanges = mapper.readValue(jsonData, UserEntity.class);
            actualUser = userDetailsService.findUserEntityById(userChanges.getId());

            Set<RoleEntity> actualRoleEntity = actualUser.getRoles();

            RoleEnum[] roleList = RoleEnum.values();
            RoleEnum newRoleEnum = null;
            for (RoleEnum role : roleList) {

                if (role.name().equals(roleType)) {
                    newRoleEnum = role;
                }
            }

            for (RoleEntity rol : actualRoleEntity) {
                rol.setRoleEnum(newRoleEnum);
            }

            userChanges.setRoles(actualRoleEntity);
            userChanges.setPassword(actualUser.getPassword());
            userChanges.setPatchList(actualUser.getPatchList());
            userDetailsService.setUSerEntity(userChanges);

        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Método para introducir nuevos usuarios
     * 
     * @param json
     * @return retornamos true si se crea correctamente, false si ocurre algo.
     */
    @RequestMapping("/newUser")
    @ResponseBody
    public boolean newUser(@RequestBody String json) {
        UserEntity newUser = null;
        try {
            JsonNode jsonNode = mapper.readTree(json);

            RoleEnum[] roleList = RoleEnum.values();
            RoleEnum newRoleEnum = null;
            for (RoleEnum role : roleList) {

                if (role.name().equals(jsonNode.get("roleEnum").asText())) {
                    newRoleEnum = role;
                }
            }

            newUser = new UserEntity();
            newUser.setUsername(jsonNode.get("username").asText());
            newUser.setPassword(new BCryptPasswordEncoder().encode(jsonNode.get("username").asText()));
            newUser.setEnabled(Boolean.parseBoolean(jsonNode.get("enabled").asText()));
            newUser.setAccountNoExpired(Boolean.parseBoolean(jsonNode.get("accountNoExpired").asText()));
            newUser.setAccountNoLocked(Boolean.parseBoolean(jsonNode.get("accountNoLocked").asText()));
            newUser.setCredentialNoExpired(Boolean.parseBoolean(jsonNode.get("credentialNoExpired").asText()));
            RoleEntity newRoleEntity = new RoleEntity();
            newRoleEntity.setRoleEnum(newRoleEnum);
            newUser.setRoles(Set.of(newRoleEntity));

            this.userRepository.save(newUser);

        } catch (JsonMappingException e) {
            e.printStackTrace();
            return false;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }

        if (userDetailsService.getUserByUsername(newUser.getUsername()) == null) {
            return false;
        }

        return true;
    }

    @RequestMapping("/delUser")
    @ResponseBody
    public boolean delUser(@RequestParam("id") Long id) {
        return userDetailsService.deleteById(id);
    }

}
