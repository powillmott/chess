package dataaccess;
import models.UserData;

public interface DataAccess {
    UserData getUser(String userName);
}
