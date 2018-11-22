
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.TimeZone;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jorge Zaruma
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("javascript");
            String script1 = (String) "function toDate(dStr, format)"
                    + " { var now = new Date(); if (format === 'h:m')"
                    + " { now.setHours(dStr.substr(0, dStr.indexOf(':')));"
                    + " now.setMinutes(dStr.substr(dStr.indexOf(':') + 1)); "
                    + "now.setSeconds(0); return now; } "
                    + "else{ return 'Invalid Format';}}";
            engine.eval(script1);
            Invocable invocar = (Invocable) engine;
            Object object = invocar.invokeFunction("toDate", "13:15", "h:m");
            Date date = (Date) object;
            System.out.println(date.toString());
        } catch (Exception e) {
            System.out.println("dfdsf");
        }
    }

}
