package sample;

import java.util.Random;

public class User {

    private String fname;
    private String lname;
    private String login;
    private String password;
    private String token;

    public User(String fname, String lname, String login, String password, String token) {
        this.fname = fname;
        this.lname = lname;
        this.login = login;
        this.password = password;
        this.token = token;
        token = null;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public void generateToken() {
        int size=25;
        Random rnd = new Random();
        String testString="";
        for(int i = 0;i<size;i++) {
            int type=rnd.nextInt(4);
            switch (type) {
                case 0:
                    testString += (char) ((rnd.nextInt(26)) + 65);
                    break;
                case 1:
                    testString += (char) ((rnd.nextInt(10)) + 48);
                    break;
                default:
                    testString += (char) ((rnd.nextInt(26)) + 97);
            }
        }
        this.token=testString;
    }
}
