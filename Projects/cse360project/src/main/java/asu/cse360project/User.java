package asu.cse360project;

public class User {
    private int id;
    private String username;
    private String role;
    private String login_role = "";

    public User(int id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public int getId() 
    {
        return id;
    }

    public String getUsername() 
    {
        return username;
    }

    public String getRole() 
    {
        return role;
    }

    public boolean isAdmin()
    {
        if(role.contains("admin"))
        {
            return true;
        }
        return false;
    }

    public boolean isInstructor() 
    {
        if(role.contains("instructor"))
        {
            return true;
        }
        return false;
    }

    public boolean isStudent() 
    {
        if(role.contains("student"))
        {
            return true;
        }
        return false;
    }

    public void setLoginRole(String input_role)
    {
        login_role = input_role;
    }

    public String getLoginRole()
    {
        return login_role;
    }

    public void resetLoginRole()
    {
        login_role = "";
    }

    public boolean hadMultipleRoles()
    {
        if(role.contains(","))
        {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
