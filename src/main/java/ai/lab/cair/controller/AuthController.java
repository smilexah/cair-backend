package ai.lab.cair.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import ai.lab.cair.dto.request.AuthRequestDto;
import ai.lab.cair.dto.response.AuthResponseDto;
import ai.lab.cair.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для аутентификации пользователей")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Вход в систему",
            description = "Аутентификация пользователя по логину и паролю")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto requestDto, HttpServletResponse response) throws Exception {
        return ResponseEntity.ok(authService.login(requestDto, response));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Обновить токен",
            description = "Обновление JWT токена",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<AuthResponseDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.refreshToken(request, response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход из системы",
            description = "Деактивация текущего JWT токена",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
