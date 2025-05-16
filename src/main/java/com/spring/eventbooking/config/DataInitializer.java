package com.spring.eventbooking.config;

import com.spring.eventbooking.entity.Category;
import com.spring.eventbooking.entity.Role;
import com.spring.eventbooking.repository.CategoryRepository;
import com.spring.eventbooking.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;

    public DataInitializer(RoleRepository roleRepository, CategoryRepository categoryRepository) {
        this.roleRepository = roleRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeCategories();
    }

    private void initializeRoles() {
        if (roleRepository.findByName("USER").isEmpty()) {
            Role userRole = new Role("USER");
            userRole.setDescription("Standard user with basic privileges");
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName("ADMIN").isEmpty()) {
            Role adminRole = new Role("ADMIN");
            adminRole.setDescription("Administrator with full system access");
            roleRepository.save(adminRole);
        }
    }

    private void initializeCategories() {
        List<Category> defaultCategories = new ArrayList<>();

        defaultCategories.add(createCategory("Conference", "Professional gatherings for knowledge sharing and networking"));
        defaultCategories.add(createCategory("Workshop", "Hands-on sessions focused on skill development"));
        defaultCategories.add(createCategory("Seminar", "Educational events with expert speakers on specific topics"));
        defaultCategories.add(createCategory("Concert", "Live music performances"));
        defaultCategories.add(createCategory("Exhibition", "Displays of art, products, or services"));
        defaultCategories.add(createCategory("Sports", "Athletic competitions and sporting events"));
        defaultCategories.add(createCategory("Festival", "Celebrations of culture, music, food, or arts"));
        defaultCategories.add(createCategory("Corporate", "Business-related events like team buildings or product launches"));
        defaultCategories.add(createCategory("Charity", "Fundraising and awareness events for causes"));
        defaultCategories.add(createCategory("Wedding", "Marriage celebrations and ceremonies"));

        for (Category category : defaultCategories) {
            if (categoryRepository.findByName(category.getName()).isEmpty()) {
                categoryRepository.save(category);
            }
        }
    }

    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }
}