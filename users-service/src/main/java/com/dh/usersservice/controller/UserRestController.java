package com.dh.usersservice.controller;

import com.dh.usersservice.model.User;
import com.dh.usersservice.model.UserBillsDTO;
import com.dh.usersservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{username}")
    public List<User> findByUsername (@PathVariable String username){
        return userService.findByUsername(username);
    }

    @GetMapping("/id/{id}")
    public User findById (@PathVariable String id){
        return userService.findById(id);
    }

    @GetMapping("/bills/{id}")
    public ResponseEntity<UserBillsDTO> findByUserId (@PathVariable String id) {
        return ResponseEntity.ok().body(userService.findByUserId(id));
    }
}
