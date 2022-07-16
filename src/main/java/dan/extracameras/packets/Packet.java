package dan.extracameras.packets;

public abstract class Packet {

    private String name;

    public Packet(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract void execute();

}
