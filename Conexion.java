import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class Conexion implements Runnable {

    public static final String NOMBRE_CLASE = Conexion.class.getSimpleName();
    public static final Logger REGISTRADOR = Logger.getLogger(NOMBRE_CLASE);

    private UsuarioM usuarios;
    private Socket socketCliente;
    private BufferedReader entrada;
    private PrintWriter salida;

    public Conexion(UsuarioM gestorUsuarios, Socket socket) {
        this.usuarios = gestorUsuarios;
        this.socketCliente = socket;

        try {
            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            salida = new PrintWriter(socketCliente.getOutputStream(), true);
        } catch (IOException e) {
            REGISTRADOR.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String linea;
            while ((linea = entrada.readLine()) != null) {
                String comando = linea.trim();

                if (comando.startsWith("CONNECT ")) {
                    String nombreUsuario = comando.substring(8).trim();
                    REGISTRADOR.info("Conectando usuario: " + nombreUsuario);
                    boolean estaConectado = usuarios.conectar(nombreUsuario, socketCliente);
                    if (estaConectado) {
                        salida.println("Conectado como " + nombreUsuario);
                    } else {
                        salida.println("Nombre de usuario ya en uso. Intente con uno diferente.");
                    }
                } else if (comando.startsWith("SEND ")) {
                    int indiceInicioMensaje = comando.indexOf('#') + 1;
                    int indiceFinMensaje = comando.indexOf('@');

                    if (indiceInicioMensaje >= 0 && indiceFinMensaje >= 0) {
                        String mensaje = comando.substring(indiceInicioMensaje, indiceFinMensaje);
                        String destinatario = comando.substring(indiceFinMensaje + 1).trim();
                        REGISTRADOR.info("Enviando mensaje a " + destinatario + ": " + mensaje);
                        usuarios.enviarMensaje(mensaje, destinatario);
                    } else {
                        salida.println("Formato de mensaje inválido. Use SEND #mensaje@destinatario.");
                    }
                } else if (comando.equals("LIST")) {
                    REGISTRADOR.info("Listando usuarios conectados");
                    StringBuilder listaUsuarios = new StringBuilder("Usuarios Conectados: \n");

                    for (String usuario : usuarios.getUsers()) {
                        listaUsuarios.append(usuario).append("\n");
                    }
                    salida.println(listaUsuarios.toString());
                } else if (comando.startsWith("DISCONNECT ")) {
                    String nombreUsuario = comando.substring(12).trim();
                    REGISTRADOR.info("Desconectando usuario: " + nombreUsuario);
                    boolean estaDesconectado = usuarios.desconectar(nombreUsuario);

                    if (estaDesconectado) {
                        try {
                            socketCliente.close();
                            break;
                        } catch (IOException e) {
                            REGISTRADOR.severe("Error al cerrar el socket: " + e.getMessage());
                            e.printStackTrace();
                        }
                        salida.println("Desconectado");
                    } else {
                        salida.println("Usuario no encontrado");
                    }
                }
            }
        } catch (IOException e) {
            REGISTRADOR.severe(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                socketCliente.close();
            } catch (IOException e) {
                REGISTRADOR.severe(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
