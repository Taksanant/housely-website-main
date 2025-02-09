package com.cp.kku.housely.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cp.kku.housely.model.Product;
import com.cp.kku.housely.service.CategoryService;
import com.cp.kku.housely.service.ProductService;
import com.cp.kku.housely.service.RoomService;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RoomService roomService;

    @GetMapping("/product")
    public String showProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts().collectList().block());
        model.addAttribute("categorys", categoryService.getAllCategories().collectList().block());
        model.addAttribute("rooms", roomService.getAllRooms().collectList().block());
        model.addAttribute("userName", getCurrentUserId());
        return "user-product";
    }
 

    public String getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
    return null;
}

    @GetMapping("/product/{id}")
    public String getViewProductById(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id).block());
        return "productDetailUser";
    }

    @GetMapping("/product/category/{id}")
    public String getViewProductByCategory(@PathVariable Long id, Model model) {
        model.addAttribute("products", productService.getProductsByCategoryId(id).collectList().block());
        model.addAttribute("categorys", categoryService.getAllCategories().collectList().block());
        model.addAttribute("rooms", roomService.getAllRooms().collectList().block());
        model.addAttribute("category", categoryService.getCategoryById(id).block());
        model.addAttribute("userName", getCurrentUserId());
        return "category-user-page";
    }

    @GetMapping("/product/room/{id}")
    public String getViewProductByRoom(@PathVariable Long id, Model model) {
        model.addAttribute("products", productService.getProductsByRoomId(id).collectList().block());
        model.addAttribute("categorys", categoryService.getAllCategories().collectList().block());
        model.addAttribute("rooms", roomService.getAllRooms().collectList().block());
        model.addAttribute("room", roomService.getRoomById(id).block());
        model.addAttribute("userName", getCurrentUserId());
        
        return "room-user-page";
    }

    // @PutMapping("/decreaseQuantity/{id}")
    // public Mono<ResponseEntity<Product>> decreaseProductQuantity(@PathVariable Long id, @RequestParam int quantity) {
    //     return productService.decreaseProductQuantity(id, quantity)
    //             .map(product -> ResponseEntity.ok(product))
    //             .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
    // }

    




}
