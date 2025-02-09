package com.cp.kku.housely.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Room {
    private Long id;
    private String description;
    private String imageBase64;
    private String roomName;
    private List<Product> productsInRoom = new ArrayList<>();
}

