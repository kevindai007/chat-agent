package ae.nationalcloud.r100.chatagent.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatClientConfig {

    private final ChatClient.Builder chatClientBuilder;
    @Value("${openai.api.key}")
    private String apiKey;

    @Bean
    public ChatClient chatClient() {
        return chatClientBuilder.build();
    }

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().build();
    }

    @Bean
    public OpenAiEmbeddingModel openAiEmbeddingModel() {
        OpenAiApi openAiApi = OpenAiApi.builder().apiKey(apiKey).build();
        return new OpenAiEmbeddingModel(openAiApi, MetadataMode.EMBED);
    }
}
