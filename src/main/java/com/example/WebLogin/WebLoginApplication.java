package com.example.WebLogin;


import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;





@SpringBootApplication
public class WebLoginApplication {



    public static void main(String[] args) {
          String url = "jdbc:sqlite:securitydb.db";

        try (var conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                var meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        SpringApplication.run(WebLoginApplication.class, args);
    }

    @Bean
    boolean init(){
        System.out.println("TEST ------------------------------- ");
        return true;
    }




//    @Bean
//    CommandLineRunner init(UserRepository userRepository) {
//        return args -> {
//            /* Create PERMISSIONS */
//            PermissionEntity createPermission = PermissionEntity.builder()
//                    .name("CREATE")
//                    .build();

//            PermissionEntity readPermission = PermissionEntity.builder()
//                    .name("READ")
//                    .build();

//            PermissionEntity updatePermission = PermissionEntity.builder()
//                    .name("UPDATE")
//                    .build();

//            PermissionEntity deletePermission = PermissionEntity.builder()
//                    .name("DELETE")
//                    .build();

//            PermissionEntity refactorPermission = PermissionEntity.builder()
//                    .name("REFACTOR")
//                    .build();

//            /* Create ROLES */
//            RoleEntity roleAdmin = RoleEntity.builder()
//                    .roleEnum(RoleEnum.ADMIN)
//                    .permissionList(Set.of(createPermission, readPermission, updatePermission, deletePermission))
//                    .build();

//            RoleEntity roleUser = RoleEntity.builder()
//                    .roleEnum(RoleEnum.USER)
//                    .permissionList(Set.of(createPermission, readPermission))
//                    .build();

//            RoleEntity roleInvited = RoleEntity.builder()
//                    .roleEnum(RoleEnum.INVITED)
//                    .permissionList(Set.of(readPermission))
//                    .build();

//            RoleEntity roleDeveloper = RoleEntity.builder()
//                    .roleEnum(RoleEnum.DEVELOPER)
//                    .permissionList(Set.of(createPermission, readPermission, updatePermission, deletePermission, refactorPermission))
//                    .build();

//            /* CREATE USERS */
//            UserEntity userSantiago = UserEntity.builder()
//                    .username("mel0n")
//                    .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6")
//                    .isEnabled(true)
//                    .accountNoExpired(true)
//                    .accountNoLocked(true)
//                    .credentialNoExpired(true)
//                    .roles(Set.of(roleAdmin))
//                    .build();

//            UserEntity userDaniel = UserEntity.builder()
//                    .username("daniel")
//                    .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6")
//                    .isEnabled(true)
//                    .accountNoExpired(true)
//                    .accountNoLocked(true)
//                    .credentialNoExpired(true)
//                    .roles(Set.of(roleUser))
//                    .build();

//            UserEntity userAndrea = UserEntity.builder()
//                    .username("andrea")
//                    .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6")
//                    .isEnabled(true)
//                    .accountNoExpired(true)
//                    .accountNoLocked(true)
//                    .credentialNoExpired(true)
//                    .roles(Set.of(roleInvited))
//                    .build();

//            UserEntity userAnyi = UserEntity.builder()
//                    .username("anyi")
//                    .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6")
//                    .isEnabled(true)
//                    .accountNoExpired(true)
//                    .accountNoLocked(true)
//                    .credentialNoExpired(true)
//                    .roles(Set.of(roleDeveloper))
//                    .build();

//            userRepository.saveAll(List.of(userSantiago, userDaniel, userAndrea, userAnyi));
//        };
// }
}
