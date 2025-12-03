package ai.anamaya.service.oms.core.context;

public sealed interface CallerContext
    permits UserCallerContext, SystemCallerContext {

    Long companyId();
}
