package com.mita.fmipaschedule.model;

public class InterfaceModel<T> {
    private boolean success;
    private String message;
    private T data;

    public InterfaceModel(){

    }

    public InterfaceModel(boolean success, String message, T data){
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
