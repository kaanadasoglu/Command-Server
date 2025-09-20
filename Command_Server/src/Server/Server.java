package Server;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
	  private static AtomicInteger clientCount = new AtomicInteger(0);
	private static final int PORT = 5000;

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(PORT);
		System.out.println("Server is listening on port "+ PORT);
		
		ExecutorService executor = Executors.newCachedThreadPool();
		
		while(true) 
		{
			Socket clientSocket = serverSocket.accept();
			int id = clientCount.incrementAndGet();
			String clientId = "[Client-" + id + "]";
			System.out.println(clientId + " Connected");
			executor.execute(new ClientHandler(clientSocket,clientId));
			
			
		}
		

	}

}
