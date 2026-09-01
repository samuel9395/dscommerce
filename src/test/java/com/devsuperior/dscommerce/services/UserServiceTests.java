package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.factory.UserDetailsFactory;
import com.devsuperior.dscommerce.factory.UserFactory;
import com.devsuperior.dscommerce.projections.UserDetailsProjection;
import com.devsuperior.dscommerce.repositories.UserRepository;
import com.devsuperior.dscommerce.util.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
/**
 * Testes unitários do {@link UserService} com foco na carga de usuário por e-mail.
 * O repositório é mockado para isolar o comportamento do serviço.
 */
public class UserServiceTests {

    // Serviço real com dependência simulada.
    @InjectMocks
    private UserService service;

    // Repositório mockado para controlar retornos de consulta.
    @Mock
    private UserRepository repository;

    @Mock
    private CustomUserUtil userUtil;

    private String existingUserName, nonExistingUserName;
    private User user;
    private List<UserDetailsProjection> userDetails;

    @BeforeEach
    void setUp() throws Exception {
        // Arrange: dados base para cenários de sucesso e falha.
        existingUserName = "maria@gmail.com";
        nonExistingUserName = "user@gmail.com";

        user = UserFactory.createCustomClientUser(1L, existingUserName);
        userDetails = UserDetailsFactory.createCustomAdmin(existingUserName);
    }

    /**
     * Deve retornar UserDetails quando o usuário existir.
     */
    @Test
    public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {

        Mockito.when(repository.searchUserAndRolesByEmail(existingUserName)).thenReturn(userDetails);
        UserDetails result = service.loadUserByUsername(existingUserName);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingUserName, result.getUsername());
    }

    /**
     * Deve lançar UsernameNotFoundException quando o usuário não existir.
     */
    @Test
    public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

        Mockito.when(repository.searchUserAndRolesByEmail(nonExistingUserName)).thenReturn(new ArrayList<>());
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(nonExistingUserName);
        });
    }

    /**
     * Deve retornar o usuario autenticado quando ele existir.
     *
     * @throws Exception
     */
    @Test
    public void authenticatedShouldReturnUserWhenUserExists() {

        Mockito.when(repository.findByEmail(existingUserName)).thenReturn(Optional.of(user));
        Mockito.when(userUtil.getLoggedUserName()).thenReturn(existingUserName);

        User result = service.authenticated();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingUserName, result.getUsername());
    }

    /**
     * Deve lançar UsernameNotFoundException quando o usuário não existir.
     */
    @Test
    public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

        Mockito.when(repository.findByEmail(nonExistingUserName)).thenReturn(Optional.empty());
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.authenticated();
        });
    }

}
