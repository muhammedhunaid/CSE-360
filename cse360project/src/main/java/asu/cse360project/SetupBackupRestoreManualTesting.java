package asu.cse360project;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


public class SetupBackupRestoreManualTesting {
    //setup for Test1
    public static void Test1() throws SQLException, IOException {
        Singleton data = Singleton.getInstance();
        data.db.clearDatabase();
        data.db.createTables();
        String uname = "TestAdminUser";
        String uname2 = "TestAdminUser2";
        String uname3 = "StudentUser";
        data.user_db.addUser(uname , "Password123#", "admin"); //create user
        data.user_db.addUser(uname2 , "Password123#", "admin"); //create user
        data.user_db.addUser(uname3 , "Password123#", "student"); //create user
        User user1 = data.user_db.getUser(uname);
        User user2 = data.user_db.getUser(uname2);
        User user3 = data.user_db.getUser(uname3);
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
        data.group_articles_db.linkSAG(user3.getId(), view_group2.getId(), false);
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
        //User user2 = data.user_db.getUser(uname2);
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
        if(num > 0) {
            user1.setLoginRole(user1.getRole());
            data.setAppUser(user1);
            Utils.setRoot("DashboardScenes/dashboard");
        }
        

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

       //Setup for Test2
       public static void HowTo() throws Exception {
        Singleton data = Singleton.getInstance();
        data.db.clearDatabase();
        data.db.createTables();
        //instructors
        String I_uname = "InstructorA";
        String I_uname2 = "ProfessorZ";
        data.user_db.addUser(I_uname , "Password123#", "instructor,student"); //create user
        data.user_db.addUser(I_uname2 , "Abcde123#", "instructor"); //create user
        User I1 = data.user_db.getUser(I_uname);
        User I2 = data.user_db.getUser(I_uname2);

        //student
        String S_uname = "John";
        String S_uname2 = "Joe";
        data.user_db.addUser(S_uname , "456Hello#", "instructor,student"); //create user
        data.user_db.addUser(S_uname2 , "Lettuce578%", "instructor"); //create user
        User john = data.user_db.getUser(S_uname);

        String A_uname = "admin";
        data.user_db.addUser(A_uname , "9Xxx_imadmin_xxX9", "admin"); //create user
        User Admin = data.user_db.getUser(A_uname);

        //create groups
        data.group_articles_db.createGroup("JavaFX", false, I1.getId());
        data.group_articles_db.createGroup("Python", false, I1.getId());
        data.group_articles_db.createGroup("Super Secret Project", true, I1.getId());
        
        //get groups
        Group g1 = data.group_articles_db.getGroup("JavaFX");
        Group g2 = data.group_articles_db.getGroup("Python");
        Group g3 = data.group_articles_db.getGroup("Super Secret Project");

        data.group_articles_db.linkSAG(Admin.getId(), g3.getId(), true);
        data.group_articles_db.linkSAG(I2.getId(), g3.getId(), true);
        data.group_articles_db.linkSAG(john.getId(), g3.getId(), false);

        //add Articles
        ArrayList <Integer> g1_id = new ArrayList<>(); g1_id.add(g1.getId());
        ArrayList <Integer> g2_id = new ArrayList<>(); g2_id.add(g2.getId());
        ArrayList <Integer> g3_id = new ArrayList<>(); g3_id.add(g3.getId());
        data.group_articles_db.addArticle(10l, "JavaFX buttons", "learn how to use buttons!", "buttons, javaFX", "In JavaFX, buttons are created using the Button class and can be customized with text, styles, and event handling. To use a button, instantiate it with a label (e.g., Button myButton = new Button(\"Click Me\");). You can handle user interactions by setting an event listener with the setOnAction method, which takes a lambda expression or an event handler (e.g., myButton.setOnAction(e -> System.out.println(\"Button clicked!\"));). Finally, add the button to a layout (e.g., VBox, HBox) and include the layout in a Scene to display it in a JavaFX application.", "beginner", "Professor Z", "", g1_id, new ArrayList<>());
        data.group_articles_db.addArticle(11l, "JavaFX FlowPane", "How do flow panes work?", "flow pane, javaFX", "In JavaFX, a FlowPane is a layout container that arranges its children in a flow, wrapping them to the next row or column when there’s no more space. To use a FlowPane, create an instance and add child nodes to it (e.g., FlowPane flowPane = new FlowPane(); flowPane.getChildren().addAll(node1, node2);). You can customize the alignment, horizontal, and vertical gap between elements using methods like setAlignment(Pos.CENTER) or setHgap(10). Finally, add the FlowPane to a Scene to display it in your JavaFX application. It’s ideal for dynamic, responsive layouts.", "intermediate", "Instructor A", "", g1_id, new ArrayList<>());
        data.group_articles_db.addArticle(12l, "JavaFX Property Binding", "Learn what JavaFX Property Binding is and how to use it", "Property Binding, javaFX", "Property binding is an expert-level concept in JavaFX that enables dynamic synchronization between UI components and underlying data models. JavaFX properties, such as StringProperty, IntegerProperty, and others, are observable objects that support listeners and binding mechanisms to react to changes automatically.\n" + //
                        "\n" + //
                        "With property binding, you can create unidirectional or bidirectional links between properties. For example, unidirectional binding ensures that a target property updates when the source changes, while bidirectional binding keeps two properties in sync, updating both whenever one changes. This is especially useful for building reactive user interfaces where UI elements automatically reflect changes in the data model and vice versa.\n" + //
                        "\n" + //
                        "Bindings can also involve transformations using classes like Bindings or custom logic to calculate derived values dynamically. This concept is central to JavaFX’s declarative and event-driven approach, reducing boilerplate code and making complex UI behaviors easier to implement and maintain. Mastery of property binding elevates the quality and responsiveness of JavaFX applications, enabling seamless interactions between the user interface and the underlying application logic.", "Expert", "Instructor A", "", g1_id, new ArrayList<>());
        data.group_articles_db.addArticle(13l, "Python Types", "What are python Types?", "Types, data, python", "In Python, types define the kinds of data a variable can hold, such as int for integers, float for decimal numbers, str for text, and bool for true/false values. To specify a type, you can assign a value directly (e.g., x = 10 for an integer) or use type annotations for clarity (e.g., x: int = 10). Python also supports complex types like list, dict, tuple, and custom types via classes. You can check a variable's type with type(x) or ensure type correctness using type hints with tools like mypy. Python’s typing is dynamic and flexible, allowing easy manipulation of data.", "beginner", "Professor Z, Instructor A", "", g2_id, new ArrayList<>());
        data.group_articles_db.addArticle(14l, "Python decorators", "What are Python decorators", "", "In Python, decorators are an advanced concept that allows you to modify or extend the behavior of functions or methods dynamically. A decorator is a function that takes another function as input and returns a new function with added functionality. For example, a simple logging decorator can wrap a function to log its calls:", "advanced", "Instructor A", "", g2_id, new ArrayList<>());
        data.group_articles_db.addArticle(15l, "Quantum Loops", "Quantum Loops", "Quantum, hyper loop, cool words", "Quantum Loops are an advanced programming paradigm inspired by quantum computing principles, allowing loops to execute all iterations simultaneously in a non-linear, probabilistic manner. Leveraging \"quantum threads,\" a Quantum Loop evaluates all potential outcomes in parallel and collapses to the most optimal or valid result based on constraints defined by the programmer.\n" + //
                        "\n" + //
                        "Unlike traditional loops, which process iterations sequentially, Quantum Loops reduce computational overhead for complex tasks like optimization, pathfinding, or large-scale data analysis. For example, a Quantum Loop can instantly evaluate millions of potential routes in a graph to return the shortest path without explicitly iterating through each possibility.\n" + //
                        "\n" + //
                        "Quantum Loops are supported by emerging quantum programming frameworks and require specialized quantum hardware for execution. They represent a transformative step in computation, merging classical algorithms with the probabilistic power of quantum mechanics, opening doors to solving previously intractable problems.", "Expert", "Instructor A", "", g3_id, new ArrayList<>());
    }
}