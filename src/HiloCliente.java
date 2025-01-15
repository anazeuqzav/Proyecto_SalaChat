import java.io.*;
import java.net.Socket;

/**
 * Clase que maneja la comunicación de cada cliente
 */
public class HiloCliente extends Thread{

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
        try(BufferedReader br = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            PrintWriter pw = new PrintWriter(cliente.getOutputStream(), true)) {

            String mensaje;
            while((mensaje = br.readLine()) != null){
                // Le quita al mensaje el nombre del cliente
                String contenidoMensaje = mensaje.substring(mensaje.indexOf(":") + 1). trim();

                // Comprueba si el mensaje contiene el comando /privado
                if (contenidoMensaje.startsWith("/privado")) {
                    String [] partesMensaje = contenidoMensaje.split(" ", 3); // divide el mensaje en 3 strings (1 el comando privado, 2 el destinatario, 3 el mensaje en si)
                    String destinatario = partesMensaje[1];
                    String mensajePrivado = partesMensaje[2];

                    enviarMensajePrivado(destinatario, mensajePrivado);

                } else if (contenidoMensaje.equals("*")) {
                    clientesConectados.eliminarCliente(cliente);
                    System.out.println("Cliente desconectado: " + cliente.getInetAddress());
                    cliente.close();

                } else {
                    System.out.println("Mensaje recibido: " + mensaje);
                    enviarMensajeATodos(mensaje);
                }
            }
        } catch (IOException e){
            System.err.println("Error en la comunicación con el cliente: " + e.getMessage());
        } finally {
            try {
                cliente.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar el cliente: " + e.getMessage());
            }
        }
    }

    private void enviarMensajeATodos(String mensaje){
        for(Socket s: clientesConectados.obtenerClientesConectados()) {
            try {
                PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                pw.println(mensaje);
            } catch (IOException e) {
                System.err.println("Error enviando mensaje: " + e.getMessage());
            }
        }
    }

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
}
