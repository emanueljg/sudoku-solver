package sudoku;

import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;

public class Gui {

    public Gui(Solver s) {
        SwingUtilities.invokeLater(() -> createWindow(s, "Sudoku Solver", 300, 300));
    }

    private class Error {
        String message;
        JTextField textField;
        int r;
        int c;
       
        public Error(String message, JTextField textField, int r, int c) {
            this.message = message;
            this.textField = textField;
            this.r = r;
            this.c = c;
        } 
    }

    private void doGridErrorPopup(List<Error> errors, JPanel grid, Solver s) {
        StringBuilder sb = new StringBuilder("Error(s) at following cell(s) occured:\n");
        for (Error error : errors) {
            // (r, c) -> (y, x)
            // (x, y) -> (c, r)
            sb.append(String.format("\nAt (%d, %d): %s", error.c + 1, error.r + 1, error.message));
        }
        sb.append("\n\nThe cell(s) will be cleared.");

        JOptionPane pane = new JOptionPane(sb.toString(), JOptionPane.ERROR_MESSAGE);
        pane.createDialog(grid, "Grid Error").show();
        for (Error error : errors) {
            s.clearNumber(error.r, error.c);
            error.textField.setText("0");     
        }
    } 

    public boolean gridToSolver(JPanel grid, Solver s) {
        int i = 0;
        List<Error> errors = new ArrayList<Error>(); 
        for (int r = 0; r < s.getDimension(); r++) {
            for (int c = 0; c < s.getDimension(); c++) {
                JTextField tf = (JTextField) grid.getComponent(i);
                try {
                    int n = Integer.parseInt(tf.getText());
                    s.setOrClearNumber(r, c, n);
                } catch (NumberFormatException e) {
                    errors.add(new Error("Invalid integer: " + tf.getText(), tf, r, c));
                } catch (IllegalArgumentException e) {
                    errors.add(new Error("Integer out of bounds: " + tf.getText(), tf, r, c));
                } finally {
                    i++;
                }
            }
        }
        if (errors.isEmpty()) {
            return true;
        } else {
            doGridErrorPopup(errors, grid, s);
            return false;
        }
    }

    public void solverToGrid(JPanel grid, Solver s) {
        int i = 0;
        for (int r = 0; r < s.getDimension(); r++) {
            for (int c = 0; c < s.getDimension(); c++) {
                JTextField tf = (JTextField) grid.getComponent(i);
                String n = String.valueOf(s.getNumber(r, c));
                tf.setText(n);
                i++;
            }
        }
    }

    public void solveAction(JPanel grid, Solver s) {
        boolean ran = gridToSolver(grid, s);
        if (ran) {
            boolean solved = s.solve();
            solverToGrid(grid, s);
            if (!solved) {
                JOptionPane pane = new JOptionPane("Could not find a solution!");
                pane.createDialog(grid, "Info").show();
            }
        }
    }

    public void clearAction(JPanel grid, Solver s) {
        s.clear();
        solverToGrid(grid, s);
    }

    public void createWindow(Solver s, 
                             String title,
                             int width, int height) {
        JFrame mainFrame = new JFrame(title);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container mainPane = mainFrame.getContentPane();
        
        JPanel grid = new JPanel();
        for (int i = 0; i < Math.pow(s.getDimension(), 2);  i++) {
            JTextField tf = new JTextField("0");
            grid.add(tf); 
        }

        GridLayout gl = new GridLayout(s.getDimension(), s.getDimension());
        grid.setLayout(gl);
        mainPane.add(grid, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(e -> { solveAction(grid, s); });
        buttonPanel.add(solveButton);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> { clearAction(grid, s); });
        buttonPanel.add(clearButton);

        mainPane.add(buttonPanel, BorderLayout.PAGE_END); 

        mainFrame.pack();
        mainFrame.setVisible(true);
    }
                           

    public static void main(String[] args) {
        Solver s = Solver.ofDefaults();
        Gui gui = new Gui(s); 
    }

}
