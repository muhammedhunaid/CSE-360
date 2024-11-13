package asu.cse360project.DashboardControllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import asu.cse360project.Singleton;
import asu.cse360project.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.Initializable;

public class ModifySAG implements Initializable{

    Singleton data = Singleton.getInstance();

    private User selectedUser = null;
    private User selectedSAGUser = null;
    
    @FXML private Button add_admin;
    @FXML private Button add_viewer;
    @FXML private TableColumn<User, String> all_users_col;
    @FXML private TableView<User> all_users_table;
    @FXML private Button delete_viewer;
    @FXML private TableColumn<User, String> sag_users_col;
    @FXML private TableView<User> sag_users_table;

    ObservableList<User> all_users = FXCollections.observableArrayList();
    ObservableList<User> sag_users = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        all_users_col.setCellValueFactory(new PropertyValueFactory<>("username"));
        sag_users_col.setCellValueFactory(new PropertyValueFactory<>("username"));

        try {
            all_users = data.user_db.ListUsers(all_users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        all_users_table.setItems(all_users);
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
    }

    @FXML
    void addViewer(ActionEvent event) throws IOException, SQLException {
        if(selectedUser != null){
            if(!sag_users.contains(selectedUser)){
                data.group_articles_db.linkSAG(selectedUser.getId(), data.SAGGroupId, false);
                getSAGUsers();
            }
        }
    }

    @FXML
    void addAdmin(ActionEvent event) throws IOException, SQLException {
        if(selectedUser != null && !selectedUser.isOnlyStudent()){
            if(!sag_users.contains(selectedUser)){
                data.group_articles_db.linkSAG(selectedUser.getId(), data.SAGGroupId, true);
                getSAGUsers();
            }
        }
    }

    @FXML
    void deleteViewer(ActionEvent event) throws IOException, SQLException {
        if(selectedSAGUser != null){
            data.group_articles_db.deleteSAGUsers(selectedSAGUser.getId());
            getSAGUsers();
        }
    }

    void getSAGUsers() throws SQLException{
        ObservableList<User> admin_users;
        ObservableList<User> viewer_users;
        
        admin_users = data.group_articles_db.ListSAGUsers(data.SAGGroupId, true);
        viewer_users = data.group_articles_db.ListSAGUsers(data.SAGGroupId, false);

        for(User i : admin_users){
            i.setUsername(i.getUsername() + " *ADMIN*");
        }
        
        sag_users.addAll(admin_users);
        sag_users.addAll(viewer_users);
    }
}