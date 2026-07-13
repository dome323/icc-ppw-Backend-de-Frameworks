package ec.edu.ups.icc.fundamentos01.categories.service;

import ec.edu.ups.icc.fundamentos01.categories.dtos.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.categories.dtos.CreateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.dtos.UpdateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.entities.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.categories.repositories.CategoryRepository;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.NotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }

    //enlista todo la catergorias
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> findAll() {
        return categoryRepository.findByDeletedFalse()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto findOne(Long id) {
        CategoryEntity entity = findActiveCategory(id);
        return toResponse(entity);
    }

    @Override
    public CategoryResponseDto create(CreateCategoryDto dto) {

        if (categoryRepository
                .existsByNameIgnoreCaseAndDeletedFalse(dto.getName())) {
            throw new ConflictException(
                    "Category name already registered"
            );
        }

        CategoryEntity entity = new CategoryEntity();
        entity.setName(dto.getName().trim());
        entity.setDescription(dto.getDescription());

        CategoryEntity saved =
                categoryRepository.save(entity);

        return toResponse(saved);
    }

    @Override
    public CategoryResponseDto update(
            Long id,
            UpdateCategoryDto dto
    ) {
        CategoryEntity entity = findActiveCategory(id);

        categoryRepository
                .findByNameIgnoreCaseAndDeletedFalse(dto.getName())
                .filter(category ->
                        !category.getId().equals(id))
                .ifPresent(category -> {
                    throw new ConflictException(
                            "Category name already registered"
                    );
                });

        entity.setName(dto.getName().trim());
        entity.setDescription(dto.getDescription());

        return toResponse(categoryRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        CategoryEntity entity = findActiveCategory(id);

        entity.setDeleted(true);
        categoryRepository.save(entity);
    }

    private CategoryEntity findActiveCategory(Long id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(

        );

        if (entity.isDeleted()) {
            throw new NotFoundException(
                    "Category not found"
            );
        }

        return entity;
    }

    private CategoryResponseDto toResponse(
            CategoryEntity entity
    ) {
        CategoryResponseDto dto =
                new CategoryResponseDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());

        return dto;
    }
}