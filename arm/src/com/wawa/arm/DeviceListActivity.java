/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wawa.arm;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wawa.arm.adapter.DeivceListAdapter;
import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.CommonMethod;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.net.WaitUIElement;
import com.wawa.arm.utile.MyToast;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class DeviceListActivity extends Activity {
    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private DeivceListAdapter mPairedDevicesArrayAdapter;
    private DeivceListAdapter mNewDevicesArrayAdapter;
    public static final String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
    private WaitUIElement dialog;
    private List<String> nopairs = new ArrayList<String>();
    private List<String> pairs = new ArrayList<String>();
    private Set<String> set = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);//��ʾһ��������������
        setContentView(R.layout.device_list);

        // Set result CANCELED incase the user backs out
        /**back��-����������*/
        setResult(Activity.RESULT_CANCELED);

        // Initialize the button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesArrayAdapter = new DeivceListAdapter(this, pairs, CommonConsts.PAIR_DEVICE);//new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new DeivceListAdapter(this, nopairs, CommonConsts.NO_PAIR_DEVICE);//new ArrayAdapter<String>(this, R.layout.device_name);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        /**��̬ע�������㲥������*/
        //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);// ��BroadcastReceiver��ȡ���������  
        //this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        //filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND); //�������� 
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  //�������
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);  //ĳ���״̬��� ���
        filter.addAction(ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);  //����
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);  //
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        /**��ȡ*/
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
            	if(CommonMethod.checkDeviceName(device.getName()))
            		pairs.add(device.getName() + "\n" + device.getAddress());
            }
        } 
        if(pairs.size() == 0){
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairs.add(noDevices);
        }
        mPairedDevicesArrayAdapter.notifyDataSetChanged();
        dialog = new WaitUIElement(this,true);
    }
    

	@Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        /**�ж��Ƿ�������������*/
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    // The on-click listener for all devices in the ListViews
    /**
     * ����������������豸
     */
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            
            BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
            // ���ӽ���֮ǰ�������  
            if(device.getBondState() != BluetoothDevice.BOND_BONDED){
            	try {
            		Method createBondMethod = device.getClass().getMethod("createBond");  
            		Boolean flag = (Boolean)createBondMethod.invoke(device);  
            		Log.d(TAG, "-----bond flag-----"+flag); 
            		/*Method setPinMethod = device.getClass().getDeclaredMethod("setPin",
                            new Class[]
                            {byte[].class});
                    Boolean returnValue = (Boolean) setPinMethod.invoke( device,
                            new Object[]{"123456".getBytes()});
                   Log.d(TAG, "-----setPin flag-----"+returnValue); 
            		
                    Method cancelUMethod = device.getClass().getMethod("cancelPairingUserInput");
                    Boolean cancelRsult = (Boolean) cancelUMethod.invoke(device);
                    Log.d(TAG, "-----cancelPairingUserInput flag-----"+cancelRsult);
                    
                    Method cancelBondMethod = device.getClass().getMethod("cancelBondProcess");
                    Boolean cancelBonRsult = (Boolean) cancelBondMethod.invoke(device);
                    Log.d(TAG, "-----cancelBondProcess flag-----"+cancelBonRsult);
                     */
				} catch (Exception e) {
					Toast.makeText(DeviceListActivity.this, "�豸���ʧ�ܣ�", 3000).show();
					Log.e(TAG, "-----���"+address+"ʧ��-----",e);  
				}
            }else{
            	Intent intent = new Intent();
                intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

                // Set result and finish this Activity
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

            /*// Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();*/
        }
    };

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    /**
     *�������������㲥
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "---�����㲥---"+action);
            Bundle b = intent.getExtras();
            if(b != null){
            	Object[] lstName = b.keySet().toArray();
            	
            	// ��ʾ�����յ�����Ϣ����ϸ��
            	for (int i = 0; i < lstName.length; i++) {
            		String keyName = lstName[i].toString();
            		Log.i(keyName, String.valueOf(b.get(keyName)));
            	}
            }

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
    				//String str = device.getName() + "/-/" + device.getAddress();
    				/*if (devices.indexOf(str) == -1)// ��ֹ�ظ����
    					devices.add(str); // ��ȡ�豸���ƺ�mac��ַ*/ 
                	if(CommonMethod.checkDeviceName(device.getName()) && set.add(device.getAddress()))
                		nopairs.add(device.getName() + "\n" + device.getAddress());
                }
                mNewDevicesArrayAdapter.notifyDataSetChanged();
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    nopairs.add(noDevices);
                }
                set.clear();
                mNewDevicesArrayAdapter.notifyDataSetChanged();
            }
            
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            	switch (device.getBondState()) {
				case BluetoothDevice.BOND_BONDED://������
					dialog.dismissProcessDialog();
					Toast.makeText(DeviceListActivity.this, "�豸��Գɹ���", 3000).show();
					Log.d(TAG, "--�豸"+device.getAddress()+"������--");
					result(device.getAddress());
					break;
				case BluetoothDevice.BOND_BONDING://�������
					//TODO �����û�����
					dialog.showProcessDialog("�豸�����...");
					Log.d(TAG, "--�豸"+device.getAddress()+"�������--");
					break;
				case BluetoothDevice.BOND_NONE://ȡ�����
					//TODO ȡ���û����Ʋ���
					dialog.dismissProcessDialog();
					Toast.makeText(DeviceListActivity.this, "�豸���ʧ�ܣ�", 3000).show();
					Log.d(TAG, "--�豸"+device.getAddress()+"ȡ�����--");
					
					break;

				}
            }
            
            /*if(ACTION_PAIRING_REQUEST.equals(action)){
            	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            	Log.d(TAG, "--�豸"+device.getAddress()+"����--");
				try {
					
					Method setPinMethod = device.getClass().getDeclaredMethod("setPin",new Class[]{byte[].class});
					Boolean returnValue = (Boolean) setPinMethod.invoke( device,
							new Object[]{OMApplication.getInstance().getVal(CommonConsts.ARM_PAIR_KEY, CommonConsts.DEFAULT_PERM_KEY).getBytes()});
					Log.d(TAG, "-----setPin flag-----"+returnValue); 
					
					Method createBondMethod = device.getClass().getMethod("createBond");  
					Log.d(TAG, "-----��ʼ���"+device.getAddress()+"-----");  
					Boolean flag = (Boolean)createBondMethod.invoke(device);  
					Log.d(TAG, "-----bond flag-----"+flag); 
					
					Method cancelUMethod = device.getClass().getMethod("cancelPairingUserInput");
					Boolean cancelRsult = (Boolean) cancelUMethod.invoke(device);
					Log.d(TAG, "-----cancelPairingUserInput flag-----"+cancelRsult);
					
				} catch (Exception e) {
					Log.e(TAG, "--setPin error---", e);
				}
            }*/
        }
        
        
    };
    
    private void result(String address){
    	// Create the result Intent and include the MAC address
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, intent);
        finish();	
    }

	public void linkDeivce(String address) {
		mBtAdapter.cancelDiscovery();
		if (!BluetoothAdapter.checkBluetoothAddress(address))
		{ // ���������ַ�Ƿ���Ч

			Log.d("-----------------", "--------checkBluetoothAddress---"+address+"----false---");
		}
        BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
        // ���ӽ���֮ǰ�������  
        if(device.getBondState() != BluetoothDevice.BOND_BONDED){
        	try {
        		Method createBondMethod = device.getClass().getMethod("createBond");  
        		Log.d(TAG, "-----��ʼ���"+address+"-----");  
        		Boolean flag = (Boolean)createBondMethod.invoke(device);  
        		Log.d(TAG, "-----bond flag-----"+flag); 
        		/*Method setPinMethod = device.getClass().getDeclaredMethod("setPin",
                        new Class[]
                        {byte[].class});
                Boolean returnValue = (Boolean) setPinMethod.invoke( device,
                        new Object[]{"123456".getBytes()});
               Log.d(TAG, "-----setPin flag-----"+returnValue); 
        		
                Method cancelUMethod = device.getClass().getMethod("cancelPairingUserInput");
                Boolean cancelRsult = (Boolean) cancelUMethod.invoke(device);
                Log.d(TAG, "-----cancelPairingUserInput flag-----"+cancelRsult);
                
                Method cancelBondMethod = device.getClass().getMethod("cancelBondProcess");
                Boolean cancelBonRsult = (Boolean) cancelBondMethod.invoke(device);
                Log.d(TAG, "-----cancelBondProcess flag-----"+cancelBonRsult);
                 */
			} catch (Exception e) {
				Toast.makeText(DeviceListActivity.this, "�豸���ʧ�ܣ�", 3000).show();
				Log.e(TAG, "-----���"+address+"ʧ��-----",e);  
			}
        }else{
        	Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
		
	}

	public void clearDeivce(String address) {
		BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
		try {
			Method createBondMethod = device.getClass().getMethod("removeBond");//cancelBondProcess");
			Boolean returnValue = (Boolean) createBondMethod.invoke(device);
			if(returnValue){
				for (Iterator iterator = pairs.iterator(); iterator.hasNext();) {
					String type = (String) iterator.next();
					String daddress = type.substring(type.length() - 17);
					if(daddress.equals(address)){
						iterator.remove();
					}
				}
				if(pairs.size() == 0){
					String noDevices = getResources().getText(R.string.none_paired).toString();
		            pairs.add(noDevices);
				}
		        mPairedDevicesArrayAdapter.notifyDataSetChanged();
				MyToast.showToast(getApplicationContext(), "�豸����ɹ���", 3000);
				Log.d(TAG, "-----�ɹ�ȡ��"+address+"���-----");  
			}else{
				MyToast.showToast(getApplicationContext(), "�豸���ʧ�ܣ��ɵ��ֻ�\"��������\"��ȡ����������", 3000);
			}
		} catch (Exception e) {
			Log.d(TAG, "-----ȡ��"+address+"���ʧ��-----",e);  
		}
	}

}
