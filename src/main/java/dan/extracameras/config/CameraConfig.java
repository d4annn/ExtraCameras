package dan.extracameras.config;

import dan.extracameras.camera.Camera;

import java.util.List;

public class CameraConfig {

    private List<Camera> cameras;
    private String world;
    private boolean server;

    public CameraConfig(List<Camera> cameras, String world, boolean server) {
        this.cameras = cameras;
        this.world = world;
        this.server = server;
    }

    public List<Camera> getCameras() {
        return cameras;
    }

    public String getWorld() {
        return world;
    }

    public boolean isServer() {
        return server;
    }

    public void changeCamera(int index, Camera newCamera) {
        this.cameras.set(index, newCamera);
    }

    public void deleteCamera(int index) {
        this.cameras.remove(index);
    }

    public boolean addCamera(Camera camera) {
        this.cameras.add(camera);
        return true;
    }
}
