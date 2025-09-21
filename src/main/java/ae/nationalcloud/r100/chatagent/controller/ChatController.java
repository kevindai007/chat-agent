package ae.nationalcloud.r100.chatagent.controller;

import ae.nationalcloud.r100.chatagent.dto.UserMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/chat/")
public class ChatController {

    private final ChatClient chatClient;
    private final ChatMemory postgresChatMemory;

    @PostMapping("/completion")
    public String completion(@RequestBody UserMessageDto userMessageDto) {
        return chatClient.prompt().user(userMessageDto.getMessage())
                .advisors(MessageChatMemoryAdvisor.builder(postgresChatMemory).conversationId(userMessageDto.getConversationId()).build())
                .call().chatResponse().getResults().getFirst().getOutput().getText();
    }

    @PostMapping(value = "/streaming-completion", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamingCompletion(@RequestBody UserMessageDto userMessageDto) {
        return chatClient.prompt().user(userMessageDto.getMessage())
                .advisors(MessageChatMemoryAdvisor.builder(postgresChatMemory).conversationId(userMessageDto.getConversationId()).build())
                .stream().content();
    }

}
