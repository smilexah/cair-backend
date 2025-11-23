package ai.lab.cair.controller;

import ai.lab.cair.dto.base.PaginatedResponse;
import ai.lab.cair.dto.request.ProjectRequestDto;
import ai.lab.cair.dto.response.ProjectResponseDto;
import ai.lab.cair.service.ProjectService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "API для управления проектами")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "Создать проект",
            description = "Создание нового проекта (только для администраторов)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<ProjectResponseDto> createProject(
            @Valid @RequestBody ProjectRequestDto requestDto) {
        ProjectResponseDto response = projectService.createProject(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить проект по ID",
            description = "Получение информации о проекте по его ID")
    public ResponseEntity<ProjectResponseDto> getProjectById(
            @Parameter(description = "ID проекта") @PathVariable Long id) {
        ProjectResponseDto response = projectService.getProjectById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Получить проект по slug",
            description = "Получение информации о проекте по его slug")
    public ResponseEntity<ProjectResponseDto> getProjectBySlug(
            @Parameter(description = "Slug проекта") @PathVariable String slug) {
        ProjectResponseDto response = projectService.getProjectBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Получить все проекты",
            description = "Получение списка всех проектов с пагинацией")
    public ResponseEntity<PaginatedResponse<ProjectResponseDto>> getAllProjects(
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Поле для сортировки") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Направление сортировки (ASC или DESC)") @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        PaginatedResponse<ProjectResponseDto> response = projectService.getAllProjects(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить проект",
            description = "Обновление информации о проекте (только для администраторов)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<ProjectResponseDto> updateProject(
            @Parameter(description = "ID проекта") @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDto requestDto) {
        ProjectResponseDto response = projectService.updateProject(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить проект",
            description = "Удаление проекта (только для администраторов)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<Void> deleteProject(
            @Parameter(description = "ID проекта") @PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

