import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Clase que maneja la comunicación de cada cliente
 */
public class HiloCliente extends Thread {

    // Atributos
    Socket cliente = null;
    ClientesConectados clientesConectados;
    String nombreCliente;

    // Constructor
    public HiloCliente(String nombreCliente, Socket cliente, ClientesConectados clientesConectados) {
        this.nombreCliente = nombreCliente;
        this.cliente = cliente;
        this.clientesConectados = clientesConectados;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
             PrintWriter pw = new PrintWriter(cliente.getOutputStream(), true)) {

            enviarListaUsuarios();

            String mensaje;
            while ((mensaje = br.readLine()) != null) {

                String[] partesMensaje = mensaje.split(" ", 4); // [0] es el emisor, [1] el comando, [2] el destinatario

                if (partesMensaje.length >= 3) {
                    // Comprueba si el mensaje contiene el comando /privado
                    if (partesMensaje[1].startsWith("/privado")) {
                        // divide el mensaje en 3 strings (1 el comando privado, 2 el destinatario, 3 el mensaje en sí)
                        String destinatario = partesMensaje[2]; // destinatario
                        String mensajePrivado = partesMensaje[3]; //mensaje en si

                        enviarMensajePrivado(destinatario, mensajePrivado);
                    } else {
                        System.out.println("Mensaje recibido: " + mensaje);
                        enviarMensajeATodos(mensaje);
                    }
                    // si el mensaje es un * lo elimina de la lista y cierra el socket.
                } else if (mensaje.equals("*")) {
                    clientesConectados.eliminarCliente(nombreCliente);
                    enviarMensajeATodos(nombreCliente + " se ha desconectado.");
                    enviarListaUsuarios();
                    cliente.close();
                }

            }
        } catch (IOException e) {
            System.err.println("Error en la comunicación con el cliente: " + e.getMessage());
        } finally {
            try {
                cliente.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar el cliente: " + e.getMessage());
            }
        }
    }

    /**
     * Método para enviar un mensaje a todos los usuarios del chat. Recorre el hashmap clientesConectados
     * y por cada cliente conectado el envía el mensaje.
     *
     * @param mensaje mensaje para enviar
     */
    private void enviarMensajeATodos(String mensaje) {
        for (Socket s : clientesConectados.obtenerClientesConectados()) {
            try {
                PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                pw.println(mensaje);
            } catch (IOException e) {
                System.err.println("Error enviando mensaje: " + e.getMessage());
            }
        }
    }

    /**
     * Método para enviar un mensaje privado a un destinatario en concreto.
     *
     * @param destinatario destinatario del mensaje
     * @param mensaje      mensaje que se va a enviar
     */
    private void enviarMensajePrivado(String destinatario, String mensaje) {
        Socket socketDestino = clientesConectados.obtenerClientePorNombre(destinatario);
        if (socketDestino != null) {
            try {
                PrintWriter pwDestino = new PrintWriter(socketDestino.getOutputStream(), true);
                pwDestino.println("[PRIVADO] " + nombreCliente + ": " + mensaje);
            } catch (IOException e) {
                System.err.println("Error enviando mensaje privado: " + e.getMessage());
            }
        } else {
            try {
                PrintWriter pw = new PrintWriter(cliente.getOutputStream(), true);
                pw.println("El usuario " + destinatario + " no está conectado.");
            } catch (IOException e) {
                System.err.println("Error notificando al remitente: " + e.getMessage());
            }
        }
    }

    /**
     * Método para enviar la lista de usuarios conectados a todos los clientes
     */
    private void enviarListaUsuarios() {
        List<String> listaUsuarios = clientesConectados.obtenerNombresClientes();
        String listaUsuariosMensaje = "/usuarios " + String.join(",", listaUsuarios);
        enviarMensajeATodos(listaUsuariosMensaje);
    }

}
