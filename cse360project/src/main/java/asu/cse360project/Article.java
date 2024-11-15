package asu.cse360project;

import java.util.ArrayList; 
import java.io.Serializable;

public class Article implements Serializable{

    /*******
     * <p> Article. </p>
     * 
     * <p> Description: This class is used to create and manage Article objects, which is used to store information and metadata about the article. </p>
     * 
     * <p> Copyright: Tu35 © 2024 </p>
     * 
     * @author Tu35
     * 
     * @version 1.00	2024-10-30 Created Article class for better organization of the code in Phase 2
     * 
     */
    
    private String title;
    private String authors;
    private String abstractText;
    private String keywords;
    private String body;
    private String level;
    private long id;
    private String permissions;
    private ArrayList<Integer> groups;
    private ArrayList<Long> references;
    private ArrayList<String> group_names;


    // Constructor to initialize the Article object
    public Article(String title, String authors, String abstractText, String keywords, String body, long id, String level, ArrayList<Integer> groups, ArrayList<Long> references, String permissions, ArrayList<String> group_names) {
        this.title = title;
        this.authors = authors;
        this.abstractText = abstractText;
        this.keywords = keywords;
        this.body = body;
        this.references = references;
        this.groups = groups;
        this.group_names = group_names;
        this.id = id;
        this.level = level;
        this.permissions = permissions;
    }

    public Article(String title, String authors, long id)
    {
        this.title = title;
        this.authors = authors;
        this.id = id;
    }

    public ArrayList<String> getGroup_names() {
        return group_names;
    }

    public String getLevel() {
        return level;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getPermissions() {
        return permissions;
    }

    public String getAuthors() {
        return authors;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getBody() {
        return body;
    }

    public ArrayList<Long> getReferences() {
        return references;
    }

    public long getId() {
        return id;
    }

    public ArrayList<Integer> getGroups() {
        return groups;
    }

    // Setters
    public void setGroups(ArrayList<Integer> groups) {
        this.groups = groups;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setreferences(ArrayList<Long> references) {
        this.references = references;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setGroup_names(ArrayList<String> group_names) {
        this.group_names = group_names;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void clearArticle(){
        this.title = null;
        this.authors = null;
        this.abstractText = null;
        this.keywords = null;
        this.body = null;
        this.references = null;
    }
}
