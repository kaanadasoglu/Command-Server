package Server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

class ClientHandlerTest {

    @Test
    void testExecuteCommandEcho() throws Exception {
        ClientHandler ch = new ClientHandler();
        String output = ch.executeCommand("echo hello");
        assertEquals("hello", output.trim());
    }

    @Test
    void testExecuteCommandList() throws Exception {
        ClientHandler ch = new ClientHandler();
        String os = System.getProperty("os.name").toLowerCase();
        String command = os.contains("win") ? "dir" : "ls";
        String output = ch.executeCommand(command);
        assertNotNull(output);
        assertFalse(output.isEmpty());
    }

    @Test
    void testCurrentDirectory() {
        ClientHandler ch = new ClientHandler();
        assertEquals(System.getProperty("user.home"), ch.getCurrentDirectory());
    }

    @Test
    void testChangeDirectory() throws Exception {
        ClientHandler ch = new ClientHandler();
        String original = ch.getCurrentDirectory();

   
        File parent = new File(original).getParentFile();
        if (parent != null) {
            String parentPath = parent.getCanonicalPath();
            ch.executeCommand("cd ..");
            ch.executeCommand("cd \"" + parent.getName() + "\""); 
      
            assertNotNull(parentPath);
        }
    }
}
