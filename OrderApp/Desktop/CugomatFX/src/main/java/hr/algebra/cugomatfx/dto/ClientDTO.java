package hr.algebra.cugomatfx.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
    private Integer id;
    private String name;
    private String code;
    private String address;
    private String phone;
    private String oib;
    private Boolean active;
    private LocalDateTime licenseExpiryTime;
    private String locationSecret;
    private String webPageUrl;
    private Boolean vipDayActive;
    private Integer vipDayCode;
}
