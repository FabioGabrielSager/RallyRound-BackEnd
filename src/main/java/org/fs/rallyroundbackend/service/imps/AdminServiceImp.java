package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.admin.AdminCompleteDataDto;
import org.fs.rallyroundbackend.dto.admin.AdminModificationRequest;
import org.fs.rallyroundbackend.dto.admin.AdminRegistrationRequest;
import org.fs.rallyroundbackend.dto.admin.AdminRegistrationResponse;
import org.fs.rallyroundbackend.dto.admin.AdminResume;
import org.fs.rallyroundbackend.dto.admin.UserPrivilegeCategoryDto;
import org.fs.rallyroundbackend.dto.admin.UserPrivilegeDto;
import org.fs.rallyroundbackend.entity.users.AdminEntity;
import org.fs.rallyroundbackend.entity.users.DepartmentEntity;
import org.fs.rallyroundbackend.entity.users.PrivilegeCategoryEntity;
import org.fs.rallyroundbackend.entity.users.PrivilegeEntity;
import org.fs.rallyroundbackend.entity.users.RoleEntity;
import org.fs.rallyroundbackend.repository.user.RoleRepository;
import org.fs.rallyroundbackend.repository.user.UserRepository;
import org.fs.rallyroundbackend.repository.user.admin.AdminRepository;
import org.fs.rallyroundbackend.repository.user.admin.DepartmentRepository;
import org.fs.rallyroundbackend.repository.user.admin.PrivilegeCategoryRepository;
import org.fs.rallyroundbackend.repository.user.admin.PrivilegeRepository;
import org.fs.rallyroundbackend.service.AdminActivityLoggerService;
import org.fs.rallyroundbackend.service.AdminService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImp implements AdminService {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final PrivilegeRepository privilegeRepository;
    private final PrivilegeCategoryRepository privilegeCategoryRepository;
    private final AdminActivityLoggerService adminActivityLoggerService;
    private ModelMapper modelMapper;

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public AdminRegistrationResponse registerAdmin(AdminRegistrationRequest request, String requesterAdminEmail) {

        if (userRepository.existsByEmailAndEnabled(request.getEmail(), true)) {
            throw new EntityExistsException("There is already an account registered with that email.");
        }

        AdminEntity newAdmin = this.modelMapper.map(request, AdminEntity.class);

        RoleEntity role = this.roleRepository.findByName("ROLE_ADMIN").orElseThrow(
                () -> new EntityNotFoundException("Role not found.")
        );

        newAdmin.setRegistrationDate(LocalDateTime.now());
        newAdmin.setPassword(this.passwordEncoder.encode(request.getPassword()));
        newAdmin.setRoles(Set.of(role));
        newAdmin.setPrivileges(this.fetchPrivilegesFromRequest(request.getPrivileges()));

        DepartmentEntity departmentEntity =
                this.departmentRepository.findByName(request.getDepartment())
                        .orElseThrow(() -> new EntityNotFoundException("Department not found."));

        newAdmin.setDepartment(departmentEntity);

        newAdmin = this.userRepository.save(newAdmin);

        this.adminActivityLoggerService.saveLog("REGISTER_ADMIN",
                "Register a new admin with email " + newAdmin.getEmail(),
                newAdmin.getId().toString(), UUID.class.getTypeName(), requesterAdminEmail);

        return AdminRegistrationResponse.builder()
                .id(newAdmin.getId())
                .userRoles(newAdmin.getRoles().stream().map(RoleEntity::getName).toList())
                .privileges(request.getPrivileges())
                .registrationDate(newAdmin.getRegistrationDate())
                .build();
    }

    @Override
    public List<UserPrivilegeCategoryDto> getAdminsPrivileges() {
        return List.of(this.modelMapper.map(this.privilegeCategoryRepository.findAll(),
                UserPrivilegeCategoryDto[].class));
    }

    @Override
    public List<AdminResume> getAllAdminsResumes(String requesterAdminEmail, LocalDate registeredDateFrom,
                                                 LocalDate registeredDateTo, String department, Boolean enabled,
                                                 String name, String lastName) {
        this.adminActivityLoggerService.saveLog("GET_ADMINS", "Get all registered admins",
                "", "", requesterAdminEmail);

        List<AdminEntity> adminEntities = this.adminRepository.findAll(name, lastName, department,
                registeredDateFrom, registeredDateTo, enabled);

        List<AdminResume> adminResumes = new ArrayList<>();

        adminEntities.forEach(adminEntity -> {
            AdminResume adminResume = this.modelMapper.map(adminEntity, AdminResume.class);
            adminResume.setRequesterAccount(adminEntity.getEmail().equals(requesterAdminEmail));
            adminResumes.add(adminResume);
        });

        return adminResumes;
    }

    @Override
    @Transactional
    public AdminCompleteDataDto getAdmin(UUID adminId, String requesterAdminEmail) {
        this.adminActivityLoggerService.saveLog("GET_ADMIN", "Get admin data by id: " + adminId,
                "", "", requesterAdminEmail);

        AdminEntity adminEntity = this.adminRepository.findById(adminId).orElseThrow(
                () -> new EntityNotFoundException("Admin with id " + adminId + " not found"));

        AdminCompleteDataDto result = this.modelMapper.map(adminEntity, AdminCompleteDataDto.class);

        result.setPrivileges(this.mapPrivilegesByCategory(adminEntity));
        result.setRequesterAccount(adminEntity.getEmail().equals(requesterAdminEmail));

        return result;
    }

    @Override
    @Transactional
    public AdminCompleteDataDto modifyAdmin(AdminModificationRequest request, String requesterAdminEmail) {
        AdminEntity adminToModify = this.adminRepository.findById(request.getAdminId()).orElseThrow(
                () -> new EntityNotFoundException("Admin with id " + request.getAdminId() + " not found.")
        );

        if(request.getName() != null && !request.getName().isBlank()) {
            adminToModify.setName(request.getName());
        }

        if(request.getLastName() != null && !request.getLastName().isBlank()) {
            adminToModify.setLastName(request.getLastName());
        }

        if(request.getEmail() != null && !request.getEmail().isBlank()) {
            adminToModify.setEmail(request.getEmail());
        }

        if(request.getBirthdate() != null) {
            adminToModify.setBirthdate(request.getBirthdate());
        }

        if(request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            adminToModify.setPhoneNumber(request.getPhoneNumber());
        }

        if(request.getDepartment() != null && !request.getDepartment().isBlank()) {
            Optional<DepartmentEntity> departmentEntityOptional =
                    this.departmentRepository.findByName(request.getDepartment());

            if(departmentEntityOptional.isEmpty()) {
                adminToModify.setDepartment(DepartmentEntity.builder().name(request.getDepartment()).build());
            } else {
                adminToModify.setDepartment(departmentEntityOptional.get());
            }
        }

        if(request.getPrivileges() != null) {
            adminToModify.setPrivileges(this.fetchPrivilegesFromRequest(request.getPrivileges()));
        }

        AdminEntity modifiedAdmin = this.adminRepository.save(adminToModify);

        this.adminActivityLoggerService.saveLog(
                "MODIFY_ADMIN", "Modify admin with",
                modifiedAdmin.getId().toString(), UUID.class.getTypeName(), requesterAdminEmail);

        AdminCompleteDataDto result = this.modelMapper.map(modifiedAdmin, AdminCompleteDataDto.class);

        result.setPrivileges(this.mapPrivilegesByCategory(modifiedAdmin));

        return result;
    }

    @Override
    @Transactional
    public void disableAdmin(UUID adminId, String requesterAdminEmail) {
        AdminEntity adminToDelete = this.adminRepository.findById(adminId).orElseThrow(
                () -> new EntityNotFoundException("Admin with id " + adminId + " not found.")
        );

        adminToDelete.setEnabled(false);

        this.adminActivityLoggerService.saveLog("DISABLE_ADMIN", "Disable admin by id",
                adminId.toString(), UUID.class.getTypeName(), requesterAdminEmail);

        this.adminRepository.save(adminToDelete);
    }

    @Override
    public void enableAdmin(UUID adminId, String requesterAdminEmail) {
        AdminEntity adminToDelete = this.adminRepository.findById(adminId).orElseThrow(
                () -> new EntityNotFoundException("Admin with id " + adminId + " not found.")
        );

        adminToDelete.setEnabled(true);

        this.adminActivityLoggerService.saveLog("ENABLE_ADMIN", "Enable admin by id",
                adminId.toString(), UUID.class.getTypeName(), requesterAdminEmail);

        this.adminRepository.save(adminToDelete);
    }

    private List<UserPrivilegeCategoryDto> mapPrivilegesByCategory(AdminEntity adminEntity) {
        Map<Short, List<UserPrivilegeDto>> privilegeCategoryDtoMap = new HashMap<>();
        Set<PrivilegeCategoryEntity> privilegeCategoryEntitySet = new HashSet<>();

        for (PrivilegeEntity privilegeEntity : adminEntity.getPrivileges()) {
            UserPrivilegeDto privilegeDto = this.modelMapper.map(privilegeEntity, UserPrivilegeDto.class);
            if(!privilegeCategoryDtoMap.containsKey(privilegeEntity.getCategory().getId())) {
                privilegeCategoryEntitySet.add(privilegeEntity.getCategory());
                privilegeCategoryDtoMap.put(privilegeEntity.getCategory().getId(), new ArrayList<>());
            }

            privilegeCategoryDtoMap.get(privilegeEntity.getCategory().getId()).add(privilegeDto);
        }

        List<UserPrivilegeCategoryDto> mappedPrivileges = new ArrayList<>();
        for (Short privilegeCategoryId : privilegeCategoryDtoMap.keySet()) {
            PrivilegeCategoryEntity privilegeCategoryEntity = privilegeCategoryEntitySet.stream()
                    .filter(pc -> pc.getId() == privilegeCategoryId).findFirst().get();

            UserPrivilegeCategoryDto privilegeCategory = new
                    UserPrivilegeCategoryDto(privilegeCategoryEntity.getId(), privilegeCategoryEntity.getName(),
                    privilegeCategoryDtoMap.get(privilegeCategoryId));

            mappedPrivileges.add(privilegeCategory);
        }

        return mappedPrivileges;
    }


    private List<PrivilegeEntity> fetchPrivilegesFromRequest(List<UserPrivilegeCategoryDto> request) {
        List<PrivilegeEntity> privilegeEntities = new ArrayList<>();
        for (UserPrivilegeCategoryDto pc : request) {
            for (UserPrivilegeDto p : pc.getPrivileges()) {
                PrivilegeEntity privilegeEntity = privilegeRepository.findByName(p.getName()).orElseThrow(
                        () -> new EntityNotFoundException("User privilege " + p.getName() + " not found.")
                );
                privilegeEntities.add(privilegeEntity);
            }
        }

        return privilegeEntities;
    }
}
