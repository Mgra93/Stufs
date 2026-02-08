package hr.algebra.cugomatfx.mapper;

import hr.algebra.cugomatfx.dto.ProductCreateDTO;
import hr.algebra.cugomatfx.dto.ProductDTO;
import hr.algebra.cugomatfx.dto.ProductUpdateDTO;
import hr.algebra.cugomatfx.models.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {
    public static Product toEntity(ProductDTO dto) {
        if (dto == null) return null;

        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setCategory(CategoryMapper.toEntity(dto.getCategory()));
        product.setQuantity(dto.getQuantity());
        product.setSumPrice(dto.getSumPrice());

        return product;
    }

    public static List<Product> toEntityList(List<ProductDTO> dtoList) {
        if (dtoList == null) return null;

        return dtoList.stream()
                .map(ProductMapper::toEntity)
                .collect(Collectors.toList());
    }

    public static ProductUpdateDTO toUpdateDto(Product product) {
        if (product == null) return null;
        ProductUpdateDTO dto = new ProductUpdateDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setCategoryId(product.getCategory().getId());

        return dto;
    }

    public static ProductCreateDTO toCreateDto(Product product) {
        if (product == null) return null;
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setCategoryId(product.getCategory().getId());

        return dto;
    }
}
