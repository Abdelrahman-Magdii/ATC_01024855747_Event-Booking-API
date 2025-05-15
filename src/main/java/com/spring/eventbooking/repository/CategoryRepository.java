package com.spring.eventbooking.repository;

import com.spring.eventbooking.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Set<Category> findAllByIdIn(List<Long> ids);

    Optional<Category> findByName(String name);

    Boolean existsByName(String name);

    List<Category> findAllByName(String name);
}
