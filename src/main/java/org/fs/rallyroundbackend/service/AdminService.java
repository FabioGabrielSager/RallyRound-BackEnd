package org.fs.rallyroundbackend.service;

import org.fs.rallyroundbackend.dto.admin.AdminCompleteDataDto;
import org.fs.rallyroundbackend.dto.admin.AdminModificationRequest;
import org.fs.rallyroundbackend.dto.admin.AdminRegistrationRequest;
import org.fs.rallyroundbackend.dto.admin.AdminRegistrationResponse;
import org.fs.rallyroundbackend.dto.admin.AdminResume;
import org.fs.rallyroundbackend.dto.admin.UserPrivilegeCategoryDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AdminService {
    AdminRegistrationResponse registerAdmin(AdminRegistrationRequest request, String requesterAdminEmail);
    List<UserPrivilegeCategoryDto> getAdminsPrivileges();
    List<AdminResume> getAllAdminsResumes(String requesterAdminEmail, LocalDate registeredDateFrom,
                                          LocalDate registeredDateTo, String department, Boolean enabled,
                                          String name, String lastName);
    AdminCompleteDataDto getAdmin(UUID adminId, String requesterAdminEmail);
    AdminCompleteDataDto modifyAdmin(AdminModificationRequest request, String requesterAdminEmail);
    void deleteAdmin(UUID adminId, String requesterAdminEmail);
}
