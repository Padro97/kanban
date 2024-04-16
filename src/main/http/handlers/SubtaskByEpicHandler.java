package main.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import main.managers.TaskManager;

import java.io.IOException;

public class SubtaskByEpicHandler extends BaseHandler {

    public SubtaskByEpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        int statusCode = 400;
        String method = httpExchange.getRequestMethod();
        String path = String.valueOf(httpExchange.getRequestURI());

        System.out.println("Обрабатывается запрос " + path + " с методом " + method);

        switch (method) {
            case "GET":
                String query = httpExchange.getRequestURI().getQuery();
                try {
                    int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                    statusCode = 200;
                    response = gson.toJson(taskManager.getSubtaskById(id));
                } catch (StringIndexOutOfBoundsException | NullPointerException e) {
                    response = "В запросе отсутствует необходимый параметр - id";
                } catch (NumberFormatException e) {
                    response = "Неверный формат id";
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