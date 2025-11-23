package ai.lab.cair.service.impl;

import ai.lab.cair.dto.base.PaginatedResponse;
import ai.lab.cair.dto.request.ProjectRequestDto;
import ai.lab.cair.dto.response.ProjectResponseDto;
import ai.lab.cair.entity.Project;
import ai.lab.cair.entity.Translation;
import ai.lab.cair.exception.DbObjectNotFoundException;
import ai.lab.cair.mapper.ProjectMapper;
import ai.lab.cair.repository.ProjectRepository;
import ai.lab.cair.repository.TranslationRepository;
import ai.lab.cair.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final TranslationRepository translationRepository;
    private final ProjectMapper projectMapper;
    private static final String ENTITY_TYPE = "Project";

    @Override
    @Transactional
    public ProjectResponseDto createProject(ProjectRequestDto requestDto) {
        // Check if slug already exists
        if (projectRepository.existsBySlug(requestDto.getSlug())) {
            throw new IllegalArgumentException("Project with slug '" + requestDto.getSlug() + "' already exists");
        }

        Project project = projectMapper.toEntity(requestDto);
        Project savedProject = projectRepository.save(project);

        // Save translations
        List<Translation> translations = projectMapper.createTranslations(savedProject.getId(), requestDto);
        translationRepository.saveAll(translations);

        return projectMapper.toDto(savedProject, translations);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new DbObjectNotFoundException(
                        HttpStatus.NOT_FOUND,
                        "PROJECT_NOT_FOUND",
                        "Project not found with id: " + id
                ));

        List<Translation> translations = translationRepository.findByEntityTypeAndEntityId(ENTITY_TYPE, id);
        return projectMapper.toDto(project, translations);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectBySlug(String slug) {
        Project project = projectRepository.findBySlug(slug)
                .orElseThrow(() -> new DbObjectNotFoundException(
                        HttpStatus.NOT_FOUND,
                        "PROJECT_NOT_FOUND",
                        "Project not found with slug: " + slug
                ));

        List<Translation> translations = translationRepository.findByEntityTypeAndEntityId(ENTITY_TYPE, project.getId());
        return projectMapper.toDto(project, translations);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ProjectResponseDto> getAllProjects(Pageable pageable) {
        Page<Project> projectsPage = projectRepository.findAll(pageable);

        Page<ProjectResponseDto> responsePage = projectsPage.map(project -> {
            List<Translation> translations = translationRepository.findByEntityTypeAndEntityId(ENTITY_TYPE, project.getId());
            return projectMapper.toDto(project, translations);
        });

        return new PaginatedResponse<>(responsePage);
    }

    @Override
    @Transactional
    public ProjectResponseDto updateProject(Long id, ProjectRequestDto requestDto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new DbObjectNotFoundException(
                        HttpStatus.NOT_FOUND,
                        "PROJECT_NOT_FOUND",
                        "Project not found with id: " + id
                ));

        // Check if slug is being changed and if new slug already exists
        if (!project.getSlug().equals(requestDto.getSlug()) && projectRepository.existsBySlug(requestDto.getSlug())) {
            throw new IllegalArgumentException("Project with slug '" + requestDto.getSlug() + "' already exists");
        }

        projectMapper.updateEntity(project, requestDto);
        Project updatedProject = projectRepository.save(project);

        // Update translations
        translationRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id);
        List<Translation> translations = projectMapper.createTranslations(id, requestDto);
        translationRepository.saveAll(translations);

        return projectMapper.toDto(updatedProject, translations);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new DbObjectNotFoundException(
                    HttpStatus.NOT_FOUND,
                    "PROJECT_NOT_FOUND",
                    "Project not found with id: " + id
            );
        }

        translationRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id);
        projectRepository.deleteById(id);
    }
}

