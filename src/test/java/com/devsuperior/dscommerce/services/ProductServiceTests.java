package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.dto.ProductMinDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.factory.ProductFactory;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.DatabaseException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
/**
 * Testes unitários do {@link ProductService} cobrindo cenários de sucesso e erro.
 * As dependências externas são mockadas para isolar a regra de negócio.
 */ public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private long existingProductId, nonExistingProductId, dependentProductId;
    private Product product;
    private String productName;
    private PageImpl<Product> page;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        // Arrange: ids representativos para cada cenário de teste.
        existingProductId = 1L;
        nonExistingProductId = 2L;
        dependentProductId = 3L;

        productName = "Playstation 5";
        product = ProductFactory.createProduct(productName);
        page = new PageImpl<>(List.of(product));
        productDTO = new ProductDTO(product);

        // Mocks para consultas.
        Mockito.when(repository.findById(existingProductId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingProductId)).thenReturn(Optional.empty());

        Mockito.when(repository.searchByName(any(), (Pageable) any())).thenReturn(page);

        // Mock para persistência.
        Mockito.when(repository.save(any())).thenReturn(product);

        // Mocks para update (referência gerenciada).
        Mockito.when(repository.getReferenceById(existingProductId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);

        // Mocks para delete.
        Mockito.when(repository.existsById(existingProductId)).thenReturn(true);
        Mockito.when(repository.existsById(dependentProductId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingProductId)).thenReturn(false);

        Mockito.doNothing().when(repository).deleteById(existingProductId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentProductId);
        Mockito.doThrow(ResourceNotFoundException.class).when(repository).deleteById(nonExistingProductId);
    }

    /**
     * Deve retornar ProductDTO quando o id informado existir.
     */
    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        // Executa a busca usando um id que existe no cenário mockado.
        ProductDTO result = service.findById(existingProductId);

        // Confirma que o serviço retornou um DTO válido.
        Assertions.assertNotNull(result);
        // Confirma que o id retornado é o mesmo id solicitado.
        Assertions.assertEquals(existingProductId, result.getId());
        // Confirma que o nome do DTO veio corretamente da entidade mockada.
        Assertions.assertEquals(product.getName(), result.getName());
    }

    /**
     * Deve lançar ResourceNotFoundException quando o id não existir.
     */
    @Test
    public void findByIdShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {
        // Valida a regra: id inexistente deve resultar em exceção de recurso não encontrado.
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingProductId);
        });
    }

    /**
     * Deve retornar uma página de ProductMinDTO com o conteúdo esperado.
     */
    @Test
    public void findAllShouldReturnPagedProductMinDTO() {
        // Paginação e filtro usados para exercitar a listagem paginada.
        Pageable pageable = PageRequest.of(0, 12);
        String name = "Playstation 5";

        // Executa a consulta paginada por nome.
        Page<ProductMinDTO> result = service.findAll(name, pageable);

        // Confirma que a página foi criada.
        Assertions.assertNotNull(result);
        // Confirma que o tamanho da página no cenário mockado é 1.
        Assertions.assertEquals(1, result.getSize());
        // Confirma que o item retornado preserva o nome do produto esperado.
        Assertions.assertEquals(product.getName(), result.iterator().next().getName());
    }

    /**
     * Deve inserir e retornar o produto persistido em formato DTO.
     */
    @Test
    public void insertShouldReturnProductDTO() {
        // Executa a inserção com um DTO válido.
        ProductDTO result = service.insert(productDTO);

        // Confirma que o retorno não é nulo e representa o produto persistido.
        Assertions.assertNotNull(result);
        Assertions.assertEquals(product.getId(), result.getId());
    }

    /**
     * Deve atualizar e retornar ProductDTO quando o id existir.
     */
    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {
        // Executa a atualização para um id existente.
        ProductDTO result = service.update(existingProductId, productDTO);

        // Confirma que o DTO atualizado foi retornado.
        Assertions.assertNotNull(result);
        // Confirma que o id retornado corresponde ao id atualizado.
        Assertions.assertEquals(existingProductId, result.getId());
        // Confirma que o nome refletiu os dados enviados no DTO de entrada.
        Assertions.assertEquals(productDTO.getName(), result.getName());
    }

    /**
     * Deve lançar ResourceNotFoundException ao tentar atualizar id inexistente.
     */
    @Test
    public void updateShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {
        // Valida a regra: não é permitido atualizar produto inexistente.
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingProductId, productDTO);
        });
    }

    /**
     * Deve excluir sem exceção quando o id existir.
     */
    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        // A exclusão de id existente não deve lançar exceção.
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingProductId);
        });

        // Confirma a interação esperada com o repositório (uma chamada de exclusão).
        Mockito.verify(repository, Mockito.times(1)).deleteById(existingProductId);
    }

    /**
     * Deve lançar ResourceNotFoundException quando o id não existir na exclusão.
     */
    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        // Valida a regra: excluir id inexistente deve gerar erro de recurso não encontrado.
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingProductId);
        });
    }

    /**
     * Deve lançar DatabaseException quando houver restrição de integridade na exclusão.
     */
    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        // Valida a tradução de erro: violação de integridade deve virar DatabaseException.
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentProductId);
        });
    }
}
