package main.managers;

import main.exceptions.ManagerSaveException;
import main.status.Status;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.util.TaskType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;



public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;
    private static final String HEADER_CSV_FILE = "id,type,name,status,description,starttime,endtime,duration,epic\n";
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");

    public FileBackedTasksManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
        this.epics = new HashMap<>();
    }

    public FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public void loadFromFile() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {

            String line = bufferedReader.readLine();
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if (line.isEmpty()) {
                    break;
                }
                Task task = fromString(line);
                if(task.getId() > super.id) {super.id = task.getId();}

                switch (task.getType()){
                    case EPIC:
                        epics.put(task.getId(), (Epic) task);
                        findStartTimeAndDurationOfEpic((Epic) task);
                        break;
                    case SUBTASK:
                            Epic epic = epics.get(((Subtask) task).getEpicId());

                        if (epic != null) {
                            subtasks.put(task.getId(), (Subtask) task);
                            epic.addSubtaskIds(task.getId());
                            getPrioritizedTasks().add(task);
                            updateStatusEpic(epic);
                            findStartTimeAndDurationOfEpic(epic);
                        } else {
                            System.out.println("Epic not found");
                        }
                        break;
                    default:
                        tasks.put(task.getId(), task);
                        getPrioritizedTasks().add(task);

                }
            }

            String lineWithHistory = bufferedReader.readLine();
            for (int id : historyFromString(lineWithHistory)) {
                addToHistory(id);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Could not read data from file");
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEADER_CSV_FILE);
            if (super.getAllTasks().isEmpty()){
                writer.write("");
            }else{
                for(Task task :super.getAllTasks()){
                    writer.write(task.toString());
                }
            }
            if(super.getAllEpics().isEmpty()){
                writer.write("");
            }else{
                for(Epic task :super.getAllEpics()){
                    writer.write(task.toString());
                }
            }
            if(super.getAllSubtasks().isEmpty()) {
                writer.write("");
            }else{
                for(Subtask task :super.getAllSubtasks()){
                    writer.write(task.toString());
                }
            }

            writer.write("\n");
            saveHistory(writer);

        }catch (ManagerSaveException | IOException e){
            e.printStackTrace();
        }
    }

    public FileWriter saveHistory(FileWriter writer) throws IOException {
        for (int i = 0; i < super.getHistory().size(); i++) {
            if(i+1 != super.getHistory().size()){
                writer.write(super.getHistory().get(i).getId() + ",");
            }else {
                writer.write(String.valueOf(super.getHistory().get(i).getId()));
            }
        }
        return writer;
    }

    private Task fromString(String value) {
        String[] params = value.split(",");

        switch (TaskType.valueOf(params[1])){
            case EPIC:
                Epic epic = new Epic(params[4], params[2], Status.valueOf(params[3]));
                epic.setId(Integer.parseInt(params[0]));
                epic.setStatus(Status.valueOf(params[3]));
                return epic;
            case SUBTASK:
                Subtask subtask = null;
                try {
                subtask = new Subtask(params[4], params[2], Status.valueOf(params[3]), Integer.parseInt(params[8]),
                        LocalDateTime.parse(params[5],FORMATTER), Integer.parseInt(params[7]));
                subtask.setId(Integer.parseInt(params[0]));
                return subtask;
                } catch (DateTimeParseException e) {
                    System.out.println("Create Subtask - error, data equals null");
                    return subtask;
                }
            default:
                Task task = null;
                try {
                    task = new Task(params[4], params[2], Status.valueOf(params[3]), LocalDateTime.parse(params[5], FORMATTER), Integer.parseInt(params[7]));
                    task.setId(Integer.parseInt(params[0]));
                    return task;
                } catch (DateTimeParseException e) {
                    System.out.println("Create Task - error, data equals null");
                    return task;
                }
        }
    }

    @Override
    public Task addTask(Task task)  {
        Task newTask = super.addTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic newEpic = super.addEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask newSubtask = super.addSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> toReturn = new ArrayList<>();
        if (value != null) {
            String[] id = value.split(",");

            for (String number : id) {
                toReturn.add(Integer.parseInt(number));
            }
        }
        return toReturn;
    }
}


