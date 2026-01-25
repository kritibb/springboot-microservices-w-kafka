package com.thekreatib.products.service;

import com.thekreatib.products.model.ProductModel;

import java.util.concurrent.ExecutionException;

public interface ProductService {
    String createProduct(ProductModel productModel) throws Exception;
}
