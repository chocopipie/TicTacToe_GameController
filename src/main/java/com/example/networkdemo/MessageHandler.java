package com.example.networkdemo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static com.example.networkdemo.HumanTypes.*;
import static com.example.networkdemo.Main.boardList;

class MessageHandler extends GameController {

    Message current_message;  // current message
    Object messageToSend;
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

        // Send the message to the server
        toServer.writeObject(messageToSend);
        toServer.reset();
    }

    // for those methods below, message will be passed in as parameter
    //
    // data will contain room id
    public void gameCreatedHandler(Message message) throws IOException {

        GameRoom gameRoom = (GameRoom) message.getData();
        String current_room_id = gameRoom.getRoomID();
        Board newBoard = new Board();  // create a new board for a new room
        System.out.println("new board created for room " + current_room_id);
        boardList.put(current_room_id,newBoard); // add that board to the boardList (key is room_id)
        System.out.println(boardList.size());

        // print out all keys-values everytime a new pair is added to the map
        System.out.println("Print all boards in the list: ");
        boardList.forEach((key, value) -> System.out.println(key));
        boardList.forEach((key, value) ->
                value.printBoard());
    }

    // data will contain the move (x and y)
    public void makeMoveHandler(Message message) throws IOException {
        Move currentMove = (Move) message.getData();
        int x = currentMove.getX();
        int y = currentMove.getY();
        char token = currentMove.getToken();
        String current_room_id = currentMove.getRoom_id();
        Board currentBoard = boardList.get(current_room_id);


//        System.out.println("Print all boards in the list: ");
//        boardList.forEach((key, value) -> System.out.println(key));
//        boardList.forEach((key, value) ->
//                value.printBoard());

        if (currentBoard != null) {
            // if the cell is empty, make the move
            if (currentBoard.getValueOfGrid(x,y) == ' ') {
                // update the board
                currentBoard.setTokenOnGrid(x,y,token);  // set token on current board
                boardList.replace(current_room_id,currentBoard); // update the board list with new token on current board
                // send moveMade (contains Move -> x,y,token)
                messageToSend = new Message(currentMove, MOVE_MADE);  // create message to send
                sendMessage();  // send moveMade or moveRejected message

                // check if there's a winner or a tie
                if(win(currentBoard,token)) {
                    // reset board
                    currentBoard.restartState();
                    // send winner message (contains Winner -> token, room_id)
                    messageToSend = new Message(currentMove, WINNER); // create message to send
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
                        //token = 'O';
                        currentMove.setToken('O');
                    else if (token == 'O')
                        //token = 'X';
                        currentMove.setToken('X');
                    // send other player's turn
                    messageToSend = new Message(currentMove, PLAYER_TURN);
                    sendMessage();
                }
            }
            else {
                // send moveRejected (contains room_id)
                messageToSend = new Message(current_room_id, MOVE_REJECTED); // create message to send
                sendMessage();  // send moveMade or moveRejected message
            }


            // print out the board after a move is set
            System.out.println("This is board : " + current_room_id);
            boardList.get(current_room_id).printBoard();

        }

    }

    public void quitHandler(Message message) throws IOException {
        String current_room_id = (String) message.getData();
        Board currentBoard = boardList.get(current_room_id);
        currentBoard.restartState();
        // send gameOver (to gameLauncher)
        messageToSend = new Message(current_room_id, GAME_OVER);
        sendMessage();
    }

    public void rematchAcceptHandler(Message message) throws IOException {
        String current_room_id = (String) message.getData();
        Board currentBoard = boardList.get(current_room_id);
        currentBoard.restartState();
        // send rematchAccepted
        messageToSend = new Message(current_room_id, REMATCH_ACCEPTED);
        sendMessage();
    }

    public void rematchRejectHandler(Message message) throws IOException {
        String current_room_id = (String) message.getData();
        Board currentBoard = boardList.get(current_room_id);
        currentBoard.restartState();
        // send rematchRejected
        //delete board form list
        boardList.remove(current_room_id);
        boardList.forEach((key, value) -> System.out.println(key));
        messageToSend = new Message(current_room_id, REMATCH_REJECTED);
        sendMessage();
    }
}
