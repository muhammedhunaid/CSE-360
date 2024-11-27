package asu.cse360project.Testing;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import asu.cse360project.Group;
import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.Utils;


public class SetupBackupRestoreManualTesting {
    //setup for Test1
    public static void Test1() throws SQLException, IOException {
        Singleton data = Singleton.getInstance();
        data.db.clearDatabase();
        data.db.createTables();
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

    //Setup for Test2
    public static void Test2(int num) throws Exception {
        Singleton data = Singleton.getInstance();
        data.db.clearDatabase();
        data.db.createTables();
        String uname = "TestAdminUser";
        String uname2 = "TestAdminUser2";
        data.user_db.addUser(uname , "Password123#", "admin"); //create user
        data.user_db.addUser(uname2 , "Password123#", "admin"); //create user
        User user1 = data.user_db.getUser(uname);
        User user2 = data.user_db.getUser(uname2);
        //create groups
        data.group_articles_db.createGroup("Group1", true, user1.getId());
        data.group_articles_db.createGroup("Group2", false, user1.getId());
        //get groups
        Group g1 = data.group_articles_db.getGroup("Group1");
        Group g2 = data.group_articles_db.getGroup("Group2");
        //add Articles
        ArrayList <Integer> g1_id = new ArrayList<>(); g1_id.add(g1.getId());
        ArrayList <Integer> g2_id = new ArrayList<>(); g2_id.add(g2.getId());
        data.group_articles_db.addArticle(10l, "Group1 1st article title", "Group1 1st article abstract", "", "", "beginner", "", "", g1_id, new ArrayList<>());
        data.group_articles_db.addArticle(11l, "Group1 2nd article title", "Group1 2nd article abstract", "", "", "intermediate", "", "", g1_id, new ArrayList<>());
        data.group_articles_db.addArticle(12l, "Group2 1st article title", "Group2 1st article abstract", "", "", "beginner", "", "", g2_id, new ArrayList<>());
        data.group_articles_db.addArticle(13l, "Group2 2nd article title", "Group2 2nd article abstract", "", "", "advanced", "", "", g2_id, new ArrayList<>());

        data.group_articles_db.backup(g1_id, "bk_g1", user1);
        data.group_articles_db.backup(g2_id, "bk_g2", user1);

        if(num == 1) { //delete multiple groups before restore
            data.group_articles_db.deleteGroup(g1.getId());
            data.group_articles_db.deleteGroup(g2.getId());
        }


        if(num == 2) { //delete articles from groups
            data.group_articles_db.deleteArticle(10l);
            data.group_articles_db.deleteArticle(12l);
            data.group_articles_db.deleteArticle(14l);
        }

        if(num == 3) { //delete mix of groups and articles
            data.group_articles_db.deleteGroup(g1.getId());
            data.group_articles_db.deleteArticle(10l);
            data.group_articles_db.deleteArticle(12l);
        }
        //set login role
        user1.setLoginRole(user1.getRole());
        data.setAppUser(user1);
        Utils.setRoot("DashboardScenes/dashboard");

        /* 
        Must Select "View Articles" and "Edit View" at start of each test
        Test 1: Testing restoring single groups run Test2(1)
            -	Input: click on bk_g1 and press restore
            -	Expected Result: "Group1" shows in groups table
            -	Assessment: Group1 showed up, pass
            -   Input: click on bk_g2 and press restore
            -	Expected Result: "Group2" shows in groups table
            -	Assessment: Group2 showed up, pass
        Test 2: Testing Restore Articles and Restoring nothing
            -   Input: click on bk_g1 and press restore
            -	Expected Result: Article "10" shows in articles table
            -	Assessment: Article 10 showed up, pass
            -   Input: click on bk_g2 and press restore
            -	Expected Result: Article "12" shows in articles table
            -	Assessment: Article 12 showed up, pass
            -   Input: click restore again
            -   Expected: no Change
            -   Assesment: no change, pass
        Test 3: Testing Both
            -	Input: click on bk_g1 and press restore
            -	Expected Result: "Group1" shows in groups table and Article "10" shows in articles table
            -	Assessment: Ariticle 10 and Group1 showed up, pass
            -	Input: click on bk_mixed and press restore
            -	Expected Result: Article "12" shows in articles table
            -	Assessment: Article 12 showed up pass
        */
    }
}