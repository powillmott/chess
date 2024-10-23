package handlers;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import service.Service;
import spark.*;

public class JoinGameHandler implements Route {
    public final service.Service serv;

    DataAccess dataAccess;
    public JoinGameHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        serv = new Service(dataAccess);
    }

    public Object handle(Request req, Response res){
        return "";
    }
}
