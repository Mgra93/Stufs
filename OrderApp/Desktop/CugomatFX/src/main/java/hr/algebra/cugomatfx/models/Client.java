package hr.algebra.cugomatfx.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private Integer id;
    private String name;
    private String code;
    private String address;
    private String phone;
    private String oib;
    private Boolean active;
    private LocalDateTime createdOn;
    private LocalDateTime licenseExpiryTime;
    private String locationSecret;
    private String webPage;
    private Boolean vipDayActive;
    private Integer vipDayCode;
}
