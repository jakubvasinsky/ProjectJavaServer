package sample;

import net.minidev.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Methods {
    List<User> list = new ArrayList<User>();



    public Methods()
    {
        list.add(new User("Roman", "Simko", "roman", "heslo","12345"));
    }

    @RequestMapping("/time")
    public String getTime() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        System.out.println(time.format(calendar.getTime()));
        return time.format(calendar.getTime());
    }





    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public ResponseEntity<String> login(@RequestBody String data) {
        JSONObject object = new JSONObject(data);
        if (findLogin(object.getString("login")) && findPassword(object.getString("password"))) {
            JSONObject res = new JSONObject();
            User user = findInformation(object.getString("login"));
            user.getToken();
            res.put("fname", user.getFname());
            res.put("lname", user.getLname());
            res.put("login", user.getLogin());
            res.put("token", user.getToken());
            return ResponseEntity.status(200).body(res.toString());
        } else {
            JSONObject res = new JSONObject();
            res.put("error", "wrong login or password");
            return ResponseEntity.status(401).body(res.toString());
        }
    }
    @RequestMapping(method=RequestMethod.POST, value="/signup")
    public ResponseEntity<String> signup(@RequestBody String data) {

        JSONObject object = new JSONObject(data);

        if (object.has("fname")) {
            if (object.has("lname") && object.has("login") && object.has("password")) {
                if (findLogin(object.getString("login"))) {
                    JSONObject res = new JSONObject();
                    res.put("error", "User already exists");
                    return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
                }
                String password = object.getString("password");
                if (password.isEmpty()) {
                    JSONObject res = new JSONObject();
                    res.put("error", "Password is a mandatory field");
                    return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
                }
                String hashpassword = hash(object.getString("password"));

                User user = new User(object.getString("fname"), object.getString("lname"), object.getString("login"), hashpassword);
                list.add(user);
                JSONObject res = new JSONObject();
                Object put = res.put("fname", object.getString("fname"));
                res.put("lname", object.getString("lname"));
                res.put("login", object.getString("login"));
                return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            } else {
                JSONObject res = new JSONObject();
                res.put("error", "Invalid body request");
                return ResponseEntity.status(400).body(res.toString());
            }
        } else {
            JSONObject res = new JSONObject();
            res.put("error", "Invalid body request");
            return ResponseEntity.status(400).body(res.toString());
        }
    }

    private String hash(String password) {
        return password;
    }

    private boolean findLogin(String login) {
        for (User user : list) {
            if (user.getLogin().equalsIgnoreCase(login))
                return true;
        }
        return false;
    }

    private boolean findPassword(String password) {
        for (User user : list) {
            if (user.getPassword().equalsIgnoreCase(password))
                return true;
        }
        return false;
    }

    private User findInformation(String login) {
        for (User user : list) {
            if (user.getLogin().equalsIgnoreCase(login))
                return user;
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/logout")
    public ResponseEntity<String> logout(@RequestBody String data) {
        JSONObject object = new JSONObject(data);
        if (object.get("login")==findInformation(object.get("login").toString())&&object.get("token")==findInformation(object.get("token").toString())){
            findInformation(object.get("login").toString());
            return ResponseEntity.status(200).body("");
        }else {
            return ResponseEntity.status(401).body("error");
        }
    }
}