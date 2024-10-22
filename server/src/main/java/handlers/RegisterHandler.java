package handlers;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import models.UserData;
import service.Service;
import spark.*;

public class RegisterHandler implements Route{

    private final DataAccess dataAccess = new MemoryDataAccess();
    public final service.Service serv = new Service(dataAccess);

    public Object handle(Request req, Response res) throws Exception {
        var newUser = new Gson().fromJson(req.body(), UserData.class);
        var result = serv.registerUser(newUser);
        return new Gson().toJson(result);

    }
}
