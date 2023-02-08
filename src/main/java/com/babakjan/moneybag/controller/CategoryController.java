package com.babakjan.moneybag.controller;

import com.babakjan.moneybag.dto.category.CategoryDto;
import com.babakjan.moneybag.dto.category.CreateCategoryRequest;
import com.babakjan.moneybag.exception.CategoryNotFoundException;
import com.babakjan.moneybag.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    //get all
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAll() {
        return CategoryService.categoriesToDtos(categoryService.getAll());
    }

    //get by id
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getById(@PathVariable Long id) throws CategoryNotFoundException {
        return categoryService.getById(id).dto();
    }

    //create
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody CreateCategoryRequest request) {
        return categoryService.save(request).dto();
    }

    //update
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@PathVariable Long id, @RequestBody CategoryDto request)
            throws CategoryNotFoundException {
        return categoryService.update(id, request).dto();
    }

    //delete
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) throws CategoryNotFoundException {
        categoryService.delete(id);
    }
}
