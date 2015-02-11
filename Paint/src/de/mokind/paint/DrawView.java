package de.mokind.paint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.hardware.input.InputManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

@SuppressLint("ClickableViewAccessibility")
public class DrawView extends ImageView {

	private float x = -1;
	private float y= -1;
	private float size= -1;
	
	private DisplayMetrics metrics = getResources().getDisplayMetrics();
	private Bitmap drawBitmap = null;
	private boolean bitmapDirty = false;
	
	private Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	public DrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DrawView(Context context) {
		super(context);
		init();
	}

    @Override
    public void onDraw(Canvas canvas){
        if (drawBitmap == null){
        	drawBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.RGB_565);
        	Canvas myCanvas = new Canvas(drawBitmap);
        	myCanvas.drawARGB(255, 255, 255, 255);
        	bitmapDirty = false;
        }
        canvas.drawBitmap(drawBitmap, 0, 0, p);
        super.onDraw(canvas);
    }
    
    public void setPaintColorARGB(int alpha, int red, int green, int blue){
    	p.setARGB(alpha, red, green, blue);
    }
    
    public void clearBackround(){
    	if (drawBitmap != null){
    		drawBitmap = Bitmap.createBitmap(drawBitmap.getWidth(), drawBitmap.getHeight(), Bitmap.Config.RGB_565);
        	Canvas myCanvas = new Canvas(drawBitmap);
        	myCanvas.drawARGB(255, 255, 255, 255);
        	bitmapDirty = false;
        	invalidate();
    	}    	
    }
    
    private void init(){
    	
    	// set paint
    	p.setARGB(255, 235, 0, 0);
    	p.setStrokeJoin(Join.ROUND);
    	p.setStrokeCap(Cap.ROUND);
    	
    	// set touch listener
    	setOnTouchListener(new View.OnTouchListener() {
			
			@SuppressLint({ "InlinedApi", "ClickableViewAccessibility" })
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (event.getAction() == MotionEvent.ACTION_DOWN || 
					event.getAction() == MotionEvent.ACTION_UP ||
					event.getAction() == MotionEvent.ACTION_MOVE)
				{
					float newsize = 1.0f / 20.0f; // default
					InputDevice.MotionRange range = event.getDevice().getMotionRange(MotionEvent.AXIS_TOUCH_MAJOR);
					if (range != null){
						// TOUCH_MAJOR
						newsize = (event.getToolMinor() + event.getTouchMajor()) / 2 / range.getMax();
						newsize = newsize * 3.0f; // push the size effect
						newsize = newsize * newsize ; 
						
					}else{
						// TOUCH_PRESSURE
						range = event.getDevice().getMotionRange(MotionEvent.AXIS_PRESSURE);
						if (range != null){
							newsize = event.getPressure() / range.getMax() * newsize * 2 ;
							newsize = newsize * 4.0f; // push the size effect
							newsize = newsize * newsize ; 
						}else{
							// TOUCH_SIZE
							range = event.getDevice().getMotionRange(MotionEvent.AXIS_SIZE);
							if (range != null){
								newsize = event.getSize() / range.getMax();
								newsize = newsize * 0.65f; // push the size effect
								newsize = newsize * newsize ; 
							}
						}
					}
					newsize = Math.max(1.0f / 70.0f, newsize);
					newsize = Math.min(1.0f / 6.0f, newsize);
					newsize = newsize * metrics.widthPixels; // shift to pixel
					
					
                    float newx = event.getX();
                    float newy = event.getY();
                    
                    if (event.getAction() == MotionEvent.ACTION_DOWN){
                    	size = newsize;
                        x = newx;
                        y = newy;
                    }
                    
                    if (drawBitmap != null && x > -1){
                    	bitmapDirty = true;
                    	Canvas myCanvas = new Canvas(drawBitmap);
                    	p.setStrokeWidth(size);
                    	p.setStrokeMiter(size);
                    	myCanvas.drawLine(x, y, newx, newy, p);
                    }
                    
                    size = newsize;
                    x = newx;
                    y = newy;
                    
                }
				invalidate();	
				return true;
			}
		});
    }

	public Bitmap getDrawBitmap() {
		if (bitmapDirty){ // only return bitmap when something is painted
			return drawBitmap;
		}else{
			return null;
		}
	}

	public void setDrawBitmap(Bitmap drawBitmap) {
		if (drawBitmap != null){
			bitmapDirty = true;
		}
		this.drawBitmap = drawBitmap;
	}


}
