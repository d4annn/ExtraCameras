package dan.extracameras;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {

    public static final String CATEGORY = "key.extracameras.category";

    public static final KeyBinding OPEN_MENU = new KeyBinding("key.extracameras.menu", GLFW.GLFW_KEY_F7, CATEGORY);

    public static final KeyBinding PLACE_CAMERA = new KeyBinding("key.extracameras.place", GLFW.GLFW_KEY_L, CATEGORY);

    public static final KeyBinding OPEN_LAST_CAMERA = new KeyBinding("key.extracameras.openlast", InputUtil.UNKNOWN_KEY.getCode(), CATEGORY);

    public static final KeyBinding EXIT_CAMERA = new KeyBinding("key.extracameras.exit", InputUtil.GLFW_KEY_X, CATEGORY);

    public static final KeyBinding SCREENSHOT = new KeyBinding("key.extracameras.screenshot", InputUtil.UNKNOWN_KEY.getCode(), CATEGORY);
}
