package servidor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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
public class PrincipalChat extends javax.swing.JFrame {

    public JTextField campoTexto; //Espacio donde se escribe el mensaje.
    public JTextArea areaTexto; //Espacio para ver mensajes enviados y recibidos.
    private static ServerSocket servidor; //Asigna la variable "Servidor"
    private static Socket conexion; //Socket para conectarse con el servidor.
    private static String ip = "127.0.0.1"; //IP a la cual se conecta el proyecto.

    public static PrincipalChat main;

    public PrincipalChat() {
        super("Servidor"); //Título del frame.

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
//        ImagenFondo image=new ImagenFondo();
//        image.setImage("/imagenes/servidor.jpg");
//        setContentPane(image);
        

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

                    File file = new File("./ConversacionServidor.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
                    bw.write(areaTexto.getText()+ "\r\n");
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
        PrincipalChat main = new PrincipalChat(); //Creación del objeto
        main.setLocationRelativeTo(null);   //Centrar el JFrame
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Habilita para cerrar la ventana.
        ExecutorService executor = Executors.newCachedThreadPool(); //Para correr los threads.

        try {
            servidor = new ServerSocket(5400, 100);
            main.mostrarMensaje("Esperando cliente... \n");

            //Bucle infinito para esperar conexiones de los clientes.
            while (true) {
                try {
                    conexion = servidor.accept(); //Permite al servidor aceptar conexiones.        

                    main.mostrarMensaje("Conexión establecida");
                    main.mostrarMensaje("Conectado a : " + conexion.getInetAddress().getHostName() + "\n");

                    main.habilitarTexto(true); //Permite escribir texto para enviar.

                    //Ejecucion de los threads
                    executor.execute(new ThreadRecibe(conexion, main)); //Cliente
                    executor.execute(new ThreadEnvia(conexion, main));
                } catch (IOException ex) {
                    Logger.getLogger(PrincipalChat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PrincipalChat.class.getName()).log(Level.SEVERE, null, ex);
        } //Fin del catch.
        finally {
        }
        executor.shutdown();
    }
}
