package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.service.RoleService;
import ai.anamaya.service.oms.rest.dto.response.ApiResponse;
import ai.anamaya.service.oms.rest.dto.response.RoleResponseRest;
import ai.anamaya.service.oms.rest.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService service;
    private final RoleMapper mapper;

    @GetMapping
    public ApiResponse<List<RoleResponseRest>> getAll() {
        List<RoleResponseRest> roles = service.getAll()
            .stream()
            .map(mapper::toRest)
            .toList();

        return ApiResponse.success(roles);
    }
}
