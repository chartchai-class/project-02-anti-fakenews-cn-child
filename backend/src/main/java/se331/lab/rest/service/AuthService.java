package se331.lab.rest.service;

import se331.lab.rest.util.AuthResponseDto;
import se331.lab.rest.util.LoginDto;
import se331.lab.rest.util.RegisterDto;

public interface AuthService {
    AuthResponseDto login(LoginDto loginDto);

    String register(RegisterDto registerDto);
}
