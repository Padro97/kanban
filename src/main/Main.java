package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.adapters.LocalDateTimeAdapter;
import main.http.KVServer;
import main.managers.HistoryManager;
import main.managers.Managers;
import main.managers.TaskManager;
import main.status.Status;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {

        KVServer server;
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();

            server = new KVServer();
            server.start();
            HistoryManager historyManager = Managers.getDefaultHistory();
            TaskManager httpTaskManager = Managers.getDefault(historyManager);

            Task task1 = new Task("DS", "Task_3", Status.NEW,
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 10)), 40);
            httpTaskManager.addTask(task1);

            Epic epic1 = new Epic("DS", "Epic_3", Status.NEW);
            httpTaskManager.addEpic(epic1);

            Subtask subtask1 = new Subtask("DS", "Subtask_4", Status.NEW, epic1.getId(),
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 10)), 40);
            httpTaskManager.addSubtask(subtask1);


            httpTaskManager.getTaskById(task1.getId());
            httpTaskManager.getEpicById(epic1.getId());
            httpTaskManager.getSubtaskById(subtask1.getId());

            System.out.println("Печать всех задач");
            System.out.println(gson.toJson(httpTaskManager.getAllTasks()));
            System.out.println("Печать всех эпиков");
            System.out.println(gson.toJson(httpTaskManager.getAllEpics()));
            System.out.println("Печать всех подзадач");
            System.out.println(gson.toJson(httpTaskManager.getAllSubtasks()));
            System.out.println("Загруженный менеджер");
            System.out.println(httpTaskManager);
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}