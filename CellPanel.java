import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JPanel;

public class CellPanel extends JPanel {

    private Cell[][] grid;
    private static int row;
    private static int col;
    private static int size;

    private static boolean gridEnabled = false;

    private static double[] color = {0, 0, 100};
    private static String colorMode = "Rainbow";
    private static float colorSpeed = 0.5f;
    private static double colorTimeDune;
    private static boolean colorEnabled = false;

    private static int brushRad = 7;
    private static float brushDensity = 0.5f;
    private static boolean brushEnabled = false;

    private static boolean eEEnabled = false;

    private final Random rand = new Random();

    /** Constructor determines size of JPanel according to received arguments
     * and adds mouse listener to that panel (click on cell changes it's state)
     */
    public CellPanel(Cell[][] grid, int row, int col, int size) {

        this.grid = grid;
        CellPanel.row = row;
        CellPanel.col = col;
        CellPanel.size = size;

        setPreferredSize(new Dimension(col * size, row * size));

        // Listener for cells
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent event) {
                if(!eEEnabled) draw(event);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                if(eETrigger(event)){
                    eEEnabled = true;
                    eEPlay(".\\\\Sound\\dune-scream.wav");
                    eEEnabled = false;
                }
                else if(!eEEnabled) draw(event);
            }
        });

    }

    private void draw(MouseEvent e)
    {
        // Finding on which cell user pressed
        int row = e.getY() / size;
        int col = e.getX() / size;
        if(row >= 0 && row < CellPanel.row && col >= 0 && col < CellPanel.col){
            incrementColor();
            // Drawing on that cell with a circle brush
            if(brushEnabled){
                for(int i = -brushRad; i < brushRad + 1; i++){
                    for(int j = -brushRad; j < brushRad + 1; j++){
                        if(row + i >= 0 && row + i < CellPanel.row && col + j >= 0 && col + j < CellPanel.col){
                            Cell cell = grid[row + i][col + j];
                            // Skip dot if it's outside of the circle and brushDensity is not met
                            if(rand.nextFloat() > brushDensity || !isInCircle(i, j, brushRad) || cell.getState()) continue;
                            cell.setState(true);
                            cell.getPayload().setColor(color);
                        }
                    }
                }
            }
            // Drawing on that cell with a single dot
            else {
                Cell cell = grid[row][col];
                if(!cell.getState()) {
                    cell.setState(true);
                    cell.getPayload().setColor(color);
                }
            }
            repaint();
        }
    }

    private boolean eETrigger(MouseEvent event)
    {
        if(brushEnabled && brushRad == 10 && brushDensity == 1f){
            int row = event.getY() / size;
            int col = event.getX() / size;
            if(row >= 0 && row < CellPanel.row && col >= 0 && col < CellPanel.col){
                for(int i = -brushRad; i < brushRad + 1; i++){
                    for(int j = -brushRad; j < brushRad + 1; j++){
                        if(row + i >= 0 && row + i < CellPanel.row && col + j >= 0 && col + j < CellPanel.col){
                            Cell cell = grid[row + i][col + j];
                            if(!isInCircle(i, j, brushRad)) continue;
                            if(!cell.getState()) return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void eEPlay(String filePath)
    {
        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) { 
                System.err.println("Wave file not found: " + filePath);
                return;
            } 

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine auline = (SourceDataLine) AudioSystem.getLine(info);
            auline.open(format);
            auline.start();

            int nBytesRead = 0;
            byte[] abData = new byte[524288];
            while (nBytesRead != -1) {
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0) {
                    auline.write(abData, 0, nBytesRead);
                }
            }

            auline.drain();
            auline.close();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static boolean getEEEnabled() {
        return eEEnabled;
    }

    public static void switchOptions(String option){
        switch (option) {
            case "GRID":
                gridEnabled = !gridEnabled;
                break;
            case "COLOR":
                colorEnabled = !colorEnabled;
                break;
            case "BRUSH":
                brushEnabled = !brushEnabled;
                break;
            default:
                break;
        }
    }

    public static boolean getBrushEnabled() {
        return brushEnabled;
    }

    public static void setBrushSize(int size){
        brushRad = size;
    }

    public static void setBrushDensity(Float density) {
        brushDensity = density;
    }

    public static boolean getColorEnabled() {
        return colorEnabled;
    }

    public static void setColor(String color){
        switch (color) {
            case "Rainbow":
                colorMode = "Rainbow";
                break;
            case "Dune":
                colorMode = "Dune";
                break;
            default:
                colorMode = "White";
                break;
        }
    }

    private static void incrementColor(){
        if(colorEnabled)
        {
            switch (colorMode) {
                case "Rainbow":
                    color[0] = (color[0] + colorSpeed) % 361;
                    color[1] = 100;
                    color[2] = 100;
                    break;
                case "Dune":
                    colorTimeDune++;
                    color[0] = 10 * Math.sin(colorTimeDune * colorSpeed/10) + 40;
                    color[1] = 10 * Math.sin(colorTimeDune * colorSpeed/10) + 70;
                    color[2] = 7 * Math.sin(colorTimeDune * colorSpeed/10) + 82;
                    break;
                default:
                    color[0] = 0;
                    color[1] = 0;
                    color[2] = 100;
                    break;
            }
        }
        else {
            color[0] = 0;
            color[1] = 0;
            color[2] = 100;
        }
    }

    private boolean isInCircle(int i, int j, int rad)
    {
        double distance = Math.pow((i), 2) + Math.pow((j), 2);
        return distance <= Math.pow(rad, 2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){

                int y = i * size;
                int x = j * size;

                boolean state = grid[i][j].getState();
                double[] color = grid[i][j].getPayload().getColor();
                float[] color_f = new float[3];
                for(int k = 0; k < 3; k++){
                    color_f[k] = (float)color[k];
                }

                // Setting colors of each cell
                if(colorEnabled){
                    g.setColor(state ? Color.getHSBColor(color_f[0]/360, color_f[1]/100, color_f[2]/100) : Color.BLACK);
                }
                else{
                    g.setColor(state ? Color.WHITE : Color.BLACK);
                }
                g.fillRect(x, y, size, size);

                if(gridEnabled){
                    // Creating "outline effect" for each cell
                    g.setColor(Color.GRAY);
                    g.drawRect(x, y, size, size);
                }
            }
        }
    }

}