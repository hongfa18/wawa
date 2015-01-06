package com.wawa.arm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.PowerManager;
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
import com.wawa.arm.common.CommonMethod;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.common.SoundPoolPlay;
import com.wawa.arm.net.WaitUIElement;
import com.wawa.arm.service.BluetoothChatService;
import com.wawa.arm.utile.MyToast;
import com.wawa.arm.utile.TransferUtil;
import com.wawa.arm.utile.log.LogUtil;
import com.wawa.arm.utile.widgets.OMDialog;
import com.wawa.arm.utile.widgets.SearchDevicesView;

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
    public static final int REQUEST_SEETTING_DEVICE = 3;
 // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
 // Name of the connected device
    private String mConnectedDeviceName = null;
    private boolean isStopflag;
    private WaitUIElement dialog;
//    private WebView runWebView=null;
    private SearchDevicesView scan;
    private Timer timer;
    private long lastUpDataTime,cmdResultTime;
	protected int stopCount = 0;
	
	private PowerManager.WakeLock mPowerMgr;
	private SoundPoolPlay play;
	

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState,true);
        setContentView(R.layout.layout_activity_main,true);
        setTitle("");
        
        openImg = (ImageView)findViewById(R.id.open_icon);
        settingImg = (ImageView)findViewById(R.id.setting_icon);
        
        openImg.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
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
				startActivityForResult(intent, REQUEST_SEETTING_DEVICE);
				overridePendingTransition(R.anim.slide_in_right_my,R.anim.slide_out_left_my);
        	}
        });
        
        /**
         * ����һ��
         * ��ȡ����Adapter
         */
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "���ֻ���֧������", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(DeviceListActivity.ACTION_PAIRING_REQUEST);
        this.registerReceiver(mReceiver, filter);
        /*���ұ�������ģ��֧��profile*/
		try {
			Method createBondMethod = mBluetoothAdapter.getClass().getMethod("getUuids");
			ParcelUuid[] returnValue = (ParcelUuid[]) createBondMethod.invoke(mBluetoothAdapter);
			String str = "";
			for (int i = 0; i < returnValue.length; i++) {
				str = str+","+returnValue[i].getUuid().toString();
			}
			LogUtil.d(TAG, "--���豸����֧��UUID--"+str);//TODO ���жϱ��������Ƿ�֧����������Ӧ��
		} catch (Exception e) {
			LogUtil.e(TAG,"", e);
		}
		dialog = new WaitUIElement(this, true);
		/*runWebView = (WebView) findViewById(R.id.gif);
		runWebView.loadDataWithBaseURL(null,"<HTML><body bgcolor='#f3f3f3'><div align=center><IMG src='file:///android_asset/strongscan.gif'/></div></body></html>", "text/html", "UTF-8",null);*/
		scan = (SearchDevicesView) findViewById(R.id.scan);
		scan.setHandler(handler);
		scan.setWillNotDraw(false);
		scan.setClickable(true);
		scan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(needOpenChooice){
					needOpenChooice = false;
					changeButtonStatu(currentStatu);
				}
				
			}
		});
		//changeButtonStatu(CommonConsts.STATU_WORKING);//for test
		
		//TODO �����Դ����ģ��
        //PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //mPowerMgr = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "wawaarm");
		
		play = new SoundPoolPlay(this);
		lastVal = -5;
	}
	
	/**
	* @Title: initStatutButton 
	* @Description:  ��ʼ��״̬
	* @param ������
	* @return ����ֵ��void 
	* @ 2014��12��1������12:14:53
	* @author W.Y 
	* @throws
	 */
	private void initStatutButton(boolean first) {
		/**��ȡ�����*/
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
        	List<String> armBlu = new ArrayList<String>();
            for (BluetoothDevice device : pairedDevices) {
                //device.getName() + "\n" + device.getAddress();
            	if(CommonMethod.checkDeviceName(device.getName())){//TODO ��ȷ������豸������������
            		armBlu.add(device.getAddress());
            	}
            }
            if(armBlu.size() == 0){//û��
            	changeButtonStatu(CommonConsts.STATU_SETTING);
        	}else if(armBlu.size() == 1 && first){//һ��
    			changeButtonStatu(CommonConsts.STATU_HISTORY,armBlu.get(0));
        	}else{//���
        		changeButtonStatu(CommonConsts.STATU_HISTORY);
        	}
        } else {
            changeButtonStatu(CommonConsts.STATU_SETTING);
        }
		
	}
	
	private int currentStatu = -1;
	private void changeButtonStatu(int statuOne,final Object... vals) {
		currentStatu = statuOne;
		if(timer != null){
			timer.cancel();
			timer = null;
		}
		View.OnClickListener click = null;
		boolean startMovie = false;
		boolean needClick = false;
		int bgId = 0;
		switch (statuOne) {
		case CommonConsts.STATU_SETTING://����
			play.stopAll();
			scan.stopSerch();
			needClick = true;
			bgId = R.drawable.selector_globle_bg;
			click = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					can = false;
					Intent serverIntent = new Intent(ARMActivity.this, DeviceListActivity.class);
        			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
				}
			};
			break;
		case CommonConsts.STATU_HISTORY://��������ʷ
			play.stopAll();
			scan.stopSerch();
			needClick = true;
			setTitle("");
			bgId = R.drawable.sp_bg;
			click = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(vals.length > 0 && vals[0] != null){
						connect((String)vals[0]);
					}else{
						Intent serverIntent = new Intent(ARMActivity.this, DeviceListActivity.class);
	        			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
					}
				}
			};
			break;
		case CommonConsts.STATU_WORKING://������֩��
			//TODO ��ɫ
			//mPowerMgr.acquire();//��ֹ����
			scan.startSerch();
			scan.changeLevle(0);
			bgId = R.anim.animation_sp_movie;
			startMovie = true;
			click = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					stopARM();
					MyToast.showToast(getApplicationContext(), "����豸��ֹͣ�����ϱ�", 3000);
				}
			};
			lastUpDataTime = System.currentTimeMillis();
			stopCount = 0;
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if(Thread.currentThread().isInterrupted()) return;
					if((System.currentTimeMillis() - lastUpDataTime) > Integer.parseInt(OMApplication.getInstance().getVal(CommonConsts.APP_TIMEOUT, "10"))*1000){
						handler.sendEmptyMessage(0);
					}
				}
			},1000,1000);
			break;
		case CommonConsts.STATU_PAUSE://��ͣ���
			//mPowerMgr.release();
			scan.onPause();
			play.stopAll();
			setTitle("0");
			bgId = R.drawable.sp_bg_03;
			break;
		default:
			bgId = R.drawable.widget_count_bg2;
			click = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			};
			break;
		}
		if(startMovie){
			openImg.setImageResource(bgId);
			Drawable animationDrawable = openImg.getDrawable();
			if(animationDrawable instanceof AnimationDrawable){
				((AnimationDrawable)animationDrawable).start();
			}
		}else{
			Drawable animationDrawable = openImg.getDrawable();
			if(animationDrawable instanceof AnimationDrawable){
				((AnimationDrawable)animationDrawable).stop();
			}
			openImg.setImageResource(bgId);
		}
		//openImg.invalidate();
		openImg.setVisibility(View.GONE);
		openImg.setOnClickListener(click);
		if(needClick)
			openImg.post(new Runnable() {
				@Override
				public void run() {
					openImg.performClick();
				}
			});
		openImg.setClickable(false);
	}
	public static final int STOP_SCAN = 0;
	public static final int START_SCAN = 1;
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message arg0) {
			switch (arg0.what) {
			case STOP_SCAN://ֹͣ
				stopARM();
				break;
			case START_SCAN://��ʼ
				startARM();
				break;
			default:
				break;
			}
			return false;
		}
	});
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
	
	
	private void connect(String address) {
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
                        new Object[]{OMApplication.getInstance().getVal(CommonConsts.ARM_PAIR_KEY, CommonConsts.DEFAULT_PERM_KEY).getBytes()});
                Log.d(TAG, "-----setPin flag-----"+returnValue); 
        		Method createBondMethod = device.getClass().getMethod("createBond");  
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
	private void setupChat() {
        Log.d(TAG, "setupChat()");
     // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
        initStatutButton(true);
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
                    ARMActivity.this.startARM();
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                	dialog.showProcessDialog("�豸������...");
                	dialog.setCancelLis(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialogin) {
							needOpenChooice = true;
							stopARM();
							mChatService.stop();
							dialog.dismissProcessDialog();
						}
					});
                    //MyToast.showDIYToast(ARMActivity.this, getString(R.string.title_connecting), 3000, true);
                    break;
                case BluetoothChatService.STATE_LISTEN://����ͨѶʧ��
                	dialog.dismissProcessDialog();
                	changeButtonStatu(CommonConsts.STATU_SETTING);
                	break;
                case BluetoothChatService.STATE_LISTEN_CLOSE://ͨѶ�ж�
                	dialog.dismissProcessDialog();
                	initStatutButton(false);
                	notification(-2);
                	break;
                case BluetoothChatService.STATE_SEND_MSG_ERROR://ָ���·�ʧ��
                	dialog.dismissProcessDialog();
                	changeButtonStatu(CommonConsts.STATU_PAUSE);
                	notification(-3);
                	break;
                case BluetoothChatService.STATE_NONE:
                	MyToast.showToast(getApplicationContext(), "��Ӧ��ʱ�����ӹرգ�����������豸��", 3000);
                	dialog.dismissProcessDialog();
                	notification(-4);
                    //MyToast.showDIYToast(ARMActivity.this, getString(R.string.title_not_connected), 3000, false);
                    //initStatutButton();
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                LogUtil.d("�������豸�������ݣ�"+TransferUtil.byte2HexStr(writeBuf));
               /* switch (writeBuf[1]) {
				case 0x31:
					MyToast.showToast(getApplicationContext(), "����ָ��ɹ�����", 2000);
					break;
				case 0x32:
					MyToast.showToast(getApplicationContext(), "ָֹͣ��ɹ�����", 2000);
					break;
				default:
					break;
				}*/
                // construct a string from the buffer
                //MyToast.showToast(ARMActivity.this, "APP���ͣ�"+TransferUtil.byte2HexStr(writeBuf),3000);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                LogUtil.d("�����豸�ϱ����ݣ�"+TransferUtil.byte2HexStr(readBuf));
                // construct a string from the valid bytes in the buffer
                //MyToast.showToast(ARMActivity.this, "APP���գ�"+TransferUtil.byte2HexStr(readBuf),3000);
                readMsg(readBuf);
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                //Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    private void startARM(){
    	int pl = Integer.parseInt(OMApplication.getInstance().getVal(CommonConsts.ARM_PL, "10050"));
    	int rm = Integer.parseInt(OMApplication.getInstance().getVal(CommonConsts.ARM_RM, "12"));
    	int sx = Integer.parseInt(OMApplication.getInstance().getVal(CommonConsts.ARM_SX, "2"));
    	int zq = Integer.parseInt(OMApplication.getInstance().getVal(CommonConsts.ARM_ZQ, "2000"))/100;
    	byte[] armpl = TransferUtil.int2bytes(pl);
    	byte armrm = (byte)rm;//Integer.toHexString(rm);//TransferUtil.int2bytes(rm);
    	byte armsx = (byte)sx;//TransferUtil.int2bytes(sx);
    	byte armzq = (byte)zq;//TransferUtil.int2bytes(zq);
    	int length = armpl.length + 1 + 1 + 1 + 4;
    	/*int plc = 0;
    	for(int i = 0;i<armpl.length;i++){
    		plc = plc+armpl[i];
    	}*/
    	byte lowEight = (byte)((pl+pl/256+rm+sx+zq+length+0xE6+0x31) & 0xFF);
    	ByteBuffer buff = ByteBuffer.allocate(length);//ע��ByteBuffer��������һ������Ϊname.length + 6�ֽڵ�ByteBuffer
		buff.put((byte) 0xE6) // CLA Class
			.put((byte) 0x31) // INS
			.put((byte)length) // P1 Parameter 1
			.put(armpl)
			.put(armrm)
			.put(armsx)
			.put(armzq)
			.put(lowEight); // �͵Ͱ�λ
		sendMessage(buff.array());
    }
    private void stopARM(){
    	changeButtonStatu(CommonConsts.STATU_PAUSE);
		notification(-1);
    	byte lowEight = (byte)((0xE6+0x32+0x04) & 0xFF);
    	final byte[] cmd =
			{ (byte) 0xE6, //
					(byte) 0x32, // 
					(byte) 0x04, // 
					(byte) lowEight, // �͵Ͱ�λ
			};
    	sendMessage(cmd);
    }
    private void readMsg(byte[] msg){
    	LogUtil.d("--ARM��������--"+TransferUtil.byte2HexStr(msg));
    	//TODO ����У��
    	if(msg.length < 2 || (msg[0] & 0XFF) != 0xD6){
    		return;
    	}
    	switch (msg[1]) {
		case 0x31://��ʼ
			if(msg.length > 20 ){
				cmdResultTime = 0;
				MyToast.showDIYToast(ARMActivity.this, getString(R.string.title_connected_to)+mConnectedDeviceName, 3000, true);
				dialog.dismissProcessDialog();
				String no = TransferUtil.byte2HexStr(Arrays.copyOfRange(msg, 8, 16));
				String version = new String(Arrays.copyOfRange(msg, 16, 22));//TransferUtil.byte2HexStr(Arrays.copyOfRange(msg, 16, 22));
				LogUtil.d(TAG, "---�豸���-"+no+";�豸�汾"+version );
				OMApplication.getInstance().setVal(CommonConsts.ARM_NO, no.toUpperCase().replaceAll("F", ""), true);
				OMApplication.getInstance().setVal(CommonConsts.ARM_VERSION, version, true);
				changeButtonStatu(CommonConsts.STATU_WORKING);
			}
			break;
		case 0x32://ֹͣ
			if(msg[2] == 0x04){
				cmdResultTime = 0;
				dialog.dismissProcessDialog();
			}
			break;
		case 0x33://�����ϱ�
			if(msg.length != 5 || msg[2] != 0x05 || currentStatu != CommonConsts.STATU_WORKING) return;
			lastUpDataTime = System.currentTimeMillis();
			int m = Arrays.copyOfRange(msg, 3, 4)[0];
			if(0 <= m && m <= 91){
				setTitle(m+"");
				scan.changeLevle(m);
				if(!isBack)
					play.play(m);
				if(isBack){
					notification(m);
				}else{
					disnotifcation();
				}
			}
			LogUtil.d(TAG, "---ARM�ϱ�����-"+m);
			break;
		default:
			break;
		}
    }
	/**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(final byte[] cmd) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        
        cmdResultTime = System.currentTimeMillis();
        switch (cmd[1]) {
		case 0x31:
			dialog.showProcessDialog("���������...");
			break;
		case 0x32:
			//dialog.showProcessDialog("����ֹͣ���...");
			play.stopAll();
			break;
		default:
			break;
		}
        if(timer != null){
			timer.cancel();
			timer = null;
		}
        stopCount = 0;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(Thread.currentThread().isInterrupted()) return;
				if(cmdResultTime == 0 ){
					timer.cancel();
					timer = null;
					return;
				}
				if((System.currentTimeMillis() - cmdResultTime) > Integer.parseInt(OMApplication.getInstance().getVal(CommonConsts.APP_TIMEOUT, "10"))*1000){
					cmdResultTime = System.currentTimeMillis();
					// Check that there's actually something to send
					stopCount++;
					if(stopCount > CommonConsts.STOP_COUNT){
						mChatService.stop();
					}else{
						mChatService.write(cmd);
					}
				}
			}
		},1000,1000);
		// Check that there's actually something to send
        mChatService.write(cmd);
    }
    
    /**
     *�������������㲥
     */
    private boolean can = false;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "---�����㲥---"+action);
            if(DeviceListActivity.ACTION_PAIRING_REQUEST.equals(action) && can){
            	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            	Log.d(TAG, "--�豸"+device.getAddress()+"����--");
				try {
					
					Method setPinMethod = device.getClass().getDeclaredMethod("setPin",
							new Class[]{byte[].class});
					Boolean returnValue = (Boolean) setPinMethod.invoke( device,
							new Object[]{OMApplication.getInstance().getVal(CommonConsts.ARM_PAIR_KEY, CommonConsts.DEFAULT_PERM_KEY).getBytes()});
					Log.d(TAG, "-----setPin flag-----"+returnValue); 
					
					Method createBondMethod = device.getClass().getMethod("createBond");  
            		Boolean flag = (Boolean)createBondMethod.invoke(device);  
            		Log.d(TAG, "-----bond flag-----"+flag); 
            		
					Method cancelUMethod = device.getClass().getMethod("cancelPairingUserInput");
					Boolean cancelRsult = (Boolean) cancelUMethod.invoke(device);
					Log.d(TAG, "-----cancelPairingUserInput flag-----"+cancelRsult);
            		
				} catch (Exception e) {
					Log.e(TAG, "--setPin error---", e);
				}
            }
        }
        
        
    };
    private boolean isBack = false;
    @Override
   	protected void onResume() {
   		super.onResume();
   		//TODO 
   		isBack = false;
   		disnotifcation();
   	}
    
	@Override
	protected void onPause() {
		super.onPause();
		play.stopAll();
		isBack = true;
		notification(lastVal);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mChatService.stop();
		this.unregisterReceiver(mReceiver);
		//mPowerMgr.release();
		play.destory();
	}
	
	private boolean needOpenChooice;
	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
		/*if(needOpenChooice){
			needOpenChooice = false;
			changeButtonStatu(currentStatu);
		}*/
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE://�����������淵�ص������豸
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	can = true;
                // Get the device MAC address
                String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // Get the BLuetoothDevice object
                connect(address);
                if(closeDialog != null && closeDialog.isShowing()) closeDialog.dismiss();
            }else{
            	needOpenChooice = true;
            }
            break;
        case REQUEST_SEETTING_DEVICE://���÷���
            if (resultCode == Activity.RESULT_OK) {
                boolean flag = data.getExtras().getBoolean("needChangeVal");
                if(flag && currentStatu == CommonConsts.STATU_WORKING){
                	startARM();
                }
            }
            break;
        case REQUEST_ENABLE_BT://�û��������������
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "---�û�û�д������豸��APP�˳�---");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                AppManager.getAppManager().AppExit(ARMActivity.this);
            }
        }
    }
	private OMDialog closeDialog;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			closeDialog = OMDialog.createDialog(this);
			closeDialog.setTitile(getString(R.string.app_exit_tip));
			closeDialog.setContent(Html.fromHtml(getString(R.string.app_menu_surelogout)));
			closeDialog.setCanceledOnTouchOutside(true);
			
			closeDialog.setBtnOkClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mChatService.stop();
					closeDialog.dismiss();
					AppManager.getAppManager().AppExit(ARMActivity.this);
				}
			});
			closeDialog.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	private Notification notification;
	private NotificationManager nManager;
	private PendingIntent pendingIntent;
	private int lastVal = 0;//δ����
	/**
	* @Title: notifcation 
	* @Description: ��̨֪ͨ 
	* @param ������@param level
	* @return ����ֵ��void 
	* @ 2014��12��5������12:39:26
	* @author W.Y 
	* @throws
	 */
	private void notification(int level){
		/*if(lastVal == level){
			return;
		}else{
			lastVal = level;
		}*/
		if(notification == null){
			Intent intent = new Intent(this,StartActivity.class);  
			intent.setAction(Intent.ACTION_MAIN); 
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);  
			
			//����һ��֪ͨ  
			notification = new Notification(R.drawable.change_icon, "", System.currentTimeMillis());
			// ������������   
			notification.flags = Notification.FLAG_ONGOING_EVENT;  
			//��NotificationManager��notify����֪ͨ�û����ɱ�������Ϣ֪ͨ  
			nManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		}
		String tip = "����:"+level;
	    //notification.largeIcon = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.launchimage));
        //notice.vibrate = new long[] { 100, 250, 100, 500 };//���� 100 �����ӳٺ��� 250 ���룬��ͣ 100 ��������� 500 ����
	    switch (level) {
		case -1://��ͣ
			tip = "�����ͣ!";
			break;
		case -2://���ӶϿ�
			tip = "�����豸ͨѶ�ж�!";
			break;
		case -3://
			tip = "ָ���·�ʧ�ܣ�";
			break;
		case -4://
			tip = "��Ӧ��ʱ�����ӹرգ�����������豸��";
			break;
		case -5://
			tip = "�޼���豸���ӣ�";
			break;
		default:
			break;
		}
	    //createImag(level >= 0?level+"":"_");
	    int iconId = level == -5?R.drawable.change_icon:getResources().getIdentifier("change_icon_"+(level >= 0?level+"":"_"),"drawable", getPackageName());
		notification.icon = iconId;//R.drawable.change_icon;
		notification.setLatestEventInfo(this, OMApplication.getInstance().getVal(CommonConsts.ARM_ADDR,""),tip, pendingIntent);  
	    //notification.icon = R.drawable.icon;   
	    //notification.tickerText = "����һ���µ���Ϣ";   
	    //notification.when = System.currentTimeMillis(); //ʱ��
	    //notification.defaults |= Notification.DEFAULT_SOUND;  // �趨����    
        //notification.defaults |= Notification.DEFAULT_VIBRATE;  //�趨��(���VIBRATEȨ��)   
        //notification.defaults |= Notification.DEFAULT_LIGHTS; // �趨LED������ 
        //notification.sound = Uri.parse("file:///sdcard/notification/ringer.mp3");//�Զ�������
          
	    nManager.notify(0, notification);//id��Ӧ����֪ͨ��Ψһ��ʶ  
	    //���ӵ����ͬid��֪ͨ�Ѿ����ύ����û�б��Ƴ����÷������ø��µ���Ϣ���滻֮ǰ��֪ͨ��  
	}
	private void disnotifcation(){
		if(nManager != null){
			nManager.cancelAll();
		}
	}
	
	private void createImag(String str){
       Bitmap photo = BitmapFactory.decodeResource(this.getResources(), R.drawable.change_icon);
       int width = photo.getWidth(), hight = photo.getHeight();
       Bitmap icon = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888); //����һ���յ�BItMap  
       Canvas canvas = new Canvas(icon);//��ʼ���������Ƶ�ͼ��icon��  
        
       Paint photoPaint = new Paint(); //��������  
       photoPaint.setDither(true); //��ȡ��������ͼ�����  
       photoPaint.setFilterBitmap(true);//����һЩ  
        
       Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());//����һ��ָ�����¾��ε�����  
       Rect dst = new Rect(0, 0, width, hight);//����һ��ָ�����¾��ε�����  
       canvas.drawBitmap(photo, src, dst, photoPaint);//��photo ���Ż������� dstʹ�õ������photoPaint  
        
       Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);//���û���  
       textPaint.setTextSize(39.0f);//�����С  20
       textPaint.setTypeface(Typeface.DEFAULT_BOLD);//����Ĭ�ϵĿ��  
       textPaint.setColor(getResources().getColor(R.color.cant_click_btn_color));//Color.RED);//���õ���ɫ  
       //textPaint.setShadowLayer(3f, 1, 1,this.getResources().getColor(android.R.color.background_dark));//Ӱ��������  
       // ����ÿһ������  
       textPaint.setTextAlign(Align.CENTER); 

       FontMetrics fontMetrics = textPaint.getFontMetrics(); 
       // �������ָ߶� 
       float fontHeight = fontMetrics.bottom - fontMetrics.top; 
       // ��������baseline 
       float textBaseY = hight - (hight - fontHeight) / 2 - fontMetrics.bottom; 
       canvas.drawText(str, width / 2, textBaseY, textPaint);//������ȥ�֣���ʼδ֪x,y������ֻ�ʻ���  20 26
       canvas.save(Canvas.ALL_SAVE_FLAG); 
       canvas.restore(); 
       saveMyBitmap(icon,str);
	}

	private void saveMyBitmap(Bitmap icon,String name) {
		// TODO Auto-generated method stub
		FileOutputStream bitmapWtriter = null;
        try {
	         bitmapWtriter = new FileOutputStream("/sdcard/smartpay/change_icon_"+name+".png");
	         icon.compress(Bitmap.CompressFormat.PNG, 100, bitmapWtriter);
        } catch (FileNotFoundException e) {
        	LogUtil.e(e);
        }
	}
}