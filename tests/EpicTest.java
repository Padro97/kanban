
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

import main.status.Status;
import main.managers.InMemoryTaskManager;
import main.tasks.Epic;
import main.tasks.Subtask;



class EpicTest {
    InMemoryTaskManager taskManagerOne = new InMemoryTaskManager();
    Epic epic1;
    Subtask subTask1;
    Subtask subTask2;
    @BeforeEach
    void beforeEach(){
        epic1 = new Epic("Test description", "TestEpic", Status.NEW);
        taskManagerOne.addEpic(epic1);
        subTask1 = new Subtask("Test description", "TestSubTask", Status.NEW, epic1.getId(),LocalDateTime.of(2022,8,9,12,0),30);
        taskManagerOne.addSubtask(subTask1);
        subTask2 = new Subtask("Test description", "TestSubTask", Status.NEW, epic1.getId(),LocalDateTime.of(2022,8,10,12,0),30);
        taskManagerOne.addSubtask(subTask2);
    }

    @Test
    void addNewEpic() {
        Epic epic2 = new Epic("Test description", "TestEpic", Status.NEW);
        taskManagerOne.addEpic(epic2);
        taskManagerOne.updateStatusEpic(epic2);
        assertEquals(epic2.getStatus(),Status.NEW,"Неверно обновляется статус пустого эпика");
    }

    @Test
    void addEpicWithSubTasksNew(){
        assertNotNull(taskManagerOne.getSubtaskTest(subTask1.getId()),"Подзадачи не возвращаются.");
        assertNotNull(taskManagerOne.getSubtaskTest(subTask2.getId()),"Подзадачи не возвращаются.");
        assertEquals(taskManagerOne.getAllSubtasks().size(),2,"Неверное количество подзадач");
        assertEquals(subTask1.getEpicId(),epic1.getId(),"Id эпика подзадачи не равно id эпика");
        assertEquals(epic1.getStatus(),Status.NEW,"Неверно обновляется статус эпика");
    }

    @Test
    void addEpicWithSubTasksDone(){
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        taskManagerOne.updateStatusEpic(epic1);
        assertNotNull(taskManagerOne.getSubtaskTest(subTask1.getId()),"Подзадачи не возвращаются.");
        assertNotNull(taskManagerOne.getSubtaskTest(subTask2.getId()),"Подзадачи не возвращаются.");
        assertEquals(taskManagerOne.getAllSubtasks().size(),2,"Неверное количество подзадач");
        assertEquals(subTask1.getEpicId(),epic1.getId(),"Id эпика подзадачи не равно id эпика");
        assertEquals(epic1.getStatus(),Status.DONE,"Неверно обновляется статус эпика");
    }

    @Test
    void addEpicWithSubTasksNewAndDone(){
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.NEW);
        taskManagerOne.updateStatusEpic(epic1);
        assertNotNull(taskManagerOne.getSubtaskTest(subTask1.getId()),"Подзадачи не возвращаются.");
        assertNotNull(taskManagerOne.getSubtaskTest(subTask2.getId()),"Подзадачи не возвращаются.");
        assertEquals(taskManagerOne.getAllSubtasks().size(),2,"Неверное количество подзадач");
        assertEquals(subTask1.getEpicId(),epic1.getId(),"Id эпика подзадачи не равно id эпика");
        assertEquals(epic1.getStatus(),Status.IN_PROGRESS,"Неверно обновляется статус эпика");
    }

    @Test
    void addEpicWithSubTasksInProgress(){
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.IN_PROGRESS);
        taskManagerOne.updateStatusEpic(epic1);
        assertNotNull(taskManagerOne.getSubtaskTest(subTask1.getId()),"Подзадачи не возвращаются.");
        assertNotNull(taskManagerOne.getSubtaskTest(subTask2.getId()),"Подзадачи не возвращаются.");
        assertEquals(taskManagerOne.getAllSubtasks().size(),2,"Неверное количество подзадач");
        assertEquals(subTask1.getEpicId(),epic1.getId(),"Id эпика подзадачи не равно id эпика");
        assertEquals(epic1.getStatus(),Status.IN_PROGRESS,"Неверно обновляется статус эпика");
    }


}