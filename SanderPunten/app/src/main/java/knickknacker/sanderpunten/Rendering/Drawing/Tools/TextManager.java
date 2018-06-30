package knickknacker.sanderpunten.Rendering.Drawing.Tools;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import knickknacker.sanderpunten.Rendering.Drawing.Drawables.Text;

/**
 * Created by Niek on 23-5-2018.
 */

/**
 * Source: http://fractiousg.blogspot.nl/2012/04/rendering-text-in-opengl-on-android.html
 * */

public class TextManager {
    public final static int START = 32;
    public final static int END = 126;
    public final static int COUNT = (((END - START) + 1) + 1);
    public final static int NONE = 32;
    public final static int UNKNOWN = (COUNT - 1);
    public final static float FONT_SIZE_MIN = 6f;
    public final static float FONT_SIZE_MAX = 180f;

    private AssetManager assets;

    private float fontPadX, fontPadY;

    private float fontHeight;
    private float fontAscent;
    private float fontDescent;

    private int textureId;
    private int textureSize;
    private TextureRegion region;
    private float charWidthMax;
    private float charHeight;
    private float[] charWidths;
    private TextureRegion[] charRegion;
    private float cellWidth, cellHeight;
    private int rowCount, colCount;
    private float scaleX, scaleY;
    private float spaceX;

    private boolean fontLoaded = false;
    private String fontFile;
    private int size = 30;
    private int fontSize;
    private float padX = 0.0f;
    private float padY = 0.0f;


    public TextManager(AssetManager assets) {
        this.assets = assets;

        charWidths = new float[COUNT];
        charRegion = new TextureRegion[COUNT];

        fontPadX = fontPadY = 0;

        fontHeight = fontAscent = fontDescent = 0.0f;

        textureId = -1;
        textureSize = 0;

        charWidthMax = 0;
        charHeight = 0;

        cellWidth = cellHeight = 0;

        rowCount = colCount = 0;

        scaleX = scaleY = 2.0f;
        spaceX = 0.0f;
    }


    public String getFontFile() {
        /** Get the file which will be used to load the font. */
        return fontFile;
    }

    public void setFontFile(String fontFile) {
        /** Set the file which will be used to load the font. */
        this.fontFile = fontFile;
    }

    public int getSize() {
        /** Get the font size. */
        return size;
    }

    public void setSize(int size) {
        /** Set the font size. */
        this.size = size;
    }

    public float getPadX() {
        /** Get the x padding. */
        return padX;
    }

    public void setPadX(float padX) {
        /** Set the x padding. */
        this.padX = padX;
    }

    public float getPadY() {
        /** Get the y padding. */
        return padY;
    }

    public void setPadY(float padY) {
        /** Set the y padding. */
        this.padY = padY;
    }

    public boolean isFontLoaded() {
        /** Check if the manager has a font loaded. */
        return fontLoaded;
    }

    public boolean load(float unit) {
        /** Load the font. */
        fontPadX = padX * unit;
        fontPadY = padY * unit;
        fontSize = (int) (size * unit);

        Typeface tf = Typeface.createFromAsset(assets, fontFile);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(fontSize);
        paint.setColor(0xffffffff);
        paint.setTypeface(tf);

        /** Get font properties. */
        Paint.FontMetrics fm = paint.getFontMetrics();
        fontHeight = (float) Math.ceil(Math.abs(fm.bottom) + Math.abs(fm.top));
        fontAscent = (float) Math.ceil(Math.abs(fm.ascent));
        fontDescent = (float) Math.ceil(Math.abs(fm.descent));

        char[] s = new char[2];
        charWidthMax = charHeight = 0;
        float[] w = new float[2];
        int count = 0;

        /** Get all character widths and the maximum width. */
        for (char c = START; c <= END; c++) {
            s[0] = c;
            paint.getTextWidths(s, 0, 1, w);
            charWidths[count] = w[0];
            if (charWidths[count] > charWidthMax) {
                charWidthMax = charWidths[count];
            }

            count++;
        }

        /** Individual for the Unknown Character. */
        s[0] = NONE;
        paint.getTextWidths(s, 0, 1, w);
        charWidths[count] = w[0];
        if (charWidths[count] > charWidthMax) {
            charWidthMax = charWidths[count];
        }

        count++;

        charHeight = fontHeight;

        /** Set the cell width and height. */
        cellWidth = charWidthMax + (2 * fontPadX);
        cellHeight = charHeight + (2 * fontPadY);
        float maxSize = cellWidth > cellHeight ? cellWidth : cellHeight;
        if (maxSize < FONT_SIZE_MIN || maxSize > FONT_SIZE_MAX) {
            fontLoaded = false;
            System.err.println("Could not load font: " + fontFile + " with fontsize: " + size);
            return false;
        }

        if (maxSize <= 24) {
            textureSize = 256;
        } else if (maxSize <= 40) {
            textureSize = 512;
        } else if (maxSize <= 80) {
            textureSize = 1024;
        } else {
            textureSize = 2048;
        }

        /** Load the texture. */
        Bitmap bitmap = Bitmap.createBitmap(textureSize, textureSize, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0x00000000);
        colCount = (int) (Math.ceil(textureSize / cellWidth));
        rowCount = (int) Math.ceil((float) COUNT / (float) colCount);

        float x = fontPadX;
        float y = (cellHeight - 1) - fontDescent - fontPadY;
        for (char c = START; c <= END; c++) {
            s[0] = c;
            canvas.drawText(s, 0, 1, x, y, paint);
            x += cellWidth;
            if ((x + cellWidth - fontPadX) > textureSize) {
                x = fontPadX;
                y += cellHeight;
            }
        }

        s[0] = NONE;
        canvas.drawText(s, 0, 1, x, y, paint);

        textureId = Textures.loadBitmapTexture(bitmap)[0];

        x = 0;
        y = 0;
        for (int c = 0; c < COUNT; c++) {
            charRegion[c] = new TextureRegion(textureSize, textureSize, x, y, cellWidth - 1, cellHeight - 1);
            x += cellWidth;
            if (x + cellWidth > textureSize) {
                x = 0;
                y += cellHeight;
            }
        }

        region = new TextureRegion( textureSize, textureSize, 0, 0, textureSize, textureSize );
        fontLoaded = true;
        return true;
    }


    public Text getText(String text, float x, float y, float[] color, float z)  {
        /** Get a Drawable/Triangles/Text to draw the given String. */
        if (!fontLoaded) {
            return null;
        }

        float chrHeight = cellHeight * scaleY;
        float chrWidth = cellWidth * scaleX;
        int len = text.length();

        float letterX, letterY;
        letterX = x;
        letterY = y;

        float[] points = new float[len * 6 * 3];
        float[] texels = new float[len * 6 * 2];
        for (int i = 0; i < len; i++)  {
            int c = (int)text.charAt(i) - START;
            if (c < 0 || c >= COUNT)
                c = UNKNOWN;

            insertData(points, texels, i, letterX, letterY, chrHeight, chrWidth, charRegion[c], z);

            letterX += (charWidths[c] + spaceX ) * scaleX;    // Advance X Position by Scaled Character Width
        }

        return new Text(points, color, textureId, texels);
    }

    private int getChar(String text, int i) {
        int c = (int)text.charAt(i) - START;
        if (c < 0 || c >= COUNT)
            c = UNKNOWN;

        return c;
    }

    public Text getTextFit(String text, float x, float y, float z, float[] color, float lineWidth,
                           float boxHeight, boolean cut, boolean breakOnSpace)  {
        /** Get a Drawable/Triangles/Text to draw the given String which fits in the given space
         * and cut of the lines on whitespaces.. */
        if (!fontLoaded) {
            return null;
        }

        float chrHeight = cellHeight * scaleY;
        float chrWidth = cellWidth * scaleX;
        int len = text.length();

        float letterX, letterY;
        letterX = x;
        letterY = y;

        float[] points = new float[len * 6 * 3];
        float[] texels = new float[len * 6 * 2];
        int wordTemp = 0;
        float wordWidth = 0f;
        int i;
        for (i = 0; i < len; i++)  {
            String s = text.substring(i, i + 1);
            /** Loop until a whole word has been found. */
            if (s.equals(" ")) {
                /** When the word fits on the current line, edit its points. */
                if (letterX + wordWidth <= lineWidth) {
                    for (int j = i - wordTemp; j <= i; j++) {
                        int c = getChar(text, j);
                        insertData(points, texels, j, letterX, letterY, chrHeight, chrWidth, charRegion[c], z);
                        letterX += (charWidths[c] + spaceX ) * scaleX;
                    }
                /** Else Go to the next line and insert it there, split the word to multiple lines
                 * if necessary. */
                } else {
                    if (wordWidth < lineWidth) {
                        letterX = 0;
                        letterY -= cellHeight * scaleY;
                        if (cut && y - boxHeight > letterY - cellHeight * scaleY) {
                            return new Text(points, color, textureId, texels);
                        }
                    }

                    for (int j = i - wordTemp; j <= i; j++) {
                        int c = getChar(text, j);
                        if (letterX + (charWidths[c] + spaceX) * scaleX > lineWidth) {
                            letterX = 0;
                            letterY -= cellHeight * scaleY;
                            if (cut && y - boxHeight > letterY - cellHeight * scaleY) {
                                return new Text(points, color, textureId, texels);
                            }
                        }

                        insertData(points, texels, j, letterX, letterY, chrHeight, chrWidth, charRegion[c], z);
                        letterX += (charWidths[c] + spaceX) * scaleX;
                    }
                }

                /** Reset word properties. */
                wordWidth = 0.0f;
                wordTemp = 0;
            } else {
                /** Edit word properties. */
                int c = getChar(text, i);
                wordWidth += (charWidths[c] + spaceX ) * scaleX;
                wordTemp++;
            }
        }

        /** Insert the last characters that were not followed by a space. */
        if (wordTemp != 0) {
            for (int j = i - wordTemp; j < i; j++) {
                int c = getChar(text, j);
                if (letterX + (charWidths[c] + spaceX) * scaleX > lineWidth) {
                    letterX = 0;
                    letterY -= cellHeight * scaleY;
                    if (cut && y - boxHeight > letterY - cellHeight * scaleY) {
                        return new Text(points, color, textureId, texels);
                    }
                }

                insertData(points, texels, j, letterX, letterY, chrHeight, chrWidth, charRegion[c], z);
                letterX += (charWidths[c] + spaceX) * scaleX;
            }
        }

        return new Text(points, color, textureId, texels);
    }

    private void insertData(float[] points, float[] texels, int i, float letterX, float letterY,
                            float chrHeight, float chrWidth, TextureRegion region, float z) {
        /** Insert the given data into the arrays for the Drawable. */
        points[i * 6 * 3] = letterX;
        points[i * 6 * 3 + 1] = letterY - chrHeight;
        points[i * 6 * 3 + 2] = z;
        points[i * 6 * 3 + 3] = letterX;
        points[i * 6 * 3 + 4] = letterY;
        points[i * 6 * 3 + 5] = z;
        points[i * 6 * 3 + 6] = letterX + chrWidth;
        points[i * 6 * 3 + 7] = letterY - chrHeight;
        points[i * 6 * 3 + 8] = z;
        points[i * 6 * 3 + 9] = letterX + chrWidth;
        points[i * 6 * 3 + 10] = letterY;
        points[i * 6 * 3 + 11] = z;
        points[i * 6 * 3 + 12] = letterX;
        points[i * 6 * 3 + 13] = letterY;
        points[i * 6 * 3 + 14] = z;
        points[i * 6 * 3 + 15] = letterX + chrWidth;
        points[i * 6 * 3 + 16] = letterY - chrHeight;
        points[i * 6 * 3 + 17] = z;

        texels[i * 6 * 2] = region.u1;
        texels[i * 6 * 2 + 1] = region.v2;
        texels[i * 6 * 2 + 2] = region.u1;
        texels[i * 6 * 2 + 3] = region.v1;
        texels[i * 6 * 2 + 4] = region.u2;
        texels[i * 6 * 2 + 5] = region.v2;
        texels[i * 6 * 2 + 6] = region.u2;
        texels[i * 6 * 2 + 7] = region.v1;
        texels[i * 6 * 2 + 8] = region.u1;
        texels[i * 6 * 2 + 9] = region.v1;
        texels[i * 6 * 2 + 10] = region.u2;
        texels[i * 6 * 2 + 11] = region.v2;
    }

    public void setScaleX(float scaleX) {
        /** Set the x scale. */
        this.scaleX = scaleX;
    }

    public void setScaleY(float scaleY) {
        /** Set the y scale. */
        this.scaleY = scaleY;
    }

    public int getTextureId() {
        /** Get the texture id. */
        return textureId;
    }

    public float getTextHeight() {
        /** Get the text height. */
        return cellHeight * scaleY;
    }
}
