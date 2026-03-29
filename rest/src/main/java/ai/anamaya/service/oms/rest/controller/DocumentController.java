package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.request.DocumentUploadRequest;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.DocumentService;
import ai.anamaya.service.oms.rest.dto.request.DocumentUploadRequestRest;
import ai.anamaya.service.oms.rest.dto.response.ApiResponse;
import ai.anamaya.service.oms.rest.mapper.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentMapper documentMapper;
    private final JwtUtils jwtUtils;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> upload(
        @ModelAttribute DocumentUploadRequestRest requestRest
    ) throws Exception {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        DocumentUploadRequest request = documentMapper.toCore(requestRest);
        String key = documentService.uploadFile(userCallerContext, request);
        return ApiResponse.success(key);
    }

    @GetMapping("/url")
    public ApiResponse<String> getUrl(@RequestParam String key) {
        return ApiResponse.success(documentService.generateDownloadUrl(key));
    }

}
