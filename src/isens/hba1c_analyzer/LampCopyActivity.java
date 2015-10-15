package isens.hba1c_analyzer;

import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.ErrorPopup;
import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.EngineerActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.RunActivity;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.CalibrationActivity.TargetMode;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.R.anim;
import isens.hba1c_analyzer.R.drawable;
import isens.hba1c_analyzer.R.id;
import isens.hba1c_analyzer.R.layout;
import isens.hba1c_analyzer.R.string;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
import isens.hba1c_analyzer.RunActivity.Cart1stFilter2;
import isens.hba1c_analyzer.RunActivity.CartDump;
import isens.hba1c_analyzer.SerialPort.CtrTarget;
import isens.hba1c_analyzer.Presenter.LampPresenter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class LampCopyActivity extends Activity {
	
	final static byte FILTER_DARK  = 1,
					  FILTER_535nm = 2,
					  FILTER_660nm = 3,
					  FILTER_750nm = 4;
	
	public SerialPort mSerialPort;
	public TimerDisplay mTimerDisplay;
	public ErrorPopup mErrorPopup;
	public Graph mGraph;
	
	public Button backBtn,
				  runBtn,
				  cancelBtn,
				  darkBtn,
				  f535nmBtn,
				  f660nmBtn,
				  f750nmBtn;
	
	public TextView adcText;
	
	public ImageView stateFlag1,
					 stateFlag2;
	
	public static boolean isCancel;
	
	public double f535nmValue[] = new double[200];
	
	public int numofSample = 0;
	
	public RunActivity.AnalyzerState photoState;

	public int checkError;
	
	public boolean isNormal = true;
	
	public SurfaceView mSurfaceView;
	
	public TextView adc1Text,
					adc2Text,
					adc3Text,
					adc4Text,
					adc5Text;
	
	public int adcMax,
			   adcMin;
	
	public int whichFilter = (int) FILTER_535nm;
	
	public boolean isMeasured = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.lamp);
		
		LampInit();
	}
	
	public void setImageId() {
		
		stateFlag1 = (ImageView) findViewById(R.id.stateFlag1);
		stateFlag2 = (ImageView) findViewById(R.id.stateFlag2);	
	}
	
	public void setTextId() {

		adcText = (TextView) findViewById(R.id.adcText);
		adc1Text = (TextView) findViewById(R.id.adc1Text);
		adc2Text = (TextView) findViewById(R.id.adc2Text);
		adc3Text = (TextView) findViewById(R.id.adc3Text);
		adc4Text = (TextView) findViewById(R.id.adc4Text);
		adc5Text = (TextView) findViewById(R.id.adc5Text);
	}
	
	public void setButtonId() {
		
		backBtn = (Button)findViewById(R.id.backBtn);
		runBtn = (Button)findViewById(R.id.runBtn);
		cancelBtn = (Button)findViewById(R.id.cancelBtn);
		darkBtn = (Button)findViewById(R.id.darkBtn);
		f535nmBtn = (Button)findViewById(R.id.f535nmBtn);
		f660nmBtn = (Button)findViewById(R.id.f660nmBtn);
		f750nmBtn = (Button)findViewById(R.id.f750nmBtn);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		runBtn.setOnTouchListener(mTouchListener);
		cancelBtn.setOnTouchListener(mTouchListener);
		darkBtn.setOnTouchListener(mTouchListener);
		f535nmBtn.setOnTouchListener(mTouchListener);
		f660nmBtn.setOnTouchListener(mTouchListener);
		f750nmBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state) {
		
		findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				
				switch(v.getId()) {
			
				case R.id.backBtn	:
					WhichIntent(TargetIntent.Engineer);
					break;
					
				case R.id.runBtn	:
					unenabledAllBtn();
					TestStart();
					break;
				
				case R.id.cancelBtn	:
					setButtonState(R.id.cancelBtn, false);
					cancelTest();
					break;
					
				default	:
					break;
				}
				
				break;
				
			case MotionEvent.ACTION_DOWN	:
				
				switch(v.getId()) {
			
				case R.id.darkBtn	:
					displayFilterBtn((int) FILTER_DARK);
					break;
					
				case R.id.f535nmBtn	:
					displayFilterBtn((int) FILTER_535nm);
					break;
					
				case R.id.f660nmBtn	:
					displayFilterBtn((int) FILTER_660nm);
					break;
				
				case R.id.f750nmBtn	:
					displayFilterBtn((int) FILTER_750nm);
					break;
					
				default	:
					break;
				}
				
				break;
			}
			
			return false;
		}
	};
	
	public void enabledAllBtn() {

		setButtonState(R.id.backBtn, true);
		setButtonState(R.id.runBtn, true);
		setButtonState(R.id.cancelBtn, true);
		setButtonState(R.id.darkBtn, true);
		setButtonState(R.id.f535nmBtn, true);
		setButtonState(R.id.f660nmBtn, true);
		setButtonState(R.id.f750nmBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		setButtonState(R.id.backBtn, false);
		setButtonState(R.id.runBtn, false);
		setButtonState(R.id.cancelBtn, false);
		setButtonState(R.id.darkBtn, false);
		setButtonState(R.id.f535nmBtn, false);
		setButtonState(R.id.f660nmBtn, false);
		setButtonState(R.id.f750nmBtn, false);
	}
	
	public void LampInit() {
		
		setImageId();
		setTextId();
		setButtonId();
		setButtonClick();
		
		displayFilterBtn((int) FILTER_535nm);
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.lampLayout);
		
		mSerialPort = new SerialPort();
		
		mSurfaceView = (SurfaceView) findViewById(R.id.graphBg);
		
		mGraph = new Graph(this, mSurfaceView);
		
		ADCAcquire(0);
		MeasureDisplay(false, CoordinateAcquire());
	}
	
	public void displayFilterBtn(int filter) {
		
		whichFilter = filter;
		
		switch(filter) {
		
		case FILTER_DARK	:
			darkBtn.setBackgroundResource(R.drawable.btn_s);
			f535nmBtn.setBackgroundResource(R.drawable.btn);
			f660nmBtn.setBackgroundResource(R.drawable.btn);
			f750nmBtn.setBackgroundResource(R.drawable.btn);
			break;
			
		case FILTER_535nm	:
			darkBtn.setBackgroundResource(R.drawable.btn);
			f535nmBtn.setBackgroundResource(R.drawable.btn_s);
			f660nmBtn.setBackgroundResource(R.drawable.btn);
			f750nmBtn.setBackgroundResource(R.drawable.btn);
			break;
			
		case FILTER_660nm	:
			darkBtn.setBackgroundResource(R.drawable.btn);
			f535nmBtn.setBackgroundResource(R.drawable.btn);
			f660nmBtn.setBackgroundResource(R.drawable.btn_s);
			f750nmBtn.setBackgroundResource(R.drawable.btn);
			break;
			
		case FILTER_750nm	:
			darkBtn.setBackgroundResource(R.drawable.btn);
			f535nmBtn.setBackgroundResource(R.drawable.btn);
			f660nmBtn.setBackgroundResource(R.drawable.btn);
			f750nmBtn.setBackgroundResource(R.drawable.btn_s);
			break;
			
		default	:
			break;
		}
	}
	
	public void TestStart() {

		isCancel = false;
		isNormal = true;
		isMeasured = false;
		
		CancelTest mCancelTest = new CancelTest();
		mCancelTest.start();
		
		photoState = AnalyzerState.MeasurePosition;
		checkError = RunActivity.NORMAL_OPERATION;
		
		TestStart mTestStart = new TestStart(this, this, R.id.lampLayout);
		mTestStart.start();
	}
	
	public class TestStart extends Thread {
		
		Activity activity;
		Context context;
		int layout;
		
		TestStart(Activity activity, Context context, int layout) {
			
			this.activity = activity;
			this.context = context;
			this.layout = layout;
		}
		
		public void run() {
			
			for(int i = 0; i < 6; i++) {
				
				switch(photoState) {
				
				case MeasurePosition :
					MotionInstruct(RunActivity.MEASURE_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.MEASURE_POSITION, AnalyzerState.FilterDark, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 5);
					break;
					
				case FilterDark		:
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					
					if(whichFilter != (int) FILTER_DARK) BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.Filter535nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					else BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.NormalOperation, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					break;
					
				case Filter535nm :
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
										
					if(whichFilter != (int) FILTER_535nm) BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter660nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					else BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.NormalOperation, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					break;
					
				case Filter660nm :
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					
					if(whichFilter != (int) FILTER_660nm) BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter750nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					else BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.NormalOperation, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					break;
					
				case Filter750nm :
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.NormalOperation, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					photoState = AnalyzerState.NoWorking;
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
					
				case NoResponse			:
					photoState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
			
			measurePhoto(activity, context, layout);
		}
	}
	
	public void measurePhoto(Activity activity, Context context, int layout) {
		
		switch(checkError) {
		
		case RunActivity.NORMAL_OPERATION	:
			DrawThread mDrawThread = new DrawThread(mGraph.GetHolder());
			mDrawThread.start();

			PhotoMeasure mPhotoMeasure = new PhotoMeasure(activity, context, layout);
			mPhotoMeasure.start();
		
			new Thread(new Runnable() {			
				public void run() {    
					runOnUiThread(new Runnable(){
						public void run() {

							setButtonState(R.id.cancelBtn, true);
						}
					});
				}
			}).start();
			break;
			
		default	:
			mErrorPopup = new ErrorPopup(activity, context, layout, null, 0);
			mErrorPopup.ErrorBtnDisplay(checkError);
			break;
		}
	}
	
	public class PhotoMeasure extends Thread {
		
		boolean isOn = false;
		double adc;
		
		Activity activity;
		Context context;
		int layoutid;
		
		PhotoMeasure(Activity activity, Context context, int layoutid) {
			
			this.activity = activity;
			this.context = context;
			this.layoutid = layoutid;
		}
		
		public void run() {
			
			while(isNormal) {
				
				if(isOn) isOn = false;
				else isOn = true;
				
				while(isMeasured) SerialPort.Sleep(100);
				
				adc = AbsorbanceMeasure();
						
				if(adc != -1.0) {
					
					ADCAcquire(adc);
					numofSample = ADCMaxMinAcquire(f535nmValue);
					MeasureDisplay(isOn, CoordinateAcquire());
					
					isMeasured = true;
				
				} else {
					
					checkError = R.string.e241;
					isNormal = false;
				}
			}
			
			switch(checkError) {
			
			case RunActivity.NORMAL_OPERATION	:
				TestCancel();
				break;
				
			default	:
				mErrorPopup = new ErrorPopup(activity, context, layoutid, null, 0);
				mErrorPopup.ErrorBtnDisplay(checkError);
				break;
			}
		}
	}
	
	public class Graph extends SurfaceView implements SurfaceHolder.Callback {

		SurfaceHolder holder;
		
		public Graph(Context context, SurfaceView surfaceView) {
			
			super(context); 
			
			holder = surfaceView.getHolder();
			
			holder.addCallback(this);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {

		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			
		}
		
		public SurfaceHolder GetHolder() {
			
			return this.holder;
		}
	}
	
	public class DrawThread extends Thread {
		
		SurfaceHolder surfaceHolder;
		
		public DrawThread(SurfaceHolder surfaceHolder) {
			
			this.surfaceHolder = surfaceHolder;
		}
		
		public void run() {
			
			Canvas canvas = null;
			Paint pnt;
			
			int[] cdn = new int[5];
			float range;
			
			pnt = new Paint();
			pnt.setColor(Color.WHITE);
			pnt.setStrokeWidth(1);
			
			while(isNormal) {
			
				try {
				
					while(!isMeasured) SerialPort.Sleep(100);
					
					canvas = surfaceHolder.lockCanvas(null);
					
					canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
					cdn = CoordinateAcquire();
					
					if(cdn[5] != 0) {
						
						range = (float) 250/(cdn[5]*12);
						
						for(int i = (f535nmValue.length-(numofSample-1)); i < (f535nmValue.length-1); i++) {
							
							canvas.drawLine(2*i, (float) (250-((f535nmValue[i]-cdn[4])*range)), 2*(i+1), (float) (250-((f535nmValue[i+1]-cdn[4])*range)), pnt);
						}
					}
					
					isMeasured = false;
										
				} finally {
					
					surfaceHolder.unlockCanvasAndPost(canvas);
				}	
			}
		}
	}
	
	public void ADCAcquire(double adc) {
		
		if(adc != 0) {
		
			for(int i = 0; i < (f535nmValue.length-1); i++) {
				
				f535nmValue[i] = f535nmValue[i + 1];
			}
			
			f535nmValue[f535nmValue.length-1] = adc;
		
		} else {			
			
			for(int i = 0; i < f535nmValue.length; i++) {
				
				f535nmValue[i] = 0;
			}
		}
	}
	
	public int ADCMaxMinAcquire(double value[]) {
		
		double currVal;
		int num = 0,
			min,
			max;
		
		max = Integer.MIN_VALUE;
		min = Integer.MAX_VALUE;
		
		for(int i = 0; i < value.length; i++) {
			
			currVal = value[i];
			
			if(currVal > 0) num++; 
			if(currVal > max) max = (int) currVal;
			if(currVal > 0 && currVal < min) min = (int) currVal;
		}
		
		adcMax = max;
		adcMin = min;
		
		return num;
	}
	
	public int ADCDiffrence() {
		
		return (adcMax - adcMin)/8;
	}
	
	public int[] CoordinateAcquire() {
		
		int yCdn[] = new int[6];
		int diff;
		
		diff = ADCDiffrence();
		
		if((adcMin - 2*diff) < 0) {
			
			diff = adcMax/10;		
		}

		yCdn[0] = adcMax + 2*diff;
		yCdn[1] = adcMax - diff;
		yCdn[2] = adcMax - 4*diff;
		yCdn[3] = adcMax - 7*diff;
		yCdn[4] = adcMax - 10*diff;
		yCdn[5] = diff;
		
		return yCdn;
	}

	public void MeasureDisplay(final boolean flag, final int xCdn[]) {
		
		final String color;
		
		if(flag) color = "#04A458";
		else color = "#00000000";
		
		new Thread(new Runnable() {			
			public void run() {    
				runOnUiThread(new Runnable(){
					public void run() {

						adcText.setText(Double.toString(f535nmValue[f535nmValue.length-1]));
						stateFlag1.setBackgroundColor(Color.parseColor(color));
						stateFlag2.setBackgroundColor(Color.parseColor(color));
						
						adc1Text.setText(Integer.toString(xCdn[0]) + " -");
						adc2Text.setText(Integer.toString(xCdn[1]) + " -");
						adc3Text.setText(Integer.toString(xCdn[2]) + " -");
						adc4Text.setText(Integer.toString(xCdn[3]) + " -");
						adc5Text.setText(Integer.toString(xCdn[4]) + " -");
					}
				});
			}
		}).start();
	}
	
	public void TestCancel() {
	
		ADCAcquire(0);
		isMeasured = true;
		photoState = AnalyzerState.FilterHome;
		
		for(int i = 0; i < 2; i++) {
			
			switch(photoState) {
			
			case FilterHome :
				MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
				BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.CartridgeHome, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
				break;
			
			case CartridgeHome :
				MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);
				BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NormalOperation, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 5);
				break;
				
			default	:
				break;
			}
		}
		
		SerialPort.Sleep(1000);
		
		ADCAcquire(0);
		MeasureDisplay(false, CoordinateAcquire());
		
		photoState = AnalyzerState.MeasurePosition;
		
		new Thread(new Runnable() {			
			public void run() {    
				runOnUiThread(new Runnable(){
					public void run() {

						setButtonState(R.id.backBtn, true);
						setButtonState(R.id.runBtn, true);
						setButtonState(R.id.darkBtn, true);
						setButtonState(R.id.f535nmBtn, true);
						setButtonState(R.id.f660nmBtn, true);
						setButtonState(R.id.f750nmBtn, true);
					}
				});
			}
		}).start();
	}
	
	public void MotionInstruct(String str, SerialPort.CtrTarget target) { // Motion of system instruction
		
		while(TimerDisplay.RXBoardFlag) SerialPort.Sleep(10);
		
		TimerDisplay.RXBoardFlag = true;
		
		mSerialPort.BoardTx(str, target);
	}
	
	public synchronized double AbsorbanceMeasure() { // Absorbance measurement
		
		int time = 0;
		String rawValue;
		double douValue = 0;
		
		while(TimerDisplay.RXBoardFlag) SerialPort.Sleep(10);
		
		TimerDisplay.RXBoardFlag = true;
		
		mSerialPort = new SerialPort();
		mSerialPort.BoardTx("VH", SerialPort.CtrTarget.NormalSet);
		
		rawValue = mSerialPort.BoardMessageOutput();			
		
		while(rawValue.length() != 8) {
			
			rawValue = mSerialPort.BoardMessageOutput();			
				
			if(time++ > 50) {
				
				photoState = AnalyzerState.NoResponse;
			
				break;
			}
		
			SerialPort.Sleep(100);
		}	
		
		TimerDisplay.RXBoardFlag = false;
		
		if(photoState != AnalyzerState.NoResponse) {

			douValue = Double.parseDouble(rawValue);
			
			return douValue;
		}
		
		return -1;
	}
	
	public void BoardMessage(String colRsp, AnalyzerState nextState, String errRsp, AnalyzerState errState, int rspTime) {
		
		int time = 0;
		String temp = "";
		
		rspTime = rspTime * 10;
		
		while(true) {
			
			temp = mSerialPort.BoardMessageOutput();
	
			if(temp.equals(colRsp)) {
				
				photoState = nextState;
				break;
			
			} else if(temp.equals(errRsp)) {
				
				photoState = errState;
				break;
			}
					
			if(time++ > rspTime) {
				
				photoState = AnalyzerState.NoResponse;
				checkError = R.string.e241;
				break;
			}
			
			SerialPort.Sleep(100);
		}
		
		TimerDisplay.RXBoardFlag = false;
	}
	
	public class CancelTest extends Thread {
		
		public void run() {
			
			while(!isCancel) SerialPort.Sleep(100);
			isNormal = false;
			if(checkError != RunActivity.NORMAL_OPERATION) TestCancel();
		}
	}
	
	public void cancelTest() {
		
		isCancel = true;
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home				:				
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			break;
			
		case Engineer		:				
			nextIntent = new Intent(getApplicationContext(), EngineerActivity.class);
			break;
			
		default		:	
			break;			
		}
		
		startActivity(nextIntent);
		finish();		
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
