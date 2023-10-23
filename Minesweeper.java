import org.checkerframework.checker.units.qual.C;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class Minesweeper {
    private JFrame frame;
    private MinesweeperEngine engine;
    private JPanel[][] board;

    private final int rows;
    private final int cols;
    private final int numMines;

    private final int HEIGHT;
    private final int WIDTH;

    private final Level LEVEL;

    public enum Level {
        EASY, MEDIUM, HARD
    }

    private static final String[] modes = new String[] {"Easy", "Medium", "Hard"};

    public Minesweeper(int rows, int cols, int numMines) {
        this.rows = rows;
        this.cols = cols;
        this.numMines = numMines;

        HEIGHT = 550/rows * rows;
        WIDTH = 550/rows * cols;

        LEVEL = null;
        setUp();
    }

    public Minesweeper(Level level) {
        if (level == Level.EASY) {
            rows = 9;
            cols = 9;
            numMines = 10;
            HEIGHT = 540;
            WIDTH = 540;
        } else if (level == Level.MEDIUM) {
            rows = 16;
            cols = 16;
            numMines = 40;
            HEIGHT = 592;
            WIDTH = 592;
        } else {
            rows = 24;
            cols = 24;
            numMines = 99;
            HEIGHT = 696;
            WIDTH = 696;
        }

        this.LEVEL = level;
        setUp();
    }

    private void setUp() {
        frame = new JFrame("Minesweeper");
        engine = new MinesweeperEngine(rows, cols, numMines);

        placeTiles();

        frame.setSize(WIDTH, HEIGHT + 27);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        fixLastTile();
    }

    private void placeTiles() {
        board = new JPanel[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = new JPanel();
                board[i][j].setBounds(j * WIDTH / cols, i * HEIGHT / rows, WIDTH / cols, HEIGHT / rows);
                board[i][j].setBackground(getColor(i, j));
                board[i][j].addMouseListener(new MouseClick(i, j));
                board[i][j].setLayout(new GridBagLayout());

                frame.add(board[i][j]);
            }
        }
    }

    private Color getColor(int row, int col) {
        if (engine.getMarked()[row][col]) {
            if (((row + col) % 2) == 0) {
                return new Color(255, 207, 2);
            }
            return Color.ORANGE;
        }

        if (engine.getRevealed()[row][col]) {
            if (((row + col) % 2) == 0) {
                return new Color(197, 197, 197);
            }
            return Color.LIGHT_GRAY;
        }

        if (((row + col) % 2) == 0) {
            return new Color(169, 215, 81);
        }

        return new Color(162, 209, 72);
    }

    private void updateTiles() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!engine.getMarked()[i][j]) {
                    if (engine.getRevealed()[i][j]) {
                        if (engine.getBoard()[i][j] == -1) {
                            board[i][j].setBackground(Color.RED);
                        } else if (engine.getBoard()[i][j] == 0) {
                            board[i][j].removeAll();
                            board[i][j].setBackground(getColor(i, j));
                        } else {
                            board[i][j].setBackground(getColor(i, j));
                            JLabel label = new JLabel(String.valueOf(engine.getBoard()[i][j]));
                            label.setHorizontalTextPosition(JLabel.CENTER);
                            label.setVerticalTextPosition(JLabel.CENTER);
                            label.setBounds(getXDisplacement(), 0, board[i][j].getWidth(), board[i][j].getHeight());

                            Font font = new Font("SansSerif", Font.PLAIN, getFontSize());
                            label.setForeground(getTextColor(engine.getBoard()[i][j]));
                            label.setFont(font);

                            board[i][j].add(label);
                        }
                    } else {
                        board[i][j].removeAll();
                        board[i][j].setBackground(getColor(i, j));
                    }
                }
            }
        }
    }

    private Color getTextColor(int tileNum) {
        return switch (tileNum) {
            case 1 -> new Color(24, 118, 210);
            case 2 -> new Color(56, 142, 60);
            case 3 -> new Color(211, 46, 47);
            case 4 -> new Color(122, 30, 162);
            case 5 -> new Color(250, 145, 10);
            case 6 -> new Color(10, 217, 250);
            case 7 -> Color.MAGENTA;
            default -> Color.BLACK;
        };
    }

    private int getFontSize() {
        if (LEVEL == Level.EASY) {
            return 33;
        } else if (LEVEL == Level.MEDIUM) {
            return 24;
        } else if (LEVEL == Level.HARD) {
            return 19;
        }

        return 25;
    }

    private int getXDisplacement() {
        int dis = (int) (WIDTH/cols/3.3);
        if (LEVEL == Level.EASY) {
            dis += 2;
        }
        return dis;
    }

    private void mark(int row, int col) {
        if (engine.mark(row, col)) {
            if (engine.getMarked()[row][col]) {
                board[row][col].setBackground(getColor(row, col));
            } else {
                board[row][col].removeAll();
                board[row][col].setBackground(getColor(row, col));
            }
        }
    }

    private void reveal(int row, int col) {
        if (engine.reveal(row, col)) {
            updateTiles();

            if (engine.getGameOver()) {
                if (!engine.getWon()) {
                    loss();
                }
                gameOver();
            }
        }
    }

    private void makeFirstMove(int row, int col) {
        engine.makeFirstMove(row, col);
        updateTiles();
    }

    private void fixLastTile() {
        board[rows-1][cols-1].setBounds((cols-1) * WIDTH / cols, (rows-1) * HEIGHT / rows, WIDTH / cols, HEIGHT / rows);
    }

    private void loss() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (engine.getBoard()[i][j] == -1) {
                    board[i][j].setBackground(Color.RED);
                }
            }
        }
    }

    private void gameOver() {
        JFrame frame2 = new JFrame("Game Finished");
        JLabel label = new JLabel();

        if (engine.getWon()) {
            label.setText("YOU WON YAY");
        } else {
            label.setText("YOU LOST LLL");
        }

        JLabel label2 = new JLabel("New Game:");

        JPanel outerPanel = new JPanel();

        outerPanel.add(label);
        outerPanel.add(label2);
        outerPanel.add(getChooser(new JFrame[] {frame, frame2}, getDefaultIndex()));

        label.setFont(new Font("SansSerif", Font.BOLD, 35));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        label2.setFont(new Font("SansSerif", Font.PLAIN, 15));
        label2.setHorizontalAlignment(SwingConstants.CENTER);

        frame2.add(outerPanel);

        frame2.setSize(325, 125);
        frame2.setLocationRelativeTo(null);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setVisible(true);
    }

    private int getDefaultIndex() {
        if (LEVEL == Level.EASY) {
            return 0;
        } else if (LEVEL == Level.MEDIUM) {
            return 1;
        }

        return 2;
    }

    public static void playGame(Level level) {
        Minesweeper ms = new Minesweeper(level);
    }

    public static void newSession() {
        JFrame frame = new JFrame("Choose Level");

        frame.add(getChooser(new JFrame[] {frame}, 1));

        frame.setSize(200, 68);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static JPanel getChooser(JFrame[] framesToClose, int defaultIndex) {
        JComboBox<String> combo = new JComboBox<>(modes);

        combo.setSelectedIndex(defaultIndex);

        JButton button = new JButton("Play");

        button.addActionListener(new PlayButtonListener(combo, framesToClose));

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(combo);
        panel.add(button);

        return panel;
    }

    public static void main(String[] args) {
        newSession();
    }

    private class MouseClick implements MouseListener {
        private final int row;
        private final int col;

        public MouseClick(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            fixLastTile();

            if (!engine.getGameOver()) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (engine.getFirstMoveMade()) {
                        reveal(row, col);
                    } else {
                        makeFirstMove(row, col);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    mark(row, col);
                }
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
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
    }

    private static class PlayButtonListener implements ActionListener {
        private final JComboBox<String> combo;
        private final JFrame[] framesToClose;

        public PlayButtonListener(JComboBox<String> combo, JFrame[] framesToClose) {
            this.combo = combo;
            this.framesToClose = framesToClose;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String mode = modes[combo.getSelectedIndex()];

            for (JFrame frame: framesToClose) {
                frame.setVisible(false);
                frame.dispose();
            }

            if (mode.equals("Easy")) {
                playGame(Level.EASY);
            } else if (mode.equals("Medium")) {
                playGame(Level.MEDIUM);
            } else {
                playGame(Level.HARD);
            }
        }
    }
}
