package com.wawa.arm.utile;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.Legend.LegendForm;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.github.mikephil.charting.utils.YLabels;
import com.wawa.arm.common.BaseActivity;
import com.wawa.arm.utile.widgets.MyLineChart;

public class LineChartUtil {
	private MyLineChart linechart;
	private BaseActivity context;
	private Typeface mTf;
	private static int GRIDCOLOR = Color.GREEN;
	
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				changeView();
				break;

			default:
				break;
			}
			return false;
		}
	});
	public LineChartUtil(BaseActivity context,MyLineChart linechart){
		this.context = context;
		this.linechart = linechart;
		this.mTf = Typeface.createFromAsset(context.getAssets(), "OpenSans-Bold.ttf");
	}
	
	public void init(){
		setupChart(linechart, null);
		linechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
			@Override
			public void onValueSelected(Entry e, int dataSetIndex) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onNothingSelected() {
				// TODO Auto-generated method stub
			}
		});
		linechart.setDrawYValues(false);
		linechart.setDrawGridBackground(false);
		linechart.setDescription("");
		
        linechart.invalidate();
	}
	
	private LineDataSet createSet(ArrayList<Entry> yVals,String title) {
        LineDataSet set = new LineDataSet(yVals, title);
        set.setLineWidth(3.5f);
        //set.setCircleSize(3.5f);
        set.setCircleSize(0.01f);
        set.setColor(GRIDCOLOR & 0x60FFFFFF);
        set.setCircleColor(GRIDCOLOR & 0x60FFFFFF);
        set.setHighLightColor(GRIDCOLOR & 0x60FFFFFF);
        set.setDrawCubic(false);//�Ƿ�����

        return set;
    }
	
	/**
	 * ������ݣ�
	 * �ж��Ƿ�ͷ����ϱ����ݣ�����0
	 * �����ϱ����ڼ����άͼ��������
	 * �ϱ����ڣ�0.5s~10s
	 * д���ݣ��о͵���д�����ϴ��ϱ����ݼ��С��1s���� >= 1sʱadd����
	 * ȡ���ݣ�ÿ��1sȡ���ݣ��Ա��ϴ�ȡ�������ݸ�����û��add�����ж��Ƿ񳬹�x��30�������ֵ������
	* @Title: addEntry 
	* @Description: TODO 
	* @param ������@param val
	* @return ����ֵ��void 
	* @ 2015��1��11������2:31:58
	* @author W.Y 
	* @throws
	 */
	private List<Integer> addData;
	private long lastAddDataTime;
	private Timer timer;
	public boolean isNeedSetZero(){
		return addData == null || addData.size() < 6;
	}
	public synchronized void addEntry(int val) {
		if(addData == null){
			addData = new ArrayList<Integer>();
		}
		if((System.currentTimeMillis() - lastAddDataTime) >= 1000){
			while (addData.size() >= 30) {
				addData.remove(0);
			}
			addData.add(val);
			lastAddDataTime = System.currentTimeMillis();
		}
		if(timer == null){
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					handler.sendEmptyMessage(0);
				}
			}, 10,1000);
		}
	}
	/**
	 * �任ͼ��
	 * 
	* @Title: changeView 
	* @Description: TODO 
	* @param ������
	* @return ����ֵ��void 
	* @ 2015��1��11������7:24:32
	* @author W.Y 
	* @throws
	 */
	//private int lastVal = -1;
	private void changeView(){
		if(addData != null){
			int lastVal = context.getTitleVal();
			if(lastVal != -1){//�����Ϊ-1���͵������-Ŀ����Ϊ�����μ���豸�ϱ����ڳ���1sͼ�϶�Ӧʱ������û������
				addEntry(lastVal);
			}
			lastVal = addData.get(addData.size() - 1);
		}

        LineData data = linechart.getData();
        
        if(data != null) {
            LineDataSet set = data.getDataSetByIndex(1);
            // set.addEntry(...);

            if (set != null) {
            	data.removeDataSet(set);
            }
            addDataSet(addData);
            //data.addLimitLine(new LimitLine(30));//�������ݵĹ���ֵ
            /*if(set.getEntryCount() < 6){
            	data.addEntry(new Entry(0, set.getEntryCount()), 0);
            }else if(set.getEntryCount() == 30){
            	data.removeDataSet(0);
            	set.removeEntry(0);
            	set.addEntry(new Entry((float) (Math.random() * 100), set.getEntryCount()));
            	data.addDataSet(set);
            	data.removeDataSet(data.getDataSetByIndex(data.getDataSetCount() - 1));
            	linechart.notifyDataSetChanged();
            	linechart.invalidate();   
            	set.removeEntry(0);
            	set.addEntry(new Entry(val, set.getEntryCount()));
            	data.addDataSet(set);
            }else{
            	data.addEntry(new Entry(val, set.getEntryCount()), 0);
            }*/
            // let the chart know it's data has changed
            //linechart.notifyDataSetChanged();
            // redraw the chart
            //linechart.invalidate();
        }
    }


    private void addDataSet(List<Integer> list) {

        LineData data = linechart.getData();
        
        if(data != null) {
            // create 30 y-vals
            ArrayList<Entry> yVals = new ArrayList<Entry>();

            for (int i = 0; i < list.size(); i++)
                yVals.add(new Entry(list.get(i), i));

            LineDataSet set = createSet(yVals,"ʵʱ�������");
            data.addDataSet(set);
            linechart.notifyDataSetChanged();
            linechart.invalidate();   
        }
    }
    /**
     * ɾ������
    * @Title: removeDataSet 
    * @Description: TODO 
    * @param ������
    * @return ����ֵ��void 
    * @ 2015��1��11������2:36:53
    * @author W.Y 
    * @throws
     */
    public void removeDataSet(boolean onlyCancelTimer) {
    	if(timer != null){
    		timer.cancel();
    		timer = null;
    	}
    	if(addData != null ) addData.clear();
    	if(onlyCancelTimer) return;

        LineData data = linechart.getData();
        
        if(data != null) {

            data.removeDataSet(data.getDataSetByIndex(1));

            linechart.notifyDataSetChanged();
            linechart.invalidate();   
        }
    }
    
    private void addEmptyData() {
        // create 30 x-vals
        String[] xVals = new String[30];

        for (int i = 0; i < 30; i++)
            xVals[i] = "" + i;

        // create a chartdata object that contains only the x-axis labels (no entries or datasets)
        LineData data = new LineData(xVals);
        
        //����Ĭ�ϼ���ʱһ����ʾ��1ʮ�ȷֳ�С����д��set1��һ����Ϊ90�ŵ�100
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        yVals.add(new Entry(90, 0));
        LineDataSet set = new LineDataSet(yVals, "");
        set.setLineWidth(1f);
        set.setCircleSize(0.5f);
        set.setColor(GRIDCOLOR & 0x00FFFFFF);
        set.setCircleColor(GRIDCOLOR & 0x00FFFFFF);
        set.setHighLightColor(GRIDCOLOR & 0x00FFFFFF);
        data.addDataSet(set);

        linechart.setData(data);
    }
    
    private void setupChart(LineChart chart, LineData data) {

        // if enabled, the chart will always start at zero on the y-axis
        chart.setStartAtZero(true);

        // disable the drawing of values into the chart
        chart.setDrawYValues(true);

        chart.setDrawBorder(false);
        chart.setBorderColor(GRIDCOLOR & 0x70FFFFFF);
        chart.setBorderWidth(3);

        // no description text
        chart.setDescription("");
        chart.setNoDataTextDescription("û�м�������ϱ�");

        // enable / disable grid lines
        chart.setDrawVerticalGrid(true);//��ֱ������
        chart.setDrawHorizontalGrid(true);//ˮƽ������
        //
        // enable / disable grid background
        chart.setDrawGridBackground(true);
        chart.setGridColor(GRIDCOLOR & 0x70FFFFFF);
        chart.setGridWidth(3f);

        // enable touch gestures
        chart.setTouchEnabled(false);//���ĳ��
        // enable scaling and dragging
        chart.setDragEnabled(false);//�϶�
        chart.setScaleEnabled(false);//����

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);//

        chart.setBackgroundColor(Color.TRANSPARENT);//TODO

        chart.setValueTypeface(mTf);

        // add data
        //chart.setData(data);
        addEmptyData();
        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(LegendForm.CIRCLE);
        l.setFormSize(10f);
        l.setTextColor(Color.WHITE);
        l.setTypeface(mTf);

        //chart.setYRange(0, 100, true);
        YLabels y = chart.getYLabels();
        y.setTextColor(Color.WHITE);
        y.setTypeface(mTf);
        y.setLabelCount(9);
        y.setTextSize(30f);
        y.setDrawUnitsInYLabel(false);;
        //y.setDrawUnitsInYLabel(true);

        XLabels x = chart.getXLabels();
        x.setTextColor(Color.WHITE);
        x.setTypeface(mTf);
        x.setTextSize(5f);
        x.setAdjustXLabels(false);//�Ƿ��Զ���̬����x��
        x.setAvoidFirstLastClipping(true);
        x.setCenterXLabelText(false);
        x.setPosition(XLabelPosition.BOTTOM);//����x����ʾ����λ��
        
        // animate calls invalidate()...
        //chart.animateX(2500);
    }
}
