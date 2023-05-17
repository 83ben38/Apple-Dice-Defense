import acm.graphics.GPoint;
import acm.program.GraphicsProgram;
import svu.csc213.Dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ADD extends GraphicsProgram {
    //wave starter / go button
    JButton waveStarter;
    //random map / fast forward button
    JButton fastForward;
    public static void main(String[] args) {
        try {
            //try to make a save file and get five dice
            if (new File(new File(".").getCanonicalPath() + "/saveFile.txt").createNewFile()){
                FileWriter w = new FileWriter(new File(".").getCanonicalPath() + "/saveFile.txt");
                w.write("0000000000000000000000000000");
                w.close();
                for (int i = 0; i < 5; i++) {
                    GlobalVariables.win();
                }
            }
        } catch (IOException ignored) {
        }

        ADD s = new ADD();
        s.start();
        GlobalVariables.screen = s;
        new Thread(GlobalVariables::exist).start();
    }
    public void run1(){
        //make a 20*10 hexagon square
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                GTile a = new GTile(50,i,j);
                GlobalVariables.tiles[i][j] = a;
                addToScreen(a);
            }
        }
        //add score labels
        GlobalVariables.scoreLabel.setFont(new Font(GlobalVariables.scoreLabel.getFont().getFontName(),Font.PLAIN,40));
        add(GlobalVariables.scoreLabel,0,GlobalVariables.scoreLabel.getHeight()/3*2);
        GlobalVariables.highScoreLabel.setFont(GlobalVariables.scoreLabel.getFont());
        add(GlobalVariables.highScoreLabel,GlobalVariables.scoreLabel.getWidth(),GlobalVariables.highScoreLabel.getHeight()/3*2);
        new Thread(GlobalVariables::addButton).start();
    }
    public void addToScreen(GTile t){
        add(t, GlobalVariables.getPositionOf(t.x,t.y));
    }
    public void addToScreen(GApple g){
        GPoint x = GlobalVariables.getPositionOf(GlobalVariables.path.get(0).x, GlobalVariables.path.get(0).y);
        add(g,x.getX()-g.getWidth()/2+ GlobalVariables.path.get(0).getWidth()/2,x.getY()-g.getHeight()/2+ GlobalVariables.path.get(0).getHeight()/2);
        g.sendToFront();
        repaint();
    }
    public void readyToGo(){
        // add the start wave button
        waveStarter.setText("Start wave");
        waveStarter.setVisible(true);
        waveStarter.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!(GlobalVariables.wave || GlobalVariables.enemiesLeft )){
                    if (GlobalVariables.waveN == GlobalVariables.waveData.length){
                        Dialog.showMessage("You win!");
                        GlobalVariables.win();
                        if (!Dialog.getYesOrNo("Would you like to continue playing?")) {
                            exit();
                        }
                        else{
                            new Thread(GlobalVariables::startWave).start();
                        }
                    }
                    else {
                        if (GlobalVariables.tutorial){
                            Dialog.showMessage("Your dice will shoot at the enemies in their range which can be seen by hovering over them.");
                            Dialog.showMessage("Click on the fast forward button to fast forward.");
                        }
                        new Thread(GlobalVariables::startWave).start();
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
        add(waveStarter,SOUTH);
        // you start with a die
        getADice();
        //add the fast forward button
        fastForward.removeMouseListener(fastForward.getMouseListeners()[0]);
        fastForward.setText("Fast forward");
        fastForward.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GlobalVariables.FF = !GlobalVariables.FF;
                if (GlobalVariables.FF){
                    fastForward.setText("Un-Fast forward");
                }
                else{
                    fastForward.setText("Fast forward");
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
        add(fastForward,SOUTH);
        if (GlobalVariables.tutorial){
            Dialog.showMessage("Before every wave you will get a dice. Click on any non-occupied space to place it.");
            Dialog.showMessage("Click start wave to start the wave.");
        }
    }
    public void getADice(){
        // tick all growth dice + gain score for score dice
        for (GTile[] someTiles: GlobalVariables.tiles) {
            for (GTile t: someTiles) {
                if (t.diceType == 6 || t.diceType == 20){
                    t.turnPassed();
                }
            }
        }
        // get a dice with a random value between 1 and the sqrt of the wave
        GlobalVariables.pickedUp = new GTile((int) Math.sqrt(GlobalVariables.randomInt(1, GlobalVariables.waveN)));
        //calculate score
        if ((1000-GlobalVariables.ticksTaken)/10 * (GlobalVariables.waveN-GlobalVariables.enemiesLeaked) > 0) {
            GlobalVariables.score += (1000 - GlobalVariables.ticksTaken)/10 * (GlobalVariables.waveN-GlobalVariables.enemiesLeaked);
        }
        if (GlobalVariables.score > GlobalVariables.highScore) {
            GlobalVariables.setHighScore(GlobalVariables.score);
        }
        //add dice score stuff
        GlobalVariables.scoreLabel.setLabel("Score: " + GlobalVariables.score);
        GlobalVariables.highScoreLabel.setLabel("High score: " + GlobalVariables.highScore);
        GlobalVariables.highScoreLabel.setLocation(GlobalVariables.scoreLabel.getWidth(),GlobalVariables.highScoreLabel.getY());
    }

}
