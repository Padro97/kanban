package main.http.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import main.managers.TaskManager;
import main.tasks.Task;

import java.io.IOException;

public class TaskHandler extends BaseHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        int statusCode;
        String method = httpExchange.getRequestMethod();
        String path = String.valueOf(httpExchange.getRequestURI());

        System.out.println("Обрабатывается запрос " + path + " с методом " + method);

        switch (method) {
            case "GET":
                String query = httpExchange.getRequestURI().getQuery();
                if (query == null) {
                    statusCode = 200;
                    String jsonString = gson.toJson(taskManager.getAllTasks());
                    System.out.println("GET TASKS: " + jsonString);
                    response = gson.toJson(jsonString);
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Task task = taskManager.getTaskById(id);
                        if (task != null) {
                            statusCode = 200;
                            response = gson.toJson(task);
                        } else {
                            statusCode = 404;
                            httpExchange.sendResponseHeaders(statusCode, 0);
                            response = "Задача с данным id не найдена";
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "В запросе отсутствует необходимый параметр id";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "Неверный формат id";
                    }
                }
                break;
            case "POST":
                String bodyRequest = readText(httpExchange);
                if(bodyRequest.isEmpty()){
                    statusCode = 400;
                    httpExchange.sendResponseHeaders(statusCode, 0);
                    return;
                }
                try {
                    Task task = gson.fromJson(bodyRequest, Task.class);
                    Integer id = task.getId();
                    if (task != null) {
                        taskManager.updateTask(task);
                        statusCode = 201;
                        response = "Задача с id=" + id + " обновлена";
                    } else {
                        Task taskCreated = taskManager.addTask(task);
                        System.out.println("CREATED TASK: " + taskCreated);
                        int idCreated = taskCreated.getId();
                        statusCode = 201;
                        response = "Создана задача с id=" + idCreated;
                    }
                } catch (JsonSyntaxException | InterruptedException e) {
                    statusCode = 400;
                    response = "Неверный формат запроса";
                }
                break;
            case "DELETE":
                response = "";
                query = httpExchange.getRequestURI().getQuery();
                if (query == null) {
                    try {
                        taskManager.removeAllTasks();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    statusCode = 200;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.removeTaskById(id);
                        statusCode = 200;
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "В запросе отсутствует необходимый параметр id";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "Неверный формат id";
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
            default:
                statusCode = 405;
                response = "Некорректный запрос";
        }

        httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
        httpExchange.sendResponseHeaders(statusCode, 0);
        writers(httpExchange);
    }
}