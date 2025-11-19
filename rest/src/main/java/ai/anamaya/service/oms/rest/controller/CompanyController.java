package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.dto.request.CompanyRequest;
import ai.anamaya.service.oms.core.dto.response.CompanyResponse;
import ai.anamaya.service.oms.core.service.CompanyService;
import ai.anamaya.service.oms.rest.dto.request.CompanyRequestRest;
import ai.anamaya.service.oms.rest.dto.response.CompanyResponseRest;
import ai.anamaya.service.oms.rest.dto.response.ApiResponse;
import ai.anamaya.service.oms.rest.mapper.CompanyMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService service;
    private final CompanyMapper mapper;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @PostMapping
    public ApiResponse<CompanyResponseRest> create(
        @Valid @RequestBody CompanyRequestRest requestRest
    ) {
        CompanyRequest requestCore = mapper.toCore(requestRest);

        CompanyResponse created = service.create(requestCore);

        return ApiResponse.success(mapper.toRest(created));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<CompanyResponseRest> update(
        @PathVariable Long id,
        @Valid @RequestBody CompanyRequestRest requestRest
    ) {
        CompanyRequest requestCore = mapper.toCore(requestRest);

        CompanyResponse updated = service.update(id, requestCore);

        return ApiResponse.success(mapper.toRest(updated));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success("Company deleted successfully");
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ApiResponse<CompanyResponseRest> getById(@PathVariable Long id) {

        CompanyResponse response = service.findById(id);

        return ApiResponse.success(mapper.toRest(response));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @GetMapping
    public ApiResponse<List<CompanyResponseRest>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort
    ) {
        Sort sorting;

        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(";");
            String field = parts[0];
            Sort.Direction dir = (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

            sorting = Sort.by(dir, field);
        } else {
            sorting = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Page<CompanyResponse> pageResult = service.findAll(page, size, sorting);

        List<CompanyResponseRest> listRest =
            pageResult.getContent()
                .stream()
                .map(mapper::toRest)
                .toList();

        return ApiResponse.paginatedSuccess(
            listRest,
            pageResult.getTotalElements(),
            pageResult.getTotalPages(),
            pageResult.isLast(),
            pageResult.getSize(),
            pageResult.getNumber()
        );
    }
}
