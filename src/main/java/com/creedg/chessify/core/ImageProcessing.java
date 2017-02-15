package com.creedg.chessify.core;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Creed on 1/27/2017.
 */

public class ImageProcessing {

    private static int rgb[];
    private static int luma[];
    private static Chessify context;
    private static CameraOverlay overlay;
    private static int frameW;
    private static int frameH;
    private static GameModel game;

    public static void initialize(Chessify ctx, CameraOverlay ovr) {
        context = ctx;
        overlay = ovr;
    }

    public static void parseImageData(byte[] data, int w, int h)
    {
        //Get some useful representations of the pixel data in the camera preview
        rgb = new int[w * h];
        luma = new int[w * h];

        //the frame size (not necessarily the screen size)
        frameW = w;
        frameH = h;

        // convert to int array containing RGB information
        decodeYUV420SP(rgb, luma, data, w, h);

        //identify where the board is and where each square lies
        defineBoard(luma);

    }


    static int colorFromID(int id) {
        if (id == 0) { return Color.parseColor("#FF0000"); }
        else if (id == 1) { return Color.parseColor("#00FF00"); }
        else if (id == 2) { return Color.parseColor("#0000FF"); }
        else return Color.parseColor("#FF0000");
    }

    static double getSlopeConsensus(ArrayList<Line> lines) {

        ArrayList<Double> slopeGroups = new ArrayList<Double>();
        ArrayList<Integer> slopeNum = new ArrayList<Integer>();

        boolean placed = false;

        for (Line l : lines) {
            for (int i = 0; i < slopeGroups.size(); i++) {
                //add it to that group
                if (Math.abs(l.m - slopeGroups.get(i)) < 10) {
                    slopeGroups.set(i, l.m);
                    slopeNum.set(i, slopeNum.get(i) + 1);
                    placed = true;
                    break;
                }
            }

            if (placed == false) {
                slopeGroups.add(l.m);
                slopeNum.add(1);
            }
        }

        int max = 0;
        int maxIdx = 0;
        for (int i = 0; i < slopeNum.size(); i++) {

            if (slopeNum.get(i) > max) {
                max = slopeNum.get(i);
                maxIdx = i;
            }
        }

        Log.d("deb",""+slopeGroups.toString());

        return slopeGroups.get(maxIdx);
    }

    static void defineBoard(int[] luma) {
        //Get the square midpoints, place a marker on them for debug

        //Work from the middle out because the middle is clear of pieces at the beginning
        int analyzeX = frameW/2;
        int analyzeY = (int)(frameH*0.3);

        /*This step only needs to be done once or when the camera is moved (which can be automatically
        detected via accelerometer). Therefore reliablity is emphasized over speed*/

        /*Luminance transitions will indicate the color changes of alternating squares. Chessboards
        almost always have darker and lighter squares.*/

        //Dimensions of preview area on test device (Galaxy S5 1080x1920):

        //Starting from center, scan vertically and record large luminance changes
        //Place points at these transitions

        overlay.clear(context);

        //The camera view region. Hardcoded for now, will be generalized.
        int xs = 1200;
        int ys = 1080;

        int [] diff = new int[frameW];
        int diffIdx = 0;
        int thresh = 0;

        //Dynamically adjusting threshold for determining dark vs light squares
        int darkLight = 0;
        int prevDarkLight = -1;
        int darkLightThresh = 150;

        int x;
        int y;

        int row_size = ys/10;
        int col_size = xs/10;

        int[] row;
        int[] col;

        int[] spacing;

        PointConsensus cons = new PointConsensus(30, xs);

        int[] col_space_cutoffs = {col_size/14,col_size/8};

        for (y = 0; y < ys; y += 33) {
            int col_idx = 0;
            int dist_counter = 0;

            col = new int[col_size];
            spacing = new int[col_size];

            //get column
            for (x = 0; x < xs; x += 10) {
                int l = getLumaAt(x, y);

                darkLight = (l < darkLightThresh) ? 0 : 1;

                if (darkLight != prevDarkLight && darkLight != -1) {
                    col[col_idx] = 1;

                    spacing[col_idx] = dist_counter;
                    dist_counter = 0;
                }

                prevDarkLight = (l < darkLightThresh) ? 0 : 1;

                col_idx++;
                dist_counter++;
            }

            //Process column to find regular pattern (vertical chessboard spacing)

            //The board will take up somewhere between 50% to 100% of the height (and contain 7 luma transitions)
            for (int v = 0; v < col_size; v++) {
                //Log.d("deb"," "+col[v]+", "+spacing[v]+"  :  "+col_space_cutoffs[0]+" "+col_space_cutoffs[1]);
                if (col[v] == 1 && spacing[v] >= col_space_cutoffs[0] && spacing[v] <= col_space_cutoffs[1]) {
                    cons.add(v*10 - 5,y);
                }
            }

        }

        int groupColor = Color.parseColor("#FF0000");
        int colorID = 0;
        int size = 5;

        for (PointGroup group : cons.getGroups()) {
            for (int i = 0; i < group.pointsX.size(); i++) {
                //overlay.drawCircle(context, group.pointsX.get(i), group.pointsY.get(i), size, colorFromID(colorID));
            }
            size += 2;
            colorID += 1;
            if (colorID > 2) { colorID = 0; }
        }

        int avgSlope = 0;
        ArrayList<PointGroup> groups = cons.getGroups();
        ArrayList<Line> lines = new ArrayList<Line>();

        for (PointGroup group : groups) {
            lines.add(group.getLine());
        }

        /*
        Double sc = getSlopeConsensus(lines);

        Log.d("deb",""+sc);

        Double slopeAvg = 0.0;

        for (PointGroup group : groups) {
            Line l = group.getLine();
            if (Math.abs(sc-l.m) < 12) {
                slopeAvg += l.m;
                overlay.drawLine(context, 0, (int) (l.b), 1080, (int) (l.m * 1080 + l.b), Color.parseColor("#FF0000"));
            }
        }
        */

        for (x = 0; x < xs; x += 33) {
            for (y = 0; y < ys; y += 4) {
                int l = getLumaAt(x, y);


                darkLight = (l < darkLightThresh) ? 0 : 1;

                if (darkLight != prevDarkLight && darkLight != -1) {
                    overlay.drawCircle(context, x, y-2, 6, Color.parseColor("#FF0000"));
                }
                prevDarkLight = (l < darkLightThresh) ? 0 : 1;

            }
        }

        for (y = 0; y < ys; y += 33) {
            for (x = 0; x < xs; x += 4) {
                int l = getLumaAt(x, y);

                darkLight = (l < darkLightThresh) ? 0 : 1;

                if (darkLight != prevDarkLight && darkLight != -1) {
                    overlay.drawCircle(context, x - 2, y, 6, Color.parseColor("#FF0000"));
                }
                prevDarkLight = (l < darkLightThresh) ? 0 : 1;
            }
        }

        game.updatePiecePlacement();
        overlay.show(context);
    }

    static int getLumaAt(int x, int y) {
        return luma[xyToIdx(x,y)];
    }

    static int xyToIdx(int x, int y) {
        return x+y*frameW;
    }

    //Decode function provided by https://github.com/ketai/ketai
    static void decodeYUV420SP(int[] rgb, int[] luma, byte[] yuv420sp, int width, int height) {

        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;

                luma[yp] = y;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }

    static void setGameModel(GameModel g) {
        game = g;
    }

}
