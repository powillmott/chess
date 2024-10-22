package handlers;
import com.google.gson.Gson;
import models.UserData;
import spark.*;

public class RegisterHandler implements Route{
    public Object handle(Request req, Response res) throws Exception {
        var newUser = new Gson().fromJson(req.body(), UserData.class);
        var result = service.RegisterUser(newUser);
        return new Gson().toJson(result);

    }
}
