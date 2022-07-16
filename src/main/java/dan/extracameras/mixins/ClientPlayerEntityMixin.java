package dan.extracameras.mixins;

import dan.extracameras.camera.CameraEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dan.extracameras.utils.Variables;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "wouldCollideAt", at = @At("HEAD"), cancellable = true)
    private void stopColliding(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (Variables.cameraOn) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isSubmergedInWater", at = @At("HEAD"), cancellable = true)
    private void removeWater(CallbackInfoReturnable<Boolean> cir) {
        if (Variables.cameraOn) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isCamera", at = @At("HEAD"), cancellable = true)
    private void allowPlayerMovementInFreeCameraMode(CallbackInfoReturnable<Boolean> cir) {
        if (Variables.cameraOn && CameraEntity.originalCameraWasPlayer()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "swingHand", at = @At("HEAD"), cancellable = true)
    private void preventHandSwing(Hand hand, CallbackInfo ci) {
        if (Variables.cameraOn) {
            ci.cancel();
        }
    }
}
