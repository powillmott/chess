package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MySqlDataAccess;
import dataaccess.MemoryDataAccess;
import handlers.*;
import service.Service;
import spark.*;
public class Server {

    public static void main(String[] args) {
        try{
            int port = Integer.parseInt(args[0]);
            new Server().run(port);
        } catch(ArrayIndexOutOfBoundsException | NumberFormatException ex){
            System.err.println("Specify the port number as a command line parameter");
        }

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        final DataAccess dataAccess = new MySqlDataAccess();
//        final DataAccess dataAccess = new MemoryDataAccess();

        // Register your endpoints and handle exceptions here.
        Spark.webSocket("/ws",new WebSocketHandler(dataAccess));

        Spark.delete("/db", new ClearHandler(dataAccess));

        Spark.post("/user", new RegisterHandler(dataAccess));

        Spark.post("/session", new LoginHandler(dataAccess));

        Spark.delete("/session", new LogoutHandler(dataAccess));

        Spark.get("/game", new ListGamesHandler(dataAccess));

        Spark.post("/game", new CreateGameHandler(dataAccess));

        Spark.put("/game", new JoinGameHandler(dataAccess));

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
