package org.fs.rallyroundbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.admin.AdminCompleteDataDto;
import org.fs.rallyroundbackend.dto.admin.AdminModificationRequest;
import org.fs.rallyroundbackend.dto.admin.AdminRegistrationRequest;
import org.fs.rallyroundbackend.dto.admin.AdminRegistrationResponse;
import org.fs.rallyroundbackend.dto.admin.AdminResume;
import org.fs.rallyroundbackend.dto.admin.UserPrivilegeCategoryDto;
import org.fs.rallyroundbackend.service.AdminService;
import org.fs.rallyroundbackend.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rr/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final JwtService jwtService;

    @PostMapping(value = "/register")
    public ResponseEntity<AdminRegistrationResponse> registerAdmin(@RequestBody @Validated
                                                                   AdminRegistrationRequest registrationRequest,
                                                                   HttpServletRequest request) {
        String adminEmail = this.jwtService.getUsernameFromToken(this.jwtService.getTokenFromRequest(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(this.adminService.registerAdmin(registrationRequest,
                adminEmail));
    }

    @PutMapping(value = "/modify")
    public ResponseEntity<AdminCompleteDataDto> modifyAdmin(@RequestBody @Validated
                                                                       AdminModificationRequest modificationRequest,
                                                                   HttpServletRequest request) {
        String adminEmail = this.jwtService.getUsernameFromToken(this.jwtService.getTokenFromRequest(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(this.adminService.modifyAdmin(modificationRequest,
                adminEmail));
    }

    @DeleteMapping("/disable/{adminId}")
    public ResponseEntity<Object> disableAdmin(@PathVariable UUID adminId, HttpServletRequest request) {
        String requesterAdminEmail = this.jwtService.getUsernameFromToken(this.jwtService.getTokenFromRequest(request));

        this.adminService.disableAdmin(adminId, requesterAdminEmail);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/enable/{adminId}")
    public ResponseEntity<Object> enableAdmin(@PathVariable UUID adminId, HttpServletRequest request) {
        String requesterAdminEmail = this.jwtService.getUsernameFromToken(this.jwtService.getTokenFromRequest(request));

        this.adminService.enableAdmin(adminId, requesterAdminEmail);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/privileges")
    public ResponseEntity<List<UserPrivilegeCategoryDto>> getAdminPrivileges() {
        return ResponseEntity.ok(this.adminService.getAdminsPrivileges());
    }

    @GetMapping("/find/")
    public ResponseEntity<List<AdminResume>> getAllAdmins(
            @RequestParam(required = false) LocalDate registeredDateFrom,
            @RequestParam(required = false) LocalDate registeredDateTo,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String lastName,
            HttpServletRequest request) {
        String adminEmail = this.jwtService.getUsernameFromToken(this.jwtService.getTokenFromRequest(request));
        return ResponseEntity.ok(this.adminService.getAllAdminsResumes(adminEmail, registeredDateFrom,
                registeredDateTo, department, enabled, name, lastName));
    }

    @GetMapping("/find/{adminId}")
    public ResponseEntity<AdminCompleteDataDto> getAdmin(@PathVariable UUID adminId, HttpServletRequest request) {
        String adminEmail = this.jwtService.getUsernameFromToken(this.jwtService.getTokenFromRequest(request));
        return ResponseEntity.ok(this.adminService.getAdmin(adminId, adminEmail));
    }
}
