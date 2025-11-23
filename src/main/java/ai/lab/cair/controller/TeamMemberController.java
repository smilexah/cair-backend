package ai.lab.cair.controller;

import ai.lab.cair.dto.base.PaginatedResponse;
import ai.lab.cair.dto.request.TeamMemberRequestDto;
import ai.lab.cair.dto.response.TeamMemberResponseDto;
import ai.lab.cair.service.TeamMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/team-members")
@RequiredArgsConstructor
@Tag(name = "Team Members", description = "API для управления членами команды")
public class TeamMemberController {
    private final TeamMemberService teamMemberService;

    @PostMapping
    @Operation(summary = "Создать члена команды",
            description = "Создание нового члена команды (только для администраторов)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<TeamMemberResponseDto> createTeamMember(
            @Valid @RequestBody TeamMemberRequestDto requestDto) {
        TeamMemberResponseDto response = teamMemberService.createTeamMember(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить члена команды по ID",
            description = "Получение информации о члене команды по его ID")
    public ResponseEntity<TeamMemberResponseDto> getTeamMemberById(
            @Parameter(description = "ID члена команды") @PathVariable Long id) {
        TeamMemberResponseDto response = teamMemberService.getTeamMemberById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Получить всех членов команды",
            description = "Получение списка всех членов команды с пагинацией")
    public ResponseEntity<PaginatedResponse<TeamMemberResponseDto>> getAllTeamMembers(
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Поле для сортировки") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Направление сортировки (ASC или DESC)") @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        PaginatedResponse<TeamMemberResponseDto> response = teamMemberService.getAllTeamMembers(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить члена команды",
            description = "Обновление информации о члене команды (только для администраторов)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<TeamMemberResponseDto> updateTeamMember(
            @Parameter(description = "ID члена команды") @PathVariable Long id,
            @Valid @RequestBody TeamMemberRequestDto requestDto) {
        TeamMemberResponseDto response = teamMemberService.updateTeamMember(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить члена команды",
            description = "Удаление члена команды (только для администраторов)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<Void> deleteTeamMember(
            @Parameter(description = "ID члена команды") @PathVariable Long id) {
        teamMemberService.deleteTeamMember(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

