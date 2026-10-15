package com.project.auripa.dto.response;

public class ResData<T> {

    private String status;
    private String message;
    private T data;

    // Constructors
    public ResData() {
    }

    // Constructor for success responses
    public ResData(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getter and Setter
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

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