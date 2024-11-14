package com.project.toondo.repository;

import com.project.toondo.entity.Goals;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goals, Long> {
    List<Goals> findByUserId(Long userId);
    Optional<Goals> findByGoalIdAndUserId(Long goalId, Long userId);
}

