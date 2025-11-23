package ai.lab.cair.service;

import ai.lab.cair.dto.base.PaginatedResponse;
import ai.lab.cair.dto.request.ProjectRequestDto;
import ai.lab.cair.dto.response.ProjectResponseDto;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectResponseDto createProject(ProjectRequestDto requestDto);

    ProjectResponseDto getProjectById(Long id);

    ProjectResponseDto getProjectBySlug(String slug);

    PaginatedResponse<ProjectResponseDto> getAllProjects(Pageable pageable);

    ProjectResponseDto updateProject(Long id, ProjectRequestDto requestDto);

    void deleteProject(Long id);
}

