package ae.nationalcloud.r100.chatagent.controller;

import ae.nationalcloud.r100.chatagent.dto.RagDocRequest;
import ae.nationalcloud.r100.chatagent.service.StaffSalaryService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .similarityThreshold(0.8d)
                .filterExpression(new FilterExpressionBuilder().eq("staff_name", "kevin").build())
                .topK(10)
                .build();
        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);
        System.out.println(similarDocuments);
    }

    @PostMapping("/add-document")
    public void addDocument(@RequestBody RagDocRequest ragDocRequest) {
        Document document = Document.builder()
                .text(ragDocRequest.fileContent())
                .metadata("biz_type", "developer_doc")
                .metadata("file_name", ragDocRequest.fileName())
                .build();
        vectorStore.add(List.of(document));
    }

    @PostMapping(value = "/add-media-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addMediaDocument(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "bizType", defaultValue = "developer_doc") String bizType,
            @RequestParam(value = "staffName", required = false) String staffName) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
        }

        List<Document> documents = extractText(file);
        if (CollectionUtils.isEmpty(documents)) {
            return;
        }
        for (Document document : documents) {
            document.getMetadata().put("biz_type", bizType);
            document.getMetadata().put("file_name", file.getOriginalFilename());
            document.getMetadata().put("content_type", file.getContentType());
            document.getMetadata().put("file_size", String.valueOf(file.getSize()));
            document.getMetadata().put("staff_name", staffName != null ? staffName : "");
        }
        documents = splitCustomized(documents);
        vectorStore.add(documents);
    }

    @SneakyThrows
    private List<Document> extractText(MultipartFile file) {
        Resource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }

            @Override
            public long contentLength() {
                return file.getSize();
            }
        };
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        return reader.read();
    }

    public List<Document> splitCustomized(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter(1000, 400, 10, 5000, true);
        return splitter.apply(documents);
    }

}
