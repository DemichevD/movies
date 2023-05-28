package ru.demichev.movies.exceptions;

public class ErrorResponse {
    private final String message;
    private final String error;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public String getError(){
        return error;
    }
}
