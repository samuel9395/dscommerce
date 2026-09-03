package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.UserDTO;
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

    /**
     * Deve retornar UserDTO quando houver usuário autenticado.
     * O teste usa spy para simular o retorno de authenticated() e isolar a regra do getUser().
     */
    @Test
    public void getMeShouldReturnUserDTOWhenUserAuthenticated() {
        // Cria um spy do serviço para controlar apenas o método authenticated().
        UserService spyUserService = Mockito.spy(service);

        // Simula um usuário autenticado válido retornado pela camada de autenticação.
        Mockito.doReturn(user).when(spyUserService).authenticated();

        // Executa o método que deve montar o DTO do usuário logado.
        UserDTO result = spyUserService.getUser();

        // Confirma que o DTO foi criado.
        Assertions.assertNotNull(result);

        // Confirma que o e-mail no DTO corresponde ao usuário autenticado do cenário.
        Assertions.assertEquals(existingUserName, result.getEmail());
    }

    /**
     * Deve propagar UsernameNotFoundException quando não houver usuário autenticado.
     */
    @Test
    public void getMeShouldThrowUsernameNotFoundExceptionWhenUserDoesNotAuthenticated() {
        // Cria um spy para forçar falha de autenticação neste cenário.
        UserService spyUserService = Mockito.spy(service);

        // Simula ausência de usuário autenticado.
        Mockito.doThrow(UsernameNotFoundException.class).when(spyUserService).authenticated();

        // Valida a regra: sem autenticação, getUser() deve lançar UsernameNotFoundException.
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            spyUserService.getUser();
        });
    }
}
