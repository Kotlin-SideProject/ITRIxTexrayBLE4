/*****************************************************************************
* Copyright (c) 2014 Laird Technologies. All Rights Reserved.
* 
* The information contained herein is property of Laird Technologies.
* Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
* This heading must NOT be removed from the file.
******************************************************************************/

package com.lairdtech.lairdtoolkit.serialdevice;

import android.bluetooth.BluetoothGatt;
import android.graphics.Color;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.lairdtech.lairdtoolkit.R;
import com.lairdtech.lairdtoolkit.bases.BaseActivity;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.StringTokenizer;

public class SerialActivity extends BaseActivity implements SerialManagerUiCallback{

	private static final String TAG = SerialActivity.class.getSimpleName();
	private Button mBtnSend;
	private SerialManager mSerialManager;
	String s0 = "";
	private boolean isPrefClearTextAfterSending = false;
	ImageView ivQuadriceps, ivBiceps, ivGluteus;
	ColorfulRingProgressView crpv1, crpv2, crpv3;
	TextView tvPercent1, tvPercent2, tvPercent3;
	int progress = 0;
	private LineChart mChart;

	//Angus ADD BY 2020/02/13




	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		setContentView(R.layout.activity_serial);
		super.onCreate(savedInstanceState);

		mSerialManager = new SerialManager(this, this);
		setBleDeviceBase(mSerialManager.getVSPDevice());

		initialiseDialogAbout(getResources().getString(R.string.about_serial));
		initialiseDialogFoundDevices("VSP");
		mBtnSend.setEnabled(true);

		setChart();

	}

	private void setChart() {
		mChart = (LineChart)findViewById(R.id.chart1);
		mChart.getDescription().setEnabled(false);
//        mChart.getDescription().setText("Real Time EMG Signal");
		mChart.getDescription().setTextColor(Color.RED);

		mChart.setTouchEnabled(true);
		mChart.setDragEnabled(true);
		mChart.setScaleEnabled(true);
		mChart.setDrawGridBackground(true);
		mChart.setPinchZoom(true);
		mChart.setBackgroundColor(Color.BLACK);


		LineData data = new LineData();
		data.setValueTextColor(Color.WHITE);
		mChart.setData(data);

		Legend l = mChart.getLegend();

		l.setForm(Legend.LegendForm.LINE);
		l.setTextColor(Color.WHITE);

		XAxis x1 = mChart.getXAxis();
		x1.setTextColor(Color.WHITE);
		x1.setDrawGridLines(true);
		x1.setAvoidFirstLastClipping(true);
		x1.setEnabled(false);
//		x1.setAxisMaximum(100f);

		YAxis leftAxis = mChart.getAxisLeft();
		leftAxis.setTextColor(Color.WHITE);
		leftAxis.setAxisMaximum(10000f);
		leftAxis.setAxisMinimum(-1000f);
		leftAxis.setDrawGridLines(true);

		YAxis rightAxis = mChart.getAxisRight();
		rightAxis.setEnabled(false);

		mChart.getAxisLeft().setDrawGridLines(true);
		mChart.getXAxis().setDrawGridLines(true);
		mChart.setDrawBorders(true);
	}

	private LineDataSet createSet(){
		LineDataSet set  = new LineDataSet(null,"Real Time EMG Signal");
		set.setAxisDependency(YAxis.AxisDependency.LEFT);
		set.setLineWidth(3f);
		set.setColor(Color.MAGENTA);
		set.setHighlightEnabled(false);
		set.setDrawValues(false);
		set.setDrawCircles(false);
		set.setCubicIntensity(0.1f);
		set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
		return set;
	}

	private void addEntry(String dataReceived){
		///string translate  1/n2/n3/n4/n5/n
			dataReceived = s0 +dataReceived;
//		Log.d(TAG, "dataReceived: \n" + dataReceived);
		StringTokenizer st  = new StringTokenizer(dataReceived,"\n");
			while (st.hasMoreTokens() ){
		//            			確認字串長度
				String s = st.nextToken();
				decoder d = new decoder(String.valueOf(s));
				if (s.length() == 5){
					switch (d.decoderChannel()){
						case 1:
							ivQuadriceps.setColorFilter(Color.argb(100, 255, d.mappingColor(), 0));
							crpv1.setPercent(d.MVIC());
							tvPercent1.setText(d.MVIC() + "");
							break;

						case 2:
							ivBiceps.setColorFilter(Color.argb(100, 255, d.mappingColor(), 0));
							crpv2.setPercent(d.MVIC());
							tvPercent2.setText(d.MVIC() + "");
							break;

						case 3:
							ivGluteus.setColorFilter(Color.argb(100, 255, d.mappingColor(), 0));
							crpv3.setPercent(d.MVIC());
							tvPercent3.setText(d.MVIC() + "");
							break;
					}

				}
				else if(s.length() < 5 ){
						s0 = s;
				}
			}
		}

	/*
	 * *************************************
	 * UI methods
	 * *************************************
	 */
	private void addEntry1(String dataReceived){
		LineData data = mChart.getData();
		if(data != null){
			LineDataSet set  = (LineDataSet) data.getDataSetByIndex(0);
			if (set == null){
				set = createSet();
				data.addDataSet(set);
			}
			dataReceived = s0 + dataReceived;
			///string translate  1/n2/n3/n4/n5/n
			StringTokenizer st  = new StringTokenizer(dataReceived,"\n");
//			int n = st.countTokens();
			while (st.hasMoreTokens()){
				String s = st.nextToken();
//				Log.d(TAG, "nextToken: " + s);
				if (s.length()==5){
					decoder d = new decoder(s);
					data.addEntry(new Entry(set.getEntryCount(),d.decoderData()),0);
					Log.d(TAG, "decoderData: " + d.decoderData());
				}else if(s.length() < 5){
					s0 = s;
				}
			}
			data.notifyDataChanged();

			mChart.notifyDataSetChanged();

			mChart.setVisibleXRange(200,200);
			//mChart.setMaxVisibleValueCount(150);

			//mChart.moveViewToX(data.getEntryCount()-100);
			mChart.moveViewToX(data.getEntryCount());

		}
	}

	@Override
	protected void bindViews(){
		super.bindViews();
		mBtnSend = (Button) findViewById(R.id.btnSend);
		ivQuadriceps = findViewById(R.id.iv_quadriceps);
		ivBiceps = findViewById(R.id.iv_biceps);
		ivGluteus = findViewById(R.id.iv_gluteus);
		crpv1 = findViewById(R.id.crpv1);
		crpv2 = findViewById(R.id.crpv2);
		crpv3 = findViewById(R.id.crpv3);
		tvPercent1 = findViewById(R.id.tvPercent1);
		tvPercent2 = findViewById(R.id.tvPercent2);
		tvPercent3 = findViewById(R.id.tvPercent3);
	}

	@Override
	protected void setListeners(){
		super.setListeners();

		mBtnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


			}
		});
	}

	@Override
	protected void onPause(){
		super.onPause();

		if(isInNewScreen == true
				|| isPrefRunInBackground == true){
			// let the app run normally in the background
		} else{
			// stop scanning or disconnect if we are connected
			if(mBluetoothAdapterWrapper.isBleScanning()){
				mBluetoothAdapterWrapper.stopBleScan();

			} else if(getBleDeviceBase().isConnecting()
					|| getBleDeviceBase().isConnected()){
				getBleDeviceBase().disconnect();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.serial, menu);
		getActionBar().setIcon(R.drawable.icon_serial);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		if (mBluetoothAdapterWrapper.isBleScanning() == true) {
			menu.findItem(R.id.action_scanning_indicator).setActionView(R.layout.progress_indicator);
		} else {
			menu.findItem(R.id.action_scanning_indicator).setActionView(null);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		
		case R.id.action_clear:
			mSerialManager.getVSPDevice().clearRxAndTxCounter();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	/*
	 * *************************************
	 * SerialManagerUiCallback
	 * *************************************
	 */
	@Override
	public void onUiConnected(BluetoothGatt gatt) {
		uiInvalidateBtnState();
	}

	@Override
	public void onUiDisconnect(BluetoothGatt gatt) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mBtnSend.setEnabled(false);
			}
		});
		uiInvalidateBtnState();
	}

	@Override
	public void onUiConnectionFailure(
			final BluetoothGatt gatt){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mBtnSend.setEnabled(false);
			}
		});
		uiInvalidateBtnState();
	}

	@Override
	public void onUiBatteryReadSuccess(String result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUiReadRemoteRssiSuccess(int rssi) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUiBonded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUiVspServiceNotFound(BluetoothGatt gatt) {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				mBtnSend.setEnabled(false);
			}
		});
	}

	@Override
	public void onUiVspRxTxCharsNotFound(BluetoothGatt gatt) {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				mBtnSend.setEnabled(false);
			}
		});
	}

	@Override
	public void onUiVspRxTxCharsFound(BluetoothGatt gatt) {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				mBtnSend.setEnabled(true);
			}
		});
	}

	@Override
	public void onUiSendDataSuccess(
			final String dataSend) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	@Override
	public void onUiReceiveData(final String dataReceived) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				addEntry1(dataReceived);
				addEntry(dataReceived);
			}
		});
	}

	@Override
	public void onUiEMGChange(final String dataReceived) {

	}

	@Override
	public void onUiUploaded() {
		mBtnSend.setEnabled(true);
	}


	/*
	 * *************************************
	 * other
	 * *************************************
	 */
	@Override
	protected void loadPref(){
		super.loadPref();
		isPrefClearTextAfterSending = mSharedPreferences.getBoolean("pref_clear_text_after_sending", false);
	}
}