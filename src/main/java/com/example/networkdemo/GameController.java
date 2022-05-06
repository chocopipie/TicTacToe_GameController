package com.example.networkdemo;

public class GameController extends Board {

    //determine if there is a winner
    public boolean win(Board board, char token) {
        //across
        char[][] temp = board.getGrid();
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

}