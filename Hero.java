import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class Hero {

    private Location loc;
    private int size;
    private Color color;
    private int dir;
    private int moves;
    private ArrayList<Integer>rCrumbs = new ArrayList<>();
    private ArrayList<Integer>cCrumbs = new ArrayList<>();


    public Hero(Location loc,int dir, int size, Color color, int moves){
        this.loc = loc;
        this.dir = dir;
        this.size = size;
        this.color = color;
        this.moves = moves;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc){
        this.loc = loc;
    }


    public Color getColor() {
        return color;
    }

    public int getDir() {
        return dir;
    }

    public void move (int key, char[][]maze){
        int r = getLoc().getR();
        int c = getLoc().getC();
        if (key==38){

            if (dir == 0){
                if (r>0 && maze[r-1][c]==' ') {
                    getLoc().setR(-1);
                    moves++;
                }
            }
            if (dir == 1){
                if (c<maze[0].length-1 && maze[r][c+1]==' ') {
                    getLoc().setC(+1);
                    moves++;
                }
            }
            if (dir == 2){
                if (r<maze.length-1 && maze[r+1][c]==' ') {
                    getLoc().setR(+1);
                    moves++;
                }
            }
            if (dir == 3){
                if (c>0 && maze[r][c-1]==' ') {
                    getLoc().setC(-1);
                    moves++;
                }
            }
        }
        if (key==37){
            dir--;
            if (dir<0)
                dir=3;
        }
        if(key==39){
            dir++;
            if (dir>3)
                dir=0;
        }
    }

    public void setMoves(int m){
        moves = m;
    }
    public int getMoves(){
        return moves;
    }

    public Rectangle getRect(){

        int r = getLoc().getR();
        int c = getLoc().getC();
        return new Rectangle(c*10+10,r*10+10,10,10);
    }

    public void setRCrumbs(ArrayList<Integer> rCrumbs){
        this.rCrumbs = rCrumbs;
    }
    public ArrayList<Integer> getRCrumbs(){
        return rCrumbs;
    }
    public void setCCrumbs(ArrayList<Integer> cCrumbs){
        this.cCrumbs = cCrumbs;
    }
    public ArrayList<Integer> getCCrumbs(){
        return cCrumbs;
    }


}
