package com.devsuperior.dscommerce.dto;

import com.devsuperior.dscommerce.entities.OrderItem;

public class OrderItemDTO {

    private Long produtoId;
    private String name;
    private Double price;
    private Integer quantity;

    public OrderItemDTO(Long produtoId, String name, Double price, Integer quantity) {
        this.produtoId = produtoId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public OrderItemDTO(OrderItem entity) {
        produtoId = entity.getProduct().getId();
        name = entity.getProduct().getName();
        price = entity.getPrice();
        quantity = entity.getQuantity();
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    // Calcula o subtotal do ‘item’ (preço x quantidade)
    public double getSubTotal() {
        return price * quantity;
    }
}
