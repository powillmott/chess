package handlers;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import models.AuthData;
import models.ErrorObject;
import models.UserData;
import service.Service;
import service.ServiceException;
import spark.*;

public class LoginHandler implements Route {
    public final service.Service serv;

    DataAccess dataAccess;
    public LoginHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        serv = new Service(dataAccess);
    }

    public Object handle(Request req, Response res){
        var newUser = new Gson().fromJson(req.body(), UserData.class);
        res.status(200);
        AuthData result = null;
        try {
            result = serv.loginUser(newUser.username(), newUser.password());
        } catch (ServiceException e) {
            res.status(401);
            return new Gson().toJson(new ErrorObject("Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new ErrorObject("Error: " + e.getMessage()));
        }

    return new Gson().toJson(result);
    }
}
