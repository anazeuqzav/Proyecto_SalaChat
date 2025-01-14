import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Esta clase implementa la interfaz gráfica de un cliente de chat grupal.
 * El cliente se conecta a un servidor de chat y puede enviar y recibir mensajes
 * de otros clientes conectados al servidor.
 *
 * @author
 */
public class ClienteChat extends JFrame implements Runnable {

    // Pon aquí las variables estáticas que necesites.
    static String nombre = "";

    // Variables de la interfaz gráfica
    private JTextField JMensaje;
    private JButton JEnviar;
    private JButton JSalir;
    private JScrollPane JScrollPane;
    private JTextArea JChat;
    private JPanel ventana;

    // Variables para la conexión
    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;


    // Constructor
    public ClienteChat() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setMinimumSize(new java.awt.Dimension(400, 300));
        setContentPane(ventana);
        JChat.setEditable(false);
        pack();

        // Acción para el botón "Enviar"
        JEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensaje();
            }
        });

        // Acción para el botón "Salir"
        JSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salirDelChat();
            }
        });
    }

    /**
     * Aquí comienza el programa cliente: en primer lugar aparece una
     * pantalla solicitando un nombre, en caso de no poner nada, el
     * programa no continúa; si se pone un nombre, se inicia la interfaz
     * gráfica.
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // Solicitud del nombre
        try{
            while (nombre.trim().isEmpty()){
                nombre = JOptionPane.showInputDialog("Introduce tu nombre o nick:");
            }
        } catch(NullPointerException npex){
            System.out.println("No se ha introducido el nombre");
        }

        // Crear y mostrar la ventana
        ClienteChat cliente = new ClienteChat();
        cliente.setTitle("Chat de " + nombre);

        cliente.conectarAlServidor();

        // Crear el hilo para la ventana de chat
        Thread chat = new Thread(cliente);
        chat.start();

    }

    /**
     * Método para conectarse al Servidor
     */
    private void conectarAlServidor() {
        try {
            socket = new Socket("localhost", 6000);
            pw = new PrintWriter(socket.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            pw.println(nombre + " se ha conectado al chat");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar al servidor: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para enviar un mensaje
     */
    private void enviarMensaje() {
        String mensaje = JMensaje.getText().trim();
        if (!mensaje.isEmpty()) {
            if(mensaje.equals("*")){
                pw.println(nombre + " se ha desconectado.");
                Thread.interrupted();
            }
            pw.println(nombre +": " + mensaje);
            JMensaje.setText("");
        }
    }

    /**
     * Método para salir del chat
     */
    private void salirDelChat() {
        try {
            pw.println("*");
            socket.close();
            System.exit(0);
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    /**
     * El método run() debe completarse con el código correspondiente a la
     * ventana de chat, esto se hace así para poder utilizar la interfaz
     * gráfica mientras el chat recibe los mensajes y los visualiza utilizando
     * su hilo específico.
     */
    @Override
    public void run() {
        String mensaje;
        try {
            // Leer mensajes del servidor
            while ((mensaje = br.readLine()) != null) {
                JChat.append(mensaje + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error en el hilo de lectura: " + e.getMessage());
        }

    }
}
