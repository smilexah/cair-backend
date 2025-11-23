package ai.lab.cair.service.impl;

import ai.lab.cair.dto.base.PaginatedResponse;
import ai.lab.cair.dto.request.TeamMemberRequestDto;
import ai.lab.cair.dto.response.TeamMemberResponseDto;
import ai.lab.cair.entity.TeamMember;
import ai.lab.cair.entity.Translation;
import ai.lab.cair.exception.DbObjectNotFoundException;
import ai.lab.cair.mapper.TeamMemberMapper;
import ai.lab.cair.repository.TeamMemberRepository;
import ai.lab.cair.repository.TranslationRepository;
import ai.lab.cair.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final TranslationRepository translationRepository;
    private final TeamMemberMapper teamMemberMapper;
    private static final String ENTITY_TYPE = "TeamMember";

    @Override
    @Transactional
    public TeamMemberResponseDto createTeamMember(TeamMemberRequestDto requestDto) {
        TeamMember teamMember = teamMemberMapper.toEntity(requestDto);
        TeamMember savedTeamMember = teamMemberRepository.save(teamMember);

        // Save translations
        List<Translation> translations = teamMemberMapper.createTranslations(savedTeamMember.getId(), requestDto);
        translationRepository.saveAll(translations);

        return teamMemberMapper.toDto(savedTeamMember, translations);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamMemberResponseDto getTeamMemberById(Long id) {
        TeamMember teamMember = teamMemberRepository.findById(id)
                .orElseThrow(() -> new DbObjectNotFoundException(
                        HttpStatus.NOT_FOUND,
                        "TEAM_MEMBER_NOT_FOUND",
                        "Team member not found with id: " + id
                ));

        List<Translation> translations = translationRepository.findByEntityTypeAndEntityId(ENTITY_TYPE, id);
        return teamMemberMapper.toDto(teamMember, translations);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TeamMemberResponseDto> getAllTeamMembers(Pageable pageable) {
        Page<TeamMember> teamMembersPage = teamMemberRepository.findAll(pageable);

        Page<TeamMemberResponseDto> responsePage = teamMembersPage.map(teamMember -> {
            List<Translation> translations = translationRepository.findByEntityTypeAndEntityId(ENTITY_TYPE, teamMember.getId());
            return teamMemberMapper.toDto(teamMember, translations);
        });

        return new PaginatedResponse<>(responsePage);
    }

    @Override
    @Transactional
    public TeamMemberResponseDto updateTeamMember(Long id, TeamMemberRequestDto requestDto) {
        TeamMember teamMember = teamMemberRepository.findById(id)
                .orElseThrow(() -> new DbObjectNotFoundException(
                        HttpStatus.NOT_FOUND,
                        "TEAM_MEMBER_NOT_FOUND",
                        "Team member not found with id: " + id
                ));

        teamMemberMapper.updateEntity(teamMember, requestDto);
        TeamMember updatedTeamMember = teamMemberRepository.save(teamMember);

        translationRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id);
        List<Translation> translations = teamMemberMapper.createTranslations(id, requestDto);
        translationRepository.saveAll(translations);

        return teamMemberMapper.toDto(updatedTeamMember, translations);
    }

    @Override
    @Transactional
    public void deleteTeamMember(Long id) {
        if (!teamMemberRepository.existsById(id)) {
            throw new DbObjectNotFoundException(
                    HttpStatus.NOT_FOUND,
                    "TEAM_MEMBER_NOT_FOUND",
                    "Team member not found with id: " + id
            );
        }

        translationRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id);
        teamMemberRepository.deleteById(id);
    }
}
