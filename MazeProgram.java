import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class MazeProgram extends JPanel implements KeyListener {

    JFrame frame;
    BufferedReader input;
    Font font = new Font("Arial",Font.PLAIN,70);
    Font font2 = new Font("Arial",Font.PLAIN,20);
    Font font3 = new Font("Arial",Font.BOLD,35);
    char [][] maze = new char[50][118];
    int r = 1,c = 3;
    Hero hero;
    int dir = 1;
    int size = 15;
    boolean draw3D = false;
    boolean isStart = true;
    boolean startTimer = true;
    boolean isDone = false;
    boolean restart = false;
    boolean first_maze = true;
    ArrayList<Wall> walls;
    int shrink = 50;
    File endSuccess = new File("/Users/Richard/IdeaProjects/MazeProject/src/endsuccess.wav");
    File startSound = new File("/Users/Richard/IdeaProjects/MazeProject/src/meteriodstart.wav");
    File dropSound = new File("/Users/Richard/IdeaProjects/MazeProject/src/start.wav");
    File maze1 = new File("/Users/Richard/IdeaProjects/MazeProject/src/maze.txt");
    File maze2 = new File("/Users/Richard/IdeaProjects/MazeProject/src/maze2.txt");
    Timer timer;
    int minute = 0;
    int second = 0;
    int breadcrumbs = 5;
    DecimalFormat decimalFormat = new DecimalFormat("00");
    String ddSecond = "00", ddMinute = "00";
    boolean isRunning = true;


    ArrayList<Integer> highscores = new ArrayList<>(5);
    ArrayList<Integer> rCrumbs = new ArrayList<>(5);
    ArrayList<Integer> cCrumbs = new ArrayList<>(5);

    public MazeProgram(){

        hero = new Hero(new Location(r,c),dir,size,Color.RED,0);

        frame = new JFrame("Maze Program");
        frame.add(this);
        frame.setSize(1200, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.addKeyListener(this);

        try{
            FileInputStream fis = new FileInputStream("highscores.txt");
            Scanner in = new Scanner(fis);
            while(in.hasNext()){
                highscores.add(Integer.parseInt(in.nextLine()));
            }
        }catch (Exception expe){

        }



        try {

            input = new BufferedReader(new FileReader(maze1));
            String text;
            int r = 0;
            while((text=input.readLine())!=null){
                for (int c = 0; c < text.length(); c++){
                    maze[r][c] = text.charAt(c);
                }
                r++;
            }

        }catch(IOException e){
            System.out.println(e.toString());
        }
        if(draw3D)
            createWalls();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.BLACK);
        g2.fillRect(0,0,1200,900);

        if(!isDone) {

            if (!draw3D) {

                g2.setColor(Color.GRAY);
                for (int c = 0; c < maze[0].length; c++) {
                    for (int r = 0; r < maze.length; r++) {
                        if (maze[r][c] == ' ')
                            g2.fillRect(c * 10 + 10, r * 10 + 10, 10, 10);
                        else
                            g2.drawRect(c * 10 + 10, r * 10 + 10, 10, 10);
                    }
                }



                g2.setColor(new Color(102,51,0));
                for (int i = 0; i < rCrumbs.size(); i++)
                    g2.fillRect(cCrumbs.get(i)*10+10,rCrumbs.get(i)*10+10,10,10);

                hero.setRCrumbs(rCrumbs);
                hero.setCCrumbs(cCrumbs);

                g2.setColor(hero.getColor());
                g2.fill(hero.getRect());

                g2.setFont(font);
                g2.setColor(Color.GRAY);

                g2.drawString(CountdownTimer(), 500, 700);
                g2.drawString("Moves: "+hero.getMoves(),450,600);

                g2.setFont(font2);
                if (breadcrumbs>-1)
                    g2.drawString("Press 'd' to drop breadcrumbs ("+breadcrumbs+")",50,650);
                else
                    g2.drawString("Press 'd' to drop breadcrumbs (0)",50,650);

                ImageIcon end = new ImageIcon("/Users/Richard/IdeaProjects/MazeProject/src/smiley.png");
                Image end_img = end.getImage();
                g2.drawImage(end_img,115*10+10,47*10+10,12,12,this);

                startTimer = false;


            }

            if (draw3D) {
                for (Wall wallGon : walls) {
                    g2.setPaint(wallGon.getPaint());
                    g2.fillPolygon(wallGon.getPoly());
                    g2.setColor(Color.BLACK);
                    g2.drawPolygon(wallGon.getPoly());

                    g2.setFont(font);
                    g2.setColor(Color.GRAY);
                    g2.drawString(CountdownTimer(), 875, 650);
                    g2.drawString("Moves: "+hero.getMoves(), 825, 550);

                    g2.setFont(font2);
                    if(breadcrumbs>-1)
                        g2.drawString("Press 'd' to drop breadcrumbs ("+breadcrumbs+")",825,300);
                    else
                        g2.drawString("Press 'd' to drop breadcrumbs (0)",825,300);

                    if(hero.getLoc().getR()==47 && hero.getLoc().getC()>=112 && hero.getDir()==1) {
                        ImageIcon i = new ImageIcon("/Users/Richard/IdeaProjects/MazeProject/src/smiley.png");
                        Image smiley = i.getImage();
                        g2.drawImage(smiley,350,350,100,100,this);
                    }

                }
            }
        }

        if(isDone){
            g2.setColor(Color.RED);
            ImageIcon i2 = new ImageIcon("/Users/Richard/IdeaProjects/MazeProject/src/trophy.png");
            Image trophy = i2.getImage();
            g2.drawImage(trophy,500,100,150,150,this);
            g2.setFont(font2);
            g2.drawString("Moves: "+hero.getMoves(),530,350);
            g2.drawString("Final Time: "+CountdownTimer(),495,400);
            g2.setColor(Color.GRAY);
            g2.drawString("Press 'm' for other maze",800,150);
            g2.drawString("Press SPACE to restart",150,150);
            g2.setFont(font3);
            g2.drawString("High Scores",470,500);
            g2.setFont(font2);
            g2.drawString("1. \t\t\t\t\t\t"+decimalFormat.format(highscores.get(0)/60)+":"+decimalFormat.format(highscores.get(0)%60),505,550);
            g2.drawString("2. \t\t\t\t\t\t"+decimalFormat.format(highscores.get(1)/60)+":"+decimalFormat.format(highscores.get(1)%60),505,600);
            g2.drawString("3. \t\t\t\t\t\t"+decimalFormat.format(highscores.get(2)/60)+":"+decimalFormat.format(highscores.get(2)%60),505,650);
            g2.drawString("4. \t\t\t\t\t\t"+decimalFormat.format(highscores.get(3)/60)+":"+decimalFormat.format(highscores.get(3)%60),505,700);
            g2.drawString("5. \t\t\t\t\t\t"+decimalFormat.format(highscores.get(4)/60)+":"+decimalFormat.format(highscores.get(4)%60),505,750);

        }
    }


    public void createWalls(){

        walls = new ArrayList<Wall>();

        int rr = hero.getLoc().getR();
        int cc = hero.getLoc().getC();
        int dir = hero.getDir();

        switch(dir){
            case 0:
                for (int n = 0; n < 5; n++){
                    try{
                        if(maze[rr-n][cc-1] == '#')
                            walls.add(getLeft(n));
                        else {
                            walls.add(getLeftPath(n + 1));
                            walls.add(getLeftFloor(n));
                            walls.add(getLeftCeil(n));
                        }
                        if(maze[rr-n][cc+1] == '#') {
                            walls.add(getRight(n));
                        }
                        else {
                            walls.add(getRightPath(n + 1));
                            walls.add(getRightFloor(n));
                            walls.add(getRightCeil(n));
                        }
                        walls.add(getFloor(n,""));
                        for (int i = 0; i < 5; i++) {
                            try {
                                if (rr-n == hero.getRCrumbs().get(i) && cc == hero.getCCrumbs().get(i)) {
                                    walls.remove(walls.size() - 1);
                                    walls.add(getFloor(n, "drop"));
                                }
                            }catch(Exception exception){

                            }
                        }
                        walls.add(getCeil(n));

                    }catch(ArrayIndexOutOfBoundsException e){

                    }
                }
                for (int i = 5; i > 0; i--){
                    try{
                        if(maze[rr-i][cc] == '#')
                            walls.add(getFaceWall(i));
                    }catch(ArrayIndexOutOfBoundsException e){

                    }
                }
                break;
                case 1:
                for (int n = 0; n < 5; n++){
                    try{
                        if(maze[rr-1][cc+n] == '#')
                            walls.add(getLeft(n));
                        else {
                            walls.add(getLeftPath(n + 1));
                            walls.add(getLeftFloor(n));
                            walls.add(getLeftCeil(n));
                        }
                        if(maze[rr+1][cc+n] == '#')
                            walls.add(getRight(n));
                        else{
                            walls.add(getRightPath(n+1));
                            walls.add(getRightFloor(n));
                            walls.add(getRightCeil(n));
                        }

                        walls.add(getFloor(n,""));
                        for (int i = 0; i < 5; i++) {
                            try {
                                if (rr == hero.getRCrumbs().get(i) && cc + n == hero.getCCrumbs().get(i)) {
                                    walls.remove(walls.size() - 1);
                                    walls.add(getFloor(n, "drop"));
                                }
                            }catch(Exception exception){

                            }
                        }
                        walls.add(getCeil(n));
                    }catch(ArrayIndexOutOfBoundsException e){

                    }
                }
                    for (int i = 5; i > 0; i--){
                        try{
                            if(maze[rr][cc+i] == '#')
                                walls.add(getFaceWall(i));
                        }catch(ArrayIndexOutOfBoundsException e){

                        }
                    }
                break;
                case 2:
                for (int n = 0; n < 5; n++){
                    try{
                        if(maze[rr+n][cc+1] == '#')
                            walls.add(getLeft(n));
                        else {
                            walls.add(getLeftPath(n + 1));
                            walls.add(getLeftFloor(n));
                            walls.add(getLeftCeil(n));
                        }
                        if(maze[rr+n][cc-1] == '#')
                            walls.add(getRight(n));
                        else{
                            walls.add(getRightPath(n+1));
                            walls.add(getRightFloor(n));
                            walls.add(getRightCeil(n));
                        }
                        walls.add(getFloor(n,""));
                        for (int i = 0; i < 5; i++) {
                            try {
                                if (rr+n == hero.getRCrumbs().get(i) && cc == hero.getCCrumbs().get(i)) {
                                    walls.remove(walls.size() - 1);
                                    walls.add(getFloor(n, "drop"));
                                }
                            }catch(Exception exception){

                            }
                        }
                        walls.add(getCeil(n));
                    }catch(ArrayIndexOutOfBoundsException e){

                    }
                }
                    for (int i = 5; i > 0; i--){
                        try{
                            if(maze[rr+i][cc] == '#')
                                walls.add(getFaceWall(i));
                        }catch(ArrayIndexOutOfBoundsException e){

                        }
                    }
                break;
                case 3:
                for (int n = 0; n < 5; n++){
                    try{
                        if(maze[rr+1][cc-n] == '#')
                            walls.add(getLeft(n));
                        else {
                            walls.add(getLeftPath(n + 1));
                            walls.add(getLeftFloor(n));
                            walls.add(getLeftCeil(n));
                        }
                        if(maze[rr-1][cc-n] == '#')
                            walls.add(getRight(n));
                        else {
                            walls.add(getRightPath(n + 1));
                            walls.add(getRightFloor(n));
                            walls.add(getRightCeil(n));
                        }
                        walls.add(getFloor(n,""));
                        for (int i = 0; i < 5; i++) {
                            try {
                                if (rr == hero.getRCrumbs().get(i) && cc - n == hero.getCCrumbs().get(i)) {
                                    walls.remove(walls.size() - 1);
                                    walls.add(getFloor(n, "drop"));
                                }
                            }catch(Exception exception){

                            }
                        }
                        walls.add(getCeil(n));

                    }catch(ArrayIndexOutOfBoundsException e){

                    }
                }
                    for (int i = 5; i > 0; i--){
                        try{
                            if(maze[rr][cc-i] == '#')
                                walls.add(getFaceWall(i));
                        }catch(ArrayIndexOutOfBoundsException e){

                        }
                    }
                break;
        }
    }

    public Wall getLeft(int n){
        int[] cLocs = new int[]{100 + 50 * n, 150 + 50 * n, 650 - 50 * n, 700 - 50 * n};
        int[] rLocs = new int[]{100 + 50 * n, 150 + 50 * n, 150 + 50 * n, 100 + 50 * n};
        return new Wall(rLocs,cLocs,255-50*n,255-50*n,255-50*n,"Left",size);
    }

    public Wall getLeftPath(int n) {
        int[] cLocs = new int[]{100 + 50 * n, 100 + 50 * n, 700 - 50 * n, 700 - 50 * n};
        int[] rLocs = new int[]{50 + 50 * n, 100 + 50 * n, 100 + 50 * n, 50 + 50 * n};
        return new Wall(rLocs,cLocs,255-50*n,255-50*n,255-50*n,"LeftPath",size);
    }

    public Wall getRight(int n){
        int[] cLocs = new int[]{100 + 50 * n, 150 + 50 * n, 650 - 50 * n, 700 - 50 * n};
        int[] rLocs = new int[]{700 - 50 * n, 650 - 50 * n, 650 - 50 * n, 700 - 50 * n};
        return new Wall(rLocs,cLocs,255-50*n,255-50*n,255-50*n,"Right",size);
    }

    public Wall getRightPath(int n) {
        int[] cLocs = new int[]{100 + 50 * n, 100 + 50 * n, 700 - 50 * n, 700 - 50 * n};
        int[] rLocs = new int[]{700 - 50 * n, 750 - 50 * n, 750 - 50 * n, 700 - 50 * n};
        return new Wall(rLocs,cLocs,255-50*n,255-50*n,255-50*n,"RightPath",size);
    }

    public Wall getCeil(int n) {
        int[] cLocs = new int[]{100 + 50 * n, 150 + 50 * n, 150 + 50 * n, 100 + 50 * n};
        int[] rLocs = new int[]{100 + 50 * n, 150 + 50 * n, 650 - 50 * n, 700 - 50 * n};
        return new Wall(rLocs,cLocs,255-50*n,255-50*n,255-50*n,"Ceil",shrink);
    }

    public Wall getFloor(int n, String type) {
        if (type.equals("drop")){
            int[] cLocs = new int[]{700 - 50 * n, 650 - 50 * n, 650 - 50 * n, 700 - 50 * n};
            int[] rLocs = new int[]{700 - 50 * n, 650 - 50 * n, 150 + 50 * n, 100 + 50 * n};
            return new Wall(rLocs,cLocs,160-5*n,82-5*n,45-5*n,"dropFloor",shrink);
        }
        else {
            int[] cLocs = new int[]{700 - 50 * n, 650 - 50 * n, 650 - 50 * n, 700 - 50 * n};
            int[] rLocs = new int[]{700 - 50 * n, 650 - 50 * n, 150 + 50 * n, 100 + 50 * n};
            return new Wall(rLocs, cLocs, 255 - 50 * n, 255 - 50 * n, 255 - 50 * n, "Floor", shrink);
        }
    }

    public Wall getFaceWall(int n) {
        int[] cLocs = new int[]{100 + 50 * n, 700 - 50 * n, 700 - 50 * n, 100 + 50 * n};
        int[] rLocs = new int[]{100 + 50 * n, 100 + 50 * n, 700 - 50 * n, 700 - 50 * n};
        return new Wall(rLocs,cLocs,255-50*n,255-50*n,255-50*n,"Face",shrink);
    }

    public Wall getRightFloor(int n){
        int [] cLocs = new int[]{700 - 50 * n,650 - 50 * n,650 - 50 * n, 650 - 50 * n};
        int [] rLocs = new int[]{700 - 50 * n,700 - 50 * n,650 - 50 * n, 650 - 50 * n};
        return new Wall(rLocs,cLocs,255-50*n,255-50*n,255-50*n,"FloorRight",shrink);
    }

    public Wall getRightCeil(int n){
        int [] cLocs = new int[]{100 + 50 * n,150 + 50 * n,150 + 50 * n, 150 + 50 * n};
        int [] rLocs = new int[]{700 - 50 * n,700 - 50 * n,650 - 50 * n, 650 - 50 * n};
        return new Wall(rLocs,cLocs,255-50*n,255-50*n,255-50*n,"CeilRight",shrink);
    }
    public Wall getLeftFloor(int n){
        int [] cLocs = new int[]{700 - 50 * n,650 - 50 * n,650 - 50 * n, 650 - 50 * n};
        int [] rLocs = new int[]{100 + 50 * n,100 + 50 * n,150 + 50 * n, 150 + 50 * n};
        return new Wall(rLocs,cLocs,255-50*n,255-50*n,255-50*n,"FloorLeft",shrink);
    }
    public Wall getLeftCeil(int n){
        int [] cLocs = new int[]{100 + 50 * n,150 + 50 * n,150 + 50 * n, 150 + 50 * n};
        int [] rLocs = new int[]{100 + 50 * n,100 + 50 * n,150 + 50 * n, 150 + 50 * n};
        return new Wall(rLocs,cLocs,255-50*n,255-50*n,255-50*n,"CeilLeft",shrink);
    }

    public static void main (String[]args){
        MazeProgram maze = new MazeProgram();
    }

    public static void PlaySound(File sound){
        try{
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(sound));
            clip.start();
        }catch (Exception e){

        }
    }

   public String CountdownTimer(){

       if (startTimer && !restart) {
           timer = new Timer(1000, new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {

                   second++;
                   ddSecond = decimalFormat.format(second);
                   ddMinute = decimalFormat.format(minute);

                   if (second == 60) {
                       second = 0;
                       minute++;
                       ddSecond = decimalFormat.format(second);
                       ddMinute = decimalFormat.format(minute);
                   }
                   repaint();
               }
           });
       }
       if(restart){
           ddMinute = "00";
           ddSecond = "00";
           restart = false;
       }
       return ddMinute+":"+ddSecond;

   }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        hero.move(e.getKeyCode(),maze);

        if(hero.getLoc().getR()==47 && hero.getLoc().getC()==115 && !isDone){
            isDone = true;
            timer.stop();
            isRunning = false;
            PlaySound(endSuccess);

            for (int i = 0; i < highscores.size(); i++){
                if (minute*60+second<=highscores.get(i)){
                    highscores.remove(4);
                    highscores.add(i,minute*60+second);
                    break;
                }

            }

            try {
                FileOutputStream fos = new FileOutputStream("highscores.txt");
                PrintWriter pw = new PrintWriter(fos);
                for (int m:highscores)
                    pw.println(m);
                pw.close();
            }catch(Exception exc){

            }

        }

        if(e.getKeyCode()==38 && isRunning) {
            timer.start();


            if(isStart) {
                PlaySound(startSound);
                isStart = false;
            }
        }
        if (e.getKeyCode()==68 && !isDone) {
            breadcrumbs--;
            if (breadcrumbs>-1) {
                PlaySound(dropSound);
                rCrumbs.add(hero.getLoc().getR());
                cCrumbs.add(hero.getLoc().getC());
            }
        }

        if(e.getKeyCode()==77 && isDone){
            first_maze = !first_maze;
            rCrumbs.clear();
            cCrumbs.clear();
            breadcrumbs = 5;
            try{
                if (!first_maze)
                    input = new BufferedReader(new FileReader(maze2));
                else
                    input = new BufferedReader(new FileReader(maze1));

                String text;
                int r = 0;
                while((text=input.readLine())!=null){
                    for (int c = 0; c < text.length(); c++){
                        maze[r][c] = text.charAt(c);
                    }
                    r++;
                }

            }catch(IOException e3){
                System.out.println(e.toString());
            }

            draw3D = !draw3D;
            if(isDone) {

                hero.setLoc(new Location(r,c));
                timer.stop();
                minute = 0;
                second = 0;
                isDone = false;
                restart = true;
                isStart = true;
                draw3D = false;
                isRunning = true;
                hero.setMoves(0);
            }
        }

        if (e.getKeyCode()==32) {
            draw3D = !draw3D;
            if(isDone) {
                rCrumbs.clear();
                cCrumbs.clear();
                breadcrumbs = 5;
                hero.setLoc(new Location(r,c));
                timer.stop();
                minute = 0;
                second = 0;
                isDone = false;
                restart = true;
                isStart = true;
                draw3D = false;
                isRunning = true;
                hero.setMoves(0);
            }
        }
        if(draw3D)
            createWalls();
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}