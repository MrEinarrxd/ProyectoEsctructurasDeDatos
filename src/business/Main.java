package business;

import presentation.TranspoRouteGUI;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            RequestController controller = new RequestController();
            GuiController guiController = new GuiController(controller);
            new TranspoRouteGUI(guiController).setVisible(true);
        });
    }
}
