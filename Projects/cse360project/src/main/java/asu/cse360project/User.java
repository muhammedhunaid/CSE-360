package asu.cse360project;

public class User {
    private String username;
    private String role;
    private String first_name;
    private String login_role = "";

    public User(String username, String first_name, String role) {
        this.username = username;
        this.first_name = first_name;
        this.role = role;
    }

    public String getUsername() 
    {
        return username;
    }

    public String getRole() 
    {
        return role;
    }

    public String getFirst_name() 
    {
        return first_name;
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
                "first name=" + first_name +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
