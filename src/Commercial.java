public class Commercial {

    private final String name;
    private final Coordinates coordinates;
    private final String type;

    public Commercial(String name, Coordinates coordinates, String type) {
        this.name = name;
        this.coordinates = coordinates;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public int getMiddleX() {
        return this.coordinates.getMiddleX();
    }

    public int getMiddleY() {
        return this.coordinates.getMiddleY();
    }
}
