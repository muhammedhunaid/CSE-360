module phase1.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens phase1.gui to javafx.fxml;
    exports phase1.gui;
}
