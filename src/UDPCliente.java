import javax.swing.*;
import java.awt.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPCliente extends JFrame {
    private static final int PUERTO = 9876;
    private static final int TAMANO_BUFFER = 1024;

    private DatagramSocket socket;
    private InetAddress servidorDireccion;
    private JTextArea areaMensajes;
    private JTextField campoTextoMensaje;
    private String nombreCliente;

    public UDPCliente() {
        try {
            socket = new DatagramSocket();
            servidorDireccion = InetAddress.getByName("localhost");

            areaMensajes = new JTextArea();
            JScrollPane scrollPane = new JScrollPane(areaMensajes);

            campoTextoMensaje = new JTextField();
            campoTextoMensaje.addActionListener(e -> enviarMensaje());

            JButton btnEnviar = new JButton("Enviar");
            btnEnviar.addActionListener(e -> enviarMensaje());

            setLayout(new BorderLayout());
            add(scrollPane, BorderLayout.CENTER);
            add(campoTextoMensaje, BorderLayout.SOUTH);
            add(btnEnviar, BorderLayout.EAST);

            nombreCliente = JOptionPane.showInputDialog("¡Hola! ¿Cómo te llamas?");
            setTitle("Chat UDP - " + nombreCliente);
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setVisible(true);

            new Thread(this::recibirMensajes).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recibirMensajes() {
        try {
            while (true) {
                byte[] buffer = new byte[TAMANO_BUFFER];
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);

                String mensaje = new String(paquete.getData(), 0, paquete.getLength());
                areaMensajes.append(mensaje + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enviarMensaje() {
        try {
            String mensaje = campoTextoMensaje.getText();

            if (mensaje.equalsIgnoreCase("salir")) {
                socket.close();
                System.exit(0);
            }

            mensaje = nombreCliente + ": " + mensaje;
            byte[] buffer = mensaje.getBytes();
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, servidorDireccion, PUERTO);
            socket.send(paquete);

            campoTextoMensaje.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UDPCliente());
    }
}