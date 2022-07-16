package dan.extracameras.mixins;

import dan.extracameras.camera.CameraEntity;
import dan.extracameras.utils.RenderUtils;
import dan.extracameras.utils.Variables;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract void reload(ResourceManager manager);

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void renderHand(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
        if(Variables.cameraOn) {

/*
            CameraEntity.setTesellating(true);
            BlockState state = Blocks.STONE.getDefaultState();
            MatrixStack matrix = matrices;
            /*First translate by the relative position
              undo rotation


            matrix.push();
            Vec3d relativePosition = RenderUtils.calculateRelativePosition(camera.getPos(), new Vec3d(-172, 130, -157));
            matrix.translate(relativePosition.x, relativePosition.y, relativePosition.z);
            VertexConsumer consumer = Variables.vertex;
            MinecraftClient.getInstance().getBlockRenderManager().renderBlock(state, new BlockPos(3, 8, 5), client.world, matrix, consumer, false, new Random());
            CameraEntity.setTesellating(false);
            matrix.pop();

 */
        }

    }
}
