package dan.extracameras.mixins;

import dan.extracameras.utils.CameraUtils;
import dan.extracameras.utils.Variables;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {

    @Inject(method = "isSpectator", at = @At("HEAD"), cancellable = true)
    private void overrideIsSpectator(CallbackInfoReturnable<Boolean> cir) {
        if (Variables.cameraOn && CameraUtils.getFreeCameraSpectator()) {
            cir.setReturnValue(true);
        }
    }
}
