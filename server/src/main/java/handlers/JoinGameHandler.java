package handlers;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import models.ErrorObject;
import models.JoinObject;
import service.Service;
import service.ServiceException;
import spark.*;

public class JoinGameHandler implements Route {
    public final service.Service serv;

    DataAccess dataAccess;
    public JoinGameHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        serv = new Service(dataAccess);
    }

    public Object handle(Request req, Response res){
        var newGame = new Gson().fromJson(req.body(), JoinObject.class);
        var newAuth = req.headers("authorization");
        res.status(200);
        try {
            serv.joinGame(newAuth, newGame.getPlayerColor(), newGame.getGameID());
        } catch (ServiceException e) {
            if (e.getMessage().equals("bad request")) {
                res.status(400);
            } else if (e.getMessage().equals("unauthorized")) {
                res.status(401);
            } else if (e.getMessage().equals("already taken")) {
                res.status(403);
            }
            return new Gson().toJson(new ErrorObject("Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new ErrorObject("Error: " + e.getMessage()));
        }
        return "{}";
    }
}
