import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var server = new Server();
        var start = server.run(8080);
    }
}