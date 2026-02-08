package hr.algebra.cugomat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientPreviewDTO {
    private Integer id;
    private String name;
    private String code;
    private String address;
    private String phone;
    private String oib;
    private String locationSecret;
    private String webPageUrl;
}
