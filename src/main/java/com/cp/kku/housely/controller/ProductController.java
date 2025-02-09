package com.cp.kku.housely.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cp.kku.housely.model.Category;
import com.cp.kku.housely.model.Product;
import com.cp.kku.housely.model.Room;
import com.cp.kku.housely.service.CategoryService;
import com.cp.kku.housely.service.ProductService;
import com.cp.kku.housely.service.RoomService;


@Controller
@RequestMapping("/admin/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RoomService roomService;

    @GetMapping("/detail/{id}")
    public String showProductDetail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id).block());
        return "productDetail";
    }
    

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts().collectList().block());
        return "product-list";
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories().collectList().block());
        model.addAttribute("rooms", roomService.getAllRooms().collectList().block());

        return "add-product-form";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam("categoryIds") List<Long> categoryIds,
                              @RequestParam("roomIds") List<Long> roomIds,
                              @RequestParam(value = "image", required = false) MultipartFile file) {
        handleImageUpload(product, file);
        setProductCategoriesAndRooms(product, categoryIds, roomIds);
        productService.createProduct(product).block();
        try {
            Thread.sleep(500); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return "redirect:/admin/products";
    }
    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id).block();
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories().collectList().block());
        model.addAttribute("rooms", roomService.getAllRooms().collectList().block());
        model.addAttribute("currentImagePath", "/uploads/" + product.getImageBase64());
        return "edit-product-form";
    }
    
    @PostMapping("/save/{id}")
    public String updateProduct(@ModelAttribute Product product,
                                @RequestParam("categoryIds") List<Long> categoryIds,
                                @RequestParam("roomIds") List<Long> roomIds,
                                @RequestParam(value = "image", required = false) MultipartFile file,
                                @PathVariable Long id) {
        product.setId(id);
        Product existingProduct = productService.getProductById(id).block();
        
        if (file != null && !file.isEmpty()) {
            handleImageUpload(product, file);
        } else {
            // Keep the existing image if no new image is uploaded
            product.setImageBase64(existingProduct.getImageBase64());
        }

        setProductCategoriesAndRooms(product, categoryIds, roomIds);
        productService.createProduct(product).block();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }        
        return "redirect:/admin/products";
    }

    private void handleImageUpload(Product product, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path path = Paths.get(uploadPath + fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                product.setImageBase64(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                // Consider proper error handling here
            }
        }
    }

    private void setProductCategoriesAndRooms(Product product, List<Long> categoryIds, List<Long> roomIds) {
        List<Category> categories = categoryIds.stream()
                .map(id -> {
                    Category category = new Category();
                    category.setCategoryId(id);
                    return category;
                })
                .collect(Collectors.toList());
        product.setCategories(categories);

        List<Room> rooms = roomIds.stream()
                .map(id -> {
                    Room room = new Room();
                    room.setId(id);
                    return room;
                })
                .collect(Collectors.toList());
        product.setRooms(rooms);
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id).block();
        return "redirect:/admin/products";
    }
}

