package hr.algebra.cugomatfx.mapper;

import hr.algebra.cugomatfx.dto.CategoryCreateDTO;
import hr.algebra.cugomatfx.dto.CategoryDTO;
import hr.algebra.cugomatfx.dto.CategoryUpdateDTO;
import hr.algebra.cugomatfx.models.Category;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {
    public static Category toEntity(CategoryDTO dto) {
        if (dto == null) return null;

        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        return category;
    }

    public static List<Category> toEntityList(List<CategoryDTO> dtoList) {
        if (dtoList == null) return null;

        return dtoList.stream()
                .map(CategoryMapper::toEntity)
                .collect(Collectors.toList());
    }

    public static CategoryCreateDTO toCreateDTO(Category object) {
        if (object == null) return null;

        CategoryCreateDTO category = new CategoryCreateDTO();
        category.setName(object.getName());
        return category;
    }

    public static CategoryUpdateDTO toUpdateDTO(Category object) {
        if (object == null) return null;

        CategoryUpdateDTO category = new CategoryUpdateDTO();
        category.setId(object.getId());
        category.setName(object.getName());
        return category;
    }
}
