package com.uniquindio.alojamientosAPI.domain.service.user;

import com.uniquindio.alojamientosAPI.domain.dto.user.UserDetailDTO;
import java.util.Optional;

public interface UserQueryService {
    Optional<UserDetailDTO> findUserDetailById(Long id);
    Optional<UserDetailDTO> findUserDetailByEmail(String email);
}
