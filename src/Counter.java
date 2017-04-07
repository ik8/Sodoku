import javax.naming.ldap.StartTlsRequest;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ikosteniov on 3/10/2017.
 */
public class Counter{

    private int seconds = 0;
    private int minutes = 0;


    public Counter() {

    }

    public void AddSecond() {

        if(minutes >= 59 && seconds >= 59) {
            seconds = minutes = 0;
        }
        else if(seconds >= 59) {
            seconds = 0;
            minutes++;
        }
        else
            seconds++;
    }

    public String FormattedTime() {
        return String.format("%02d : %02d", minutes, seconds);
    }
}
