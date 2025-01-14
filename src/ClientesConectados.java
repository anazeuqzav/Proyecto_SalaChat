import java.net.Socket;
import java.util.ArrayList;

/**
 * Clase que representa una lista de clientes conectados. Contiene una lista donde se van almacenando.
 */

public class ClientesConectados {
    // Atributos
    ArrayList<Socket> clientesConectados = new ArrayList<>();

    // Constructores
    public ClientesConectados(){

    }

    public ClientesConectados(ArrayList<Socket> clientesConectados) {
        this.clientesConectados = clientesConectados;
    }

    /**
     * Método para obtener el número de clientes conectados
     * @return entero con el número de clientes conectados
     */
    public synchronized int numeroClientesConectados(){
        return clientesConectados.size();
    }


    /**
     * Método para añadir un cliente a la lista de clientes conectados
     * @param cliente cliente para añadir a la lista
     */
    public synchronized void anadirCliente(Socket cliente) {
        clientesConectados.add(cliente);
    }

    /**
     * Método para eliminar un cliente de la lista de clientes conectados
     * @param cliente cliente para eliminar de la lista
     */
    public synchronized void eliminarCliente(Socket cliente){
        clientesConectados.remove(cliente);
    }

    /**
     * Método que devuelve una copia del array de clientes conectados
     * @return arrayList de clientes conectados
     */
    public synchronized ArrayList<Socket> obtenerClientesConectados() {
        return new ArrayList<>(clientesConectados); // Devolver una copia para evitar problemas de concurrencia.
    }

}
