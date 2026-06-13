package dev.juda.error;

public final class ConflictException extends DomainException{
    public ConflictException(String message){
        super(message);
    }
}
