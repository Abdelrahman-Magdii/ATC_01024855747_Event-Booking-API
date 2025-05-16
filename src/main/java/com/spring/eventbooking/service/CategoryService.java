package com.spring.eventbooking.service;

import com.spring.eventbooking.dto.Response.CategoryResponse;
import com.spring.eventbooking.entity.Category;
import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.exception.GlobalException;
import com.spring.eventbooking.repository.CategoryRepository;
import com.spring.eventbooking.utiles.GlobalFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = findCategoryById(id);
        return mapToResponse(category);
    }

    public Set<Event> getCategoryEvents(Long id) {
        Category category = findCategoryById(id);
        return category.getEvents();
    }

    @Transactional
    public CategoryResponse createCategory(CategoryResponse categoryResponse) {
        if (categoryRepository.existsByName(categoryResponse.getName())) {
            throw new GlobalException(GlobalFunction.getMS("category.already.exist", categoryResponse.getName()), HttpStatus.BAD_REQUEST);
        }

        Category category = new Category();
        category.setName(categoryResponse.getName());
        category.setDescription(categoryResponse.getDescription());

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryResponse categoryResponse) {
        Category category = findCategoryById(id);

        if (!category.getName().equals(categoryResponse.getName()) &&
                categoryRepository.existsByName(categoryResponse.getName())) {
            throw new GlobalException(GlobalFunction.getMS("category.already.exist", categoryResponse.getName()), HttpStatus.BAD_REQUEST);
        }

        category.setName(categoryResponse.getName());
        category.setDescription(categoryResponse.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = findCategoryById(id);
        if (!category.getEvents().isEmpty()) {
            category.getEvents().forEach(event -> event.getCategories().remove(category));
            throw new GlobalException("category.not.delete", HttpStatus.BAD_REQUEST);
        }
        categoryRepository.delete(category);
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new GlobalException(GlobalFunction.getMS("category.not.found.id", id), HttpStatus.NOT_FOUND));
    }

    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        return response;
    }
}

