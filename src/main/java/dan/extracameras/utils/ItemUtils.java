package dan.extracameras.utils;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemUtils {

    public static ItemStack getItemStackFromItemCommandOutputName(String name) {
        String identifier = "minecraft:" + getBlockName(Registry.BLOCK.get(new Identifier("minecraft:" + name.replaceAll(" ", "_").toLowerCase()))).toLowerCase();

        return Registry.ITEM.get(new Identifier(identifier)).getDefaultStack();
    }

    public static int getMaxCount(String name) {
        return getItemStackFromItemCommandOutputName(name).getMaxCount();
    }

    public static String getBlockName(Block item) {
        String[] split = item.toString().toLowerCase().split(" ");
        return split[1];
    }
}
