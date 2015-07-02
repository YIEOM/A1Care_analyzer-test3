package isens.hba1c_analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.SerialPort.CtrTarget;
import isens.hba1c_analyzer.Temperature.CellTmpRead;
import isens.hba1c_analyzer.View.AboutActivity;
import isens.hba1c_analyzer.View.tHbActivity;
import isens.hba1c_analyzer.View.AdjustmentActivity;
import isens.hba1c_analyzer.View.f535Activity;
import isens.hba1c_analyzer.View.f660Activity;
import isens.hba1c_analyzer.View.CorrelationActivity;
import isens.hba1c_analyzer.View.LampActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

public class EngineerActivity extends Activity {
	
	public TimerDisplay mTimerDisplay;
	public SerialPort mSerialPort;
	public DataStorage mDataStorage;
	public ErrorPopup mErrorPopup;
	
	public Button escBtn,
	  			  lampBtn,
	  			  adjustBtn,
	  			  calibrationBtn,
	  			  tempBtn,
	  			  tHbBtn,
	  			  f535Btn,
	  			  f660Btn,
	  			  collelationBtn,
	  			  aboutBtn,
	  			  deleteBtn;
	
	public TextView swVersionText, fwVersionText, osVersionText;
	
	public Activity activity;
	public Context context;
	
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.engineer);
		
		MaintenanceInit();
	}
	
	public void setTextId() {

		swVersionText = (TextView)findViewById(R.id.swVersionText);
		fwVersionText = (TextView)findViewById(R.id.fwVersionText);
		osVersionText = (TextView)findViewById(R.id.osVersionText);		
	}
	
	public void setButtonId() {
		
		escBtn = (Button)findViewById(R.id.escBtn);
		adjustBtn = (Button)findViewById(R.id.adjustBtn);
		calibrationBtn = (Button)findViewById(R.id.calibrationBtn);
		tempBtn = (Button)findViewById(R.id.tempBtn);
		lampBtn = (Button)findViewById(R.id.lampBtn);
		tHbBtn = (Button)findViewById(R.id.tHbBtn);
		f535Btn = (Button)findViewById(R.id.f535Btn);
		f660Btn = (Button)findViewById(R.id.f660Btn);
		collelationBtn = (Button)findViewById(R.id.collelationBtn);
		aboutBtn = (Button)findViewById(R.id.aboutBtn);
		deleteBtn = (Button)findViewById(R.id.deleteBtn);
	}
	
	public void setButtonClick() {
		
		escBtn.setOnTouchListener(mTouchListener);
		adjustBtn.setOnTouchListener(mTouchListener);
		calibrationBtn.setOnTouchListener(mTouchListener);
		tempBtn.setOnTouchListener(mTouchListener);
		lampBtn.setOnTouchListener(mTouchListener);
		tHbBtn.setOnTouchListener(mTouchListener);
		f535Btn.setOnTouchListener(mTouchListener);
		f660Btn.setOnTouchListener(mTouchListener);
		collelationBtn.setOnTouchListener(mTouchListener);
		aboutBtn.setOnTouchListener(mTouchListener);
		deleteBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state) {
		
		findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				
				if(!btnState) {

					btnState = true;
					
					switch(v.getId()) {
				
					case R.id.escBtn	:
						WhichIntent(activity, TargetIntent.Home);
						break;
						
					case R.id.adjustBtn	:
						WhichIntent(activity, TargetIntent.Adjustment);
						break;
					
					case R.id.calibrationBtn	:
						WhichIntent(activity, TargetIntent.Calibration);
						break;
					
					case R.id.tempBtn	:
						WhichIntent(activity, TargetIntent.Temperature);
						break;
						
					case R.id.lampBtn	:
						WhichIntent(activity, TargetIntent.Lamp);
						break;
						
					case R.id.tHbBtn	:
						WhichIntent(activity, TargetIntent.tHb);
						break;
					
					case R.id.f535Btn	:
						WhichIntent(activity, TargetIntent.f535);
						break;
						
					case R.id.f660Btn	:
						WhichIntent(activity, TargetIntent.f660);
						break;
						
					case R.id.collelationBtn	:
						WhichIntent(activity, TargetIntent.Correlation);
						break;
					
					case R.id.aboutBtn	:
						WhichIntent(activity, TargetIntent.About);
						break;
						
					case R.id.deleteBtn	:
						mErrorPopup = new ErrorPopup(activity, context, R.id.engineerlayout);
						mErrorPopup.OXBtnDisplay(R.string.delete);
						btnState = false;
						break;
						
					default	:
						break;
					}
				}
			
				break;
			}
			
			return false;
		}
	};
	
	public void MaintenanceInit() {
		
		activity = this;
		context = this;
		
		setTextId();
		setButtonId();
		setButtonClick();
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.engineerlayout);
	}
	
	public void WhichIntent(Activity activity, TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home			:				
			nextIntent = new Intent(activity.getApplicationContext(), HomeActivity.class);
			break;
						
		case Lamp		:				
			nextIntent = new Intent(activity.getApplicationContext(), LampCopyActivity.class);
			break;
			
		case Adjustment		:				
			nextIntent = new Intent(activity.getApplicationContext(), AdjustmentActivity.class);
			break;

		case Calibration	:				
			nextIntent = new Intent(activity.getApplicationContext(), CalibrationActivity.class);
			break;
			
		case Temperature	:				
			nextIntent = new Intent(activity.getApplicationContext(), TemperatureActivity.class);
			break;
		
		case tHb		:				
			nextIntent = new Intent(activity.getApplicationContext(), tHbActivity.class);
			break;
					
		case f535		:				
			nextIntent = new Intent(activity.getApplicationContext(), f535Activity.class);
			break;
		
		case f660		:				
			nextIntent = new Intent(activity.getApplicationContext(), f660Activity.class);
			break;
		
		case Correlation	:				
			nextIntent = new Intent(activity.getApplicationContext(), CorrelationActivity.class);
			break;
		
		case About	:				
			nextIntent = new Intent(activity.getApplicationContext(), AboutActivity.class);
			break;
			
		case Delete	:
			int patientDataCnt = RemoveActivity.PatientDataCnt;
			int controlDataCnt = RemoveActivity.ControlDataCnt;
			RemoveActivity.PatientDataCnt = 1;
			RemoveActivity.ControlDataCnt = 1;
			
			nextIntent = new Intent(activity.getApplicationContext(), FileDeleteActivity.class);
			nextIntent.putExtra("PatientDataCnt", patientDataCnt);
			nextIntent.putExtra("ControlDataCnt", controlDataCnt);
			break;
			
		default		:	
			break;			
		}
		
		activity.startActivity(nextIntent);
		finish(activity);
	}
	
	public void finish(Activity activity) {
		
		super.finish();
		activity.overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}