package com.media_sanctum.backend.resource;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataResponse<T> {
    private @Nullable T data;
    private @Nullable ErrorResponse error;

    public static <T> DataResponse<T> data(T data) {
        return DataResponse.<T>builder().data(data).build();
    }

    public static <T> DataResponse<T> error(ErrorResponse error) {
        return DataResponse.<T>builder().error(error).build();
    }
}
