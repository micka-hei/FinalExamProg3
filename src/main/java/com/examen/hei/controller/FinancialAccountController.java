package com.examen.hei.controller;

import com.examen.hei.model.FinancialAccount;
import com.examen.hei.repository.DatabaseSimulator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/financial-accounts")
public class FinancialAccountController {

    private final DatabaseSimulator db;

    public FinancialAccountController(DatabaseSimulator db) {
        this.db = db;
    }

    @GetMapping
    public ResponseEntity<Collection<FinancialAccount>> getAllAccounts() {
        return ResponseEntity.ok(db.findAllFinancialAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialAccount> getAccountById(@PathVariable String id) {
        return db.findFinancialAccountById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}