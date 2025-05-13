package utils;

import javax.swing.*;
import java.awt.*;

public class WindowManager {

    // Dimensions minimales standards pour toutes les fenêtres
    public static final int MIN_WIDTH = 1000;
    public static final int MIN_HEIGHT = 600;

    /**
     * Applique un style cohérent à une fenêtre et la centre à l'écran
     * @param window La fenêtre à configurer
     */
    public static void setupWindow(JFrame window) {
        // Définition de la taille minimale
        window.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));

        // Comportement à la fermeture
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Centrage de la fenêtre
        window.setLocationRelativeTo(null);

        // Ajout d'un style d'icône pour toutes les fenêtres (facultatif)
        // window.setIconImage(new ImageIcon(WindowManager.class.getResource("/views/LogoAichi.png")).getImage());
    }

    /**
     * Ouvre une nouvelle fenêtre en fermant l'ancienne pour donner l'impression
     * d'une navigation dans la même application
     * @param newWindow La nouvelle fenêtre à afficher
     * @param currentWindow La fenêtre actuelle à fermer
     */
    public static void switchWindow(JFrame newWindow, JFrame currentWindow) {
        setupWindow(newWindow);
        newWindow.setVisible(true);
        currentWindow.dispose();
    }
}