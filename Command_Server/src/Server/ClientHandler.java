package Server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private String currentDirectory;
    private String clientId;

    private static final int COMMAND_TIMEOUT = 5;

    public ClientHandler(Socket socket, String clientId) {
        this.clientSocket = socket;
        this.clientId = clientId;
        this.currentDirectory = System.getProperty("user.home");
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String command;
            while ((command = in.readLine()) != null) {
                if (command.trim().isEmpty()) {
                    out.println("END_OF_OUTPUT");
                    continue;
                }

                System.out.println(clientId + " > " + command);

                if (command.startsWith("cd ")) {
                    handleCd(command, out);
                } else if (command.equals("pwd")) {
                    out.println("Current directory: " + currentDirectory);
                } else if (command.startsWith("mkdir ")) {
                    handleMkdir(command, out);
                } else if (command.startsWith("del ")) {
                    handleDel(command, out);
                } else {
                    handleOtherCommand(command, out);
                }

                out.println("END_OF_OUTPUT");
            }

        } catch (Exception e) {
            System.out.println("Client disconnected: " + clientId);
        }
    }

    private void handleCd(String command, PrintWriter out) {
        String path = command.substring(3).trim();
        File newDir = new File(path);
        if (!newDir.isAbsolute()) newDir = new File(currentDirectory, path);

        try {
            if (newDir.exists() && newDir.isDirectory()) {
                currentDirectory = newDir.getCanonicalPath();
                out.println("Changed directory to: " + currentDirectory);
            } else {
                out.println("Error: Directory not found -> " + path);
            }
        } catch (IOException e) {
            out.println("Error changing directory: " + e.getMessage());
        }
    }

    private void handleMkdir(String command, PrintWriter out) {
        try {
            String output = executeCommand(command);
            if (output.isEmpty()) {
                out.println("Directory created successfully.");
            } else {
                out.println( output);
            }
        } catch (Exception e) {
            out.println("Error creating directory: " + e.getMessage());
        }
    }

    private void handleDel(String command, PrintWriter out) {
        try {
            String output = executeCommand(command);
            if (output.isEmpty()) {
                out.println("File deleted successfully.");
            } else {
                out.println( output);
            }
        } catch (Exception e) {
            out.println("Error deleting file: " + e.getMessage());
        }
    }

    private void handleOtherCommand(String command, PrintWriter out) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> executeCommand(command));
        try {
            String output = future.get(COMMAND_TIMEOUT, TimeUnit.SECONDS);
            if (output.isEmpty()) {
                out.println("Command executed successfully.");
            } else {
                out.println(output);
            }
        } catch (TimeoutException e) {
            future.cancel(true);
            out.println("Error: Command timed out after " + COMMAND_TIMEOUT + " seconds.");
        } catch (Exception e) {
            out.println("Error executing command: " + e.getMessage());
        } finally {
            executor.shutdownNow();
        }
    }

    public String executeCommand(String command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder();
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            if (command.equals("ls")) command = "dir";
            pb.command("cmd", "/c", command);
        } else {
            pb.command("bash", "-c", command);
        }

        pb.directory(new File(currentDirectory));
        pb.redirectErrorStream(true);

        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        boolean finished = process.waitFor(COMMAND_TIMEOUT, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            output.append("Error: Command forcibly terminated due to timeout.\n");
        }

        return output.toString().trim();
    }

    public ClientHandler() {
        this.currentDirectory = System.getProperty("user.home");
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }
    public void testChangeDir(String command) {
        handleCd(command, new PrintWriter(System.out, true));
    }

}
