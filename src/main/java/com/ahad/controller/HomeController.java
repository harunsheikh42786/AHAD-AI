package com.ahad.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ahad.entities.User;
import com.ahad.helper.Message;
import com.ahad.repositories.UserRepository;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @ModelAttribute
    public void getUser(Model model) {
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("message", new Message("", ""));
        return "login";
    }

    // @PostMapping("/login-processing")
    // public String loginProcessing(@RequestParam("email") String email,
    // @RequestParam("password") String password,
    // Model model) {
    // System.out.println("Email: " + email + " Password: " + password);

    // User user = this.userRepository.findUserByEmail(email);
    // if (user == null) {
    // model.addAttribute("message", new Message("User not found with this email.",
    // "danger"));
    // return "login";
    // }
    // if (user.getPassword().equals(password)) {
    // model.addAttribute("message", new Message("Welcome to Deen Activities",
    // "primary"));
    // return "user/profile";
    // }
    // model.addAttribute("message",
    // new Message("Password is incorrect. Try again with correct password.",
    // "danger"));
    // return "login";
    // }

    // Registration User Page
    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("message", new Message("", ""));
        return "registration";
    }

    // Registration User Processing
    @PostMapping("/registration-processing")
    public String registrationProcessing(@ModelAttribute User user, Model model) {

        if (this.userRepository.findUserByUsername(user.getUsername()) != null) {
            model.addAttribute("message", new Message("This username is already used.", "danger"));
            return "registration";
        }

        User existUser = this.userRepository.findUserByEmail(user.getEmail());

        if (existUser != null) {
            model.addAttribute("message", new Message("This email is already registered.", "danger"));
            return "registration";
        } else {
            System.out.println(user);
            model.addAttribute("message", new Message("Successfully registered.", "success"));
            user.setId(UUID.randomUUID().toString());
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            this.userRepository.save(user);
            return "registration";
        }
    }

}
