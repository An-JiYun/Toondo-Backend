package com.project.toondo.service;

import com.project.toondo.dto.GoalRequest;
import com.project.toondo.entity.Goals;
import com.project.toondo.entity.Users;
import com.project.toondo.repository.GoalRepository;
import com.project.toondo.repository.UserRepository;
import com.project.toondo.service.GoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalService goalService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("목표 생성 테스트")
    public void testCreateGoal() {
        Users user = new Users();
        user.setUserId(1L);
        GoalRequest goalRequest = new GoalRequest("Study", LocalDate.now(), LocalDate.now().plusDays(7));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(goalRepository.save(any(Goals.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Goals createdGoal = goalService.createGoal(1L, goalRequest);

        assertThat(createdGoal.getGoalName()).isEqualTo("Study");
    }

    @Test
    @DisplayName("사용자가 없을 때 목표 생성 실패")
    public void testCreateGoal_UserNotFound() {
        GoalRequest goalRequest = new GoalRequest("Study", LocalDate.now(), LocalDate.now().plusDays(7));

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.createGoal(1L, goalRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("모든 목표 조회 테스트")
    public void testGetAllGoalsByUserId() {
        Goals goal1 = new Goals(null, "Study", LocalDate.now(), LocalDate.now().plusDays(7));
        Goals goal2 = new Goals(null, "Exercise", LocalDate.now(), LocalDate.now().plusDays(14));

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(goalRepository.findByUserId(anyLong())).thenReturn(Arrays.asList(goal1, goal2));

        List<Goals> goals = goalService.getAllGoalsByUserId(1L);

        assertThat(goals).hasSize(2);
        assertThat(goals.get(0).getGoalName()).isEqualTo("Study");
        assertThat(goals.get(1).getGoalName()).isEqualTo("Exercise");
    }

    @Test
    @DisplayName("사용자가 없을 때 목표 조회 실패")
    public void testGetAllGoalsByUserId_UserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> goalService.getAllGoalsByUserId(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("특정 목표 조회 테스트")
    public void testGetGoalByIdAndUserId() {
        Goals goal = new Goals(null, "Study", LocalDate.now(), LocalDate.now().plusDays(7));

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(goalRepository.findByGoalIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(goal));

        Optional<Goals> retrievedGoal = goalService.getGoalByIdAndUserId(1L, 1L);

        assertThat(retrievedGoal).isPresent();
        assertThat(retrievedGoal.get().getGoalName()).isEqualTo("Study");
    }

    @Test
    @DisplayName("사용자가 없을 때 목표 조회 실패")
    public void testGetGoalByIdAndUserId_UserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> goalService.getGoalByIdAndUserId(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("목표 삭제 테스트")
    public void testDeleteGoal() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(goalRepository.findByGoalIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(new Goals()));
        doNothing().when(goalRepository).delete(any(Goals.class));

        boolean isDeleted = goalService.deleteGoal(1L, 1L);

        assertThat(isDeleted).isTrue();
    }

    @Test
    @DisplayName("사용자가 없을 때 목표 삭제 실패")
    public void testDeleteGoal_UserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> goalService.deleteGoal(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("목표 수정 테스트")
    public void testUpdateGoal() {
        Goals goal = new Goals(null, "Old Goal", LocalDate.now(), LocalDate.now().plusDays(7));
        GoalRequest goalRequest = new GoalRequest("Updated Goal", LocalDate.now(), LocalDate.now().plusDays(10));

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(goalRepository.findByGoalIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goals.class))).thenReturn(goal);

        boolean isUpdated = goalService.updateGoal(1L, 1L, goalRequest);

        assertThat(isUpdated).isTrue();
        assertThat(goal.getGoalName()).isEqualTo("Updated Goal");
        assertThat(goal.getDeadline()).isEqualTo(LocalDate.now().plusDays(10));
    }

    @Test
    @DisplayName("사용자가 없을 때 목표 수정 실패")
    public void testUpdateGoal_UserNotFound() {
        GoalRequest goalRequest = new GoalRequest("Updated Goal", LocalDate.now(), LocalDate.now().plusDays(10));

        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> goalService.updateGoal(1L, 1L, goalRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");
    }
}
