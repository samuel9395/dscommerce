package com.devsuperior.dscommerce.factory;

import com.devsuperior.dscommerce.entities.Role;
import com.devsuperior.dscommerce.entities.User;

import java.time.LocalDate;

/**
 * Fábrica de usuários para montagem de cenários de teste.
 */
public class UserFactory {

    /**
     * Cria um usuário padrão com perfil de cliente.
     */
    public static User createClientUser() {
        User user = new User(1L, "Maria Brown", "maria@gmail.com", "988888888", LocalDate.parse("1990-01-01"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG");
        user.addRole(new Role(1L, "ROLE_CLIENT"));
        return user;
    }

    /**
     * Cria um usuário padrão com perfil de administrador.
     */
    public static User createAdminUser() {
        User user = new User(2L, "Alex Shaun", "alex@gmail.com", "988888888", LocalDate.parse("1989-10-10"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG");
        user.addRole(new Role(2L, "ROLE_ADMIN"));
        return user;
    }

    /**
     * Cria um usuário cliente com id e e-mail customizados.
     */
    public static User createCustomClientUser(Long userId, String userName) {
        User user = new User(userId, "Maria Brown", userName, "988888888", LocalDate.parse("1990-01-01"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG");
        user.addRole(new Role(1L, "ROLE_CLIENT"));
        return user;
    }

    /**
     * Cria um usuário administrador com id e e-mail customizados.
     */
    public static User createCustomAdminUser(Long userId, String userName) {
        User user = new User(userId, "Alex Shaun", userName, "988888888", LocalDate.parse("1989-10-10"), "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG");
        user.addRole(new Role(2L, "ROLE_ADMIN"));
        return user;
    }
}
