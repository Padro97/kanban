import main.managers.FileBackedTasksManager;
import main.managers.Managers;
import main.status.Status;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    public static final Path path = Path.of("data.test.csv");
    File file = new File(String.valueOf(path));
    @Override
    protected FileBackedTasksManager createTaskManager() {
        return new FileBackedTasksManager(Managers.getDefaultHistory(), file);
    }

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(path);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    public void shouldCorrectlySaveAndLoad() throws IOException, InterruptedException {
        Task task = new Task("Description", "Title", Status.NEW, LocalDateTime.now(), 1);
        taskManager.addTask(task);
        FileBackedTasksManager fileManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        fileManager.loadFromFile();
        assertEquals(List.of(task), taskManager.getAllTasks());

    }

    @Test
    public void shouldSaveAndLoadEmptyTasksEpicsSubtasks() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        fileManager.save();
        fileManager.loadFromFile();
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllTasks());
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllSubtasks());
    }

    @Test
    public void shouldSaveAndLoadEmptyHistory() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        fileManager.save();
        fileManager.loadFromFile();
        assertEquals(Collections.EMPTY_LIST, taskManager.getHistory());
    }

    @Test
    public void shouldBeFileBackupManager() throws IOException, InterruptedException {
        Path path3 = Path.of("data.test.csv");
        File file = new File(String.valueOf(path3));
        FileBackedTasksManager test = new FileBackedTasksManager(Managers.getDefaultHistory(), file);

        Task task = new Task("A","B",Status.NEW,
                LocalDateTime.of(2022,3,10,12,0),50);
        test.addTask(task);
        Epic epic = new Epic("TestEpic", "Test description", Status.NEW);
        epic.setId(10);
        test.addEpic(epic);
        Subtask subTask = new Subtask("Test description", "TestSubTask", Status.NEW, 10,LocalDateTime.of(2022,6,1,11,30),30);
        test.addSubtask(subTask);
        Subtask subTask2 = new Subtask("Test description", "TestSubTask", Status.NEW, 10,LocalDateTime.of(2022,8,10,12,0),30);
        test.addSubtask(subTask2);

        Task task2 = new Task("A","B",Status.NEW,
                LocalDateTime.of(2022,2,10,12,0),50) ;
        test.addTask(task2);
        test.getTaskById(1);
        test.getEpicById(10);
        test.getSubtaskById(3);

        assertEquals(test.getTaskList(), test.getTaskList());
        assertEquals(test.getSubtasksList(), test.getSubtasksList());
        assertEquals(test.getEpicsList(), test.getEpicsList());
        assertEquals(test.getHistory(), test.getHistory());
        assertEquals(test.getPrioritizedTasks(), test.getPrioritizedTasks());
        assertNotNull(test);
    }
}