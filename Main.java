public class Main {
    
    // Number Of Rows And Columns
    private static final int ROW = 150;
    private static final int COL = 150;
    private static final int CELL_SIZE = 4;

    public static void main(String[] args) {

        Cell[][] grid = new Cell[ROW][COL];
        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                grid[i][j] = new Cell();
            }
        }

        new ApplicationFrame(grid, ROW, COL, CELL_SIZE);
    }
}
