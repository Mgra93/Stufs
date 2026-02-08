package hr.algebra.cugomat.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "[Client]")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "[Name]", nullable = false, length = 50)
    private String name;

    @Column(name = "Code", length = 10, nullable = false)
    private String code;

    @Column(name = "Address", length = 200)
    private String address;

    @Column(name = "Phone", length = 20)
    private String phone;

    @Column(name = "OIB", nullable = false, length = 11)
    private String oib;

    @Column(name = "Active", nullable = false)
    private Boolean active;

    @Column(name = "CreatedOn", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdOn;

    @Column(name="LicenseExpiryTime", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime licenseExpiryTime;

    @Column(name="LocationSecret", nullable = false)
    private String locationSecret;

    @Column(name="WebPageUrl", nullable = false)
    private String webPageUrl;

    @Column(name="VipDayActive", nullable = false)
    private Boolean vipDayActive;

    @Column(name="VipDayCode", nullable = false)
    private Integer vipDayCode;
}
