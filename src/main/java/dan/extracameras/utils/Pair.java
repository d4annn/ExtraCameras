package dan.extracameras.utils;

import org.jetbrains.annotations.NotNull;

public class Pair<E, T> {

    private Object o1;
    private Object o2;

    public Pair(@NotNull Object o1,@NotNull Object o2) {
        this.o1 = o1;
        this.o2 = o2;
    }

    public boolean areEqual() {
        return this.o1.equals(this.o2);
    }

    public Object getO1() {
        return this.o1;
    }

    public Object getO2() {
        return this.o2;
    }
}
