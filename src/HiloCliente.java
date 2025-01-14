import java.io.*;
import java.net.Socket;

/**
 * Clase que maneja la comunicación de cada cliente
 */
public class HiloCliente extends Thread{

    // Atributos
    Socket cliente = null;
    ClientesConectados clientesConectados;

    // Constructor
    public HiloCliente(Socket cliente, ClientesConectados clientesConectados) {
        this.cliente = cliente;
        this.clientesConectados = clientesConectados;
    }

    @Override
    public void run() {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            PrintWriter pw = new PrintWriter(cliente.getOutputStream(), true)) {

            String mensaje;
            while((mensaje = br.readLine()) != null){
                if(mensaje.equals("*")) {
                    clientesConectados.eliminarCliente(cliente);
                    System.out.println("Cliente desconectado: " + cliente.getInetAddress());                    /** TODO!!! POR AQUI ME HE QUEDADO */
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
                PrintWriter pw = new PrintWriter(cliente.getOutputStream(), true);
                pw.println(mensaje);
            } catch (IOException e) {
                System.err.println("Error enviando mensaje: " + e.getMessage());
            }
        }
    }
}
