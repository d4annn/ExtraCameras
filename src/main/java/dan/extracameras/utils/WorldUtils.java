package dan.extracameras.utils;

import dan.extracameras.config.CameraConfig;
import dan.extracameras.config.Config;
import dan.extracameras.gui.widgets.map.WorldMap;
import dan.extracameras.objects.JoinEvent;
import dan.extracameras.packets.Packets;

import java.util.ArrayList;

public class WorldUtils {

    public static void onLeave() {
        Instance.fakeCamera = null;
        Instance.cameraOn = false;
        Instance.isOnWorld = false;
        Instance.currentWorldCameras = null;
        if (Instance.map != null)
            Instance.map.close();
        Instance.map = null;
        Instance.currentWorld = "";
    }

    public static void onJoin(JoinEvent event) {

        Instance.isOnWorld = true;
        Packets.executePackets();
        String world = event.getWorld();
        if (!event.isServer()) {
            String[] split = world.split("-");
            world = split[1].replaceFirst(" ", "");
        }
        Instance.currentWorld = world;
        Instance.map = new WorldMap();
        if (Config.getInstance().worldCameras != null) {
            for (CameraConfig configs : Config.getInstance().worldCameras) {
                if (event.isServer() == configs.isServer() && configs.getWorld().equals(world)) {
                    Instance.currentWorldCameras = configs;
                    FileUtils.initImages(Instance.currentWorldCameras.getCameras(), Instance.currentWorldCameras.getWorld());
                    return;
                }
            }
        }
        //If this executes means that is a new world
        CameraConfig newWorldConfig = new CameraConfig(new ArrayList<>(), world, event.isServer());
        Config.getInstance().addCameraConfig(newWorldConfig);
        Config.getInstance().saveConfig();
        Instance.currentWorldCameras = newWorldConfig;
    }
}
