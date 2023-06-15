import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestBrandubhConsole {

    @Test
    public void testStart() {
        String[] args1 = {};
        String input1 = "1\nPlayer1\n2\nPlayer2\n";
        System.setIn(new ByteArrayInputStream(input1.getBytes()));
        BrandubhConsole.start(args1);
        // Exemple : Vérification de la sortie attendue dans la console
        assertEquals("You chose a brother (human) vs brother (human) game!\n", getConsoleOutput());

    }

    private String getConsoleOutput() {
        // Capture la sortie de la console
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);
        // Récupère la sortie
        String output = outputStream.toString();
        // Restaure la sortie standard
        System.setOut(System.out);
        return output;

    }

}
