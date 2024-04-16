package main.managers;

import main.tasks.Task;
import main.util.CustomLinkedList;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> list;

    public InMemoryHistoryManager() {
        this.list = new CustomLinkedList<>();
    }
    @Override
    public void add(Task task){
        if(task != null) {
            remove(task.getId());
            list.linkLast(task);
        } else {
            System.out.println("Non-existent task.");
        }
    }

    @Override
    public void remove(int id){
        list.remove(id);
    }
    @Override
    public List<Task> getHistory(){
        return list.getTasks();
    }
}

