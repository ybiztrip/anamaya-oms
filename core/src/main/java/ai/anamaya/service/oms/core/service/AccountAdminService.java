package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.internal.AccountListService;
import ai.anamaya.service.oms.core.client.internal.dto.response.AccountResponse;
import ai.anamaya.service.oms.core.context.CallerContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountAdminService {

    private final AccountListService accountListService;

    public List<AccountResponse> getAccountList(CallerContext callerContext) {
        return accountListService.getAccountList(callerContext);
    }
}
