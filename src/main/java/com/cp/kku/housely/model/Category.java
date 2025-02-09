package com.cp.kku.housely.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Category {

    private Long categoryId;
    private String categoryName;
    private String description;
    private String imageBase64;
    private List<Product> productsInCategory = new ArrayList<>();

}
