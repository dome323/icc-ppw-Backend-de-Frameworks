package ec.edu.ups.icc.fundamentos01.products.mappers;

import java.util.Comparator;
import java.util.List;

import ec.edu.ups.icc.fundamentos01.categories.dtos.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.categories.entities.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;

public class ProductMapper {

    private ProductMapper() {
    }

    /*
     * Convierte ProductEntity
     * en ProductModel.
     */
    public static ProductModel toModelFromEntity(
            ProductEntity entity
    ) {

        ProductModel model =
                new ProductModel();

        model.setId(
                entity.getId()
        );

        model.setName(
                entity.getName()
        );

        model.setPrice(
                entity.getPrice()
        );

        model.setStock(
                entity.getStock()
        );

        model.setOwner(
                entity.getOwner()
        );

        /*
         * ProductEntity tiene Set<CategoryEntity>.
         * ProductModel tiene List<CategoryEntity>.
         *
         * Por eso se convierte el Set en List.
         */
        if (entity.getCategories() != null) {

            model.setCategories(
                    entity.getCategories()
                            .stream()
                            .toList()
            );
        } else {

            model.setCategories(
                    List.of()
            );
        }

        model.setCreatedAt(
                entity.getCreatedAt()
        );

        model.setUpdatedAt(
                entity.getUpdatedAt()
        );

        model.setDeleted(
                entity.isDeleted()
        );

        return model;
    }

    /*
     * Convierte ProductModel
     * en ProductResponseDto.
     */
    public static ProductResponseDto toResponse(
            ProductModel model
    ) {

        ProductResponseDto dto =
                new ProductResponseDto();

        dto.setId(
                model.getId()
        );

        dto.setName(
                model.getName()
        );

        dto.setPrice(
                model.getPrice()
        );

        dto.setStock(
                model.getStock()
        );

        dto.setCreatedAt(
                model.getCreatedAt()
        );

        dto.setUpdatedAt(
                model.getUpdatedAt()
        );

        /*
         * Convierte el usuario propietario
         * en UserResponseDto.
         */
        if (model.getOwner() != null) {

            UserResponseDto ownerDto =
                    new UserResponseDto();

            ownerDto.setId(
                    model.getOwner().getId()
            );

            ownerDto.setName(
                    model.getOwner().getName()
            );

            ownerDto.setEmail(
                    model.getOwner().getEmail()
            );

            dto.setOwner(
                    ownerDto
            );
        }

        /*
         * Convierte las categorías del modelo
         * en CategoryResponseDto.
         */
        List<CategoryEntity> modelCategories =
                model.getCategories() != null
                        ? model.getCategories()
                        : List.of();

        List<CategoryResponseDto> categories =
                modelCategories
                        .stream()
                        .filter(category ->
                                category != null
                        )
                        .filter(category ->
                                !category.isDeleted()
                        )
                        .sorted(
                                Comparator.comparing(
                                        CategoryEntity::getId
                                )
                        )
                        .map(category -> {

                            CategoryResponseDto categoryDto =
                                    new CategoryResponseDto();

                            categoryDto.setId(
                                    category.getId()
                            );

                            categoryDto.setName(
                                    category.getName()
                            );

                            categoryDto.setDescription(
                                    category.getDescription()
                            );

                            return categoryDto;
                        })
                        .toList();

        dto.setCategories(
                categories
        );

        return dto;
    }
}