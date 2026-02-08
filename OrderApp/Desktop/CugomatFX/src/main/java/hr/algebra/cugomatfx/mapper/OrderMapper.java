package hr.algebra.cugomatfx.mapper;

import hr.algebra.cugomatfx.dto.OrderDTO;
import hr.algebra.cugomatfx.models.Order;
import hr.algebra.cugomatfx.models.User;

import java.util.ArrayList;
import java.util.List;

public class OrderMapper {
    public static Order toEntity(OrderDTO dto) {
        if (dto == null) return null;

        Order order = new Order();
        order.setId(dto.getId());
        order.setCreatedOn(dto.getCreatedOn());
        order.setTotalPrice(dto.getTotalPrice());
        order.setHasDiscount(dto.getHasDiscount());
        order.setFinalPrice(dto.getFinalPrice());
        order.setStatus(dto.getStatus());
        order.setUser(UserMapper.toEntity(dto.getUser()));
        order.setWorker(UserMapper.toEntity(dto.getWorker()));
        order.setTableCode(dto.getTableCode());
        order.setProducts(ProductMapper.toEntityList(dto.getProducts()));
        return order;
    }

    public static List<Order> toEntityList(List<OrderDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) return new ArrayList<>();
        List<Order> orderList = new ArrayList<>();
        for (OrderDTO dto : dtoList) {
            orderList.add(toEntity(dto));
        }
        return orderList;
    }
}
