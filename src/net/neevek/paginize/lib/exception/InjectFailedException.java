package net.neevek.paginize.lib.exception;

/**
 * Created by neevek on 12/27/13.
 */
public class InjectFailedException extends RuntimeException {
    public InjectFailedException(String msg) {
        super(msg);
    }

    public InjectFailedException(Throwable throwable) {
        super(throwable);
    }

    public InjectFailedException() {
        super();
    }
}
