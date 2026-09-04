package com.devsuperior.dscommerce.factory;

import com.devsuperior.dscommerce.projections.UserDetailsProjection;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Fábrica de projeções usadas nos testes de autenticação/autorização.
 */
public class UserDetailsFactory {

    /**
     * Cria um usuário com perfil de cliente.
     */
    public static List<UserDetailsProjection> createCustomClient(String userName) {
        List<UserDetailsProjection> list = new ArrayList<>();
        list.add(new UserDetailsImpl(userName, "123456", 1L, "ROLE_CLIENT"));
        return list;
    }

    /**
     * Cria um usuário com perfil de administrador.
     */
    public static List<UserDetailsProjection> createCustomAdmin(String userName) {
        List<UserDetailsProjection> list = new ArrayList<>();
        list.add(new UserDetailsImpl(userName, "123456", 2L, "ROLE_ADMIN"));
        return list;
    }

    /**
     * Cria um usuário com os perfis de cliente e administrador.
     */
    public static List<UserDetailsProjection> createCustomAdminClientUser(String userName) {
        List<UserDetailsProjection> list = new ArrayList<>();
        list.add(new UserDetailsImpl(userName, "123456", 1L, "ROLE_CLIENT"));
        list.add(new UserDetailsImpl(userName, "123456", 2L, "ROLE_ADMIN"));
        return list;
    }
}

/**
 * Implementação simples de {@link UserDetailsProjection} para cenários de teste.
 */
class UserDetailsImpl implements UserDetailsProjection {

    private String userName;
    private String password;
    private Long roleId;
    private String authority;

    public UserDetailsImpl() {}

    public UserDetailsImpl(String userName, String password, Long roleId, String authority) {
        this.userName = userName;
        this.password = password;
        this.roleId = roleId;
        this.authority = authority;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Long getRoleId() {
        return roleId;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
