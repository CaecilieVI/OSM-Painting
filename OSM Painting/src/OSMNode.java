/**
 * Created by caecilieiversen on 22/02/2017.
 */

public class OSMNode {
    float lon, lat;

    public float getLon() {
        return lon;
    }

    public float getLat() {
        return lat;
    }

    public OSMNode(float lon, float lat) {
        this.lon = lon;
        this.lat = lat;
    }
}
