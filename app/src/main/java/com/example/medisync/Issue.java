public class Issue {
    private String id;
    private String issue;
    private String resolution;
    private String dateAdded;

    public Issue() {
        // Required empty constructor for Firestore
    }

    public Issue(String id, String issue, String resolution, String dateAdded) {
        this.id = id;
        this.issue = issue;
        this.resolution = resolution;
        this.dateAdded = dateAdded;
    }

    public String getId() {
        return id;
    }

    public String getIssue() {
        return issue;
    }

    public String getResolution() {
        return resolution;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
}
