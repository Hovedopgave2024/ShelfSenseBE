package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.Product;
import com.example.shelfsensebe.Model.ProductComponent;
import com.example.shelfsensebe.Service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

public class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    @Mock
    private HttpSession session;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    private Product getProduct() {
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
        productComponent1.setQuantity(5);
        productComponent1.setProduct(updatedProduct);
        productComponent1.setComponent(component1);

        ProductComponent productComponent2 = new ProductComponent();
        productComponent2.setId(2);
        productComponent2.setQuantity(10);
        productComponent2.setProduct(updatedProduct);
        productComponent2.setComponent(component2);

        ProductComponent productComponent3 = new ProductComponent();
        productComponent3.setId(3);
        productComponent3.setQuantity(15);
        productComponent3.setProduct(updatedProduct);
        productComponent3.setComponent(component3);

        List<ProductComponent> productComponents = new ArrayList<>();
        productComponents.add(productComponent1);
        productComponents.add(productComponent2);
        productComponents.add(productComponent3);
        updatedProduct.setProductComponentList(productComponents);
        return updatedProduct;
    }

    @Test
    public void testUpdateProductById_Returns_200_Ok() {
        Product updatedProduct = getProduct();

        UserDTO mockUser = new UserDTO(1, "Test User");

        when(session.getAttribute("user")).thenReturn(mockUser);
        when(productService.updateProduct(updatedProduct)).thenReturn(updatedProduct);

        ResponseEntity<Product> response = productController.updateProductById(updatedProduct, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProduct, response.getBody());
        assertEquals(3, updatedProduct.getProductComponentList().size());

        // Assert the details of ProductComponents
        assertEquals(1, updatedProduct.getProductComponentList().get(0).getComponent().getId());
        assertEquals(2, updatedProduct.getProductComponentList().get(1).getComponent().getId());
        assertEquals(3, updatedProduct.getProductComponentList().get(2).getComponent().getId());

        assertEquals(5, updatedProduct.getProductComponentList().get(0).getQuantity());
        assertEquals(10, updatedProduct.getProductComponentList().get(1).getQuantity());
        assertEquals(15, updatedProduct.getProductComponentList().get(2).getQuantity());

        verify(session, times(1)).getAttribute("user");
        verify(productService, times(1)).updateProduct(updatedProduct);

        System.out.println("Test testUpdateProductById_Returns_200_Ok passed successfully.");
    }

    @Test
    public void testUpdateProductById_Returns_401_Unauthorized() {
        Product updatedProduct = getProduct();

        when(session.getAttribute("user")).thenReturn(null);
        when(productService.updateProduct(updatedProduct)).thenReturn(updatedProduct);

        ResponseEntity<Product> response = productController.updateProductById(updatedProduct, session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());

        verify(session, times(1)).getAttribute("user");
        verify(productService, never()).updateProduct(any());

        System.out.println("Test testUpdateProductById_Returns_401_Unauthorized passed successfully.");
    }

    @Test
    public void testUpdateProductById_Returns_400_BadRequest() {
        Product invalidProduct = new Product();
        invalidProduct.setId(1);
        invalidProduct.setName(null);
        invalidProduct.setPrice(-50.0);

        UserDTO mockUser = new UserDTO(1, "Test User");
        when(session.getAttribute("user")).thenReturn(mockUser);

        when(productService.updateProduct(invalidProduct)).thenThrow(new IllegalArgumentException("Invalid product details"));

        ResponseEntity<Product> response = productController.updateProductById(invalidProduct, session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(session, times(1)).getAttribute("user");
        verify(productService, times(1)).updateProduct(invalidProduct);

        System.out.println("Test testUpdateProductById_Returns_400_BadRequest passed successfully.");
    }
}
