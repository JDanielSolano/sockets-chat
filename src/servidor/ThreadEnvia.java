package servidor;

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

        main.campoTexto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                mensaje = event.getActionCommand();
                enviarDatos(mensaje); //se envia el mensaje
                main.campoTexto.setText(""); //borra el texto del espacio
            } //Fin metodo actionPerformed
        }
        );//Fin llamada a addActionListener
    }

    //enviar objeto a Cliente
    private void enviarDatos(String mensaje) {
        try {
            salida.writeObject(usuario + ": " + mensaje + "\n");
            salida.flush(); //flush salida a cliente
            main.mostrarMensaje(usuario + ": " + mensaje + "\n");
        } //Fin try
        catch (IOException ioException) {
            main.mostrarMensaje("Error al escribir el mensaje");
        } //Fin catch  

    } //Fin methodo enviarDatos

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
