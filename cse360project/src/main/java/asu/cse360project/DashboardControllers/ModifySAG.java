package asu.cse360project.DashboardControllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;

public class ModifySAG implements Initializable{

    Singleton data = Singleton.getInstance();
    private int num_admins = 0;

    private User selectedUser = null;
    private User selectedSAGUser = null;
    
    @FXML private Button add_admin;
    @FXML private Button add_viewer;
    @FXML private TableColumn<User, String> all_users_col;
    @FXML private TableView<User> all_users_table;
    @FXML private Button delete_viewer;
    @FXML private TableColumn<User, String> sag_users_col;
    @FXML private TableColumn<User, String> all_users_role_col;
    @FXML private TableView<User> sag_users_table;

    ObservableList<User> all_users = FXCollections.observableArrayList();
    ObservableList<User> sag_users = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        all_users_col.setCellValueFactory(new PropertyValueFactory<>("username"));
        sag_users_col.setCellValueFactory(new PropertyValueFactory<>("username"));
        all_users_role_col.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        try {
            getSAGUsers();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        all_users_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedUser = newSelection;
            }
        });

        sag_users_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedSAGUser = newSelection;
            }
        });

        addTooltips();
    }

    void addTooltips() {
        Tooltip addAdminTooltip = new Tooltip("Add the selected user as an Admin to the group.");
        Tooltip addViewerTooltip = new Tooltip("Add the selected user as a Viewer to the group.");
        Tooltip deleteViewerTooltip = new Tooltip("Remove the selected user from the group.");

        add_admin.setTooltip(addAdminTooltip);
        add_viewer.setTooltip(addViewerTooltip);
        delete_viewer.setTooltip(deleteViewerTooltip);
    }

    @FXML
    void addViewer(ActionEvent event) throws IOException, SQLException {
        if(selectedUser != null){
            data.group_articles_db.linkSAG(selectedUser.getId(), data.edit_group.get(0), false);
            getSAGUsers();
        }
    }

    @FXML
    void addAdmin(ActionEvent event) throws IOException, SQLException {
        if(selectedUser != null && !selectedUser.isOnlyStudent()){
            data.group_articles_db.linkSAG(selectedUser.getId(), data.edit_group.get(0), true);
            getSAGUsers();
        }
    }

    @FXML
    void back(ActionEvent event) throws IOException, SQLException {
        Utils.setContentArea(data.view_area, data.view_box);  // Navigate to manage articles view
    }

    @FXML
    void deleteViewer(ActionEvent event) throws IOException, SQLException {
        if(selectedSAGUser != null){
            if(selectedSAGUser.getUsername().contains(" *ADMIN*") && num_admins == 1) {  
                return;
            }

            data.group_articles_db.deleteSAGUsers(selectedSAGUser.getId(), data.edit_group.get(0));
            getSAGUsers();
        }
    }

    void getSAGUsers() throws SQLException{
        all_users = data.group_articles_db.ListUsersNotInGroup(data.edit_group.get(0));
        all_users_table.setItems(all_users);

        ObservableList<User> admin_users = data.group_articles_db.ListSAGUsers(data.edit_group.get(0), true);
        ObservableList<User> viewer_users = data.group_articles_db.ListSAGUsers(data.edit_group.get(0), false);

        num_admins = admin_users.size();
        for(User i : admin_users){
            i.setUsername(i.getUsername() + " *ADMIN*");
        }

        sag_users.clear();        
        sag_users.addAll(admin_users);
        sag_users.addAll(viewer_users);

        sag_users_table.setItems(sag_users);
    }
}