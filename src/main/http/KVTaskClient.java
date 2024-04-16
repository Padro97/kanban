package main.http;

import main.exceptions.KVTaskClientGetTokenException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {

    private String apiToken;

    private final String serverURL;

    public KVTaskClient(String serverURL) {
        this.serverURL = serverURL;
        register();

    }

    private void register() {
        try {
            URI uri = URI.create(this.serverURL + "/register");

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new KVTaskClientGetTokenException("Failed to get a token");
            }
            apiToken = response.body();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void put(String key, String json) {
        URI uri = URI.create(this.serverURL + "/save/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<Void> response = client.send(request,
                    HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                throw new KVTaskClientGetTokenException("Failed to save data");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new KVTaskClientGetTokenException("Failed data");
        }
    }

    public String load(String key) {
        URI uri = URI.create(this.serverURL + "/load/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                throw new KVTaskClientGetTokenException("Failed to save data");
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new KVTaskClientGetTokenException("Failed data");
        }
    }
}