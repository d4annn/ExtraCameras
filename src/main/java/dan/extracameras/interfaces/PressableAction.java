package dan.extracameras.interfaces;

@FunctionalInterface
public interface PressableAction<T> {

    void onPress(T t);
}
