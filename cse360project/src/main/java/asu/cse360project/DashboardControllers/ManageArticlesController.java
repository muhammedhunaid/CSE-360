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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


public class ManageArticlesController implements Initializable {
	Singleton data = Singleton.getInstance();
	private Article selectedArticle = null;
    ObservableList<Article> all_articles; 
	
	@FXML private TableColumn<Article, String> title_col;
    @FXML private TableColumn<Article, String> author_col;
    @FXML private TableView<Article> article_table;
    @FXML private Text title;
    @FXML private Text authors;
    @FXML private Text abstractText;
    @FXML private Text keywords;
    @FXML private Text body;
    @FXML private Text groups;
    @FXML private Text references;
    @FXML private Button delete;
    @FXML private Button add;
    @FXML private Button update;
    @FXML private Button back;
    @FXML private HBox content_area;


    @Override
	public void initialize(URL location, ResourceBundle resources) {
        title_col.setCellValueFactory(new PropertyValueFactory<>("title"));
        author_col.setCellValueFactory(new PropertyValueFactory<>("authors"));

        // Load all users from the database
        try {
            all_articles = data.group_articles_db.ListArticles(data.edit_group.getId());
        } catch (SQLException e) {
            System.err.println("error getting articles");
            e.printStackTrace();
        }

        article_table.setItems(all_articles);

        // Add listener to handle row selection in the table
        article_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedArticle = newSelection;
                viewArticle();
            }
        });
	}
    
    @FXML
    void add(ActionEvent event) throws IOException {
        data.editing_article = false;
        Utils.setContentArea("create_edit_article");
    }

    void viewArticle() {
        title.setText(selectedArticle.getTitle());
        authors.setText(selectedArticle.getAuthors());
        abstractText.setText(selectedArticle.getAbstractText());
        body.setText(selectedArticle.getBody());
        keywords.setText(selectedArticle.getKeywords());  
        references.setText(selectedArticle.getReferences().toString());      
        groups.setText(selectedArticle.getGroups().toString());    
    }

    @FXML
    void back(ActionEvent event) throws IOException {

    }

    @FXML
    void delete(ActionEvent event) {
    	
    }

    @FXML
    void update(ActionEvent event) throws IOException {
    	if(selectedArticle != null)
        {
            data.editing_article = true;
            data.article = selectedArticle;
            Utils.setContentArea("create_edit_article");
        }
    }  
}
