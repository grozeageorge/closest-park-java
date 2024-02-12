import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class City extends JPanel implements MouseListener, MouseMotionListener {
    private final Font font;
    private final int BASE_SIZE;
    private int fieldX = -10,fieldY = -10;
    private final Car car = new Car("CAR", new Coordinates(fieldY,553,603,fieldX));

    public static ArrayList<Zone<Parking>> parkingZones = new ArrayList<>();
    public static ArrayList<Zone<Commercial>> commercialZones = new ArrayList<>();
    public static HashMap<String, Vector<LocalTime>> parkingSituation = new HashMap<>();

    public static void drawCenteredString(Graphics2D g, String text, Rectangle rect) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics();
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + metrics.getAscent();
        // Draw the String
        g.drawString(text, x, y);
    }


    public City(int x, int y, int BASE_SIZE) {
        this.BASE_SIZE = BASE_SIZE;
        Dimension dimension = new Dimension(
                x * BASE_SIZE,
                y * BASE_SIZE
        );
        this.font = new Font(Font.DIALOG,Font.PLAIN,12);
        this.setPreferredSize(dimension);
        this.addMouseListener(this);
        readFiles();
    }

    public void readFiles() {
        final Object lock = new Object();
        AtomicBoolean isFirstThreadDone = new AtomicBoolean(false);
        AtomicBoolean isSecondThreadDone = new AtomicBoolean(false);

        Thread thread1 = new Thread(() -> {
            synchronized (lock) {
                try {
                    File file = new File("Fisiere Intrare/parcari.txt");
                    Scanner sc = new Scanner(file);
                    readParkings(sc);
                    isFirstThreadDone.set(true);
                    lock.notify();
                } catch (FileNotFoundException e) {
                    System.out.println("File not found");
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (lock) {
                while (!isFirstThreadDone.get()) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    File file = new File("Fisiere Intrare/comercial.txt");
                    Scanner sc = new Scanner(file);
                    readComercials(sc);
                    isSecondThreadDone.set(true);
                    lock.notify();
                } catch (FileNotFoundException e) {
                    System.out.println("File not found");
                }
            }
        });


        Thread thread3 = new Thread(() -> {
            synchronized (lock) {
                while (!isSecondThreadDone.get()) {
                    try {
                        lock.wait();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                try {
                    File file = new File("Fisiere Intrare/situatieParcari1_ora10.00.txt");
                    Scanner sc = new Scanner(file);
                    readParkingsSituation(sc);
                    lock.notify();
                } catch (FileNotFoundException e) {
                    System.out.println("File not found");
                }
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();
        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void readParkingsSituation(Scanner scanner) {
        String name;
        scanner.useDelimiter(" |, |" + System.lineSeparator());
        while(scanner.hasNext()) {
            name = scanner.next();
            Vector<LocalTime> FreeParkTimes = new Vector<>();
            while (!(scanner.hasNext("P.*")) && scanner.hasNext()) {
                for (Zone<Parking> zone : parkingZones) {
                    for (Parking park : zone.getObjectList()) {
                        if (name.equals(park.getName())) {
                            for (int i = 0; i < park.getNrParkingSpaces(); i++) {
                                if (scanner.hasNext("09|08|07|06|05|04|03|02|01"))
                                    scanner.next();
                                String time;
                                if (scanner.hasNext("P.*") || !(scanner.hasNext())) {
                                    time = "00:00";
                                }
                                else {
                                    time = scanner.next();
                                }
                                LocalTime localTime = LocalTime.parse(time);
                                FreeParkTimes.add(localTime);
                            }
                            while (!(scanner.hasNext("P.*")) && scanner.hasNext()) {
                                scanner.next();
                            }
                        }
                    }
                }
            }
            parkingSituation.put(name,FreeParkTimes);
        }
    }
    public void readComercials(Scanner scanner) {
        while(scanner.hasNext()) {
            String name;
            int N,S,V,E;
            scanner.useDelimiter(": N-|,S-|,V-|,E-|: |" + System.lineSeparator());
            name = scanner.next();
            N = Integer.parseInt(scanner.next());
            S = Integer.parseInt(scanner.next());
            V = Integer.parseInt(scanner.next());
            E = Integer.parseInt(scanner.next());
            Coordinates coordinates = new Coordinates(N,S,V,E);
            Zone<Commercial> cz = new Zone<>(name,coordinates);
            while(!(scanner.hasNext("Zona.*")) && scanner.hasNext()) {
                name = scanner.next();
                N = Integer.parseInt(scanner.next());
                S = Integer.parseInt(scanner.next());
                V = Integer.parseInt(scanner.next());
                E = Integer.parseInt(scanner.next());
                String type = scanner.next();
                Commercial c = new Commercial(name,new Coordinates(N,S,V,E),type);
                cz.getObjectList().add(c);
            }
            commercialZones.add(cz);
        }
    }

    public void readParkings(Scanner scanner) {
        while (scanner.hasNext()) {
            String name;
            int N, S, V, E;
            scanner.useDelimiter(": N-|,S-|,V-|,E-|: {2}N-|: |" + System.lineSeparator() + "|LOC" + System.lineSeparator() + "|LOC");
            name = scanner.next();
            N = Integer.parseInt(scanner.next());
            S = Integer.parseInt(scanner.next());
            V = Integer.parseInt(scanner.next());
            E = Integer.parseInt(scanner.next());
            Coordinates coordinates = new Coordinates(N, S, V, E);
            Zone<Parking> pz = new Zone<>(name, coordinates);
            while (!(scanner.hasNext("Zona.*")) && scanner.hasNext()) {
                name = scanner.next();
                N = Integer.parseInt(scanner.next());
                S = Integer.parseInt(scanner.next());
                V = Integer.parseInt(scanner.next());
                E = Integer.parseInt(scanner.next());
                int nrParkingSpaces = Integer.parseInt(scanner.next());
                Parking p = new Parking(name, new Coordinates(N, S, V, E), nrParkingSpaces);
                pz.getObjectList().add(p);
            }
            parkingZones.add(pz);

        }
    }

    public boolean isAnyParkingSpaceEmpty() {
        for (Map.Entry<String, Vector<LocalTime>> entry : parkingSituation.entrySet()) {
            for (LocalTime localTime : entry.getValue()) {
                if (localTime == LocalTime.MIN)
                    return true;
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.setFont(font);

        Zone.paintZones(g, graphics2D, BASE_SIZE);

        car.paintCar(g, graphics2D, BASE_SIZE);
        if (fieldX != -10) {
            if (isAnyParkingSpaceEmpty()) {
                car.paintDistance(graphics2D, BASE_SIZE);
                car.paintMostFreeDistance(graphics2D, BASE_SIZE);
            }
            else {
                LocalTime time = car.nextFreeParkingSpaceTime();
                Parking freeSpace = car.nextFreeParkingSpace(time);
                car.paintDistanceNextFreeSpace(graphics2D,BASE_SIZE,freeSpace);
            }
            car.paintPharmacyDistance(graphics2D, BASE_SIZE);
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.fieldX = e.getX() / BASE_SIZE;
        this.fieldY = e.getY() / BASE_SIZE;
    }

    public void messageParkingsEmpty(StringBuilder builder) {
        try {
            LocalTime time = car.nextFreeParkingSpaceTime();
            builder.append(System.lineSeparator()).append("Ora eliberarii: ").append(time.toString());
            Parking freePark = car.nextFreeParkingSpace(time);
            for (Zone<Parking> zone : parkingZones) {
                for (Parking park : zone.getObjectList()) {
                    if (park.getName().equals(freePark.getName()))
                        builder.append(", Zona de parcare: ").append(zone.getName())
                                .append(", Numele parcarii: ").append(park.getName())
                                .append(", Coordonatele Geografice: Nord:").append(park.getY() + 550)
                                .append(", Sud:").append(park.getY() + 550 + park.getHeight())
                                .append(", Vest:").append(park.getX() + 600 + park.getWidth())
                                .append(", Est:").append(park.getX() + 600);
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Parking's aren't full");
        }
    }

    public void messageClosestParking(StringBuilder builder) {
        try {
            Parking freePark = car.closestParkingSpace();
            for (Zone<Parking> zone : parkingZones) {
                for (Parking park : zone.getObjectList()) {
                    if (park.getName().equals(freePark.getName())) {
                        int nrOfFreeParkingSpaces = zone.getNrFreeParkingSpaces(park.getName());
                        builder.append("Zona de parcare: ").append(zone.getName())
                                .append(", Numele parcarii: ").append(park.getName())
                                .append(", Numar de locuri libere: ").append(nrOfFreeParkingSpaces)
                                .append(", Coordonatele Geografice: Nord:").append(park.getY() + 550)
                                .append(", Sud:").append(park.getY() + 550 + park.getHeight())
                                .append(", Vest:").append(park.getX() + 600 + park.getWidth())
                                .append(", Est:").append(park.getX() + 600);
                    }
                }
            }
        }
        catch (NullPointerException e) {
            System.out.println("No free park");
        }
    }

    public void messageMostParkingSpaces(StringBuilder builder) {
        try {
            String mostFreePark = car.mostFreePark();
            for (Zone<Parking> zone : parkingZones) {
                for (Parking park : zone.getObjectList()) {
                    if (park.getName().equals(mostFreePark)) {
                        int nrOfFreeParkingSpaces = zone.getNrFreeParkingSpaces(park.getName());
                        builder.append("Zona de parcare: ").append(zone.getName())
                                .append(", Numele parcarii: ").append(park.getName())
                                .append(", Numar de locuri libere: ").append(nrOfFreeParkingSpaces)
                                .append(", Coordonatele Geografice: Nord:").append(park.getY() + 550)
                                .append(", Sud:").append(park.getY() + 550 + park.getHeight())
                                .append(", Vest:").append(park.getX() + 600 + park.getWidth())
                                .append(", Est:").append(park.getX() + 600);
                    }
                }
            }
        }
        catch (NullPointerException e) {
            System.out.println("No free park");
        }
    }

    public void messageClosestPharmacy(StringBuilder builder) {
        try {
            String closestPharmacy = car.closestPharmacy();
            for (Zone<Commercial> zone : commercialZones) {
                for (Commercial comercial : zone.getObjectList()) {
                    if (comercial.getName().equals(closestPharmacy)) {
                        builder.append("Zona comerciala: ").append(zone.getName())
                                .append(", Numele magazinului: ").append(comercial.getName())
                                .append(", Tipul magazinului: ").append(comercial.getType())
                                .append(", Coordonatele geografice: Nord:").append(comercial.getY() + 550)
                                .append(", Sud:").append(comercial.getY() + 550 + comercial.getHeight())
                                .append(", Vest:").append(comercial.getX() + 600 + comercial.getWidth())
                                .append(", Est:").append(comercial.getX() + 600);
                    }
                }
            }
        }
        catch (NullPointerException e) {
            System.out.println("No pharmacies");
        }
    }

    public void getMessageToShow(StringBuilder builder) {

        final Object lock = new Object();
        AtomicBoolean isThreadDone = new AtomicBoolean(false);

        Thread thread1 = new Thread(() -> {
            synchronized (lock) {
                builder.append("Closest free parking:").append(System.lineSeparator());
                messageClosestParking(builder);
                builder.append(System.lineSeparator()).append("Closest parking with the most free parking spaces:").append(System.lineSeparator());
                messageMostParkingSpaces(builder);
                isThreadDone.set(true);
                lock.notify();
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (lock) {
                builder.append("All parking spaces are occupied");
                messageParkingsEmpty(builder);
                isThreadDone.set(true);
                lock.notify();
            }
        });

        Thread thread3 = new Thread(() -> {
            synchronized (lock) {
                while (!isThreadDone.get()) {
                    try {
                        lock.wait();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                builder.append(System.lineSeparator()).append("Closest pharmacy:").append(System.lineSeparator());
                messageClosestPharmacy(builder);
            }
        });

        if (isAnyParkingSpaceEmpty()) {
            thread1.start();
        } else {
            thread2.start();
        }
        thread3.start();
        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.car.setX(fieldX);
        this.car.setY(fieldY);

        if (e.getButton() == 1) {
            repaint();
            StringBuilder builder = new StringBuilder();
            getMessageToShow(builder);

            Frame frame = Frame.getFrames()[0];
            JOptionPane.showMessageDialog(frame, builder.toString());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
