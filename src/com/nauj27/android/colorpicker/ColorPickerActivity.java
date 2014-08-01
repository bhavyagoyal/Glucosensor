/**
 *  Color Picker by Juan Martín
 *  Copyright (C) 2012 nauj27.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.nauj27.android.colorpicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nauj27.android.colorpicker.ral.RalColor;
//import com.nauj27.android.colorpicker.DragRectView;
/**
 * @author Juan Martín
 *
 */
public class ColorPickerActivity extends Activity {
	private static final String APPLICATION_NAME = "Glucosensor";
	
	private static final int CAPTURE_ACTIVITY_REQUEST_CODE = 100;
	private static final int SELECT_ACTIVITY_REQUEST_CODE = 200;
	
	private static final String KEY_PHOTO_PATH = "photoUri";
	private static final String KEY_COLOR_COMPONENTS = "rgb";
	
	private Uri photoUri;
	public static ImageView imageView;
	private RalColor ralColor = null;
	public static int redAverage = 0;
	public static int greenAverage = 0;
	public static int blueAverage = 0;
	public static int firstR=0;
	public static int secondR=0;
	public static int level=0;
	public static Button b;
	public static Button c;
	public DragRectView drrect;
	public static double ans=1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(APPLICATION_NAME, "onCreate method entered");
		setContentView(R.layout.activity_color_picker);
		b = (Button) findViewById(R.id.helppage);
		c = (Button) findViewById(R.id.camera);
		level=0;
		b.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	            Intent i = new Intent(ColorPickerActivity.this, Help.class);
	            startActivity(i);
	    	}
	    });
		
		c.setText("Start");
	    c.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	if(level==2){
	        		level=0;
	        		secondR=redAverage;
					ans = Math.pow((secondR-firstR),3.6777);
					ans = Math.floor(ans*1000)/1000;
					TextView vi = (TextView) findViewById(R.id.textView4);
					vi.setText("Glucose "+ans+" mg/dl");
	        		c.setText("StartAgain");
	        	}
	        	else{
					Toast.makeText(
							getApplicationContext(), "Zoom on the strip in the middle of the screen, capture the image and then Save", 
							Toast.LENGTH_LONG).show();

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					photoUri = getOutputMediaFileUri();
					if (photoUri == null) {
					}
					
					intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
					startActivityForResult(intent, CAPTURE_ACTIVITY_REQUEST_CODE);
	        		
	        	}
	        }

	    });
	    
		if (imageView == null) {
			imageView = (ImageView)findViewById(R.id.imageView);
			if (imageView != null) {
				ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
				viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						if (photoUri != null) {
							try {
								showCapturedImage();
								//updateResultData();
							} catch (FileNotFoundException e) {
								Log.e(APPLICATION_NAME, "File ".
										concat(photoUri.getPath()).concat(" not found!"));
							} catch (Exception e) {
								// Ignore
							}
						}
						
						/**
				         * Set the listener for touch event into the image view.
				         */
				        imageView.setOnTouchListener(onTouchListener);
						imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
				});
			}
		}
		Toast.makeText(
				this, "Tap on Start button to take a picture of the strip",
				Toast.LENGTH_LONG).show();


		//drawingImageView = (ImageView) this.findViewById(R.id.DrawingImageView);
//	    Bitmap bitmap = Bitmap.createBitmap((int) getWindowManager()
//	        .getDefaultDisplay().getWidth(), (int) getWindowManager()
//	        .getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
//	    Canvas canvas = new Canvas(bitmap);
//	    imageView.setImageBitmap(bitmap);

	    // Rectangle

//	    Paint paint = new Paint();
//	    paint.setColor(Color.GREEN);
//	    paint.setStyle(Paint.Style.FILL_AND_STROKE);
//	    paint.setStrokeWidth(10);
//	    float leftx = 200;
//	    float topy = 200;
//	    float rightx = 250;
//	    float bottomy = 250;
//	    canvas.drawRect(leftx, topy, rightx, bottomy, paint);		
		
		
	    if (savedInstanceState != null) {
			String photoUriPath = savedInstanceState.getString(KEY_PHOTO_PATH);
			if (photoUriPath != null) {
				photoUri = Uri.fromFile(new File(photoUriPath));
			}
			
			if (savedInstanceState.containsKey(KEY_COLOR_COMPONENTS)) {
				ralColor = new RalColor(
						savedInstanceState.getInt(KEY_COLOR_COMPONENTS));
			}
		}
//		TextView textViewRgb = (TextView)findViewById(R.id.textViewRgb);
//		textViewRgb.setText(
//			"RGBf: ");
			
	}
	
	public void updateResultData() {
//		int index = 0;
//		int red = redAverage;
//		Log.d("d", "d");
		// Set the color name from localized resource
//		Toast.makeText(
//				this, "updateresult",
//				Toast.LENGTH_LONG).show();

		Log.d("level",""+level);
		if(level==0){
			level++;
			c.setText("Second Image");
		}
		else if(level==1){
			level++;
			firstR=redAverage;			
			c.setText("Finish");
		}
		
//		ImageView imageViewColor = (ImageView)findViewById(R.id.imageViewColor);
//		imageViewColor.setBackgroundColor(ralColor.getColor());
//		
//		TextView textViewRgb = (TextView)findViewById(R.id.textViewRgb);
//		textViewRgb.setText(
//			"RGB: ".concat(Integer.toString(red , 10)));
			
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(APPLICATION_NAME, "onResume method entered");
		
		imageView = (ImageView) findViewById(R.id.imageView);
		if (imageView != null) {
			Log.d("here","1");
			ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
			viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				
				@SuppressWarnings("deprecation")
				@Override
				public void onGlobalLayout() {
					Log.d("here","2");
					if (photoUri != null) {
						Log.d("here","3");
						try {
							showCapturedImage();
							//updateResultData();
						} catch (FileNotFoundException e) {
							Log.e(APPLICATION_NAME, "File ".
									concat(photoUri.getPath()).concat(" not found!"));
						} catch (Exception e) {
							// Ignore
						}
					}
					
					/**
			         * Set the listener for touch event into the image view.
			         */
			        imageView.setOnTouchListener(onTouchListener);
					imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}

			});
//			Toast.makeText(this, "tap on the centre of strip" , 
//					Toast.LENGTH_LONG).show();

		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.d(APPLICATION_NAME, "onStop method entered");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(APPLICATION_NAME, "onDestroy method entered");
	}

			
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(APPLICATION_NAME, "onSaveInstanceState");
		
		if (photoUri != null) {
			String realPath;
			try {
				realPath = getRealPathFromURI(photoUri);
			} catch (UnsupportedEncodingException|NullPointerException e) {
				realPath = null;
			}
			outState.putString(KEY_PHOTO_PATH, realPath);
		}
		Log.d(APPLICATION_NAME, "onSaveInstanceStateend");
		
		if (ralColor != null) {
			outState.putInt(KEY_COLOR_COMPONENTS, ralColor.getColor());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_color_picker, menu);
		return true;
	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem menuItem) {
//		switch (menuItem.getItemId()) {
//		case R.id.picture_from_camera:
//			Toast.makeText(
//					this, "Zoom on the strip in the middle of the screen, capture the image and then Save", 
//					Toast.LENGTH_LONG).show();
//			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			photoUri = getOutputMediaFileUri();
//			if (photoUri == null) {
//				Toast.makeText(
//						this, R.string.cant_write_external_storage, 
//						Toast.LENGTH_LONG).show();
//				return true;
//			}
//			
//			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//			startActivityForResult(intent, CAPTURE_ACTIVITY_REQUEST_CODE);
////			Toast.makeText(this, "tap on the centre of strip" , 
////					Toast.LENGTH_LONG).show();
//
//			return true;
//			
//		case R.id.picture_from_gallery:
//			Intent intentGallery = new Intent();
//			intentGallery.setType("image/*");
//			intentGallery.setAction(Intent.ACTION_GET_CONTENT);
//			
//			startActivityForResult(
//				Intent.createChooser(
//						intentGallery, 
//						getString(R.string.select_picture)),
//				SELECT_ACTIVITY_REQUEST_CODE);
////			Toast.makeText(this, "tap on the centre of strip" , 
////					Toast.LENGTH_LONG).show();
//
//			return true;
//			
//		default:
//			return super.onOptionsItemSelected(menuItem);
//		}
//
//	}
	
	OnTouchListener onTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			
			int action = motionEvent.getAction();
			switch(action) {
				case(MotionEvent.ACTION_DOWN):
					Log.d("got it","a");
					//ans = Math.pow(2, 4);
					TextView v = (TextView) findViewById(R.id.textView4);
					v.setText("");
			}
			return true;
		}
	};
	
	@Override
	protected void onActivityResult(
		int requestCode, 
		int resultCode, 
		Intent data) {
		if (requestCode == CAPTURE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Log.i(APPLICATION_NAME, "Capture result OK");
				updateResultData();
				try {
					showCapturedImage();
				} catch (FileNotFoundException fileNotFoundException) {
					// Image decode failed, advise user
				} catch (NullPointerException nullPointerException) {
					imageView = (ImageView)findViewById(R.id.imageView);
					try {
						showCapturedImage();
					} catch (Exception exception) {
						// Do nothing
					}
				}
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
				Toast.makeText(this, R.string.action_canceled,
						Toast.LENGTH_SHORT).show();
			} else {
				// Image capture failed, advise user
			}
		}
		
		if (requestCode == SELECT_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Log.i(APPLICATION_NAME, "Select result OK");
				updateResultData();
				try {
					photoUri = data.getData();
					showCapturedImage();
				} catch (FileNotFoundException e) {
					// Image decode failed, advise user
				} catch (NullPointerException e) {
					imageView = (ImageView)findViewById(R.id.imageView);
					try {
						showCapturedImage();
					} catch (Exception exception) {
						// Do nothing
					}
				}
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
				Toast.makeText(this, R.string.action_canceled,
						Toast.LENGTH_SHORT).show();
			} else {
				// Image capture failed, advise user
			}
		}
		
		if (resultCode == RESULT_OK) {
			imageView.setOnTouchListener(onTouchListener);
		}
		Toast.makeText(this, "tap on the centre of strip" , 
				Toast.LENGTH_LONG).show();

	}

	/**
	 * Shows the image captured by the camera into the image view
	 * @param data
	 * @throws FileNotFoundException 
	 */
	private void showCapturedImage() throws 
	FileNotFoundException, NullPointerException {
		//ImageView imageView = (ImageView)findViewById(R.id.imageView);
		if (imageView == null) {
			throw new NullPointerException();
		}
		
		// Get the dimensions of the container
		FrameLayout frameLayoutImage = (FrameLayout)findViewById(R.id.frameLayoutImage);
		int targetW = frameLayoutImage.getWidth();
	    int targetH = frameLayoutImage.getHeight();
	  
	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeStream(
	    		this.getContentResolver().openInputStream(photoUri), 
	    		null, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;
	  
	    // Determine how much to scale down the image
	    int scaleFactor = 1;
	    try {
	    	scaleFactor = Math.min(photoW/targetW, photoH/targetH);
	    } catch (ArithmeticException arithmeticException) {
	    	Log.w(APPLICATION_NAME, "frameLayout not yet inflated, no scaling");
	    }
	    
	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;
	  
	    try {
		    Bitmap bitmap = BitmapFactory.decodeStream(
		    		this.getContentResolver().openInputStream(photoUri), 
		    		null, bmOptions);
		    
		    int bitmapSize = bitmap.getRowBytes() * bitmap.getHeight();
		    //int bitmapSize = bitmap.getByteCount(); // API 12
		    
		    if ( bitmapSize > 20000000) {
		    	bmOptions.inSampleSize = 2;
		    	bitmap = BitmapFactory.decodeStream(
			    		this.getContentResolver().openInputStream(photoUri), 
			    		null, bmOptions);
		    }
		    
		    imageView.setImageBitmap(bitmap);
	    } catch (OutOfMemoryError e) {
	    	Log.e(APPLICATION_NAME, e.getLocalizedMessage());
	    }
	 //updateResultData();   
	}
	
	/** Create a file Uri for saving an image */
	private static Uri getOutputMediaFileUri(){
		try {
			return Uri.fromFile(getOutputMediaFile());
		} catch(NullPointerException e) {
			return null;
		}
	}

	/** Create a File for saving an image */
	private static File getOutputMediaFile(){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(
	    		Environment.getExternalStoragePublicDirectory(
	    			Environment.DIRECTORY_PICTURES), APPLICATION_NAME);

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.e(APPLICATION_NAME, "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file pseudo random name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).
	    		format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
        "IMG_"+ timeStamp + ".jpg");

	    return mediaFile;
	}	

	@SuppressWarnings("deprecation")
	private String getRealPathFromURI(Uri contentUri) throws UnsupportedEncodingException {
		String realPath;
//		Log.d("before","d");

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
	//	Log.d("before2","d");
        if (cursor!=null) {
    	//	Log.d("before7","d");
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        cursor.moveToFirst();
			//Log.d("before3",""+column_index);
	        realPath = cursor.getString(column_index);
	        //cursor.close();
        } else {
    		//Log.d("before8","d");
        	realPath = contentUri.toString();
        }
	//	Log.d("before3",""+realPath);
        
        // Hack to avoid file:// repetition
        if (realPath.startsWith("file://")) {
    		//Log.d("before5","d");
            	realPath = realPath.substring("file://".length());
        }
//		Log.d("before4","d");
        return URLDecoder.decode(realPath, "UTF-8");
    }
	
	
}
	
	
	
	
	
	
