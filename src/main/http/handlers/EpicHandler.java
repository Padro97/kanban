package main.http.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import main.managers.TaskManager;
import main.tasks.Epic;

import java.io.IOException;

public class EpicHandler extends BaseHandler {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode;
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                String query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    statusCode = 200;
                    String jsonString = gson.toJson(taskManager.getAllEpics());
                    System.out.println("GET EPICS: " + jsonString);
                    response = gson.toJson(jsonString);
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Epic epic = taskManager.getEpicById(id);
                        if (epic != null) {
                            statusCode = 200;
                            response = gson.toJson(epic);
                        } else {
                            statusCode = 404;
                            exchange.sendResponseHeaders(statusCode, 0);
                            response = "���� � ������ id �� ������";
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "� ������� ����������� ����������� �������� id";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "�������� ������ id";
                    }
                }
                break;
            case "POST":
                String bodyRequest = readText(exchange);
                if(bodyRequest.isEmpty()){
                    statusCode = 400;
                    exchange.sendResponseHeaders(statusCode, 0);
                    return;
                }
                try {
                    Epic epic = gson.fromJson(bodyRequest, Epic.class);
                    Integer id = epic.getId();
                    if (epic != null) {
                        taskManager.updateTask(epic);
                        statusCode = 200;
                        response = "���� � id=" + id + " ��������";
                    } else {
                        System.out.println("CREATED");
                        Epic epicCreated = taskManager.addEpic(epic);
                        System.out.println("CREATED EPIC: " + epicCreated);
                        int idCreated = epicCreated.getId();
                        statusCode = 201;
                        response = "������ ���� � id=" + idCreated;
                    }
                } catch (JsonSyntaxException | InterruptedException e) {
                    statusCode = 400;
                    response = "�������� ������ �������";
                }
                break;
            case "DELETE":
                response = "";
                query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    try {
                        taskManager.removeAllEpics();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    statusCode = 200;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.removeEpicById(id);
                        statusCode = 200;
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "� ������� ����������� ����������� �������� id";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "�������� ������ id";
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
            default:
                statusCode = 405;
                response = "������������ ������";
        }

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
        exchange.sendResponseHeaders(statusCode, 0);
        writers(exchange);
    }
}