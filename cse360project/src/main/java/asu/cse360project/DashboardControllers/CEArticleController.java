package asu.cse360project.DashboardControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import asu.cse360project.Article;
import asu.cse360project.Group;
import asu.cse360project.Singleton;
import asu.cse360project.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CEArticleController implements Initializable {

    Singleton data = Singleton.getInstance();
    String article_level = "";
    String refrence = "";
    String groups = "";
    ObservableList<Article> articles_list;
    ObservableList<Group> groups_list;
    private Article selectedArticle = null;
    private Group selectedGroup = null;


    //scene elements
    @FXML private CheckBox admin;
    @FXML private Label article_links;
    @FXML private TableColumn<Article, String> article_col;
    @FXML private TableView<Article> article_table;
    @FXML private TextArea body;
    @FXML private TextField description;
    @FXML private Label group_links;
    @FXML private TableColumn<Group, String> group_col;
    @FXML private TableView<Group> group_table;
    @FXML private CheckBox instructor;
    @FXML private MenuButton level;
    @FXML private CheckBox student;
    @FXML private TextField title;
    @FXML private Label title_label;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        article_col.setCellValueFactory(new PropertyValueFactory<>("title"));
        group_col.setCellValueFactory(new PropertyValueFactory<>("name"));

        articles_list = FXCollections.observableArrayList();
        groups_list = FXCollections.observableArrayList();
        
        group_table.setItems(groups_list);
        article_table.setItems(articles_list);
        
        //TODO: Query data base for all articles and groups and add to lists

        group_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedGroup = newSelection;
            }
        });

        article_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedArticle = newSelection;
            }
        });
    }

    @FXML
    void addArticle(ActionEvent event) {
        if(selectedArticle != null)
        {
            article_links.setText(article_links.getText() + selectedArticle.getName());
        }
    }   

    @FXML
    void addGroup(ActionEvent event) {
        if(selectedGroup != null)
        {
            group_links.setText(group_links.getText() + selectedGroup.getName());
        }
    }

    @FXML
    void advanced(ActionEvent event) {
        article_level = "advanced";
        level.setText("advanced");
    }

    @FXML
    void beginner(ActionEvent event) {
        article_level = "beginner";
        level.setText("beginner");
    }

    @FXML
    void expert(ActionEvent event) {
        article_level = "expert";
        level.setText("expert");
    }

    @FXML
    void intermediate(ActionEvent event) {
        article_level = "intermediate";
        level.setText("intermediate");
    }

    @FXML
    void submit(ActionEvent event) throws IOException {
        //get text
        String title_text = title.getText();
        String description_text = description.getText();
        String body_text = body.getText();

        //get level, return if not selected
        if(article_level == "") { return; }

        //get permission, return if none selected
        String permissions = getPermissions();
        if(permissions == "") { return; }

        //TODO: add or edit info info db
        if(data.editing_article)
        {
            //edit article 
            data.editing_article = false;
        }else{
            //add controller
        }

        Utils.setContentArea("view_articles");       
    }

    @FXML
    void filter(ActionEvent event) {

    }

    //helper function to determine which permissions are checked
    private String getPermissions() {
        String p = "";
        //check instructor check box
        if(instructor.isSelected()) {
            p += "instructor,"; 
        }
        //check admin check box
        if(instructor.isSelected()) {
            p += "instructor,"; 
        }
        //check student check box
        if(instructor.isSelected()) {
            p += "student,"; 
        }
        return p;
    }
}
