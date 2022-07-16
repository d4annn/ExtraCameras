package dan.extracameras.gui.world;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Property;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

import javax.imageio.ImageIO;
import java.nio.charset.MalformedInputException;

public class ImagineWorldScreen extends Screen {

    public ImagineWorldScreen() {
        super(new TranslatableText("pene"));
    }

    @Override
    protected void init() {
        super.init();

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

    }
}
