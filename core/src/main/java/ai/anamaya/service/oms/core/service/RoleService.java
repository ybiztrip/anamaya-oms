package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.dto.response.RoleResponse;
import ai.anamaya.service.oms.core.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository repository;

    public List<RoleResponse> getAll() {
        return repository.findAllByIsSuperAdmin("0")
            .stream()
            .map(role -> RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .code(role.getCode())
                .build())
            .toList();
    }
}
