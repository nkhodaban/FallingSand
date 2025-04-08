public class Dot {
    
    private double color[] = {0, 0, 100}; // Hue, Saturation, Brightness
    private int speed = 0;

    public Dot(){
        double color[] = {0, 0, 100}; // white
        this.color = color;
        this.speed = 0;
    }

    public Dot(double color[], int speed){
        this.color = color;
        this.speed = speed;
    }

    public double[] getColor(){
        return color;
    }

    public void setColor(double[] color){
        this.color[0] = color[0]%361;
        this.color[1] = color[1]%101;
        this.color[2] = color[2]%101;
    }

    public int getSpeed() {
        return speed;
    }

    public int getUpdateSpeed(){
        speed++;
        return speed;
    }

    public void setSpeed(int speed){
        this.speed = speed;
    }
}
