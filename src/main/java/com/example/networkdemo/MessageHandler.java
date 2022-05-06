package com.example.networkdemo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static com.example.networkdemo.HumanTypes.*;
import static com.example.networkdemo.Main.boardList;

class MessageHandler extends GameController {

    Message current_message;  // current message
    Object messageToSend;
    Board currentBoard = new Board();
    private String current_room_id;
    ObjectOutputStream toServer;
    ObjectInputStream fromServer;
    //char currentToken = 'O';

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
        RoomList roomList = (RoomList) message.getData();
        current_room_id = roomList.getGameRoomList().get(roomList.size()-1).getRoomID();
        //GameRoom currentRoom = (GameRoom) message.getData();
        //current_room_id = currentRoom.getRoomID();
        System.out.println(current_room_id);
        currentBoard = new Board();
        System.out.println("New Board Created");
        boardList.put(current_room_id,currentBoard);
        //boardList.get(current_room_id).printBoard();

        boardList.forEach((key, value) -> System.out.println(key));
    }

    // data will contain the move (x and y)
    public void makeMoveHandler(Message message) throws IOException {
        Move currentMove = (Move) message.getData();
        int x = currentMove.getX();
        int y = currentMove.getY();
        char token = currentMove.getToken();
        current_room_id = currentMove.getRoom_id();
        currentBoard = boardList.get(current_room_id);

        // if the cell is empty, make the move
        if (currentBoard.getValueOfGrid(x,y) == ' ') {
            // update the board
            currentBoard.setTokenOnGrid(x,y,token);
            // send moveMade (contains Move -> x,y,token)
            messageToSend = new Message(currentMove, MOVE_MADE);  // create message to send
            sendMessage();  // send moveMade or moveRejected message

            // check if there's a winner or a tie
            if(win(currentBoard,token)) {
                // reset board
                currentBoard.restartState();
                // send winner message (contains Winner -> token, room_id)
                messageToSend = new Message(token, WINNER); // create message to send
                sendMessage();  // send message created above
            }
            else if(isFull(currentBoard.getGrid()) && !win(currentBoard,token)){
                // reset board
                currentBoard.restartState();
                // send tie message (contains room_id)
                messageToSend = new Message(current_room_id, TIE);
                sendMessage();  // send message created above
            }
            else {
                // if move is made and no one wins or game continues, switch player's turn
                // switch current token
                if (token == 'X')
                    token = 'O';
                else if (token == 'O')
                    token = 'X';
                // send other player's turn
                messageToSend = new Message(token, PLAYER_TURN);
                sendMessage();
            }
        }
        else {
            // send moveRejected (contains room_id)
            messageToSend = new Message(current_room_id, MOVE_REJECTED); // create message to send
            sendMessage();  // send moveMade or moveRejected message
        }


        // print out the board to test
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                System.out.print(currentBoard.getGrid()[i][j] + " ");
            System.out.println();
        }
    }

    public void quitHandler(Message message) throws IOException {
        current_room_id = (String) message.getData();
        currentBoard = boardList.get(current_room_id);
        currentBoard.restartState();
        // send gameOver (to gameLauncher)
        messageToSend = new Message(current_room_id, GAME_OVER);
        sendMessage();
    }

    public void rematchAcceptHandler(Message message) throws IOException {
        current_room_id = (String) message.getData();
        currentBoard = boardList.get(current_room_id);
        currentBoard.restartState();
        // send rematchAccepted
        messageToSend = new Message(current_room_id, REMATCH_ACCEPTED);
        sendMessage();
    }

    public void rematchRejectHandler(Message message) throws IOException {
        current_room_id = (String) message.getData();
        currentBoard = boardList.get(current_room_id);
        currentBoard.restartState();
        // send rematchRejected
        messageToSend = new Message(current_room_id, REMATCH_REJECTED);
        sendMessage();
    }
}
