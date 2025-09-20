package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		String host = "localhost";
		int port = 5000;
		
		try(Socket socket = new Socket(host,port);
			PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			Scanner scanner = new Scanner(System.in)) {
			
			System.out.println("Connected to server at: " + host+":"+port);
			
			while(true) 
			{
				System.out.print(">");
				String command = scanner.nextLine();
				out.println(command);
				
				String response;
				while((response = in.readLine())!= null) 
				{
					if(response.equals("END_OF_OUTPUT")) 
						break;
					System.out.println(response);
				}
			}
			
		} catch (IOException e) {
			 e.printStackTrace();
		}

	}

}
