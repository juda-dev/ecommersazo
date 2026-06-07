package dev.juda.error;

public final class NotFoundException extends DomainException{
    public NotFoundException(String resource, Object id){
        super(resource + " not found with id: " + id);
    }

    public NotFoundException(String message){
        super(message);
    }
}
