import acm.graphics.GOval;

import java.awt.*;
import java.util.ArrayList;

public class GProjectile extends GMovable {
    ArrayList<GApple> hit = new ArrayList<>();
    GOval v = new GOval(7,7);
    GApple target;
    int type;
    int lvl;
    int pierceLeft;
    public void tick(){
        repaint();
        if (type == 1){
            moveSteps(lvl);
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                GApple enemy = GlobalVariables.enemies.get(i);
                if (isTouching(enemy)){
                    pierceLeft--;
                    if (pierceLeft < 1){
                        this.remove();
                    }
                    if(enemy.frozen > 1){
                        enemy.life -= lvl;
                    }
                    enemy.life -= lvl;
                    hit.add(enemy);
                    if (enemy.life < 1){
                        enemy.remove();
                        i--;
                    }
                }
            }
        }
        else if (type == 2){
            if (pierceLeft == pierce()){
                moveTowards(target.getX(), target.getY(),lvl*2);
            }
            else{
                moveSteps(lvl*2);
            }
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                GApple enemy = GlobalVariables.enemies.get(i);
                if (isTouching(enemy)){
                    pierceLeft--;
                    if (pierceLeft < 1){
                        this.remove();
                    }
                    if (lvl > 2) {
                        if(enemy.frozen > 1){
                            enemy.life -= Math.pow(lvl - 2, 2);
                        }
                        enemy.life -= Math.pow(lvl - 2, 2);
                    }
                    enemy.fire+=lvl*Math.sqrt(lvl);
                    if (enemy.fireDmg < lvl){
                        enemy.fireDmg = lvl;
                    }
                    hit.add(enemy);
                    if (enemy.life < 1){
                        enemy.remove();
                        i--;
                    }
                }
            }
        }
        else if (type == 3){
            if (pierceLeft > 0) {
                moveTowards(target.getX(), target.getY(), lvl * 2);
                for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                    GApple enemy = GlobalVariables.enemies.get(i);
                    if (isTouching(enemy)) {
                        if (enemy.frozen > 0){
                            enemy.life -= lvl;
                        }
                        enemy.life -= lvl;
                        pierceLeft = 0;
                        hit.add(enemy);
                        if (enemy.life < 1) {
                            enemy.remove();
                            i--;
                        }
                    }
                }
            }
            else{
                setLocation(getX()-getWidth()*0.02,getY()-getHeight()*0.02);
                scale(1.04);
                for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                    GApple enemy = GlobalVariables.enemies.get(i);
                    if (isTouching(enemy)) {
                        if (enemy.frozen > 0){
                            enemy.life -= lvl;
                        }
                        enemy.life -= lvl;
                        hit.add(enemy);
                        if (enemy.life < 1) {
                            enemy.remove();
                            i--;
                        }
                    }
                }
                if (getWidth() > 24 + (25*lvl)){
                    remove();
                }
            }
        }
        else if (type == 4){
            if (pierceLeft == pierce()){
                moveTowards(target.getX(), target.getY(),lvl*3);
            }
            else{
                moveSteps(lvl*3);
            }
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                GApple enemy = GlobalVariables.enemies.get(i);
                if (isTouching(enemy)){
                    pierceLeft--;
                    if (pierceLeft < 1){
                        this.remove();
                    }
                    if (enemy.frozen > 0){
                        enemy.life -= Math.pow(lvl,2) + 3;
                    }
                    enemy.life -= Math.pow(lvl,2) + 3;
                    hit.add(enemy);
                    if (enemy.life < 1){
                        enemy.remove();
                        i--;
                    }
                }
            }
        }
        else if (type == 5){
            if (pierceLeft == pierce()){
                moveTowards(target.getX(), target.getY(),lvl);
            }
            else{
                moveSteps(lvl);
            }
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                GApple enemy = GlobalVariables.enemies.get(i);
                if (isTouching(enemy)){
                    pierceLeft--;
                    if (pierceLeft < 1){
                        this.remove();
                    }
                    if (lvl > 2) {
                        enemy.life -= Math.pow(lvl - 2, 2);
                    }
                    enemy.frozen += 100*lvl;
                    hit.add(enemy);
                    if (enemy.life < 1){
                        enemy.remove();
                        i--;
                    }
                }
            }
        }
        if (type != 1) {
            if (target.life < 1) {
                if (pierceLeft == pierce()) {
                    remove();
                }
            }
        }
        if (this.getX() < -50 || this.getX() > 1050 || this.getY() < -50 || this.getY() > 550){
            remove();
        }
    }
    public GProjectile(int type, int lvl, GApple target, GTile starter){
        GlobalVariables.projectiles.add(this);
        this.lvl = lvl;
        this.type = type;
        this.target = target;
        pierceLeft = pierce();
        v.setFilled(true);
        Color z;
        switch (type) {
            case 2 -> z = Color.red;
            case 3 -> z = Color.blue;
            case 4 -> z = Color.yellow;
            default -> z = Color.cyan;
        }
        v.setFillColor(z);
        add(v);
        addToScreen(starter);
    }
    public GProjectile(int lvl, int direction, GTile starter){
        GlobalVariables.projectiles.add(this);
        type = 1;
        this.lvl = lvl;
        if (direction == 1){
            xAmount = 0;
            yAmount = 1;
        }
        else if (direction == 2){
            yAmount = 0.4;
            xAmount = 0.6;
        }
        else if (direction == 3){
            yAmount = -0.4;
            xAmount = 0.6;
        }
        else if (direction == 4){
            yAmount = -1;
            xAmount = 0;
        }
        else if (direction == 5){
            yAmount = -0.4;
            xAmount = -0.6;
        }
        else{
            xAmount = -0.6;
            yAmount = 0.4;
        }
        pierceLeft = 10*lvl;
        v.setFilled(true);
        v.setFillColor(Color.green);
        add(v);
        addToScreen(starter);
    }
    private boolean isTouching(GApple enemy){
        return (calculateDistance(enemy.getX(),enemy.getY()) < 25 && !hit.contains(enemy));
    }
    private int pierce(){
        if (type == 2){
            return 2*lvl;
        }
        else if (type == 3){
            return 1;
        }
        else if (type == 4){
            return 1+lvl;
        }
        else {
          return 2 + (lvl/2);
        }
    }
    public double calculateDistance(double x, double y){
        return Math.sqrt(Math.pow(x-getX(),2)+Math.pow(y-getY(),2));
    }
    private void addToScreen(GTile s){
        GlobalVariables.screen.add(this,s.getX()+s.getWidth()/2-this.getWidth()/2,s.getY()+s.getHeight()/2-this.getHeight()/2);
    }
    void remove(){
        GlobalVariables.projectiles.remove(this);
        GlobalVariables.screen.remove(this);
    }
}
