package dan.extracameras.gui.widgets.map;

import net.minecraft.block.Block;

public class Position {

    private int x;
    private int y;
    private int z;
    private String block;

    public Position(int x, int y, int z, String block) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = block;
    }

    public String getBlock() {
        return this.block;
    }

    public int getZ() {
        return z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
