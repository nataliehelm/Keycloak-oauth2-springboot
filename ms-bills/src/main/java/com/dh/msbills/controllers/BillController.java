package com.dh.msbills.controllers;

import com.dh.msbills.models.Bill;
import com.dh.msbills.services.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService service;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<Bill>> getAll() {
        return ResponseEntity.ok().body(service.getAllBill());
    }


    @PostMapping("/save")
    @PreAuthorize("hasAuthority('GROUP_/PROVIDERS')")
    public ResponseEntity<Bill> save (@RequestBody Bill bill){
        return ResponseEntity.ok().body(service.save(bill));
    }

    @GetMapping("/find/{userId}")
    public ResponseEntity<List<Bill>> findAll (@PathVariable String userId) {
        return ResponseEntity.ok().body(service.findByUserId(userId));
    }

}
