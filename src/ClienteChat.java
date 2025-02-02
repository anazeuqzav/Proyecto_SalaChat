import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

    // Nombre del usuario
    static String nombre = "";

    // Variables de la interfaz gráfica
    private JTextField JMensaje;
    private JButton JEnviar;
    private JButton JSalir;
    private JScrollPane JScrollPane;
    private JTextPane JChat;
    private JPanel ventana;

    // Lista de usuarios
    private JTextPane JUsuariosConectados;

    // Variables para la conexión
    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;

    // Personalización del mensaje
    private Color colorMensajesPropios = new Color(85, 153, 71);
    private Color colorMensajePrivado = new Color(250, 109, 105);
    private Color colorMensajesResto = new Color(40, 65, 104);


    // Constructor
    // Constructor
    public ClienteChat() {
        setTitle("Chat de " + nombre);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        ventana = new JPanel(new BorderLayout());

        JChat = new JTextPane();
        JChat.setEditable(false);
        JScrollPane scrollChat = new JScrollPane(JChat);

        JUsuariosConectados = new JTextPane();
        JUsuariosConectados.setEditable(false);
        JUsuariosConectados.setBackground(new Color(240, 240, 240));
        JScrollPane scrollUsuarios = new JScrollPane(JUsuariosConectados);
        scrollUsuarios.setPreferredSize(new Dimension(100, 0));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollChat, scrollUsuarios);
        splitPane.setDividerLocation(500);

        JPanel panelInferior = new JPanel(new BorderLayout());
        JMensaje = new JTextField();
        JEnviar = new JButton("Enviar");
        JSalir = new JButton("Salir");

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(JEnviar);
        panelBotones.add(JSalir);

        panelInferior.add(JMensaje, BorderLayout.CENTER);
        panelInferior.add(panelBotones, BorderLayout.EAST);

        ventana.add(splitPane, BorderLayout.CENTER);
        ventana.add(panelInferior, BorderLayout.SOUTH);

        setContentPane(ventana);
        setVisible(true);


        // Acción para el botón "Enviar"
        JEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensaje(); // envía el mensaje cuando se presiona el botón
            }
        });

        // Acción para el botón "Salir"
        JSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salirDelChat(); // sale del chat cuando se presiona el boton salir
            }
        });

        // Key listener para enviar con enter
        JMensaje.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { // Si se presiona Enter
                    enviarMensaje(); // Enviar el mensaje
                }
            }
        });
    }

    /**
     * Aquí comienza el programa cliente: en primer lugar aparece una
     * pantalla solicitando un nombre, en caso de no poner nada, el
     * programa no continúa; si se pone un nombre, se inicia la interfaz
     * gráfica.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // Solicitud del nombre
        try {
            while (nombre.trim().isEmpty()) {
                nombre = JOptionPane.showInputDialog("Introduce tu nombre o nick:");
            }
        } catch (NullPointerException npex) {
            System.out.println("No se ha introducido el nombre");
        }

        // Mostrar mensaje de aviso sobre mensajes privados
        JOptionPane.showMessageDialog(null, "Puedes enviar mensajes privados usando el comando: /privado [nombre de destinatario] [mensaje]",
                "Instrucciones", JOptionPane.INFORMATION_MESSAGE);


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

            pw.println(nombre); // envía el nombre al servidor
            pw.println(nombre + " se ha conectado al chat"); // envía mensaje de confirmación de conexión
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar al servidor: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para enviar un mensaje
     */
    private void enviarMensaje() {
        String mensaje = JMensaje.getText().trim(); // obtiene el texto del campo de mensaje
        if (!mensaje.isEmpty()) {
            if (mensaje.equals("*")) {
                salirDelChat();
            }
            pw.println(nombre + ": " + mensaje);
            JMensaje.setText("");
        }
    }

    /**
     * Método para salir del chat
     */
    private void salirDelChat() {
        pw.println("*"); // notifica la desconexión
        System.exit(0);
    }

    /**
     * Método para personalizar el color de un mensaje
     *
     * @param mensaje El mensaje que se envía al chat
     * @param color   el color del que saldrá el mensaje.
     */
    private void personalizarMensaje(String mensaje, Color color) {
        // Clase de Java Swing que Permite manejar documentos con
        // texto con estilos como cambiar color que es para lo que lo uso.
        StyledDocument doc = JChat.getStyledDocument();
        Style estilo = JChat.addStyle("Estilo", null); // le agrega estilo al chat
        StyleConstants.setForeground(estilo, color); // para el estilo creado anteriormente le pone el color al texto

        try {
            doc.insertString(doc.getLength(), mensaje + "\n", estilo); //inserta el contenido del mensaje con el estilo en el chat.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para actualizar la lista de usuarios conectados
     *
     * @param listaUsuarios Lista de nombres de usuarios conectados
     */
    private void actualizarListaUsuarios(String listaUsuarios) {
        StringBuilder usuariosFormateados = new StringBuilder();

        for (String usuario : listaUsuarios.split(",")) {
            usuariosFormateados.append(usuario).append("\n");
        }

        JUsuariosConectados.setText(usuariosFormateados.toString());
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
            // Leer mensajes del servidor en bucle
            while ((mensaje = br.readLine()) != null) {
                // si el mensaje empieza por el nombre del propio usuario
                if (mensaje.startsWith(nombre)) {
                    personalizarMensaje(mensaje, colorMensajesPropios);
                } else if (mensaje.startsWith("/usuarios ")) {
                    actualizarListaUsuarios(mensaje.substring(10));
                } else if (mensaje.contains("[PRIVADO]")) { // si el mensaje empieza por privado
                    personalizarMensaje(mensaje, colorMensajePrivado);
                } else {
                    personalizarMensaje(mensaje, colorMensajesResto);
                }
            }
        } catch (IOException e) {
            System.err.println("Error en el hilo de lectura: " + e.getMessage());
        }

    }
}
