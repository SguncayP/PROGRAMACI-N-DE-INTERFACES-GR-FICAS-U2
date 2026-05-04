package controlador;

import java.util.Locale;
import javax.swing.UIManager;
import vista.ventana;

public class Main {
    public static void main(String[] args) {
        // Configuración inicial en Español
        Locale localeSeleccionado = Locale.forLanguageTag("es");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        java.awt.EventQueue.invokeLater(() -> {
            try {
                ventana vistaPrincipal = new ventana(localeSeleccionado);
                new logica_ventana(vistaPrincipal);
                vistaPrincipal.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}