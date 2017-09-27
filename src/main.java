import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException {
        Calculate c = new Calculate();
        c.checkAvailableCourses();

        ReadBlackList rb = new ReadBlackList();
        rb.read();
    }
}
