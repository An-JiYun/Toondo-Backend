package com.project.toondo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.toondo.dto.GoalRequest;
import com.project.toondo.entity.Goals;
import com.project.toondo.entity.Users;
import com.project.toondo.repository.UserRepository;
import com.project.toondo.service.GoalService;
import com.project.toondo.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(GoalController.class)
public class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoalService goalService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository; // 추가된 부분

    @Autowired
    private ObjectMapper objectMapper;

    private String token = "Bearer mockToken";

    @BeforeEach
    void setUp() {
        // Mock User 설정
        Users mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setLoginId("testUser");
        mockUser.setPassword("testPassword");
        mockUser.setNickname("Tester");

        // jwtService에서 항상 userId 1L을 반환하도록 설정
        when(jwtService.getUserId()).thenReturn(1L);

        // userRepository에서 userId 1L인 사용자 찾을 수 있도록 설정
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
    }

    @WithMockUser // 인증된 사용자로 설정
    @Test
    @DisplayName("목표 등록 성공")
    public void testCreateGoal() throws Exception {
        GoalRequest goalRequest = new GoalRequest("Study", LocalDate.now(), LocalDate.now().plusDays(7));

        mockMvc.perform(post("/goals/create")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goalRequest))
                        .with(csrf())) // CSRF 토큰 추가
                .andExpect(status().isCreated())
                .andExpect(content().string("목표가 등록되었습니다."));
    }

    @WithMockUser
    @Test
    @DisplayName("모든 목표 조회 성공")
    public void testGetAllGoals() throws Exception {
        Goals goal1 = new Goals(null, "Study", LocalDate.now(), LocalDate.now().plusDays(7));
        Goals goal2 = new Goals(null, "Exercise", LocalDate.now(), LocalDate.now().plusDays(14));

        when(goalService.getAllGoalsByUserId(anyLong())).thenReturn(Arrays.asList(goal1, goal2));

        mockMvc.perform(get("/goals/list")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].goalName").value("Study"))
                .andExpect(jsonPath("$[1].goalName").value("Exercise"));
    }

    @WithMockUser
    @Test
    @DisplayName("특정 목표 조회 성공")
    public void testGetGoalById() throws Exception {
        Goals goal = new Goals(null, "Study", LocalDate.now(), LocalDate.now().plusDays(7));
        when(goalService.getGoalByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(goal));

        mockMvc.perform(get("/goals/detail/1")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalName").value("Study"));
    }

    @WithMockUser
    @Test
    @DisplayName("목표 삭제 성공")
    public void testDeleteGoal() throws Exception {
        when(goalService.deleteGoal(anyLong(), anyLong())).thenReturn(true);

        mockMvc.perform(delete("/goals/delete/1")
                        .header("Authorization", token)
                        .with(csrf())) // CSRF 토큰 추가
                .andExpect(status().isOk())
                .andExpect(content().string("목표가 삭제되었습니다."));
    }

    @WithMockUser
    @Test
    @DisplayName("목표 수정 성공")
    public void testUpdateGoal() throws Exception {
        GoalRequest goalRequest = new GoalRequest("Updated Goal", LocalDate.now(), LocalDate.now().plusDays(7));
        when(goalService.updateGoal(anyLong(), anyLong(), any(GoalRequest.class))).thenReturn(true);

        mockMvc.perform(put("/goals/update/1")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goalRequest))
                        .with(csrf())) // CSRF 토큰 추가
                .andExpect(status().isOk())
                .andExpect(content().string("목표가 수정되었습니다."));
    }
}
