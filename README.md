### Running the Project

You can run the project either through your IDE or directly from the terminal.

1. **Compile the project (if using terminal)**  
   Navigate to the root folder and run:  
   ```
   javac Server/*.java Client/*.java
Start the Server
Run:

```
java Server.Server
```
You should see:

Server is listening on port 5000
Start Clients
Open a new terminal (or IDE run configuration) for each client and run:

```
java Client.Client
```
You will get a > prompt for entering commands.

Multiple Clients:
Each time you run the client, it opens a new independent session. For example:

Client-1 can navigate directories and run commands without affecting Client-2.

Client-2 can simultaneously execute different commands.

The server maintains separate current working directories for each client, ensuring that their sessions do not interfere with each other.
