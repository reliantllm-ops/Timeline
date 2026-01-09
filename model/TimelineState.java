package model;

import java.util.ArrayList;

public class TimelineState {
    public ArrayList<TimelineTask> tasks;
    public ArrayList<TimelineMilestone> milestones;
    public ArrayList<Object> layerOrder;
    public ArrayList<TimelineEvent> events;

    public TimelineState(ArrayList<TimelineTask> tasks, ArrayList<TimelineMilestone> milestones,
                  ArrayList<Object> layerOrder, ArrayList<TimelineEvent> events) {
        // Deep copy tasks
        this.tasks = new ArrayList<>();
        for (TimelineTask t : tasks) {
            this.tasks.add(t.copy());
        }
        // Deep copy milestones
        this.milestones = new ArrayList<>();
        for (TimelineMilestone m : milestones) {
            this.milestones.add(m.copy());
        }
        // Deep copy layer order (references to new copies)
        this.layerOrder = new ArrayList<>();
        for (Object item : layerOrder) {
            if (item instanceof TimelineTask) {
                int idx = tasks.indexOf(item);
                if (idx >= 0) this.layerOrder.add(this.tasks.get(idx));
            } else if (item instanceof TimelineMilestone) {
                int idx = milestones.indexOf(item);
                if (idx >= 0) this.layerOrder.add(this.milestones.get(idx));
            }
        }
        // Deep copy events
        this.events = new ArrayList<>();
        for (TimelineEvent e : events) {
            this.events.add(new TimelineEvent(e.title, e.date, e.description));
        }
    }
}
