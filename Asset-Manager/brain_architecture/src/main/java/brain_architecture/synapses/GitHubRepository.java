package brain_architecture.synapses;

/**
 * GitHubRepository - Data class for GitHub repository information.
 */
public class GitHubRepository {
    
    private final String owner;
    private final String repo;
    private final String content;
    private final long stars;
    private final long forks;
    
    public GitHubRepository(String owner, String repo, String content, long stars, long forks) {
        this.owner = owner;
        this.repo = repo;
        this.content = content;
        this.stars = stars;
        this.forks = forks;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public String getRepo() {
        return repo;
    }
    
    public String getContent() {
        return content;
    }
    
    public long getStars() {
        return stars;
    }
    
    public long getForks() {
        return forks;
    }
}
