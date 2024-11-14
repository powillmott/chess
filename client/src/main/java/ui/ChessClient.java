package ui;

public class ChessClient {
    private final String serverURL;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }
}
