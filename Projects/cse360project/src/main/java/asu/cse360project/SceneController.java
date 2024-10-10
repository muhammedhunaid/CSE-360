package asu.cse360project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;

public class SceneController {
    private User user;

    public void setUser(User in)
    {
        user = in;
    }

    public User getUser(User in)
    {
        return user;
    }
}
