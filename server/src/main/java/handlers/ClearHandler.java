package handlers;
import spark.*;

public class ClearHandler implements Route {
    public Object handle(Request req, Response res){
        res.status(200);
        res.type("text/plain");
        res.body("{}");
        return res.body();
    }
}
