package dan.extracameras.utils;

import dan.extracameras.camera.Camera;
import dan.extracameras.camera.FakeCameraClient;
import dan.extracameras.config.CameraConfig;
import dan.extracameras.config.Config;
import dan.extracameras.gui.widgets.map.WorldMap;
import dan.extracameras.packets.Packet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Instance {

    public static boolean isOnWorld = false;

    public static List<Packet> packets = new ArrayList<>();

    public static final File CONFIG_FOLDER = new File(new File(MinecraftClient.getInstance().runDirectory, "config").getAbsolutePath() + "\\ExtraCameras");

    public static final File CONFIG_FILE = new File(new File(MinecraftClient.getInstance().runDirectory, "config").getAbsolutePath() + "\\ExtraCameras\\config.json");

    public static FakeCameraClient fakeCamera;

    public static WorldMap map;

    public static boolean cameraOn = false;

    public static CameraConfig currentWorldCameras = null;

    public static Camera lastCamera;
}
