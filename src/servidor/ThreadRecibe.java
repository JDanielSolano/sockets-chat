package servidor;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadRecibe implements Runnable {
    private final PrincipalChat main;
    private String mensaje; 
    private ObjectInputStream entrada;
    private Socket cliente;
   
    
   //Inicializar chatServer y configurar GUI
   public ThreadRecibe(Socket cliente, PrincipalChat main){
       this.cliente = cliente;
       this.main = main;
   }  

    public void mostrarMensaje(String mensaje) {
        main.areaTexto.append(mensaje);
    } 
   
    public void run() {
        try {
            entrada = new ObjectInputStream(cliente.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ThreadRecibe.class.getName()).log(Level.SEVERE, null, ex);
        }
        do { //Procesa los mensajes enviados desde el servidor.
            try {//Leer el mensaje y mostrarlo.
                mensaje = (String) entrada.readObject(); //Leer nuevo mensaje.
                main.mostrarMensaje(mensaje);
            } //Fin del try.
            catch (SocketException ex) {
            }
            catch (EOFException eofException) {
                main.mostrarMensaje("Fin de la conexión");
                break;
            } //Fin del catch.
            catch (IOException ex) {
                Logger.getLogger(ThreadRecibe.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException classNotFoundException) {
                main.mostrarMensaje("Objeto desconocido");
            } //Fin de otro catch.              

        } while (!mensaje.equals("<<SERVIDOR>> TERMINATE")); //Ejecuta hasta que el server escriba TERMINATE

        try {
            entrada.close(); //Cierra entrada.
            cliente.close(); //Cierra socket.
        } //Fin try
        catch (IOException ioException) {
            ioException.printStackTrace();
        } //fin catch

        main.mostrarMensaje("Fin de la conexión");
        System.exit(0);
    } 
} 
