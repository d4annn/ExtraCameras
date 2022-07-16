package dan.extracameras.packets;

import dan.extracameras.KeyBinds;
import dan.extracameras.camera.Camera;
import dan.extracameras.camera.CameraEntity;
import dan.extracameras.gui.CreateCameraScreen;
import dan.extracameras.gui.MapScreen;
import dan.extracameras.utils.CameraUtils;
import dan.extracameras.utils.RenderUtils;
import dan.extracameras.utils.Variables;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Packets {

    public static void executePackets() {
        if (!Variables.packets.isEmpty()) {
            for (Packet packet : Variables.packets) {
                packet.execute();
            }
            Variables.packets.clear();
        }
    }

    public static void registerPackets() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (KeyBinds.OPEN_MENU.wasPressed() && Variables.isOnWorld) {
                MinecraftClient.getInstance().setScreen(new MapScreen());
            }

            if (KeyBinds.PLACE_CAMERA.wasPressed() && Variables.isOnWorld) {
                MinecraftClient.getInstance().setScreen(new CreateCameraScreen(null, null, false));
            }

            if (KeyBinds.EXIT_CAMERA.wasPressed() && Variables.isOnWorld && Variables.cameraOn) {
                CameraUtils.onDisable();
            }

            if (KeyBinds.OPEN_LAST_CAMERA.wasPressed() && Variables.isOnWorld && Variables.cameraOn) {
                if (null != Variables.lastCamera) {
                    CameraUtils.onEnable(Variables.lastCamera);
                }
            }
            if (Variables.isOnWorld)
                Variables.map.tryUpdate();
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (Variables.cameraOn) CameraEntity.movementTick();
        });
    }
}
