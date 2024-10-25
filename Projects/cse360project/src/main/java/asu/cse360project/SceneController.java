package asu.cse360project;

//Parent class for carrying user data between scenes
public class SceneController {
    private String username;

    public void setUser(String in)
    {
        username = in;
    }

    public String getUser()
    {
        return username;
    }
}
