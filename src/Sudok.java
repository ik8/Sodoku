/**
 * Created by ikosteniov on 4/5/2017.
 */
public class Sudok {

    private int[][] full_board = new int[9][9];
    private int[][] solve_board = new int[9][9];
    private int[][] puzzle_board = new int[9][9];

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

    public static void printArray(int numbers[]) {
        for (int i = 0; i < 9; i++) {
            System.out.print(numbers[i] + " ");
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
                k = puzzle_board[numbers1[i]-1][numbers2[j]-1];
                puzzle_board[numbers1[i]-1][numbers2[j]-1] = 0;
                if(canBeSolved(puzzle_board) != 1)
                    puzzle_board[numbers1[i]-1][numbers2[j]-1] = k;
                else
                    numbers_to_delete--;
            }
    }

    public void printBoard(int board[][]) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(board[i][j] != 0)
                    System.out.printf("%d ", board[i][j]);
                else
                    System.out.printf("- ");
            }
            System.out.println();
        }
    }

    public int[][] getFull_board() {
        return full_board;
    }

    public void setFull_board(int[][] full_board) {
        this.full_board = full_board;
    }

    public int[][] getSolve_board() {
        return solve_board;
    }

    public void setSolve_board(int[][] solve_board) {
        this.solve_board = solve_board;
    }

    public int[][] getPuzzle_board() {
        return puzzle_board;
    }

    public void setPuzzle_board(int[][] puzzle_board) {
        this.puzzle_board = puzzle_board;
    }

    public static void main(String[] args) {
        Sudok a = new Sudok();
        a.createFullBoard(0, 0);
        a.printBoard(a.full_board);
        System.out.println();
        //a.createSudokuPazzle(100);
        a.printBoard(a.puzzle_board);
        System.out.println();
        a.solveSudokuPazzle();
        a.printBoard(a.solve_board);

    }




}
