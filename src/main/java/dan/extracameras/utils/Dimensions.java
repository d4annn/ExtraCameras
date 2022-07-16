package dan.extracameras.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum Dimensions {

    OVERWORLD(new TranslatableText("Overworld")),
    NETHER(new TranslatableText("Nether")),
    END(new TranslatableText("End"));

    private final Text name;

    private Dimensions(Text name) {
        this.name = name;
    }

    public Text getName() {
        return this.name;
    }

    public static Dimensions getCurrent() {
        String dimension = MinecraftClient.getInstance().world.getDimension().getEffects().getPath();
        if (dimension.equals("overworld")) {
            return OVERWORLD;
        } else if (dimension.equals("the_nether")) {
            return NETHER;
        } else if (dimension.equals("the_end")) {
            return END;
        }
        return null;
    }

    public static Dimensions getByName(String name) {
        if(name.equals("Overworld")) {
            return OVERWORLD;
        } else if(name.equals("Nether")) {
            return NETHER;
        } else if(name.equals("End")) {
            return END;
        }
        return null;
    }
}
