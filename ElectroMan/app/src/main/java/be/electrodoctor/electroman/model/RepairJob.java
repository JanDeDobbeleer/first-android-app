package be.electrodoctor.electroman.model;

/**
 * Created by janjoris on 27/01/15.
 */
public class RepairJob {

    private long id;
    private int problemCode;
    private Client client;
    private String device;
    private String description;
    private String comment;
    private boolean processed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getProblemCode() {
        return problemCode;
    }

    public void setProblemCode(int problemCode) {
        this.problemCode = problemCode;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}
