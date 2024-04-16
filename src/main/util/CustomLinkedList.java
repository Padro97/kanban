package main.util;

import main.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomLinkedList<T extends Task> {

    private Node<T> first;
    private Node<T> last;
    private final HashMap<Integer, Node<T>> registry = new HashMap<>();

    public void linkLast(T last) {
        Node<T> lastNode = this.last;
        Node<T> newNode = new Node<>(last, null, lastNode);

        this.last = newNode;
        if (lastNode == null) {
            first = newNode;
        } else {
            lastNode.setNext(newNode);
        }
        this.registry.put(newNode.getData().getId(), newNode);

    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<T> node = first;

        while (node != null){
            tasks.add(node.getData());
            node = node.getNext();
        }
        return tasks;
    }

    public void remove(int id){
        if(registry.containsKey(id)){
            removeNode(registry.get(id));
        }
    }

    private void removeNode(Node<T> node) {

        Node<T> prevNode = node.getPrev();
        Node<T> nextNode = node.getNext();
        registry.remove(node.getData().getId());

        if (prevNode == null) {
            first = nextNode;
        } else {
            prevNode.setNext(nextNode);
            node.setPrev(null);
        }
        if (nextNode == null) {
            last = prevNode;
        } else {
            nextNode.setPrev(prevNode);
            node.setNext(null);
        }
    }
}