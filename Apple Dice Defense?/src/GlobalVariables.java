import acm.graphics.GPoint;

import java.util.ArrayList;

import static acm.util.JTFTools.pause;

public class GlobalVariables {
    static boolean wave = false;
    static boolean FF = false;
    static volatile boolean enemiesLeft = false;
    static int waveN = 0;
    static int lives = 100;
    static GTile pickedUp;
    static ArrayList<GApple> enemies = new ArrayList<>();
    static int[][] waveData = {{1,1,2,3},{2,2,0,2},{2,1,1,5},{5,2,0,1},{1,1,1,10},{3,4,0,1},{5,2,2,4},{3,2,1,10},{20,2,0,1},{6,6,2,6},{20,1,1,5},{5,3,1,30},{100,3,0,1},{20,10,3,3},{20,3,1,50},{100,3,3,5},{250,4,0,1},{50,15,3,5},{250,4,3,5},{100,3,1,50},{500,5,0,1},{100,15,1,25},{2500,3,0,1}};
    static GTile[][] tiles = new GTile[20][10];
    static ArrayList<GProjectile> projectiles = new ArrayList<>();
    static ADD screen;
    static boolean starting = true;
    static int[] current;
    static ArrayList<GTile> path = new ArrayList<>();
    public static void tick(){
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).tick();
        }
        for (GTile[] someTiles: tiles) {
            for (GTile tile:someTiles) {
                tile.tick();
            }
        }
        for (int i = 0; i < projectiles.size(); i++) {
            projectiles.get(i).tick();
        }
        enemies.sort((o1, o2) -> {
            if (o1.pathAmount > o2.pathAmount){
                return 1;
            }
            else if (o2.pathAmount > o1.pathAmount){
                return -1;
            }
            else{
                if (o1.pathLeft < o2.pathLeft){
                    return 1;
                }
                else if (o2.pathLeft < o1.pathLeft) {
                    return -1;
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
        p.setLocation(x*38,(y+((double)x%2/2))*50);
        return p;
    }
    public static int randomInt(int from, int to){
        return (int)(Math.random()*(to-from+1)) + from;
    }
    public static void startWave(){
        wave = true;
        enemiesLeft = true;
        if (waveN < waveData.length) {
            for (int i = 0; i < waveData[waveN][3]; i++) {
                GApple p = new GApple(waveData[waveN][0], waveData[waveN][1]);
                screen.addToScreen(p);
                for (int j = 0; j < 10 * GlobalVariables.waveData[waveN][2]; j++) {
                    tick();
                    if (FF) {
                        pause(10);
                    } else {
                        pause(25);
                    }
                }
            }
        }
        else{
            for (int i = 0; i < waveN; i++) {
                GApple p = new GApple(waveN*waveN,(int)Math.sqrt(waveN));
                screen.addToScreen(p);
                for (int j = 0; j < 1000 * Math.pow(0.99,waveN); j++) {
                    tick();
                    if (FF) {
                        pause(10);
                    } else {
                        pause(25);
                    }
                }
            }
        }
        wave = false;
        while(enemiesLeft){
            tick();
            if (FF){
                pause(10);
            }
            else {
                pause(25);
            }
        }
        while(projectiles.size() > 0){
            projectiles.get(0).remove();
        }
        waveN++;
        screen.getADice();
    }
}
