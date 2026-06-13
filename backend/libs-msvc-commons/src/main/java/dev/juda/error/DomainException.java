package dev.juda.error;

public sealed abstract class DomainException extends RuntimeException
        permits BadRequestException, ConflictException, NotFoundException {
    protected DomainException(String message){
        super(message);
    }
}
