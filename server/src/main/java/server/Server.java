package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
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

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", new ClearHandler());

        Spark.post("/user", new RegisterHandler());

        Spark.post("/session", new LoginHandler());

        Spark.delete("/session", new LogoutHandler());

        Spark.get("/game", new ListGamesHandler());

        Spark.post("/game", new CreateGameHandler());

        Spark.put("/game", new JoinGameHandler());

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
