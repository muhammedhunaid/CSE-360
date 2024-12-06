/*******
 * <p> backup_container. </p>
 * 
 * <p> Description: A class used to store collections of `Article` and `Group` objects
 * that are intended to be backed up or serialized for later use or restoration. </p>
 * 
 * <p> Copyright: Tu35 2024 </p>
 * 
 * @author Tu35
 * 
 * @version 1.00	2024-10-30 Initial version with basic backup functionality
 * 
 */
package asu.cse360project;

import java.io.Serializable;
import java.util.ArrayList;

public class backup_container implements Serializable {

    // A list of Article objects to be backed up
    public ArrayList<Article> articles;

    // A list of Group objects to be backed up
    public ArrayList<Group> groups;

    /**
     * Constructs a new `backup_container` object with the specified lists of articles and groups.
     * 
     * @param articles The list of Article objects to be included in the backup
     * @param groups The list of Group objects to be included in the backup
     */
    public backup_container(ArrayList<Article> articles, ArrayList<Group> groups) {
        this.articles = articles;
        this.groups = groups;
    }
}