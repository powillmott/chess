package handlers;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import models.AuthData;
import models.UserData;
import service.Service;
import spark.*;

public class LoginHandler implements Route {
    private final DataAccess dataAccess = new MemoryDataAccess();
    public final service.Service serv = new Service(dataAccess);

    public Object handle(Request req, Response res){
        var newUser = new Gson().fromJson(req.body(), UserData.class);
        AuthData newAuth = serv.loginUser(newUser.username(), newUser.password());
    return new Gson().toJson(newAuth);
    }
}
