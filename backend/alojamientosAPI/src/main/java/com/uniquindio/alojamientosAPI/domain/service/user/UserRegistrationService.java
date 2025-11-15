package com.uniquindio.alojamientosAPI.domain.service.user;

import com.uniquindio.alojamientosAPI.domain.dto.user.RegisterUserCommand;
import com.uniquindio.alojamientosAPI.domain.dto.user.RegisteredUserResult;

public interface UserRegistrationService {
    RegisteredUserResult register(RegisterUserCommand command);
}

