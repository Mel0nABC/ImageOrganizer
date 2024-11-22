package com.example.weblogin.otherClasses;

import java.util.Set;

import com.example.weblogin.persistence.entity.PermissionEntity;
import com.example.weblogin.persistence.entity.RoleEntity;
import com.example.weblogin.persistence.entity.RoleEnum;

public class GetRoles {

    public static RoleEntity getRoles(String roleSelected) {

        RoleEntity newRoleSelected = null;

        // Create PERMISSIONS
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

        switch (roleSelected) {
            case "ADMIN":
                newRoleSelected = RoleEntity.builder()
                        .roleEnum(RoleEnum.ADMIN)
                        .permissionList(Set.of(createPermission, readPermission, updatePermission,
                                deletePermission))
                        .build();
                break;

            case "USER":
                newRoleSelected = RoleEntity.builder()
                        .roleEnum(RoleEnum.USER)
                        .permissionList(Set.of(createPermission, readPermission))
                        .build();
                break;
            case "INVITED":

                newRoleSelected = RoleEntity.builder()
                        .roleEnum(RoleEnum.INVITED)
                        .permissionList(Set.of(readPermission))
                        .build();
                break;
            case "DEVELOPER":

                newRoleSelected = RoleEntity.builder()
                        .roleEnum(RoleEnum.DEVELOPER)
                        .permissionList(Set.of(createPermission, readPermission, updatePermission,
                                deletePermission, refactorPermission))
                        .build();
                break;

            default:
                break;
        }

        return newRoleSelected;
    }

}
