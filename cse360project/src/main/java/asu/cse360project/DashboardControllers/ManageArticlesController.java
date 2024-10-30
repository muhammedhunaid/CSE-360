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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
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
    @FXML private Text description;
    @FXML private Text keywords;
    @FXML private Text body;
    @FXML private Text references;
    @FXML private Text header;

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
        setTable();
        article_table.setItems(all_articles);

        // Add listener to handle row selection in the table
        article_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedArticle = newSelection;
                viewArticle();
            }
        });

        article_table.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    try {
                        update(new ActionEvent());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
            }
        });
	}

    private void setTable()
    {
        try {
            all_articles = data.group_articles_db.ListMultipleGroupsArticles(data.edit_group);
            article_table.setItems(all_articles);
        } catch (SQLException e) {
            System.err.println("error getting articles");
            e.printStackTrace();
        }
    }
    
    @FXML
    void add(ActionEvent event) throws IOException {
        data.editing_article = false;
        Utils.setContentArea("create_edit_article");
    }

    void viewArticle() {
        String headTxt = "id: " + String.valueOf(selectedArticle.getId());
        headTxt += "    Groups: " + selectedArticle.getGroup_names().toString();
        headTxt += "    Level: " + selectedArticle.getLevel(); 
        headTxt += "    Permissions: " + selectedArticle.getPermissions();   
        header.setText(headTxt); 
        title.setText(selectedArticle.getTitle());
        authors.setText("Authors: " + selectedArticle.getAuthors());
        description.setText("Desciption: " + selectedArticle.getAbstractText());
        body.setText("Body: " + selectedArticle.getBody());
        keywords.setText("Keywords: " + selectedArticle.getKeywords());  
        references.setText("Refrences: " + selectedArticle.getReferences().toString());        
    }

    @FXML
    void back(ActionEvent event) throws IOException {
        Utils.setContentArea("group_dashboard");
    }

    @FXML
    void delete(ActionEvent event) {
    	if(selectedArticle != null)
        {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText("Are you sure you want to delete this article?");
            alert.setContentText("Article ID: " + selectedArticle.getId());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        data.group_articles_db.deleteArticle(selectedArticle.getId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                setTable();
            });
        }
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
