package ae.nationalcloud.r100.chatagent.config;

import java.time.Instant;
import java.util.List;

import ae.nationalcloud.r100.chatagent.entity.ChatHistoryEntity;
import ae.nationalcloud.r100.chatagent.repository.ChatHistoryEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostgresChatMemory implements ChatMemory {

    private final ChatHistoryEntityRepository chatHistoryEntityRepository;

    @Override
    public void add(String conversationId, List<Message> messages) {
        for (Message message : messages) {
            ChatHistoryEntity chatHistoryEntity = new ChatHistoryEntity();
            chatHistoryEntity.setConversationId(Long.valueOf(conversationId));
            chatHistoryEntity.setMessageType(message.getMessageType().name());
            chatHistoryEntity.setMessageContent(message.getText());
            chatHistoryEntity.setCreatedTime(Instant.now());
            chatHistoryEntity.setUpdatedTime(Instant.now());
            chatHistoryEntityRepository.save(chatHistoryEntity);
        }
    }

    @Override
    public List<Message> get(String conversationId) {
        return chatHistoryEntityRepository.findByConversationId(Long.valueOf(conversationId)).stream().map(entity -> {
            MessageType messageType = MessageType.valueOf(entity.getMessageType());
            Message message = null;
            switch (messageType) {
                case USER -> message = new UserMessage(entity.getMessageContent());
                case ASSISTANT -> message = new AssistantMessage(entity.getMessageContent());
            }
            return message;
        }).toList();
    }

    @Override
    public void clear(String conversationId) {

    }
}
