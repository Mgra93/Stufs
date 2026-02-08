package hr.algebra.cugomatfx.mapper;

import hr.algebra.cugomatfx.dto.OrderFilterDTO;
import hr.algebra.cugomatfx.dto.OrderFilter;

public class OrderFilterMapper {
    public static OrderFilterDTO toDTO(OrderFilter filter) {
        if (filter == null) return null;

        OrderFilterDTO dto = new OrderFilterDTO();
        dto.setDate(filter.getDate());
        dto.setWorker(filter.getWorker());
        dto.setStatus(filter.getStatus());
        dto.setUser(filter.getUser());
        return dto;
    }
}
