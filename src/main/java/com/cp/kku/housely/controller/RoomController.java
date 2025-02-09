package com.cp.kku.housely.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cp.kku.housely.model.Room;
import com.cp.kku.housely.service.RoomService;

import java.util.List;

@Controller
@RequestMapping("/admin/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public String listRooms(Model model) {
        List<Room> rooms = roomService.getAllRooms().collectList().block();
        model.addAttribute("rooms", rooms);
        return "room-list";
    }

    @GetMapping("/add")
    public String showAddRoomForm(Model model) {
        model.addAttribute("room", new Room());
        return "add-room-form";
    }

    @PostMapping("/save")
    public String saveRoom(@ModelAttribute("room") Room room) {
        System.out.println("here");
        roomService.createRoom(room).block();
        return "redirect:/admin/rooms";
    }

    @GetMapping("/edit/{id}")
    public String showEditRoomForm(@PathVariable Long id, Model model) {
        Room room = roomService.getRoomById(id).block();
        model.addAttribute("room", room);
        return "edit-room-form";
    }
    
    @PostMapping("/save/{id}")
    public String saveRoom(@ModelAttribute("room") Room room, @PathVariable Long id) {
    	room.setId(id);
        roomService.createRoom(room).block();
        return "redirect:/admin/rooms";
    }

    @GetMapping("/delete/{id}")
public String deleteRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
        roomService.deleteRoom(id).block();
        redirectAttributes.addFlashAttribute("message", "Room deleted successfully.");
    } catch (DataIntegrityViolationException e) {
        redirectAttributes.addFlashAttribute("error", "ไม่สามารถลบห้องได้เนื่องจากมีข้อมูลที่เกี่ยวข้อง กรุณาลบข้อมูลที่เกี่ยวข้องก่อน");
    } catch (AccessDeniedException e) {
        redirectAttributes.addFlashAttribute("error", "คุณไม่มีสิทธิ์ในการลบห้องนี้");
    } catch (IllegalStateException e) {
        redirectAttributes.addFlashAttribute("error", "ไม่สามารถลบห้องหลักของระบบได้");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "เกิดข้อผิดพลาดในการลบห้องเนื่องจากมีสินค้าในห้องนี้");
    }
    return "redirect:/admin/rooms";
}
}
