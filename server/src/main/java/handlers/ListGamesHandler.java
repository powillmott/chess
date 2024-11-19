package handlers;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import models.ErrorObject;
import models.GameData;
import models.GamesObject;
import service.Service;
import service.ServiceException;
import spark.*;

import java.util.Collection;

public class ListGamesHandler implements Route {
    public final service.Service serv;

    DataAccess dataAccess;
    public ListGamesHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        serv = new Service(dataAccess);
    }

    public Object handle(Request req, Response res){
        var newAuth = req.headers("authorization");
        Collection<GameData> result;
        try {
            result = serv.getAllGames(newAuth);
        } catch (ServiceException e) {
            res.status(401);
            return new Gson().toJson(new ErrorObject("Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new ErrorObject("Error: " + e.getMessage()));
        }
        return new Gson().toJson(new GamesObject(result));
    }
}
