package ae.nationalcloud.r100.chatagent.repository;

import java.util.List;

import ae.nationalcloud.r100.chatagent.entity.ChatHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatHistoryEntityRepository extends JpaRepository<ChatHistoryEntity, Long> {

    List<ChatHistoryEntity> findByConversationId(Long aLong);
}