import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public class Zone <T> {
    private final String name;
    private final Coordinates coordinates;

    private final ArrayList<T> objectList;

    public Zone(String name, Coordinates coordinates) {
        this.name = name;
        this.coordinates = coordinates;
        this.objectList = new ArrayList<>();
    }

    public ArrayList<T> getObjectList() {
        try {
            return this.objectList;
        }
        catch (NullPointerException e) {
            System.out.println("The array of Objects is null");
        }
        return null;
    }

    public static void paintZones(Graphics g, Graphics2D graphics2D, int BASE_SIZE) {

        try {
            for (Zone<Parking> zone : City.parkingZones) {
                g.setColor(Color.BLACK);
                int positionX = zone.getX() * BASE_SIZE;
                int positionY = zone.getY() * BASE_SIZE;
                int width = zone.getWidth() * BASE_SIZE;
                int height = zone.getHeight() * BASE_SIZE;
                g.drawRect(positionX, positionY, width, height);

                graphics2D.setColor(Color.red);
                City.drawCenteredString(graphics2D, zone.getName(), new Rectangle(positionX, positionY, width, height));

                g.setColor(Color.BLACK);
                for (Parking parking : zone.getObjectList()) {
                    g.setColor(Color.BLACK);
                    positionX = parking.getX() * BASE_SIZE;
                    positionY = parking.getY() * BASE_SIZE;
                    width = parking.getWidth() * BASE_SIZE;
                    height = parking.getHeight() * BASE_SIZE;
                    g.drawRect(positionX, positionY, width, height);

                    graphics2D.setColor(Color.red);
                    City.drawCenteredString(graphics2D, parking.getName(), new Rectangle(positionX, positionY, width, height));
                }
            }

            for (Zone<Commercial> zone : City.commercialZones) {
                g.setColor(Color.BLACK);
                int positionX = zone.getX() * BASE_SIZE;
                int positionY = zone.getY() * BASE_SIZE;
                int width = zone.getWidth() * BASE_SIZE;
                int height = zone.getHeight() * BASE_SIZE;
                g.drawRect(positionX, positionY, width, height);

                graphics2D.setColor(Color.red);
                City.drawCenteredString(graphics2D, zone.getName(), new Rectangle(positionX, positionY, width, height));

                for (Commercial comercial : zone.getObjectList()) {
                    g.setColor(Color.BLACK);
                    positionX = comercial.getX() * BASE_SIZE;
                    positionY = comercial.getY() * BASE_SIZE;
                    width = comercial.getWidth() * BASE_SIZE;
                    height = comercial.getHeight() * BASE_SIZE;
                    g.drawRect(positionX, positionY, width, height);

                    graphics2D.setColor(Color.red);
                    City.drawCenteredString(graphics2D, comercial.getName(), new Rectangle(positionX, positionY, width, height));
                }
            }
        }catch (NullPointerException e) {
            System.out.println("Null pointer exception");
        }
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

    public int getNrFreeParkingSpaces(String parkName) {
        int count = 0;
        for (Map.Entry<String, Vector<LocalTime>> entry : City.parkingSituation.entrySet()) {
            if (entry.getKey().equals(parkName) || entry.getKey().equals(parkName + System.lineSeparator())) {
                for (LocalTime localTime : entry.getValue()) {
                    if (localTime.equals(LocalTime.MIN)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

}
