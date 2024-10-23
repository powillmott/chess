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

public class RegisterHandler implements Route{

    public final service.Service serv;

    DataAccess dataAccess;
    public RegisterHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        serv = new Service(dataAccess);
    }

    public Object handle(Request req, Response res) throws Exception {
        var newUser = new Gson().fromJson(req.body(), UserData.class);
        res.status(200);
        AuthData result = null;
        try {
            result = serv.registerUser(newUser);

        } catch (ServiceException e) {
            if (e.getMessage().equals("bad request")){
                res.status(400);
            } else if (e.getMessage().equals("already taken")) {
                res.status(403);
            }
            return new Gson().toJson(new ErrorObject("Error: " + e.getMessage()));

        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new ErrorObject("Error: " + e.getMessage()));
        }
        return new Gson().toJson(result);
    }
}
