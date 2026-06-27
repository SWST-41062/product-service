package com.assignment.product.service;

import com.assignment.product.dto.CreateProductRequest;
import com.assignment.product.dto.ProductResponse;
import com.assignment.product.entity.Product;
import com.assignment.product.exception.ProductNotFoundException;
import com.assignment.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createsProduct() {
        when(productRepository.save(any(Product.class)))
                .thenReturn(new Product(1L, "Keyboard", new BigDecimal("75.50")));

        ProductResponse response = productService.create(
                new CreateProductRequest(" Keyboard ", new BigDecimal("75.50"))
        );

        assertThat(response.productId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Keyboard");
        assertThat(response.unitPrice()).isEqualByComparingTo("75.50");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void getsProductById() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(new Product(1L, "Mouse", new BigDecimal("25.00"))));

        ProductResponse response = productService.getById(1L);

        assertThat(response.name()).isEqualTo("Mouse");
    }

    @Test
    void rejectsMissingProduct() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product 99 was not found");
    }

    @Test
    void deletesExistingProduct() {
        Product product = new Product(1L, "Mouse", new BigDecimal("25.00"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteById(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void rejectsDeletingMissingProduct() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteById(99L))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
