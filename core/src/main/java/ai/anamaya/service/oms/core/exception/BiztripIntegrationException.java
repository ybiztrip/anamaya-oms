package ai.anamaya.service.oms.core.exception;

import org.springframework.http.HttpStatus;

public class BiztripIntegrationException extends RuntimeException {

    private final HttpStatus status;

    public BiztripIntegrationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
