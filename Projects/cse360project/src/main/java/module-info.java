module asu.cse360project {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.sql; // For JDBC

    opens asu.cse360project to javafx.fxml;
    opens asu.cse360project.DashboardControllers to javafx.fxml;
    exports asu.cse360project;
}
