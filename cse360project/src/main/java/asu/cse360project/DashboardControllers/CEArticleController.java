package asu.cse360project.DashboardControllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import asu.cse360project.Article;
import asu.cse360project.EncryptionHelpers.EncryptionHelper;
import asu.cse360project.Group;
import asu.cse360project.Singleton;
import asu.cse360project.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton; 
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller class for creating and editing articles in the application.
 * This controller manages the Create/Edit Article interface and provides functionality for:
 * - Creating new articles with comprehensive metadata
 * - Editing existing articles and their properties
 * - Managing article permissions and access levels
 * - Linking articles to groups and other articles
 * - Handling article encryption and storage
 * 
 * The controller implements JavaFX's Initializable interface and provides
 * a rich user interface for article management through various UI components
 * and database interactions.
 *
 * @author Tu35
 * @version 2.00 2024-12-06 Enhanced documentation and added encryption support
 * @version 1.00 2024-11-18 Initial version with core article management functionality
 */
public class CEArticleController implements Initializable {

    /** Singleton instance for accessing shared application data */
    private Singleton data = Singleton.getInstance();
    
    /** Current access level of the article being edited */
    String article_level = "";
    
    /** Observable list of all articles in the system */
    ObservableList<Article> articles_list;
    
    /** Observable list of all groups in the system */
    ObservableList<Group> groups_list;
    
    /** List of group IDs associated with the current article */
    ArrayList<Integer> groups = new ArrayList<>();

    /** List of article references associated with the current article */
    ArrayList<Long> references = new ArrayList<>();

    // FXML Injected Components
    
    /** Table column for article title */
    @FXML
    private TableColumn<Article, String> article_col;
    
    /** Table column for article authors */
    @FXML
    private TableColumn<Article, String> authors_col;
    
    /** Table column for article ID */
    @FXML
    private TableColumn<Article, Long> article_id_col;
    
    /** Table view for displaying available articles */
    @FXML
    private TableView<Article> article_table;
    
    /** Table column for group name */
    @FXML
    private TableColumn<Group, String> group_col;
    
    /** Table column for group ID */
    @FXML
    private TableColumn<Group, Integer> group_id_col;
    
    /** Table view for displaying available groups */
    @FXML
    private TableView<Group> group_table;

    /** Text area for article body/content */
    @FXML
    private TextArea body;
    
    /** Text field for article description */
    @FXML
    private TextField description;
    
    /** Text field for article keywords */
    @FXML
    private TextField keywords;
    
    /** Text field for article authors */
    @FXML
    private TextField authors;
    
    /** Text field for searching articles */
    @FXML
    private TextField search_article;
    
    /** Text field for searching groups */
    @FXML
    private TextField search_group;
    
    /** Text field for article title */
    @FXML
    private TextField title;

    /** Label for displaying group links */
    @FXML
    private Label group_links;
    
    /** Label for displaying article links */
    @FXML
    private Label article_links;
    
    /** Label for displaying title */
    @FXML
    private Label title_label;

    /** Menu button for selecting article access level */
    @FXML
    private MenuButton level;
    
    /** Selected article for editing */
    private Article selectedArticle;
    
    /** Selected group for article assignment */
    private Group selectedGroup;
    
    /** Helper for encryption/decryption operations */
    private EncryptionHelper encryptionHelper;

    /**
     * Initializes the controller after FXML injection is complete.
     * Sets up table columns, loads initial data, and configures event handlers.
     *
     * @param location The location used to resolve relative paths for the root object
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            encryptionHelper = new EncryptionHelper();
        } catch (Exception e) {
            e.printStackTrace();
        }
        body.setWrapText(true);

        // Setting cell value factories to map table columns with article and group properties
        article_col.setCellValueFactory(new PropertyValueFactory<>("title"));
        authors_col.setCellValueFactory(new PropertyValueFactory<>("authors"));
        article_id_col.setCellValueFactory(new PropertyValueFactory<>("id"));

        group_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        group_id_col.setCellValueFactory(new PropertyValueFactory<>("id"));

        articles_list = FXCollections.observableArrayList();
        groups_list = FXCollections.observableArrayList();

        addTooltips();

        // Attempt to load all groups, including special groups (All Articles, Ungrouped Articles) from database
        try {
            groups_list = data.group_articles_db.getAllSpecialGroups(data.getAppUser().getId());
            group_table.setItems(groups_list);  // Set groups list to group table
        } catch (SQLException e) {
            e.printStackTrace();
        }

        group_table.setItems(groups_list);  // Set groups list to group table

        // Listener for selected group in table to load articles for that group
        group_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedGroup = newSelection;
                ArrayList<Integer> g = new ArrayList<>();
                g.add(selectedGroup.getId());
                try {
                    articles_list = data.group_articles_db.ListMultipleGroupsArticles(g);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                article_table.setItems(articles_list); // Display articles in table
            }
        });

        // Listener for selected article in table
        article_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedArticle = newSelection;
            }
        });

        // If editing an article, set the form content with the selected article's data
        if(data.editing_article)
        {
            title_label.setText("Editing Article");
            try {
                setContent();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds tooltips to UI components for better user experience.
     */
    void addTooltips() {
        Tooltip levelTooltip = new Tooltip("Select the article's difficulty level (e.g., beginner, advanced).");
        level.setTooltip(levelTooltip);
    
        Tooltip searchArticleTooltip = new Tooltip("Search for an article by ID.");
        Tooltip searchGroupTooltip = new Tooltip("Search for a group by ID.");
        search_article.setTooltip(searchArticleTooltip);
        search_group.setTooltip(searchGroupTooltip);
    
        Tooltip descriptionTooltip = new Tooltip("Enter a short description of the article.");
        description.setTooltip(descriptionTooltip);
    
        Tooltip keywordsTooltip = new Tooltip("Enter keywords for the article, separated by commas.");
        keywords.setTooltip(keywordsTooltip);
    
        Tooltip authorsTooltip = new Tooltip("Enter the names of the authors.");
        authors.setTooltip(authorsTooltip);
    
        Tooltip titleTooltip = new Tooltip("Enter the title of the article.");
        title.setTooltip(titleTooltip);
    
        Tooltip bodyTooltip = new Tooltip("Enter the full content of the article.");
        body.setTooltip(bodyTooltip);
    }

    /**
     * Populates the UI fields with the selected article’s content.
     * 
     * @throws Exception if encryption/decryption fails
     */
    private void setContent() throws Exception {
        Article article = data.article;

        title.setText(article.getTitle()); //fill title
        authors.setText(article.getAuthors()); //fill authors
        description.setText(article.getAbstractText()); //fill description
        body.setText(encryptionHelper.decrypt(article.getBody())); //fill body
        keywords.setText(article.getKeywords()); //fill keywords
        setLevel(article.getLevel()); //fill level
        article_level = article.getLevel();
        article_links.setText("Article Links: " + article.getReferences().toString()); //fill groups
        group_links.setText("Groups links: " + article.getGroups().toString()); //fill refrences
        references = article.getReferences(); //set refrences
        groups = article.getGroups(); //sert groups
    }

    /**
     * Removes the selected article from articles references.
     * 
     * @param event The action event triggered by the button click
     */
    @FXML
    void removeArticle(ActionEvent event) {
        if(selectedArticle != null) 
        {
            references.remove(Long.valueOf(selectedArticle.getId())); //remove article from refrences 
            article_links.setText("Article Links: " + references.toString()); //update UI
        }
    }

    /**
     * Removes the selected group from the group links.
     * 
     * @param event The action event triggered by the button click
     */
    @FXML
    void removeGroup(ActionEvent event) {
        if(selectedGroup != null)
        {
            groups.remove(Integer.valueOf(selectedGroup.getId())); //remove group from group links
            group_links.setText("Groups links: " + groups.toString()); //update UI
        }
    }

    /**
     * Adds the selected article to the references list.
     * 
     * @param event The action event triggered by the button click
     */
    @FXML
    void addArticle(ActionEvent event) {
        if(selectedArticle != null && !references.contains(selectedArticle.getId()))
        {
            references.add(selectedArticle.getId()); //add article to list of article refrences
            article_links.setText("Article Links: " + references.toString()); //update UI
        }
    }

    /**
     * Adds the selected group to the group links list.
     * 
     * @param event The action event triggered by the button click
     */
    @FXML
    void addGroup(ActionEvent event) {
        if(selectedGroup != null)
        {
            groups.add(selectedGroup.getId()); //add group to list of article groups
            group_links.setText("Groups links: " + groups.toString()); //update UI
        }
    }

    /**
     * Sets the article level to 'advanced'.
     * 
     * @param event The action event triggered by the button click
     */
    @FXML
    void advanced(ActionEvent event) {
        article_level = "advanced";
        level.setText("advanced");
    }

    /**
     * Sets the article level to 'beginner'.
     * 
     * @param event The action event triggered by the button click
     */
    @FXML
    void beginner(ActionEvent event) {
        article_level = "beginner";
        level.setText("beginner");
    }

    /**
     * Sets the article level to 'expert'.
     * 
     * @param event The action event triggered by the button click
     */
    @FXML
    void expert(ActionEvent event) {
        article_level = "expert";
        level.setText("expert");
    }

    /**
     * Sets the article level to 'intermediate'.
     * 
     * @param event The action event triggered by the button click
     */
    @FXML
    void intermediate(ActionEvent event) {
        article_level = "intermediate";
        level.setText("intermediate");
    }

    /**
     * Cancels the editing or creation of an article and returns to the manage articles page.
     * 
     * @param event The action event triggered by the button click
     * @throws IOException if navigation fails
     */
    @FXML
    void cancel(ActionEvent event) throws IOException {
        Utils.setContentArea(data.view_area, data.view_box);  // Navigate to manage articles view
    }

    /**
     * Sets the article level based on the provided level text.
     * 
     * @param level_txt The level text to set
     */
    void setLevel(String level_txt) {
        level.setText(level_txt);
    }

    /**
     * Submits the article by adding it or updating it in the database.
     * 
     * @param event The action event triggered by the button click
     * @throws SQLException if database operation fails
     * @throws IOException if encryption/decryption fails
     * @throws Exception if other errors occur
     */
    @FXML
    void submit(ActionEvent event) throws SQLException, IOException, Exception {
        //get text
        String title_text = title.getText();
        String description_text = description.getText();
        String body_text = body.getText();
        String keyword_text = keywords.getText();
        String authors_text = authors.getText();

        //get level, return if not selected
        if(article_level == "") { return; }
 
        if(data.editing_article) //update article if editing
        {
            data.group_articles_db.updateArticle(data.article.getId(), title_text, description_text, keyword_text, body_text, article_level, authors_text, "", groups, references);
            data.editing_article = false; //update singelton to show no longer editing
            data.article = null; // update singelton to remove article no longer working with
            data.sa_controller.getArticles();
        }else{ //create new article
            data.group_articles_db.addArticle(title_text, description_text, keyword_text, body_text, article_level, authors_text, "", groups, references);
            data.sa_controller.getArticles();
        }

        Utils.setContentArea(data.view_area, data.view_box);  // Navigate to manage articles view
    }

    /**
     * Searches for a group based on the input group ID and selects it in the UI.
     * 
     * @param event The action event triggered by the button click
     */
    @FXML
    void searchGroup(ActionEvent event) {
        int grp_id = -1;
        try{  //try to cast input to integer, return if not
            grp_id = Integer.parseInt(search_group.getText().toString());
        } catch (NumberFormatException e) {
           return; 
        }

        for (Group grp : groups_list) { //search groups for user inputed group id
            if (grp.getId() == grp_id) {
                group_table.getSelectionModel().select(grp); // Highlight the row if group is found
                group_table.scrollTo(grp); // Scroll to the row
                return;
            } else {
                //Utils.setLabel(error_label, "Group not Found", Color.RED); // Show error if group is not found
            }
        }
    }

    /**
     * Searches for an article based on the input article ID and selects it in the UI.
     * 
     * @param event The action event triggered by the button click
     */
    @FXML
    void searchArticle(ActionEvent event) {
        Long article_id = (long) -1;
        try{ //try to cast input to Long, return if not
            article_id = Long.parseLong(search_article.getText().toString());
        } catch (NumberFormatException e) {
            return;
        }

        for (Article art : articles_list) {  //search articles for user inputed article id
            if (art.getId() == article_id) {
                article_table.getSelectionModel().select(art); // Highlight the row if article is found
                article_table.scrollTo(art); // Scroll to the row
                return;
            } else {
                //Utils.setLabel(error_label, "Group not Found", Color.RED); // Show error if article is not found
            }
        }
    }
}
