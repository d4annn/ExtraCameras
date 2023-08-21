package dan.extracameras.objects;

public class GuiElement {

    private double x;
    private double y;
    private boolean hold;
    private boolean placed;
    private GuiElement def;

    public GuiElement(double x, double y, boolean hold, boolean placed, boolean copy) {
        this.x = x;
        this.y = y;
        this.hold = hold;
        this.placed = placed;
        if (copy)
            def = new GuiElement(x, y, hold, placed, false);
    }

    public void setDef() {
        this.x = def.getX();
        this.y = def.getY();
        this.hold = def.isHold();
        this.placed = def.isPlaced();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isHold() {
        return hold;
    }

    public void setHold(boolean hold) {
        this.hold = hold;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }
}
