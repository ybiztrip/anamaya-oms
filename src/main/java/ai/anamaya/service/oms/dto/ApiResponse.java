package ai.anamaya.service.oms.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ai.anamaya.service.oms.dto.ApiResponse<T> success(T data) {
        return new ai.anamaya.service.oms.dto.ApiResponse<>(true, "Success", data);
    }

    public static <T> ai.anamaya.service.oms.dto.ApiResponse<T> error(String message) {
        return new ai.anamaya.service.oms.dto.ApiResponse<>(false, message, null);
    }
}
