package com.project.artists.exception;

/**
 * Exception para rate limit excedido (429)
 */
public class RateLimitExceededException extends RuntimeException {
    
    private final long retryAfter; // segundos até poder tentar novamente
    
    public RateLimitExceededException(String message, long retryAfter) {
        super(message);
        this.retryAfter = retryAfter;
    }
    
    public long getRetryAfter() {
        return retryAfter;
    }
}
