package main.tasks;

import main.status.Status;
import main.util.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private String description;
    private Integer id;
    private String name;
    private Status status;
    private int duration;
    private LocalDateTime startTime;

    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");

    public Task(String description, String name, Status status, LocalDateTime startTime,int duration) {
        this.description = description;
        this.name = name;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime(){
        try {
            return this.startTime.plus(Duration.ofMinutes(this.duration));
        } catch (NullPointerException e){
            System.out.print("NullPointerException caught");
            return null;
        }
    }

    public DateTimeFormatter getFormatter () { return FORMATTER; }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration){
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(description, task.description) && Objects.equals(name, task.name) &&
                status == task.status && Objects.equals(startTime, task.startTime) &&
                Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, id, name, status);
    }

    @Override
    public String toString() {
        if (startTime != null) {
            return id + "," + TaskType.TASK + "," + name + "," + status + "," + description + "," +
                    startTime.format(FORMATTER) + "," + this.getEndTime().format(FORMATTER) + "," + duration + ",\n";
        } else {
            return "startTime = null";
        }
    }
}
