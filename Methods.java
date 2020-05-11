package sample;

import net.minidev.json.JSONObject;
import org.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Methods {
    List<User> list = new ArrayList<User>();
    List<String> log = new ArrayList<String>();
    List<String> messages = new ArrayList<String>();


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
    public ResponseEntity<String> signup(@RequestBody String data){
        //System.out.println(data);
        JSONObject obj = new JSONObject(data);

        if(obj.has("fname") && obj.has("lname")&& obj.has("login")&& obj.has("password"))
        { // vstup je ok, mame vsetky kluce
            if(existLogin(obj.getString("login"))){
                JSONObject res = new JSONObject();
                res.put("error","User already exists");
                return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            }
            String password = obj.getString("password");
            if(password.isEmpty()){
                JSONObject res = new JSONObject();
                res.put("error","Password is a mandatory field");
                return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            }
            String hashPass = hash(obj.getString("password"));

            User user = new User(obj.getString("fname"), obj.getString("lname"), obj.getString("login"), hashPass);
            list.add(user);
            JSONObject res = new JSONObject();
            res.put("fname",obj.getString("fname"));
            res.put("lname",obj.getString("lname"));
            res.put("login",obj.getString("login"));
            return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        }
        else{
            JSONObject res = new JSONObject();
            res.put("error","Invalid body request");
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
    @RequestMapping(method=RequestMethod.POST, value="/changepassword")
    public ResponseEntity<String> changePasswd(@RequestBody String data){

        JSONObject object = new JSONObject(data);

        if(object.has("oldpassword") && object.has("newpassword")&& object.has("login"))
        {
            String login = object.getString("login");
            String oldpassword = object.getString("oldpassword");
            String newpassword = object.getString("newpassword");
            if(oldpassword.isEmpty() || newpassword.isEmpty()){
                JSONObject res = new JSONObject();
                res.put("error","Passwords are mandatory fields");
                return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            }
            if(!existLogin(login) || !checkPassword(login,oldpassword)){
                JSONObject res = new JSONObject();
                res.put("error","Invalid login or password");
                return ResponseEntity.status(401).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            }

            String hashPass = hash(object.getString("newpassword"));

            User user = getUser(login);
            user.setPassword(hashPass);
            return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body("{}");
        }
        else{
            JSONObject res = new JSONObject();
            res.put("error","Invalid body request");
            return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/message/post")
    public ResponseEntity<String> newMessage(@RequestBody String data, @RequestParam(value = "token") String userToken) {
        JSONObject object = new JSONObject(data);
        if (userToken.equals(findInformation(object.getString("from")).getToken())&& findLogin(object.getString("from")) && findLogin(object.getString("to"))) {

            String timeStamp = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(Calendar.getInstance().getTime());
            object.put("time", timeStamp);
            messages.add(object.toString());
            return ResponseEntity.status(201).body("Message send");
        } else {
            return ResponseEntity.status(400).body("error");
        }
    }
    @RequestMapping(method = RequestMethod.POST, value = "/message/get")
    public ResponseEntity<String> showMessages(@RequestBody String data, @RequestParam(value = "token") String userToken) {
        JSONObject object = new JSONObject(data);

        if (findLogin(object.getString("login")) && findInformation(object.getString("login")).getToken().equals(userToken)) {
            JSONObject format = new JSONObject();

            int count = 0;
            for (String list : messages) {
                JSONObject information = new JSONObject(list);

                System.out.println(list);
                if (information.getString("from").equals(object.getString("login"))||information.getString("to").equals(object.getString("login"))) {
                    format.put(String.valueOf(count), list);
                    count++;
                }
            }
            String string = format.toString();

            return ResponseEntity.status(201).body(string);


        } else {
            return ResponseEntity.status(400).body("error");
        }
    }
    @RequestMapping(method = RequestMethod.GET, value = "/messages/{from}")
    public Object getMessage(@RequestParam(value = "from") String from, @RequestHeader(name = "Authorization") String token) throws JSONException {

        System.out.println("messages from");


        JSONObject res = new JSONObject();

        User temp = getUser(from);

        if (temp == null  || !validToken(token) ) {
            res.put("error", "Login error");
            return ResponseEntity.status(401).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        }

return "Application is running";
    }
}