package com.creedg.chessify.core;

import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Creed on 1/27/2017.
 */

//Update the model of the game via image recognition as fast as possible
public class GameModel {

    /*Pieces ids defined as   NO PIECE: 0
                              WHITE: A1->H1 (1-8), B2->H2 (9-16)
                              BLACK: H8->A8 (17-24), H7->A7 (25-32)

     Board defined 2d array from whites point of view (0,0) in bottom left*/


    private int[][] pieces_state;
    private int[][] squares_state;
    ImageView[] allPieces = new ImageView[32];
    String[] imgNames = {"w_rook","w_knight","w_bishop","w_queen","w_king","w_bishop","w_knight","w_rook",
            "w_pawn","w_pawn","w_pawn","w_pawn","w_pawn","w_pawn","w_pawn","w_pawn",
            "b_rook","b_knight","b_bishop","b_queen","b_king","b_bishop","b_knight","b_rook",
            "b_pawn","b_pawn","b_pawn","b_pawn","b_pawn","b_pawn","b_pawn","b_pawn"};
    int boardStartX;
    int boardStartY;
    int spaceSize;
    private static CameraOverlay overlay;
    Chessify ctx;

    public GameModel(Chessify context, CameraOverlay ovr) {
        pieces_state = new int[8][8];
        squares_state = new int[8][8];

        //TODO: hardcoded for now, should depend on resolution
        boardStartX = -175;
        boardStartY = context.screenHeight-348;
        spaceSize = 85;
        ctx = context;
        overlay = ovr;
        initPieces(context);
        movePieces();
    }

    /*Initialize the board by placing appropriate pieces in board state, returns false if pieces
    are not present*/

    public boolean initPieces(Chessify context) {

        for (int y = 0; y <= 1; y++) {
            for (int x = 0; x <= 7; x++) {
                pieces_state[x][y] = (y*8+x+1);
            }
        }

        for (int y = 7; y >= 6; y--) {
            for (int x = 7; x >= 0; x--) {
                pieces_state[x][y] = ((6-y)*8+x-7+32);
            }
        }


        for (int i = 0; i < 32; i++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(context.getResources().getIdentifier(imgNames[i], "drawable", context.getPackageName()));
            RelativeLayout rl = (RelativeLayout) context.findViewById(R.id.info_pane);
            rl.addView(iv);
            iv.setScaleX((float)0.15);
            iv.setScaleY((float)0.15);
            iv.setTranslationY(context.screenHeight-348-i*85);
            iv.setTranslationX(0-175+i*85);
            iv.bringToFront();
            allPieces[i] = iv;
        }

        updatePiecePlacement();

        return true;
    }

    public void updatePiecePlacement() {

        Log.d("deb","DRAWING");
        for (int i = 0; i < 32; i++) {
            allPieces[i].setTranslationX(0-999);
        }

        /*
        //Display where we think the pieces are with blue circles
        for (int y = 7; y >= 0; y--) {
            for (int x = 7; x >= 0; x--) {
                int state = pieces_state[x][y];
                if (state != 0) {
                    allPieces[state-1].setTranslationX(boardStartX+spaceSize*x);
                    allPieces[state-1].setTranslationY(boardStartY-spaceSize*y);
                    overlay.drawCircle(ctx, (int)(x*118)+185, 955-(int)(y*114), 40, Color.parseColor("#0000FF"));
                }
            }
        }
        */

    }

    public void movePieces() {
        updatePiecePlacement();
    }

    public void swap_state(int x1,int y1,int x2,int y2) {
        int save = pieces_state[x2][y2];
        pieces_state[x2][y2] = pieces_state[x1][y1];
        pieces_state[x1][y1] = save;
    }

}
