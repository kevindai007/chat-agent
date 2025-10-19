package ae.nationalcloud.r100.chatagent.repository;

import ae.nationalcloud.r100.chatagent.entity.StaffSalaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffSalaryRepository extends JpaRepository<StaffSalaryEntity, Long> {
}