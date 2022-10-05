package cliente;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 * Esta clase se encarga de correr los threads de enviar y recibir texto.
 * También se encarga de crear la interfaz gráfica.
 *
 * @author Daniel 
 */
public class PrincipalChat extends JFrame {

    public JTextField campoTexto; //Para mostrar mensajes de los usuarios 
    public JTextArea areaTexto; //Para ingresar mensaje a enviar

    private static ServerSocket servidor; //
    private static Socket cliente; //Socket para conectarse con el cliente
    private static String ip = "127.0.0.1"; //IP a la cual se conecta (hay que cambiarlo para usarlo en red)

    public static PrincipalChat main;

    public PrincipalChat() {

        super("Cliente"); //Establece titulo al Frame de ese lado

        campoTexto = new JTextField(); //Se crea el espacio para los textos enviados/recibidos.
        campoTexto.setEditable(false); //No permite que sea editable el campo de texto.
        Font fuente = new Font("Monospaced", 50, 20);
        add(campoTexto, BorderLayout.SOUTH); //Coloca el espacio de escritura en la parte inferior.

        areaTexto = new JTextArea(); //Crear displayArea
        areaTexto.setEditable(false);
        add(new JScrollPane(areaTexto), BorderLayout.CENTER);
        areaTexto.setBackground(Color.WHITE); //Color de fondo en espacio enviados/recibidos.
        areaTexto.setForeground(Color.DARK_GRAY); //Letra gris oscuro en espacio enviados/recibidos.
        areaTexto.setFont(fuente); //Asigna el tipo de fuente en espacio enviados/recibidos.
        campoTexto.setForeground(Color.BLACK); //Letra negra en espacio de escritura.
        campoTexto.setFont(fuente); //Tipo de fuente en espacio de escritura.

        //Crea menú "Opciones" y submenú "Opción", "Guardar Chat" y "Salir".
        JMenu menuOpciones = new JMenu("Opción");
        JMenuItem guardar = new JMenuItem("Guardar Chat");
        JMenuItem salir = new JMenuItem("Salir");
        menuOpciones.add(guardar); //Agrega el submenú.
        menuOpciones.add(salir); //Agrega el submenú.

        JMenuBar barra = new JMenuBar(); //Crea la barra de menús.
        setJMenuBar(barra); //Agrega barra de menús a la aplicación.
        barra.add(menuOpciones); //Se agrega Opciones a la visibilidad.

        //Acción que se realiza cuando se presiona la opción ''Salir''
        salir.addActionListener(new ActionListener() { //Clase interna de la librería.
            public void actionPerformed(ActionEvent e) {
                System.exit(0); //Sale de la aplicación.
            }
        });
        guardar.addActionListener(new ActionListener() { //Clase interna de la librería.
            public void actionPerformed(ActionEvent e) {

                try {

                    File file = new File("./ConversacionCliente.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
                    bw.write(areaTexto.getText() + "\r\n");
                    bw.close();

                } catch (IOException e2) {

                }

            }
        });
        setSize(800, 650); //Establece tamaño a la ventana
        setVisible(true); //La ventana es visible.
    }

    //Muestra mensajes en pantalla
    public void mostrarMensaje(String mensaje) {
        areaTexto.append(mensaje + "\n ");
    }

    public void habilitarTexto(boolean editable) {
        campoTexto.setEditable(editable);
    }

    public static void main(String[] args) {
        PrincipalChat main = new PrincipalChat(); ////Creación del objeto
        main.setLocationRelativeTo(null);   //Centrar el JFrame
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Habilita cerrar la ventana.
        ExecutorService executor = Executors.newCachedThreadPool(); //Para correr los threads.

        try {
            main.mostrarMensaje("Esperando servidor...");
            cliente = new Socket(InetAddress.getByName(ip), 5400); //Se comunica con el server
            main.mostrarMensaje("Conectado a: " + cliente.getInetAddress().getHostName() + "\n");

            main.habilitarTexto(true); //Habilita entrada de texto

            //Ejecución de los Threads (hilos)
            executor.execute(new ThreadRecibe(cliente, main));
            executor.execute(new ThreadEnvia(cliente, main));

        } catch (IOException ex) {
            Logger.getLogger(PrincipalChat.class.getName()).log(Level.SEVERE, null, ex);
        } //Fin del catch
        finally {
        }
        executor.shutdown();
    }
}
