package com.spring.eventbooking.repository;

import com.spring.eventbooking.entity.Role;
import com.spring.eventbooking.entity.User;
import com.spring.eventbooking.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUser(User user);
    List<UserRole> findByRole(Role role);
    Optional<UserRole> findByUserAndRole(User user, Role role);
}
