package ai.anamaya.service.oms.core.context;

public record SystemCallerContext(Long companyId, Long userId) implements CallerContext {

    public SystemCallerContext(Long companyId) {
        this(companyId, null);
    }

}
