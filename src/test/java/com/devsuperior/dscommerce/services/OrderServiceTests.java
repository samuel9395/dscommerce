package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.OrderItem;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.factory.OrderFactory;
import com.devsuperior.dscommerce.factory.ProductFactory;
import com.devsuperior.dscommerce.factory.UserFactory;
import com.devsuperior.dscommerce.repositories.OrderItemRepository;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ForBiddenException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
/**
 * Testes unitários do {@link OrderService} cobrindo consulta e criação de pedidos
 * com cenários de autorização, existência de recursos e consistência de dados.
 */
public class OrderServiceTests {

    @InjectMocks
    private OrderService service;

    @Mock
    private OrderRepository repository;

    @Mock
    private AuthService authService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserService userService;

    private Long existingOrderId, nonExistingOrderId;
    private Long existingProductId, nonExistingProductId;
    private Order order;
    private OrderDTO orderDTO;
    private User admin, client;
    private Product product;

    @BeforeEach
    void setUp() {
        // Arrange: ids representativos para cenários de sucesso e falha.
        existingOrderId = 1L;
        nonExistingOrderId = 2L;

        existingProductId = 1L;
        nonExistingProductId = 2L;

        // Usuários com perfis diferentes para validar autorização.
        admin = UserFactory.createCustomAdminUser(1L, "Jef");
        client = UserFactory.createCustomClientUser(2L, "Loomie");

        // Entidades base utilizadas pelos testes.
        order = OrderFactory.createOrder(client);
        orderDTO = new OrderDTO(order);
        product = ProductFactory.createProduct();

        // Mocks de consulta por id do pedido.
        Mockito.when(repository.findById(existingOrderId)).thenReturn(Optional.of(order));
        Mockito.when(repository.findById(nonExistingOrderId)).thenReturn(Optional.empty());

        // Mocks de referência de produto para composição dos itens do pedido.
        Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);

        // Mocks de persistência do pedido e de seus itens.
        Mockito.when(repository.save(any())).thenReturn(order);
        Mockito.when(orderItemRepository.saveAll(any())).thenReturn(new ArrayList<>(order.getItems()));
    }

    /**
     * Deve retornar OrderDTO quando o pedido existir e o usuário autenticado tiver
     * permissão de administrador.
     */
    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndAdminLogged() {
        // Simula autorização concedida pelo serviço de autenticação.
        Mockito.doNothing().when(authService).validateUserAdmin(any());

        // Executa busca do pedido por id existente.
        OrderDTO result = service.findById(existingOrderId);

        // Confirma que houve retorno e que o id é o esperado.
        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingOrderId, result.getId());
    }

    /**
     * Deve retornar OrderDTO quando o pedido existir e o cliente autenticado
     * puder acessar o próprio pedido.
     */
    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndSelfClientLogged() {
        // Simula validação de autorização aprovada para o cliente dono do pedido.
        Mockito.doNothing().when(authService).validateUserAdmin(any());

        // Executa busca do pedido por id existente.
        OrderDTO result = service.findById(existingOrderId);

        // Confirma retorno válido e id correto.
        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingOrderId, result.getId());
    }

    /**
     * Deve lançar ForBiddenException quando o pedido existir, mas o cliente
     * autenticado não tiver autorização para acessá-lo.
     */
    @Test
    public void findByIdShouldThrowsForBiddenExceptionWhenIdExistsAndOtherClientLogged() {
        // Simula negação de acesso na camada de autorização.
        Mockito.doThrow(ForBiddenException.class).when(authService).validateUserAdmin(any());

        // Regra esperada: acesso não autorizado deve resultar em ForBiddenException.
        Assertions.assertThrows(ForBiddenException.class, () -> {
            service.findById(existingOrderId);
        });
    }

    /**
     * Deve lançar ResourceNotFoundException quando o pedido não existir.
     */
    @Test
    public void findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist() {
        // Simula autorização concedida para isolar o cenário de recurso inexistente.
        Mockito.doNothing().when(authService).validateUserAdmin(any());

        // Regra esperada: id inexistente deve gerar exceção de recurso não encontrado.
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingOrderId);
        });
    }

    /**
     * Deve inserir pedido e retornar OrderDTO quando o usuário autenticado for admin.
     */
    @Test
    public void insertShouldReturnOrderDTOWhenAdminLogged() {
        // Simula usuário autenticado com perfil administrador.
        Mockito.when(userService.authenticated()).thenReturn(admin);

        // Executa inserção do pedido.
        OrderDTO result = service.insert(orderDTO);

        // Confirma que o pedido foi criado e convertido para DTO.
        Assertions.assertNotNull(result);
    }

    /**
     * Deve inserir pedido e retornar OrderDTO quando o usuário autenticado for cliente.
     */
    @Test
    public void insertShouldReturnOrderDTOWhenClientLogged() {
        // Simula usuário autenticado com perfil cliente.
        Mockito.when(userService.authenticated()).thenReturn(client);

        // Executa inserção do pedido.
        OrderDTO result = service.insert(orderDTO);

        // Confirma retorno válido após persistência.
        Assertions.assertNotNull(result);
    }

    /**
     * Deve lançar UsernameNotFoundException quando não houver usuário autenticado.
     */
    @Test
    public void insertShouldThrowsUserNotFoundExceptionWhenUserNotLogged() {
        // Simula ausência de autenticação no momento da criação do pedido.
        Mockito.doThrow(UsernameNotFoundException.class).when(userService).authenticated();

        order.setClient(new User());
        orderDTO = new OrderDTO(order);

        // Regra esperada: sem usuário autenticado, o serviço deve falhar com exceção.
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.insert(orderDTO);
        });
    }

    /**
     * Deve lançar EntityNotFoundException quando algum item do pedido referenciar
     * produto inexistente.
     */
    @Test
    public void insertShouldThrowsEntityNotFoundExceptionWhenOrderProductIdDoesNotExist() {
        // Simula usuário autenticado para isolar a falha de produto inexistente.
        Mockito.when(userService.authenticated()).thenReturn(client);

        // Força item com produto inexistente para disparar erro no getReferenceById.
        product.setId(nonExistingProductId);
        OrderItem orderItem = new OrderItem(order, product, 2, 10.0);
        order.getItems().add(orderItem);

        orderDTO = new OrderDTO(order);

        // Regra esperada: referência inválida de produto deve propagar EntityNotFoundException.
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            service.insert(orderDTO);
        });
    }
}
