package ae.nationalcloud.r100.chatagent.service;

import ae.nationalcloud.r100.chatagent.entity.StaffSalaryEntity;
import ae.nationalcloud.r100.chatagent.repository.StaffSalaryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StaffSalaryService {
    private final StaffSalaryRepository staffSalaryRepository;
    private final ObjectMapper objectMapper;
    private final VectorStore vectorStore;


    @SneakyThrows
    public void syncStaffSalaryData() {
        List<StaffSalaryEntity> salaryEntityList = staffSalaryRepository.findAll();

        List<Document> documents = new ArrayList<>();
        for (StaffSalaryEntity storyHistoryEntity : salaryEntityList) {
            Document textDoc = Document.builder()
                    .text(objectMapper.writeValueAsString(storyHistoryEntity))
                    .metadata("biz_type", "staff_salary")
                    .metadata("staff_name", storyHistoryEntity.getName())
                    .build();
            documents.add(textDoc);
        }
        vectorStore.add(documents);
    }
}
