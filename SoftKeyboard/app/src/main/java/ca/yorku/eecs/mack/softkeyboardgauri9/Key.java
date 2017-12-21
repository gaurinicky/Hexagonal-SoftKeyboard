package ca.yorku.eecs.mack.softkeyboardgauri9;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.ToneGenerator;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

/**
 * Key - a class for keys for the soft keyboard
 * <p>
 *
 * @author (c) Scott MacKenzie, 2015-2017
 */
public class Key extends View
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages
    private final int UP_FILL_COLOR = Color.GRAY;
    private final int DOWN_FILL_COLOR = 0xff8080ff;
    private final int BORDER_COLOR = Color.BLACK;

    // requirements for key "type"
    private final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijlkmnopqrstuvwxyz";
    private final String SPACE = "Space";
    private final String ENTER = "Enter";
    private final String BACKSPACE = "Bksp";

    //public int width;
    //public int height;
    //Paint linePaint, fillPaint, textPaint;
    Rect keyShape;
    String keyText;
    int keyType;
    int charCode;
    //int xText, yText;

    //stuff for hex
    private int DEFAULT_WIDTH = 130;
    private int DEFAULT_HEIGHT = 130;

    // Set the shape our our hexagonal button by defining arrays to scale the x and y points of a six-point path.
    // NOTE: The values are scaling factors, which are multiplied by the view width and view height.
    private float[] BUTTON_POINTS_X_FACTOR = {0.00f, 0.50f, 1.00f, 1.00f, 0.50f, 0.00f};
    private float[] BUTTON_POINTS_Y_FACTOR = {0.25f, 0.00f, 0.25f, 0.75f, 1.00f, 0.75f};

    //ENTER
    private float[] BUTTON_ENTER_X = {0.00f, 0.50f, 1.00f, 1.50f, 2.00f, 2.00f, 1.50f, 1.00f, 0.50f, 0.00f};
    private float[] BUTTON_ENTER_Y = {0.25f, 0.00f, 0.25f, 0.00f, 0.25f, 0.75f, 1.00f, 0.75f, 1.00f, 0.75f};

    //SPACE BAR
    private float[] BUTTON_SPACE_X = {0.00f, 0.50f, 1.00f, 1.50f, 2.00f, 2.50f, 3.00f, 3.50f, 4.00f, 4.00f, 3.50f, 3.00f, 2.50f, 2.00f, 1.50f, 1.00f, 0.50f, 0.00f};
    private float[] BUTTON_SPACE_Y = {0.25f, 0.00f, 0.25f, 0.00f, 0.25f, 0.00f, 0.25f, 0.00f, 0.25f, 0.75f, 1.00f, 0.75f, 1.00f, 0.75f, 1.00f, 0.75f, 1.00f, 0.75f};

    // used for layout params (see DemoCustomButtonActivity)
    final static float LEFT_MARGIN_ADJ = 0.50f; // from above x-factor array
    final static float TOP_MARGIN_ADJ = 0.75f; // from above y-factor array

    public int width;
    public int height;
    float hexagonX[], hexagonY[];
    Paint linePaint, fillPaint, textPaint;
    Path buttonPath;
    String buttonText;
    int xText, yText;
    boolean isPressed;
    ToneGenerator tg;


    /**
     * Construct a Key object.
     */
    public Key(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize();
    }

    /**
     * Construct a Key object.
     */
    public Key(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    /**
     * Construct a Key object.
     */
    public Key(Context context)
    {
        super(context);
        initialize();
    }

    private void initialize()
    {
        setFocusableInTouchMode(true);
        setFocusable(true);

        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(5);
        linePaint.setColor(BORDER_COLOR);

        fillPaint = new Paint();
        fillPaint.setColor(UP_FILL_COLOR);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        //added gauri
        isPressed = false;
        keyText = "?"; //default

        //setButtonSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
       if(!SoftKeyboardSetup.keyshape.equals("Hexagon"))
        {
            keyShape = new Rect();
            DEFAULT_HEIGHT = 130;
            DEFAULT_WIDTH = 130;
        }
        else
        {
            DEFAULT_WIDTH = 130;
            DEFAULT_HEIGHT = 130;
        }

        //keyShape = new Rect();
    }

    /*
     * Since we are extending View, we must override onMeasure. See DemoCustomButton for further
     * discussion.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(width, height);
    }

    /*
     * Draw the key. Modify the code here to create the desired appearance.
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(SoftKeyboardSetup.keyshape.equals("Square"))
        {
            canvas.drawRect(keyShape, fillPaint);
            canvas.drawRect(keyShape, linePaint);
            canvas.drawText(keyText, xText, yText, textPaint);

        }
        else
        {
            canvas.drawPath(buttonPath, fillPaint);
            canvas.drawPath(buttonPath, linePaint);
            canvas.drawText(keyText, xText, yText, textPaint);
        }

        //canvas.drawRect(keyShape, fillPaint);
        //canvas.drawRect(keyShape, linePaint);
        //canvas.drawText(keyText, xText, yText, textPaint);


    }

    public void initializeKey(String textArg, int widthArg, int heightArg)
    {
        keyText = textArg;
        width = widthArg;
        height = heightArg;
        if(!SoftKeyboardSetup.keyshape.equals("Square"))
        {
            // set the key's "type" and character code based on the key text
            if (ALPHA.contains(keyText))
            {
                keyType = KeyboardEvent.TYPE_ALPHA;
                keyText = keyText.toLowerCase(Locale.CANADA);
                charCode = keyText.toLowerCase(Locale.CANADA).charAt(0);
                setButtonSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
                setTextSize(0.7f);

                xText = width / 2;
                yText = height / 2 - (int)(textPaint.ascent() / 3);

            } else if (keyText.equals(SPACE))
            {
                //keyShape.left = 0;
                //keyShape.right = width;
                //keyShape.top = 0;
                //keyShape.bottom = height;
                BUTTON_POINTS_X_FACTOR = BUTTON_SPACE_X;
                BUTTON_POINTS_Y_FACTOR = BUTTON_SPACE_Y;
                keyType = KeyboardEvent.TYPE_SPACE;
                charCode = KeyboardEvent.CHAR_SPACE;
                setButtonSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
                setTextSize(0.5f);

                xText = width*2;
                yText = height/2 - (int)(textPaint.ascent() / 3);

            } else if (keyText.equals(ENTER))
            {
                //keyShape.left = 0;
                //keyShape.right = width;
                //keyShape.top = 0;
                //keyShape.bottom = height;
                BUTTON_POINTS_X_FACTOR = BUTTON_ENTER_X;
                BUTTON_POINTS_Y_FACTOR = BUTTON_ENTER_Y;
                keyType = KeyboardEvent.TYPE_ENTER;
                charCode = KeyboardEvent.CHAR_ENTER;
                setButtonSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
                setTextSize(0.5f);

                xText = width;
                yText = height/2 - (int)(textPaint.ascent() / 3);

            } else if (keyText.equals(BACKSPACE))
            {
                //keyShape.left = 0;
                //keyShape.right = width;
                //keyShape.top = 0;
                //keyShape.bottom = height;
                BUTTON_POINTS_X_FACTOR = BUTTON_ENTER_X;
                BUTTON_POINTS_Y_FACTOR = BUTTON_ENTER_Y;
                keyType = KeyboardEvent.TYPE_BACKSPACE;
                charCode = KeyboardEvent.CHAR_BACKSPACE;
                setButtonSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
                setTextSize(0.5f);

                xText = width;
                yText = height/2 - (int)(textPaint.ascent() / 3);


            } else
            {
                keyType = KeyboardEvent.TYPE_UNDEFINED;
                charCode = KeyboardEvent.CHAR_NULL;
                setButtonSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
                xText = width / 2;
                yText = height / 2 - (int)(textPaint.ascent() / 3);
            }

        }
        else
        {
            // set the key's "type" and character code based on the key text
            if (ALPHA.contains(keyText))
            {
                keyType = KeyboardEvent.TYPE_ALPHA;
                keyText = keyText.toLowerCase(Locale.CANADA);
                charCode = keyText.toLowerCase(Locale.CANADA).charAt(0);
                //setButtonSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
                setTextSize(0.7f);

                //xText = width / 2;
                //yText = height / 2 - (int)(textPaint.ascent() / 3);

            } else if (keyText.equals(SPACE))
            {
                //keyShape.left = 0;
                //keyShape.right = width;
                //keyShape.top = 0;
                //keyShape.bottom = height;
                //BUTTON_POINTS_X_FACTOR = BUTTON_SPACE_X;
                //BUTTON_POINTS_Y_FACTOR = BUTTON_SPACE_Y;
                keyType = KeyboardEvent.TYPE_SPACE;
                charCode = KeyboardEvent.CHAR_SPACE;
                //setButtonSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
                setTextSize(0.5f);

                //xText = width*2;
                //yText = height/2 - (int)(textPaint.ascent() / 3);

            } else if (keyText.equals(ENTER))
            {
                //keyShape.left = 0;
                //keyShape.right = width;
                //keyShape.top = 0;
                //keyShape.bottom = height;
                //BUTTON_POINTS_X_FACTOR = BUTTON_ENTER_X;
                //BUTTON_POINTS_Y_FACTOR = BUTTON_ENTER_Y;
                keyType = KeyboardEvent.TYPE_ENTER;
                charCode = KeyboardEvent.CHAR_ENTER;
                //setButtonSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
                setTextSize(0.5f);

                //xText = width;
                //yText = height/2 - (int)(textPaint.ascent() / 3);

            } else if (keyText.equals(BACKSPACE))
            {
                //keyShape.left = 0;
                //keyShape.right = width;
                //keyShape.top = 0;
                //keyShape.bottom = height;
                //BUTTON_POINTS_X_FACTOR = BUTTON_ENTER_X;
                //BUTTON_POINTS_Y_FACTOR = BUTTON_ENTER_Y;
                keyType = KeyboardEvent.TYPE_BACKSPACE;
                charCode = KeyboardEvent.CHAR_BACKSPACE;
                //setButtonSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
                setTextSize(0.5f);

                //xText = width;
                //yText = height/2 - (int)(textPaint.ascent() / 3);


            } else
            {
                keyType = KeyboardEvent.TYPE_UNDEFINED;
                charCode = KeyboardEvent.CHAR_NULL;
                //setButtonSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
                //xText = width / 2;
                //yText = height / 2 - (int)(textPaint.ascent() / 3);
            }

            xText = width / 2;
            yText = height / 2 - (int)(textPaint.ascent() / 3);

            keyShape.left = 0;
            keyShape.right = width;
            keyShape.top = 0;
            keyShape.bottom = height;

        }


    }

    //added gauri
    public void setButtonSize(int widthArg, int heightArg)
    {
        width = widthArg;
        height = heightArg;
        textPaint.setTextSize(width / 3);
        xText = width / 2;
        yText = height / 2 - (int)(textPaint.ascent() / 3);

        // Now do the path. Define hexagon (generic, in terms of width and height)

            hexagonX = new float[BUTTON_POINTS_X_FACTOR.length];
            hexagonY = new float[BUTTON_POINTS_Y_FACTOR.length];
            for (int i = 0; i < hexagonX.length; ++i)
            {
                hexagonX[i] = width * BUTTON_POINTS_X_FACTOR[i];
                hexagonY[i] = height * BUTTON_POINTS_Y_FACTOR[i];
            }

            buttonPath = new Path();
            buttonPath.moveTo(hexagonX[0], hexagonY[0]);
            for (int i = 1; i < hexagonX.length; ++i)
                buttonPath.lineTo(hexagonX[i], hexagonY[i]);
            buttonPath.close();



    }

    /*
     * Set the text size based on the text to display in the key and the size of the key.
     *
     * The argument passed in is a nominal factor that sets the text size as a ratio of key height
     * (e.g., 0.7 x height for alpha keys). A further adjustment is necessary if the text is too
     * wide to fit in the key. This might happen for keys such as SPACE, BACKSPACE, or ENTER
     * (depending on the key width). The adjustment ensures the width of the text does not exceed 85%
     * of the width of the key.
     */
    private void setTextSize(float nominalArg)
    {
        // nominal text size (works fine for alpha keys and wide SPACE keys)
        textPaint.setTextSize(nominalArg * height);

        // adjust the text size for narrow keys
        float textWidth = textPaint.measureText(keyText);
        float textSize = textPaint.getTextSize();
        float newTextSize = textSize * ((0.85f * width) / textWidth);
        if (newTextSize < textSize)
            textPaint.setTextSize(newTextSize);
    }

    public String getText()
    {
        return keyText;
    }

    public void setText(String textArg)
    {
        keyText = textArg;
    }

    public int getType()
    {
        return keyType;
    }

    public int getCharCode()
    {
        return charCode;
    }

    public void setKeyPressed(boolean pressed)
    {
        if (pressed)
            fillPaint.setColor(DOWN_FILL_COLOR);
        else
            fillPaint.setColor(UP_FILL_COLOR);
        invalidate();
    }

}