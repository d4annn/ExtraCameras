package dan.extracameras;

import dan.extracameras.config.Config;
import dan.extracameras.packets.Packets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtraCameras implements ClientModInitializer {

    public static final Logger logger = LogManager.getLogger("extracameras");

    @Override
    public void onInitializeClient() {
        Config.setInstance(new Config());
        Config.getInstance().loadConfig();
        KeyBindingHelper.registerKeyBinding(KeyBinds.OPEN_MENU);
        KeyBindingHelper.registerKeyBinding(KeyBinds.PLACE_CAMERA);
        KeyBindingHelper.registerKeyBinding(KeyBinds.OPEN_LAST_CAMERA);
        KeyBindingHelper.registerKeyBinding(KeyBinds.EXIT_CAMERA);
        KeyBindingHelper.registerKeyBinding(KeyBinds.SCREENSHOT);
        Packets.registerPackets();
        //TODO: add tutorial
    }

    /*
    Camera exceptions , no loaded zone
    other dimension
     */
}
