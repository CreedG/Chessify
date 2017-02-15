package com.creedg.chessify.core;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Creed on 1/27/2017.
 */

//Overlay shapes on the camera display, mostly for image processing debug purposes

public class CameraOverlay {

    Paint paint;
    Bitmap bg;
    Canvas canvas;
    ImageView iV;

    public CameraOverlay(Chessify context) {
        paint = new Paint();
        bg = Bitmap.createBitmap(context.screenWidth, context.screenHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bg);

        RelativeLayout ll = (RelativeLayout) context.findViewById(R.id.drawing);
        iV = new ImageView(context);
        iV.setImageBitmap(bg);
        ll.addView(iV);
    }

    public void show(Chessify context) {
        iV.setImageBitmap(bg);
    }

    public void clear(Chessify context) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    //Drawing functions
    public void drawCircle(Chessify context, int x, int y, int rad, int col) {

        paint.setColor(col);
        //Transform so x and y match the camera preview's coordinates
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawCircle(context.screenWidth-y+(context.screenWidth-y)*30/context.screenWidth-15,x,rad,paint);

    }

    public void drawLine(Chessify context, int x1, int y1, int x2, int y2, int col) {

        paint.setColor(col);
        //Transform so x and y match the camera preview's coordinates
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawLine(context.screenWidth-y1+10,x1,context.screenWidth-y2+10,x2,paint);

    }

}
