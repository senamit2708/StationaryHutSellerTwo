package com.example.stationaryhutsellertwo.model;

import java.util.HashMap;
import java.util.Map;

public class Product {

    private String productName;
    private String productNumber;
    private int productPrice;
    private String brand;
    private int minimumOrder;
    private int quantity;
    private String imageUrl;
    private String category;

    public Product(String productName, String productNumber, int productPrice, String brand, int minimumOrder, int quantity,
                   String imageUrl, String category) {
        this.productName = productName;
        this.productNumber = productNumber;
        this.productPrice = productPrice;
        this.brand = brand;
        this.minimumOrder = minimumOrder;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.category = category;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("productNumber", productNumber);
        result.put("productName", productName);
        result.put("productPrice", productPrice);
        result.put("productQuantity", quantity);
        result.put("minimumOrder", minimumOrder);
        result.put("brand",brand);
        result.put("imageUrl",imageUrl);
        result.put("category",category);
        return result;
    }
}
