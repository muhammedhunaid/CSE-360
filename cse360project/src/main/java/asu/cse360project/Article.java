package asu.cse360project;

import java.util.ArrayList; 
import java.io.Serializable;

/**
 * Represents an academic article in the system with its associated metadata and content.
 * This class implements Serializable to support object persistence and provides comprehensive
 * management of article data including title, authors, abstract, keywords, and access controls.
 * 
 * The class supports different levels of article detail, from minimal (title and authors only)
 * to complete article information including references and group permissions.
 * 
 * @author Tu35
 * @version 2.00 2024-12-06 Enhanced documentation and access control features
 * @version 1.00 2024-10-30 Initial version
 */
public class Article implements Serializable {
    /** The title of the article */
    private String title;
    
    /** The authors of the article in a comma-separated format */
    private String authors;
    
    /** The abstract or summary of the article */
    private String abstractText;
    
    /** Keywords associated with the article for search and categorization */
    private String keywords;
    
    /** The main content/body of the article */
    private String body;
    
    /** The access level of the article (e.g., public, private, restricted) */
    private String level;
    
    /** Unique identifier for the article */
    private long id;
    
    /** Specific permissions for accessing the article */
    private String permissions;
    
    /** List of group IDs that have access to this article */
    private ArrayList<Integer> groups;
    
    /** List of article IDs that this article references */
    private ArrayList<Long> references;
    
    /** List of group names associated with this article for display purposes */
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

    /**
     * Sets the list of groups that have access to this article.
     * 
     * @param groups List of group IDs to set
     */
    public void setGroups(ArrayList<Integer> groups) {
        this.groups = groups;
    }

    /**
     * Sets the access level of the article.
     * 
     * @param level The new access level (e.g., public, private, restricted)
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * Sets the list of referenced articles.
     * 
     * @param references List of article IDs that this article references
     */
    public void setreferences(ArrayList<Long> references) {
        this.references = references;
    }

    /**
     * Sets the title of the article.
     * 
     * @param title The new title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the unique identifier of the article.
     * 
     * @param id The new ID to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Sets the list of group names associated with this article.
     * 
     * @param group_names List of group names to set
     */
    public void setGroup_names(ArrayList<String> group_names) {
        this.group_names = group_names;
    }

    /**
     * Sets the access permissions for the article.
     * 
     * @param permissions The new permissions string to set
     */
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    /**
     * Sets the authors of the article.
     * 
     * @param authors The new authors string to set (comma-separated)
     */
    public void setAuthors(String authors) {
        this.authors = authors;
    }

    /**
     * Sets the abstract text of the article.
     * 
     * @param abstractText The new abstract text to set
     */
    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    /**
     * Sets the keywords associated with the article.
     * 
     * @param keywords The new keywords to set
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * Sets the main content/body of the article.
     * 
     * @param body The new body text to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Clears all content fields of the article.
     * This includes title, authors, abstract, keywords, body, and references.
     * Note that this does not clear access control fields (groups, permissions, level).
     */
    public void clearArticle() {
        this.title = null;
        this.authors = null;
        this.abstractText = null;
        this.keywords = null;
        this.body = null;
        this.references = null;
    }
}
