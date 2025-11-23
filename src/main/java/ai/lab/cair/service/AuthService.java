package ai.lab.cair.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ai.lab.cair.dto.request.AuthRequestDto;
import ai.lab.cair.dto.response.AuthResponseDto;
import org.apache.coyote.BadRequestException;

public interface AuthService {
    AuthResponseDto login(AuthRequestDto req, HttpServletResponse response) throws BadRequestException;

    AuthResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response);

    void logout(HttpServletRequest request, HttpServletResponse response);
}
