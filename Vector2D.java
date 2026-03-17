public class Vector2D {
    public double x;
    public double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D() {
        this(0, 0);
    }

    // Мутирующие методы для GC-оптимизации (in-place)
    public Vector2D set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2D set(Vector2D other) {
        this.x = other.x;
        this.y = other.y;
        return this;
    }

    public Vector2D add(double addX, double addY) {
        this.x += addX;
        this.y += addY;
        return this;
    }

    public Vector2D sub(double subX, double subY) {
        this.x -= subX;
        this.y -= subY;
        return this;
    }

    public Vector2D scl(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    // Глубокое копирование вектора
    public Vector2D cpy() {
        return new Vector2D(this.x, this.y);
    }
}
