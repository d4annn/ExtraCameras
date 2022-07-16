package dan.extracameras;

import dan.extracameras.config.Config;
import dan.extracameras.packets.Packets;
import dan.extracameras.utils.Variables;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtraCameras implements ClientModInitializer {

    public static final Logger logger = LogManager.getLogger("extracameras");

    @Override
    public void onInitializeClient() {
        Variables.config = new Config();
        Variables.config.loadUp();
        Variables.config.loadConfig();
        Variables.config.reloadConfig();
        Variables.config.saveConfig();
        KeyBindingHelper.registerKeyBinding(KeyBinds.OPEN_MENU);
        KeyBindingHelper.registerKeyBinding(KeyBinds.PLACE_CAMERA);
        KeyBindingHelper.registerKeyBinding(KeyBinds.OPEN_LAST_CAMERA);
        KeyBindingHelper.registerKeyBinding(KeyBinds.EXIT_CAMERA);
        Packets.registerPackets();
        //TODO: add tutorial
    }

    /*
    Camera exceptions , no loaded zone
    other dimension
     */
}
