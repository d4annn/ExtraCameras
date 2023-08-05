package dan.extracameras.utils;

import dan.extracameras.camera.Camera;
import dan.extracameras.camera.CameraEntity;
import dan.extracameras.camera.FakeCameraClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

public class CameraUtils {

    private static boolean spec = false;

    public static void setFreeCameraSpectator(boolean isSpectator) {
        spec = isSpectator;
    }

    public static boolean getFreeCameraSpectator() {
        return spec;
    }

    public static void onEnable(Camera camera) {
        Instance.fakeCamera = new FakeCameraClient();
        Instance.cameraOn = true;
        MinecraftClient.getInstance().setScreen(null);
        CameraEntity.setCameraState(true, camera);
        Instance.lastCamera = camera;
        CameraEntity.setTesellating(false);
    }

    public static void onDisable() {
        Instance.fakeCamera.despawn();
        Instance.fakeCamera.resetPlayerPosition();
        Instance.cameraOn = false;
        MinecraftClient.getInstance().worldRenderer.reload();
        CameraEntity.setTesellating(false);
        CameraEntity.setCameraState(false, null);
    }

    @Nullable
    public static Entity getCameraEntity() {
        MinecraftClient mc = MinecraftClient.getInstance();
        Entity entity = mc.getCameraEntity();
        if (entity == null) {
            entity = mc.player;
        }
        return entity;
    }

    public static void markChunksForRebuildOnDeactivation(int lastChunkX, int lastChunkZ) {
        Entity entity = getCameraEntity();
        MinecraftClient mc = MinecraftClient.getInstance();
        final int viewDistance = mc.options.viewDistance;
        final int chunkX = MathHelper.floor(entity.getX() / 16.0) >> 4;
        final int chunkZ = MathHelper.floor(entity.getZ() / 16.0) >> 4;

        final int minCameraCX = lastChunkX - viewDistance;
        final int maxCameraCX = lastChunkX + viewDistance;
        final int minCameraCZ = lastChunkZ - viewDistance;
        final int maxCameraCZ = lastChunkZ + viewDistance;
        final int minCX = chunkX - viewDistance;
        final int maxCX = chunkX + viewDistance;
        final int minCZ = chunkZ - viewDistance;
        final int maxCZ = chunkZ + viewDistance;

        for (int cz = minCZ; cz <= maxCZ; ++cz) {
            for (int cx = minCX; cx <= maxCX; ++cx) {
                // Mark all chunks that were not in free camera range
                if ((cx < minCameraCX || cx > maxCameraCX || cz < minCameraCZ || cz > maxCameraCZ) &&
                        isClientChunkLoaded(mc.world, cx, cz)) {
                    markChunkForReRender(mc.worldRenderer, cx, cz);
                }
            }
        }
    }

    public static void markChunksForRebuild(int chunkX, int chunkZ, int lastChunkX, int lastChunkZ) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.world == null || (chunkX == lastChunkX && chunkZ == lastChunkZ)) {
            return;
        }

        final int viewDistance = mc.options.viewDistance;

        if (chunkX != lastChunkX) {
            final int minCX = chunkX > lastChunkX ? lastChunkX + viewDistance : chunkX - viewDistance;
            final int maxCX = chunkX > lastChunkX ? chunkX + viewDistance : lastChunkX - viewDistance;

            for (int cx = minCX; cx <= maxCX; ++cx) {
                for (int cz = chunkZ - viewDistance; cz <= chunkZ + viewDistance; ++cz) {
                    if (isClientChunkLoaded(mc.world, cx, cz)) {
                        markChunkForReRender(mc.worldRenderer, cx, cz);
                    }
                }
            }
        }
    }

    public static void markChunkForReRender(WorldRenderer renderer, int chunkX, int chunkZ) {
        for (int cy = 0; cy < 16; ++cy) {
            renderer.scheduleBlockRender(chunkX, cy, chunkZ);
        }
    }

    public static boolean isClientChunkLoaded(ClientWorld world, int chunkX, int chunkZ) {
        return world.getChunkManager().getChunk(chunkX, chunkZ, ChunkStatus.FULL, false) != null;
    }
}
