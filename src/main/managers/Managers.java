package main.managers;

import main.http.HTTPTaskManager;



import main.http.KVServer;


public class Managers {
    public static TaskManager getInMemoryTaskManager(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static HTTPTaskManager getDefault(HistoryManager historyManager) {
        return new HTTPTaskManager(historyManager, "http://localhost:" + KVServer.PORT, true);
    }


}