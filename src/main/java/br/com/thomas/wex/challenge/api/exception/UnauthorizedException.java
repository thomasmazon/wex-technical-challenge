package br.com.thomas.wex.challenge.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 4911206271888112646L;

    /**
     * Constructs a <code>UnauthorizedException</code> with the specified message.
     *
     * @param errorCode
     *            the detail message.
     */
    public UnauthorizedException(String errorCode) {
        super(errorCode);
    }

    /**
     * Constructs a {@code UnauthorizedException} with the specified message and root cause.
     *
     * @param errorCode
     *            the detail message.
     * @param t
     *            root cause
     */
    public UnauthorizedException(String errorCode, Throwable t) {
        super(errorCode, t);
    }
}