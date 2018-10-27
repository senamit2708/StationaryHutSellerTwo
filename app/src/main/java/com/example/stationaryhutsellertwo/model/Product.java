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

    public Product(String productName, String productNumber, int productPrice, String brand, int minimumOrder, int quantity,String imageUrl) {
        this.productName = productName;
        this.productNumber = productNumber;
        this.productPrice = productPrice;
        this.brand = brand;
        this.minimumOrder = minimumOrder;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("productNumber", productNumber);
        result.put("productName", productName);
        result.put("productPrice", productPrice);
        result.put("productQuantity", quantity);
        result.put("minimumOrder", minimumOrder);
        result.put("brand",brand);
        return result;
    }
}
