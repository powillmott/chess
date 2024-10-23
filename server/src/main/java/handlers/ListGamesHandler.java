package handlers;
import dataaccess.DataAccess;
import service.Service;
import spark.*;

public class ListGamesHandler implements Route {
    public final service.Service serv;

    DataAccess dataAccess;
    public ListGamesHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        serv = new Service(dataAccess);
    }

    public Object handle(Request req, Response res){
        return "";
    }
}
