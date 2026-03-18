import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Orbital Chaos Simulation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            SimulationPanel panel = new SimulationPanel();
            frame.add(panel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            panel.startSimulation();
        });
    }

    static class SimulationPanel extends JPanel {
        private final List<CelestialBody> bodies;
        private final PhysicsEngine engine;
        private final double deltaTime = 0.016; // ~60 FPS
        private Timer timer;

        public SimulationPanel() {
            setPreferredSize(new Dimension(800, 600));
            setBackground(Color.BLACK);

            bodies = new ArrayList<>();
            // Sun
            Vector2D pos1 = new Vector2D(0, 0);
            Vector2D vel1 = new Vector2D(0, 0);
            CelestialBody sun = new CelestialBody(1000, 30, pos1, vel1);

            // Planet
            Vector2D pos2 = new Vector2D(200, 0);
            Vector2D vel2 = new Vector2D(0, 15);
            CelestialBody planet = new CelestialBody(10, 10, pos2, vel2);

            bodies.add(sun);
            bodies.add(planet);

            // PhysicsEngine (G = 1.0, softening = 1.0 for visual purposes)
            engine = new PhysicsEngine(1.0, 1.0);
        }

        public void startSimulation() {
            timer = new Timer(16, e -> {
                engine.update(deltaTime, bodies);
                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            for (CelestialBody body : bodies) {
                int drawX = centerX + (int) body.position.x - (int) body.radius;
                int drawY = centerY + (int) body.position.y - (int) body.radius;
                int diameter = (int) (body.radius * 2);

                if (body.mass > 500) {
                    g.setColor(Color.YELLOW);
                } else {
                    g.setColor(Color.CYAN);
                }

                g.fillOval(drawX, drawY, diameter, diameter);
            }
        }
    }
}
