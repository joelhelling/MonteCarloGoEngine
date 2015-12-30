package Model;

/**
 * Created by Joel on 5/3/2015.
 */
public class Point {
    private int x;
    private int y;

    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    @Override
    public boolean equals(Object other)
    {
        if(other instanceof Point) {
            Point p = (Point) other;
            return this.x == p.getX() && this.y == p.getY();
        }
        else
            return false;
    }

    public String toString()
    {
        return "(" + this.x + ", " + this.y + ")";
    }
}
