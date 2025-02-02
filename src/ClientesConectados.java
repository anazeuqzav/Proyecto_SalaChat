import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que representa una lista de clientes conectados. Contiene una lista donde se van almacenando.
 */

public class ClientesConectados {
    // Atributos

    // Cambio a HashMap para poder identificar al usuario por el nombre
    private Map<String, Socket> clientesConectados = new HashMap<>();

    // Constructores
    public ClientesConectados() {

    }

    public ClientesConectados(HashMap<String, Socket> clientesConectados) {
        this.clientesConectados = clientesConectados;
    }

    /**
     * Método para obtener el número de clientes conectados
     *
     * @return entero con el número de clientes conectados
     */
    public synchronized int numeroClientesConectados() {
        return clientesConectados.size();
    }


    /**
     * Método para añadir un cliente a la lista de clientes conectados
     *
     * @param cliente cliente para añadir a la lista
     */
    public synchronized void anadirCliente(String nombre, Socket cliente) {
        clientesConectados.put(nombre, cliente);
    }

    /**
     * Método para eliminar un cliente de la lista de clientes conectados
     *
     * @param nombre cliente para eliminar de la lista
     */
    public synchronized void eliminarCliente(String nombre) {
        clientesConectados.remove(nombre); // elimina el cliente por nombre
    }

    /**
     * Método que devuelve una copia del array de clientes conectados
     *
     * @return arrayList de clientes conectados
     */
    public synchronized List<Socket> obtenerClientesConectados() {
        return clientesConectados.values().stream().toList(); // devuelve una lista de sockets
    }


    /**
     * Método para obtener un cliente por nombre
     *
     * @param nombre
     * @return
     */
    public synchronized Socket obtenerClientePorNombre(String nombre) {
        return clientesConectados.get(nombre);
    }

    /**
     * Método para obtener la lista de nombres de clientes conectados
     *
     * @return ArrayList de Strings con los nombres
     */
    public synchronized List<String> obtenerNombresClientes() {
        return new ArrayList<>(clientesConectados.keySet());
    }

}
