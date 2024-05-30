package org.fs.rallyroundbackend.controller;

import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.service.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rr/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    private ResponseEntity<List<String>> getAllDepartments() {
        return ResponseEntity.ok(this.departmentService.getDepartments());
    }
}
