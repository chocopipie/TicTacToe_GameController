package com.example.networkdemo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static com.example.networkdemo.HumanTypes.*;

class MessageHandler extends GameController {

    Message current_message;  // current message
    Object messageToSend;
    private String current_room_id = "noIDfornow";
    ObjectOutputStream toServer;
    ObjectInputStream fromServer;

    MessageHandler(ObjectOutputStream toServer, ObjectInputStream fromServer) {
        this.toServer = toServer;
        this.fromServer = fromServer;
    }

    void sendMessage() throws IOException {
        // Downcast message from Object
        current_message = (Message)messageToSend;
        System.out.println("sending: " + current_message.getType().getDescription());

        //Object msg = message;
        // Send the message to the server
        toServer.writeObject(messageToSend);
    }

    // for those methods below, message will be passed in as parameter
    //
    // data will contain room id
    public void gameCreatedHandler(Message message) throws IOException {
        //current_room_id = (String) message.getData();
        GameController.Board.restartState();

        //send gameCreated message (to game launcher, contains room_id)
        // MULTIGAME_CREATED is temporarily used instead of GAME_CREATED
        messageToSend = new Message(current_room_id, MULTIGAME_CREATED);  // create message to send
        sendMessage();
    }

    // data will contain the move (x and y)
    public void makeMoveHandler(Message message) throws IOException {
        Move currentMove = (Move) message.getData();
        int x = currentMove.getX();
        int y = currentMove.getY();
        char token = currentMove.getToken();
        //current_room_id = currentMove.getRoom_id();

        // if the cell is empty, make the move
        if (GameController.Board.getValueOfGrid(x,y) == ' ') {
            // update the board
            GameController.Board.setTokenOnGrid(x,y,token);
            // send moveMade (contains Move -> x,y,token)
            messageToSend = new Message(currentMove, MOVE_MADE);  // create message to send

        }
        else {
            // send moveRejected (contains room_id)
            messageToSend = new Message(current_room_id, MOVE_REJECTED); // create message to send
        }
        sendMessage();  // send moveMade or moveRejected message

        // print out the board to test
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                System.out.print(Board.getGrid()[i][j] + " ");
            System.out.println();
        }


        // check if there's a winner or a tie
        if(win(token)) {
            // reset board
            GameController.Board.restartState();
            // send winner message (contains Winner -> token, room_id)
             messageToSend = new Message(token, WINNER); // create message to send
            sendMessage();  // send message created above
        }
        if(isFull(GameController.Board.getGrid()) && !win(token)){
            // reset board
            GameController.Board.restartState();
            // send tie message (contains room_id)
            messageToSend = new Message(current_room_id, TIE);
            sendMessage();  // send message created above
        }
    }


    public void quitHandler(Message message) throws IOException {
        //current_room_id = (String) message.getData();
        GameController.Board.restartState();
        // send gameOver (to gameLauncher)
        messageToSend = new Message(current_room_id, GAME_OVER);
        sendMessage();
    }

    public void rematchAcceptHandler(Message message) throws IOException {
        //current_room_id = (String) message.getData();
        GameController.Board.restartState();
        // send rematchAccepted
        messageToSend = new Message(current_room_id, REMATCH_ACCEPTED);
        sendMessage();
    }

    public void rematchRejectHandler(Message message) throws IOException {
        //current_room_id = (String) message.getData();
        GameController.Board.restartState();
        // send rematchRejected
        messageToSend = new Message(current_room_id, REMATCH_REJECTED);
        sendMessage();
    }
}
