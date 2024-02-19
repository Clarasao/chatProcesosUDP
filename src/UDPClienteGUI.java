import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClienteGUI extends JFrame {
    private static final int PUERTO = 9876;
    private static final int TAMANO_BUFFER = 1024;

    private DatagramSocket socket;
    private InetAddress servidorDireccion;

    private JTextArea mensajesArea;
    private JTextField mensajeField;

    public UDPClienteGUI() {
        super("Cliente de Chat UDP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        mensajesArea = new JTextArea();
        mensajeField = new JTextField();

        JButton enviarButton = new JButton("Enviar");
        JButton salirButton = new JButton("Salir");

        enviarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensaje();
            }
        });

        salirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desconectarCliente();
            }
        });

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(enviarButton);
        panelBotones.add(salirButton);

        add(new JScrollPane(mensajesArea), BorderLayout.CENTER);
        add(mensajeField, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.SOUTH);

        setVisible(true);

        inicializarCliente();
    }

    private void inicializarCliente() {
        try {
            socket = new DatagramSocket();
            servidorDireccion = InetAddress.getByName("localhost");

            System.out.println("Cliente UDP iniciado. Escriba 'salir' para cerrar.");

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
                SwingUtilities.invokeLater(() -> mensajesArea.append("Servidor: " + mensaje + "\n"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enviarMensaje() {
        String mensaje = mensajeField.getText();
        if (!mensaje.isEmpty()) {
            try {
                byte[] buffer = mensaje.getBytes();
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, servidorDireccion, PUERTO);
                socket.send(paquete);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mensajeField.setText("");
        }
    }

    private void desconectarCliente() {
        try {
            socket.close();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UDPClienteGUI());
    }
}
