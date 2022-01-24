import acm.graphics.*;

import java.awt.*;

public class GApple extends GMovable {
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
        int lifeDiv = life;
        double scale = 1;
        while(lifeDiv > 100){
            scale*=1.25;
            lifeDiv/=100;
        }
        if (lifeDiv <= 10){
            Color v;
            if (lifeDiv<=1){
                v =Color.red;
            }
            else if (lifeDiv<=2){
                v = Color.blue;
            }
            else if (lifeDiv<=3){
                v = Color.green;
            }
            else if (lifeDiv<=4){
                v = Color.yellow;
            }
            else if (lifeDiv<=5){
                v = Color.magenta;
            }
            else if (lifeDiv<=6){
                v = Color.black;
            }
            else if (lifeDiv<=7){
                v = Color.white;
            }
            else if (lifeDiv<=8){
                v = Color.orange;
            }
            else if (lifeDiv<=9){
                v = new Color(95, 0, 255);
            }
            else {
                v = Color.cyan;
            }
            GOval o = new GOval(40,25);
            o.setFillColor(v);
            o.setFilled(true);
            GLine l = new GLine(0,0,0,-15);
            add(l);
            add(o,-20,-12.5);
        }
        else if (lifeDiv <= 30){
            Color c = Color.white;
            Color v = Color.black;
            if (lifeDiv <= 20){
                c = Color.black;
                v = Color.white;
            }
            GOval o = new GOval(40,25);
            o.setFillColor(c);
            o.setFilled(true);
            GLine l = new GLine(0,0,0,-15);
            l.setColor(c);
            add(l);
            add(o,-20,-12.5);
            for (int i = 0; i < 4; i++) {
                GLine r = new GLine(-15,i*6.25-10,15,i*6.25-10);
                r.setColor(v);
                add(r);
            }
        }
        else if (lifeDiv <= 50){
            GOval o = new GOval(40,25);
            o.setFillColor(Color.gray);
            o.setFilled(true);
            GLine l = new GLine(0,0,0,-15);
            l.setColor(Color.gray);
            add(l);
            add(o,-20,-12.5);
            Color[] c = new Color[]{Color.red,Color.orange,Color.yellow,Color.green,Color.blue,new Color(95,0,255),Color.magenta};
            for (int i = 0; i < c.length; i++) {
                GLine r = new GLine(-15,i*3.125-10,15,i*3.125-10);
                r.setColor(c[i]);
                add(r);
            }
        }
        else{
            GOval o = new GOval(40,25);
            o.setFillColor(Color.red);
            o.setFilled(true);
            add(o,-20,-12.5);
            GPolygon p = new GPolygon();
            p.addVertex(5,0);
            p.addVertex(15,0);
            p.addVertex(20,10);
            p.addVertex(15,20);
            p.addVertex(5,20);
            p.addVertex(0,10);
            add(p,p.getWidth()/-2,p.getHeight()/-2);
            Color[] c = new Color[]{Color.red,Color.yellow,Color.green,Color.blue,Color.cyan};
            for (int i = 0; i < c.length; i++) {
                GOval g = new GOval(5,5);
                g.setFilled(true);
                g.setFillColor(c[i]);
                if (i == 2){
                   add(g,g.getWidth()/-2,g.getHeight()/-2);
                }
                else {
                    add(g, (i / 2) * 5 - g.getWidth(), (i % 2) * 5 - g.getHeight());
                }
            }
        }
        if (frozen > 0){
            GOval o = new GOval(40,25);
            o.setFillColor(new Color(0, 255, 196, 92));
            o.setFilled(true);
            add(o,-20,-12.5);
        }
        if (fire > 0){
            GOval o = new GOval(40,25);
            o.setFillColor(new Color(255, 0, 0, 92));
            o.setFilled(true);
            add(o,-20,-12.5);
        }
        scale(scale);
    }
    public void tick(){
        configureImage();
        if(frozen > 1){
            frozen--;
        }
        else{
            GTile next = GlobalVariables.path.get(pathAmount+1);
            moveTowards(next.getX()+next.getWidth()/2,next.getY()+next.getHeight()/2,speed);
            pathLeft-=speed;
            if (pathLeft < 1){
                pathLeft = 50;
                pathAmount++;
                if (fire > 0){
                    fire--;
                    life-=fireDmg;
                    if (fire < 1){
                        fireDmg = 0;
                    }
                }
                if (life < 1){
                    remove();
                }
                else if (pathAmount == GlobalVariables.path.size()-1){
                    GlobalVariables.lives-=life;
                    remove();
                }
            }
        }
    }
    public void remove(){
        GlobalVariables.enemies.remove(this);
        GlobalVariables.screen.remove(this);
        if (GlobalVariables.enemies.size() == 0){
            GlobalVariables.enemiesLeft = false;
        }
    }
}
