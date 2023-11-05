import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class UsuarioM {

    public static final String NOMBRE_CLASE = UsuarioM.class.getSimpleName();
    public static final Logger REGISTRADOR = Logger.getLogger(NOMBRE_CLASE);

    private Map<String, Socket> conexiones;

    public UsuarioM() {
        super();
        conexiones = new HashMap<>();
    }

    public boolean conectar(String usuario, Socket socket) {
        boolean resultado = true;

        if (conexiones.containsKey(usuario)) {
            resultado = false;
        } else {
            conexiones.put(usuario, socket);
        }
        return resultado;
    }

    public boolean desconectar(String usuario) {
        if (conexiones.containsKey(usuario)) {
            conexiones.remove(usuario);
            return true;
        }
        return false;
    }

    public Set<String> getUsuariosConectados() {
        return conexiones.keySet();
    }

    public Socket obtenerConexion(String usuario) {
        return conexiones.get(usuario);
    }

    public void enviarMensaje(String mensaje, String destinatario) {
        Collection<Socket> conexionesActivas = conexiones.values();

        for (Socket s : conexionesActivas) {
            try {
                PrintWriter salida = new PrintWriter(s.getOutputStream(), true);
                salida.println(destinatario + ": " + mensaje);
            } catch (IOException e) {
                REGISTRADOR.severe(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
