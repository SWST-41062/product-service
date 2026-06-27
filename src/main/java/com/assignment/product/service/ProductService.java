package com.assignment.product.service;

import com.assignment.product.dto.CreateProductRequest;
import com.assignment.product.dto.ProductResponse;
import com.assignment.product.entity.Product;
import com.assignment.product.exception.ProductNotFoundException;
import com.assignment.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Product product = new Product(request.name().trim(), request.unitPrice());
        return toResponse(productRepository.save(product));
    }

    public ProductResponse getById(Long productId) {
        return productRepository.findById(productId)
                .map(this::toResponse)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Transactional
    public void deleteById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        productRepository.delete(product);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getProductId(), product.getName(), product.getUnitPrice());
    }
}
