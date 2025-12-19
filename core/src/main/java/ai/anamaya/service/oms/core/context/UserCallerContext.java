package ai.anamaya.service.oms.core.context;

public record UserCallerContext(
    Long companyId,
    Long userId,
    String userEmail) implements CallerContext {
}
