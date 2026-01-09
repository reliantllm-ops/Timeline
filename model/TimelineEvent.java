package model;

public class TimelineEvent {
    public String title, date, description;

    public TimelineEvent(String title, String date, String description) {
        this.title = title;
        this.date = date;
        this.description = description;
    }

    public TimelineEvent copy() {
        return new TimelineEvent(title, date, description);
    }
}
