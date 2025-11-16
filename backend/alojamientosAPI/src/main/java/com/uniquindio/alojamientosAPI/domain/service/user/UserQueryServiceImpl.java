package com.uniquindio.alojamientosAPI.domain.service.user;

import com.uniquindio.alojamientosAPI.domain.dto.user.UserDetailDTO;
import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserDetailDTO> findUserDetailById(Long id) {
        return userRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public Optional<UserDetailDTO> findUserDetailByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertToDTO);
    }

    private UserDetailDTO convertToDTO(UserEntity user) {
        return UserDetailDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dayOfBirth(user.getDayOfBirth())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .urlAccountPhoto(user.getUrlAccountPhoto())
                .homeAddress(user.getHomeAddress())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toList()))
                .build();
    }
}
