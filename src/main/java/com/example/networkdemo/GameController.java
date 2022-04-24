package com.example.networkdemo;

public class GameController {

    //determine if there is a winner
    public boolean win(char token) {
        //across
        char[][] temp = Board.getGrid();
        for (int i = 0; i < 3; i++)
            if (temp[i][0] == token && temp[i][1]  == token && temp[i][2]  == token) {
                return true;
            }

        //line
        for (int j = 0; j < 3; j++)
            if (temp[0][j] ==  token && temp[1][j] == token && temp[2][j] == token) {
                return true;
            }

        //diagonal [top left to bottom right]
        if (temp[0][0] == token && temp[1][1] == token && temp[2][2] == token) {
            return true;
        }

        //diagonal [bottom left to top right]
        if (temp[0][2] == token && temp[1][1] == token && temp[2][0] == token) {
            return true;
        }

        return false;
    }

    //determine if space is set already
    public static boolean isFull(char grid[][]) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (grid[i][j] == ' ')
                    return false;
        return true;
    }


    // nested class for the board
    // this class implements Observer interface
    static class Board implements Observer {
        private static char[][] grid = new char[][] {
                { ' ', ' ', ' '},
                { ' ', ' ', ' '},
                { ' ', ' ', ' '}};


        //update the state of board
        @Override
        public void update(char t, int row, int col){
            grid[row][col] = t;
        }


        //set value onto grid
        public static void setTokenOnGrid(int x, int y, char token){
            grid[x][y] = token;
        }

        //return grid
        public static char[][] getGrid(){
            return grid;
        }

        //return specific value on grid
        public static char getValueOfGrid(int x, int y){
            char val = grid[x][y];
            return val;
        }

        //initialize board
        public static void restartState(){
            grid = new char[][] {{ ' ', ' ', ' '},
                    { ' ', ' ', ' '},
                    { ' ', ' ', ' '}};
        }

    }
}