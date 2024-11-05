package asu.cse360project.DashboardControllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import asu.cse360project.Article;
import asu.cse360project.Singleton;
import asu.cse360project.Utils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


/**
 * Controller class for managing articles in the system.
 * Provides functionality to add, remove, view, and update articles.
 */
public class ManageArticlesController implements Initializable {
    Singleton data = Singleton.getInstance(); // Singleton instance for accessing global data
    private Article selectedArticle = null; // Currently selected article
    ObservableList<Article> all_articles; // List to hold all articles
    
    @FXML private TableColumn<Article, String> title_col; // Table column for article titles
    @FXML private TableColumn<Article, String> author_col; // Table column for article authors
    @FXML private TableView<Article> article_table; // Table view for displaying articles

    @FXML private Text title; // Text field for displaying article title
    @FXML private Text authors; // Text field for displaying article authors
    @FXML private Text description; // Text field for displaying article description
    @FXML private Text keywords; // Text field for displaying article keywords
    @FXML private Text body; // Text field for displaying article body
    @FXML private Text references; // Text field for displaying article references
    @FXML private Text header; // Text field for displaying article header information

    @FXML private Button delete; // Button for deleting articles
    @FXML private Button add; // Button for adding articles
    @FXML private Button update; // Button for updating articles
    @FXML private Button back; // Button for going back
    @FXML private HBox content_area; // HBox for content area


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up table columns
        title_col.setCellValueFactory(new PropertyValueFactory<>("title"));
        author_col.setCellValueFactory(new PropertyValueFactory<>("authors"));

        // Load all articles from the database
        setTable();
        article_table.setItems(all_articles);

        // Add listener to handle row selection in the table
        article_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedArticle = newSelection;
                viewArticle(); // View the selected article
            }
        });

        // Add listener to handle double click on table rows for updating articles
        article_table.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    try {
                        update(new ActionEvent()); // Update the selected article
                    } catch (IOException e) {
                        // Handle exception
                        e.printStackTrace();
                    }
            }
        });
    }

    /**
     * Sets up the table with articles from the database.
     */
    private void setTable()
    {
        try {
            all_articles = data.group_articles_db.ListMultipleGroupsArticles(data.edit_group); // Load articles for the current group
            article_table.setItems(all_articles); // Set the table items
        } catch (SQLException e) {
            System.err.println("error getting articles"); // Log error message
            e.printStackTrace(); // Print the stack trace
        }
    }
    
    /**
     * Handles the add article action.
     * @param event The action event.
     * @throws IOException If an I/O exception occurs.
     */
    @FXML
    void add(ActionEvent event) throws IOException {
        data.editing_article = false; // Indicate that a new article is being created
        Utils.setContentArea("create_edit_article"); // Set the content area to the create/edit article view
    }

    /**
     * Views the selected article.
     */
    void viewArticle() {
        String headTxt = "id: " + String.valueOf(selectedArticle.getId()); // Article ID
        headTxt += "    Groups: " + selectedArticle.getGroup_names().toString(); // Article groups
        headTxt += "    Level: " + selectedArticle.getLevel(); // Article level
        headTxt += "    Permissions: " + selectedArticle.getPermissions(); // Article permissions
        header.setText(headTxt); // Set the header text
        title.setText(selectedArticle.getTitle()); // Set the title
        authors.setText("Authors: " + selectedArticle.getAuthors()); // Set the authors
        description.setText("Desciption: " + selectedArticle.getAbstractText()); // Set the description
        body.setText("Body: " + selectedArticle.getBody()); // Set the body
        keywords.setText("Keywords: " + selectedArticle.getKeywords()); // Set the keywords
        references.setText("Refrences: " + selectedArticle.getReferences().toString()); // Set the references
    }

    /**
     * Handles the back action.
     * @param event The action event.
     * @throws IOException If an I/O exception occurs.
     */
    @FXML
    void back(ActionEvent event) throws IOException {
        Utils.setContentArea("group_dashboard"); // Set the content area to the group dashboard view
    }

    /**
     * Handles the delete article action.
     * @param event The action event.
     */
    @FXML
    void delete(ActionEvent event) {
    	if(selectedArticle != null) // Check if an article is selected
        {
            Alert alert = new Alert(AlertType.CONFIRMATION); // Create a confirmation alert
            alert.setTitle("Delete Confirmation"); // Set the title
            alert.setHeaderText("Are you sure you want to delete this article?"); // Set the header text
            alert.setContentText("Article ID: " + selectedArticle.getId()); // Set the content text

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) { // If the user confirms
                    try {
                        data.group_articles_db.deleteArticle(selectedArticle.getId()); // Delete the article
                    } catch (SQLException e) {
                        e.printStackTrace(); // Handle exception
                    }
                }
                setTable(); // Refresh the table
            });
        }
    }

    /**
     * Handles the update article action.
     * @param event The action event.
     * @throws IOException If an I/O exception occurs.
     */
    @FXML
    void update(ActionEvent event) throws IOException {
    	if(selectedArticle != null) // Check if an article is selected
        {
            data.editing_article = true; // Indicate that an article is being edited
            data.article = selectedArticle; // Set the article to be edited
            Utils.setContentArea("create_edit_article"); // Set the content area to the create/edit article view
        }
    }  
}
