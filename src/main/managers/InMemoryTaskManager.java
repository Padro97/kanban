package main.managers;

import main.exceptions.ValidationException;
import main.status.Status;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected HistoryManager historyManager;
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    public int generateId() {
        return ++id;
    }
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Task addTask(Task task) {
        int newTaskId = generateId();
        task.setId(newTaskId);
        validation(task);
        tasks.put(newTaskId, task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        if (epic.getId() <= 0) {
            throw new ValidationException("Invalid epic ID");
        }
        int newEpicId = generateId();
        epic.setId(newEpicId);
        epics.put(newEpicId, epic);
        findStartTimeAndDurationOfEpic(epic);
        return epic;
    }

    public Subtask addSubtask(Subtask subtask) {
        int newSubtaskId = generateId();

        if (newSubtaskId < 0) {
            throw new ValidationException("Invalid subtask ID");
        }

        subtask.setId(newSubtaskId);
        Epic epic = epics.get(subtask.getEpicId());

        if (epic != null) {
            validation(subtask);
            subtasks.put(newSubtaskId, subtask);
            epic.addSubtaskIds(newSubtaskId);
            updateStatusEpic(epic);
            findStartTimeAndDurationOfEpic(epic);
            prioritizedTasks.add(subtask);
            return subtask;
        } else {
            System.out.println("Epic not found");
            return null;
        }
    }


    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Task not found");
        }
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                prioritizedTasks.remove(subtasks.get(subtaskId));
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
            prioritizedTasks.remove(epics.get(id));

            epics.remove(id);
            //historyManager.remove(id);
        } else {
            System.out.println("Epic not found");
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIds().remove((Integer) subtask.getId());
            updateStatusEpic(epic);
            subtasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(subtask);
        } else {
            System.out.println("Subtask not found");
        }
    }

    @Override
    public void removeAllTasks() {
        if (!tasks.isEmpty()) {
            List<Integer> removeListTasks = new ArrayList<>(tasks.keySet());
            for (Integer removeListTask : removeListTasks) {
                removeTaskById(removeListTask);
            }
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        if (!epics.isEmpty()) {
            List<Integer> removeListEpics = new ArrayList<>(epics.keySet());
            for (Integer remove : removeListEpics) {
                removeEpicById(remove);
            }
        }
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void removeAllSubtasks() {
        if (!subtasks.isEmpty()) {
            List<Integer> removeListSubtasks = new ArrayList<>(subtasks.keySet());
            for (Integer remove : removeListSubtasks) {
                removeSubtaskById(remove);
            }
        }
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public List<Task> getAllTasks() {
        if (tasks.size() == 0) {
            return Collections.emptyList();
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        if (epics.size() == 0) {
            return Collections.emptyList();
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        if (subtasks.size() == 0) {
            return Collections.emptyList();
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasksByEpicId(int id) {
        if (epics.containsKey(id)) {
            List<Subtask> subtasksNew = new ArrayList<>();
            Epic epic = epics.get(id);
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasksNew.add(subtasks.get(subtaskId));
            }
            return subtasksNew;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            validation(task);
            prioritizedTasks.remove(tasks.get(task.getId()));
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Updated epic cannot be null");
        }
        String epicId = String.valueOf(epic.getId());
        if (epicId == null) {
            throw new IllegalArgumentException("Epic ID cannot be null");
        }
        if (epics.containsKey(epic.getId())) {
            Epic oldEpic = epics.get(epic.getId());
            oldEpic.setDescription(epic.getDescription());
            oldEpic.setName(epic.getName());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            validation(subtask);
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatusEpic(epic);
            findStartTimeAndDurationOfEpic(epic);
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void updateStatusEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubtaskIds().size() == 0) {
                epic.setStatus(Status.NEW);
            } else {
                int countDone = 0;
                int countNew = 0;

                for (int i = 0; i < epic.getSubtaskIds().size(); i++) {
                    if (subtasks.get(epic.getSubtaskIds().get(i)).getStatus() == Status.DONE) {
                        countDone++;
                    }
                    if (subtasks.get(epic.getSubtaskIds().get(i)).getStatus() == Status.NEW) {
                        countNew++;
                    }
                }

                if (countDone == epic.getSubtaskIds().size()) {
                    epic.setStatus(Status.DONE);
                } else if (countNew == epic.getSubtaskIds().size()) {
                    epic.setStatus(Status.NEW);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        }
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void setPrioritizedTasks(Task task) {
        prioritizedTasks.add(task);
    }

    @Override
    public void validation (Task task) {
        for (Task existTask : prioritizedTasks) {
            if (Objects.equals(task.getId(), existTask.getId())) {
                continue;
            }
            if (existTask.getStartTime() == null || task.getStartTime() == null) {
                break;
            }
            if(task.getEndTime().isBefore(existTask.getStartTime())
                    || task.getEndTime().equals(existTask.getStartTime())
                    || task.getStartTime().isAfter(existTask.getEndTime())
                    || task.getStartTime().equals(existTask.getStartTime())){
            } else {
                throw new ValidationException(task + " error validation: " + existTask);
            }

        }
    }

    public void addToHistory(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        } else if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
        } else if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
        }
    }

    @Override
    public void findStartTimeAndDurationOfEpic(Epic epic) {
        List<Integer> subTaskIds = epic.getSubtaskIds();

        LocalDateTime endTime = null;
        LocalDateTime startTime = null;
        int duration = 0;

        for(int id : subTaskIds){
            Subtask subTask = subtasks.get(id);
            if (subTask.getStartTime() != null) {
                if (startTime == null || startTime.isAfter(subTask.getStartTime())) {
                    startTime = subTask.getStartTime();
                }
            }
            if (subTask.getEndTime() != null) {
                if (endTime == null || endTime.isBefore(subTask.getEndTime())) {
                    endTime = subTask.getEndTime();
                }
            }
            duration += subTask.getDuration();
        }
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(duration);

    }

    public boolean isValidSubtaskId(int subtaskId) {

        return subtaskId > 0;
    }


    // Методы для тестов без сохраниния в историю просмотров
    public Subtask getSubtaskTest(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicTest(int id) {
        return epics.get(id);
    }

    public Task getTaskTest(int id) {
        return tasks.get(id);
    }

    @Override
    public Map<Integer, Subtask> getSubtasksList() {
        return subtasks;
    }

    @Override
    public Map<Integer, Epic> getEpicsList() {
        return epics;
    }

    @Override
    public Map<Integer, Task> getTaskList() {
        return tasks;
    }
}