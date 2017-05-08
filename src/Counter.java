

/**
 * Created by ikosteniov on 3/10/2017.
 */
public class Counter{

    private int seconds;
    private int minutes;


    public Counter() {
        seconds = 0;
        minutes = 0;
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

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String FormattedTime() {
        return String.format("%02d : %02d", minutes, seconds);
    }
}
