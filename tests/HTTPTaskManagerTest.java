import main.http.HTTPTaskManager;
import main.http.KVServer;
import main.managers.HistoryManager;
import main.managers.Managers;
import main.managers.TaskManager;
import main.status.Status;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HTTPTaskManagerTest<T extends TaskManagerTest<HTTPTaskManager>> {
    private KVServer server;
    private TaskManager manager;

    private HTTPTaskManager httpTaskManager;

    @BeforeEach
    public void createManager() throws IOException, InterruptedException {
            server = new KVServer();
            server.start();
            HistoryManager historyManager = Managers.getDefaultHistory();
            manager = Managers.getDefault(historyManager);

    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void shouldLoadTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test description 1", "TestTask 1",Status.NEW,
                LocalDateTime.of(2022,1,1,1,0),10);
        Task task2 = new Task ("Test description 2", "TestTask 2",Status.NEW,
                LocalDateTime.of(2022,4,1,1,5),10);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllTasks(), list);

        HistoryManager historyManager = Managers.getDefaultHistory();
        httpTaskManager = new HTTPTaskManager(historyManager, "http://localhost:" + KVServer.PORT, true);
        assertEquals(manager.getTaskList(), httpTaskManager.getTaskList(),
                "The list of tasks after unloading does not match");
        assertEquals(manager.getHistory(), httpTaskManager.getHistory(),
                "The list of history after unloading does not match");
        assertEquals(manager.getPrioritizedTasks(), httpTaskManager.getPrioritizedTasks(),
                "The list of prioritized tasks after unloading does not match");
        assertEquals(manager.getId(), httpTaskManager.getId(), "The id value does not match");

    }

    @Test
    public void shouldLoadEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("TestEpic 1", "Test description 1", Status.NEW);
        Epic epic2 = new Epic("TestEpic 2", "Test description 2", Status.NEW);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.getEpicById(epic1.getId());
        manager.getEpicById(epic2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllEpics(), list);

        HistoryManager historyManager = Managers.getDefaultHistory();
        httpTaskManager = new HTTPTaskManager(historyManager, "http://localhost:" + KVServer.PORT, true);
        assertEquals(manager.getEpicsList(), httpTaskManager.getEpicsList(),
                "The list of epics after unloading does not match");
        assertEquals(manager.getHistory(), httpTaskManager.getHistory(),
                "The list of history after unloading does not match");
        assertEquals(manager.getId(), httpTaskManager.getId(), "The id value does not match");
    }

    @Test
    public void shouldLoadSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("TestEpic", "Test description", Status.NEW);
        Subtask subtask1 = new Subtask("Test description", "TestSubTask", Status.NEW, epic1.getId(),
                LocalDateTime.of(2022,8,10,12,0),30);
        Subtask subtask2 = new Subtask("Test description", "TestSubTask", Status.NEW, epic1.getId(),
                LocalDateTime.of(2022,11,10,12,0),30);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllSubtasks(), list);

        HistoryManager historyManager = Managers.getDefaultHistory();
        httpTaskManager = new HTTPTaskManager(historyManager, "http://localhost:" + KVServer.PORT, true);
        assertEquals(manager.getSubtasksList(), httpTaskManager.getSubtasksList(),
                "The list of subtasks after unloading does not match");
        assertEquals(manager.getHistory(), httpTaskManager.getHistory(),
                "The list of history after unloading does not match");
        assertEquals(manager.getPrioritizedTasks(), httpTaskManager.getPrioritizedTasks(),
                "The list of prioritized tasks after unloading does not match");
        assertEquals(manager.getId(), httpTaskManager.getId(), "The id value does not match");
    }
}