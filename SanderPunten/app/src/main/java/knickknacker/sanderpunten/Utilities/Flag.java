package knickknacker.sanderpunten.Utilities;

public class Flag {
    int flag;

    public Flag() {
        flag = 0;
    }

    public void set(int bit) {
        flag = flag | (1 << bit);
    }

    public void unset(int bit) {
        flag = flag & ~(1 << bit);
    }

    public boolean hasSet(int bit) {
        if ((flag & (1 << bit)) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSet() {
        if (flag == 0) {
            return false;
        } else {
            return true;
        }
    }
}
