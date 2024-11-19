package asu.cse360project;

import java.io.Serializable;
import java.util.ArrayList;

public class backup_container implements Serializable {
    public ArrayList<Article> articles;
    public ArrayList<Group> groups;

    public backup_container(ArrayList<Article> articles,  ArrayList<Group> groups)
    {
        this.articles = articles;
        this.groups = groups;
    }
}