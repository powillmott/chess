package handlers;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import service.Service;
import spark.*;

public class JoinGameHandler implements Route {
    private final DataAccess dataAccess = new MemoryDataAccess();
    public final service.Service serv = new Service(dataAccess);

    public Object handle(Request req, Response res){
        return "";
    }
}
