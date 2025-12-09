package ai.anamaya.service.oms.core.context;

public record UserCallerContext(
    Long companyId,
    Long userId) implements CallerContext {
}
