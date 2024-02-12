
public class Parking {

    private final String name;
    private final Coordinates coordinates;
    private final int nrParkingSpaces;

    public Parking (String name, Coordinates coordinates,int nrParkingSpaces) {
        this.name = name;
        this.coordinates = coordinates;
        this.nrParkingSpaces = nrParkingSpaces;
    }

    public int getNrParkingSpaces() {
        return nrParkingSpaces;
    }
    public String getName() {
        return name;
    }

    public int getX() {
        return this.coordinates.getX();
    }

    public int getY() {
        return this.coordinates.getY();
    }

    public int getWidth() {
        return this.coordinates.getWidth();
    }

    public int getHeight() {
        return this.coordinates.getHeight();
    }

    public int getMiddleX() {
        return this.coordinates.getMiddleX();
    }

    public int getMiddleY() {
        return this.coordinates.getMiddleY();
    }
}
