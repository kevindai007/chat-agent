package ae.nationalcloud.r100.chatagent.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "staff_salary")
public class StaffSalaryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('staff_salary_id_seq')")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "salary", nullable = false)
    private BigDecimal salary;

    @ColumnDefault("now()")
    @Column(name = "created_time", nullable = false)
    private Instant createdTime;

    @ColumnDefault("now()")
    @Column(name = "updated_time", nullable = false)
    private Instant updatedTime;

}