import java.awt.BorderLayout;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

public class ApplicationFrame extends JFrame {

    private static final int GAP = 5;
    private final CellPanel view;
    private final Random rand = new Random();
    private Cell[][] newGrid;

    public ApplicationFrame(Cell[][] grid,  int row, int col, int size){

        // Trying to change look of GUI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        setLayout(new BorderLayout(GAP, GAP));

        // Adding few panels on screen
        view = new CellPanel(grid, row, col, size);
        add(view, BorderLayout.NORTH);
        addButtons(grid);

        // Options for frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Creating new grid for falling sand
        newGrid = new Cell[row][col];
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                newGrid[i][j] = new Cell();
            }
        }

        // Running of the falling of sand
        while(true)
        {
            for(int i = 0; i < row; i++){
                for(int j = 0; j < col; j++){
                    int speed = grid[i][j].getPayload().getUpdateSpeed();
                    if(i + speed >= row) speed = row - i - 1;
                    boolean state = grid[i][j].getState();
                    boolean nextState = grid[i + speed][j].getState();
                    // Reduce speed until it arrives on top of another cell
                    while(speed > 0 && nextState){
                        speed--;
                        nextState = grid[i + speed][j].getState();
                    }
                    // Main logic of falling sand
                    if (i + 1 < row && state && !CellPanel.getEEEnabled()){
                        newGrid[i][j].copyCell(grid[i][j]);
                        // Falling down if nothing is below
                        if (!nextState)switchCells(newGrid[i][j], newGrid[i + speed][j]);
                        // If there is something below, try to move left or right to trickle down
                        else{
                            int downDir = (int) Math.pow(-1, (rand.nextInt(2) + 1));
                            if (j + downDir >= 0 && j + downDir < col && !grid[i + 1][j + downDir].getState()){
                                switchCells(newGrid[i][j], newGrid[i + 1][j + downDir]);
                            }
                        }
                    }
                }
            }
            // Copy new grid to old grid
            for(int i = 0; i < row; i++){
                for(int j = 0; j < col; j++){
                    grid[i][j].copyCell(newGrid[i][j]);
                }
            }
            // Wait so that user can see the falling of sand
            try {
                TimeUnit.MILLISECONDS.sleep(40);
            } catch (Exception ignored) {}
            view.repaint();
        }
    }

    private void addButtons(Cell[][] grid){

        JPanel bottom = new JPanel();

        JButton clear = new JButton("Clear");
        JToggleButton toggleGrid = new JToggleButton("Grid");
        JToggleButton toggleColor = new JToggleButton("Color");
        JToggleButton toggleBrush = new JToggleButton("Brush");
        JComboBox<String> colorSelect = new JComboBox<>(new String[]{"White", "Rainbow", "Dune"});
        JComboBox<Integer> brushSize = new JComboBox<>(new Integer[]{2, 4, 6, 8, 10});
        JComboBox<Float> brushDensity = new JComboBox<>(new Float[]{0.25f, 0.5f, 0.75f, 1f});

        bottom.add(clear);
        bottom.add(toggleGrid);
        bottom.add(toggleColor);
        bottom.add(colorSelect);
        colorSelect.setEnabled(false);
        bottom.add(toggleBrush);
        bottom.add(brushSize);
        brushSize.setEnabled(false);
        bottom.add(brushDensity);
        brushDensity.setEnabled(false);
        add(bottom, BorderLayout.SOUTH);

        // Action listener for clear button
        clear.addActionListener(e ->{
            // Reset value to each Cell in grid
            for(int i = 0; i < grid.length; i++){
                for(int j = 0; j < grid[0].length; j++){
                    grid[i][j].reset();
                    newGrid[i][j].reset();
                }
            }
            repaint();
        });

        // Action listener for grid button
        toggleGrid.addActionListener(e ->{
            // Toggle the option so that grid is visible or not
            CellPanel.switchOptions("GRID");
            repaint();
        });

        // Action listener for color button
        toggleColor.addActionListener(e ->{
            // Toggle the option to have color choices or just white
            CellPanel.switchOptions("COLOR");
            colorSelect.setEnabled(CellPanel.getColorEnabled());
            repaint();
        });

        // Action listener for color select
        colorSelect.addActionListener(e ->{
            // Change the color of brush
            @SuppressWarnings("unchecked")
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            String color = (String )cb.getSelectedItem();
            CellPanel.setColor(color);
        });

        // Action listener for brush button
        toggleBrush.addActionListener(e ->{
            // Toggle the option so that a bigger brush is used or not
            CellPanel.switchOptions("BRUSH");
            brushDensity.setEnabled(CellPanel.getBrushEnabled());
            brushSize.setEnabled(CellPanel.getBrushEnabled());
            repaint();
        });

        // Action listener for brush size
        brushSize.addActionListener(e ->{
            // Change the size of brush
            @SuppressWarnings("unchecked")
            JComboBox<Integer> cb = (JComboBox<Integer>) e.getSource();
            Integer size = (Integer) cb.getSelectedItem();
            CellPanel.setBrushSize(size);
        });

        // Action listener for brush density
        brushDensity.addActionListener(e ->{
            // Change the density of brush
            @SuppressWarnings("unchecked")
            JComboBox<Float> cb = (JComboBox<Float>) e.getSource();
            Float density = (Float) cb.getSelectedItem();
            CellPanel.setBrushDensity(density);
        });
    }

    private void switchCells(Cell oldCell, Cell newCell){
        Cell temp = new Cell();
        temp.copyCell(newCell);
        newCell.copyCell(oldCell);
        oldCell.copyCell(temp);
    }
}