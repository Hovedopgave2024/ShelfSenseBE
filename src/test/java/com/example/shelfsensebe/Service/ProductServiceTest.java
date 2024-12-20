package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.Product;
import com.example.shelfsensebe.Model.ProductComponent;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.Repository.ProductComponentRepository;
import com.example.shelfsensebe.Repository.ProductRepository;
import com.example.shelfsensebe.utility.TextSanitizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        updatedProduct.setName("<&'>Updated Product<&'>");
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
        existingComponent2.setId(5);

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
        existingProductComponent3.setId(3);
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
    public void testUpdateProductById_Returns_UpdatedProduct() {
        // Arrange
        Product existingProduct = getExistingProduct();
        Product updatedProduct = getUpdatedProduct();

        // Define controlled lists
        List<ProductComponent> productComponentsToUpdate = new ArrayList<>();
        List<ProductComponent> productComponentsToAdd = new ArrayList<>();
        List<ProductComponent> productComponentsToDelete = new ArrayList<>();

        // Populate these lists as per the expected service behavior
        productComponentsToUpdate.add(existingProduct.getProductComponentList().get(0)); // Updated component
        productComponentsToUpdate.add(existingProduct.getProductComponentList().get(1)); // Updated component
        productComponentsToAdd.add(updatedProduct.getProductComponentList().get(2));    // Added component
        productComponentsToDelete.add(existingProduct.getProductComponentList().get(2)); // Deleted component

        // Mock repository and sanitizer behavior
        when(productRepository.findById(existingProduct.getId()))
                .thenReturn(Optional.of(existingProduct));
        when(textSanitizer.sanitize(updatedProduct.getName()))
                .thenReturn("Updated Product");

        when(componentRepository.findById(1))
                .thenReturn(Optional.of(existingProduct.getProductComponentList().get(0).getComponent()));
        when(componentRepository.findById(2))
                .thenReturn(Optional.of(existingProduct.getProductComponentList().get(1).getComponent()));
        when(componentRepository.findById(3))
                .thenReturn(Optional.of(existingProduct.getProductComponentList().get(2).getComponent()));
        when(componentRepository.findById(4))
                .thenReturn(Optional.of(updatedProduct.getProductComponentList().get(2).getComponent()));

        // These passedComponents comes in a specific order from the service class, which is:
        // 1. componentsToDelete, 2. componentsToUpdate, 3. componentsToAdd.
        // That's why we can use passedComponents because we know the flow of the expected components.

        // Mock deleteAll for productComponentsToDelete
        doAnswer(invocation -> {
            List<ProductComponent> passedComponents = invocation.getArgument(0);
            System.out.println("Components to Delete: " + passedComponents);
            assertEquals(productComponentsToDelete, passedComponents);
            return null; // Void method requires null
        }).when(productComponentRepository).deleteAll(productComponentsToDelete);

        // Mock saveAll for productComponentsToUpdate
        doAnswer(invocation -> {
            List<ProductComponent> passedComponents = invocation.getArgument(0);
            System.out.println("Components to Update: " + passedComponents);
            assertEquals(productComponentsToUpdate, passedComponents);
            return passedComponents;
        }).when(productComponentRepository).saveAll(productComponentsToUpdate);

        // Mock saveAll for productComponentsToAdd
        doAnswer(invocation -> {
            List<ProductComponent> passedComponents = invocation.getArgument(0);
            for (ProductComponent component : passedComponents) {
                if (component.getId() == 0) {
                    component.setId(4);
                }
            }
            System.out.println("Components to Add: " + passedComponents);
            assertEquals(productComponentsToAdd, passedComponents); // Ensure exact match
            return passedComponents;
        }).when(productComponentRepository).saveAll(productComponentsToAdd);

        // Act
        Product resultProduct = productService.updateProduct(updatedProduct);

        // Assert final product state
        assertEquals("Updated Product", existingProduct.getName());
        assertEquals(100.0, existingProduct.getPrice());

        // Verify interactions
        verify(productComponentRepository, times(1)).deleteAll(productComponentsToDelete);
        verify(productComponentRepository, times(1)).saveAll(productComponentsToUpdate);
        verify(productComponentRepository, times(1)).saveAll(productComponentsToAdd);
        verify(productRepository, times(1)).save(existingProduct);

        assertEquals(4, productComponentsToAdd.get(0).getId());
        assertEquals(7, productComponentsToUpdate.get(0).getQuantity());
        assertEquals(2, productComponentsToUpdate.get(1).getId());
        assertEquals(3, productComponentsToDelete.get(0).getId());
        assertEquals(existingProduct, resultProduct);

        System.out.println("Test testUpdateProductById_Returns_UpdatedProduct passed successfully.");
    }

    @Test
    public void testUpdateProductById_Returns_400_BadRequest() {
        // Arrange
        Product existingProduct = getExistingProduct();
        Product updatedProduct = getUpdatedProduct();

        // Define controlled lists
        List<ProductComponent> productComponentsToUpdate = new ArrayList<>();
        List<ProductComponent> productComponentsToAdd = new ArrayList<>();
        List<ProductComponent> productComponentsToDelete = new ArrayList<>();

        // Populate these lists as per the expected service behavior
        productComponentsToUpdate.add(existingProduct.getProductComponentList().get(0));

        when(componentRepository.findById(1))
                .thenReturn(Optional.of(existingProduct.getProductComponentList().get(0).getComponent()));

        // Trying to update a component by id in a productComponent that needs to be updated.
        when(componentRepository.findById(5))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Component not found for ID: " + 5));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.updateProduct(updatedProduct);
        });

        // Validate exception details
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("400 BAD_REQUEST \"Product not found\"", exception.getMessage());

        verify(productComponentRepository, never()).deleteAll(productComponentsToDelete);
        verify(productComponentRepository, never()).saveAll(productComponentsToUpdate);
        verify(productComponentRepository, never()).saveAll(productComponentsToAdd);
        verify(productRepository, never()).save(existingProduct);

        System.out.println("Test testUpdateProductById_Returns_400_BadRequest passed successfully.");
    }
}
