import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {

    private double G; // Гравитационная постоянная
    private double softeningSq; // Квадрат параметра сглаживания (softening parameter)

    /**
     * @param G Гравитационная постоянная
     * @param softening Параметр сглаживания (защита от сингулярности при r -> 0)
     */
    public PhysicsEngine(double G, double softening) {
        this.G = G;
        this.softeningSq = softening * softening;
    }

    /**
     * Основной метод обновления физики (Velocity Verlet integration).
     * @param deltaTime Шаг времени (в секундах)
     * @param bodies Список тел для расчета
     */
    public void update(double deltaTime, List<CelestialBody> bodies) {

        // ФАЗА 1: Обновление позиций и "половинный" шаг скоростей
        // v(t + dt/2) = v(t) + 0.5 * a(t) * dt
        // x(t + dt) = x(t) + v(t + dt/2) * dt
        for (int i = 0; i < bodies.size(); i++) {
            CelestialBody body = bodies.get(i);

            body.velocity.add(
                body.acceleration.x * 0.5 * deltaTime,
                body.acceleration.y * 0.5 * deltaTime
            );

            body.position.add(
                body.velocity.x * deltaTime,
                body.velocity.y * deltaTime
            );
        }

        // Обнуляем ускорения для нового шага расчета сил
        for (int i = 0; i < bodies.size(); i++) {
            bodies.get(i).acceleration.set(0, 0);
        }

        // ФАЗА 2: Расчет новых ускорений a(t + dt)
        // ВАЖНО: Ниже реализована сложность O(N^2).
        //
        // [TODO: Интеграция Barnes-Hut / QuadTree]
        // 1. Построить QuadTree(bodies)
        // 2. Для каждого тела: tree.calculateForceOn(body)
        // 3. Убрать вложенный O(N^2) цикл ниже.

        int size = bodies.size();
        for (int i = 0; i < size; i++) {
            CelestialBody b1 = bodies.get(i);

            for (int j = i + 1; j < size; j++) {
                CelestialBody b2 = bodies.get(j);

                double dx = b2.position.x - b1.position.x;
                double dy = b2.position.y - b1.position.y;

                // r^2 + epsilon^2
                double distSq = dx * dx + dy * dy + softeningSq;

                // Оптимизация: a = G * m / r^2. Учитывая вектор направления (dx/r),
                // множитель становится: G * m / r^3.
                // distSq * Math.sqrt(distSq) = r^3
                double invDistCube = 1.0 / (distSq * Math.sqrt(distSq));

                // Ускорение для первого тела (вызванное вторым)
                double accel1 = G * b2.mass * invDistCube;
                b1.acceleration.add(accel1 * dx, accel1 * dy);

                // Ускорение для второго тела (вызванное первым) — 3й закон Ньютона
                double accel2 = G * b1.mass * invDistCube;
                b2.acceleration.sub(accel2 * dx, accel2 * dy);
            }
        }

        // ФАЗА 3: Завершение шага скоростей (вторая половина)
        // v(t + dt) = v(t + dt/2) + 0.5 * a(t + dt) * dt
        for (int i = 0; i < bodies.size(); i++) {
            CelestialBody body = bodies.get(i);
            body.velocity.add(
                body.acceleration.x * 0.5 * deltaTime,
                body.acceleration.y * 0.5 * deltaTime
            );
        }
    }

    /**
     * API для предикции (Deep Copy).
     * ИИ-штурман может взять этот слепок и прогнать метод update() 1000 раз
     * в параллельном потоке, не затрагивая рендер и текущий стейт игры.
     */
    public static List<CelestialBody> deepCopyState(List<CelestialBody> currentState) {
        List<CelestialBody> copiedState = new ArrayList<>(currentState.size());
        for (int i = 0; i < currentState.size(); i++) {
            copiedState.add(currentState.get(i).cloneBody());
        }
        return copiedState;
    }
}
