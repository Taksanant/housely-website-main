package com.cp.kku.housely.model;


import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Product {
	private Long id;
	private String productCode;
	private String brandName;
	private String ProductName;
    private double price;
    private int quantity;
    private String imageBase64;
    private String description;
    private List<Category> categories = new ArrayList<>();
    private List<Room> rooms = new ArrayList<>();
	
}
