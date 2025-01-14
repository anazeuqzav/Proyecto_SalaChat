import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Clase Servidor que espera la conexión de los clientes.
 */
public class ServidorChat {

    private static final int MAX_CLIENTES = 10;
    private static final int PUERTO = 6000;

    public static void main(String[] args) throws IOException {

        ClientesConectados clientesConectados = new ClientesConectados();

        ServerSocket servidor;
        servidor = new ServerSocket(PUERTO);
        System.out.println("Servidor iniciado...");

        while (true) {
            if (clientesConectados.numeroClientesConectados() < MAX_CLIENTES) {
                Socket cliente = servidor.accept();
                System.out.println("Nuevo cliente conectado: " + cliente.getInetAddress());

                HiloCliente hiloCliente = new HiloCliente(cliente, clientesConectados);
                hiloCliente.start();
            } else {
                System.out.println("Se ha alcanzado el número máximo de clientes");

            }
        }
    }
}
