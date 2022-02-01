import java.awt.*;

public class Wall {

    private int [] rows;
    private int [] cols;
    private int r,g,b;
    private String type;
    private int size;

    public Wall(int [] rows, int [] cols, int r, int g, int b, String type, int size){
        this.rows = rows;
        this.cols = cols;
        this.r = r;
        this.g = g;
        this.b = b;
        this.type = type;
        this.size = size;
    }
    public Polygon getPoly(){
         return new Polygon(rows,cols,4);
    }
    public GradientPaint getPaint(){
        switch(type){
            case "Right":
                return new GradientPaint(rows[0],cols[0],new Color(r,g,b),rows[1],cols[0],new Color(r-50,g-50,b-50));
            case "Left":
                return new GradientPaint(rows[0],cols[0],new Color(r,g,b),rows[1],cols[0],new Color(r-50,g-50,b-50));
            case "Floor":
                return new GradientPaint(rows[0],cols[0],new Color(r,g,b),rows[0],cols[1],new Color(r-50,g-50,b-50));
            case "dropFloor":
                return new GradientPaint(rows[0],cols[0],new Color(r,g,b),rows[0],cols[1],new Color(r-5,g-5,b-5));
            case "Ceil":
                return new GradientPaint(rows[0],cols[0],new Color(r,g,b),rows[0],cols[1],new Color(r-50,g-50,b-50));
            case "CeilRight":
                return new GradientPaint(rows[0],cols[0],new Color(r,g,b),rows[0],cols[1],new Color(r-50,g-50,b-50));
            case "FloorRight":
                return new GradientPaint(rows[0],cols[0],new Color(r,g,b),rows[0],cols[1],new Color(r-50,g-50,b-50));
            case "CeilLeft":
                return new GradientPaint(rows[0],cols[0],new Color(r,g,b),rows[0],cols[1],new Color(r-50,g-50,b-50));
            case "FloorLeft":
                return new GradientPaint(rows[0],cols[0],new Color(r,g,b),rows[0],cols[1],new Color(r-50,g-50,b-50));
            default:
                return new GradientPaint(rows[0],cols[0],new Color(r,g,b),rows[1],cols[0],new Color(120,120,120));
        }
    }

}
