package com.eventhub.eventhub_api.controller;

import com.eventhub.eventhub_api.dto.CategoryDTO;
import com.eventhub.eventhub_api.dto.CreateCategoryDTO;
import com.eventhub.eventhub_api.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "List all categories", description = "Returns all available event categories")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Returns a single category by its ID")
    @ApiResponse(responseCode = "200", description = "Category found")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findCategoryById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new category", description = "Creates a new event category")
    @ApiResponse(responseCode = "201", description = "Category created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CreateCategoryDTO dto) {
        CategoryDTO created = categoryService.createCategory(dto);
        return ResponseEntity.status(201).body(created);
    }
}
