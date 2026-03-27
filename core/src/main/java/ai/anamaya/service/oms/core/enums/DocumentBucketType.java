package ai.anamaya.service.oms.core.enums;

import lombok.Getter;

@Getter
public enum DocumentBucketType {
    ATTACHMENT_BOOKING("attachment-booking"),
    ATTACHMENT_HOTEL("attachment-hotel"),
    ATTACHMENT_FLIGHT("attachment-flight");

    private final String path;

    DocumentBucketType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
