package asu.cse360project.DashboardControllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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

import java.util.ArrayList; 

public class CEArticleController implements Initializable {

    Singleton data = Singleton.getInstance();
    String article_level = "";
    ObservableList<Article> articles_list;
    ObservableList<Group> groups_list;
    ArrayList<Integer> groups = new ArrayList<>();
    ArrayList<Long> refrences = new ArrayList<>();
    private Article selectedArticle = null;
    private Group selectedGroup = null;


    //scene elements
    @FXML private TableColumn<Article, String> article_col;
    @FXML private TableColumn<Article, String> authors_col;
    @FXML private TableColumn<Article, Long> article_id_col;
    @FXML private TableView<Article> article_table;
    @FXML private TableColumn<Group, String> group_col;
    @FXML private TableColumn<Group, Integer> group_id_col;
    @FXML private TableView<Group> group_table;
    
    @FXML private TextArea body;
    @FXML private TextField description;
    @FXML private TextField keywords;
    @FXML private TextField authors;
    @FXML private TextField search_article;
    @FXML private TextField search_group;
    @FXML private TextField title;

    @FXML private Label group_links;
    @FXML private Label article_links;
    @FXML private Label title_label;

    @FXML private CheckBox instructor;
    @FXML private CheckBox student;
    @FXML private CheckBox admin;
    @FXML private MenuButton level;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        body.setWrapText(true);

        article_col.setCellValueFactory(new PropertyValueFactory<>("title"));
        authors_col.setCellValueFactory(new PropertyValueFactory<>("authors"));
        article_id_col.setCellValueFactory(new PropertyValueFactory<>("id"));

        group_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        group_id_col.setCellValueFactory(new PropertyValueFactory<>("id"));

        articles_list = FXCollections.observableArrayList();
        groups_list = FXCollections.observableArrayList();
        
        try {
            groups_list = data.group_articles_db.getAllGroups();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        group_table.setItems(groups_list);

        group_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedGroup = newSelection;

                try {
                    articles_list = data.group_articles_db.ListArticles(selectedGroup.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                article_table.setItems(articles_list);
            }
        });

        article_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedArticle = newSelection;
            }
        });

        if(data.editing_article)
        {
            title_label.setText("Editing Article");
            setContent();
        }
    }

    private void setContent() {
        Article article = data.article;

        title.setText(article.getTitle());
        authors.setText(article.getAuthors());
        description.setText(article.getAbstractText());
        body.setText(article.getBody());
        keywords.setText(article.getKeywords());
        setLevel(article.getLevel());
        article_level = article.getLevel();
        setPermissions(article.getPermissions());
        article_links.setText("Article Links: " + article.getReferences().toString());
        group_links.setText("Groups links: " + article.getGroups().toString());
        refrences = article.getReferences();
        groups = article.getGroups();
    }

    private void setPermissions(String permissions) {
        if(permissions.contains("admin"))
        {
            admin.setSelected(true);
        }

        if(permissions.contains("instructor"))
        {
            instructor.setSelected(true);
        }

        if(permissions.contains("student"))
        {
            student.setSelected(true);
        }
    }

    @FXML
    void removeArticle(ActionEvent event) {
        if(selectedArticle != null)
        {
            refrences.remove(Long.valueOf(selectedArticle.getId()));
            article_links.setText("Article Links: " + refrences.toString());
        }
    }
    
    @FXML
    void removeGroup(ActionEvent event) {
        if(selectedGroup != null)
        {
            groups.remove(Integer.valueOf(selectedGroup.getId()));
            group_links.setText("Groups links: " + groups.toString());
        }
    }

    @FXML
    void addArticle(ActionEvent event) {
        if(selectedArticle != null && !refrences.contains(selectedArticle.getId()))
        {
            refrences.add(selectedArticle.getId());
            article_links.setText("Article Links: " + refrences.toString());
        }
    }

    @FXML
    void addGroup(ActionEvent event) {
        if(selectedGroup != null && !groups.contains(selectedGroup.getId()))
        {
            groups.add(selectedGroup.getId());
            group_links.setText("Groups links: " + groups.toString()); 
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
    void cancel(ActionEvent event) throws IOException {
        Utils.setContentArea("manage_articles");
    }

    void setLevel(String level_txt) {
        level.setText(level_txt);
    }

    @FXML
    void submit(ActionEvent event) throws IOException, SQLException {
        //get text
        String title_text = title.getText();
        String description_text = description.getText();
        String body_text = body.getText();
        String keyword_text = keywords.getText();
        String authors_text = authors.getText();

        //get level, return if not selected
        if(article_level == "") { return; }

        //get permission, return if none selected
        String permissions = getPermissions();
        if(permissions == "") { return; }

        if(data.editing_article)
        {
            data.group_articles_db.updateArticle(data.article.getId(), title_text, description_text, keyword_text, body_text, article_level, authors_text, permissions, groups, refrences);
            data.editing_article = false;
            data.article = null;
        }else{
            data.group_articles_db.addArticle(title_text, description_text, keyword_text, body_text, article_level, authors_text, permissions, groups, refrences);
        }

        Utils.setContentArea("manage_articles"); 
    }

    //helper function to determine which permissions are checked
    private String getPermissions() {
        String p = "";
        //check instructor check box
        if(instructor.isSelected()) {
            p += "instructor,"; 
        }
        //check admin check box
        if(admin.isSelected()) {
            p += "admin,"; 
        }
        //check student check box
        if(student.isSelected()) {
            p += "student,"; 
        }
        return p;
    }

    @FXML
    void searchGroup(ActionEvent event) {
        int grp_id = -1;
        try{
            grp_id = Integer.parseInt(search_group.getText().toString());
        } catch (NumberFormatException e) {
           return;
        }

        for (Group grp : groups_list) {
            if (grp.getId() == grp_id) {
                group_table.getSelectionModel().select(grp); // Highlight the row if user is found
                group_table.scrollTo(grp); // Scroll to the row
                return;
            } else {
                //Utils.setLabel(error_label, "Group not Found", Color.RED); // Show error if user is not found
            }
        }
    }

    @FXML
    void searchArticle(ActionEvent event) {
        Long article_id = (long) -1;
        try{
            article_id = Long.parseLong(search_group.getText().toString());
        } catch (NumberFormatException e) {
            return;
        }

        for (Article art : articles_list) {
            if (art.getId() == article_id) {
                article_table.getSelectionModel().select(art); // Highlight the row if user is found
                article_table.scrollTo(art); // Scroll to the row
                return;
            } else {
                //Utils.setLabel(error_label, "Group not Found", Color.RED); // Show error if user is not found
            }
        }
    }
}
