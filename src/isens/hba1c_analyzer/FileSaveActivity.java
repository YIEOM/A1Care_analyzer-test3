package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FileSaveActivity extends Activity {

	public static byte NORMAL_RESULT = 0,
					   CONTROL_TEST = 1,
					   PATIENT_TEST = 2;
	
	private DataStorage SaveData;
	
	private TextView Text;
	private Intent itn;
	
	private StringBuffer overallData = new StringBuffer(),
						 historyData = new StringBuffer();

	public static int DataCnt,
					  TempDataCnt;
	
	private int runState,
				whichState;
	
	private String dataType;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file);
		
		Text = (TextView) findViewById(R.id.text);		
		
		DataInit();
	}
	
//	public void DataInit() {
//		
//		DataArray();
//		
//		SaveData = new DataStorage();
//		
//		itn = getIntent();
//		
//		if(itn.getIntExtra("RunState", 0) == (int) NORMAL_RESULT) {
//
//			if(dataType.equals("W") || dataType.equals("X") || dataType.equals("Y") || dataType.equals("Z")) SaveData.DataSave(CONTROL_TEST, overallData);
//			else SaveData.DataSave(PATIENT_TEST, overallData); // if HbA1c test is normal, the Result data is saved
//		}
//		
//		SaveData.DataHistorySave(overallData, historyData); // the History data is saved
//		
//		WhichIntent();
//	}
//	
//	public void DataArray() { // Enumerating data
//		
//		DecimalFormat dfm = new DecimalFormat("0000");
//
//		overallData.delete(0, overallData.capacity());
//		historyData.delete(0, historyData.capacity());
//		
//		itn = getIntent();
//		whichState = itn.getIntExtra("WhichIntent", 0);
//		DataCnt = itn.getIntExtra("DataCnt", 0);
//		TempDataCnt = DataCnt % 9999;
//		if(TempDataCnt == 0) TempDataCnt = 9999;
//		
//		dataType = itn.getStringExtra("Type");
//		
//		overallData.append(itn.getStringExtra("Year"));
//		overallData.append(itn.getStringExtra("Month"));
//		overallData.append(itn.getStringExtra("Day"));
//		overallData.append(itn.getStringExtra("AmPm"));
//		overallData.append(itn.getStringExtra("Hour"));
//		overallData.append(itn.getStringExtra("Minute"));
//		overallData.append(dfm.format(TempDataCnt));
//		overallData.append(itn.getStringExtra("Type"));
//		overallData.append(itn.getStringExtra("RefNumber"));
//		overallData.append(itn.getStringExtra("PatientIDLen"));
//		overallData.append(itn.getStringExtra("PatientID"));
//		overallData.append(itn.getStringExtra("OperatorLen"));
//		overallData.append(itn.getStringExtra("Operator"));
//		overallData.append(itn.getStringExtra("Primary"));
//		overallData.append(itn.getStringExtra("Hba1cPct"));
//		
//		historyData.append(itn.getStringExtra("BlankVal0") + "\t");
//		historyData.append(itn.getStringExtra("BlankVal1") + "\t");
//		historyData.append(itn.getStringExtra("BlankVal2") + "\t");
//		historyData.append(itn.getStringExtra("BlankVal3") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs1by0") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs1by1") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs1by2") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs2by0") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs2by1") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs2by2") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs3by0") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs3by1") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs3by2") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs1by0") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs1by1") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs1by2") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs2by0") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs2by1") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs2by2") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs3by0") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs3by1") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs3by2") + "\t");
//		historyData.append(itn.getStringExtra("HWSN") + "\t");
//		historyData.append(itn.getStringExtra("SWVersion") + "\t");
//		historyData.append(itn.getStringExtra("FWVersion") + "\t");
//		historyData.append(itn.getStringExtra("OSVersion"));
//	}
//	
//	public void WhichIntent() { // Activity conversion
//	
//		Intent nextIntent = new Intent(getApplicationContext(), RemoveActivity.class);
//		nextIntent.putExtra("WhichIntent", whichState);
//		nextIntent.putExtra("DataCnt", DataCnt);
//		startActivity(nextIntent);
//		finish();
//	}
	
	public void DataInit() {
		
		DataArray();
		
		SaveData = new DataStorage();
		
		itn = getIntent();
		
		SaveData.saveTmp(overallData); // the History data is saved
		
		WhichIntent();
	}
	
	public void DataArray() { // Enumerating data
		
		overallData.delete(0, overallData.capacity());

		itn = getIntent();
		whichState = itn.getIntExtra("WhichIntent", 0);
		
		overallData.append(itn.getStringExtra("Month") + ".");
		overallData.append(itn.getStringExtra("Day") + "\t");
		overallData.append(itn.getStringExtra("AmPm") + " ");
		overallData.append(itn.getStringExtra("Hour") + ":");
		overallData.append(itn.getStringExtra("Minute") + "\r\n");
		overallData.append(itn.getDoubleExtra("Inside Temp0", 0.0) + "\r\n");
		overallData.append(itn.getDoubleExtra("Inside Temp1", 0.0) + "\r\n");
		overallData.append(itn.getDoubleExtra("Inside Temp2", 0.0) + "\r\n");
		overallData.append(itn.getDoubleExtra("Inside Temp3", 0.0) + "\r\n");
		overallData.append(itn.getDoubleExtra("Inside Temp4", 0.0) + "\r\n");
		overallData.append(itn.getDoubleExtra("Inside Temp5", 0.0) + "\r\n");
		overallData.append(itn.getDoubleExtra("Inside Temp6", 0.0) + "\r\n");
		overallData.append(itn.getDoubleExtra("Inside Temp7", 0.0) + "\r\n");
		overallData.append(itn.getDoubleExtra("Inside Temp8", 0.0) + "\r\n");
		overallData.append(itn.getDoubleExtra("Inside Temp9", 0.0) + "\r\n");
		overallData.append(itn.getDoubleExtra("Inside Temp10", 0.0) + "\r\n");
		overallData.append(itn.getDoubleExtra("Inside Temp11", 0.0) + "\r\n");
		overallData.append(itn.getDoubleExtra("Inside Temp12", 0.0));
	}
	
	public void WhichIntent() { // Activity conversion
	
		itn = getIntent();
		
		int systemCheck = itn.getIntExtra("System Check State", 0);
		
		Intent nextIntent = null;
		
		nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
		nextIntent.putExtra("System Check State", systemCheck);
		startActivity(nextIntent);
		finish();
	}
	
	public void finish() {
		
		super.finish();
	}
}
