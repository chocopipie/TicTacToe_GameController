package com.example.networkdemo;

// nested class for the board
// this class implements Observer interface
public class Board implements Observer {
    private static char[][] grid;

    public Board() {
        grid = new char[][] {
                { 's', ' ', ' '},
                { ' ', ' ', ' '},
                { ' ', ' ', 'v'}};
    }
    //update the state of board
    @Override
    public void update(char t, int row, int col){
        grid[row][col] = t;
    }


    //set value onto grid
    public void setTokenOnGrid(int x, int y, char token){
        grid[x][y] = token;
    }

    //return grid
    public char[][] getGrid(){
        return grid;
    }

    //return specific value on grid
    public char getValueOfGrid(int x, int y){
        char val = grid[x][y];
        return val;
    }

    //initialize board
    public void restartState(){
        grid = new char[][] {{ ' ', ' ', ' '},
                { ' ', ' ', ' '},
                { ' ', ' ', ' '}};
    }

    public void printBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                System.out.print(grid[i][j] + " ");
            System.out.println();
        }
    }

}
