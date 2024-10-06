module phase1.gui {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens phase1.gui to javafx.fxml;
    opens phase1.gui.DashboardControllers to javafx.fxml; 
    exports phase1.gui;
}
