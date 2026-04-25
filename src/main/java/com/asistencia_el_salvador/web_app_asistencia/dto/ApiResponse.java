package com.asistencia_el_salvador.web_app_asistencia.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private Object errors;

    @JsonIgnore
    private T data;

    @JsonIgnore
    private String fieldName = "data"; // Por defecto "data"

    // ── Constructors ──────────────────────────────────────────

    private ApiResponse() {}

    // ── Factory methods ───────────────────────────────────────

    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success   = true;
        r.data      = data;
        return r;
    }

    public static <T> ApiResponse<T> ok(T data, String fieldName) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success   = true;
        r.data      = data;
        r.fieldName = fieldName;
        return r;
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success   = true;
        r.message   = message;
        r.data      = data;
        return r;
    }

    public static <T> ApiResponse<T> ok(String message, T data, String fieldName) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success   = true;
        r.message   = message;
        r.data      = data;
        r.fieldName = fieldName;
        return r;
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = false;
        r.message = message;
        return r;
    }

    public static <T> ApiResponse<T> error(String message, Object errors) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = false;
        r.message = message;
        r.errors  = errors;
        return r;
    }

    // ── Getters ───────────────────────────────────────────────

    public boolean isSuccess()  { return success; }
    public String  getMessage() { return message; }
    public Object  getErrors()  { return errors;  }

    @JsonIgnore
    public T getData() { return data; }

    // ── Serialización dinámica ────────────────────────────────

    @JsonAnyGetter
    public Map<String, Object> getDynamicFields() {
        Map<String, Object> map = new HashMap<>();
        if (data != null) {
            map.put(fieldName, data);
        }
        return map;
    }
}