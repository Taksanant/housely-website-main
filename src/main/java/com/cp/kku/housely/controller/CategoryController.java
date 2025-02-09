package com.cp.kku.housely.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cp.kku.housely.model.Category;
import com.cp.kku.housely.service.CategoryService;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories().collectList().block();
        model.addAttribute("categories", categories);
        return "category-list";
    }

    @GetMapping("/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "add-category-form";
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute("category") Category category) {
        categoryService.createCategory(category).block();
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditCategoryForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id).block();
        model.addAttribute("category", category);
        return "edit-category-form";
    }
    
    @PostMapping("/save/{id}")
    public String updateCategory(@ModelAttribute("category") Category category, @PathVariable Long id) {
    	category.setCategoryId(id);
        categoryService.createCategory(category).block();
        return "redirect:/admin/categories";
    }
    

@GetMapping("/delete/{id}")
public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
        categoryService.deleteCategory(id).block();
        redirectAttributes.addFlashAttribute("message", "Category deleted successfully.");
    } catch (DataIntegrityViolationException e) {
        redirectAttributes.addFlashAttribute("error", "ไม่สามารถลบหมวดหมู่ได้เนื่องจากมีข้อมูลที่เกี่ยวข้อง กรุณาลบข้อมูลที่เกี่ยวข้องก่อน");
    } catch (AccessDeniedException e) {
        redirectAttributes.addFlashAttribute("error", "คุณไม่มีสิทธิ์ในการลบหมวดหมู่นี้");
    } catch (IllegalStateException e) {
        redirectAttributes.addFlashAttribute("error", "ไม่สามารถลบหมวดหมู่หลักของระบบได้");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "เกิดข้อผิดพลาดในการลบหมวดหมู่เนื่องจากมีสินค้าในหมวดหมู่นี้");
    }
    return "redirect:/admin/categories";
}

}
