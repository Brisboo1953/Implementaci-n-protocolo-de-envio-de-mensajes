import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class EnviarC {

    public static final String CLASS_NAME = EnviarC.class.getSimpleName();
    public static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    public static final int PORT = 1818;

    public static void main(String[] args) {

        String hostName = null;
        int portNumber = 1818;

        if (args.length != 2) {
            System.err.println(
                    "Usage: java SendClient <host name> <port number>");
            hostName = "localhost";
            portNumber = 1818;
            // System.exit(1);
        } else {
            hostName = args[0];
            portNumber = Integer.parseInt(args[1]);
        }

        try {
            Socket echoSocket = new Socket(hostName, portNumber);

            PrintWriter out
                    = new PrintWriter( echoSocket.getOutputStream() , true);

            BufferedReader in
                    = new BufferedReader(
                    new InputStreamReader( echoSocket.getInputStream() ));

            InputHandler inputHandler = new InputHandler(in);

            Thread t = new Thread(inputHandler);
            t.start();

            BufferedReader teclado
                    = new BufferedReader(
                    new InputStreamReader(System.in));

            String userInput;
            while ((userInput = teclado.readLine()) != null) {
                if( userInput.trim().equals(".") ) {
                    out.println(userInput);
                    break;
                }   else {
                    out.println(userInput);
                }
                //System.out.println("echo: " + in.readLine());
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to "
                    + hostName);
            System.exit(1);
        }

    }

}
