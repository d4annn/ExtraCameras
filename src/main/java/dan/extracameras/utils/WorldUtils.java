package dan.extracameras.utils;

import dan.extracameras.config.CameraConfig;
import dan.extracameras.gui.widgets.map.WorldMap;
import dan.extracameras.objects.JoinEvent;
import dan.extracameras.packets.Packets;

import java.util.ArrayList;

public class WorldUtils {

    public static void onLeave() {
        Variables.fakeCamera = null;
        Variables.cameraOn = false;
        Variables.isOnWorld = false;
        Variables.currentWorldCameras = null;
        Variables.map = null;
        Variables.config.loadConfig();
    }

    public static void onJoin(JoinEvent event) {
        Variables.map = new WorldMap();
        Variables.isOnWorld = true;
        Packets.executePackets();
        String world = event.getWorld();
        if(!event.isServer()) {
            String[] split = world.split("-");
            world = split[1].replaceFirst(" ", "");
        }
        if (Variables.config.getWorldCameras() != null) {
            for (CameraConfig configs : Variables.config.getWorldCameras()) {
                if (event.isServer() == configs.isServer() && configs.getWorld().equals(world)) {
                    Variables.currentWorldCameras = configs;
                    return;
                }
            }
        }
        //If this executes means that is a new world
        CameraConfig newWorldConfig = new CameraConfig(new ArrayList<>(), world, event.isServer());
        Variables.config.addCameraConfig(newWorldConfig);
        Variables.config.saveConfig();
        Variables.currentWorldCameras = newWorldConfig;
    }
}
