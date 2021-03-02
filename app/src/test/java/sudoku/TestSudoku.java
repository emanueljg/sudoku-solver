package sudoku;

import static org.junit.jupiter.api.Assertions.*;

import org.opentest4j.AssertionFailedError;

import org.junit.jupiter.api.*;


class TestSudoku {
    Solver s1;
    Solver s2;
    Solver s3;

    @BeforeEach
    void setUp() {
        s1 = Solver.fromEnumerated(9);
        s2 = Solver.ofDefaults(); 
        s3 = Solver.ofDimension(16);
    }

    @AfterEach
    void tearDown() {
        s1 = null;
        s2 = null;
        s3 = null;
    }

    @Test
    void testAssertIsWithinBounds() {
        s2.assertIsWithinBounds(0, 0, 1);
        s2.assertIsWithinBounds(0, 8, 3);
        s2.assertIsWithinBounds(8, 0, 4);
        s2.assertIsWithinBounds(8, 8, 8);
        s2.assertIsWithinBounds(2, 6, 2);
        s2.assertIsWithinBounds(6, 5, 9);

        s2.assertIsWithinBounds(0, 0, 1);
        s2.assertIsWithinBounds(2, 8, 9);
        s2.assertIsWithinBounds(5, 7, 5);

        s2.assertIsWithinBounds(2, 7);
        s2.assertIsWithinBounds(1, 8);
        s2.assertIsWithinBounds(4, 7);
        s2.assertIsWithinBounds(8, 2);

        s2.assertIsWithinBounds(1);
        s2.assertIsWithinBounds(9);
        s2.assertIsWithinBounds(2);
        s2.assertIsWithinBounds(4);
        s2.assertIsWithinBounds(8);
        
        assertThrows(IllegalArgumentException.class,
                     () -> {s2.assertIsWithinBounds(-1, 0, 4);});
        assertThrows(IllegalArgumentException.class,
                     () -> {s2.assertIsWithinBounds(0, -1, 9);});
        assertThrows(IllegalArgumentException.class,
                     () -> {s2.assertIsWithinBounds(-1, -1, 7);});
        assertThrows(IllegalArgumentException.class,
                     () -> {s2.assertIsWithinBounds(10, 4, 9);});
        assertThrows(IllegalArgumentException.class,
                     () -> {s2.assertIsWithinBounds(2, 10, 2);});
        assertThrows(IllegalArgumentException.class,
                     () -> {s2.assertIsWithinBounds(-1, -2);});
        assertThrows(IllegalArgumentException.class,
                     () -> {s2.assertIsWithinBounds(-1, 0);});
        assertThrows(IllegalArgumentException.class,
                     () -> {s2.assertIsWithinBounds(20, 5);});
        assertThrows(IllegalArgumentException.class,
                     () -> {s2.assertIsWithinBounds(30, 30);});
        assertThrows(IllegalArgumentException.class,
                     () -> {s2.assertIsWithinBounds(40, 2);});
        assertThrows(IllegalArgumentException.class,
                     () -> {s2.assertIsWithinBounds(-100);});
        assertThrows(IllegalArgumentException.class,
                     () -> {s2.assertIsWithinBounds(999);});
    }

    @Test
    void testGetBoxSize() {
        assertEquals(3, s2.getBoxSize());  
    }

    @Test
    void testGetBox() {
        int[] b1 = new int[]{0 , 1 , 2 , 9 , 10, 11, 18, 19, 20};
        int[] b2 = new int[]{3 , 4 , 5 , 12, 13, 14, 21, 22, 23};
        int[] b3 = new int[]{6 , 7 , 8 , 15, 16, 17, 24, 25, 26}; 
        int[] b4 = new int[]{27, 28, 29, 36, 37, 38, 45, 46, 47};
        int[] b5 = new int[]{30, 31, 32, 39, 40, 41, 48, 49, 50};
        int[] b6 = new int[]{33, 34, 35, 42, 43, 44, 51, 52, 53};
        int[] b7 = new int[]{54, 55, 56, 63, 64, 65, 72, 73, 74}; 
        int[] b8 = new int[]{57, 58, 59, 66, 67, 68, 75, 76, 77};
        int[] b9 = new int[]{60, 61, 62, 69, 70, 71, 78, 79, 80};
        
        int[][] boxes = new int[][]{b1, b2, b3,
                                    b4, b5, b6,
                                    b7, b8, b9};
        
        for (int[] box : boxes) {
            for (int n : box) {
                int[] coords = s1.find(n);
                int r = coords[0];
                int c = coords[1];
                assertArrayEquals(box, s1.getBox(r, c));
            }
        }
    }

    @Test
    void testIsUniqueArray() {
        int[] a0 = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] a1 = new int[]{9, 8, 7, 6, 5, 4, 3, 2, 1};
        int[] a2 = new int[]{1, 8, 4, 9, 2, 3, 7, 6, 5}; 
        int[] a3 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] a4 = new int[]{1, 9, 3, 0, 2, 4, 7, 0, 0};
        
        assertTrue(s1.isUniqueArray(a0)); 
        assertTrue(s1.isUniqueArray(a1)); 
        assertTrue(s1.isUniqueArray(a2)); 
        assertTrue(s1.isUniqueArray(a3));
        assertTrue(s1.isUniqueArray(a4));
        
        int[] a5 = new int[]{0, 0, 0, 4, 0, 0, 4, 0, 0};
        int[] a6 = new int[]{9, 6, 3, 8, 7, 0, 3, 6, 0};
        int[] a7 = new int[]{7, 2, 2, 2, 8, 7, 6, 2, 1};
        int[] a8 = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1};
        int[] a9 = new int[]{7, 1, 3, 4, 4, 7, 8, 9, 2};
    
        assertFalse(s1.isUniqueArray(a5));
        assertFalse(s1.isUniqueArray(a6));
        assertFalse(s1.isUniqueArray(a7));
        assertFalse(s1.isUniqueArray(a8));
        assertFalse(s1.isUniqueArray(a9));
    }
}
