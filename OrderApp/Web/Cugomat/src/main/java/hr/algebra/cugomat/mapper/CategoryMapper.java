package hr.algebra.cugomat.mapper;

import hr.algebra.cugomat.dto.CategoryDTO;
import hr.algebra.cugomat.dto.CategoryCreateDTO;
import hr.algebra.cugomat.models.Category;

public class CategoryMapper {
    public static CategoryDTO toDTO(Category category) {
        if (category == null) return null;

        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());

        return dto;
    }

    public static Category toEntity(CategoryDTO dto) {
        if (dto == null) return null;

        Category category = new Category();
        category.setName(dto.getName());

        return category;
    }

    public static Category toEntity(CategoryCreateDTO dto){
        Category category = new Category();
        category.setName(dto.getName());

        return category;
    }
}
