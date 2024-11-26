package asu.cse360project.Testing;

import java.io.IOException;
import java.sql.SQLException;

import asu.cse360project.Group;
import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.Utils;


public class SetupManualTesting {

    //setup for Test1
    public static void Test1() throws SQLException, IOException {
        Singleton data = Singleton.getInstance();
        String uname = "TestAdminUser";
        data.user_db.addUser(uname , "Password123#", "admin"); //create user
        User user = data.user_db.getUser(uname);
        //create viwer group
        data.group_articles_db.createGroup("Viewing Group", false, user.getId()); 
        Group view_group = data.group_articles_db.getGroup("Viewing Group");
        //remove user from group
        data.group_articles_db.deleteSAGUsers(user.getId());
        //add user as viewer
        data.group_articles_db.linkSAG(user.getId(), view_group.getId(), false);
        //create user as admin of group
        data.group_articles_db.createGroup("Admin Group", true, user.getId());
        //set login role
        user.setLoginRole(user.getRole());
        data.setAppUser(user);
        Utils.setRoot("DashboardScenes/dashboard");
    }
}
