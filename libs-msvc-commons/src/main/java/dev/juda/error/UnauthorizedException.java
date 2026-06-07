package dev.juda.error;

public final class UnauthorizedException extends DomainException{
    public UnauthorizedException(String message){
        super(message);
    }
}
