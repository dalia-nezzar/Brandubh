import control.BRBController;
import control.BRBDecider;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
// assertions
import org.junit.jupiter.api.Assertions;

public class TestBrandubhConsole {

    @Test
    public void testSetAI() {
        // Arrange
        String[] args = {"", "2", ""};

        // Act
        String aiMode = BrandubhConsole.setAI(1, args);

        // Assert
        Assertions.assertEquals("God Loki (EAT)", aiMode);
        Assertions.assertEquals(3, BRBController.typeAI1);
    }
}
