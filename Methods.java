package sample;

import org.springframework.web.bind.annotation.RequestMapping;


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
}