package ai.anamaya.service.oms.rest.controller;


import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.FileService;
import ai.anamaya.service.oms.rest.dto.request.FileFetchRequestRest;
import ai.anamaya.service.oms.rest.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final JwtUtils jwtUtils;
    private final FileMapper mapper;
    private final FileService fileService;

    @PostMapping("/fetch")
    public ResponseEntity<byte[]> fetch(
        @RequestBody FileFetchRequestRest requestRest) {

        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();

        UserCallerContext userCallerContext =
            new UserCallerContext(companyId, userId, userEmail);

        var request = mapper.toCore(requestRest);
        byte[] pdf = fileService.fetch(userCallerContext, request);

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file.pdf")
            .body(pdf);
    }
}
