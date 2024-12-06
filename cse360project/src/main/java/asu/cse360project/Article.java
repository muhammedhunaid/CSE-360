package asu.cse360project;

import java.util.ArrayList; 
import java.io.Serializable;

/**
 * Represents an academic article in the system with its associated metadata and content.
 * This class implements Serializable to support object persistence.
 * 
 * @author Tu35
 * @version 1.00 2024-10-30 Initial version
 */
public class Article implements Serializable{

    /*******
     * <p> Article. </p>
     * 
     * <p> Description: This class is used to create and manage Article objects, which is used to store information and metadata about the article. </p>
     * 
     * <p> Copyright: Tu35 2024 </p>
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


    /**
     * Constructs a new Article with complete details.
     * 
     * @param title The title of the article
     * @param authors The authors of the article
     * @param abstractText The abstract/summary of the article
     * @param keywords Keywords associated with the article
     * @param body The main content of the article
     * @param id Unique identifier for the article
     * @param level Access level of the article
     * @param groups List of group IDs that have access to this article
     * @param references List of referenced article IDs
     * @param permissions Access permissions for the article
     * @param group_names List of group names associated with the article
     */
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

    /**
     * Constructs a minimal Article object with only essential details.
     * Useful for creating article previews or references.
     * 
     * @param title The title of the article
     * @param authors The authors of the article
     * @param id Unique identifier for the article
     */
    public Article(String title, String authors, long id)
    {
        this.title = title;
        this.authors = authors;
        this.id = id;
    }

    /**
     * @return List of group names associated with this article
     */
    public ArrayList<String> getGroup_names() {
        return group_names;
    }

    /**
     * @return The access level of the article
     */
    public String getLevel() {
        return level;
    }

    /**
     * @return The title of the article
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The access permissions for the article
     */
    public String getPermissions() {
        return permissions;
    }

    /**
     * @return The authors of the article
     */
    public String getAuthors() {
        return authors;
    }

    /**
     * @return The abstract text of the article
     */
    public String getAbstractText() {
        return abstractText;
    }

    /**
     * @return Keywords associated with the article
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * @return The main content/body of the article
     */
    public String getBody() {
        return body;
    }

    /**
     * @return List of article IDs that this article references
     */
    public ArrayList<Long> getReferences() {
        return references;
    }

    /**
     * @return Unique identifier of the article
     */
    public long getId() {
        return id;
    }

    /**
     * @return List of group IDs that have access to this article
     */
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
