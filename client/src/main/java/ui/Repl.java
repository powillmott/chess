package ui;

public class Repl {
    private final ChessClient client;

    public Repl(String serverURL) {client = new ChessClient(serverUrl);}
}
