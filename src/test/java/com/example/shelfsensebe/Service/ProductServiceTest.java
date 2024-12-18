package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.utility.TextSanitizer;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.Product;
import com.example.shelfsensebe.Model.ProductComponent;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.Repository.ProductComponentRepository;
import com.example.shelfsensebe.Repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private AutoCloseable closeable;

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductComponentRepository productComponentRepository;

    @Mock
    private ComponentRepository componentRepository;

    @Mock
    private TextSanitizer textSanitizer;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    private Product getUpdatedProduct() {
        Product updatedProduct = new Product();
        updatedProduct.setId(1);
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(100.0);

        Component component1 = new Component();
        component1.setId(1);

        Component component2 = new Component();
        component2.setId(2);

        Component component3 = new Component();
        component3.setId(3);

        ProductComponent productComponent1 = new ProductComponent();
        productComponent1.setId(1);
        productComponent1.setQuantity(7);
        productComponent1.setProduct(updatedProduct);
        productComponent1.setComponent(component1);

        ProductComponent productComponent2 = new ProductComponent();
        productComponent2.setId(2);
        productComponent2.setQuantity(10);
        productComponent2.setProduct(updatedProduct);
        productComponent2.setComponent(component2);

        ProductComponent productComponent4 = new ProductComponent();
        productComponent4.setId(4);
        productComponent4.setQuantity(100);
        productComponent4.setProduct(updatedProduct);
        productComponent4.setComponent(component3);

        List<ProductComponent> productComponents = new ArrayList<>();
        productComponents.add(productComponent1);
        productComponents.add(productComponent2);
        productComponents.add(productComponent4);
        updatedProduct.setProductComponentList(productComponents);
        return updatedProduct;
    }

    private Product getExistingProduct() {
        Product existingProduct = new Product();
        existingProduct.setId(1);
        existingProduct.setName("Existing Product");
        existingProduct.setPrice(200.0);

        Component existingComponent1 = new Component();
        existingComponent1.setId(1);

        Component existingComponent2 = new Component();
        existingComponent2.setId(2);

        Component existingComponent3 = new Component();
        existingComponent3.setId(3);

        ProductComponent existingProductComponent1 = new ProductComponent();
        existingProductComponent1.setId(1);
        existingProductComponent1.setQuantity(5);
        existingProductComponent1.setProduct(existingProduct);
        existingProductComponent1.setComponent(existingComponent1);

        ProductComponent existingProductComponent2 = new ProductComponent();
        existingProductComponent2.setId(2);
        existingProductComponent2.setQuantity(10);
        existingProductComponent2.setProduct(existingProduct);
        existingProductComponent2.setComponent(existingComponent2);

        ProductComponent existingProductComponent3 = new ProductComponent();
        existingProductComponent3.setId(4);
        existingProductComponent3.setQuantity(15);
        existingProductComponent3.setProduct(existingProduct);
        existingProductComponent3.setComponent(existingComponent3);

        List<ProductComponent> existingProductComponents = new ArrayList<>();
        existingProductComponents.add(existingProductComponent1);
        existingProductComponents.add(existingProductComponent2);
        existingProductComponents.add(existingProductComponent3);
        existingProduct.setProductComponentList(existingProductComponents);
        return existingProduct;
    }

    @Test
    public void testUpdateProduct_FlowVerification() {
        // Arrange
        Product existingProduct = getExistingProduct();
        Product updatedProduct = getUpdatedProduct();

        // Mock repository and sanitizer behavior
        when(productRepository.findById(existingProduct.getId()))
                .thenReturn(Optional.of(existingProduct));
        when(textSanitizer.sanitize(updatedProduct.getName()))
                .thenReturn(updatedProduct.getName());

        when(componentRepository.findById(1)).thenReturn(Optional.of(existingProduct.getProductComponentList().get(0).getComponent()));
        when(componentRepository.findById(2)).thenReturn(Optional.of(existingProduct.getProductComponentList().get(1).getComponent()));
        when(componentRepository.findById(3)).thenReturn(Optional.of(existingProduct.getProductComponentList().get(2).getComponent()));

        // Act
        Product resultProduct = productService.updateProduct(updatedProduct);

        // Assert

        // Verify name and price update
        assertEquals(updatedProduct.getName(), resultProduct.getName(), "Product name should be updated");
        assertEquals(updatedProduct.getPrice(), resultProduct.getPrice(), "Product price should be updated");

        // Verify repository interactions
        verify(productRepository).findById(existingProduct.getId());
        verify(productRepository).save(existingProduct);
    }
}