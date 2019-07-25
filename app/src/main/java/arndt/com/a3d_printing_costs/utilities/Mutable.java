package arndt.com.a3d_printing_costs.utilities;

import java.util.Objects;

public class Mutable<V>{
    V b;

    public Mutable(V b) {
        this.b = b;
    }

    public V getB() {
        return b;
    }

    public void setB(V b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return b.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mutable<?> mutable = (Mutable<?>) o;
        return Objects.equals(b, mutable.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(b);
    }
}