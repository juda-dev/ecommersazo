package dev.juda.error;

public class ApplicationException extends BaseException{

    public ApplicationException(String errorCode, String message){
        super(message, errorCode, httpStatusFromCode(errorCode));
    }

    public ApplicationException(String errorCode, String message, int httpStatus){
        super(message, errorCode, httpStatus);
    }

    public ApplicationException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, httpStatusFromCode(errorCode), cause);
    }

    private static int httpStatusFromCode(String code) {
        if (code == null) return 500;
        if (code.contains("NOT_FOUND")) return 404;
        if (code.contains("CONFLICT") || code.contains("ALREADY_EXISTS")) return 409;
        if (code.contains("FORBIDDEN")) return 403;
        return 422;
    }
}
