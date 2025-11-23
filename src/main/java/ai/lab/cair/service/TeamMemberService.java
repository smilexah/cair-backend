package ai.lab.cair.service;

import ai.lab.cair.dto.base.PaginatedResponse;
import ai.lab.cair.dto.request.TeamMemberRequestDto;
import ai.lab.cair.dto.response.TeamMemberResponseDto;
import org.springframework.data.domain.Pageable;

public interface TeamMemberService {
    TeamMemberResponseDto createTeamMember(TeamMemberRequestDto requestDto);

    TeamMemberResponseDto getTeamMemberById(Long id);

    PaginatedResponse<TeamMemberResponseDto> getAllTeamMembers(Pageable pageable);

    TeamMemberResponseDto updateTeamMember(Long id, TeamMemberRequestDto requestDto);

    void deleteTeamMember(Long id);
}

