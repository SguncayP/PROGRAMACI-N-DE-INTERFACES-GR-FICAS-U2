package controlador;

import javax.swing.UIManager;
import vista.ventana;

/**
 * Clase principal que inicia la aplicación siguiendo el patrón MVC.
 */
public class Main {

    public static void main(String[] args) {
        // Intentar establecer el aspecto visual del sistema operativo (Look and Feel)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, continuará con el estilo por defecto de Java (Metal/Nimbus)
            e.printStackTrace();
        }

        // Ejecutar la creación de la interfaz en el hilo de despacho de eventos (EDT)
        // para garantizar la estabilidad de los componentes Swing.
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // 1. Instanciar la Vista
                    ventana vistaPrincipal = new ventana();

                    // 2. Instanciar el Controlador pasándole la vista
                    // El controlador se encargará de inicializar la lógica y el modelo
                    new logica_ventana(vistaPrincipal);

                    // 3. Hacer visible la ventana
                    vistaPrincipal.setVisible(true);

                    // Centrar la ventana en la pantalla
                    vistaPrincipal.setLocationRelativeTo(null);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}