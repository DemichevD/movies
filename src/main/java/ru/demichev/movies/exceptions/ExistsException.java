package ru.demichev.movies.exceptions;

public class ExistsException extends RuntimeException{
    public ExistsException(String message){super(message);}

    public ExistsException(String message, Throwable cause){
        super(message,cause);
    }
}
