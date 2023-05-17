import acm.graphics.*;

import java.awt.*;

public class GApple extends GMovable {
    boolean pierce = true;
    boolean zombie = false;
    boolean bombed = false;
    int bombDmg;
    double vunrablility = 1;
    int fireDmg = 0;
    int fire = 0;
    int frozen = 0;
    int life;
    int speed;
    int pathAmount = 0;
    int pathLeft = 50;
    public GApple(int life, int speed) {
        this.life=life;
        this.speed=speed;
        configureImage();
        GlobalVariables.enemies.add(this);
    }
    public void configureImage(){
        removeAll();
        if (zombie){
            GOval o = new GOval(40, 25);
            o.setFillColor(new Color(125,150,75));
            o.setFilled(true);
            GLine l = new GLine(0, 0, 0, -15);
            add(l);
            add(o, -20, -12.5);
        }
        else {
            int lifeDiv = life;
            double scale = 1;
            // every 100x life the apples scale by 1.25
            while (lifeDiv > 100) {
                scale *= 1.25;
                lifeDiv /= 100;
            }
            if (lifeDiv <= 10) {
                Color v;
                if (lifeDiv <= 1) {
                    v = Color.red;
                } else if (lifeDiv <= 2) {
                    v = Color.blue;
                } else if (lifeDiv <= 3) {
                    v = Color.green;
                } else if (lifeDiv <= 4) {
                    v = Color.yellow;
                } else if (lifeDiv <= 5) {
                    v = Color.magenta;
                } else if (lifeDiv <= 6) {
                    v = Color.black;
                } else if (lifeDiv <= 7) {
                    v = Color.white;
                } else if (lifeDiv <= 8) {
                    v = Color.orange;
                } else if (lifeDiv <= 9) {
                    v = new Color(95, 0, 255);
                } else {
                    v = Color.cyan;
                }
                GOval o = new GOval(40, 25);
                o.setFillColor(v);
                o.setFilled(true);
                GLine l = new GLine(0, 0, 0, -15);
                add(l);
                add(o, -20, -12.5);
            }
            else if (lifeDiv <= 30) {
                Color c = Color.white;
                Color v = Color.black;
                if (lifeDiv <= 20) {
                    c = Color.black;
                    v = Color.white;
                }
                GOval o = new GOval(40, 25);
                o.setFillColor(c);
                o.setFilled(true);
                GLine l = new GLine(0, 0, 0, -15);
                l.setColor(c);
                add(l);
                add(o, -20, -12.5);
                for (int i = 0; i < 4; i++) {
                    GLine r = new GLine(-15, i * 6.25 - 10, 15, i * 6.25 - 10);
                    r.setColor(v);
                    add(r);
                }
            }
            else if (lifeDiv <= 50) {
                GOval o = new GOval(40, 25);
                o.setFillColor(Color.gray);
                o.setFilled(true);
                GLine l = new GLine(0, 0, 0, -15);
                l.setColor(Color.gray);
                add(l);
                add(o, -20, -12.5);
                Color[] c = new Color[]{Color.red, Color.orange, Color.yellow, Color.green, Color.blue, new Color(95, 0, 255), Color.magenta};
                for (int i = 0; i < c.length; i++) {
                    GLine r = new GLine(-15, i * 3.125 - 10, 15, i * 3.125 - 10);
                    r.setColor(c[i]);
                    add(r);
                }
            }
            else {
                GOval o = new GOval(40, 25);
                o.setFillColor(Color.red);
                o.setFilled(true);
                add(o, -20, -12.5);
                GPolygon p = new GPolygon();
                p.addVertex(5, 0);
                p.addVertex(15, 0);
                p.addVertex(20, 10);
                p.addVertex(15, 20);
                p.addVertex(5, 20);
                p.addVertex(0, 10);
                add(p, p.getWidth() / -2, p.getHeight() / -2);
                Color[] c = new Color[]{Color.red, Color.yellow, Color.green, Color.blue, Color.cyan};
                for (int i = 0; i < c.length; i++) {
                    GOval g = new GOval(5, 5);
                    g.setFilled(true);
                    g.setFillColor(c[i]);
                    if (i == 2) {
                        add(g, g.getWidth() / -2, g.getHeight() / -2);
                    } else {
                        add(g, (i / 2) * 5 - g.getWidth(), (i % 2) * 5 - g.getHeight());
                    }
                }
            }
            if (frozen > 0) {
                GOval o = new GOval(40, 25);
                o.setFillColor(new Color(0, 255, 196, 92));
                o.setFilled(true);
                add(o, -20, -12.5);
            }
            if (fire > 0) {
                GOval o = new GOval(40, 25);
                o.setFillColor(new Color(255, 0, 0, 92));
                o.setFilled(true);
                add(o, -20, -12.5);
            }
            if (bombed){
                GOval o = new GOval(40, 25);
                o.setFillColor(new Color(0, 0, 0, 92));
                o.setFilled(true);
                add(o, -20, -12.5);
            }
            scale(scale);
        }
    }
    public void tick(){
        configureImage();
        if (zombie){
            //move backwards
            if (pathAmount == 0) {
                GlobalVariables.lives += life;
                remove();
                return;
            }
            GTile next = GlobalVariables.path.get(pathAmount - 1);
            moveTowards(next.getX() + next.getWidth() / 2, next.getY() + next.getHeight() / 2, speed);
            pathLeft -= speed;
            //check for apples
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                GApple enemy = GlobalVariables.enemies.get(i);
                if (Math.sqrt(Math.pow(enemy.getX()-getX(),2)+Math.pow(enemy.getY()-getY(),2)) < 25){
                    if (enemy.life > life){
                        enemy.life-=this.life;
                        remove();
                        break;
                    }
                    else{
                        this.life-= enemy.life;
                        enemy.life = 0;
                        enemy.remove();
                        i--;
                    }
                }
            }
            //gain lives if it gets to the beginning
            if (pathLeft < 1) {
                pathLeft = 50;
                pathAmount--;
                if (pathAmount == 0) {
                    GlobalVariables.lives += life;
                    GlobalVariables.enemiesLeaked--;
                    remove();
                }
            }
        }
        else {
            if (frozen > 0) {
                //frozen enemies lose 1 + sqrt of life / 100 frozen every tick.
                frozen-= Math.sqrt((life/100))+1;
                if (frozen <= 0) {
                    vunrablility = 1;
                }
            } else {
                // move your speed amount on the path
                GTile next = GlobalVariables.path.get(pathAmount + 1);
                moveTowards(next.getX() + next.getWidth() / 2, next.getY() + next.getHeight() / 2, speed);
                pathLeft -= speed;
                if (pathLeft < 1) {
                    // if you get past a hex, reset and start moving toward the next one
                    pathLeft = 50;
                    pathAmount++;
                    if (fire > 0) {
                        //take fire damage
                        fire--;
                        life -= fireDmg * vunrablility;
                        if (fire < 1) {
                            fireDmg = 0;
                        }
                    }
                    if (life < 1) {
                        remove();
                    } else if (pathAmount == GlobalVariables.path.size() - 1) {
                        GlobalVariables.enemiesLeaked++;
                        GlobalVariables.lives -= life;
                        remove();
                    }
                }
            }
        }
    }
    public void remove(){
        if (!zombie){
            // remove all sources of this
            GlobalVariables.enemies.remove(this);
            if (GlobalVariables.enemies.size() == 0){
                GlobalVariables.enemiesLeft = false;
                GlobalVariables.screen.remove(this);
                return;
            }
        }
        if (bombed){
            //make sure to set off a bomb if you have one
            GlobalVariables.projectiles.add(new GProjectile(bombDmg,this));
        }
        if (zombie){
            GlobalVariables.zombies.remove(this);
        }
        //check to see if you will become a zombie
        int health = 0;
        if (!zombie){
            for (GTile[] someTiles: GlobalVariables.tiles) {
                for (GTile tile: someTiles) {
                    if (tile.dice && tile.diceType == 11){
                        if (tile.zombieStuff(this) > health){
                            health = tile.zombieStuff(this);
                        }
                    }
                }
            }
        }
        if (health != 0){
            zombie = true;
            GlobalVariables.zombies.add(this);
            life = health;
        }
        else{
            GlobalVariables.screen.remove(this);
        }
    }
}
