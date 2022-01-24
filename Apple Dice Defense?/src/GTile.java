import acm.graphics.GCompound;
import acm.graphics.GLabel;
import acm.graphics.GOval;
import acm.graphics.GPolygon;
import svu.csc213.Dialog;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class GTile extends GCompound {
    GLabel l;
    GOval range;
    GPolygon g = new GPolygon();
    ArrayList<GOval> o =  new ArrayList<>();
    boolean path = false;
    boolean dice = false;
    int diceType;
    int diceAmount;
    int size;
    int x;
    int y;
    int ticksLeft;
    public GTile(int size, int x, int y){
        this.size = size;
        this.x = x;
        this.y = y;
        g.addVertex((double)size*1/4,0);
        g.addVertex((double)size*3/4,0);
        g.addVertex(size,(double)size/2);
        g.addVertex((double)size*3/4,size);
        g.addVertex((double)size*1/4,size);
        g.addVertex(0,(double)size/2);
        add(g);
        g.setFillColor(new Color(0,125,0));
        g.setFilled(true);
        MouseListener m = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    GTile.this.mouseClicked();
                } catch (CloneNotSupportedException ex) {
                    ex.printStackTrace();
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
                if (range != null) {
                    range.setVisible(true);
                    sendToFront();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (range != null) {
                    range.setVisible(false);
                    sendToBack();
                }
            }
        };
        this.addMouseListener(m);
        ticksLeft = getAttackSpeed();
    }
    public GTile(int lvl){
        diceType = GlobalVariables.randomInt(1,5);
        diceAmount = lvl;
    }
    public void setPath(){
        path = true;
        g.setFillColor(Color.MAGENTA);
        range = null;
    }
    public void setEmpty(){
        remove(range);
        range = null;
        dice = false;
        path = false;
        while(o.size() > 0){
            remove(o.get(0));
            o.remove(0);
        }
        g.setFillColor(new Color(0,125,0));
        g.setColor(Color.black);
    }
    public int getAttackSpeed(){
        if(diceType == 1 || diceType == 3 || diceType == 4){
            return 192;
        }
        else{
            return 96;
        }
    }
    public void setDice(int type, int amount){
        dice = true;
        diceType = type;
        diceAmount = amount;
        configureDice();
    }
    private void configureDice(){
        Color v;
        switch (diceType) {
            case 1 -> v = Color.green;
            case 2 -> v = Color.red;
            case 3 -> v = Color.blue;
            case 4 -> v = Color.yellow;
            default -> v = Color.cyan;
        }
        if (diceAmount%2==1){
            o.add(new GOval((double)size/2-(double)size/14,(double)size/2-(double)size/14,(double)size/7,(double)size/7));
        }
        switch(diceAmount/2){
            case 3:
                o.add(new GOval((double)size/4-(double)size/14,(double)size/2-(double)size/14,(double)size/7,(double)size/7));
                o.add(new GOval((double)size*3/4-(double)size/14,(double)size/2-(double)size/14,(double)size/7,(double)size/7));
            case 2:
                o.add(new GOval((double)size/4-(double)size/14,(double)size/4-(double)size/14,(double)size/7,(double)size/7));
                o.add(new GOval((double)size*3/4-(double)size/14,(double)size*3/4-(double)size/14,(double)size/7,(double)size/7));
            case 1:
                o.add(new GOval((double)size/4-(double)size/14,(double)size*3/4-(double)size/14,(double)size/7,(double)size/7));
                o.add(new GOval((double)size*3/4-(double)size/14,(double)size/4-(double)size/14,(double)size/7,(double)size/7));
        }
        for (GOval z: o) {
            z.setFillColor(v);
            z.setFilled(true);
            add(z);
        }
        g.setColor(v);
        int rangeAmt = getRange();
        range = new GOval((rangeAmt*100)+50,(rangeAmt*100)+50);
        range.setFillColor(new Color(0, 250, 200, 75));
        range.setFilled(true);
        add(range,g.getWidth()/2-range.getWidth()/2,g.getHeight()/2-range.getHeight()/2);
        range.setVisible(false);
    }
    private void mouseClicked() throws CloneNotSupportedException {
        if(GlobalVariables.starting){
            if (GlobalVariables.current == null && x == 0){
                GlobalVariables.current = new int[]{x, y};
                setPath();
                GlobalVariables.path.add(this);
            }
            else if (GlobalVariables.current == null){

            }
            else if (GlobalVariables.isNextTo(x,y, GlobalVariables.current[0], GlobalVariables.current[1]) && !path){
                GlobalVariables.current = new int[]{x, y};
                setPath();
                GlobalVariables.path.add(this);
                if (x == 19){
                    GlobalVariables.starting = false;
                    GlobalVariables.screen.readyToGo();
                    l = new GLabel("Lives: " + GlobalVariables.lives);
                    add(l,getWidth()/2-l.getWidth()/2,getHeight()/2-l.getHeight()/2);
                }
            }
        }
        else{
            if (GlobalVariables.pickedUp == null && dice){
                GlobalVariables.pickedUp = (GTile)this.clone();
                setEmpty();
            }
            else if (GlobalVariables.pickedUp == null){
                /*nothing*/
            }
            else if (!dice && !path){
                setDice(GlobalVariables.pickedUp.diceType, GlobalVariables.pickedUp.diceAmount);
                GlobalVariables.pickedUp = null;
            }
            else if (dice && GlobalVariables.pickedUp.diceType == diceType && GlobalVariables.pickedUp.diceAmount == diceAmount && diceAmount < 6){
                GlobalVariables.pickedUp = null;
                diceAmount++;
            }
        }
    }
    public void tick(){
        if (dice){
            ticksLeft-=diceAmount;
            if (ticksLeft < 1){
                ticksLeft = getAttackSpeed();
                fire();
            }
        }
        else if (l != null){
            l.setLabel("Lives: " + GlobalVariables.lives);
            if (GlobalVariables.lives < 1){
                Dialog.showMessage("You lose!");
                GlobalVariables.screen.exit();
            }
        }
    }
    public double calculateDistance(double x, double y){
        return Math.sqrt(Math.pow(x-getX(),2)+Math.pow(y-getY(),2));
    }
    private int getRange(){
        switch(diceType){
            case 1 -> {
                return 0;
            }
            case 2, 3 -> {
                return 2;
            }
            case 4 -> {
                return 3;
            }
            default -> {
                return 1;
            }
        }
    }
    public void fire(){
        if (diceType != 1) {
            int number = -1;
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                if (calculateDistance(GlobalVariables.enemies.get(i).getX(), GlobalVariables.enemies.get(i).getY()) < getRange() * 50) {
                    number = i;
                    break;
                }
            }
            if (number > -1) {
                new GProjectile(diceType, diceAmount, GlobalVariables.enemies.get(number), this);
            }
        }
        else{
            for (int i = 0; i < 6; i++) {
                new GProjectile(diceAmount, i, this);
            }
        }
    }
    @Override
    public double getWidth(){
        return g.getWidth();
    }
    public double getHeight(){
        return g.getHeight();
    }
}
