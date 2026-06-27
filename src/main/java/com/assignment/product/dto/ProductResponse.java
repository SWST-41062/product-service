package com.assignment.product.dto;

import java.math.BigDecimal;

public record ProductResponse(Long productId, String name, BigDecimal unitPrice) {
}
