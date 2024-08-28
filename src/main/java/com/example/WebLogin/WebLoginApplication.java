package com.example.WebLogin;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.example.WebLogin.persistence.entity.PermissionEntity;
import com.example.WebLogin.persistence.entity.RoleEntity;
import com.example.WebLogin.persistence.entity.RoleEnum;
import com.example.WebLogin.persistence.entity.UserEntity;
import com.example.WebLogin.persistence.repository.UserRepository;
import com.example.WebLogin.service.WatchingDirectory;


@SpringBootApplication
public class WebLoginApplication {

    @Autowired
    Environment env;

    public static void main(String[] args) {
        SpringApplication.run(WebLoginApplication.class, args);
    }

    // @Autowired
    // private WatchingDirectory watchingDirectory;
    private static final String SEPARADOR = File.separator;
    private static final String HOME_DIR = System.getProperty("user.dir");
    private static final String PATHLOGS = HOME_DIR + SEPARADOR + "logs";
    private final File PATH = new File(PATHLOGS);
    private static final File LOG_FILE_PATH = new File(PATHLOGS + SEPARADOR + "logs.log");



    /**
     * Para especificar cuál será el aporte de data, en este caso, la bbdd de
     * sqlite.
     * 
     * @return
     */
    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("driverClassName"));
        dataSource.setUrl(env.getProperty("url"));
        dataSource.setUsername(env.getProperty("username"));
        dataSource.setPassword(env.getProperty("password"));
        return dataSource;
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository) {

        // System.out.println("CREAMOS USUARIOS Y ROLES");
        return args -> {
            Iterable<UserEntity> users = userRepository.findAll();
            Iterator<UserEntity> userList = users.iterator();

            boolean root = false;
            int contador = 0;
            while (userList.hasNext()) {
                UserEntity tmp = userList.next();
                contador++;
            }

            if (contador == 0) {
                userRepository.saveAll(List.of(createRoot()));
            }

            if (!LOG_FILE_PATH.exists()) {
                PATH.mkdirs();
                LOG_FILE_PATH.createNewFile();
            }

            // WatchingDirectory watchingDirectory = new WatchingDirectory();
            // watchingDirectory.setInitialPath("/media/Almacenamiento/Download/webFotos");

            // userDetailsService.getAllPathList().forEach(path -> {
            // System.out.println(path.getPath_dir());
            // });

            // // Create PERMISSIONS
            // PermissionEntity createPermission = PermissionEntity.builder()
            // .name("CREATE")
            // .build();

            // PermissionEntity readPermission = PermissionEntity.builder()
            // .name("READ")
            // .build();

            // PermissionEntity updatePermission = PermissionEntity.builder()
            // .name("UPDATE")
            // .build();

            // PermissionEntity deletePermission = PermissionEntity.builder()
            // .name("DELETE")
            // .build();

            // PermissionEntity refactorPermission = PermissionEntity.builder()
            // .name("REFACTOR")
            // .build();

            // /* Create ROLES */
            // RoleEntity roleAdmin = RoleEntity.builder()
            // .roleEnum(RoleEnum.ADMIN)
            // .permissionList(Set.of(createPermission, readPermission, updatePermission,
            // deletePermission))
            // .build();

            // RoleEntity roleUser = RoleEntity.builder()
            // .roleEnum(RoleEnum.USER)
            // .permissionList(Set.of(createPermission, readPermission))
            // .build();

            // RoleEntity roleInvited = RoleEntity.builder()
            // .roleEnum(RoleEnum.INVITED)
            // .permissionList(Set.of(readPermission))
            // .build();

            // RoleEntity roleDeveloper = RoleEntity.builder()
            // .roleEnum(RoleEnum.DEVELOPER)
            // .permissionList(Set.of(createPermission, readPermission, updatePermission,
            // deletePermission, refactorPermission))
            // .build();

            // /* CREAMOS UN PATH, DE PRUEBA */
            // // PathEntity rootPath = PathEntity.builder()
            // // .path_dir("/media/Almacenamiento")
            // // .build();

            // /* CREATE USERS */
            // UserEntity root = UserEntity.builder()
            // .username("root")
            // .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6")
            // .isEnabled(true)
            // .accountNoExpired(true)
            // .accountNoLocked(true)
            // .credentialNoExpired(true)
            // .roles(Set.of(roleAdmin))
            // // .patchList(List.of(rootPath))
            // .build();

            // UserEntity userDaniel = UserEntity.builder()
            // .username("daniel")
            // .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6")
            // .isEnabled(true)
            // .accountNoExpired(true)
            // .accountNoLocked(true)
            // .credentialNoExpired(true)
            // .roles(Set.of(roleUser))
            // .build();

            // UserEntity userAndrea = UserEntity.builder()
            // .username("andrea")
            // .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6")
            // .isEnabled(true)
            // .accountNoExpired(true)
            // .accountNoLocked(true)
            // .credentialNoExpired(true)
            // .roles(Set.of(roleInvited))
            // .build();

            // UserEntity userAnyi = UserEntity.builder()
            // .username("anyi")
            // .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6")
            // .isEnabled(true)
            // .accountNoExpired(true)
            // .accountNoLocked(true)
            // .credentialNoExpired(true)
            // .roles(Set.of(roleDeveloper))
            // .build();

            // userRepository.saveAll(List.of(root, userAndrea, userAnyi, userDaniel));
        };
    }

    public UserEntity createRoot() {
        // // Create PERMISSIONS
        PermissionEntity createPermission = PermissionEntity.builder()
                .name("CREATE")
                .build();

        PermissionEntity readPermission = PermissionEntity.builder()
                .name("READ")
                .build();

        PermissionEntity updatePermission = PermissionEntity.builder()
                .name("UPDATE")
                .build();

        PermissionEntity deletePermission = PermissionEntity.builder()
                .name("DELETE")
                .build();

        PermissionEntity refactorPermission = PermissionEntity.builder()
                .name("REFACTOR")
                .build();

        /* Create ROLES */
        RoleEntity roleAdmin = RoleEntity.builder()
                .roleEnum(RoleEnum.ADMIN)
                .permissionList(Set.of(createPermission, readPermission, updatePermission,
                        deletePermission))
                .build();

        /* CREATE USERS */
        UserEntity root = UserEntity.builder()
                .username("root")
                .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6")
                .isEnabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .roles(Set.of(roleAdmin))
                // .patchList(List.of(rootPath))
                .build();

        return root;
    }

    public static File getLogFilePath() {
        return LOG_FILE_PATH;
    }

}
