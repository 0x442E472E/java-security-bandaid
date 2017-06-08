package oss.security.bandaid.support;

/**
 * Created by D.Grodt on 06.06.2017.
 */
public class BandaidException extends RuntimeException {
    public BandaidException() {
    }

    public BandaidException(final String message) {
        super(message);
    }

    public BandaidException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public BandaidException(final Throwable cause) {
        super(cause);
    }
}
