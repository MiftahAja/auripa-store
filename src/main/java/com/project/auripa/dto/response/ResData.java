package com.project.auripa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class ResData<T> {

    private String status;
    private String message;
    private T data;

    // Static Methods
    public static <T> ResData<T> of(T data) {
        return new ResData<>("success", "Success", data);
    }

    public static <T> ResData<T> of(String message, T data) {
        return new ResData<>("success", message, data);
    }

    public static ResData<Void> noContent() {
        return new ResData<>("success", "Success", null);
    }

    public static <T> ResData<T> created(T data) {
        return new ResData<>("success", "Created successfully", data);
    }

    public static ResData<Void> deleted() {
        return new ResData<>("success", "Deleted successfully", null);
    }
    
    public static <T> ResData<T> error(int code, String message, T data) {
        String statusLabel = (code >= 200 && code < 300) ? "success" : "error";
        return new ResData<>(statusLabel, message, data);
    }
}