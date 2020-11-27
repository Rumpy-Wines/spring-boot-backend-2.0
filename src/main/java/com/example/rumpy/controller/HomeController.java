package com.example.rumpy.controller;

import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Add the controller.
@Controller
@NoArgsConstructor
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello there. What are you up to");
    }
}
