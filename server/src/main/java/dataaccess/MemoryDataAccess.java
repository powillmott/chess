package dataaccess;

import models.UserData;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    final private Map<String, UserData> users = new HashMap<>();

    @Override
    public UserData getUser(String userName) {
        return users.get(userName);
    }
}
