package main.managers;

import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TaskManager {
    List<Task> getHistory();

    Task addTask(Task task) throws IOException, InterruptedException;

    Epic addEpic(Epic epic) throws IOException, InterruptedException;

    Subtask addSubtask(Subtask subtask) throws IOException, InterruptedException;

    void removeTaskById(int id) throws IOException, InterruptedException;

    void removeEpicById(int id) throws IOException, InterruptedException;

    void removeSubtaskById(int id) throws IOException, InterruptedException;

    void removeAllTasks() throws IOException, InterruptedException;

    void removeAllEpics() throws IOException, InterruptedException;

    void removeAllSubtasks() throws IOException, InterruptedException;

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getAllSubtasksByEpicId(int id);

    void updateTask(Task task) throws IOException, InterruptedException;

    void updateEpic(Epic epic);

   // void updateStatusEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void findStartTimeAndDurationOfEpic(Epic epic);

    Map<Integer, Task> getTaskList();

    Map<Integer, Epic> getEpicsList();
    Map<Integer, Subtask> getSubtasksList();

    void validation (Task task);

    public List<Task> getPrioritizedTasks();

    void setPrioritizedTasks(Task task);

    Integer getId();

    boolean isValidSubtaskId(int subtaskId);
}