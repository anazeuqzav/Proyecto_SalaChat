import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            // Comprueba si se puede aceptar un nuevo cliente
            if (clientesConectados.numeroClientesConectados() < MAX_CLIENTES) {
                Socket cliente = servidor.accept();
                System.out.println("Nuevo cliente conectado: " + cliente.getInetAddress());

                // Leer el nombre del cliente
                BufferedReader br = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                String nombreCliente = br.readLine();

                System.out.println("Nombre del cliente: " + nombreCliente);

                clientesConectados.anadirCliente(nombreCliente, cliente); // guarda el cliente conectado con su nombre

                HiloCliente hiloCliente = new HiloCliente(nombreCliente, cliente, clientesConectados);
                hiloCliente.start();
            } else {
                System.out.println("Se ha alcanzado el número máximo de clientes");

            }
        }
    }
}
