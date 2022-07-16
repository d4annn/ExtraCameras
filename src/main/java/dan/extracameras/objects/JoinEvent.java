package dan.extracameras.objects;

public class JoinEvent {

    private String world;
    private boolean server;

    public JoinEvent(String world, boolean server) {
        this.world = world;
        this.server = server;
    }

    public String getWorld() {
        return this.world;
    }

    public boolean isServer() {
        return this.server;
    }
}
