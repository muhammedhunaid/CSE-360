package asu.cse360project.DashboardControllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import asu.cse360project.Article;
import asu.cse360project.Group;
import asu.cse360project.Singleton;
import asu.cse360project.EncryptionHelpers.EncryptionHelper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;

public class SearchArticlesController implements Initializable{
    Singleton data = Singleton.getInstance();

    //table variables
    ObservableList<Article> articles_list;
    ObservableList<Group> groups_list;
    Article selectedArticle;
    Group selectedGroup;
    EncryptionHelper encryptionHelper;

    //buttons
    @FXML private MenuButton level_btn;

    //article table JavaFX elements
    @FXML private TableView<Article> article_table;
    @FXML private TableColumn<Article, String> article_abstract;
    @FXML private TableColumn<Article, String> article_author;
    @FXML private TableColumn<Article, String> article_title;

    //group table JavaFX elements
    @FXML private TableView<Group> group_table;
    @FXML private TableColumn<Group, String> group_id;
    @FXML private TableColumn<Group, String> group_name;

    //article elements
    @FXML private Text authors;
    @FXML private Text body;
    @FXML private Text description;
    @FXML private Text header;
    @FXML private Text keywords;
    @FXML private Text references;
    @FXML private Text title;

    //search settings
    String level = "All";
    String search_keywords = "";

    //text fields
    @FXML private TextField keyword_input;
    @FXML private TextField group_input;
    @FXML private TextField article_input;


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        //make group table and set elements
        try {
            encryptionHelper = new EncryptionHelper();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        makeGroupTable();
        try {
            getGroups();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //make article table set elements
        makeArticleTable();
    }

    //get all articles from picked group
    public void getArticles() throws SQLException {
        articles_list = data.group_articles_db.searchArticles(selectedGroup.getId(), level, search_keywords);
        article_table.setItems(articles_list);
    }

    //get all groups user can access
    public void getGroups() throws SQLException {
        groups_list = data.group_articles_db.getAllGeneralGroups();
        groups_list.addAll(data.group_articles_db.getAllSpecialGroups(data.getAppUser().getId()));
        group_table.setItems(groups_list);
    }
                  
    //helper method for setting up Article table
    private void makeArticleTable() {
        // Set up table columns to display Article information
        article_title.setCellValueFactory(new PropertyValueFactory<>("title"));
        article_author.setCellValueFactory(new PropertyValueFactory<>("authors"));
        article_abstract.setCellValueFactory(new PropertyValueFactory<>("abstractText"));

        article_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        createWrappedColumn(article_title);
        createWrappedColumn(article_author);
        createWrappedColumn(article_abstract);

        // Add listener to handle row selection in the table
        article_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedArticle = newSelection;
            }
        });

        // Add listener to handle double clicking article
        article_table.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                try {
                    viewArticle();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
    
    //helper method for setting up Group table
    private void makeGroupTable() {
        // Set up table columns to display Group information
        group_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        group_name.setCellValueFactory(new PropertyValueFactory<>("name"));

        group_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        createWrappedColumn(group_name);

        // Add listener to handle row selection in the table
        group_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
            	selectedGroup = newSelection;
                try {
                    //set articles table to articles in group
                    getArticles();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private <T> void createWrappedColumn (TableColumn<T, String> column) {
        column.setCellFactory(tc -> {
            TableCell<T, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(article_abstract.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
    }

    @FXML //set get keywords searched
    void SearchKeywords(ActionEvent event) throws SQLException {
        search_keywords = keyword_input.getText();
        getArticles();
    }

    @FXML
    void SearchArticles(ActionEvent event) {

    }

    @FXML
    void searchGroups(ActionEvent event) {

    }

    @FXML   //set search level to advanced
    void setAdvanced(ActionEvent event) throws SQLException {
        level = "advanced";
        level_btn.setText(level);
        getArticles();
    }

    @FXML //set search level to beginner
    void setBeginner(ActionEvent event) throws SQLException {
        level = "beginner";
        level_btn.setText(level);
        getArticles();
    }

    @FXML //set search level to expert
    void setExpert(ActionEvent event) throws SQLException {
        level = "expert";
        level_btn.setText(level);
        getArticles();
    }

    @FXML //set search level to all
    void setAll(ActionEvent event) throws SQLException {
        level = "all";
        level_btn.setText(level);
        getArticles();
    }

    @FXML //set search level to Intermidiate
    void setIntermidiate(ActionEvent event) throws SQLException {
        level = "intermediate";
        level_btn.setText(level);
        getArticles();
    }

    // Populates the UI fields with the selected article’s content
    private void viewArticle() throws Exception { 
        String headTxt = "id: " + String.valueOf(selectedArticle.getId()); // Article ID
        headTxt += "    Groups: " + selectedArticle.getGroup_names().toString(); // Article groups
        headTxt += "    Level: " + selectedArticle.getLevel(); // Article level
        headTxt += "    Permissions: " + selectedArticle.getPermissions(); // Article permissions
        header.setText(headTxt); // Set the header text
        title.setText(selectedArticle.getTitle()); // Set the title
        authors.setText("Authors: " + selectedArticle.getAuthors()); // Set the authors
        description.setText("Desciption: " + selectedArticle.getAbstractText()); // Set the description
        body.setText("Body: " + encryptionHelper.decrypt(selectedArticle.getBody())); // Set the body  TODO: decrypt
        keywords.setText("Keywords: " + selectedArticle.getKeywords()); // Set the keywords
        references.setText("Refrences: " + selectedArticle.getReferences().toString()); // Set the references
    }
}

