package com.spring.eventbooking.repository;

import com.spring.eventbooking.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Set<Tag> findAllByIdIn(List<Long> ids);

    Optional<Tag> findByName(String name);

    Boolean existsByName(String name);

    List<Tag> findAllByName(String name);
}