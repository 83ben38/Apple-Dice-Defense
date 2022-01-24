import acm.graphics.GPoint;
import acm.program.GraphicsProgram;
import svu.csc213.Dialog;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ADD extends GraphicsProgram {
    public static void main(String[] args) {
        new ADD().start();
    }
    public void run(){
        GlobalVariables.screen = this;
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                GTile a = new GTile(50,i,j);
                GlobalVariables.tiles[i][j] = a;
                addToScreen(a);
            }
        }
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
        JButton waveStarter = new JButton("Start wave");
        waveStarter.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!(GlobalVariables.wave || GlobalVariables.enemiesLeft )){
                    if (GlobalVariables.waveN == GlobalVariables.waveData.length){
                        Dialog.showMessage("You win!");
                        if (!Dialog.getYesOrNo("Would you like to continue playing?")) {
                            exit();
                        }
                    }
                    else {
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
        getADice();
        JButton fastForward = new JButton("Fast forward");
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
    }
    public void getADice(){
        GlobalVariables.pickedUp = new GTile((int) Math.sqrt(GlobalVariables.randomInt(1, GlobalVariables.waveN)));
    }

}
