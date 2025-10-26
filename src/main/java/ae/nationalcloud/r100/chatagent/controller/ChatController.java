package ae.nationalcloud.r100.chatagent.controller;

import ae.nationalcloud.r100.chatagent.dto.UserMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
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
    private final VectorStore vectorStore;

    @PostMapping("/completion")
    public String completion(@RequestBody UserMessageDto userMessageDto) {
        var qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(
                        SearchRequest.builder().
                                query(userMessageDto.getMessage()).
                                filterExpression(new FilterExpressionBuilder().eq("biz_type", "staff_salary").build())
                                .similarityThreshold(0.8)
                                .topK(1).build()
                )
                .build();

        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .filterExpression(new FilterExpressionBuilder().eq("biz_type", "staff_salary").build())
                        .similarityThreshold(0.80)
                        .topK(1)
                        .vectorStore(vectorStore)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(false)
                        .build())
                .build();

        return chatClient.prompt().user(userMessageDto.getMessage())
                .advisors(MessageChatMemoryAdvisor.builder(postgresChatMemory).conversationId(userMessageDto.getConversationId()).build())
//                .advisors(new QuestionAnswerAdvisor(vectorStore))
//                .advisors(retrievalAugmentationAdvisor)
                .advisors(qaAdvisor)
                .call().chatResponse().getResults().getFirst().getOutput().getText();
    }

    @PostMapping(value = "/streaming-completion", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamingCompletion(@RequestBody UserMessageDto userMessageDto) {
        return chatClient.prompt().user(userMessageDto.getMessage())
                .advisors(MessageChatMemoryAdvisor.builder(postgresChatMemory).conversationId(userMessageDto.getConversationId()).build())
                .stream().content();
    }

}
