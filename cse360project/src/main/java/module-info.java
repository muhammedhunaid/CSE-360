module asu.cse360project {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;
    requires javafx.graphics;
    requires com.h2database;
    requires javafx.base; // For JDBC
    requires org.bouncycastle.provider; // For JDBC
    requires jdk.incubator.vector;
    
    opens asu.cse360project to javafx.fxml;
    opens asu.cse360project.DashboardControllers to javafx.fxml;
    opens asu.cse360project.LoginControllers to javafx.fxml;
    exports asu.cse360project;
    exports asu.cse360project.DashboardControllers;
    exports asu.cse360project.DatabaseHelpers;
    exports asu.cse360project.EncryptionHelpers;
}
