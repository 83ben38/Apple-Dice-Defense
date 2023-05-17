import acm.graphics.GLabel;
import acm.graphics.GPoint;
import acm.program.Program;
import svu.csc213.Dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import static acm.util.JTFTools.pause;

public class GlobalVariables {
    static boolean question = false;
    static boolean tutorial = false;
    static int enemiesLeaked = 0;
    static int score = 0;
    static int highScore;
    static GLabel scoreLabel = new GLabel("Score: " + score);
    static GLabel highScoreLabel = new GLabel("High score: " + highScore);
    static int ticksTaken;
    static volatile String location;
    static {
        try {
            location = new File(".").getCanonicalPath() + "/saveFile.txt";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static int[] v = new int[10];
    static boolean wave = false;
    static boolean FF = false;
    static volatile boolean enemiesLeft = false;
    static int waveN = 0;
    static int lives = 100;
    static GTile pickedUp;
    static ArrayList<GApple> enemies = new ArrayList<>();
    static ArrayList<GApple> zombies = new ArrayList<>();
    // the wave information in the form of health, speed, spacing, amount
    static int[][] waveData = {{1,1,2,3},{2,2,0,2},{2,1,1,5},{5,2,0,1},{1,1,1,10},{3,4,0,1},{5,2,2,4},{3,2,1,10},{20,2,0,1},{6,6,2,6},{20,1,1,5},{5,3,1,30},{100,3,0,1},{20,10,3,3},{20,3,1,50},{100,3,3,5},{250,4,0,1},{50,20,3,5},{250,4,3,5},{100,3,1,50},{500,5,0,1},{100,25,1,10},{10,4,0,300},{500,3,2,20},{2500,4,0,1}};
    static GTile[][] tiles = new GTile[20][10];
    static ArrayList<GProjectile> projectiles = new ArrayList<>();
    static ADD screen;
    static volatile boolean starting = false;
    // coordinates of the current end of the path
    static int[] current;
    static ArrayList<GTile> path = new ArrayList<>();
    public static void tick(){
        //tick all the enemies, zombies, tiles, projectiles
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).tick();
        }
        for (int i = 0; i < zombies.size(); i++) {
            zombies.get(i).tick();
        }
        for (GTile[] someTiles: tiles) {
            for (GTile tile:someTiles) {
                tile.tick();
            }
        }
        for (int i = 0; i < projectiles.size(); i++) {
            projectiles.get(i).tick();
        }
        // sort the enemies so first will be targeted
        enemies.sort((o1, o2) -> {
            if (o1.pathAmount > o2.pathAmount){
                return -1;
            }
            else if (o2.pathAmount > o1.pathAmount){
                return 1;
            }
            else{
                if (o1.pathLeft < o2.pathLeft){
                    return -1;
                }
                else if (o2.pathLeft < o1.pathLeft) {
                    return 1;
                }
                return 0;
            }
        });
        screen.repaint();
    }
    public static boolean isNextTo(int x1, int y1, int x2, int y2){
        if (x2%2==1){
            if (y2 == y1-1){
                return Math.abs(x1 - x2) < 2;
            }
            else if (y2 == y1){
               return Math.abs(x1 - x2) == 1;
            }
            else if (y2-1 == y1){
                return x1 == x2;
            }
            else{
                return false;
            }
        }
        else{
            if (y2-1 == y1){
                return Math.abs(x1 - x2) < 2;
            }
            else if (y2 == y1){
                return Math.abs(x1 - x2) == 1;
            }
            else if (y2 == y1-1){
                return x1 == x2;
            }
            else{
                return false;
            }
        }
    }
    public static GPoint getPositionOf(int x, int y){
        GPoint p = new GPoint();
        p.setLocation(x*38,(y+((double)x%2/2)+1)*50);
        return p;
    }
    public static int randomInt(int from, int to){
        return (int)(Math.random()*(to-from+1)) + from;
    }
    public static void startWave(){
        wave = true;
        enemiesLeft = true;
        // make enemies properly spaced and stuff
        if (waveN < waveData.length) {
            for (int i = 0; i < waveData[waveN][3]; i++) {
                GApple p = new GApple(waveData[waveN][0], waveData[waveN][1]);
                screen.addToScreen(p);
                enemiesLeft = true;
                for (int j = 0; j < 10 * GlobalVariables.waveData[waveN][2]; j++) {
                    tick();
                    if (FF) {
                        //fast forward is 250 fps
                        pause(4);
                    } else {
                        //slower is 40 fps
                        pause(25);
                    }
                }
            }
        }
        else{
            for (int i = 0; i < ((waveN-22)*10); i++) {
                GApple p = new GApple(waveN*waveN*waveN*waveN/500,waveN-22);
                screen.addToScreen(p);
                enemiesLeft = true;
                for (int j = 0; j < 25 * Math.pow(0.75,(waveN-22)); j++) {
                    tick();
                    if (FF) {
                        //freeplay fast forward is 1000 fps
                        pause(1);
                    } else {
                        pause(10);
                    }
                }
            }
        }

        wave = false;
        ticksTaken = 0;
        // continue ticking while there are enemies left
        while(enemiesLeft){
            tick();
            ticksTaken++;
            if (waveN < waveData.length){
                if (FF) {
                    pause(4);
                } else {
                    pause(25);
                }
            }
            else {
                if (FF) {
                    pause(1);
                } else {
                    pause(10);
                }
            }
        }
        //remove all the projectiles, zombies
        while(projectiles.size() > 0){
            projectiles.get(0).remove();
        }
        while(zombies.size() > 0){
            zombies.get(0).remove();
        }
        //if the wave was a freeplay wave, have a chance to get a freeplay dice
        if (waveN >= waveData.length){
            freeplayWin();
        }
        waveN++;
        if (tutorial){
            Dialog.showMessage("If you have two of the same dice you can combine them to make them more powerful.");
            Dialog.showMessage("Have fun!");
            tutorial = false;
        }
        //get a dice at the end of the wave
        screen.getADice();
    }
    public static void run2(){
        //get the file contents
        File f = new File(location);
        String s = null;
        try {
            s = new Scanner(f).next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        highScore = Integer.parseInt(s.substring(s.length()-8));
        highScoreLabel.setLabel("High score: " + highScore);
        //add each dice if you have it
        for (int i = 1; i < s.length()-7; i++) {
            if (s.charAt(i-1) == '1') {
                GTile t = new GTile(50, ((i - 1) % 5) + 2, 4 + ((i - 1) / 5), i);
                screen.addToScreen(t);
            }
        }
        //add the top 5 slots
        GTile[] z = new GTile[5];
        for (int i = 1; i < 6; i++) {
            GTile t = new GTile(50,i+1,2);
            screen.addToScreen(t);
            z[i-1] = t;
        }
        GLabel l = new GLabel("?");
        l.setFont(new Font(l.getFont().getFontName(), Font.PLAIN,30));
        l.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                question = !question;
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        screen.add(l,l.getWidth(),l.getHeight()*2/3);
        JButton j = new JButton("GO!");
        screen.add(j, Program.SOUTH);

        j.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //check if all the top 5 slots are full
                boolean exit = true;
                for (int i = 0; i < 5; i++) {
                    if (!z[i].dice){
                        exit = false;
                    }
                }
                //if they are, start the game
                if (exit) {
                    starting = true;
                    j.setVisible(false);
                    // the first slot is more likely to be picked and the last is less likely
                    for (int i = 0; i < 3; i++) {
                        v[i] = z[0].diceType;
                    }
                    for (int i = 1; i < 4; i++) {
                        for (int k = (i*2) + 1; k < (i*2) + 3; k++) {
                            v[k] = z[i].diceType;
                        }
                    }
                    v[9] = z[4].diceType;
                    screen.removeAll();
                    //this button will be recycled as the wave starter
                    screen.waveStarter = j;
                    j.removeMouseListener(this);
                    screen.remove(l);
                    question = false;
                    screen.run1();
                    if (tutorial){
                        Dialog.showMessage("Now you make a path for the apples to go on.");
                        Dialog.showMessage("It must start on the right and end on the left.");
                        Dialog.showMessage("Alternatively, you can click the random map button for a random map.");
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        JButton i = new JButton("tutorial");
        screen.add(i,Program.SOUTH);
        screen.fastForward = i;
        i.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tutorial = true;
                Dialog.showMessage("Welcome to apple dice defense");
                Dialog.showMessage("It is a tower defense game where you place dice to try to stop the apples from getting to the exit.");
                Dialog.showMessage("You may have one dice picked up at a time. Click on an empty slot to place a dice and on a dice to pick it up.");
                Dialog.showMessage("Move five dice to the top and then press GO!.");
                Dialog.showMessage("Click the question mark to see what dice do. Click on it again to be able to move them.");
                Dialog.showMessage("You will get more of the dice in the first slot and less of the last.");
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
    public static void exist(){
        run2();
    }
    public static void getDice(int i) throws IOException {
        //change the value of the requested dice
        File f = new File(location);
        String s = null;
        try {
            s = new Scanner(f).next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        s = s.substring(0,i) + "1" + s.substring(i+1);
        FileWriter w = new FileWriter(location);
        w.append(s);
        w.flush();
    }
    public static void win(){
        //get the file
        String s = "";
        try {
            s = new Scanner(new File(location)).next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // if you have any missing dice
        if (s.substring(0,16).contains("0")){
            int o = s.indexOf("0");
            String v = s.substring(0,o) + "1" + s.substring(o+1,16);
            // if you have one then get it
            if (v.contains("0")){
                int r = randomInt(0,15);
                while (s.charAt(r) == '1'){
                    r = randomInt(0,15);
                }
                int k = randomInt(0,15);
                while (s.charAt(k) == '1' || k == r){
                    k = randomInt(0,15);
                }
                String[] names = {"Green dice","Fire dice","Water dice","Electric dice","Ice dice","Growth dice", "Orbit dice", "Multi-projectile dice", "Buff dice", "Trapper dice", "Zombie dice", "Bomb dice", "Flamethrower dice", "Anti-pierce dice", "Effect amplifier dice", "Rainbow dice"};
                try {
                    if (Dialog.getYesOrNo("Would you like " + names[r] + "(Yes), or " + names[k] + "(No)?")) {
                        getDice(r);
                    } else {
                        getDice(k);
                    }
                }
                catch(IOException ignored){

                }
            }
            //otherwise pick between two
            else{
                Dialog.showMessage("You have unlocked all the dice.");
                if (o != -1){
                    try {
                        getDice(o);
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }
    public static void freeplayWin(){
        //get a freeplay dice you dont have
        String r = "";
        try {
            r = new Scanner(new File(location)).next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (r.substring(16,r.length()-8).contains("0")) {
            if (randomInt(1, 10) == 1) {
                try {
                    int s = randomInt(16, r.length() - 9);
                    while (r.charAt(s) == '1') {
                        s = randomInt(16, r.length() - 9);
                    }
                    getDice(s);
                    String[] names = {"Wild dice","Combiner dice", "Randomizer dice", "Score dice"};
                    Dialog.showMessage("You got " + names[s - 16]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (randomInt(1,10) == 1){
            Dialog.showMessage("You have all of the freeplay dice.");
        }
    }
    public static void addButton(){
        screen.fastForward.setText("Random map");
        while(screen.fastForward.getMouseListeners().length > 0) {
            screen.fastForward.removeMouseListener(screen.fastForward.getMouseListeners()[0]);
        }
        // this will be recycled as the fastforward button
        screen.fastForward.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // random path code
                ArrayList<GTile> backup = (ArrayList<GTile>) path.clone();
                if (path.size() < 1){
                    tiles[0][randomInt(0,9)].makePath();
                }
                int x = path.get(path.size()-1).x;
                int y = path.get(path.size()-1).y;
                while (starting){
                    tiles[x][y].makePath();
                    ArrayList<GTile> gt = tiles[x][y].getAround();
                    GTile next = gt.get(randomInt(0,gt.size()-1));
                    while (next.path){
                        gt.remove(next);
                        if (gt.size() < 1){
                            for (int i = 0; i < path.size(); i++) {
                                if (!backup.contains(path.get(i))){
                                    path.get(i).setEmpty();
                                }
                            }
                            path = (ArrayList<GTile>) backup.clone();
                            if (path.size() < 1){
                                current = null;
                                tiles[0][randomInt(0,9)].makePath();
                            }
                            next = path.get(path.size()-1);
                            current = new int[]{next.x, next.y};
                            break;
                        }
                        next = gt.get(randomInt(0,gt.size()-1));
                    }
                    x = next.x;
                    y = next.y;
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
    public static void setHighScore(int t){
        highScore = t;
        //change the value of the requested dice
        File f = new File(location);
        String s = null;
        String scor = Integer.toString(score);
        while (scor.length() < 8){
            scor = "0" + scor;
        }
        try {
            s = new Scanner(f).next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        s = s.substring(0,s.length()-8) + scor;
        try {
            FileWriter w = new FileWriter(location);
            w.append(s);
            w.flush();
        } catch (IOException ignored) {
        }

    }
}
