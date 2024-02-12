import java.awt.*;
import java.time.LocalTime;
import java.util.Map;
import java.util.Vector;

import static java.lang.Math.sqrt;

public class Car {
    private final String name;
    private final Coordinates coordinates;

    public Car(String name, Coordinates coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }

    public int getX() {
        return this.coordinates.getX();
    }

    public void setX(int positionX) {
        this.coordinates.setX(positionX);
    }

    public void setY(int positionY) {
        this.coordinates.setY(positionY);
    }

    public int getS() {
        return 3;
    }
    public int getV() {
        return 3;
    }

    public int getY() {
        return this.coordinates.getY();
    }

    public void paintCar(Graphics g, Graphics2D graphics2D, int BASE_SIZE) {
        try {
            g.setColor(Color.BLACK);
            int positionX = this.getX() * BASE_SIZE;
            int positionY = this.getY() * BASE_SIZE;
            int width = this.getS() * BASE_SIZE;
            int height = this.getV() * BASE_SIZE;
            g.drawRect(positionX, positionY, width, height);
            City.drawCenteredString(graphics2D, this.name, new Rectangle(positionX, positionY, width, height));
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception");
        }
    }

    private double distance(int x1, int y1, int x2, int y2) {
        return sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
    }

    public Parking closestParkingSpace() {
        try {
            int x = this.getX();
            int y = this.getY();
            double minDistance = Integer.MAX_VALUE;
            Parking closestParking = null;
            for (Zone<Parking> zone : City.parkingZones) {
                if (minDistance == 0)
                    break;
                for (Parking park : zone.getObjectList()) {
                    for (Map.Entry<String, Vector<LocalTime>> entry : City.parkingSituation.entrySet()) {
                        LocalTime nullLocalTime = LocalTime.MIN;
                        if ((entry.getKey().equals(park.getName()) || entry.getKey().equals(park.getName() + System.lineSeparator())) && entry.getValue().contains(nullLocalTime)) {
                            if (minDistance == 0)
                                break;
                            if (x >= park.getX() && y >= park.getY() && x < park.getX() + park.getWidth() && y < park.getY() + park.getHeight()) {
                                closestParking = park;
                                minDistance = 0;
                            } else {
                                double distance = distance(x, y, park.getMiddleX(), park.getMiddleY());
                                if (distance < minDistance) {
                                    minDistance = distance;
                                    closestParking = park;
                                }
                            }
                        }
                    }
                }
            }
            return closestParking;
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception");
        }
        return null;
    }

    public String mostFreePark() {
        try {
            int max = 0;
            String name = null;
            for (Map.Entry<String, Vector<LocalTime>> entry : City.parkingSituation.entrySet()) {
                int count = 0;
                for (LocalTime localTime : entry.getValue()) {
                    if (localTime == LocalTime.MIN)
                        count++;
                }
                if (max < count) {
                    name = entry.getKey();
                    max = count;
                }
            }
            return name;
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception");
        }
        return null;
    }


    public Parking nextFreeParkingSpace(LocalTime time) {
        try {
            String parkName = null;
            for (Map.Entry<String, Vector<LocalTime>> entry : City.parkingSituation.entrySet()) {
                if (parkName != null)
                    break;
                for (LocalTime localTime : entry.getValue()) {
                    if (localTime.equals(time)) {
                        parkName = entry.getKey();
                        break;
                    }
                }
            }

            for (Zone<Parking> zone : City.parkingZones) {
                for (Parking park : zone.getObjectList()) {
                    if (park.getName().equals(parkName) || park.getName().equals(parkName + System.lineSeparator()))
                        return park;
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception");
        }
        return null;
    }


    public LocalTime nextFreeParkingSpaceTime() {
        LocalTime minLocalTime = LocalTime.MAX;
        for (Map.Entry<String, Vector<LocalTime>> entry : City.parkingSituation.entrySet()) {
            for (LocalTime localTime : entry.getValue()) {
                if (localTime.getHour() <= minLocalTime.getHour() && localTime.getMinute() < minLocalTime.getMinute()) {
                    minLocalTime = localTime;
                }
            }
        }
        return minLocalTime;
    }

    public void paintDistance(Graphics2D graphics2D, int BASE_SIZE) {
        try {
            Parking closestParking = closestParkingSpace();
            graphics2D.setColor(Color.green);
            int positionX = this.getX() * BASE_SIZE;
            int positionY = this.getY() * BASE_SIZE;
            int positionX2 = closestParking.getMiddleX() * BASE_SIZE;
            int positionY2 = closestParking.getMiddleY() * BASE_SIZE;
            graphics2D.drawLine(positionX, positionY, positionX2, positionY2);
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception");
        }
    }

    public void paintMostFreeDistance(Graphics2D graphics2D, int BASE_SIZE) {
        try {
            for (Zone<Parking> zone : City.parkingZones) {
                for (Parking park : zone.getObjectList()) {
                    if (park.getName().equals(mostFreePark())) {
                        graphics2D.setColor(Color.orange);
                        int positionX = this.getX() * BASE_SIZE;
                        int positionY = this.getY() * BASE_SIZE;
                        int positionX2 = park.getMiddleX() * BASE_SIZE;
                        int positionY2 = park.getMiddleY() * BASE_SIZE;
                        graphics2D.drawLine(positionX, positionY, positionX2, positionY2);
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception");
        }
    }

    public String closestPharmacy() {
        try {
            double minDistance = Integer.MAX_VALUE;
            String closestPharmacy = null;
            for (Zone<Commercial> zone : City.commercialZones) {
                if (minDistance == 0)
                    break;
                for (Commercial comercial : zone.getObjectList()) {
                    if (comercial.getType().equals("farmacie")) {
                        if (minDistance == 0)
                            break;
                        if (this.getX() >= comercial.getX() && this.getY() >= comercial.getY() && this.getX() < comercial.getX() + comercial.getWidth() && this.getY() < comercial.getY() + comercial.getHeight()) {
                            closestPharmacy = comercial.getName();
                            minDistance = 0;
                        } else {
                            double distance = distance(this.getX(), this.getY(), comercial.getMiddleX(), comercial.getMiddleY());
                            if (distance < minDistance) {
                                minDistance = distance;
                                closestPharmacy = comercial.getName();
                            }
                        }
                    }
                }
            }
            return closestPharmacy;
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception");
        }
        return null;
    }

    public void paintPharmacyDistance(Graphics2D graphics2D, int BASE_SIZE) {
        try {
            String closestPharmacy = closestPharmacy();
            for (Zone<Commercial> zone : City.commercialZones) {
                for (Commercial comercial : zone.getObjectList()) {
                    if (comercial.getName().equals(closestPharmacy)) {
                        graphics2D.setColor(Color.blue);
                        int positionX = this.getX() * BASE_SIZE;
                        int positionY = this.getY() * BASE_SIZE;
                        int positionX2 = comercial.getMiddleX() * BASE_SIZE;
                        int positionY2 = comercial.getMiddleY() * BASE_SIZE;
                        graphics2D.drawLine(positionX, positionY, positionX2, positionY2);
                    }
                }
            }
        } catch ( NullPointerException e) {
            System.out.println("Null pointer exception");
        }
    }

    public void paintDistanceNextFreeSpace(Graphics2D graphics2D, int BASE_SIZE, Parking parking) {
        try {
            graphics2D.setColor(Color.green);
            int positionX = this.getX() * BASE_SIZE;
            int positionY = this.getY() * BASE_SIZE;
            int positionX2 = parking.getMiddleX() * BASE_SIZE;
            int positionY2 = parking.getMiddleY() * BASE_SIZE;
            graphics2D.drawLine(positionX, positionY, positionX2, positionY2);
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception");
        }
    }
}
