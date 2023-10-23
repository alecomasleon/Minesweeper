package Minesweeper;

import java.util.Random;
import java.util.HashSet;
import com.google.common.collect.ImmutableList;


public class MinesweeperEngine {
    private int[][] board;
    private final int rows;
    private final int cols;
    private final boolean[][] revealed;
    private boolean[][] marked;
    private final int numMines;
    private boolean gameOver;
    private boolean won;
    private HashSet<ImmutableList<Integer>> islandMembersFound;
    private boolean firstMoveMade;

    public MinesweeperEngine(int rows, int cols, int numMines) {
        this.rows = rows;
        this.cols = cols;
        this.numMines = numMines;
        revealed = createFalse();
        marked = createFalse();
        gameOver = false;
        won = false;
        firstMoveMade = false;
    }

    public void makeFirstMove(int row, int col) {
        if (!marked[row][col]) {
            firstMoveMade = true;
            marked = createFalse();
            createBoard(row, col);
            reveal(row, col);
        }
    }

    private void createBoard(int fRow, int fCol) {
        board = new int[rows][cols];

        Random rand = new Random();
        int row;
        int col;

        for (int i = 0; i < numMines; i++) {
            do {
                row = rand.nextInt(rows);
                col = rand.nextInt(cols);
            } while (board[row][col] == -1 || (pointInSafe(row, fRow) && pointInSafe(col, fCol)));

            board[row][col] = -1;

            for (int k = -1; k <= 1; k++) {
                for (int j = -1; j <= 1; j++) {
                    if ((row + k < rows) && (row + k >= 0) && (col + j < cols) && (col + j >= 0) && (board[row+k][col+j] != -1)) {
                        board[row+k][col+j]++;
                    }
                }
            }
        }
    }

    private boolean pointInSafe(int point, int safe) {
        boolean ans = false;

        for (int i = -1; i <= 1; i++) {
            if (point == safe + i) {
                ans = true;
                break;
            }
        }

        return ans;
    }

    private boolean[][] createFalse() {
        boolean[][] falsies = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                falsies[i][j] = false;
            }
        }

        return falsies;
    }

    public boolean mark(int row, int col) {
        if (!revealed[row][col]) {
            marked[row][col] = !marked[row][col];

            return true;
        }

        return false;
    }

    public boolean reveal(int row, int col) {
        if (!revealed[row][col] && !marked[row][col]) {
            revealed[row][col] = true;
            if (board[row][col] == -1) {
                gameOver = true;
            } else if (board[row][col] == 0) {
                islandMembersFound = new HashSet<>();
                revealIsland(row, col);
            }

            checkWon();

            return true;
        }

        return false;
    }
    
    private void checkWon() {
        int count = 0;
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (revealed[i][j] && board[i][j] != -1) {
                    count++;
                }
            }
        }
        
        won = count == (rows*cols - numMines);
        if (won) {
            gameOver = true;
        }
    }

    private void revealIsland(int row, int col) {
        if (!islandMembersFound.contains(ImmutableList.of(row, col))) {
            islandMembersFound.add(ImmutableList.of(row, col));
            revealed[row][col] = true;

            if (board[row][col] == 0) {
                for (int k = -1; k <= 1; k++) {
                    for (int j = -1; j <= 1; j++) {
                        if ((row + k < rows) && (row + k >= 0) && (col + j < cols) && (col + j >= 0)) {
                            revealIsland(row + k, col + j);
                        }
                    }
                }
            }
        }
    }

    public boolean[][] getMarked() {
        return marked;
    }
    
    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public boolean getGameOver() {
        return gameOver;
    }
    
    public boolean getWon() {
        return won;
    }

    public boolean[][] getRevealed() {
        return revealed;
    }

    public int[][] getBoard() {
        return board;
    }

    public int getNumMines() {
        return numMines;
    }

    public boolean getFirstMoveMade() {
        return firstMoveMade;
    }
}
