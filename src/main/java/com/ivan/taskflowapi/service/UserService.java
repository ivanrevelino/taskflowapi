package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.user.UserRequestDTO;
import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.mapper.UserMapper;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public Page<UserResponseDTO> listAll(Pageable pageable) {
        return repository.findAll(pageable).map(userMapper::toDTO);
    }

    @Transactional
    public UserResponseDTO save(@Valid UserRequestDTO requestDTO) {

        if (repository.findByUsername(requestDTO.username()) != null) {
            log.warn("REGISTRATION FAILED - username already exists: {}", requestDTO.username());
            throw new BadRequestException("User already exists");
        }

        String password = passwordEncoder.encode(requestDTO.password());
        User userToBeSaved = User.builder()
                .name(requestDTO.name())
                .username(requestDTO.username())
                .role(requestDTO.role())
                .password(password)
                .build();
        User saved = repository.save(userToBeSaved);

        log.info("CREATION SUCCESS - userId: {}, username: {}, role: {}",
                saved.getId(),
                saved.getUsername(),
                saved.getRole());

        return userMapper.toDTO(saved);
    }

    public UserResponseDTO findById(Long id) {
        if (id <= 0) throw new BadRequestException("Invalid value");
        User user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDTO(user);
    }

    public UserResponseDTO findByUserName(@NotBlank String username) {
        User user = (User) repository.findByUsername(username);

        if (user == null) throw new ResourceNotFoundException("User not found");

        return userMapper.toDTO(user);
    }

    @Transactional
    public void delete(Long id) {
        if (id <= 0) throw new BadRequestException("Invalid value");
        User userToBeDeleted = findByIdOrElseThrowResourceNotFoundException(id);
        repository.delete(userToBeDeleted);
    }

    public User findByIdOrElseThrowResourceNotFoundException(Long id) {
        if (id <= 0) throw new BadRequestException("Invalid value");
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
