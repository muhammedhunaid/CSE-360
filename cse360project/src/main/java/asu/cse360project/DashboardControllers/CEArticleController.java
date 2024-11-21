package asu.cse360project.DashboardControllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import asu.cse360project.*;
import asu.cse360project.EncryptionHelpers.EncryptionHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory; 

/**
 * 
 * <p> CEArticleController class </p>
 * 
 * <p> The CEArticleController class manages the Create/Edit Article interface in a JavaFX application.
 * This controller allows users to create, edit, and manage articles with various properties, including title,
 * authors, abstract, body, keywords, and permissions. Users can link articles to multiple groups and add 
 * cross-references to other articles.  </p>
 * 
 * <p> Copyright: Tu35 © 2024 </p>
 * 
 * @author Tu35
 * 
 * @version 1.00	2024-11-18 JavaFX controller class for creating and editing articles
 * 
 */
public class CEArticleController implements Initializable {

     // Singleton instance for accessing shared data
    Singleton data = Singleton.getInstance();
    String article_level = "";
    ObservableList<Article> articles_list;
    ObservableList<Group> groups_list;
    ArrayList<Integer> groups = new ArrayList<>();
    ArrayList<Long> refrences = new ArrayList<>();
    private Article selectedArticle = null;
    private Group selectedGroup = null;
    EncryptionHelper encryptionHelper;


    //////////JavaFX elements ///////////////////////////

    // tableViews and columns
    @FXML private TableColumn<Article, String> article_col;
    @FXML private TableColumn<Article, String> authors_col;
    @FXML private TableColumn<Article, Long> article_id_col;
    @FXML private TableView<Article> article_table;
    @FXML private TableColumn<Group, String> group_col;
    @FXML private TableColumn<Group, Integer> group_id_col;
    @FXML private TableView<Group> group_table;

    //text input
    @FXML private TextArea body;
    @FXML private TextField description;
    @FXML private TextField keywords;
    @FXML private TextField authors;
    @FXML private TextField search_article;
    @FXML private TextField search_group;
    @FXML private TextField title;

    //labels
    @FXML private Label group_links;
    @FXML private Label article_links;
    @FXML private Label title_label;

    //user input
    @FXML private MenuButton level;
    /////////////////////////////////////////////////////

    // Initializes the controller and sets up tables ahd populates with groups and articles
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
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

    // Populates the UI fields with the selected article’s content
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
        refrences = article.getReferences(); //set refrences
        groups = article.getGroups(); //sert groups
    }

    // Removes the selected article from articles references
    @FXML
    void removeArticle(ActionEvent event) {
        if(selectedArticle != null) 
        {
            refrences.remove(Long.valueOf(selectedArticle.getId())); //remove article from refrences 
            article_links.setText("Article Links: " + refrences.toString()); //update UI
        }
    }

    // Removes the selected group from the group links
    @FXML
    void removeGroup(ActionEvent event) {
        if(selectedGroup != null)
        {
            groups.remove(Integer.valueOf(selectedGroup.getId())); //remove group from group links
            group_links.setText("Groups links: " + groups.toString()); //update UI
        }
    }

    // Adds the selected article to the references list
    @FXML
    void addArticle(ActionEvent event) {
        if(selectedArticle != null && !refrences.contains(selectedArticle.getId()))
        {
            refrences.add(selectedArticle.getId()); //add article to list of article refrences
            article_links.setText("Article Links: " + refrences.toString()); //update UI
        }
    }

     // Adds the selected group to the group links list
    @FXML
    void addGroup(ActionEvent event) {
        if(selectedGroup != null)
        {
            groups.add(selectedGroup.getId()); //add group to list of article groups
            group_links.setText("Groups links: " + groups.toString()); //update UI
        }
    }

    // Sets the article level to 'advanced'
    @FXML
    void advanced(ActionEvent event) {
        article_level = "advanced";
        level.setText("advanced");
    }

     // Sets the article level to 'beginner'
    @FXML
    void beginner(ActionEvent event) {
        article_level = "beginner";
        level.setText("beginner");
    }

    // Sets the article level to 'expert'
    @FXML
    void expert(ActionEvent event) {
        article_level = "expert";
        level.setText("expert");
    }

    // Sets the article level to 'intermediate'
    @FXML
    void intermediate(ActionEvent event) {
        article_level = "intermediate";
        level.setText("intermediate");
    }

     // Cancels the editing or creation of an article and returns to the manage articles page
    @FXML
    void cancel(ActionEvent event) throws IOException {
        Utils.setContentArea(data.view_area, data.view_box);  // Navigate to manage articles view
    }

    void setLevel(String level_txt) {
        level.setText(level_txt);
    }

    // Submits the article by adding it or updating it in the database
    @FXML
    void submit(ActionEvent event) throws IOException, SQLException, Exception {
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
            data.group_articles_db.updateArticle(data.article.getId(), title_text, description_text, keyword_text, encryptionHelper.decrypt(body_text), article_level, authors_text, "", groups, refrences);
            data.editing_article = false; //update singelton to show no longer editing
            data.article = null; // update singelton to remove article no longer working with
            data.sa_controller.getArticles();
        }else{ //create new article
            data.group_articles_db.addArticle(title_text, description_text, keyword_text, body_text, article_level, authors_text, "", groups, refrences);
            data.sa_controller.getArticles();
        }

        Utils.setContentArea(data.view_area, data.view_box);  // Navigate to manage articles view
    }

    // Searches for a group based on the input group ID and selects it in the UI
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

     // Searches for an article based on the input article ID and selects it in the UI
    @FXML
    void searchArticle(ActionEvent event) {
        Long article_id = (long) -1;
        try{ //try to cast input to Long, return if not
            article_id = Long.parseLong(search_group.getText().toString());
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
