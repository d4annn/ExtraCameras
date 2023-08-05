package dan.extracameras.mixins;

import dan.extracameras.utils.Instance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract void reload(ResourceManager manager);

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void renderHand(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
        if(Instance.cameraOn) {

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
