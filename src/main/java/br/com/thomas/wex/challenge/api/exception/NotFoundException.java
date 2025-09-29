package br.com.thomas.wex.challenge.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -9221648404855307733L;

    /**
     * Constructs a <code>NotFoundException</code> with the specified message.
     *
     * @param message
     *            the detail message.
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a {@code NotFoundException} with the specified message and root
     * cause.
     *
     * @param message
     *            the detail message.
     * @param t
     *            root cause
     */
    public NotFoundException(String message, Throwable t) {
        super(message, t);
    }
}