package handlers;

import com.google.gson.Gson;
import models.AuthData;
import models.GameData;
import spark.*;

public class CreateGameHandler implements Route {
    public Object handle(Request req, Response res){
        var newGame = new Gson().fromJson(req.body(), GameData.class);
        res.status(200);
        AuthData result = null;

        return new Gson().toJson(newGame.gameID());
    }
}
