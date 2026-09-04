package com.devsuperior.dscommerce.factory;

import com.devsuperior.dscommerce.entities.*;

import java.time.Instant;

/**
 * Fábrica de pedidos para montagem de cenários de teste.
 * Centraliza a criação de entidades com dados consistentes e reaproveitáveis.
 */
public class OrderFactory {

    /**
     * Cria um pedido padrão com:
     * cliente informado, status WAITING_PAYMENT e um item associado.
     * Esse cenário base é usado nos testes de serviço de pedido.
     */
    public static Order createOrder(User client) {
        // Cria o pedido principal vinculado ao cliente do cenário.
        Order order = new Order(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, client);

        // Adiciona um item para representar pedido com conteúdo válido.
        Product product = ProductFactory.createProduct();
        OrderItem orderItem = new OrderItem(order, product, 2, 10.0);
        order.getItems().add(orderItem);

        return order;
    }
}
