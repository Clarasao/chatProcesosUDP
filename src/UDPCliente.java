import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPCliente {
    private static final int PUERTO = 9876;
    private static final int TAMANO_BUFFER = 1024;

    private DatagramSocket socket;
    private InetAddress servidorDireccion;

    public UDPCliente() {
        try {
            socket = new DatagramSocket();
            servidorDireccion = InetAddress.getByName("localhost");

            System.out.println("Cliente UDP iniciado. Escriba 'salir' para cerrar.");

            new Thread(this::recibirMensajes).start();
            enviarMensajes();
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
                System.out.println("Servidor: " + mensaje);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enviarMensajes() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String mensaje = scanner.nextLine();

                if (mensaje.equalsIgnoreCase("salir")) {
                    System.out.println("Desconectando cliente.");
                    socket.close();
                    System.exit(0);
                }

                byte[] buffer = mensaje.getBytes();
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, servidorDireccion, PUERTO);
                socket.send(paquete);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new UDPCliente();
    }
}
