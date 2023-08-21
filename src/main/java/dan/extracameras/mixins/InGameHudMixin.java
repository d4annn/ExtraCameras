package dan.extracameras.mixins;

import dan.extracameras.KeyBinds;
import dan.extracameras.utils.RenderUtils;
import dan.extracameras.utils.Instance;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow public abstract TextRenderer getTextRenderer();

    @Shadow private int scaledHeight;

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void renderHotbar(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        if(Instance.cameraOn) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if(Instance.cameraOn) {
            getTextRenderer().draw(matrices, Text.of("Press §l" + KeyBinds.SCREENSHOT.getBoundKeyLocalizedText().getString() + " §rto take camera profile image"), 5, this.scaledHeight - 22 - getTextRenderer().fontHeight, -1);
            getTextRenderer().draw(matrices, Text.of("Press §l" + KeyBinds.EXIT_CAMERA.getBoundKeyLocalizedText().getString() + " §rto exit the camera"), 5, this.scaledHeight - 20, -1);
        }
    }
}
