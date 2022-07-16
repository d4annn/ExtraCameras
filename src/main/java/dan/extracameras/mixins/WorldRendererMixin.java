package dan.extracameras.mixins;

import com.mojang.blaze3d.platform.GlStateManager;
import dan.extracameras.utils.CameraUtils;
import dan.extracameras.utils.Variables;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldRenderer.class, priority = 1001)
public class WorldRendererMixin {

    @Shadow
    private int cameraChunkX;
    @Shadow
    private int cameraChunkZ;

    private int lastUpdatePosX;
    private int lastUpdatePosZ;

    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    private void cancelRainRender(LightmapTextureManager lightmap, float partialTicks, double x, double y, double z, CallbackInfo ci) {
        if (Variables.cameraOn) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=terrain_setup"))
    private void preSetupTerrain(net.minecraft.client.util.math.MatrixStack matrixStack, float partialTicks, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer renderer, LightmapTextureManager lightmap, Matrix4f matrix4f, CallbackInfo ci) {
        if (Variables.cameraOn) {
            CameraUtils.setFreeCameraSpectator(true);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=compilechunks"))
    private void postSetupTerrain(net.minecraft.client.util.math.MatrixStack matrixStack, float partialTicks, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer renderer, LightmapTextureManager lightmap, Matrix4f matrix4f, CallbackInfo ci) {
        CameraUtils.setFreeCameraSpectator(false);
    }

    @Redirect(method = "render", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;", ordinal = 3))
    private Entity allowRenderingClientPlayerInFreeCameraMode(Camera camera) {
        if (Variables.cameraOn) {
            return MinecraftClient.getInstance().player;
        }

        return camera.getFocusedEntity();
    }

    @Inject(method = "setupTerrain", require = 0,
            at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/client/render/WorldRenderer;lastCameraChunkUpdateX:D"))
    private void rebuildChunksAroundCamera1(
            Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator, CallbackInfo ci) {
        if (Variables.cameraOn) {
            // Hold on to the previous update position before it gets updated
            this.lastUpdatePosX = this.cameraChunkX;
            this.lastUpdatePosZ = this.cameraChunkZ;
        }
    }

    @Inject(method = "setupTerrain", require = 0,
            at = @At(value = "INVOKE", shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/client/render/BuiltChunkStorage;updateCameraPosition(DD)V"))
    private void rebuildChunksAroundCamera2(
            Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator, CallbackInfo ci) {
        if (Variables.cameraOn) {
            int x = MathHelper.floor(camera.getPos().x) >> 4;
            int z = MathHelper.floor(camera.getPos().z) >> 4;
            CameraUtils.markChunksForRebuild(x, z, this.lastUpdatePosX, this.lastUpdatePosZ);
        }
    }
}
