package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.CategoryDTO;
import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.factory.CategoryFactory;
import com.devsuperior.dscommerce.repositories.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
/**
 * Testes unitários do {@link CategoryService} com o repositório mockado.
 * O objetivo é validar o mapeamento de entidades para DTOs sem depender do banco.
 */
public class CategoryServiceTest {

    // Serviço real com as dependências simuladas pelo Mockito.
    @InjectMocks
    private CategoryService service;

    @Mock
    private CategoryRepository repository;

    private Category category;
    private List<Category> list;

    @BeforeEach
    void setUp() {
        // Arrange: cria o cenário base usado pelos testes.
        category = CategoryFactory.createCategory();
        list = new ArrayList<>();
        list.add(category);

        // Define o retorno esperado do repositório para findAll().
        Mockito.when(repository.findAll()).thenReturn(list);
    }

    /**
     * Verifica se findAll retorna uma lista de DTOs com os dados esperados.
     */
    @Test
    public void findAllShouldReturnListCategoryDTO() {
        // Executa o método alvo para obter as categorias já convertidas para DTO.
        List<CategoryDTO> result = service.findAll();

        // Garante que o serviço retornou exatamente a categoria preparada no cenário.
        Assertions.assertEquals(1, result.size());

        // Garante que o id do DTO corresponde ao id da entidade mockada.
        Assertions.assertEquals(category.getId(), result.get(0).getId());

        // Garante que o nome do DTO corresponde ao nome da entidade mockada.
        Assertions.assertEquals(category.getName(), result.get(0).getName());
    }
}
