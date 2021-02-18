package lk.hd192.project.pojo;

public class Messages {

    private String message, from;
    private long time;
    private boolean seen;

    public Messages() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Messages(String message, String from, long time, boolean seen) {
        this.message = message;
        this.from = from;
        this.time = time;
        this.seen = seen;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}

