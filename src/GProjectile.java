import acm.graphics.GLine;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GPoint;

import java.awt.*;
import java.util.ArrayList;

public class GProjectile extends GMovable {
    GTile owner;
    ArrayList<GApple> hit = new ArrayList<>();
    GOval v = new GOval(7,7);
    GApple target;
    GPoint targets;
    int type;
    int lvl;
    int pierceLeft;
    boolean out;
    public void tick(){
        //basic code explained in type == 8
        repaint();
        if (type == 0){
            //bombs blowing up
            setLocation(getX()-getWidth()*0.02,getY()-getHeight()*0.02);
            scale(1.04);
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                GApple enemy = GlobalVariables.enemies.get(i);
                if (isTouching(enemy)) {
                    enemy.life -= lvl*enemy.vunrablility;
                    hit.add(enemy);
                    if (enemy.life < 1) {
                        enemy.remove();
                        i--;
                    }
                }
            }
            if (getWidth() > 100){
                remove();
            }
        }
        if (type == 1){
            //green dice
            moveSteps(lvl);
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                GApple enemy = GlobalVariables.enemies.get(i);
                if (isTouching(enemy)){
                    if (pierceLeft > 0) {
                        enemy.life -= (lvl + (lvl/2) + (lvl/3) + (lvl/5))*enemy.vunrablility;
                        hit.add(enemy);
                        if (enemy.life < 1) {
                            enemy.remove();
                            i--;
                        }
                    }
                    if (enemy.pierce) {
                        pierceLeft--;
                    }
                    target = null;
                    if (pierceLeft < 1){
                        this.remove();
                    }
                }
            }
        }
        else if (type == 2){
            //fire dice
            if (target != null){
                moveTowards(target.getX(), target.getY(),lvl*2);
            }
            else{
                moveSteps(lvl*2);
            }
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                GApple enemy = GlobalVariables.enemies.get(i);
                if (isTouching(enemy)){
                    if (pierceLeft > 0) {
                        if (lvl > 2) {
                            enemy.life -= Math.pow(lvl - 2, 2)*enemy.vunrablility;
                        }
                        enemy.fire += lvl * Math.sqrt(lvl);
                        if (enemy.fireDmg < lvl) {
                            enemy.fireDmg = lvl;
                        }
                        hit.add(enemy);
                        if (enemy.life < 1) {
                            enemy.remove();
                            i--;
                        }
                        target = null;
                    }
                    if (enemy.pierce) {
                        pierceLeft--;
                    }
                    if (pierceLeft < 1){
                        this.remove();
                    }
                }
            }
        }
        else if (type == 3){
            //water dice
            // move to your target if you haven't hit anything
            if (pierceLeft > 0) {
                moveTowards(target.getX(), target.getY(), lvl * 2);
                for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                    GApple enemy = GlobalVariables.enemies.get(i);
                    if (isTouching(enemy)) {
                        enemy.life -= lvl*enemy.vunrablility*((lvl/2)+1);
                        pierceLeft = 0;
                        hit.add(enemy);
                        if (enemy.life < 1) {
                            enemy.remove();
                            i--;
                        }
                    }
                }
            }
            //blow up
            else{
                setLocation(getX()-getWidth()*0.02,getY()-getHeight()*0.02);
                scale(1.04);
                for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                    GApple enemy = GlobalVariables.enemies.get(i);
                    if (isTouching(enemy)) {
                        enemy.life -= lvl*enemy.vunrablility*((lvl/2)+1);
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
            //lighting dice
            //follow enemy if you haven't hit anything
            if (target != null){
                moveTowards(target.getX(), target.getY(),lvl*3);
                for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                    GApple enemy = GlobalVariables.enemies.get(i);
                    if (isTouching(enemy)){
                            enemy.life -= Math.pow(1.5,pierceLeft)*target.vunrablility;
                            hit.add(enemy);
                            if (enemy.life < 1) {
                                enemy.remove();
                            }
                        if (enemy.pierce) {
                            pierceLeft--;
                        }
                        target = null;
                        break;
                    }
                }
            }
            //chain to other enemies
            else{
                GApple target = null;
                for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                    if (!hit.contains(GlobalVariables.enemies.get(i))){
                        target = GlobalVariables.enemies.get(i);
                        break;
                    }
                }
                if (target == null){
                    remove();
                }
                else{
                    if (pierceLeft < 1){
                        pierceLeft--;
                        if (pierceLeft < -10){
                            remove();
                        }
                    }
                    else {
                        GLine l = new GLine(hit.get(hit.size() - 1).getX() - this.getX(), hit.get(hit.size() - 1).getY() - this.getY(), target.getX() - this.getX(), target.getY() - this.getY());
                        l.setColor(Color.yellow);
                        add(l);
                        target.life -=Math.pow(1.5,pierceLeft)*target.vunrablility;
                        hit.add(target);
                        if (target.life < 1){
                            target.remove();
                        }
                        if (target.pierce) {
                            pierceLeft--;
                        }
                    }
                }
            }

        }
        else if (type == 5){
            //ice dice
            if (target != null){
                moveTowards(target.getX(), target.getY(),lvl);
            }
            else{
                moveSteps(lvl);
            }
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                GApple enemy = GlobalVariables.enemies.get(i);
                if (isTouching(enemy)){
                    target = null;
                    if (pierceLeft > 0) {
                        if (lvl > 2) {
                            enemy.life -= Math.pow(lvl - 2, 2);
                        }
                        enemy.frozen += 25 * lvl;
                        if (enemy.vunrablility < 1 + (0.2*lvl)){
                            enemy.vunrablility = 1 + (0.2*lvl);
                        }
                        hit.add(enemy);
                        if (enemy.life < 1) {
                            enemy.remove();
                            i--;
                        }
                    }
                    if (enemy.pierce) {
                        pierceLeft--;
                    }
                    if (pierceLeft < 1){
                        this.remove();
                    }
                }
            }
        }
        else if (type == 7){
            //orbit dice
            double x = owner.getX()+owner.getWidth()/2-getWidth()/2;
            double y = owner.getY()+owner.getHeight()/2-getHeight()/2;
            if (out){
                if (pierceLeft < 0) {
                    pierceLeft = 300;
                    hit.clear();
                }
                if (pierceLeft < 25){
                    moveTowards(x,y+100,lvl*2);
                }
                else if (pierceLeft < 50){
                    moveTowards(x-38,y+75,lvl*2);
                }
                else if (pierceLeft < 75){
                    moveTowards(x-76,y+50,lvl*2);
                }
                else if (pierceLeft < 100){
                    moveTowards(x-76,y,lvl*2);
                }
                else if (pierceLeft < 125){
                    moveTowards(x-76,y-50,lvl*2);
                }
                else if (pierceLeft < 150){
                    moveTowards(x-38,y-75,lvl*2);
                }
                else if (pierceLeft < 175){
                    moveTowards(x,y-100,lvl*2);
                }
                else if (pierceLeft < 200){
                    moveTowards(x+38,y-75,lvl*2);
                }
                else if (pierceLeft < 225){
                    moveTowards(x+76,y-50,lvl*2);
                }
                else if (pierceLeft < 250){
                    moveTowards(x+76,y,lvl*2);
                }
                else if (pierceLeft < 275){
                    moveTowards(x+76,y+50,lvl*2);
                }
                else{
                    moveTowards(x+38,y+75,lvl*2);
                }
            }
            else {
                if (pierceLeft < 0) {
                    pierceLeft = 300;
                    hit.clear();
                }
                if (pierceLeft < 50) {
                    moveTowards(x, y + 50, lvl);
                }
                else if (pierceLeft < 100) {
                    moveTowards(x + 38, y + 25, lvl);
                }
                else if (pierceLeft < 150) {
                    moveTowards(x + 38, y - 25, lvl);
                }
                else if (pierceLeft < 200) {
                    moveTowards(x, y - 50, lvl);
                }
                else if (pierceLeft < 250) {
                    moveTowards(x - 38, y - 25, lvl);
                }
                else {
                    moveTowards(x - 38, y + 25, lvl);
                }
            }
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                GApple enemy = GlobalVariables.enemies.get(i);
                if (isTouching(enemy)){
                        enemy.life-=lvl*2*((lvl/3)+1)*enemy.vunrablility;
                        hit.add(enemy);
                        if (enemy.life < 1) {
                            enemy.remove();
                            i--;
                        }
                }
            }
            pierceLeft-=lvl;
        }
        else if (type == 8){
            //multi-projectile dice (basic normal code)
            if (target != null){
                // move to the target if it still exists
                moveTowards(target.getX(), target.getY(),lvl*2);
            }
            else{
                //otherwise move straight
                moveSteps(lvl*2);
            }
            //check if you are touching each enemy
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                GApple enemy = GlobalVariables.enemies.get(i);
                if (isTouching(enemy)){
                    target = null;
                    if (pierceLeft > 0) {
                        //take damage
                        enemy.life -= (lvl + (lvl/2) + (lvl/3)) *enemy.vunrablility;
                        // make sure you don't hit the same enemy twice
                        hit.add(enemy);
                        if (enemy.life < 1) {
                            enemy.remove();
                            i--;
                        }
                    }
                    // if the enemy hasn't been antipierced, reduce your pierce left
                    if (enemy.pierce) {
                        pierceLeft--;
                    }
                    //remove if you are out of pierce
                    if (pierceLeft < 1){
                        this.remove();
                    }
                }
            }
        }
        else if (type == 10){
            //trapper dice
            //if you have reached your space, check for passing enemies
            if (targets == null){
                for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                    GApple enemy = GlobalVariables.enemies.get(i);
                    if (isTouching(enemy)){
                        if (pierceLeft*enemy.vunrablility > enemy.life) {
                            pierceLeft-=enemy.life/enemy.vunrablility;
                            enemy.life-=enemy.life;
                            enemy.remove();
                            i--;
                        }
                        else{
                            enemy.life-=pierceLeft*enemy.vunrablility;
                            this.remove();
                            break;
                        }
                    }
                }
            }
            //otherwise move to your space
            else {
                moveTowards(targets.getX(),targets.getY(),lvl);
                if (calculateDistance(targets.getX(),targets.getY()) < lvl){
                    this.setLocation(targets);
                    targets = null;
                }
            }
        }
        else if (type == 12){
            // bomb dice
            moveTowards(target.getX(), target.getY(),lvl*2);
            if (isTouching(target)){
                target.bombed = true;
                target.bombDmg = 5*lvl;
                remove();
            }
        }
        else if (type == 13){
            //flamethrower dice
            if (target != null){
                moveTowards(target.getX(), target.getY(),lvl*3);
            }
            else{
                moveSteps(lvl*3);
            }
            //you are not allowed to go outside your range
            if (calculateDistance(owner.getX()-owner.getWidth()/2,owner.getY()-owner.getHeight()/2) > (owner.getRange()*50)+25){
                remove();
            }
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                GApple enemy = GlobalVariables.enemies.get(i);
                if (isTouching(enemy)){
                    target = null;
                    if (pierceLeft > 0) {
                        enemy.life-=lvl;
                        hit.add(enemy);
                        if (enemy.life < 1) {
                            enemy.remove();
                            i--;
                        }
                    }
                    if (enemy.pierce) {
                        pierceLeft--;
                    }
                    if (pierceLeft < 1){
                        this.remove();
                    }
                }
            }
        }
        else if (type == 14){
            //anti pierce dice
            if (target != null){
                moveTowards(target.getX(), target.getY(),lvl*3);
            }
            else{
                moveSteps(lvl*3);
            }
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                GApple enemy = GlobalVariables.enemies.get(i);
                if (isTouching(enemy)){
                    target = null;
                    if (pierceLeft > 0) {
                        enemy.life-=(lvl*lvl)+4;
                        hit.add(enemy);
                        if (enemy.life < 1) {
                            enemy.remove();
                            i--;
                        }
                    }
                    if (enemy.pierce) {
                        pierceLeft--;
                        //antipierce the enemy
                        enemy.pierce = false;
                    }
                    if (pierceLeft < 1){
                        this.remove();
                    }
                }
            }
        }
        else if (type == 15){
            //effect amplifier dice
            if (target != null){
                moveTowards(target.getX(), target.getY(),lvl*3);
            }
            else{
                moveSteps(lvl*3);
            }
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                GApple enemy = GlobalVariables.enemies.get(i);
                if (isTouching(enemy)){
                    target = null;
                    if (pierceLeft > 0) {
                        //amplify fireDmg, bombDmg, and vunrability of apples
                        if (enemy.fireDmg > 0){
                            if (enemy.fireDmg*(1 + (0.05*lvl)) == enemy.fireDmg){
                                enemy.fireDmg++;
                            }
                            else{
                                enemy.fireDmg*=(1 + (0.05*lvl));
                            }
                        }
                        if (enemy.vunrablility > 1){
                            enemy.vunrablility*=(1 + (0.05*lvl));
                        }
                        if (enemy.bombDmg > 0){
                            if (enemy.bombDmg*(1 + (0.05*lvl)) == enemy.bombDmg){
                                enemy.bombDmg++;
                            }
                            else{
                                enemy.bombDmg*=(1 + (0.05*lvl));
                            }
                        }
                        hit.add(enemy);
                        if (enemy.life < 1) {
                            enemy.remove();
                            i--;
                        }
                    }
                    if (enemy.pierce) {
                        pierceLeft--;
                    }
                    if (pierceLeft < 1){
                        this.remove();
                    }
                }
            }
        }
        else if (type == 16){
            //rainbow dice
            int p;
            if (owner.getThings()[0]){
                //bonus projectile speed for red
                p =lvl*8;
            }
            else{
                p = lvl*3;
            }
            if (target != null){
                moveTowards(target.getX(), target.getY(),p);
            }
            else{
                moveSteps(p);
            }
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                GApple enemy = GlobalVariables.enemies.get(i);
                if (isTouching(enemy)){
                    target = null;
                    if (pierceLeft > 0) {
                        //bonus damage for orange and blue
                        if (owner.getThings()[1] && owner.getThings()[4]){
                            enemy.life-=(lvl*lvl)+3;
                        }
                        else if (owner.getThings()[1] || owner.getThings()[4]){
                            enemy.life-=lvl + (lvl/2) + (lvl/3) + (lvl/5);
                        }
                        else{
                            enemy.life-=lvl;
                        }
                        hit.add(enemy);
                        if (enemy.life < 1) {
                            enemy.remove();
                            i--;
                        }
                    }
                    if (enemy.pierce) {
                        pierceLeft--;
                    }
                    if (pierceLeft < 1){
                        this.remove();
                    }
                }
            }
        }
        // if your target is gone remove since you will have nowhere to target
        if (target != null) {
            if (target.life < 1) {
                if (pierceLeft == pierce()) {
                    remove();
                }
            }
        }
        // remove if you are out of bounds
        if (this.getX() < -50 || this.getX() > 1050 || this.getY() < -50 || this.getY() > 550){
            remove();
        }
    }
    public GProjectile(int type, int lvl, GApple target, GTile starter){
        //normal projectile instantiation
        owner = starter;
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
            case 8 -> z = new Color(75, 30, 20);
            case 12 -> z = Color.black;
            case 13 -> z = new Color(125,0,0);
            case 14 -> z = Color.pink;
            case 15 -> z = Color.magenta;
            case 16 -> z = Color.white;
            default -> z = Color.cyan;
        }
        v.setFillColor(z);
        add(v);
        addToScreen(starter);
    }
    public GProjectile(int lvl, int direction, GTile starter){
        //instantiation for green dice
        owner = starter;
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
    public GProjectile(int lvl, GTile starter, boolean outside){
        //instantiation for orbit dice
        v.setFillColor(new Color(50, 0, 125));
        v.setFilled(true);
        add(v);
        type = 7;
        this.lvl = lvl;
        owner = starter;
        addToScreen(starter);
        out = outside;
        pierceLeft = 300;
    }
    public GProjectile(int lvl, GTile target, GTile starter){
        //instantiation for trapper dice
        owner = starter;
        GlobalVariables.projectiles.add(this);
        this.lvl = lvl;
        type  =10;
        v.setSize(6+lvl,6+lvl);
        targets = new GPoint(target.getX()+target.getWidth()/2+GlobalVariables.randomInt(-15,15),target.getY()+target.getHeight()/2+GlobalVariables.randomInt(-15,15));
        pierceLeft = pierce();
        v.setFilled(true);
        v.setFillColor(new Color(150,255,150));
        add(v);
        addToScreen(starter);
    }
    public GProjectile(int dmg, GApple starter){
        // instantiation for bombs
        this.lvl = dmg;
        type = 0;
        v.setFilled(true);
        v.setFillColor(new Color(0,0,0));
        add(v);
        addToScreen(starter);
    }
    private boolean isTouching(GApple enemy){
        return (calculateDistance(enemy.getX(),enemy.getY()) < Math.sqrt(Math.pow(getWidth()/2,2)+Math.pow(getHeight()/2,2)) +  Math.sqrt(Math.pow(enemy.getWidth()/2,2)+Math.pow(enemy.getHeight()/2,2))  && !hit.contains(enemy));
    }
    private int pierce(){
        //return the amount of pierce
        if (type == 2 || (type == 16 && owner.getThings()[5])){
            return 2*lvl;
        }
        else if (type == 3 || type == 14){
            return 1;
        }
        else if (type == 4){
            return 3+lvl;
        }
        else if (type == 10){
            return 6*lvl;
        }
        else {
          return 2 + (lvl/2);
        }
    }
    public double calculateDistance(double x, double y){
        return Math.sqrt(Math.pow(x-getX(),2)+Math.pow(y-getY(),2));
    }
    private void addToScreen(GObject s){
        GlobalVariables.screen.add(this,s.getX()+s.getWidth()/2-this.getWidth()/2,s.getY()+s.getHeight()/2-this.getHeight()/2);
    }
    void remove(){
        GlobalVariables.projectiles.remove(this);
        GlobalVariables.screen.remove(this);
        if (owner != null) {
            owner.projectiles.remove(this);
        }
    }
}
