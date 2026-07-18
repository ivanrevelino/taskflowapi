package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.user.UserRequestDTO;
import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.mapper.UserMapper;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.UserRoles;
import com.ivan.taskflowapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;

    public Page<UserResponseDTO> listAll(Pageable pageable) {
        return repository.findAll(pageable).map(userMapper::toDTO);
    }

    @Transactional
    public UserResponseDTO save(@Valid  UserRequestDTO requestDTO) {

        User entity = userMapper.toEntity(requestDTO);
        entity.setRole(UserRoles.USER);
        User saved = repository.save(entity);
        return userMapper.toDTO(saved);
    }

    public UserResponseDTO findById(Long id) {
        if (id <= 0) throw new BadRequestException("Invalid value");
        User user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDTO(user);
    }

    public void delete(Long id) {
        if (id <= 0) throw new BadRequestException("Invalid value");
        User userToBeDeleted = findByIdOrElseThrowResourceNotFoundException(id);
        repository.delete(userToBeDeleted);
    }

    public User findByIdOrElseThrowResourceNotFoundException(Long id) {
        if (id <= 0) throw new BadRequestException("Invalid value");
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
