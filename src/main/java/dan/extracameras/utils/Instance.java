package dan.extracameras.utils;

import dan.extracameras.camera.Camera;
import dan.extracameras.camera.FakeCameraClient;
import dan.extracameras.config.CameraConfig;
import dan.extracameras.gui.widgets.map.WorldMap;
import dan.extracameras.packets.Packet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Instance {

    public static final File CONFIG_FOLDER = new File(new File(MinecraftClient.getInstance().runDirectory, "config").getAbsolutePath() + "\\ExtraCameras");
    public static final File CONFIG_FILE = new File(CONFIG_FOLDER.getAbsolutePath() + "\\config.json");
    public static final File IMAGE_FOLDER = new File(CONFIG_FOLDER.getAbsolutePath() + "\\CameraImages");
    public static final File MAP_FOLDER = new File(CONFIG_FOLDER.getAbsolutePath() + "\\Maps");
    public static boolean isOnWorld = false;
    public static List<Packet> packets = new ArrayList<>();


    public static FakeCameraClient fakeCamera;

    public static WorldMap map;

    public static boolean cameraOn = false;

    public static CameraConfig currentWorldCameras = null;

    public static Camera lastCamera;

    public static Map<String, Map<String, Identifier>> images = new HashMap<>();

    public static String currentWorld = "";
}
