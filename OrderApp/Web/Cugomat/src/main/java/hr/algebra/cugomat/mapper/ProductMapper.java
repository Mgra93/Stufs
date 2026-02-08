package hr.algebra.cugomat.mapper;

import hr.algebra.cugomat.dto.ProductCreateDTO;
import hr.algebra.cugomat.dto.ProductDTO;
import hr.algebra.cugomat.models.OrderProduct;
import hr.algebra.cugomat.models.Product;

import java.math.BigDecimal;

public class ProductMapper {
    public static ProductDTO toDTO(OrderProduct orderProduct) {
        if (orderProduct == null) return null;

        ProductDTO dto = new ProductDTO();
        dto.setId(orderProduct.getId());
        dto.setName(orderProduct.getProduct().getName());
        dto.setPrice(orderProduct.getProduct().getPrice());
        dto.setQuantity(orderProduct.getQuantity());
        BigDecimal price = orderProduct.getProduct().getPrice();
        BigDecimal quantity = BigDecimal.valueOf(orderProduct.getQuantity());
        dto.setSumPrice(price.multiply(quantity));

        if(orderProduct.getProduct().getCategory() != null){
            dto.setCategory(CategoryMapper.toDTO(orderProduct.getProduct().getCategory()));
        }

        return dto;
    }

    public static ProductDTO toDTO(Product product) {
        if (product == null) return null;

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setCategory(CategoryMapper.toDTO(product.getCategory()));

        return dto;
    }

    public static Product toEntity(ProductCreateDTO dto){
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        return product;
    }
}
