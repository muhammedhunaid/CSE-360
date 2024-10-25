package asu.cse360project.DashboardControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import asu.cse360project.App;
import asu.cse360project.Article;
import asu.cse360project.Singleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class ManageArticle implements Initializable {
	Singleton data = Singleton.getInstance();
	private Article selectedArticle = null;
	private static Scene scene;
	private Stage stage;
	
	
	@FXML private TableColumn<Article, String> articleCol;
    @FXML private TableView<Article> articleList;
    @FXML private Text title;
    @FXML private Text authors;
    @FXML private Text abstractText;
    @FXML private Text keywords;
    @FXML private Text body;
    @FXML private Text references;
    @FXML private Button delete;
    @FXML private Button add;
    @FXML private Button update;
    @FXML private Button back;

    @Override
	public void initialize(URL location, ResourceBundle resources) {
    	//Sets up column
    	articleCol.setCellValueFactory(new PropertyValueFactory<Article, String>("title"));
    	//Loads test data
    	articleList.setItems(articles_list());
    	//Article is selected from list
		articleList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedArticle = newSelection;
                setTitle();
                setAuthors();
                setAbstract();
                setKeywords();
                setBody();
                setReferences();
                deleteActivated();
            }
        });
	}

    public ObservableList<Article> articles_list(){
    	ObservableList<Article> articles = FXCollections.observableArrayList();
    	articles.add(new Article("Test", "Tyler", "This is a Test", "Test, Test 1, Test One", "THis is a test to see if this works", "nothing"));
    	articles.add(new Article("Again Test", "Tyler", "This is a Test", "Test, Test 1, Test One", "THis is a test to see if this works", "nothing"));
    	articles.add(new Article("Again Again Test", "Tyler", "This is a Test", "Test, Test 1, Test One", "THis is a test to see if this works", "nothing"));
    	return articles;
    }
    
    @FXML
    void add(ActionEvent event) throws IOException {
    	Parent root = FXMLLoader.load(App.class.getResource("DashboardScenes/create_edit_article.fxml"));
        scene = new Scene(root, 700, 700);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void back(ActionEvent event) throws IOException {
    	Parent root = FXMLLoader.load(App.class.getResource("DashboardScenes/dashboard.fxml"));
        scene = new Scene(root, 700, 700);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void delete(ActionEvent event) {
    	ObservableList<Article> articleSelected = articleList.getSelectionModel().getSelectedItems();
    	ObservableList<Article> allArticles = articleList.getItems();
    	
    	for (Article article: articleSelected) {
    		allArticles.remove(article);
    	}
    }

    @FXML
    void update(ActionEvent event) throws IOException {
    	Parent root = FXMLLoader.load(App.class.getResource("DashboardScenes/create_edit_article.fxml"));
        scene = new Scene(root, 700, 700);
        stage.setScene(scene);
        stage.show();
        
        // TODO: input into the title, description, and body text boxes the already entered strings
    }
    
    @FXML
    void setTitle() {
    	title.setText(selectedArticle.getTitle());
    }
    
    @FXML
    void setAuthors() {
		authors.setText(selectedArticle.getAuthors());
    }
    
    @FXML
    void setAbstract() {
    	abstractText.setText(selectedArticle.getAbstractText());
    }
    
    @FXML
    void setKeywords() {
    	keywords.setText(selectedArticle.getKeywords());
    }
    
    @FXML
    void setBody() {
    	body.setText(selectedArticle.getBody());
    }
    
    @FXML
    void setReferences() {
    	references.setText(selectedArticle.getReferences());
    }
    
    @FXML
    void deleteActivated() {
    	delete.setDisable(false);
    	delete.setOpacity(1.0);
    }
    
}
