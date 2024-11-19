package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import models.ErrorObject;
import service.Service;
import service.ServiceException;
import spark.*;

public class LogoutHandler implements Route {
    public final service.Service serv;

    DataAccess dataAccess;
    public LogoutHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        serv = new Service(dataAccess);
    }

    public Object handle(Request req, Response res){
        var newAuth = req.headers("authorization");
        try {
            serv.logoutUser(newAuth);
        } catch (ServiceException e) {
            res.status(401);
            return new Gson().toJson(new ErrorObject("Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new ErrorObject("Error: " + e.getMessage()));
        }
        return "";
    }
}
