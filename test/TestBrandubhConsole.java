import control.BRBController;
import control.BRBDecider;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class TestBrandubhConsole {

    @Test
    public void testStartMethod() {
        // Test modeChoice = -1
        String[] args1 = {};
        // Mock the input to simulate user interaction
        BRBController.input = new Scanner("1\n");
        BRBDecider.loadData("dataMap.bin");
        // Call the method and assert the expected behavior
        assertDoesNotThrow(() -> BrandubhConsole.start(args1));
       

        // Test modeChoice = 1
        String[] args2 = {"1"};
        // Mock the input to simulate user interaction
        BRBController.input = new Scanner("1\n");
        BRBDecider.loadData("dataMap.bin");
        // Call the method and assert the expected behavior
        assertDoesNotThrow(() -> BrandubhConsole.start(args2));

        // Test modeChoice = 2
        String[] args3 = {"2"};
        // Mock the input to simulate user interaction
        BRBController.input = new Scanner("1\n");
        BRBDecider.loadData("dataMap.bin");
        // Call the method and assert the expected behavior
        assertDoesNotThrow(() -> BrandubhConsole.start(args3));
    }

    // Add more test methods for other methods in your code

}
