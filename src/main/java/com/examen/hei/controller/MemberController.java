package com.examen.hei.controller;

import com.examen.hei.model.CreateMember;
import com.examen.hei.model.Member;
import com.examen.hei.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<List<Member>> createMembers(@Valid @RequestBody List<CreateMember> requests) {
        List<Member> created = memberService.createMembers(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}