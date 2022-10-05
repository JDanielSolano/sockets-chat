package cliente;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;
import javax.swing.JOptionPane;

public class ThreadEnvia implements Runnable {

    private final PrincipalChat main;
    private ObjectOutputStream salida;
    private String mensaje;
    private Socket conexion;
    private String usuario;

    public ThreadEnvia(Socket conexion, final PrincipalChat main) {
        this.conexion = conexion;
        this.main = main;

        try {
            usuario = JOptionPane.showInputDialog("Ingrese el usuario (Servidor)");//Esto es el login
            while("".equals(usuario)){        

                usuario = JOptionPane.showInputDialog("Usuario invalido, ingresar usuario");//Esto es el login
            
            }
        } catch (Exception ex) {

        }
        //Cuando se empieza a escribir algo en el chat
        main.campoTexto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                mensaje = event.getActionCommand();
                enviarDatos(mensaje); //Esto lo que hace es enviar el mensaje.
                main.campoTexto.setText(""); //Deja en blanco el espacio cuando ya se manda el chat
                //Para que así no estorbe y dé campo de escribir otra cosa
            } //Se acaba el método "actionPerformed"
        }
        );//Se acaba la llamada de "addActionListener"
    }

    //Enviar mensaje al Servidor
    private void enviarDatos(String mensaje) {//Envia los mensajes.
        try {

            salida.writeObject(usuario + ": " + mensaje + "\n");
            salida.flush(); //Salida de ''flush'' a cliente
            main.mostrarMensaje(usuario + ": " + mensaje + "\n");
        } //Cierra try
        catch (IOException ioException) {
            main.mostrarMensaje("Error al escribir el mensaje");//Por aquello, porque a veces se cae.
        } //Cierra catch 

    } //Se cierra el método de enviar datos

    //Maneja el área de el hilo que envía las cosas
    public void mostrarMensaje(String mensaje) {
        main.areaTexto.append(mensaje);
    }

    public void run() {
        try {
            salida = new ObjectOutputStream(conexion.getOutputStream());
            salida.flush();
        } catch (SocketException ex) {
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (NullPointerException ex) {
        }
    }

}
