package com.mosaic.jc.lang;

import com.mosaic.jc.utils.reflection.ReflectionUtils;
import com.mosaic.jc.utils.reflection.ReflectionUtils;

/**
 * Represents either a result or an error.  An error may optionally include
 * a stack trace, but that is not mandatory.  The decision of whether to include
 * a stack trace or not is a balance between performance and ease of tracking
 * down problems.  The decision to not include a stack trace may be overridden
 * at boot via the System Property 'either.force.stacktraces=true|false'.
 */
@SuppressWarnings("unchecked")
public abstract class Either<T> {

    private static boolean FORCE_STACKTRACES = Boolean.parseBoolean(System.getProperty("either.force.stacktraces", "true"));


    public static <T> Either<T> success( T result ) {
        return new ResultEither<T>(result);
    }

    public static <T> Either<T> error( String errorCode, String message, Object...messageArgs ) {
        Message   msg = new Message(message,messageArgs);
        Throwable ex  = selectExceptionFor( errorCode, msg, true );

        return new ErrorEither<T>( errorCode, msg, ex );
    }

    public static <T> Either<T> errorWithNoStackTrace( String errorCode, String message, Object...messageArgs ) {
        Message   msg = new Message(message,messageArgs);
        Throwable ex  = selectExceptionFor( errorCode, msg, FORCE_STACKTRACES );

        return new ErrorEither<T>( errorCode, msg, ex );
    }

    public abstract boolean isError();
    public abstract boolean hasResult();

    public abstract T getResult();

    public abstract String getErrorCode();

    public abstract String getErrorMessage();

    public abstract Throwable getException();


    private static Throwable selectExceptionFor( String errorCode, Message msg, boolean includeStackTrace ) {
        if ( !includeStackTrace ) {
            return null;
        }

        Class<? extends Throwable> clazz = selectExceptionClassFor(errorCode);

        return ReflectionUtils.newInstance(clazz, msg.toString());
    }

    private static Class<? extends Throwable> selectExceptionClassFor( String errorCode ) {
        if ( !errorCode.contains(".") ) { // optimisation to help avoid costly ClassCastException below
            return RuntimeException.class;
        }

        try {
            Class candidateClass = Class.forName(errorCode);

            if ( ReflectionUtils.doesAExtendB(candidateClass,Throwable.class) ) {
                return candidateClass;
            } else {
                return RuntimeException.class;
            }
        } catch (ClassNotFoundException e) {
            return RuntimeException.class;
        }
    }

}

class ResultEither<T> extends Either<T> {

    private final T result;


    public ResultEither( T result ) {
        this.result = result;
    }

    public boolean isError() {
        return false;
    }

    public boolean hasResult() {
        return true;
    }

    public T getResult() {
        return result;
    }

    public String getErrorCode() {
        throw new UnsupportedOperationException("Either has a result, and thus there is no error code");
    }

    public String getErrorMessage() {
        throw new UnsupportedOperationException("Either has a result, and thus there is no error message");
    }

    public Throwable getException() {
        throw new UnsupportedOperationException("Either has a result, and thus there is no underlying exception");
    }

}

class ErrorEither<T> extends Either<T> {

    private String    errorCode;
    private Message   msg;
    private Throwable ex;


    public ErrorEither( String errorCode, Message msg, Throwable ex ) {
        this.errorCode = errorCode;
        this.msg       = msg;
        this.ex        = ex;
    }

    public boolean isError() {
        return true;
    }

    public boolean hasResult() {
        return false;
    }

    public T getResult() {
        throw new UnsupportedOperationException("");
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return msg.toString();
    }

    public Throwable getException() {
        return ex;
    }

}