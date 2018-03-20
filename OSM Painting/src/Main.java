import javax.swing.*;
import java.io.BufferedReader;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Model model;
            if (args.length == 0) {
                model = new Model("/Users/caecilieiversen/IdeaProjects/handin3/src/map.osm");
            } else {
                model = new Model(args[0]);
            }
            DrawWindow drawWindow = new DrawWindow(model);
        });
    }
}
