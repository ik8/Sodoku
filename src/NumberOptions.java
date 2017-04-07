/**
 * Created by ikosteniov on 3/26/2017.
 */
public class NumberOptions {

    private boolean numbers[] = new boolean[9];

    public NumberOptions() {
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = false;
            /*
            False -> can be applied to this specific cell
            True -> can't be applied to this specific cell
             */
        }
    }

    public boolean[] getNumbers() {
        return numbers;
    }

    public void setNumbers(boolean numbers_to_copy[]) {
        for (int i = 0; i < numbers_to_copy.length; i++) {
            numbers[i] = numbers_to_copy[i];
        }
    }


}
