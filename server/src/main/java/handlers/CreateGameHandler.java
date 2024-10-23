package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import models.AuthData;
import models.ErrorObject;
import models.GameData;
import service.Service;
import service.ServiceException;
import spark.*;

import java.io.Reader;
import java.util.Random;


public class CreateGameHandler implements Route {
    public final service.Service serv;

    DataAccess dataAccess;
    public CreateGameHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        serv = new Service(dataAccess);
    }

    public Object handle(Request req, Response res){
        var newGame = new Gson().fromJson(req.body(), GameData.class);
        var newAuth = req.headers("authorization");
        res.status(200);
        GameData result;
        try {
            result = serv.createGame(newAuth,newGame.gameName(),null);
        } catch (ServiceException e) {
            if (e.getMessage().equals("bad request")){
                res.status(400);
            } else if (e.getMessage().equals("unauthorized")) {
                res.status(401);
            }
            return new Gson().toJson(new ErrorObject("Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new ErrorObject("Error: " + e.getMessage()));
        }

        return new Gson().toJson(new GameData(result.gameID(), null,null,null,null));
    }
}
