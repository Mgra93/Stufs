package hr.algebra.cugomat.mapper;

import hr.algebra.cugomat.dto.OrderCreateDTO;
import hr.algebra.cugomat.dto.OrderDTO;
import hr.algebra.cugomat.dto.ProductDTO;
import hr.algebra.cugomat.models.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {
    public static OrderDTO toDTO(Order order) {
        if (order == null) return null;

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCreatedOn(order.getCreatedOn());
        dto.setStatus(order.getStatus());
        dto.setTableCode(order.getTableCode());
        dto.setUser(UserMapper.toPreviewDTO(order.getUser()));

        if(order.getWorker() != null){
            dto.setWorker(UserMapper.toPreviewDTO(order.getWorker()));
        }

        if(order.getClient() != null){
            dto.setClient(ClientMapper.toPreviewDTO(order.getClient()));
        }

        List<ProductDTO> productDTOList = new ArrayList<>();

        if (order.getOrderProducts() != null) {
            for (OrderProduct op : order.getOrderProducts()) {
                ProductDTO pDto = ProductMapper.toDTO(op);
                productDTOList.add(pDto);
            }
        }
        dto.setProducts(productDTOList);
        dto.setTotalPrice(order.getTotalPrice());
        dto.setHasDiscount(order.getHasDiscount());
        dto.setFinalPrice(order.getFinalPrice());

        return dto;
    }
}
