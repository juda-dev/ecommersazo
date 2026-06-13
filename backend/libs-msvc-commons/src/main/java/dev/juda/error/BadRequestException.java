package dev.juda.error;

public final class BadRequestException extends DomainException{
    public BadRequestException(String message){
        super(message);
    }
}
