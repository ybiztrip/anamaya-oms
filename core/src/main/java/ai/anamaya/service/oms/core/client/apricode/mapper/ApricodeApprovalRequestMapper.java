package ai.anamaya.service.oms.core.client.apricode.mapper;

import ai.anamaya.service.oms.core.client.apricode.dto.request.AppricodeApprovalRequestRequest;
import ai.anamaya.service.oms.core.entity.User;

public class ApricodeApprovalRequestMapper {

    public AppricodeApprovalRequestRequest map(User user) {
        AppricodeApprovalRequestRequest req = new AppricodeApprovalRequestRequest();
        return req;
    }
}
