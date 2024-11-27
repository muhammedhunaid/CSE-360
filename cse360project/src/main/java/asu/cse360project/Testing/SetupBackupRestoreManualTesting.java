package asu.cse360project.Testing;

import java.io.IOException;
import java.sql.SQLException;

import asu.cse360project.Group;
import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.Utils;


public class SetupBackupRestoreManualTesting {

    //setup for Test1
    public static void Test1() throws SQLException, IOException {
        Singleton data = Singleton.getInstance();
        String uname = "TestAdminUser";
        String uname2 = "TestAdminUser2";
        data.user_db.addUser(uname , "Password123#", "admin"); //create user
        data.user_db.addUser(uname2 , "Password123#", "admin"); //create user
        User user1 = data.user_db.getUser(uname);
        User user2 = data.user_db.getUser(uname2);
        //create groups
        data.group_articles_db.createGroup("Viewing Group1", false, user1.getId()); 
        data.group_articles_db.createGroup("Viewing Group2", true, user2.getId());
        data.group_articles_db.createGroup("Admin Group1", true, user1.getId());
        data.group_articles_db.createGroup("Admin Group2", false, user1.getId());
        //get groups and remove user as admin
        Group view_group1 = data.group_articles_db.getGroup("Viewing Group1");
        Group view_group2 = data.group_articles_db.getGroup("Viewing Group2");
        //remove user from group and add as viewer
        data.group_articles_db.deleteSAGUsers(user1.getId(), view_group1.getId());
        data.group_articles_db.linkSAG(user1.getId(), view_group1.getId(), false);
        //add user as viewer
        data.group_articles_db.linkSAG(user1.getId(), view_group2.getId(), false);
        //create user as admin of group

        //set login role
        user1.setLoginRole(user1.getRole());
        data.setAppUser(user1);
        Utils.setRoot("DashboardScenes/dashboard");

        /* 
        Test 1: Testing no groups 
            -	Input: select backup button. 
            -	Expected Result: nothing happens.
            -	Assessment: Nothing happened, passed
            Test 2: Only admin groups 
            -	Input: Double click “Admin Group1” and “Admin Group2”, then select backup button. 
            -	Expected Result: Window should popup prompting for name of backup file
            -	Assessment: Pop up happened, passed
            Test 3: Only Viewer groups 
            -	Input: Double click “Viewing Group1” and “Viewing Group1”, then select backup button. 
            -	Expected Result: display error saying “You are not an admin of one of the selected groups to backup”
            -	Assessment: Error Message Showed, passed
            Test 4: Admin and Viewer groups 
            -	Input: Double click “Admin Group1” and “Admin Group2” and “Viewing Group1”, then select backup button. 
            -	Expected Result: display error saying “You are not an admin of one of the selected groups to backup”
            -	Assessment: Error Message Showed, passed  
        */
    }
}