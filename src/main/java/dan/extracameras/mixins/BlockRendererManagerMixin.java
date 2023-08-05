package dan.extracameras.mixins;

import dan.extracameras.utils.Instance;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockRenderManager.class)
public class BlockRendererManagerMixin {

    @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true)
    private void cancelDefaultRenderBlock(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, Random random, CallbackInfoReturnable<Boolean> cir) {
        if(Instance.cameraOn) {
            cir.cancel();
        }
    }

    @Inject(method = "renderDamage", at = @At("HEAD"), cancellable = true)
    private void cancelDefaultRenderDamage(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrices, VertexConsumer vertexConsumer, CallbackInfo ci) {
        if(Instance.cameraOn) {
            ci.cancel();
        }
    }

    @Inject(method = "renderBlockAsEntity", at = @At("HEAD"), cancellable = true)
    private void cancelDefaultRenderBlockAsEntity(BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light, int overlay, CallbackInfo ci) {
        if(Instance.cameraOn) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFluid", at = @At("HEAD"), cancellable = true)
    private void cancelDefaultRender(BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, FluidState state, CallbackInfoReturnable<Boolean> cir) {
        if(Instance.cameraOn) {
            cir.cancel();
        }
    }
}
