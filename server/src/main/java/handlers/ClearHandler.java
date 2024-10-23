package handlers;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import service.Service;
import spark.*;

public class ClearHandler implements Route {
    public final service.Service serv;

    DataAccess dataAccess;
    public ClearHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        serv = new Service(dataAccess);
    }


    public Object handle(Request req, Response res){
        boolean isClear = serv.clearAll();
        if (isClear){
            res.status(200);
            res.type("text/plain");
            res.body("{}");
        } else {
            res.status(500);
            res.type("text/plain");
            res.body("{ message: Error: game not cleared }");
        }
        return res.body();
    }
}
