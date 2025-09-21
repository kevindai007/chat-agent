package ae.nationalcloud.r100.chatagent.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "chat_history")
public class ChatHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "message_type", nullable = false, length = 20)
    private String messageType;

    @Column(name = "message_content", nullable = false, length = 4096)
    private String messageContent;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_time", nullable = false)
    private Instant createdTime;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_time", nullable = false)
    private Instant updatedTime;

}