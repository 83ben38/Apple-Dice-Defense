import acm.graphics.*;
import svu.csc213.Dialog;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class GTile extends GCompound {
    ArrayList<GProjectile> projectiles = new ArrayList<>();
    ArrayList<GTile> nextTo;
    GLabel l;
    GOval range;
    GPolygon g = new GPolygon();
    ArrayList<GOval> o =  new ArrayList<>();
    ArrayList<GPolygon> stuff = new ArrayList<>();
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
                    range.sendToFront();
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
    }
    public GTile(int lvl){
        //make a random dice type from selected ones
        diceType = GlobalVariables.v[GlobalVariables.randomInt(0,9)];
        diceAmount = lvl;
    }
    public GTile(int size, int x, int y, int diceType){
        this(size,x,y);
        setDice(diceType,1);
    }
    public void setPath(){
        path = true;
        g.setFillColor(Color.MAGENTA);
        range = null;
    }
    public void setEmpty(){
        if (range != null) {
            GlobalVariables.screen.remove(range);
        }
        range = null;
        dice = false;
        path = false;
        while (stuff.size() > 0){
            remove(stuff.get(0));
            stuff.remove(0);
        }
        while(o.size() > 0){
            remove(o.get(0));
            o.remove(0);
        }
        g.setFillColor(new Color(0,125,0));
        g.setColor(Color.black);
        while (projectiles.size() > 0){
            projectiles.get(0).remove();
        }
    }
    public int getAttackSpeed(){
        //the attack speed in ticks of the level one tower
        if(diceType == 1 || diceType == 3 || diceType == 4 || diceType == 8 || diceType == 14){
            return 192;
        }
        else if (diceType == 6){
            return 4;
        }
        else if (diceType == 9){
            return 6;
        }
        else if (diceType == 13){
            return 24;
        }
        else if (diceType == 16){
            //red and green bonus give attack speed
            if (getThings()[0]&&getThings()[3]){
                return 48;
            }
            else if (getThings()[0]||getThings()[3]){
                return 96;
            }
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
        if (diceType == 16){
            configureRainbowDice();
        }
        ticksLeft = getAttackSpeed();
        Color v;
        //get the Color of the dice
        switch (diceType) {
            case 1 -> v = Color.green;
            case 2 -> v = Color.red;
            case 3 -> v = Color.blue;
            case 4 -> v = Color.yellow;
            case 6 -> v = new Color(150, 75, 255);
            case 7 -> v = new Color(50, 0, 125);
            case 8 -> v = new Color(75, 30, 20);
            case 9 -> v = Color.orange;
            case 10 -> v = new Color(150, 255, 150);
            case 11 -> v = new Color(125, 150, 75);
            case 12 -> v = Color.black;
            case 13 -> v = new Color(125, 0, 0);
            case 14 -> v = Color.pink;
            case 15 -> v = Color.magenta;
            case 16 -> v = Color.white;
            case 17 -> v = new Color(25, 100, 125);
            case 18 -> v = new Color(162, 255, 0, 255);
            case 19 -> v = new Color(175, 0, 100);
            case 20 -> v = new Color(75, 150, 255);
            default -> v = Color.cyan;
        }
        //add the pips
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
        //add the range
        int rangeAmt = getRange();
        range = new GOval((rangeAmt*100)+50,(rangeAmt*100)+50);
        range.setFillColor(new Color(0, 250, 200, 75));
        range.setFilled(true);
        GlobalVariables.screen.add(range,g.getWidth()/2-range.getWidth()/2+GlobalVariables.getPositionOf(x,y).getX(),g.getHeight()/2-range.getHeight()/2+GlobalVariables.getPositionOf(x,y).getY());
        range.setVisible(false);
    }
    public void configureRainbowDice(){
        //check for adjacent rainbow dice
        while (stuff.size() > 0){
            remove(stuff.get(0));
            stuff.remove(0);
        }
        GPoint[] i = new GPoint[]{new GPoint(12.5,0),new GPoint(37.5,0),new GPoint(50,25),new GPoint(37.5,50),new GPoint(12.5,50),new GPoint(0,25),new GPoint(12.5,0)};
        Color[] c = new Color[]{Color.red,Color.orange,Color.yellow,Color.green,Color.blue,Color.magenta};
        for (int j = 0; j < i.length-1; j++) {
            GPolygon p = new GPolygon();
            p.addVertex(i[j].getX(),i[j].getY());
            p.addVertex(i[j+1].getX(),i[j+1].getY());
            p.addVertex(((i[j+1].getX()-25)*0.8)+25,((i[j+1].getY()-25)*0.8)+25);
            p.addVertex(((i[j].getX()-25)*0.8)+25,((i[j].getY()-25)*0.8)+25);
            if (getThings()[j]){
                //light up the polygon if there is a dice there
                p.setFilled(true);
                p.setFillColor(c[j]);
            }
            p.setColor(c[j]);
            add(p);
            stuff.add(p);
            p.sendToBack();
        }
        g.sendToBack();
    }
    public boolean[] getThings(){
        //check each adjacent hex for a rainbow dice
        boolean[] f = new boolean[]{false,false,false,false,false,false};
        if (GlobalVariables.tiles[0][0] != null) {
            if (x % 2 == 0) {
                if (y  > 0){
                    f[0] = GlobalVariables.tiles[x][y - 1].diceType == 16;
                    if (x < GlobalVariables.tiles.length-1){
                        f[1] = GlobalVariables.tiles[x+1][y - 1].diceType == 16;
                    }
                    if (x > 0){
                        f[5] = GlobalVariables.tiles[x-1][y - 1].diceType == 16;
                    }
                }
                if (y < GlobalVariables.tiles[0].length-1){
                    f[3] = GlobalVariables.tiles[x][y + 1].diceType == 16;
                }
                if (x > 0){
                    f[4] = GlobalVariables.tiles[x - 1][y].diceType == 16;
                }
                if (x < GlobalVariables.tiles.length-1){
                    f[2] = GlobalVariables.tiles[x + 1][y].diceType == 16;
                }
            } else {
                if (y  < GlobalVariables.tiles[0].length-1){
                    f[3] = GlobalVariables.tiles[x][y + 1].diceType == 16;
                    if (x < GlobalVariables.tiles.length-1){
                        f[2] = GlobalVariables.tiles[x+1][y + 1].diceType == 16;
                    }
                    f[4] = GlobalVariables.tiles[x-1][y + 1].diceType == 16;
                }
                if (y > 0){
                    f[0] = GlobalVariables.tiles[x][y-1].diceType == 16;
                }
                if (x > 0){
                    f[5] = GlobalVariables.tiles[x - 1][y].diceType == 16;
                }
                if (x < GlobalVariables.tiles.length-1){
                    f[1] = GlobalVariables.tiles[x + 1][y].diceType == 16;
                }
            }
        }
        return f;
    }
    private void mouseClicked() throws CloneNotSupportedException {
        if(GlobalVariables.starting){
            //make a path if the game has not started yet
            makePath();
        }
        else if (GlobalVariables.question && dice){
            String[] t = new String[]{"Green dice shoots in all six directions.","Fire dice sets enemies on fire.","Water dice has exploding bullets","Electric dice does high damage and chains from enemy to enemy.", "Ice dice freezes enemies and makes them more vulnerable.","Growth dice grows over time.","Orbit dice has a projectile orbit it.","Multi-projectile dice shoots multiple projectiles.","Buff dice makes other dice in range shoot faster.","Trapper dice puts traps on the track","Zombie dice turns dead enemies into zombies.","Bomb dice bombs enemies with bombs that blow up when they die.","Flamethrower dice shoots really quickly but bullets cannot leave its range.","Anti-pierce dice antipierces enemies so they dont take pierce.","Effect amplifier dice multiplies ice vulnerability, fire damage, and bomb damage.","Rainbow dice gets stats buffs(faster attack speed, more damage, etc.) for each rainbow dice in the adjacent hexes.","Wildcard dice can turn into any other dice by clicking on it with the dice picked up.","Combiner dice can combine with any other dice.","Randomizer dice can combine with any other dice and randomize it.","Score dice gives you score at the end of each round."};
            Dialog.showMessage(t[diceType-1]);
        }
        else{
            //if nothing is being held, pick up
            if (GlobalVariables.pickedUp == null && dice){
                GlobalVariables.pickedUp = (GTile)this.clone();
                this.diceType = 0;
                setEmpty();
            }
            else if (GlobalVariables.pickedUp == null){
                /*nothing*/
            }
            // if empty and a dice is being held, drop it
            else if (!dice && !path){
                setDice(GlobalVariables.pickedUp.diceType, GlobalVariables.pickedUp.diceAmount);
                GlobalVariables.pickedUp = null;
                if (diceType == 9){
                    nextTo = getNextTo();
                }
            }
            // if you match the picked up dice, merge the two
            else if (dice && (GlobalVariables.pickedUp.diceType == diceType || GlobalVariables.pickedUp.diceType == 18) && GlobalVariables.pickedUp.diceAmount == diceAmount && diceAmount < 6){
                GlobalVariables.pickedUp = null;
                diceAmount++;
                setEmpty();
                setDice(diceType,diceAmount);
            }
            else if (dice && GlobalVariables.pickedUp.diceType == 19 && GlobalVariables.pickedUp.diceAmount == diceAmount && diceAmount < 6) {
                GlobalVariables.pickedUp = null;
                diceAmount++;
                setEmpty();
                diceType = GlobalVariables.v[GlobalVariables.randomInt(0,9)];
                setDice(diceType,diceAmount);
            }
            // if you are wild dice, transform into the held dice
            else if (dice && diceType == 17 && diceAmount == GlobalVariables.pickedUp.diceAmount){
                this.diceType = GlobalVariables.pickedUp.diceType;
                GlobalVariables.screen.remove(range);
                configureDice();
            }
        }
    }
    public void tick(){
        if (diceType == 16){
            //rainbow dice check for surrounding rainbow dice
            configureRainbowDice();
        }
        //any dice that are not these dice type shoot when they run out of ticks left
        if (dice && diceType != 6 && diceType != 7 && diceType != 11 && diceType != 17 && diceType != 18 && diceType != 19 && diceType != 20){
            ticksLeft-=diceAmount;
            if (ticksLeft < 1){
                fire();
            }
        }
        //type seven makes sure to have the orbit projectiles around it
        else if (dice && diceType == 7){
            if (projectiles.size() < 1){
                projectiles.add(new GProjectile(diceAmount,this,false));
            }
            if (projectiles.size() < 2 && diceAmount > 4){
                projectiles.add(new GProjectile(diceAmount,this,true));
            }
            for (GProjectile p: projectiles) {
                p.tick();
            }
        }
        // if you have the life count, make sure to set it
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
    public int getRange(){
        int k;
        switch(diceType){
            case 1, 6, 17, 18, 19, 20 -> {
                return 0;
            }
            case 2, 3, 10, 12, 15 -> k = 2;
            case 4,8,11,14 -> k = 3;
            case 16 ->{
                //yellow bonus gives range
                if (getThings()[2]){
                    k = 3;
                }
                else{
                    k = 2;
                }
            }
            default -> k = 1;
        }
        if (diceAmount > 4){
            k++;
        }
        return k;
    }
    public void fire(){
        //any dice type that is not these shoots if there is anything in range and then resets its ticks left
        if (diceType != 1 && diceType != 8 && diceType != 9 && diceType != 10 && diceType != 12) {
            int number = -1;
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                if (calculateDistance(GlobalVariables.enemies.get(i).getX(), GlobalVariables.enemies.get(i).getY()) < (getRange()+0.5) * 50) {
                    number = i;
                    break;
                }
            }
            if (number > -1) {
                ticksLeft = getAttackSpeed();
                projectiles.add(new GProjectile(diceType, diceAmount, GlobalVariables.enemies.get(number), this));
            }
        }
        // type 8 shoots the number of pips it has
        else if (diceType == 8){
            int number = diceAmount;
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                if (calculateDistance(GlobalVariables.enemies.get(i).getX(), GlobalVariables.enemies.get(i).getY()) < (getRange()+0.5) * 50) {
                    number--;
                    projectiles.add(new GProjectile(diceType, diceAmount, GlobalVariables.enemies.get(i), this));
                    if (number < 1){
                        break;
                    }
                }
            }
            if (number < diceAmount) {
                ticksLeft = getAttackSpeed();
            }
        }
        // type 9 ticks all surrounding dice
        else if (diceType == 9){
            nextTo = getNextTo();
            for (GTile t: nextTo) {
                if (t.dice && t.diceType != 9){
                    t.tick();
                }
            }
            ticksLeft = getAttackSpeed();
        }
        // type 10 shoots trapper projectiles
        else if (diceType == 10){
            nextTo = getPathInRange();
            if (nextTo.size() > 0) {
                projectiles.add(new GProjectile(diceAmount, nextTo.get(GlobalVariables.randomInt(0, nextTo.size() - 1)), this));
                ticksLeft = getAttackSpeed();
            }
        }
        // type 12
        else if (diceType == 12){
            int number = -1;
            for (int i = 0; i < GlobalVariables.enemies.size(); i++) {
                if (calculateDistance(GlobalVariables.enemies.get(i).getX(), GlobalVariables.enemies.get(i).getY()) < (getRange()+0.5) * 50 && !GlobalVariables.enemies.get(i).bombed) {
                    number = i;
                    break;
                }
            }
            if (number > -1) {
                ticksLeft = getAttackSpeed();
                projectiles.add(new GProjectile(diceType, diceAmount, GlobalVariables.enemies.get(number), this));
            }
        }
        // type 1 shoot is all 6 directions
        else{
            for (int i = 0; i < 6; i++) {
                ticksLeft = getAttackSpeed();
                projectiles.add(new GProjectile(diceAmount, i, this));
            }
        }
    }
    public void turnPassed(){
        if (diceType == 20){
            //score dice
            GlobalVariables.score+=Math.pow(2,diceAmount)*100;
        }
        else {
            //growth dice
            ticksLeft--;
            if (ticksLeft == 0) {
                setEmpty();
                setDice(GlobalVariables.v[GlobalVariables.randomInt(0, 9)], diceAmount + 1);
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
    private ArrayList<GTile> getNextTo(){
        //get all dice within range
        ArrayList<GTile> x = new ArrayList<>();
        for (GTile[] t : GlobalVariables.tiles) {
            for (GTile c : t) {
                if (c != null) {
                    if (calculateDistance(c.getX(), c.getY()) < (getRange()*50)+25 && c.dice) {
                        x.add(c);
                    }
                }
            }
        }
        return x;
    }
    public ArrayList<GTile> getAround(){
        //get all tiles next to this
        ArrayList<GTile> x = new ArrayList<>();
        for (GTile[] t : GlobalVariables.tiles) {
            for (GTile c : t) {
                if (c != null) {
                    if (GlobalVariables.isNextTo(this.x,y,c.x,c.y)) {
                        x.add(c);
                    }
                }
            }
        }
        return x;
    }
    private ArrayList<GTile> getPathInRange(){
        //get all paths within range
        ArrayList<GTile> x = new ArrayList<>();
        for (GTile[] t : GlobalVariables.tiles) {
            for (GTile c : t) {
                if (c != null) {
                    if (calculateDistance(c.getX(), c.getY()) < (getRange()*50)+25 && c.path) {
                        x.add(c);
                    }
                }
            }
        }
        return x;
    }
    public int zombieStuff(GApple p){
        if (calculateDistance(p.getX(), p.getY()) < (getRange()*50)+25) {
            return diceAmount*diceAmount*3;
        }
        return 0;
    }
    public void makePath(){
        // if no paths have been made and this is at the beginning
        if (GlobalVariables.current == null && x == 0){
            GlobalVariables.current = new int[]{x, y};
            setPath();
            GlobalVariables.path.add(this);
        }
        else if (GlobalVariables.current == null){

        }
        // if this is next to the last path
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
}
