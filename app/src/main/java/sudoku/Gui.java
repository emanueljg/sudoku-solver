package sudoku;

import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;

public class Gui {

    public Gui(SudokuSolver s) {
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

    private void doGridErrorPopup(List<Error> errors, JPanel grid, SudokuSolver s) {
        StringBuilder sb = new StringBuilder("Error(s) at following cell(s) occured:\n");
        for (Error error : errors) {
            // (r, c) -> (y, x)
            // (x, y) -> (c, r)
            sb.append(String.format("\nAt (%d, %d): %s", error.c + 1, error.r + 1, error.message));
        }
        // perhaps this is a bit rude?
        //sb.append("\n\nThe cell(s) will be cleared.");        

        JOptionPane pane = new JOptionPane(sb.toString(), JOptionPane.ERROR_MESSAGE);
        pane.createDialog(grid, "Grid Error").show();
        //for (Error error : errors) {
        //    s.clearNumber(error.r, error.c);
        //    error.textField.setText("");
        //}
    } 

    public boolean gridToSolver(JPanel grid, SudokuSolver s) {
        int i = 0;
        List<Error> errors = new ArrayList<Error>(); 
        for (int r = 0; r < s.getDimension(); r++) {
            for (int c = 0; c < s.getDimension(); c++) {
                JTextField tf = (JTextField) grid.getComponent(i);
                try {
                    String text = tf.getText();
                    if (text.equals("")) {
                        s.clearNumber(r, c);
                        continue;
                    }
                    int n = Integer.parseInt(text);
                    s.setNumber(r, c, n);
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

    public void solverToGrid(JPanel grid, SudokuSolver s) {
        int i = 0;
        for (int r = 0; r < s.getDimension(); r++) {
            for (int c = 0; c < s.getDimension(); c++) {
                JTextField tf = (JTextField) grid.getComponent(i);
                int nbr = s.getNumber(r, c);
                String n = nbr != 0 ? String.valueOf(nbr) : "";
                tf.setText(n);
                i++;
            }
        }
    }

    public void solveAction(JPanel grid, SudokuSolver s) {
        boolean ran = gridToSolver(grid, s);
        if (ran) {
            boolean solved = s.solve();
            solverToGrid(grid, s);
            if (!solved) {
                JOptionPane pane = new JOptionPane("Could not find a solution!");
                pane.createDialog(grid, "Info").setVisible(true);
            }
        }
    }

    public void clearAction(JPanel grid, SudokuSolver s) {
        s.clear();
        solverToGrid(grid, s);
    }

    // I was about to write a huge text about how this works
    // but I changed my mind, it made it pointlessly complicated
    // if you want to know how it works, bring out a pen and paper
    // like I did. It will make sense, trust me.
    private void doColorTextFields(JPanel panel, SudokuSolver s) {
        boolean penDown = false;
        int boxSize = (int) Math.sqrt((int) s.getDimension());
        int boxRowSwitch = boxSize * s.getDimension();
        for (int i = 0; i < Math.pow(s.getDimension(), 2); i++) {
            if (i % boxSize == 0 &&  
                (i % s.getDimension() != 0 || 
                 (boxSize % 2 != 0 && i % boxRowSwitch == 0) || 
                 (boxSize % 2 == 0 && i % boxRowSwitch != 0))) {
                penDown = !penDown;
            }
            Component tf = panel.getComponent(i);
            Color color = penDown ? Color.PINK : Color.WHITE;
            tf.setBackground(color);      
        }
    }

    public void createWindow(SudokuSolver s, 
                             String title,
                             int width, int height) {
        JFrame mainFrame = new JFrame(title);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container mainPane = mainFrame.getContentPane();
        
        JPanel grid = new JPanel();
        Font f = new Font("serif", Font.PLAIN, 30);
        
        for (int i = 0; i < Math.pow(s.getDimension(), 2); i++) {
            JTextField tf = new JTextField();
            tf.setPreferredSize(new Dimension(50, 50));
            tf.setHorizontalAlignment(JTextField.CENTER);
            tf.setFont(f);
            grid.add(tf); 
        }
        doColorTextFields(grid, s);
        
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
        int dim = 9;
        SudokuSolver s = Solver.ofDimension(dim);
        Gui gui = new Gui(s); 
    }

}
