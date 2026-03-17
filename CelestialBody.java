public class CelestialBody {
    public double mass;
    public double radius;

    public final Vector2D position;
    public final Vector2D velocity;
    public final Vector2D acceleration;

    public CelestialBody(double mass, double radius, Vector2D position, Vector2D velocity) {
        this.mass = mass;
        this.radius = radius;
        // Копируем стартовые векторы, чтобы избежать мутаций по ссылкам извне
        this.position = new Vector2D().set(position);
        this.velocity = new Vector2D().set(velocity);
        this.acceleration = new Vector2D();
    }

    /**
     * Copy-конструктор для глубокого копирования (Deep Copy).
     * Используется ИИ-штурманом для симуляции будущего в других потоках.
     */
    public CelestialBody(CelestialBody other) {
        this.mass = other.mass;
        this.radius = other.radius;
        this.position = other.position.cpy();
        this.velocity = other.velocity.cpy();
        this.acceleration = other.acceleration.cpy();
    }

    /**
     * Создает независимый слепок тела.
     */
    public CelestialBody cloneBody() {
        return new CelestialBody(this);
    }
}
