package com.wawa.arm;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.wawa.arm.common.AppManager;
import com.wawa.arm.common.BaseActivity;
import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.utile.BluetoothChatService;
import com.wawa.arm.utile.MyToast;
import com.wawa.arm.utile.TransferUtil;
import com.wawa.arm.utile.log.LogUtil;
import com.wawa.arm.utile.widgets.OMDialog;

public class ARMActivity extends BaseActivity{
	private static final String TAG = ARMActivity.class.getSimpleName();
	private ImageView openImg,settingImg;
	// Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
 // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
 // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
 // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
 // Name of the connected device
    private String mConnectedDeviceName = null;
    private boolean isStopflag;
	

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        setTitle("0");
        
        openImg = (ImageView)findViewById(R.id.open_icon);
        settingImg = (ImageView)findViewById(R.id.setting_icon);
        
        openImg.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		//TODO 
        		/*1�����ж��Ƿ�֧��������2�������Ƿ�򿪣�3.1����ѯ��ƥ��õ�������3.1.1������Ƿ���ARM���豸��������ͨ��
        		 * 3.1.2�����ʧЧ��ARM���ڷ�Χ�ڡ�
        		3.2���Զ����Ҹ�������������ARMЭ��������Χ�����豸������ѡ���1���Ƿ�򿪣�����ṩѡ��
        		3.2.1����̨�Զ���ԣ���Գɹ�ʧ�ܡ��ɹ�������ͨѶ
        		*/
        		if(isStopflag){
        			isStopflag = false;
        			stopARM();
        		}else{
        			isStopflag = true;
        			Intent serverIntent = new Intent(ARMActivity.this, DeviceListActivity.class);
        			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        		}
        	}
        });
        settingImg.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent intent = new Intent(ARMActivity.this, SettingActivity.class);
				startActivity(intent);
        	}
        });
        
        /**
         * ����һ��
         * ��ȡ����Adapter
         */
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        /*���ұ�������ģ��֧��profile*/
		try {
			Method createBondMethod = mBluetoothAdapter.getClass().getMethod("getUuids");
			ParcelUuid[] returnValue = (ParcelUuid[]) createBondMethod.invoke(mBluetoothAdapter);
			String str = "";
			for (int i = 0; i < returnValue.length; i++) {
				str = str+","+returnValue[i].getUuid().toString();
			}
			LogUtil.d(TAG, "--���豸����֧��UUID--"+str);
		} catch (Exception e) {
			LogUtil.e(TAG,"", e);
		}
        
	}
	@Override
    public void onStart() {
        super.onStart();
        LogUtil.d(TAG, "--ARMActivity start---");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        /**
         * �������
         * �ж������Ƿ��
         */
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE://�����������淵�ص������豸
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // Get the BLuetoothDevice object
                /**
                 * ��ȡԶ���豸������ָ���ǻ�ȡbluetoothSocketָ�����ӵ��Ǹ�Զ�������豸
                 */
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // ���ӽ���֮ǰ�������  
                if(device.getBondState() != BluetoothDevice.BOND_BONDED){
                	try {
                		Method setPinMethod = device.getClass().getDeclaredMethod("setPin",
                                new Class[]
                                {byte[].class});
                        Boolean returnValue = (Boolean) setPinMethod.invoke( device,
                                new Object[]{"1234".getBytes()});
                        Log.d(TAG, "-----setPin flag-----"+returnValue); 
                		Method createBondMethod = device.getClass().getMethod("createBond");  
                		Log.d(TAG, "-----��ʼ���"+address+"-----");  
                		Boolean flag = (Boolean)createBondMethod.invoke(device);  
                		Log.d(TAG, "-----bond flag-----"+flag); 
                		/*
                        Method cancelUMethod = device.getClass().getMethod("cancelPairingUserInput");
                        Boolean cancelRsult = (Boolean) cancelUMethod.invoke(device);
                        Log.d(TAG, "-----cancelPairingUserInput flag-----"+cancelRsult); */
					} catch (Exception e) {
						Log.e(TAG, "-----���"+address+"ʧ��-----",e);  
					}
                }
                // Attempt to connect to the device
                mChatService.connect(device);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
	
	private void setupChat() {
        Log.d(TAG, "setupChat()");
     // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
    }
	// The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                LogUtil.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    MyToast.showDIYToast(ARMActivity.this, getString(R.string.title_connected_to)+mConnectedDeviceName, 3000, true);
                    ARMActivity.this.startARM();
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                    MyToast.showDIYToast(ARMActivity.this, getString(R.string.title_connecting), 3000, true);
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    MyToast.showDIYToast(ARMActivity.this, getString(R.string.title_not_connected), 3000, true);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                LogUtil.d("�������豸�������ݣ�"+TransferUtil.byte2HexStr(writeBuf));
                // construct a string from the buffer
                MyToast.showToast(ARMActivity.this, "APP���ͣ�"+TransferUtil.byte2HexStr(writeBuf),3000);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                LogUtil.d("�����豸�ϱ����ݣ�"+TransferUtil.byte2HexStr(readBuf));
                // construct a string from the valid bytes in the buffer
                MyToast.showToast(ARMActivity.this, "APP���գ�"+TransferUtil.byte2HexStr(readBuf),3000);
                readMsg(readBuf);
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    private void startARM(){
    	byte[] armpl = TransferUtil.int2bytes(OMApplication.getInstance().getVal(CommonConsts.ARM_PL, 10000));
    	byte[] armrm = TransferUtil.int2bytes(OMApplication.getInstance().getVal(CommonConsts.ARM_RM, 10000));
    	byte[] armsx = TransferUtil.int2bytes(OMApplication.getInstance().getVal(CommonConsts.ARM_SX, 10000));
    	byte[] armzq = TransferUtil.int2bytes(OMApplication.getInstance().getVal(CommonConsts.ARM_ZQ, 10000));
    	int length = armpl.length + armrm.length + armsx.length + armzq.length + 4;
    	ByteBuffer buff = ByteBuffer.allocate(length);//ע��ByteBuffer��������һ������Ϊname.length + 6�ֽڵ�ByteBuffer
		buff.put((byte) 0xE6) // CLA Class
			.put((byte) 0x31) // INS
			.put((byte)length) // P1 Parameter 1
			.put(armpl)
			.put(armrm)
			.put(armsx)
			.put(armzq)
			.put((byte) 0x08); // �͵Ͱ�λ
		sendMessage(buff.array());
    }
    private void stopARM(){
    	final byte[] cmd =
			{ (byte) 0xE6, //
					(byte) 0x32, // 
					(byte) 0x04, // 
					(byte) 0x04, // �͵Ͱ�λ
			};
    	sendMessage(cmd);
    }
    private void readMsg(byte[] msg){
    	if(msg.length < 2){
    		return;
    	}
    	switch (msg[1]) {
		case 0x31:
			String no = TransferUtil.byte2HexStr(Arrays.copyOfRange(msg, 8, 12));
			String version = TransferUtil.byte2HexStr(Arrays.copyOfRange(msg, 12, 14));
			LogUtil.d(TAG, "---�豸���-"+no+";�豸�汾"+version );
			OMApplication.getInstance().setVal(CommonConsts.ARM_NO, no, true);
			OMApplication.getInstance().setVal(CommonConsts.ARM_VERSION, version, true);
			break;
		case 0x32:
					
			break;
		case 0x33:
			int m = TransferUtil.byteArrayToInt(Arrays.copyOfRange(msg, 3, 5));
			setTitle(m+"");
			LogUtil.d(TAG, "---ARM�ϱ�����-"+m );
			break;
		default:
			break;
		}
    }
	/**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(byte[] cmd) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        mChatService.write(cmd);
    }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			final OMDialog dialog = OMDialog.createDialog(this);
			dialog.setTitile(getString(R.string.app_exit_tip));
			dialog.setContent(Html.fromHtml(getString(R.string.app_menu_surelogout)));
			dialog.setCanceledOnTouchOutside(true);
			
			dialog.setBtnOkClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					AppManager.getAppManager().AppExit(ARMActivity.this);
				}
			});
			dialog.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}