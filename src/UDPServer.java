import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPServer {
    private static final int PUERTO = 9876;
    private static final int TAMANO_BUFFER = 1024;

    private List<InetAddress> clientesConectados;
    private DatagramSocket socket;

    public UDPServer() {
        clientesConectados = new ArrayList<>();

        try {
            socket = new DatagramSocket(PUERTO);
            System.out.println("Servidor UDP iniciado en el puerto " + PUERTO);
            esperarClientes();
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

                if (!clientesConectados.contains(direccionCliente)) {
                    System.out.println("Nuevo cliente conectado: " + direccionCliente);
                    clientesConectados.add(direccionCliente);
                }

                String mensaje = new String(paquete.getData(), 0, paquete.getLength());
                broadcastMensaje(mensaje, direccionCliente);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastMensaje(String mensaje, InetAddress remitente) {
        System.out.println("Mensaje recibido de " + remitente + ": " + mensaje);

        for (InetAddress cliente : clientesConectados) {
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

    public static void main(String[] args) {
        new UDPServer();
    }
}
