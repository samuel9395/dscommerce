package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.factory.UserFactory;
import com.devsuperior.dscommerce.services.exceptions.ForBiddenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
/**
 * Testes unitários do {@link AuthService} para validar regras de autorização
 * baseadas no usuário autenticado (admin, próprio cliente ou outro cliente).
 */
public class AuthServiceTests {

    @InjectMocks
    private AuthService service;

    @Mock
    private UserService userService;

    private User admin, selfClient, otherClient;

    @BeforeEach
    void setUp() {
        // Arrange: usuários representando os perfis usados nos cenários de autorização.
        admin = UserFactory.createAdminUser();
        selfClient = UserFactory.createCustomClientUser(1L, "Bob");
        otherClient = UserFactory.createCustomClientUser(2L, "Ana");
    }

    /**
     * Deve permitir acesso quando o usuário autenticado possui papel de administrador.
     */
    @Test
    public void validateUserAdminShouldDoNothingWhenAdminLogged() {
        // Simula autenticação como admin.
        Mockito.when(userService.authenticated()).thenReturn(admin);
        Long userId = admin.getId();

        // Regra esperada: admin pode acessar qualquer recurso sem exceção.
        Assertions.assertDoesNotThrow(() -> {
            service.validateUserAdmin(userId);
        });
    }

    /**
     * Deve permitir acesso quando o cliente autenticado acessa seu próprio recurso.
     */
    @Test
    public void validateUserAdminShouldDoNothingWhenSelfClientLogged() {
        // Simula autenticação do próprio cliente dono do recurso.
        Mockito.when(userService.authenticated()).thenReturn(selfClient);
        Long userId = selfClient.getId();

        // Regra esperada: o próprio cliente pode acessar seus dados.
        Assertions.assertDoesNotThrow(() -> {
            service.validateUserAdmin(userId);
        });
    }

    /**
     * Deve negar acesso quando um cliente tenta acessar recurso de outro cliente.
     */
    @Test
    public void validateUserAdminThroesForbiddenExceptionWhenClientOtherLogged() {
        // Simula cliente autenticado diferente do dono do recurso.
        Mockito.when(userService.authenticated()).thenReturn(selfClient);
        Long userId = otherClient.getId();

        // Regra esperada: deve lançar ForBiddenException por acesso indevido.
        Assertions.assertThrows(ForBiddenException.class, () -> {
            service.validateUserAdmin(userId);
        });
    }
}
