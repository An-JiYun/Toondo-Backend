package com.project.toondo.repository;

import com.project.toondo.entity.Goals;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goals, Long> {

    // 유저의 ID를 기반으로 Goals 리스트를 가져오는 메서드
    List<Goals> findByUserUserId(Long userId);

    // Goal ID와 유저 ID를 기반으로 특정 Goal을 가져오는 메서드
    Optional<Goals> findByGoalIdAndUserUserId(Long goalId, Long userId);
}
