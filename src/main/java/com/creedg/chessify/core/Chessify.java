package com.creedg.chessify.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class Chessify extends Activity  {

    private Camera mCamera = null;
    private CameraView mCameraView = null;
    public int screenWidth;
    public int screenHeight;
    public int statusBarHeight;
    CameraOverlay overlay;

    GameModel game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("deb","start");

        setupCamera();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;
        screenHeight = displaymetrics.heightPixels;
        statusBarHeight = getStatusBarHeight();
        Log.d("deb","status bar height"+statusBarHeight);

        //Camera overlay allows us to draw on top of the camera view
        overlay = new CameraOverlay(this);
        ImageProcessing.initialize(this, overlay);

        setupGameInfo();
    }

    protected void setupCamera() {
        try{
            mCamera = Camera.open();
        } catch (Exception e){
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

        if(mCamera != null) {
            mCameraView = new CameraView(this, mCamera);
            FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);
        }
    }

    protected void setupGameInfo() {
        ImageView board = new ImageView(this);
        //The display is top 60% for the camera preview, and bottom 40% for the game info display

        //Place the virtual board
        board.setImageResource(R.drawable.chessboard2_320);

        float infoViewTop = (float)(screenHeight*0.6);
        float infoViewHeight = (float)(screenHeight*0.4)-statusBarHeight;
        float boardScale = infoViewHeight/pxFromDp(this, 320);

        Paint paint = new Paint();
        Bitmap bg = Bitmap.createBitmap(screenWidth+42, screenHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);

        paint.setColor(Color.parseColor("#EEEED2"));
        canvas.drawRect(0,(int)infoViewTop+statusBarHeight-25,screenWidth+42,screenHeight,paint);

        paint.setColor(Color.parseColor("#3D3C38"));
        canvas.drawRect(0,(int)infoViewTop+statusBarHeight-28,screenWidth+42,(int)infoViewTop+statusBarHeight-15,paint);

        //Placeholder text, will implement features such as timer, move evaluator, etc. after chess engine integration
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(60);
        canvas.drawText("3:00", pxFromDp(this, 320)*boardScale+160, (int)infoViewTop+220, paint);
        canvas.drawText("3:00", pxFromDp(this, 320)*boardScale+160, (int)infoViewTop+650, paint);

        RelativeLayout ll = (RelativeLayout) findViewById(R.id.info_pane);
        ImageView iV = new ImageView(this);
        iV.setImageBitmap(bg);
        ll.addView(iV);

        Button analyzeButton = new Button(this);
        analyzeButton.setTextSize(10);
        analyzeButton.setText("Analyze game");
        ll.addView(analyzeButton);
        analyzeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new AlertDialog.Builder(Chessify.this)
                        .setTitle("Not yet!")
                        .setMessage("Under construction. Chessify still needs access to a chess engine.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        analyzeButton.setTranslationX(pxFromDp(this, 320)*boardScale+49);
        analyzeButton.setTranslationY((int)infoViewTop+287);

        board.setPivotX(-1);
        board.setPivotY(0);
        board.setTranslationX(0);
        board.setScaleX(boardScale);
        board.setScaleY(boardScale);
        board.setTranslationY(infoViewTop);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.info_pane);
        rl.addView(board);

        GameModel game = new GameModel(this, overlay);
        ImageProcessing.setGameModel(game);
    }



    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }


}
