package com.museum.demo.controller;



import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    // Test endpoint to verify if the controller is working
    @GetMapping("/test")
    public String test() {
        return "AuthController is working!";
    }
    @GetMapping("/")
    public String home() {
        return "AuthController is up and running!";
    }
}
