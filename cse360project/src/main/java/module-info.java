module asu.cse360project {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires com.h2database; // For JDBC
//    requires org.bouncycastle.lts.prov; // For Bouncy castle

    opens asu.cse360project to javafx.fxml;
    opens asu.cse360project.DashboardControllers to javafx.fxml;
    opens asu.cse360project.LoginControllers to javafx.fxml;
    exports asu.cse360project;
}
