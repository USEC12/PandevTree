package com.example.TelegramBotForPandev.service;

import com.example.TelegramBotForPandev.model.Category;
import com.example.TelegramBotForPandev.repository.CategoryRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;


    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category createCategory(String name, String parentName) throws Exception {
        Optional<Category> existingCategory = categoryRepository.findByName(name);
        if (existingCategory.isPresent()) {
            throw new Exception("Категория с таким именем уже существует.");
        }

        Category category = new Category(name);

        if (parentName != null) {
            Optional<Category> parentCategory = categoryRepository.findByName(parentName);
            if (parentCategory.isPresent()) {
                category.setParent(parentCategory.get());
            } else {
                throw new Exception("Родительская категория не найдена.");
            }
        }

        return categoryRepository.save(category);
    }

    // Получение всех корневых категорий
    @Transactional(readOnly = true)
    public List<Category> getRootCategories() {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getParent() == null)
                .toList();
    }

    // Поиск категории по имени
    @Transactional(readOnly = true)
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    // Удаление категории
    public void deleteAllCategories() {
        categoryRepository.deleteAll();
    }
    @Transactional
    public void deleteCategory(String name) throws Exception {
        Optional<Category> category = categoryRepository.findByName(name);
        if (category.isPresent()) {
            categoryRepository.delete(category.get());
        } else {
            throw new Exception("Категория не найдена.");
        }
    }
}