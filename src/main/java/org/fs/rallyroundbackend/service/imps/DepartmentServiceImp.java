package org.fs.rallyroundbackend.service.imps;

import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.repository.user.admin.DepartmentRepository;
import org.fs.rallyroundbackend.service.DepartmentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImp implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<String> getDepartments() {
        return List.of(this.modelMapper.map(departmentRepository.findAll(),
                String[].class));
    }
}
