
public class Sudok {

    private int[][] full_board   =  new int[9][9];
    private int[][] solve_board  =  new int[9][9];
    private int[][] puzzle_board =  new int[9][9];
    private int[][] human_solve_board = new int[9][9];
    private int[][] step_board = new int[9][9];
    private NumberOptions[][] options_array = new NumberOptions[9][9];
    private int human_step_counter = 0;

    public static void shuffle(int numbers[]) {
        int random, ezer;
        for (int i = 1; i <= 9; i++) {
            numbers[i-1] = i;
        }
        for (int i = 0; i < 9; i++) {
            random = (int) (Math.random()*8 + 1);
            ezer = numbers[i];
            numbers[i] = numbers[random];
            numbers[random] = ezer;
        }
    }

    public int isValid(int [][] grid, int row, int col, int value) {
        int i,j;
        for (i = 0; i < 9; i++) {
            if(i!=row)
                if(grid[i][col] == value)
                    return 0;
        }

        for (i = 0; i < 9; i++) {
            if(i!=col)
                if(grid[row][i] == value)
                    return 0;
        }

        int square_row, square_col;
        square_row = row/3*3;
        square_col = col/3*3;

        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                if((square_row+i!=row)||(square_col+j!=col))
                    if(grid[square_row+i][square_col+j] == value)
                        return 0;
            }
        }

        return 1;
    }

    public int createFullBoard(int row, int col) {

        int[] candidate = new int[9];
        int done;
        int i=0;

        shuffle(candidate);

        if(row==8 && col==8) {
            while(i < 9 && (isValid(full_board, row, col, candidate[i]) == 0))
                i++;
            if(i==9)
                return 0;
            full_board[8][8] = candidate[i];
            return 1;
        }

        for (i = 0; i < 9; i++) {
            if(isValid(full_board, row, col, candidate[i])!= 0) {
                full_board[row][col] = candidate[i];
                if(col==8)
                    done = createFullBoard(row+1, 0);
                else
                    done = createFullBoard(row, col+1);
                if(done == 0)
                    full_board[row][col]=0;
                else
                    return 1;
            }
        }
        return 0;
    }

    public int solveBoard() {

        int i,j,num,k;
        i=j=0;
        for (i = 0; i < 9; i++) {
            for (j = 0; j < 9 && solve_board[i][j]!=0; j++) {
                if(j==8)
                    break;
            }
            if(solve_board[i][j]==0)
                break;
        }

        if(i==9)
            return 1;

        k=0;
        for(num = 1;num < 10;num++) {
            if(isValid(solve_board, i, j, num)!=0) {
                solve_board[i][j] = num;
                k+= solveBoard();
                if(k!=1)
                    solve_board[i][j] = 0;
                if(k==2)
                    return 2;
            }
        }
        return k;
    }

    public int validPuzzle() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(puzzle_board[i][j] != 0 && isValid(puzzle_board, i, j, puzzle_board[i][j]) == 0)
                    return 0;
            }
        }
        return 1;
    }
    public int canBeSolved(int sub_board[][]) {

        int i,j,num,k;
        i=j=0;

        for (i = 0;i < 9;i++) {
            for (j = 0;j<9 && sub_board[i][j]!=0;j++) {
                if(j==8)
                    break;
            }
            if(sub_board[i][j]==0)
                break;
        }
        if(i==9)
            return 1;

        k=0;
        for (num=1;num<10;num++) {
            if(isValid(sub_board, i, j, num)!=0) {
                sub_board[i][j] = num;
                k+=canBeSolved(sub_board);
                sub_board[i][j]=0;
                if(k==2)
                    return 2;
            }
        }
        return k;
    }

    public int solveSudokuPazzle() {
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                solve_board[i][j] = puzzle_board[i][j];

        return solveBoard();
    }

    public void createSudokuPazzle(int number) {
        int numbers_to_delete = number;
        int[] numbers1 = new int[9];
        int[] numbers2 = new int[9];
        int i, j, k;
        shuffle(numbers1);
        shuffle(numbers2);

        for (i = 0;i < 9; i++)
            for (j = 0;j < 9; j++)
                puzzle_board[i][j] = full_board[i][j];

        for (i = 0;i < 9; i++)
            for (j = 0;j < 9 && numbers_to_delete > 0; j++) {
                k = puzzle_board[numbers2[i]-1][numbers1[j]-1];
                puzzle_board[numbers2[i]-1][numbers1[j]-1] = 0;
                if(canBeSolved(puzzle_board) != 1)
                    puzzle_board[numbers2[i]-1][numbers1[j]-1] = k;
                else
                    numbers_to_delete--;
            }
    }

    public void human_solution() {
        for (int i = 0; i < 9; i++) {
            human_solve_board[i] = new int[9];
            for (int j = 0; j < 9; j++) {
                human_solve_board[i][j] = puzzle_board[i][j];
            }
        }
        init_Options();
    }

    public void init_Options() {
        for (int i = 0; i < 9; i++) {
            options_array[i] = new NumberOptions[9];
            for (int j = 0; j < 9; j++) {
                options_array[i][j] = new NumberOptions();
                for (int k = 0; k < 9; k++) {
                    if(isValid(human_solve_board, i, j, k+1)==0)
                        options_array[i][j].getNumbers()[k] = true;
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(human_solve_board[i][j]!=0) {
                    for (int k = 0; k < 9; k++)
                        options_array[i][j].getNumbers()[k] = true;
                }
            }
        }
    }


    public void updateOptions(int row, int col, int value) {
        for (int i = 0; i < 9; i++) {
            options_array[row][col].getNumbers()[i] = true;
        }
        for (int i = row/3*3; i < row/3*3+3; i++) {
            for (int j = col/3*3; j < col/3*3+3; j++) {
                options_array[i][j].getNumbers()[value] = true;
            }
        }

        for (int i = 0; i < 9; i++) {
            options_array[row][i].getNumbers()[value] = true;
            options_array[i][col].getNumbers()[value] = true;
        }
    }

    public boolean humanSolver(int level) {
        int counter = 0;
        while(!isBoardDone() && counter < 10) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (human_solve_board[i][j] == 0) {
                        if (!putOneOption(i, j))
                            if (!putInSquare(i, j))
                                if (!putInLineOrCol(i, j));


                    }

                }
            }
            counter++;
        }
        if(counter == 10) {
            return false;
        }
        return true;

    }

    public boolean putInLineOrCol(int row, int col) {
        int row_app, col_app;
        row_app = col_app = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(!options_array[row][j].getNumbers()[i])
                    col_app++;
                if(!options_array[j][col].getNumbers()[i])
                    row_app++;
            }
            if((row_app == 1 || col_app == 1) && isValid(human_solve_board, row, col, i+1) == 1) {
                human_solve_board[row][col] = (i+1);
                step_board[row][col] = ++human_step_counter;
                updateOptions(row, col, i);
                return true;
            }
            row_app = col_app = 0;
        }
        return false;
    }

    public boolean putInSquare(int row, int col) {
        int square_app = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                    if(!options_array[j][k].getNumbers()[i])
                        square_app++;
                    if(!options_array[j][k].getNumbers()[i])
                        square_app++;
                }
            }
            if(square_app == 1 && isValid(human_solve_board, row, col, i+1) == 1) {
                human_solve_board[row][col] = (i+1);
                step_board[row][col] = ++human_step_counter;
                return true;
            }
            square_app = 0;
        }
        return false;
    }


    public boolean putOneOption(int row, int col) {
        int optionCount = 0;
        int number = 0;
        for (int i = 0; i < 9; i++) {
            if(!options_array[row][col].getNumbers()[i]) {
                optionCount++;
                number = i+1;
            }

        }
        if(optionCount == 1 && isValid(human_solve_board, row, col, number) == 1) {
            human_solve_board[row][col] = number;
            step_board[row][col] = ++human_step_counter;
            updateOptions(row, col, number-1);
            return true;
        }
        return false;
    }

    public boolean isBoardDone() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(human_solve_board[i][j] == 0)
                    return false;
            }
        }
        return true;
    }


    public int[][] getFull_board() {
        return full_board;
    }


    public int[][] getSolve_board() {
        return solve_board;
    }


    public int[][] getPuzzle_board() {
        return puzzle_board;
    }


    public int[][] getStep_board() {
        return step_board;
    }


    public int[][] getHuman_solve_board() {
        return human_solve_board;
    }






}
