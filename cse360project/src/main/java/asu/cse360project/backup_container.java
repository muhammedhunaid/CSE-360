package asu.cse360project;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Container class for storing collections of Articles and Groups during backup operations.
 * This class implements Serializable to support persistence of the entire backup state,
 * allowing for data to be saved to disk and restored later.
 * 
 * The class serves as a data transfer object (DTO) for backup/restore operations,
 * encapsulating both articles and their associated groups in a single serializable unit.
 *
 * @author Tu35
 * @version 1.00 2024-10-30 Initial version with basic backup functionality
 */
public class backup_container implements Serializable {
    /** 
     * Collection of articles to be backed up.
     * Public access is provided for direct serialization/deserialization.
     */
    public ArrayList<Article> articles;

    /** 
     * Collection of groups to be backed up.
     * Public access is provided for direct serialization/deserialization.
     */
    public ArrayList<Group> groups;

    /**
     * Constructs a new backup container with specified collections.
     * Both collections are stored as-is without creating defensive copies,
     * as this class is meant for temporary storage during backup operations.
     *
     * @param articles Collection of articles to backup
     * @param groups Collection of groups to backup
     */
    public backup_container(ArrayList<Article> articles, ArrayList<Group> groups) {
        this.articles = articles;
        this.groups = groups;
    }
}