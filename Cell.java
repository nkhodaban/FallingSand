public class Cell {
    private boolean state;
    private Dot payload;

    public Cell(){
        double[] color = {0, 0, 100};
        this.state = false;
        this.payload = new Dot(color, 0);
    }

    public Cell(boolean state, double[] color, int speed){
        this.state = state;
        this.payload = new Dot(color, speed);
    }

    public void copyCell(Cell cell){
        this.state = cell.getState();
        this.setPayload(cell.getPayload());
    }

    public boolean getState(){
        return this.state;
    }

    public void setState(boolean state){
        this.state = state;
    }

    public Dot getPayload(){
        return payload;
    }

    public void setPayload(Dot payload){
        this.payload.setColor(payload.getColor());
        this.payload.setSpeed(payload.getSpeed());
    }

    public void reset(){
        this.state = false;
        double[] color = {0, 0, 0};
        payload.setColor(color);
        payload.setSpeed(0);
    }

}

