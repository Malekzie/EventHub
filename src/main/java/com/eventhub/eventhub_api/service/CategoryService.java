package com.eventhub.eventhub_api.service;

import com.eventhub.eventhub_api.dto.CategoryDTO;
import com.eventhub.eventhub_api.dto.CreateCategoryDTO;
import com.eventhub.eventhub_api.exception.CategoryNotFoundException;
import com.eventhub.eventhub_api.model.Category;
import com.eventhub.eventhub_api.repository.CategoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Cacheable(value = "categories")
    public List<CategoryDTO> findAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Cacheable(value = "category", key = "#id")
    public CategoryDTO findCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return toDTO(category);
    }

    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDTO createCategory(CreateCategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        Category saved = categoryRepository.save(category);
        return toDTO(saved);
    }

    private CategoryDTO toDTO(Category category) {
        return new CategoryDTO(category.getId(), category.getName());
    }
}
