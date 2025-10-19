package ae.nationalcloud.r100.chatagent.controller;

import ae.nationalcloud.r100.chatagent.service.StaffSalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rag/management")
public class RagManagementController {
    private final StaffSalaryService staffSalaryService;
    private final VectorStore vectorStore;


    @PostMapping("/sync-staff-salary")
    public void syncStaffSalary() {
        staffSalaryService.syncStaffSalaryData();
    }

    @GetMapping("/search")
    public void search(@RequestParam String query) {
        List<Document> similarDocuments = vectorStore.similaritySearch(query);
        System.out.println(similarDocuments);
    }
}
