
public class Coordinates {
    private int N,E;
    private final int V,S;

    public Coordinates(int N, int S, int V,int E) {
        this.N = N - 550;
        this.S = S - 550;
        this.V = V - 600;
        this.E = E - 600;
    }

    public int getX() {
        return E;
    }

    public void setX(int positionX) {
        this.E = positionX;
    }

    public int getY() {
        return N;
    }

    public void setY(int positionY) {
        this.N = positionY;
    }

    public int getWidth() {
            return V - E;
    }

    public int getHeight() {
        return S - N;
    }

    public int getMiddleX() {
        return (V+E)/2;
    }

    public int getMiddleY() {
        return (N+S) / 2;
    }
}
