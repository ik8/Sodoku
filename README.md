#Sudoku

Sudoku puzzle generator and solver

`Sudok board = new Sudok();  // Creates a new Sudoku Object.` 
 
 ```
     a.createFullBoard(0, 0);      // Creates the board
     a.printBoard(a.full_board);   // Prints the board
     
     8 5 6 9 3 4 2 7 1 
     2 4 3 5 7 1 8 6 9 
     9 1 7 2 6 8 5 3 4 
     5 8 2 3 9 7 1 4 6 
     6 7 1 8 4 5 3 9 2 
     3 9 4 6 1 2 7 8 5 
     4 6 5 7 2 3 9 1 8 
     1 3 8 4 5 9 6 2 7 
     7 2 9 1 8 6 4 5 3 
 ```
 
 
 ```
     a.createSudokuPazzle(81);         // The number indicates the maximum numbers to erase
     a.printBoard(a.puzzle_board);     // Prints the puzzle board
    
     - 5 - - 3 - 2 - - 
     - - - - 7 - - - - 
     - 1 7 - - 8 - 3 - 
     - - - - - - - - - 
     - - - - 4 5 3 - - 
     - - - 6 - - - 8 - 
     - 6 5 - - 3 9 - - 
     1 3 8 - - 9 6 - - 
     - 2 - - - - 4 - - 
 ```
 
 ```
     a.solveSudokuPazzle();           // Solves the puzzle board.
     a.printBoard(a.solve_board);     // Prints the solver board
        
     8 5 6 9 3 4 2 7 1 
     2 4 3 5 7 1 8 6 9 
     9 1 7 2 6 8 5 3 4 
     5 8 2 3 9 7 1 4 6 
     6 7 1 8 4 5 3 9 2 
     3 9 4 6 1 2 7 8 5 
     4 6 5 7 2 3 9 1 8 
     1 3 8 4 5 9 6 2 7 
     7 2 9 1 8 6 4 5 3
 ```
 
 The algorithm which solves the board is using **Backtracking Recursion** which is beautiful for the sudoku game. the function `public int canBeSolved(int sub_board[][]) ` also ensures that there is only one solution to the puzzle board.