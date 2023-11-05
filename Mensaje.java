
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class MessageServer {
    public static final String CLASS_NAME = MessageServer.class.getSimpleName();
    public static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    public static final int PORT = 1818;

    public static void main(String args[]) {

        int portNumber;
        if (args.length != 1) {
            System.err.println("Usage: java MessageServer <port number>");
            portNumber = PORT;
            //System.exit(1);
        } else {
            portNumber = Integer.parseInt(args[0]);
        }

        UsuarioM users = new UsuarioM();


        try {
            ServerSocket serverSocket
                    = new ServerSocket( portNumber );

            while( true ) {

                Socket clientSocket = serverSocket.accept();

                LOGGER.info("CONNECTION: " +
                        clientSocket.getRemoteSocketAddress().toString());

                Thread serviceProcess = new Thread( new Conexion(users, clientSocket)) ;
                serviceProcess.start();
            }

        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
