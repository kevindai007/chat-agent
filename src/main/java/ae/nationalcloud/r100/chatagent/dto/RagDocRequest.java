package ae.nationalcloud.r100.chatagent.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RagDocRequest(
        String fileName,
        String fileContent) {
}
