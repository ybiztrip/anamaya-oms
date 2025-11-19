package ai.anamaya.service.oms.core.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    // Optional pagination fields (null if not paginated)
    private Long totalElements;
    private Integer totalPages;
    private Boolean last;
    private Integer size;
    private Integer number;

    // ✅ Normal success (single or list, without pagination)
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Success")
                .data(data)
                .build();
    }

    // ✅ Success with pagination
    public static <T> ApiResponse<T> paginatedSuccess(
            T data,
            long totalElements,
            int totalPages,
            boolean last,
            int size,
            int number
    ) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Success")
                .data(data)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .last(last)
                .size(size)
                .number(number)
                .build();
    }

    // ✅ Error case
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
