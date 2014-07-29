package com.nauj27.android.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
public class DragRectView extends View{

    private Paint mRectPaint;

    private int mStartX = 0;
    private int mStartY = 0;
    private int mEndX = 0;
    private int mEndY = 0;
    private boolean mDrawRect = false;
    private TextPaint mTextPaint = null;
    private TextPaint mTextPaint2 = null;
    public ImageView imageVie;
    private OnUpCallback mCallback = null;
    public boolean clearCanvas=false;
    public interface OnUpCallback {
        void onRectFinished(Rect rect);
    }

    public DragRectView(final Context context) {
        super(context);
        init();
    }

    public DragRectView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragRectView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Sets callback for up
     *
     * @param callback {@link OnUpCallback}
     */
    public void setOnUpCallback(OnUpCallback callback) {
        mCallback = callback;
    }

    
    private void init() {
        mRectPaint = new Paint();
        mRectPaint.setColor(getContext().getResources().getColor(R.color.red));
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(5); // TODO: should take from resources

        mTextPaint = new TextPaint();
        mTextPaint.setColor(getContext().getResources().getColor(R.color.red));
        mTextPaint.setTextSize(50);
        
        mTextPaint2 = new TextPaint();
        mTextPaint2.setColor(getContext().getResources().getColor(R.color.red));
        mTextPaint2.setTextSize(30);       
        mTextPaint2.setTextAlign(Align.RIGHT);
        
		//setContentView(R.layout.activity_color_picker);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {

        // TODO: be aware of multi-touches
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	imageVie = ColorPickerActivity.imageView;
                mDrawRect = false;
                mStartX = imageVie.getLeft();
                mStartY = imageVie.getTop();
                invalidate();
                    mEndX = imageVie.getRight();
                    mEndY = imageVie.getBottom();
                    invalidate();
                mDrawRect = true;
				int x;
				int y;
				int color;
		    	BitmapDrawable bitmapDrawable = (BitmapDrawable)imageVie.getDrawable();
		    	Bitmap imageBitmap = bitmapDrawable.getBitmap();
				x = imageBitmap.getWidth() /2;//Math.round(event.getX()); //imageBitmap.getWidth() /2;
				y = imageBitmap.getHeight() /2;//Math.round(event.getY());
		    	int pixelsNumber=0;
		    	ColorPickerActivity.redAverage=0;
				int n = x/20;
				int m = y/20;
				for(int p=-2;p<3;p++){
					for(int q=-2;q<3;q++){
						int a = Math.round((event.getX()-mStartX)*imageBitmap.getWidth() /imageVie.getWidth()) +p*n;//x+n/2-i;
						int b = Math.round((event.getY()-mStartY)*imageBitmap.getHeight() /imageVie.getHeight()) +q*m;//x+n/2-i;
						//int b = Math.round(event.getY()-mStartY)+q*m);//y+m/2-j;
		    			try {
		        			color = imageBitmap.getPixel(a, b);
		        			ColorPickerActivity.redAverage += Color.red(color);
		        			pixelsNumber += 1;
		        			Log.d("a and b",""+a+" "+b+" "+Color.red(color));
		        			//Log.d("a and b",""+a+" "+b);
		        		} catch(Exception e) {
		        			//Log.w(TAG, "Error picking color!");
		        		}	
					}
				}		
				x = imageVie.getWidth()/2;//imageBitmap.getWidth() /2;//Math.round(event.getX()); //imageBitmap.getWidth() /2;
				y = imageVie.getHeight() /2;//Math.round(event.getY());
				n=x/5;
				m=y/5;
				mStartX  = Math.round(event.getX()-n/2);
				mStartY = Math.round(event.getY()-m/2);
				mEndX = mStartX+n;
				mEndY = mStartY+m;
//				Log.d("ex",""+event.getX());
//				Log.d("ey",""+event.getY());
//                Log.d("l",""+mStartX);
//                Log.d("t",""+mStartY);
//                Log.d("h",""+imageVie.getWidth());
//                Log.d("w",""+imageVie.getHeight());
//                Log.d("h",""+imageBitmap.getWidth());
//                Log.d("w",""+imageBitmap.getHeight());
//                Log.d("l",""+mEndX);
//                Log.d("t",""+mEndY);
				
                if(pixelsNumber!=0){
    		    	ColorPickerActivity.redAverage = ColorPickerActivity.redAverage / pixelsNumber;                	
                }

                break;

            case MotionEvent.ACTION_UP:
                if (mCallback != null) {
                    mCallback.onRectFinished(new Rect(mStartX, mStartY,
                            mEndX, mEndY));
                }
                                
                invalidate();
                break;

            default:
                break;
        }

        return false;
    }
    
    
//    public void clearCanvas(){
//
//        //canvas.drawColor(0, Mode.CLEAR);
//
//        clearCanvas = true;
//        invalidate();
//    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
//        if(clearCanvas)
//        {  // Choose the colour you want to clear with.
//            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
////            canvas.drawColor(0, Mode.CLEAR);
//            clearCanvas = false;
//        }
        if (mDrawRect) {
            canvas.drawRect(Math.min(mStartX, mEndX), Math.min(mStartY, mEndY),
                    Math.max(mEndX, mStartX), Math.max(mEndY, mStartY), mRectPaint);
//            canvas.drawText(""+""+ColorPickerActivity.redAverage+" mg/dl",//+" "+ColorPickerActivity.greenAverage+" "+ColorPickerActivity.blueAverage,
//                    10,40,mTextPaint);//Math.max(mEndX, mStartX), Math.max(mEndY, mStartY), mTextPaint);
//            canvas.drawText(""+""+ColorPickerActivity.firstR+" mg/dl",//+" "+ColorPickerActivity.greenAverage+" "+ColorPickerActivity.blueAverage,
//                    10,80,mTextPaint);//Math.max(mEndX, mStartX), Math.max(mEndY, mStartY), mTextPaint);
//            canvas.drawText(""+""+ColorPickerActivity.secondR+" mg/dl",//+" "+ColorPickerActivity.greenAverage+" "+ColorPickerActivity.blueAverage,
//                    10,120,mTextPaint);//Math.max(mEndX, mStartX), Math.max(mEndY, mStartY), mTextPaint);
//            canvas.drawText(""+ColorPickerActivity.level,//+" "+ColorPickerActivity.greenAverage+" "+ColorPickerActivity.blueAverage,
//                    10,160,mTextPaint);//Math.max(mEndX, mStartX), Math.max(mEndY, mStartY), mTextPaint);
        }
    }
}