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
import asu.cse360project.Group;
import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class SearchArticlesController implements Initializable{
    Singleton data = Singleton.getInstance();
    boolean add_special = false;
    String last_search = "";

    //table variables
    ObservableList<Article> articles_list  = FXCollections.observableArrayList();;
    ObservableList<Group> groups_list = FXCollections.observableArrayList();
    ObservableList<String> all_backups; // List to hold all group names
    ArrayList<Integer> selected_groups = new ArrayList<>();
    Article selectedArticle;
    Group selectedGroup;
    EncryptionHelper encryptionHelper;
    String selectedBackup; 
    Alert alert;

    @FXML private Label error_text;
    @FXML private ScrollPane scroll_pane;

    //buttons
    @FXML private MenuButton level_btn;
    @FXML private HBox top_pane;
    @FXML private VBox bottom_box;
    @FXML private StackPane botttom_pane;
    @FXML private ToggleButton edit_view_btn;

    //article table JavaFX elements
    @FXML private TableView<Article> article_table;
    @FXML private TableColumn<Article, String> article_abstract;
    @FXML private TableColumn<Article, String> article_author;
    @FXML private TableColumn<Article, String> article_title;
    @FXML private TableColumn<Article, String> article_id_col;

    //group table JavaFX elements
    @FXML private TableView<Group> group_table;
    @FXML private TableColumn<Group, String> group_id;
    @FXML private TableColumn<Group, String> group_name;

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


        scroll_pane.setOnMouseMoved(event -> {
            Utils.disableNode(error_text);
        });

        backups_list.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
            	selectedBackup = newSelection;
                //Utils.disableNode(error_label);
            }
        });

        disableByRole();

        addTooltips();
    }

    void addTooltips() {
        Tooltip editViewTooltip = new Tooltip("Toggle between edit view and search view.");
        Tooltip levelTooltip = new Tooltip("Set the difficulty level for filtering articles.");
        Tooltip editArticleTooltip = new Tooltip("Edit the selected article.");
    
        // Attach tooltips to buttons that exist
        edit_view_btn.setTooltip(editViewTooltip);
        level_btn.setTooltip(levelTooltip);
        edit_article_btn.setTooltip(editArticleTooltip);
    }

    private void disableByRole() {
        if (data.getAppUser().getLoginRole().equals("student")) {
            Utils.disableNode(edit_view_btn);
        }

        if (data.getAppUser().getLoginRole().equals("admin")) {
            Utils.disableNode(edit_article_btn);
        }
    }

    //get all articles from picked group
    public void getArticles() throws SQLException {
        User user = data.getAppUser();
        articles_list = data.group_articles_db.searchArticles(selected_groups, level, search_keywords, user.getId(), user.getLoginRole());
        article_table.setItems(articles_list);
    }

    //get all groups user can access
    public void getGroups() throws SQLException {
        groups_list.clear();
        if(!data.getAppUser().getLoginRole().equals("student")) {
            groups_list.add(new Group("Ungrouped", 0));
        }
        groups_list.addAll(data.group_articles_db.getAllSpecialGroups(data.getAppUser().getId()));
        group_table.setItems(groups_list);
    }
                  
    //helper method for setting up Article table
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
    }

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
    }

    @FXML   //set search level to advanced
    void setAdvanced(ActionEvent event) throws SQLException {
        level = "advanced";
        level_btn.setText("Advanced");
    }

    @FXML //set search level to beginner
    void setBeginner(ActionEvent event) throws SQLException {
        level = "beginner";
        level_btn.setText("Beginner");
    }

    @FXML //set search level to expert
    void setExpert(ActionEvent event) throws SQLException {
        level = "expert";
        level_btn.setText("Expert");
    }

    @FXML //set search level to all
    void setAll(ActionEvent event) throws SQLException {
        level = "all";
        level_btn.setText("All");
    }

    @FXML //set search level to Intermidiate
    void setIntermidiate(ActionEvent event) throws SQLException {
        level = "intermediate";
        level_btn.setText("Intermediate");
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
        if (data.getAppUser().getLoginRole().equals("admin")) { //dont decrypt body for admins
            body.setText("Body: " + selectedArticle.getBody()); // Set the body
        }else{
            body.setText("Body: " + encryptionHelper.decrypt(selectedArticle.getBody())); // Set the body
        }
        keywords.setText("Keywords: " + selectedArticle.getKeywords()); // Set the keywords
        references.setText("Refrences: " + selectedArticle.getReferences().toString()); // Set the references
    }

    /**
     * Handles the add article action.
     * @param event The action event.
     * @throws IOException If an I/O exception occurs.
    * @throws SQLException 
    */
    @FXML
    void addArticle(ActionEvent event) throws IOException, SQLException {
        data.editing_article = false; // Indicate that a new article is being created
        Utils.setContentArea(data.view_area, "create_edit_article"); // Set the content area to the create/edit article view
    }

    @FXML
    void addGroup(ActionEvent event) throws SQLException {
        add_special = false;
        boolean added = showGroupNamePopup();
        if(!added)
        {
            Utils.setLabel(error_text, "Error Inserting General Group", Color.RED);
        }else{
            getGroups();
        }
    }

    @FXML
    void addSAG(ActionEvent event) throws SQLException {
        add_special = true;
        System.out.println(add_special);
        boolean added = showGroupNamePopup();
        if(!added)
        {
            Utils.setLabel(error_text, "Error Inserting Special Group", Color.RED);
        }else{
            getGroups();
        }
    }

    @FXML
    void backupGroups(ActionEvent event) throws SQLException {
        if(selected_groups.size() > 0)
        {
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
                    ArrayList<Integer> all = new ArrayList<>();
                    all.add(-1);
                    data.group_articles_db.backup(all, fileName, data.getAppUser());
                    setBackupsTable();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

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

    @FXML
    void deleteGroup(ActionEvent event) throws SQLException {
        if(selectedGroup != null)
        {
            data.group_articles_db.deleteGroup(selectedGroup.getId());
            groups_list.remove(groups_list.indexOf(selectedGroup));
        }
    }

    @FXML
    void delete_backup(ActionEvent event) {
        if(selectedBackup != null)
        {
            data.user_db.deleteBackupFile(data.getAppUser().getUsername(), selectedBackup);
            setBackupsTable();
            removeFile(selectedBackup);
        }
    }

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

    @FXML
    void updateAccess(ActionEvent event) throws IOException, SQLException {
        if(selected_groups.size() > 1 || selected_groups.isEmpty())
        {
            Utils.setLabel(error_text, "Select one group to edit", null);
        }else{
            data.edit_group = selected_groups;
            Utils.setContentArea(data.view_area, "modify_SAG");
        }
        
    }

    @FXML
    void updateArticle(ActionEvent event) throws IOException, SQLException {
        if(selectedArticle != null) // Check if an article is selected
        {
            data.editing_article = true; // Indicate that an article is being edited
            data.article = selectedArticle; // Set the article to be edited
            Utils.setContentArea(data.view_area, "create_edit_article"); // Set the content area to the create/edit article view
        }
    }

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

    // Method to show the popup with the two options
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

    @FXML
    void selectGroup(ActionEvent event) throws SQLException {
        if(selectedGroup != null && !selected_groups.contains(selectedGroup.getId()))
        {
            selected_groups.add(selectedGroup.getId());
            groups_selected_txt.setText("Group: " + selected_groups.toString());
        }
    }

    @FXML
    void removeGroup(ActionEvent event) throws SQLException {
        if(selectedGroup != null && selected_groups.contains(selectedGroup.getId()))
        {
            selected_groups.remove(Integer.valueOf(selectedGroup.getId()));
            groups_selected_txt.setText("Groups: " + selected_groups.toString());
        }
    }
    
    private void setBackupsTable() {
        String list = data.user_db.getUserBackups(data.getAppUser().getUsername());
        System.out.println(list);
        if(list != "") {
            String[] backups_array  = list.split(",");
            all_backups = FXCollections.observableArrayList(backups_array);
            backups_list.setItems(all_backups);
        }
    }

}

