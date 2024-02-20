import javax.swing.*;
import java.awt.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class UDPServer extends JFrame {
    private static final int PUERTO = 9876;
    private static final int TAMANO_BUFFER = 1024;

    private Map<InetAddress, String> clientesConectados;
    private DatagramSocket socket;
    private JTextArea areaMensajes;

    public UDPServer() {
        clientesConectados = new HashMap<>();
        areaMensajes = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(areaMensajes);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        setTitle("Servidor UDP");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        iniciarServidor();
    }

    private void iniciarServidor() {
        try {
            socket = new DatagramSocket(PUERTO);
            areaMensajes.append("Servidor UDP iniciado en el puerto " + PUERTO + "\n");

            new Thread(this::esperarClientes).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void esperarClientes() {
        try {
            while (true) {
                byte[] buffer = new byte[TAMANO_BUFFER];
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);

                InetAddress direccionCliente = paquete.getAddress();

                if (!clientesConectados.containsKey(direccionCliente)) {
                    // Nuevo cliente: solicitar nombre
                    enviarMensaje("Ingrese su nombre:", direccionCliente);
                    String nombreCliente = recibirMensaje(direccionCliente);
                    areaMensajes.append("Nuevo cliente conectado: " + nombreCliente + " (" + direccionCliente + ")\n");
                    clientesConectados.put(direccionCliente, nombreCliente);
                    enviarMensaje("¡Bienvenido, " + nombreCliente + "!", direccionCliente);
                }

                String mensaje = recibirMensaje(direccionCliente);
                broadcastMensaje(clientesConectados.get(direccionCliente) + ": " + mensaje, direccionCliente);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastMensaje(String mensaje, InetAddress remitente) {
        areaMensajes.append(mensaje + "\n");

        for (InetAddress cliente : clientesConectados.keySet()) {
            if (!cliente.equals(remitente)) {
                enviarMensaje(mensaje, cliente);
            }
        }
    }

    private void enviarMensaje(String mensaje, InetAddress destino) {
        try {
            byte[] buffer = mensaje.getBytes();
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, destino, PUERTO);
            socket.send(paquete);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String recibirMensaje(InetAddress origen) {
        try {
            byte[] buffer = new byte[TAMANO_BUFFER];
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
            socket.receive(paquete);
            return new String(paquete.getData(), 0, paquete.getLength());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UDPServer();
            }
        });
    }
}
