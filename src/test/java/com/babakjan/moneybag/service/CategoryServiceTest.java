package com.babakjan.moneybag.service;

import com.babakjan.moneybag.dto.category.CategoryAnalyticDto;
import com.babakjan.moneybag.dto.category.CategoryDto;
import com.babakjan.moneybag.dto.category.CreateCategoryRequest;
import com.babakjan.moneybag.entity.Category;
import com.babakjan.moneybag.entity.CategoryAnalytic;
import com.babakjan.moneybag.entity.Record;
import com.babakjan.moneybag.entity.User;
import com.babakjan.moneybag.error.exception.CategoryNotFoundException;
import com.babakjan.moneybag.error.exception.UserNotFoundException;
import com.babakjan.moneybag.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private AuthenticationService authenticationService;

    @Test
    void getAll() {
        // when
        categoryService.getAll();

        // then
        verify(categoryRepository).findAll();
    }

    @Test
    void getById() throws CategoryNotFoundException {
        // given
        Long categoryId = 34L;
        Category category = Category.builder()
                .name("Groceries")
                .id(categoryId)
                .build();
        given(categoryRepository.findById(categoryId))
                .willReturn(Optional.of(category));

        // when
        Category result = categoryService.getById(categoryId);

        // then
        ArgumentCaptor<Long> idCapture = ArgumentCaptor.forClass(Long.class);
        verify(categoryRepository).findById(idCapture.capture());
        assertThat(idCapture.getValue()).isEqualTo(categoryId);
        assertThat(result).isEqualTo(category);
    }

    @Test
    void save() {
        // given
        Category category = Category.builder()
                .name("Groceries")
                .color("#fff")
                .icon("mdi-shape-outline")
                .records(new ArrayList<>())
                .build();

        CreateCategoryRequest createRequest = new CreateCategoryRequest(
                "Groceries",
                "mdi-shape-outline",
                "#fff"
        );
        given(categoryRepository.save(category))
                .willReturn(category);

        // when
        categoryService.save(createRequest);

        // then
        ArgumentCaptor<Category> categoryCapture = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryCapture.capture());
        assertThat(categoryCapture.getValue()).isEqualTo(category);
    }

    @Test
    void delete() throws CategoryNotFoundException {
        // given
        Long categoryId = 43L;
        Category category = Category.builder()
                .id(categoryId)
                .name("Groceries")
                .color("#fff")
                .icon("mdi-shape-outline")
                .records(new ArrayList<>())
                .build();

        CreateCategoryRequest createRequest = new CreateCategoryRequest(
                "Groceries",
                "mdi-shape-outline",
                "#fff"
        );
        given(categoryRepository.findById(categoryId))
                .willReturn(Optional.of(category));

        // when
        categoryService.deleteById(categoryId);

        // then
        ArgumentCaptor<Long> idCapture = ArgumentCaptor.forClass(Long.class);
        verify(categoryRepository).deleteById(idCapture.capture());
        assertThat(idCapture.getValue()).isEqualTo(categoryId);
    }

    @Test
    void update() throws CategoryNotFoundException {
        // given
        Long categoryId = 43L;
        Category category = Category.builder()
                .id(categoryId)
                .name("Groceries")
                .color("#fff")
                .icon("mdi-shape-outline")
                .records(new ArrayList<>())
                .build();
        CategoryDto updateRequest = new CategoryDto(
                categoryId,
                "Food",
                "mdi-shape",
                "3d3d3d"
        );
        Category updatedCategory = Category.builder()
                .id(categoryId)
                .name("Food")
                .color("3d3d3d")
                .icon("mdi-shape")
                .records(new ArrayList<>())
                .build();

        given(categoryRepository.findById(categoryId))
                .willReturn(Optional.of(category));
        given(categoryRepository.save(updatedCategory))
                .willReturn(updatedCategory);

        // when
        Category result = categoryService.update(categoryId, updateRequest);

        // then
        ArgumentCaptor<Long> idCapture = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Category> categoryCapture = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).findById(idCapture.capture());
        verify(categoryRepository).save(categoryCapture.capture());
        assertThat(idCapture.getValue()).isEqualTo(categoryId);
        assertThat(categoryCapture.getValue()).isEqualTo(updatedCategory);
        assertThat(result).isEqualTo(updatedCategory);
    }

    @Test
    void getCategoriesAnalytic() throws UserNotFoundException {
        // given
        Long userId = 443L;
        User user = User.builder()
                .id(userId)
                .build();
        Date from = (new GregorianCalendar(2023, Calendar.JANUARY, 8)).getTime();
        Date to = (new GregorianCalendar(2023, Calendar.DECEMBER, 31)).getTime();
        List<CategoryAnalytic> analytics = List.of(
                new CategoryAnalytic(
                        Category.builder()
                                .name("Income")
                                .build(),
                        9999.89,
                        934L
                )
        );
        given(categoryRepository.findCategoriesAnalytic(userId, from, to))
                .willReturn(analytics);

        // when
        var result = categoryService.getCategoriesAnalytic(userId, from, to);

        // then
        ArgumentCaptor<Long> idCapture = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Date> fromCapture = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> toCapture = ArgumentCaptor.forClass(Date.class);
        verify(categoryRepository)
                .findCategoriesAnalytic(idCapture.capture(), fromCapture.capture(), toCapture.capture());
        assertThat(idCapture.getValue()).isEqualTo(userId);
        assertThat(fromCapture.getValue()).isEqualTo(from);
        assertThat(toCapture.getValue()).isEqualTo(to);
        assertThat(result).isEqualTo(analytics);
    }

    @Test
    void categoriesToDtos() {
        // given
        Record apples = Record.builder()
                .id(1L)
                .label("apples")
                .build();
        Record yogurts = Record.builder()
                .id(2L)
                .label("yogurts")
                .build();
        Record dividends = Record.builder()
                .id(45L)
                .label("dividends")
                .build();
        Record salary = Record.builder()
                .id(908L)
                .label("salary")
                .build();
        Category food = new Category(
                9L,
                "food",
                "mdi-food",
                "#fefefe",
                List.of(apples, yogurts)
        );
        Category income = new Category(
                934L,
                "income",
                "mdi-cash",
                "#fefeaa",
                List.of(dividends, salary)
        );
        CategoryDto foodDto = new CategoryDto(
                9L,
                "food",
                "mdi-food",
                "#fefefe"
        );
        CategoryDto incomeDto = new CategoryDto(
                934L,
                "income",
                "mdi-cash",
                "#fefeaa"
        );
        List<Category> categories = List.of(food, income);
        List<CategoryDto> categoryDtos = List.of(foodDto, incomeDto);

        // when
        List<CategoryDto> result = CategoryService.categoriesToDtos(categories);

        // then
        assertThat(result).isEqualTo(categoryDtos);
    }

    @Test
    void categoriesAnalyticToDtos() {
        Category food = new Category(
                9L,
                "food",
                "mdi-food",
                "#fefefe",
                new ArrayList<>()
        );
        Category income = new Category(
                934L,
                "income",
                "mdi-cash",
                "#fefeaa",
                new ArrayList<>()
        );
        CategoryAnalytic foodAnalytic = new CategoryAnalytic(
                food,
                9999.89,
                934L
        );
        CategoryAnalytic incomeAnalytic = new CategoryAnalytic(
                income,
                1000000000.0,
                150L
        );
        CategoryDto foodDto = new CategoryDto(
                9L,
                "food",
                "mdi-food",
                "#fefefe"
        );
        CategoryDto incomeDto = new CategoryDto(
                934L,
                "income",
                "mdi-cash",
                "#fefeaa"
        );
        CategoryAnalyticDto foodAnalyticDto = new CategoryAnalyticDto(
                foodDto,
                9999.89,
                934L
        );
        CategoryAnalyticDto incomeAnalyticDto = new CategoryAnalyticDto(
                incomeDto,
                1000000000.0,
                150L
        );
        List<CategoryAnalytic> categoryAnalytics = List.of(foodAnalytic, incomeAnalytic);
        List<CategoryAnalyticDto> categoryAnalyticDtos = List.of(foodAnalyticDto, incomeAnalyticDto);

        // when
        List<CategoryAnalyticDto> result = CategoryService.categoriesAnalyticToDtos(categoryAnalytics);

        // then
        assertThat(result).isEqualTo(categoryAnalyticDtos);
    }
}