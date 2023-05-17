import acm.graphics.GCompound;

import static java.lang.Math.abs;

public class GMovable extends GCompound {
    double xAmount;
    double yAmount;
    private void calculateDirection(double x, double y) {
        //proportinate the x and y amount to where you want to go
        double xDifference = x-getX();
        double yDifference = y-getY();
        xAmount = xDifference/(abs(xDifference)+abs(yDifference));
        yAmount = yDifference/(abs(xDifference)+abs(yDifference));
    }
    /**move towards the specified destination */
    public void moveTowards(double x, double y, double amount){
        calculateDirection(x,y);
        moveSteps(amount);
    }
    /**move straight in the direction you are going*/
    public void moveSteps(double amount){
        setLocation(getX()+(xAmount*amount),getY()+(yAmount*amount));
    }
}
