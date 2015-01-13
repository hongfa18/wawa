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
        set.setDrawCubic(false);//是否画曲线

        return set;
    }
	
	/**
	 * 添加数据：
	 * 判断是否头五个上报数据，是置0
	 * 根据上报周期计算二维图最大点数据
	 * 上报周期：0.5s~10s
	 * 写数据：有就调用写，与上次上报数据间隔小于1s抛弃 >= 1s时add数据
	 * 取数据：每隔1s取数据，对比上次取到的数据个数，没变add，并判断是否超过x轴30最大数据值，超过
	* @Title: addEntry 
	* @Description: TODO 
	* @param 参数：@param val
	* @return 返回值：void 
	* @ 2015年1月11日上午2:31:58
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
	 * 变换图像
	 * 
	* @Title: changeView 
	* @Description: TODO 
	* @param 参数：
	* @return 返回值：void 
	* @ 2015年1月11日下午7:24:32
	* @author W.Y 
	* @throws
	 */
	//private int lastVal = -1;
	private void changeView(){
		if(addData != null){
			int lastVal = context.getTitleVal();
			if(lastVal != -1){//如果不为-1，就调用添加-目的是为了屏蔽监测设备上报周期超过1s图上对应时间轴上没有数据
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
            //data.addLimitLine(new LimitLine(30));//设置数据的过滤值
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

            LineDataSet set = createSet(yVals,"实时监测数据");
            data.addDataSet(set);
            linechart.notifyDataSetChanged();
            linechart.invalidate();   
        }
    }
    /**
     * 删除数据
    * @Title: removeDataSet 
    * @Description: TODO 
    * @param 参数：
    * @return 返回值：void 
    * @ 2015年1月11日上午2:36:53
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
        
        //由于默认加载时一轴显示将1十等分成小数，写成set1第一个点为90撑到100
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
        chart.setNoDataTextDescription("没有监测数据上报");

        // enable / disable grid lines
        chart.setDrawVerticalGrid(true);//垂直网格线
        chart.setDrawHorizontalGrid(true);//水平网格线
        //
        // enable / disable grid background
        chart.setDrawGridBackground(true);
        chart.setGridColor(GRIDCOLOR & 0x70FFFFFF);
        chart.setGridWidth(3f);

        // enable touch gestures
        chart.setTouchEnabled(false);//点击某点
        // enable scaling and dragging
        chart.setDragEnabled(false);//拖动
        chart.setScaleEnabled(false);//缩放

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
        x.setAdjustXLabels(false);//是否自动动态调整x轴
        x.setAvoidFirstLastClipping(true);
        x.setCenterXLabelText(false);
        x.setPosition(XLabelPosition.BOTTOM);//设置x轴提示文字位置
        
        // animate calls invalidate()...
        //chart.animateX(2500);
    }
}
