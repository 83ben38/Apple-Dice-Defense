import acm.graphics.GCompound;

import static java.lang.Math.abs;

public class GMovable extends GCompound {
    double xAmount;
    double yAmount;

    private void calculateDirection(double x, double y) {
        double xDifference = x-getX();
        double yDifference = y-getY();
        xAmount = xDifference/(abs(xDifference)+abs(yDifference));
        yAmount = yDifference/(abs(xDifference)+abs(yDifference));
    }
    public void moveTowards(double x, double y, double amount){
        calculateDirection(x,y);
        moveSteps(amount);
    }
    public void moveSteps(double amount){
        setLocation(getX()+(xAmount*amount),getY()+(yAmount*amount));
    }
}
