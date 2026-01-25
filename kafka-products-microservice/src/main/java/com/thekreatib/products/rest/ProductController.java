package com.thekreatib.products.rest;

import com.thekreatib.products.model.ProductModel;
import com.thekreatib.products.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final Logger LOG= LoggerFactory.getLogger(this.getClass());
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService=productService;
    }

    @PostMapping
    public ResponseEntity<Object> createProduct(@RequestBody ProductModel product){
        String productId= null;
        try {
            productId = productService.createProduct(product);
        } catch (Exception e) {
            LOG.error("Exception caught: "+e.getMessage());
          return ResponseEntity.status(HttpStatus.CREATED).body(new ErrorMessage(new Date(), e.getMessage(),"/products"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Created product with id: "+productId);
    }
}
