package com.ahad.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ahad.entities.User;
import com.ahad.helper.Message;
import com.ahad.services.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void userAttributes(Model model) {
        model.addAttribute("message", new Message("", ""));
    }

    // User Profile
    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        model.addAttribute("message", new Message("", ""));
        User user = userService.getLoginUser(principal);
        model.addAttribute("user", user);
        return "user/profile";
    }

    // Update User
    @PostMapping("/update-user")
    public String updateData(@ModelAttribute User user, Model model, Principal principal) {
        User updatedUser = userService.updateUser(user, principal, user.getPassword());
        if (updatedUser != null) {
            model.addAttribute("message", new Message("Profile updated successfully.", "success"));
        } else {
            model.addAttribute("message",
                    new Message("Password is incorrect. Try again with correct password.", "danger"));
        }
        return "user/profile";
    }

}
