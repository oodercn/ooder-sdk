package net.ooder.skills.exception;

public class RepositoryNotFoundException extends DiscoveryException {

    private static final long serialVersionUID = 1L;

    private final String owner;
    private final String repo;

    public RepositoryNotFoundException(String owner, String repo) {
        super("REPOSITORY_NOT_FOUND", String.format("Repository not found: %s/%s", owner, repo));
        this.owner = owner;
        this.repo = repo;
    }

    public String getOwner() {
        return owner;
    }

    public String getRepo() {
        return repo;
    }
}
