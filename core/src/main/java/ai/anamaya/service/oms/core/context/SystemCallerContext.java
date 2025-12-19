package ai.anamaya.service.oms.core.context;

public record SystemCallerContext(
    Long companyId, Long userId, String userEmail) implements CallerContext {

    public SystemCallerContext(Long companyId) {
        this(companyId, null, "");
    }

}
