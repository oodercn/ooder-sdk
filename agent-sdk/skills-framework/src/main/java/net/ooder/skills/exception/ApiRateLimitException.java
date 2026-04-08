package net.ooder.skills.exception;

public class ApiRateLimitException extends DiscoveryException {

    private static final long serialVersionUID = 1L;

    private final long retryAfterSeconds;

    public ApiRateLimitException(String source, long retryAfterSeconds) {
        super("API_RATE_LIMIT", String.format("%s API rate limit exceeded. Retry after %d seconds", source, retryAfterSeconds));
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
