package definition;

/**
 *
 * @author TrongDai
 */
public class Machine {
    private int core;
    private int ram;
    private int disk;
    
    public void setCore(int core) {
        this.core = core;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public void setDisk(int disk) {
        this.disk = disk;
    }
    
    public int getCore() {
        return core;
    }

    public int getRam() {
        return ram;
    }

    public int getDisk() {
        return disk;
    }
    
    public Machine(int core, int ram, int disk) {
        this.core = core;
        this.ram = ram;
        this.disk = disk;
    }
}
