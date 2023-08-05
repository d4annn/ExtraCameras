package dan.extracameras.packets;

import dan.extracameras.KeyBinds;
import dan.extracameras.camera.CameraEntity;
import dan.extracameras.gui.CreateCameraScreen;
import dan.extracameras.gui.MapScreen;
import dan.extracameras.utils.CameraUtils;
import dan.extracameras.utils.Instance;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class Packets {

    public static void executePackets() {
        if (!Instance.packets.isEmpty()) {
            for (Packet packet : Instance.packets) {
                packet.execute();
            }
            Instance.packets.clear();
        }
    }

    public static void registerPackets() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (KeyBinds.OPEN_MENU.wasPressed() && Instance.isOnWorld) {
                MinecraftClient.getInstance().setScreen(new MapScreen());
            }

            if (KeyBinds.PLACE_CAMERA.wasPressed() && Instance.isOnWorld) {
                MinecraftClient.getInstance().setScreen(new CreateCameraScreen(null, null, false));
            }

            if (KeyBinds.EXIT_CAMERA.wasPressed() && Instance.isOnWorld && Instance.cameraOn) {
                CameraUtils.onDisable();
            }

            if (KeyBinds.OPEN_LAST_CAMERA.wasPressed() && Instance.isOnWorld && Instance.cameraOn) {
                if (null != Instance.lastCamera) {
                    CameraUtils.onEnable(Instance.lastCamera);
                }
            }
            if (Instance.isOnWorld)
                Instance.map.tryUpdate();
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (Instance.cameraOn) CameraEntity.movementTick();
        });
    }
}
