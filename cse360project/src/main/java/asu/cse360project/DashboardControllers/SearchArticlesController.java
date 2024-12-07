/**
 * Controller class for managing article search and display functionality in the application.
 * This controller provides a comprehensive interface for:
 * - Searching and filtering articles by keywords, groups, and difficulty levels
 * - Managing article groups and access permissions
 * - Creating and restoring backups of articles and groups
 * - Viewing and editing article content with encryption support
 * 
 * The controller implements role-based access control, allowing different
 * functionality for administrators, instructors, and students. It also
 * maintains search history and provides an intuitive interface for
 * article and group management.
 *
 * @author Tu35
 * @version 2.00 2024-12-06 Enhanced documentation and added encryption support
 * @version 1.00 2024-11-01 Initial version with search functionalities
 */
package asu.cse360project.DashboardControllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import asu.cse360project.Article;
import asu.cse360project.EncryptionHelpers.EncryptionHelper;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import asu.cse360project.Group;
import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Controller class for managing article search and display functionality in the application.
 * Provides features for searching, viewing, and managing articles and groups, including:
 * - Article searching and filtering
 * - Group management
 * - Access level control
 * - Backup management
 * 
 * This controller implements Initializable to support JavaFX initialization.
 * 
 * @author Tu35
 * @version 1.00 2024-11-01 Initial version with search functionalities
 */
public class SearchArticlesController implements Initializable{
    /** Singleton instance for managing application state */
    Singleton data = Singleton.getInstance();
    
    /** Flag for special addition mode */
    boolean add_special = false;
    
    /** Stores the last search query for history tracking */
    String last_search = "";
    
    /** Delay timer for search operations and error messages */
    PauseTransition pause;

    /** Observable list of articles for TableView */
    ObservableList<Article> articles_list = FXCollections.observableArrayList();
    
    /** Observable list of groups for TableView */
    ObservableList<Group> groups_list = FXCollections.observableArrayList();
    
    /** List of available backup names */
    ObservableList<String> all_backups;
    
    /** Currently selected group IDs for filtering */
    ArrayList<Integer> selected_groups = new ArrayList<>();
    
    /** Currently selected article in the UI */
    Article selectedArticle;
    
    /** Currently selected group in the UI */
    Group selectedGroup;
    
    /** Helper for encryption/decryption operations */
    EncryptionHelper encryptionHelper;
    
    /** Currently selected backup name */
    String selectedBackup;
    
    /** Alert dialog for user notifications */
    Alert alert;

    // FXML injected fields
    /** Label for displaying error messages */
    @FXML private Label error_text;
    
    /** Main scroll pane for the view */
    @FXML private ScrollPane scroll_pane;

    /** Dropdown button for selecting access level */
    @FXML private MenuButton level_btn;
    
    /** Container for top UI elements */
    @FXML private HBox top_pane;
    
    /** Container for bottom UI elements */
    @FXML private VBox bottom_box;
    
    /** Stack pane for layered UI elements */
    @FXML private StackPane botttom_pane;
    
    /** Toggle button for switching between edit and view modes */
    @FXML private ToggleButton edit_view_btn;

    // Article table components
    /** TableView for displaying articles */
    @FXML private TableView<Article> article_table;
    
    /** Column for article abstracts */
    @FXML private TableColumn<Article, String> article_abstract;
    
    /** Column for article authors */
    @FXML private TableColumn<Article, String> article_author;
    
    /** Column for article titles */
    @FXML private TableColumn<Article, String> article_title;
    
    /** Column for article IDs */
    @FXML private TableColumn<Article, String> article_id_col;

    // Group table components
    /** TableView for displaying groups */
    @FXML private TableView<Group> group_table;
    
    /** Column for group IDs */
    @FXML private TableColumn<Group, String> group_id;
    
    /** Column for group names */
    @FXML private TableColumn<Group, String> group_name;
    
    /** Column for group administrators */
    @FXML private TableColumn<Group, String> group_admin;

    //backups table
    @FXML private ListView<String> backups_list;

    //article elements
    @FXML private Text authors;
    @FXML private Text body;
    @FXML private Text description;
    @FXML private Text header;
    @FXML private Text keywords;
    @FXML private Text references;
    @FXML private Text title;

    //search settings
    String level = "all";
    String search_keywords = "";

    //text fields
    @FXML private TextField keyword_input;
    @FXML private TextField groups_input;
    @FXML private TextField articles_input;
    @FXML private Text groups_selected_txt;
    
    @FXML private Button edit_article_btn;

    /**
     * Initializes the controller after FXML injection is complete.
     * Sets up:
     * - Table columns and data bindings
     * - Event listeners for UI components
     * - Initial data loading
     * - Role-based access control
     * - Error handling and tooltips
     *
     * @param location The location used to resolve relative paths
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Initialize alert for confirmation dialogs
        alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Please confirm your action");

        data.sa_controller = this;
        data.view_area = botttom_pane;
        data.view_box = bottom_box;
        Utils.disableNode(top_pane);
        //make group table and set elements
        try {
            encryptionHelper = new EncryptionHelper();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //make article table set elements
        makeArticleTable();
        makeGroupTable();
        setBackupsTable();

        try {
            getGroups();
            getArticles();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        backups_list.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
            	selectedBackup = newSelection;
                //Utils.disableNode(error_label);
            }
        });

        pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> error_text.setVisible(false));

        disableByRole();

        addTooltips();
    }

    /**
     * Adds tooltips to UI components for better user experience.
     * Tooltips provide helpful descriptions of button functions and input fields.
     */
    void addTooltips() {
        Tooltip editViewTooltip = new Tooltip("Toggle between edit view and search view.");
        Tooltip levelTooltip = new Tooltip("Set the difficulty level for filtering articles.");
        Tooltip editArticleTooltip = new Tooltip("Edit the selected article.");
    
        // Attach tooltips to buttons that exist
        edit_view_btn.setTooltip(editViewTooltip);
        level_btn.setTooltip(levelTooltip);
        edit_article_btn.setTooltip(editArticleTooltip);
    }

    /**
     * Disables certain UI components based on user role.
     * - Students cannot edit articles or view admin columns
     * - Admins cannot edit articles directly
     */
    private void disableByRole() {
        if (data.getAppUser().getLoginRole().equals("student")) {
            Utils.disableNode(edit_view_btn);
            group_admin.setVisible(false);
        }

        if (data.getAppUser().getLoginRole().equals("admin")) {
            Utils.disableNode(edit_article_btn);
        }
    }

    /**
     * Retrieves and displays articles based on current filters.
     * Articles are filtered by:
     * - Selected groups
     * - Difficulty level
     * - Search keywords
     * - User's role and permissions
     *
     * @throws SQLException if database access fails
     */
    public void getArticles() throws SQLException {
        User user = data.getAppUser();
        articles_list = data.group_articles_db.searchArticles(selected_groups, level, search_keywords, user.getId(), user.getLoginRole());
        article_table.setItems(articles_list);
    }

    /**
     * Retrieves and displays groups accessible to the current user.
     * For non-student users, includes an "Ungrouped" option.
     * Also loads special groups based on user permissions.
     *
     * @throws SQLException if database access fails
     */
    public void getGroups() throws SQLException {
        groups_list.clear();
        if(!data.getAppUser().getLoginRole().equals("student")) {
            groups_list.add(new Group("Ungrouped", 0));
        }
        groups_list.addAll(data.group_articles_db.getAllSpecialGroups(data.getAppUser().getId()));
        group_table.setItems(groups_list);
    }
                  
    /**
     * Helper method for setting up Article table.
     * Configures table columns and data bindings.
     */
    private void makeArticleTable() {
        // Set up table columns to display Article information
        article_title.setCellValueFactory(new PropertyValueFactory<>("title"));
        article_author.setCellValueFactory(new PropertyValueFactory<>("authors"));
        article_abstract.setCellValueFactory(new PropertyValueFactory<>("abstractText"));
        article_id_col.setCellValueFactory(new PropertyValueFactory<>("id"));

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
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && selectedArticle != null) {
                try {
                    viewArticle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * Helper method for setting up Group table.
     * Configures table columns and data bindings.
     */
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
            }
        });

        group_table.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && selectedGroup != null) {
                try {
                    if(selected_groups.contains(selectedGroup.getId())) {
                        removeGroup(new ActionEvent());
                    }else{
                        selectGroup(new ActionEvent());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        group_admin.setCellValueFactory(cellData -> {
            Group g = cellData.getValue();
            return new SimpleStringProperty(g.isAdmin(data.getAppUser()) ? "Y" : "N");
        });
    }

    /**
     * Creates a wrapped text cell for table columns.
     * Enables text wrapping in table cells for better content display.
     *
     * @param <T> The type of the TableColumn
     * @param column The column to apply wrapping to
     */
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

    /**
     * Handles article search based on keywords.
     * Updates the search history if the search criteria changes.
     *
     * @param event The triggering action event
     * @throws SQLException if database access fails
     */
    @FXML //set get keywords searched
    void searchArticles(ActionEvent event) throws SQLException {
        search_keywords = keyword_input.getText();
        if(!groups_list.isEmpty() || !search_keywords.isEmpty() || !level.equals("all")) {
            String search = "Group(s): " + data.group_articles_db.getGroupNames(selected_groups) + 
                            " | Keywords: " + search_keywords +
                            " | Level: " + level; 

            if (last_search != search)
            {
                data.message_db.addSearch(search, data.getAppUser().getId());
                last_search = search;
            }
        }
        getArticles();
    }

    /**
     * Searches for a specific article by ID or title.
     * Scrolls to and selects the matching article if found.
     *
     * @param event The triggering action event
     */
    @FXML
    void SearchArticles(ActionEvent event) {
        Long article_id = (long) -1;
        String article_name = groups_input.getText().toString();
        boolean num = true;
        try{ //try to cast input to Long, return if not
            article_id = Long.parseLong(articles_input.getText().toString());
        } catch (NumberFormatException e) {
            num = false;
        }

        for (Article art : articles_list) {  //search articles for user inputed article id
            if(num && art.getId() == article_id)
            {
                article_table.getSelectionModel().select(art); // Highlight the row if group is found
                article_table.scrollTo(art); // Scroll to the row
                return;
            } else if(art.getTitle().equals(article_name)) {
                article_table.getSelectionModel().select(art); // Highlight the row if group is found
                article_table.scrollTo(art); // Scroll to the row
                return;
            }
        }
        Utils.setLabel(error_text, "Group not Found", Color.RED); // Show error if article is not found
        pause.play();
    }

    /**
     * Searches for a specific group by ID or name.
     * Scrolls to and selects the matching group if found.
     *
     * @param event The triggering action event
     */
    @FXML
    void searchGroups(ActionEvent event) {
        int grp_id = -1;
        String group_name = groups_input.getText().toString();
        boolean num = true;
        try {
            grp_id = Integer.parseInt(group_name);
        } catch (NumberFormatException e) {
            num = false;
        }

        for (Group grp : groups_list) { //search groups for user inputed group id
            if(num && grp.getId() == grp_id)
            {
                group_table.getSelectionModel().select(grp); // Highlight the row if group is found
                group_table.scrollTo(grp); // Scroll to the row
                return;
            } else if(grp.getName().equals(group_name)) {
                group_table.getSelectionModel().select(grp); // Highlight the row if group is found
                group_table.scrollTo(grp); // Scroll to the row
                return;
            }
        }
        Utils.setLabel(error_text, "Article not Found", Color.RED); // Show error if group is not found
        pause.play();
    }

    /**
     * Updates the level filter to show only advanced articles.
     *
     * @param event The triggering action event
     * @throws SQLException if database access fails
     */
    @FXML   //set search level to advanced
    void setAdvanced(ActionEvent event) throws SQLException {
        level = "advanced";
        level_btn.setText("Advanced");
    }

    /**
     * Updates the level filter to show only beginner articles.
     *
     * @param event The triggering action event
     * @throws SQLException if database access fails
     */
    @FXML //set search level to beginner
    void setBeginner(ActionEvent event) throws SQLException {
        level = "beginner";
        level_btn.setText("Beginner");
    }

    /**
     * Updates the level filter to show only expert articles.
     *
     * @param event The triggering action event
     * @throws SQLException if database access fails
     */
    @FXML //set search level to expert
    void setExpert(ActionEvent event) throws SQLException {
        level = "expert";
        level_btn.setText("Expert");
    }

    /**
     * Removes all level filters to show articles of all levels.
     *
     * @param event The triggering action event
     * @throws SQLException if database access fails
     */
    @FXML //set search level to all
    void setAll(ActionEvent event) throws SQLException {
        level = "all";
        level_btn.setText("All");
    }

    /**
     * Updates the level filter to show only intermediate articles.
     *
     * @param event The triggering action event
     * @throws SQLException if database access fails
     */
    @FXML //set search level to Intermidiate
    void setIntermidiate(ActionEvent event) throws SQLException {
        level = "intermediate";
        level_btn.setText("Intermediate");
    }

    /**
     * Displays the selected article's content in the view area.
     * Decrypts the article body if the user is not an admin.
     *
     * @throws Exception if decryption fails
     */
    private void viewArticle() throws Exception { 
        String headTxt = "id: " + String.valueOf(selectedArticle.getId()); // Article ID
        headTxt += "    Groups: " + selectedArticle.getGroup_names().toString(); // Article groups
        headTxt += "    Level: " + selectedArticle.getLevel(); // Article level
        headTxt += "    Permissions: " + selectedArticle.getPermissions(); // Article permissions
        header.setText(headTxt); // Set the header text
        title.setText(selectedArticle.getTitle()); // Set the title
        authors.setText("Authors: " + selectedArticle.getAuthors()); // Set the authors
        description.setText("Desciption: " + selectedArticle.getAbstractText()); // Set the description
        if (data.getAppUser().getLoginRole().equals("admin")) { //dont decrypt body for admins
            body.setText("Body: " + selectedArticle.getBody()); // Set the body
        }else{
            body.setText("Body: " + encryptionHelper.decrypt(selectedArticle.getBody())); // Set the body
        }
        keywords.setText("Keywords: " + selectedArticle.getKeywords()); // Set the keywords
        references.setText("Refrences: " + selectedArticle.getReferences().toString()); // Set the references
    }

    /**
     * Initiates the article creation process.
     * Switches to the create/edit article view.
     *
     * @param event The triggering action event
     * @throws IOException if view navigation fails
     * @throws SQLException if database access fails
     */
    @FXML
    void addArticle(ActionEvent event) throws IOException, SQLException {
        data.editing_article = false; // Indicate that a new article is being created
        Utils.setContentArea(data.view_area, "create_edit_article"); // Set the content area to the create/edit article view
    }

    /**
     * Shows a dialog for creating a new group.
     * Updates the groups list after successful creation.
     *
     * @param event The triggering action event
     * @throws SQLException if database access fails
     */
    @FXML
    void addGroup(ActionEvent event) throws SQLException {
        add_special = false;
        boolean added = showGroupNamePopup();
        if(!added)
        {
            Utils.setLabel(error_text, "Error Inserting General Group", Color.RED);
            pause.play();
        }else{
            getGroups();
        }
    }

    /**
     * Creates a special group.
     * Updates the groups list after successful creation.
     *
     * @param event The triggering action event
     * @throws SQLException if database access fails
     */
    @FXML
    void addSAG(ActionEvent event) throws SQLException {
        add_special = true;
        System.out.println(add_special);
        boolean added = showGroupNamePopup();
        if(!added)
        {
            Utils.setLabel(error_text, "Error Inserting Special Group", Color.RED);
            pause.play();
        }else{
            getGroups();
        }
    }

    /**
     * Creates a backup of the current group data.
     * Allows users to specify a backup name.
     *
     * @param event The triggering action event
     * @throws SQLException if database access fails
     */
    @FXML
    void backupGroups(ActionEvent event) throws SQLException {
        if(selected_groups.size() > 0)
        {
            if(!adminOfGroups())
            {
                Utils.setLabel(error_text, "You are not an admin of one of the selected groups to backup", Color.RED);
                pause.play();
                return;
            }
            // Create a TextInputDialog
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Backup File Input");
            dialog.setHeaderText("Enter the Backup File Name");
            dialog.setContentText("File Name:");

            // Show the dialog and wait for the response
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(fileName -> {
                try {
                    if(all_backups == null || !all_backups.contains(fileName))
                    {
                        data.group_articles_db.backup(selected_groups, fileName, data.getAppUser());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
        setBackupsTable();
    }

    /**
     * Creates a backup of all system data.
     * Includes articles, groups, and user data.
     *
     * @param event The triggering action event
     * @throws SQLException if database access fails
     */
    @FXML
    void backupAll(ActionEvent event) throws SQLException {
        // Create a TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Backup File Input");
        dialog.setHeaderText("Enter the Backup File Name");
        dialog.setContentText("File Name:");

        // Show the dialog and wait for the response
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(fileName -> {
            try {
                if(all_backups == null || !all_backups.contains(fileName))
                {
                    ArrayList<Integer> all = getAdminGroups();
                    data.group_articles_db.backup(all, fileName, data.getAppUser());
                    setBackupsTable();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Deletes the selected article after confirmation.
     * Only available to users with appropriate permissions.
     *
     * @param event The triggering action event
     * @throws SQLException if database access fails
     */
    @FXML
    void deleteArticle(ActionEvent event) {
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
                        articles_list.remove(articles_list.indexOf(selectedArticle));
                    } catch (SQLException e) {
                        e.printStackTrace(); // Handle exception
                    }
                }
            });
        }
    }

    /**
     * Deletes the selected group after confirmation.
     * Only available to group administrators.
     *
     * @param event The triggering action event
     * @throws SQLException if database access fails
     */
    @FXML
    void deleteGroup(ActionEvent event) throws SQLException {
        if(selectedGroup == null) {
            Utils.setLabel(error_text, "No group selected", Color.RED);
            pause.play();
            return;
        }

        if(!selectedGroup.isAdmin(data.getAppUser())) {
            Utils.setLabel(error_text, "Not an admin of the group your trying to delete", Color.RED);
            pause.play();
            return;
        }

        data.group_articles_db.deleteGroup(selectedGroup.getId());
        groups_list.remove(groups_list.indexOf(selectedGroup));
    }

    /**
     * Deletes the selected backup file.
     *
     * @param event The triggering action event
     */
    @FXML
    void delete_backup(ActionEvent event) {
        if(selectedBackup != null)
        {
            data.user_db.deleteBackupFile(data.getAppUser().getUsername(), selectedBackup);
            setBackupsTable();
            removeFile(selectedBackup);
        }
    }

    /**
     * Removes a file from the file system.
     *
     * @param file_name The name of the file to remove
     */
    private void removeFile(String file_name) {
        String filePath = "Backups/" + file_name; // Update this to the file you want to delete
        File file = new File(filePath);

        // Check if the file exists and delete it
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("File deleted successfully: " + filePath);
            } else {
                System.out.println("Failed to delete the file: " + filePath);
            }
        } else {
            System.out.println("File not found: " + filePath);
        }
    }

    /**
     * Merges data from a selected backup.
     * Requires confirmation before proceeding.
     *
     * @param event The triggering action event
     * @throws SQLException if database access fails
     */
    @FXML
    void merge(ActionEvent event) {
        if(selectedBackup != null)
        {
            alert.setContentText("Are you sure you want to Merge and Restore backup");
            alert.showAndWait().ifPresent(response -> {
                try {
                    data.group_articles_db.restoreMerge(selectedBackup);
                    getGroups();
                    getArticles(); 
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception EncryptionError){
                    EncryptionError.printStackTrace();
                }              
            });
            
        }
    }

    /**
     * Restores data from a selected backup.
     * Requires confirmation before proceeding.
     *
     * @param event The triggering action event
     * @throws SQLException if database access fails
     */
    @FXML
    void restore(ActionEvent event) {
        if(selectedBackup != null)
        {
            alert.setContentText("Are you sure you want to Restore backup");
            alert.showAndWait().ifPresent(response -> {
                try {
                    data.group_articles_db.restore(selectedBackup);
                    getGroups();
                    getArticles();   
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception EncryptionError){
                    EncryptionError.printStackTrace();
                }
            });
            
        }
    }

    /**
     * Updates access permissions for a selected group.
     * Only available to group administrators.
     *
     * @param event The triggering action event
     * @throws IOException if view navigation fails
     * @throws SQLException if database access fails
     */
    @FXML
    void updateAccess(ActionEvent event) throws IOException, SQLException {
        if(selected_groups.size() > 1 || selected_groups.isEmpty())
        {
            Utils.setLabel(error_text, "Select one group to edit access", Color.RED);
            pause.play();
        }else if(adminOfGroups()) {
            data.edit_group = selected_groups;
            Utils.setContentArea(data.view_area, "modify_SAG");
        }else{
            Utils.setLabel(error_text, "not an admin of the selected group", Color.RED);
            pause.play();
        }
        
    }

    /**
     * Edits the selected article.
     * Switches to the create/edit article view.
     *
     * @param event The triggering action event
     * @throws IOException if view navigation fails
     * @throws SQLException if database access fails
     */
    @FXML
    void updateArticle(ActionEvent event) throws IOException, SQLException {
        if(selectedArticle != null) // Check if an article is selected
        {
            data.editing_article = true; // Indicate that an article is being edited
            data.article = selectedArticle; // Set the article to be edited
            Utils.setContentArea(data.view_area, "create_edit_article"); // Set the content area to the create/edit article view
        }
    }

    /**
     * Toggles between edit and view modes.
     * Updates UI components based on the selected mode.
     *
     * @param event The triggering action event
     */
    @FXML   //change to edit view
    void editView(ActionEvent event) throws IOException {
        if(edit_view_btn.isSelected())
        {
            Utils.enableNode(top_pane);
            edit_view_btn.setText("Search View");
        }

        if(!edit_view_btn.isSelected())
        {
            Utils.disableNode(top_pane);
            Utils.setContentArea(data.view_area, data.view_box);
            edit_view_btn.setText("Edit View");
        }
    }

    /**
     * Shows a dialog for creating a new group.
     * Returns true if the group is created successfully.
     *
     * @return true if the group is created, false otherwise
     * @throws SQLException if database access fails
     */
    private boolean showGroupNamePopup() throws SQLException {
        // Create a TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Group Name Input");
        dialog.setHeaderText("Enter the name of the group:");
        dialog.setContentText("Group Name:");

        // Show the dialog and wait for the user's input
        Optional<String> result = dialog.showAndWait();

        // Check if the user entered a value
        if (result.isPresent() && !result.get().isBlank()) {
            String groupName = result.get();
            return data.group_articles_db.createGroup(groupName, add_special, data.getAppUser().getId());
        } else {
            return false; // Input cancelled or invalid
        }
    }

    /**
     * Selects a group for filtering.
     * Adds the group ID to the selected groups list.
     *
     * @param event The triggering action event
     * @throws SQLException if database access fails
     */
    @FXML
    void selectGroup(ActionEvent event) throws SQLException {
        if(selectedGroup != null && !selected_groups.contains(selectedGroup.getId()))
        {
            selected_groups.add(selectedGroup.getId());
            groups_selected_txt.setText("Group: " + selected_groups.toString());
        }
    }

    /**
     * Removes a group from the selected groups list.
     *
     * @param event The triggering action event
     * @throws SQLException if database access fails
     */
    @FXML
    void removeGroup(ActionEvent event) throws SQLException {
        if(selectedGroup != null && selected_groups.contains(selectedGroup.getId()))
        {
            selected_groups.remove(Integer.valueOf(selectedGroup.getId()));
            groups_selected_txt.setText("Groups: " + selected_groups.toString());
        }
    }
    
    /**
     * Sets up the backups table with available backup names.
     */
    private void setBackupsTable() {
        String list = data.user_db.getUserBackups(data.getAppUser().getUsername());
        System.out.println(list);
        if(list != "") {
            String[] backups_array  = list.split(",");
            all_backups = FXCollections.observableArrayList(backups_array);
            backups_list.setItems(all_backups);
        }
    }

    /**
     * Checks if the user is an administrator of the selected groups.
     *
     * @return true if the user is an admin, false otherwise
     * @throws SQLException if database access fails
     */
    public boolean adminOfGroups() throws SQLException {
        for(int group_id : selected_groups) {
            Group g = data.group_articles_db.getGroup(group_id);
            if(group_id > 0 && !g.isAdmin(data.getAppUser())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves a list of groups that the user is an administrator of.
     *
     * @return A list of group IDs
     * @throws SQLException if database access fails
     */
    public ArrayList<Integer> getAdminGroups() throws SQLException {
        ArrayList<Integer> all = new ArrayList<>();
        for(Group group: groups_list) {
            if(group.getId() == 0) {
                all.add(0);
            }else{
                if(group.getAdmin_users() != null && group.getAdmin_users().contains(data.getAppUser())) {
                    all.add(group.getId());
                }
            }
        }
        return all;
    }

    /**
     * Sets the groups list for the controller.
     *
     * @param groups_list The new groups list
     */
    public void setGroups_list(ObservableList<Group> groups_list) {
        this.groups_list = groups_list;
    }

    /**
     * Sets the singleton instance for the controller.
     *
     * @param data The new singleton instance
     */
    public void setData(Singleton data) {
        this.data = data;
    }
}
