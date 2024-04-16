import main.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.managers.TaskManager;
import main.status.Status;
import main.tasks.Task;
import main.tasks.Subtask;
import main.tasks.Epic;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;



import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    @BeforeEach
    public void initTaskManager() {
        taskManager = createTaskManager();
    }

    protected abstract T createTaskManager();



    @Test
    void testAddTask() {
        TaskManager taskManager = createTaskManager();
        Task task = new Task("Task1", "Description", Status.NEW,
                LocalDateTime.now().minusDays(1), 1);

        Task addedTask = null;
        try {
            addedTask = taskManager.addTask(task);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertEquals(task, addedTask);
        assertTrue(taskManager.getAllTasks().contains(task));
    }

    @Test
    public void addTask_emptyTaskList() {
        Task task = new Task("Test Task", "Description", Status.NEW, null, 0);
        Task addedTask = null;
        try {
            addedTask = taskManager.addTask(task);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertEquals(task, addedTask);

        try {
            taskManager.removeAllTasks();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertEquals(0, taskManager.getAllTasks().size());
    }



    @Test
    void testAddEpic() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Epic1", "Description", Status.IN_PROGRESS);
        epic.setId(1);
        Epic addedEpic = null;
        try {
            addedEpic = taskManager.addEpic(epic);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertEquals(epic, addedEpic);
        assertTrue(taskManager.getAllEpics().contains(epic));
    }

    @Test
    void testAddEpicWithEmptyList() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Epic1", "Description", Status.IN_PROGRESS);
        epic.setId(1);
        try {
            Epic addedEpic = taskManager.addEpic(epic);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            taskManager.removeAllEpics();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertEquals(0, taskManager.getAllEpics().size());
    }
    @Test
    void testAddEpicWithInvalidEpicId() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Epic1", "Description", Status.IN_PROGRESS);
        epic.setId(-1);

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> taskManager.addEpic(epic));

        assertEquals("Invalid epic ID", validationException.getMessage());
    }


    @Test
    void testAddSubtask() {

        Epic epic = new Epic("Epic Description", "Sample Epic", Status.NEW);
        epic.setId(1);
        try {
            taskManager.addEpic(epic);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Subtask subtask = new Subtask("Sample Subtask", "Subtask Description", Status.NEW, 1, LocalDateTime.now(), 1);

        try {
            taskManager.addSubtask(subtask);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertTrue(taskManager.getAllSubtasks().contains(subtask));
    }

    @Test
    void testAddSubTaskWithEmptyList() {
        Epic epic = new Epic("Epic Description", "Sample Epic", Status.NEW);
        epic.setId(1);

        try {
            taskManager.addEpic(epic);
            Subtask subtask = new Subtask("Sample Subtask", "Subtask Description", Status.NEW, 1, LocalDateTime.now(), 1);
            taskManager.addSubtask(subtask);
            taskManager.removeAllSubtasks();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertEquals(0, taskManager.getAllSubtasks().size());
    }


    @Test
    void testRemoveTaskById() {
        TaskManager taskManager = createTaskManager();

        // Standard behavior
        Task task1 = new Task("Task1", "Description", Status.NEW,
                LocalDateTime.now().minusDays(1), 1);
        try {
            taskManager.addTask(task1);
            taskManager.removeTaskById(task1.getId());
            assertTrue(taskManager.getAllTasks().isEmpty());

            // Empty task list
            taskManager.removeTaskById(1);
            assertTrue(taskManager.getAllTasks().isEmpty());

            // Invalid task ID
            Task task2 = new Task("Task2", "Description", Status.NEW,
                    LocalDateTime.now().minusDays(1), 2);
            taskManager.addTask(task2);
            taskManager.removeTaskById(3);
            assertFalse(taskManager.getAllTasks().isEmpty());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void testRemoveEpicByIdStandardBehavior() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Epic1", "Description", Status.IN_PROGRESS);
        epic.setId(1);
        try {
            taskManager.addEpic(epic);
            taskManager.removeEpicById(epic.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void testRemoveEpicByIdEmptyEpicList() {
        TaskManager taskManager = createTaskManager();
        try {
            taskManager.removeEpicById(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void testRemoveEpicByIdInvalidEpicId() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Epic2", "Description", Status.IN_PROGRESS);
        epic.setId(1);
        try {
            taskManager.addEpic(epic);
            taskManager.removeEpicById(3);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertFalse(taskManager.getAllEpics().isEmpty());
    }


    @Test
    void testRemoveSubtaskByIdStandardBehavior() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Epic1", "Description", Status.IN_PROGRESS);
        epic.setId(1);
        Subtask subtask = new Subtask("Subtask1", "Description", Status.NEW, 1,
                LocalDateTime.now().plusDays(1), 2);
        try {
            taskManager.addEpic(epic);
            taskManager.addSubtask(subtask);
            taskManager.removeSubtaskById(subtask.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void testRemoveSubtaskByIdEmptySubtaskList() {
        TaskManager taskManager = createTaskManager();
        try {
            taskManager.removeSubtaskById(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void testRemoveSubtaskByIdInvalidSubtaskId() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Epic2", "Description", Status.IN_PROGRESS);
        epic.setId(1);
        Subtask subtask = new Subtask("Subtask2", "Description", Status.NEW, 1,
                LocalDateTime.now().plusDays(1), 2);
        try {
            taskManager.addEpic(epic);
            taskManager.addSubtask(subtask);
            taskManager.removeSubtaskById(3);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertFalse(taskManager.getAllSubtasks().isEmpty());
    }



    @Test
    void testRemoveAllTasks() {
        TaskManager taskManager = createTaskManager();
        Task task1 = new Task("Task1", "Description", Status.NEW,
                LocalDateTime.now().minusDays(1), 1);
        Task task2 = new Task("Task2", "Description", Status.IN_PROGRESS,
                LocalDateTime.now().plusDays(2), 3);
        try {
            taskManager.addTask(task1);
            taskManager.addTask(task2);

            // Standard behavior
            taskManager.removeAllTasks();
            assertTrue(taskManager.getAllTasks().isEmpty());

            // Empty task list
            taskManager.removeAllTasks();
            assertTrue(taskManager.getAllTasks().isEmpty());

            // Invalid task ID
            taskManager.addTask(task1);
            taskManager.removeAllTasks();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void testRemoveAllEpics() {
        TaskManager taskManager = createTaskManager();
        Epic epic1 = new Epic("Epic1", "Description", Status.IN_PROGRESS);
        Epic epic2 = new Epic("Epic2", "Description", Status.DONE);
        epic1.setId(1);
        epic2.setId(2);
        try {
            taskManager.addEpic(epic1);
            taskManager.addEpic(epic2);

            // Standard behavior
            taskManager.removeAllEpics();
            assertTrue(taskManager.getAllEpics().isEmpty());

            // Empty epic list
            taskManager.removeAllEpics();
            assertTrue(taskManager.getAllEpics().isEmpty());

            // Invalid epic ID
            taskManager.addEpic(epic1);
            taskManager.removeAllEpics();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void testRemoveAllSubtasks() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Epic1", "Description", Status.IN_PROGRESS);
        epic.setId(1);
        try {
            taskManager.addEpic(epic);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Subtask subtask1 = new Subtask("Subtask1", "Description", Status.NEW, 1,
                LocalDateTime.now().plusDays(1), 2);
        Subtask subtask2 = new Subtask("Subtask2", "Description", Status.IN_PROGRESS, 1,
                LocalDateTime.now().plusDays(3), 4);
        try {
            taskManager.addSubtask(subtask1);
            taskManager.addSubtask(subtask2);

            // Standard behavior
            taskManager.removeAllSubtasks();
            assertTrue(taskManager.getAllSubtasks().isEmpty());

            // Empty subtask list
            taskManager.removeAllSubtasks();
            assertTrue(taskManager.getAllSubtasks().isEmpty());

            // Invalid subtask ID
            taskManager.addSubtask(subtask1);
            taskManager.removeAllSubtasks();
            assertTrue(taskManager.getAllSubtasks().isEmpty());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetTaskByIdStandardBehavior() {
        TaskManager taskManager = createTaskManager();
        Task task = new Task("Task1", "Description", Status.NEW,
                LocalDateTime.now().minusDays(1), 1);
        try {
            taskManager.addTask(task);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertEquals(task, retrievedTask);
    }

    @Test
    void testGetTaskByIdEmptyTaskList() {
        TaskManager taskManager = createTaskManager();
        Task retrievedTask = taskManager.getTaskById(1);
        assertNull(retrievedTask);
    }

    @Test
    void testGetTaskByIdInvalidTaskId() {
        TaskManager taskManager = createTaskManager();
        Task task = new Task("Task2", "Description", Status.NEW,
                LocalDateTime.now().minusDays(1), 2);
        try {
            taskManager.addTask(task);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Task retrievedTask = taskManager.getTaskById(3);
        assertNull(retrievedTask);
    }


    @Test
    void testGetEpicByIdStandardBehavior() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Epic1", "Description", Status.IN_PROGRESS);
        epic.setId(1);
        try {
            taskManager.addEpic(epic);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Epic retrievedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(epic, retrievedEpic);
    }

    @Test
    void testGetEpicByIdEmptyEpicList() {
        TaskManager taskManager = createTaskManager();
        Epic retrievedEpic = taskManager.getEpicById(1);
        assertNull(retrievedEpic);
    }

    @Test
    void testGetEpicByIdInvalidEpicId() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Epic2", "Description", Status.IN_PROGRESS);
        epic.setId(2);
        try {
            taskManager.addEpic(epic);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Epic retrievedEpic = taskManager.getEpicById(3);
        assertNull(retrievedEpic);
    }


    @Test
    void testGetSubtaskByIdStandardBehavior() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Epic1", "Description", Status.IN_PROGRESS);
        epic.setId(1);
        try {
            taskManager.addEpic(epic);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Subtask subtask = new Subtask("Subtask1", "Description", Status.NEW, 1,
                LocalDateTime.now().plusDays(1), 2);
        try {
            taskManager.addSubtask(subtask);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertEquals(subtask, retrievedSubtask);
    }

    @Test
    void testGetSubtaskByIdEmptySubtaskList() {
        TaskManager taskManager = createTaskManager();
        Subtask retrievedSubtask = taskManager.getSubtaskById(1);
        assertNull(retrievedSubtask);
    }

    @Test
    void testGetSubtaskByIdInvalidSubtaskId() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Epic2", "Description", Status.IN_PROGRESS);
        epic.setId(2);
        try {
            taskManager.addEpic(epic);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Subtask subtask = new Subtask("Subtask2", "Description", Status.NEW, 2,
                LocalDateTime.now().plusDays(1), 3);
        try {
            taskManager.addSubtask(subtask);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Subtask retrievedSubtask = taskManager.getSubtaskById(4);
        assertNull(retrievedSubtask);
    }


    @Test
    void testUpdateTask() {
        TaskManager taskManager = createTaskManager();
        Task task = new Task("Task1", "Description", Status.NEW,
                LocalDateTime.now().minusDays(1), 1);
        task.setId(1);
        try {
            taskManager.addTask(task);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Standard behavior
        task.setDescription("Updated Description");
        task.setStatus(Status.IN_PROGRESS);
        try {
            taskManager.updateTask(task);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals(task, updatedTask);
    }

    @Test
    void testUpdateTaskWithInvalidId() {

        // Invalid task ID
        Task invalidTask = new Task("InvalidTask", "Description", Status.IN_PROGRESS,
                LocalDateTime.now().plusDays(1), 3);
        invalidTask.setId(-1);
        try {
            taskManager.updateTask(invalidTask);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertNull(taskManager.getTaskById(invalidTask.getId()));
    }

    @Test
    void testUpdateTaskWithEmptyList() {
        // Empty task list
        Task emptyTask = new Task("EmptyTask", "Description", Status.NEW,
                LocalDateTime.now().minusDays(1), 2);
        emptyTask.setId(2);
        try {
            taskManager.updateTask(emptyTask);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Task retrievedTask = taskManager.getTaskById(emptyTask.getId());
        assertNull(retrievedTask, "Non-existent task.");
    }


    @Test
    void testUpdateEpic() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Epic1", "Description", Status.IN_PROGRESS);
        epic.setId(1);
        try {
            taskManager.addEpic(epic);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Standard behavior
        epic.setStatus(Status.DONE);
        taskManager.updateEpic(epic);
        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(epic, updatedEpic);

    }

    @Test
    void testUpdateEpicWithEmptyList() {
        Epic epic = new Epic("Epic1", "Description", Status.IN_PROGRESS);
        epic.setId(1);
        try {
            taskManager.addEpic(epic);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        epic.setDescription("Updated Description");
        epic.setStatus(Status.DONE);
        taskManager.updateEpic(epic);
        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(epic, updatedEpic);
    }






    @Test
    void testUpdateSubtaskStandardBehavior() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Epic1", "Description", Status.IN_PROGRESS);
        epic.setId(1);
        try {
            taskManager.addEpic(epic);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Subtask subtask = new Subtask("Subtask1", "Description", Status.NEW, 1,
                LocalDateTime.now().plusDays(1), 2);
        try {
            taskManager.addSubtask(subtask);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Standard behavior
        subtask.setDescription("Updated Description");
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        Subtask updatedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertEquals(subtask, updatedSubtask);
    }

    @Test
    void testUpdateSubtaskWithEmptySubtaskList() {
        Epic epic = new Epic("Epic1", "Description", Status.IN_PROGRESS);
        epic.setId(1);
        try {
            taskManager.addEpic(epic);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Subtask emptySubtask = new Subtask("EmptySubtask", "Description", Status.NEW, 1,
                LocalDateTime.now().plusDays(1), 3);

        taskManager.updateSubtask(emptySubtask);
        assertNotNull(emptySubtask.getDescription());
    }



    @Test
    void getAllTasksStandardCase() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, LocalDateTime.now(), 1);

        try {
            taskManager.addTask(task1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(1, tasks.size());
    }

    @Test
    void getAllTasks_emptyList() {
        List<Task> tasks = taskManager.getAllTasks();

        assertTrue(tasks.isEmpty());
    }


    @Test
    void getAllEpics_standardCase() {
        Epic epic1 = new Epic("Epic 1", "Description 1", Status.NEW);
        Epic epic2 = new Epic("Epic 2", "Description 2", Status.IN_PROGRESS);
        epic1.setId(1);
        epic2.setId(2);
        try {
            taskManager.addEpic(epic1);
            taskManager.addEpic(epic2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<Epic> epics = taskManager.getAllEpics();

        assertEquals(2, epics.size());
        assertTrue(epics.contains(epic1));
        assertTrue(epics.contains(epic2));
    }

    @Test
    void getAllEpics_emptyList() {
        List<Epic> epics = taskManager.getAllEpics();

        assertTrue(epics.isEmpty());
    }

    @Test
    void testGetAllTasksWithInvalidId() {
        TaskManager taskManager = createTaskManager();

        List<Task> tasksBefore = taskManager.getAllTasks();
        Task validTask = new Task("ValidTask", "Description", Status.NEW,
                LocalDateTime.now().minusDays(1), 1);
        try {
            taskManager.addTask(validTask);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<Task> tasksAfter = taskManager.getAllTasks();
        assertTrue(tasksBefore.isEmpty());
        assertEquals(1, tasksAfter.size());
        assertEquals(validTask, tasksAfter.get(0));
    }



    @Test
    void getAllSubtasks_standardCase() {
        Epic epic1 = new Epic("Epic 1", "Description 1", Status.NEW);
        epic1.setId(1);
        try {
            taskManager.addEpic(epic1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, 1, LocalDateTime.now(), 2);

        try {
            taskManager.addSubtask(subtask1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertEquals(1, subtasks.size());

    }

    @Test
    void getAllSubtasks_emptyList() {
        List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertTrue(subtasks.isEmpty());
    }

    @Test
    void getAllSubtasksWithInvalidId() {
        TaskManager taskManager = createTaskManager();
        Epic epic = new Epic("Epic1", "Description", Status.IN_PROGRESS);
        epic.setId(1);
        try {
            taskManager.addEpic(epic);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Subtask invalidSubtask = new Subtask("InvalidSubtask", "Description", Status.NEW, 123,
                LocalDateTime.now().plusDays(1), 3);
        try {
            taskManager.addSubtask(invalidSubtask);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<Subtask> subtasksWithInvalidId = taskManager.getAllSubtasks();

        assertTrue(subtasksWithInvalidId.isEmpty(), "Subtasks with invalid ID should be empty");

        List<Subtask> allSubtasks = taskManager.getAllSubtasks();
        assertEquals(0, allSubtasks.size(), "No subtasks should be present");
    }

    @Test
    void getAllSubtasksByEpicId_standardCase() {
        Epic epic = new Epic("Epic", "Description", Status.NEW);
        epic.setId(1);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, 1, LocalDateTime.now(), 2);


        try {
            taskManager.addEpic(epic);
            taskManager.addSubtask(subtask1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        List<Subtask> subtasks = taskManager.getAllSubtasksByEpicId(epic.getId());

        assertEquals(1, subtasks.size());

    }

    @Test
    void getAllSubtasksByEpicId_emptyList() {
        Epic epic = new Epic("Epic", "Description", Status.NEW);
        epic.setId(1);

        List<Subtask> subtasks = taskManager.getAllSubtasksByEpicId(epic.getId());

        assertTrue(subtasks.isEmpty());
    }

    @Test
    void getAllSubtasksByEpicId_invalidEpicId() {
        TaskManager taskManager = createTaskManager();
        List<Subtask> subtasksByInvalidEpicId = taskManager.getAllSubtasksByEpicId(-1);
        assertTrue(subtasksByInvalidEpicId.isEmpty(), "List should be empty for an invalid epic ID");
    }

}