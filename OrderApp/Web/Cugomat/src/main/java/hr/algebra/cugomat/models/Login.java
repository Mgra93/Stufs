package hr.algebra.cugomat.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Login {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Time", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime time;;

    @Column(name = "Ip", nullable = false)
    private String ip;

    @ManyToOne
    @JoinColumn(name = "UserId", nullable = false)
    private User user;
}
