package server;

import com.google.gson.Gson;
import exception.ServerException;
import model.*;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;
    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData userData) throws ServerException {
        var path = "/user";
        return this.makeRequest("POST", path, userData, AuthData.class);
    }

    public AuthData login(LoginRequest loginRequest) throws ServerException {
        var path = "/session";
        return this.makeRequest("POST", path, loginRequest, AuthData.class);
    }

    public void logout(String authToken) throws ServerException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, authToken,null);
    }

    public GamesList listGames(String authToken) throws ServerException {
        var path = "/game/"+authToken;
        return this.makeRequest("GET", path, null, GamesList.class);
    }

    public String addGame(String authToken, String gameName) throws ServerException {
        var path = "/game/"+authToken;
        return this.makeRequest("POST", path, "{ \"GameName\": \"" + gameName + "\" }", null);
    }

    public void joinGame(String authToken, String gameID, String playerColor) throws ServerException {
        var path = "/game/"+authToken;
        this.makeRequest("PUT", path, "{ \"gameID\": \"" + gameID + "\", \"playerColor\": \"" + playerColor + "\" }", null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ServerException {
        return makeRequest(method,path,request,"",responseClass);
    }
    private <T> T makeRequest(String method, String path, Object request, String header, Class<T> responseClass) throws ServerException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.addRequestProperty("authorization", header);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ServerException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ServerException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ServerException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
