package sudoku;

import java.util.stream.IntStream;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

public class Solver implements SudokuSolver {
    private int dimension;
    private int[][] matrix;
    private boolean bounds;
    private int[][] origins;
    private int boxSize;
    
    /**
     * Constructs a new Solver.
     *
     * @param dimension
     *        The (quadratic) dimensions of the matrix. 
     * @param bounds
     *        If the solver should check boundaries or not.
     */
    private Solver(int dimension, boolean bounds) {
        this.dimension = dimension;
        this.bounds = bounds;
        this.matrix = new int[dimension][dimension];
        this.boxSize = getBoxSize();
        this.origins = getOrigins();
    }   

    /**
     * Returns a Solver with sensible defaults.
     *
     * @return the Solver 
     */
    public static Solver ofDefaults() {
        return new Solver(9, true);     
    }

    /**
     * Returns a Solver with a custom dimension.
     *
     * @param dim 
     *        the dimension 
     *
     * @return the Solver
     */
    public static Solver ofDimension(int dim) {
        return new Solver(dim, true);
    }
    
    /**
     * Returns a Solver that has its matrix set to nbrs.
     * 
     * @param nbrs
     *        the matrix
     * @return the solver
     */
    public static Solver ofMatrix(int[][] nbrs) {
        int dim = nbrs.length;
        Solver s = new Solver(dim, true);
        s.setMatrix(nbrs);
        return s;
    }

    /** 
     * Returns the dimension of the grid.
     * 
     * @return the dimension of the grid
     */
    @Override
    public int getDimension() {
        return this.dimension; 
    } 
 
    /**
     * Asserts that a row, column and number are within their respective bounds. 
     * 
     * @param r
     *        the row
     * @param c
     *        the column
     * @param n
     *        the number
     * @throws IllegalArgumentException
     *        if r, c is outside [0, getDimension() - 1] 
     *        or n    is outside [1, getDimension() - 1]      
     */
    public void assertIsWithinBounds(int r, int c, int n) {
        if (bounds &&
            !(0 <= r && r < getDimension() &&
              0 <= c && c < getDimension() &&  
              1 <= n && n <= getDimension())) {
            throw new IllegalArgumentException();
        }
    }  

    /**
     * Asserts that a row and column are within their bounds.
     * 
     * @param r
     *        the row
     * @param c
     *        the column
     * @throws IllegalArgumentException
     *        if the delegated method call throws IllegalArgumentException
     */ 
    public void assertIsWithinBounds(int r, int c) {
        assertIsWithinBounds(r, c, 1); 
    }
    
    /**
     * Asserts that a number is within its bounds.
     *
     * @param n
     *        the number
     * @throws IllegalArgumentException
     *        if the delegated method call throws IllegalArgumentException
     */
    public void assertIsWithinBounds(int n) {
        assertIsWithinBounds(0, 0, n); 
    }

    /**
     * Asserts that the matrix nbrs is quadratic and within bounds. 
     * 
     * @param nbrs
     *        the matrix
     * @throws IllegalArgumentException
     *        if the matrix is not quadric or if the delegated method call
     *        throws IllegalArgumentException 
     */
    private void assertMatrixIsWithinBounds(int[][] nbrs) {
        int rows = nbrs.length;
        int cols = nbrs[0] != null ? nbrs[0].length : 0;
        if (rows != cols) {  // must be quadratic
            throw new IllegalArgumentException();
        } else {
            assertIsWithinBounds(rows, cols);
        }
    }
    
    /**
     * Gets the matrix.  
     * 
     * @return the matrix
     */
    public int[][] getMatrix() {
        return this.matrix;
    }
 
    /**
     * Sets the matrix. 
     *
     * Bounds are checked by assertIsWithinBounds.
     *
     * @throws IllegalArgumentException
     *         if assertIsWithinBounds throws it
     */ 
    public void setMatrix(int[][] nbrs) {
        assertMatrixIsWithinBounds(nbrs);
        this.matrix = nbrs;
    } 

    /**
     * Clears the matrix.
     */
    public void clear() {
        this.matrix = new int[getDimension()][getDimension()];
    }
    
    /**
     * Gets the number at row r, column c.
     *
     * @return the number
     * @throws IllegalArgumentException
     *         if assertIsWithinBounds throws it
     */
    public int getNumber(int r, int c) {
        assertIsWithinBounds(r, c);
        return this.matrix[r][c];
    }

    /**
     * Sets the number nbr at row r, column c.
     *
     * Setting the number (0) is not allowed and should
     * be handeled by clearNumber instead. Alternatively,
     * use setOrClearNumber.
     * 
     * @throws IllegalArgumentException
     *         if assertIsWithinBounds throws it
     */
    public void setNumber(int r, int c, int nbr) {
        assertIsWithinBounds(r, c, nbr);
        this.matrix[r][c] = nbr;
    }

    /**
     * Clears the number at row c, column c. 
     * 
     * @throws IllegalArgumentException
     *         if assertIsWithinBounds throws it
     */
    public void clearNumber(int r, int c) {
        assertIsWithinBounds(r, c);
        this.matrix[r][c] = 0;    
    }

    /**
     * Either sets or clears a number at row r, column c, depending on the 
     * number. 
     *
     * Equivalent to setNumber(r, c, nbr) if nbr is not 0, else clearNumber(n, c).
     *
     * This is a convenience method. There comes many times when sudoku boards needs
     * to be fully copied and the setNumber safeguard to only set valid numbers is 
     * impractical. However, when this safeguard does not need to be lifted, use
     * setNumber instead. This makes sure that code that clears a number where it
     * shouldn't crashes fast and early. 
     * 
     * @param r
     *        the row
     * @param c
     *        the column
     * @param nbr
     *        the number
     * @throws IllegalArgumentException
     *         if assertIsWithinBounds throws it
     */  
    public void setOrClearNumber(int r, int c, int nbr) {
        if (nbr == 0) {
            clearNumber(r, c);
        } else {
            setNumber(r, c, nbr);
        }
    }

    /**
     * Gets a copy of the row r.
     * 
     * @param r 
     *        the row 
     * @return the row
     * @throws IllegalArgumentException
     *         if assertIsWithinBounds throws it
     */
    public int[] getRow(int r) {
        assertIsWithinBounds(r, 0); 
        return this.matrix[r].clone();
    }

    /**
     * Gets a copy of the column c.
     *
     * @param c
     *        the row
     * @return the column
     * @throws IllegalArgumentException
     *         if assertIsWithinBounds throws it
     */
    public int[] getCol(int c) {
        assertIsWithinBounds(0, c);
        int[] col = new int[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            col[i] = this.matrix[i][c];
        }
        return col;
    }

    /**
     * Gets the size of a box. 
     * 
     * Equivalent to the square root of dimension.
     * 
     * @return the box size 
     * @throws IllegalArgumentException
     *         if assertIsWithinBounds throws it
     */
    public int getBoxSize() {
        return (int) Math.sqrt((int) getDimension());
    }

    /**
     * Gets all box origins.
     *
     * An origin of a box is its upper left point.
     * 
     * @return the origins
     */
    private int[][] getOrigins() {
        int[][] origins = new int[getDimension()][2];
        int i = 0;
        for (int y = 0; y < getDimension(); y += this.boxSize) {
            for (int x = 0; x < getDimension(); x += this.boxSize) {
                origins[i][0] = y;
                origins[i][1] = x; 
                i++;
            } 
        }
        return origins;
    }

    /**
     * Gets the box at row r, column c.
     * 
     * Bounds are checked with assertIsWithinBounds. 
     *
     * @param r
     *        the row
     * @param c
     *        the column 
     * @return the box
     * @throws IllegalArgumentException
     *         if assertIsWithinBounds throws it
     */  
    public int[] getBox(int r, int c) {
        assertIsWithinBounds(r, c);
        int size = this.boxSize;        
        int[][] origins = this.origins; 
        int[] box = new int[getDimension()];

        for (int i = 0; i < getDimension(); i++) {
            if (origins[i][0] <= r && r < origins[i][0] + size &&
                origins[i][1] <= c && c < origins[i][1] + size) {

                int j = 0;
                for (int y = 0; y < size; y++) {
                    for (int x = 0; x < size; x++) {
                        box[j] = getNumber(origins[i][0] + y, origins[i][1] + x); 
                        j++;
                    }
                } 
            }
        }
        return box;
    }

    /**
     * Returns true if a contains one of less of each number in [1, getDimension()], else false.
     * 
     * @param a
     *        the array to check
     * @return true if array is unique, false otherwise
     */ 
    public boolean isUniqueArray(int[] a) {
        Set<Integer> collect = new HashSet<Integer>();
        for (int n : a) {
            if (n != 0 && collect.contains(n)) return false;
            collect.add(n);
        }
        return true;
    }

    /**
     * Check sudoku rules for row r, column c.
     * 
     * Bounds are checked with assertIsWithinBounds. 
     *
     * @param r
     *        the row
     * @param c
     *        the column
     * @return true if all sudoku rules are true for the position,
     *         false otherwise
     * @throws IllegalArgumentException
     *         if assertIsWithinBounds throws it
     */
    public boolean isValid(int r , int c) {
        assertIsWithinBounds(r, c);
        return isUniqueArray(getRow(r)) &&
               isUniqueArray(getCol(c)) &&
               isUniqueArray(getBox(r, c));
    }

    /**
     * Check sudoku rules for row r, column c after trying to set nbr.
     * 
     * Bounds are checked with assertIsWithinBounds.
     *
     * After having tried with the new number, the old number
     * is set again, restoring the status quo. 
     *
     * @param r
     *        the row
     * @param c
     *        the column
     * @param nbr
     *        the number
     * @return true if all sudoku rules are true for the position
     *         after number placement, false otherwise
     * @throws IllegalArgumentException
     *         if assertIsWithinBounds throws it
     */
    public boolean isValid(int r, int c, int nbr) {
        assertIsWithinBounds(r, c, nbr);
        int oldNbr = getNumber(r, c); 
        setOrClearNumber(r, c, nbr);
        boolean isValid = isValid(r, c);
        setOrClearNumber(r, c, oldNbr);  
        return isValid;
    }
    
    /**
     * Check sudoku rules for the entire matrix.
     *
     * @return true if all sudoku rules are true for all positions,
     *         false otherwise 
     */
    public boolean isAllValid() {
        for (int r = 0; r < getDimension(); r++) {
            for (int c = 0; c < getDimension(); c++) {
                if (!isValid(r, c)) return false;
            }    
        }
        return true;
    }

    /**
     * Gets the row and column from row r and column c.
     * 
     * Bounds are NOT checked. This is because nextPosFrom should only be
     * called in internal methods, which we assume are called correctly. An
     * incorrect nextPosFrom call should then crash early and hard.
     * 
     * @param r
     *        the row
     * @param c
     *        the column
     * @return the row and column pair 
     */
    private int[] nextPosFrom(int r, int c) {
        int[] pos = new int[2];
        if ((c + 1) == getDimension()) {
            pos[0] = r + 1;
            pos[1] = 0;
        } else {
            pos[0] = r;
            pos[1] = c + 1;
        }
        return pos;
    }    

    /**
     * Solves for the next position. 
     * 
     * @param r
     *        the row
     * @param c
     *        the column
     * @return true if a solution was found, false otherwise
     */
    private boolean solveNext(int r, int c) {
        int[] next = nextPosFrom(r, c);
        return next[0] == getDimension() ? true : solve(next[0], next[1]); 
    }

    /**
     * Solves the sudoku.
     *
     * @return true if a solution was found, false otherwise
     */
    public boolean solve() {
        int[][] oldMatrix = getMatrix();    
        return solve(0, 0);
    }

    /**
     * Solves the sudoku at row r, column c.
     *
     * @param r
     *        the row
     * @param c
     *        the column
     * @return true if a solution was found, false otherwise
     */
    private boolean solve(int r, int c) {
        if (getNumber(r, c) == 0) {
            boolean isSolved = false;
            for (int n = 1; n <= getDimension(); n++) {
                if (isValid(r, c, n)) {
                    setNumber(r, c, n);
                    isSolved = solveNext(r, c);      
                } 
            }
            if (!isSolved) {
                clearNumber(r, c);
            }
            return isSolved;
        } else {
            if (isValid(r, c)) {
                return solveNext(r, c);
            } else {
                return false;
            }
        }
    }

    /**
     * Returns a string representation of the sudoku.
     * 
     * @return the string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n");
        for (int r = 0; r < getDimension(); r++) {
            // write row 
            for (int c = 0; c < getDimension(); c++) {
                String num = Integer.toString(getNumber(r, c));
                if (num.length() == 1) {
                    sb.append("0");    
                }
                sb.append(num);
                sb.append(" ");
                // write (part of) column
                if ((c + 1) % getBoxSize() == 0) {
                    sb.append("| ");
                } 
            }
            sb.append("\n");
            // write horizontal sep before numbers
            if ((r + 1) % getBoxSize() == 0) {
                for (int i = 0; i < getDimension(); i++) {
                    sb.append("----");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    } 

    // ----------------------------------------------------------------
    // The following methods should be used ONLY for testing purposes,|
    // since they go against common sense class usage and actively    |
    // violates sudoku rules.                                         |
    // For example: Numbers go from 1 to dim^2 - 1, not 1 to 9.       |
    // ----------------------------------------------------------------

    /**
     * Returns a SudokuSolver with the board [0, dimension^2] 
     *
     * !!WARNING!!
     * SHOULD NOT BE USED FOR NORMAL SUDOKU SOLVING!
     * 
     * @param dim
     *        the dimension
     * @return a new SudokuSolver
     */ 
    public static Solver fromEnumerated(int dim) {
        Solver s = new Solver(dim, false);
        int i = 0;
        for (int r = 0; r < dim; r++) {
            for (int c = 0; c < dim; c++) {
                s.setNumber(r, c, i);
                i++;
            }
        }
        return s;
    } 

    /**
     * Returns the position of number nbr 
     *
     * !!WARNING!!
     * SHOULD NOT BE USED FOR NORMAL SUDOKU SOLVING!
     * 
     * @param n
     *        the number
     * @return the position as an array (row, column)
     */
    public int[] find(int n) {
        for (int r = 0; r < getDimension(); r++) {
            for (int c = 0; c < getDimension(); c++) {
                if (getNumber(r, c) == n) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }
}
