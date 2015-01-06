package com.wawa.arm.adapter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.entity.AppParamMap;
import com.wawa.arm.entity.DateInfo;
import com.wawa.arm.entity.TradeRecord;
import com.wawa.arm.utile.PropertiesUtile;
import com.wawa.arm.utile.log.LogUtil;
import com.wawa.arm.R;

public class TradeRecordListAdapter extends BaseAdapter {
	private Context context;
	private List<Object> mItems;

	public TradeRecordListAdapter(Context context, List<Object> items) {
		this.context = context;
		this.mItems = items;
	}
    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position) instanceof DateInfo ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position) instanceof TradeRecord;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder hodler = null;
        Object item = getItem(position);

        if (item instanceof TradeRecord) {
        	TradeRecord  trade = (TradeRecord) item;
        	if (convertView == null) {
        		hodler = new ViewHolder();
        		convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_layout_content, parent, false);
        		hodler.icon = (ImageView)convertView.findViewById(R.id.item_query_list_icon);
        		hodler.moneyNO = (TextView)convertView.findViewById(R.id.item_query_money_number);
        		hodler.resultFlag = (TextView)convertView.findViewById(R.id.item_query_oper_result);
        		hodler.operType = (TextView)convertView.findViewById(R.id.item_query_open_type);
        		hodler.operTime = (TextView)convertView.findViewById(R.id.item_query_oper_time);
        		convertView.setTag(hodler);
        	}else{
        		hodler = (ViewHolder) convertView.getTag();
        	}
        	String money = "--.--";
        	try {
        		DecimalFormat df = new DecimalFormat();
    			df.applyPattern("###,##0.00");
    			Double moneyD = Double.valueOf(trade.getAmount());
    			money = df.format(moneyD/100);
			} catch (Exception e) {
				LogUtil.e(e);
			}
        	hodler.icon.setImageResource(getTypeIcon(trade.getTradeType()));
        	hodler.operType.setText(getTypeName(trade.getTradeType()));
        	hodler.operTime.setText(trade.getTimeHour());
    		hodler.moneyNO.setText(trade.getOpType().replace("0", "-").replace("1", "+")+money);
    		hodler.resultFlag.setText(context.getResources().getString(R.string.select_date_tip003));
            //time.setText(((TradeRecord)item).getTime());
            //title.setText(TradeRecord.getTitle(context,((MessageInfoItemInfo)item).getInfoType()));

        } else {
            if (convertView == null) {
            	hodler = new ViewHolder();
            	convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_layout_header, parent, false);
                hodler.date = (TextView) convertView.findViewById(R.id.id_item_listview_layout_header_time);
                convertView.setTag(hodler);
            }else{
            	hodler = (ViewHolder) convertView.getTag();
            }
            hodler.date.setText(((DateInfo)item).getDate());
        }
        return convertView;
    }
    private class ViewHolder {
		TextView moneyNO;
		TextView resultFlag;
		TextView operType;
		TextView operTime;
		ImageView icon;
		
		TextView date;
	}
    
    public static int getTypeIcon(String tradeType){
    	int id = R.drawable.table_icon_unknown;
    	if(tradeType.equals("33") || tradeType.equals("1305")){//send money
    		id = R.drawable.table_icon_sendmoney;
    	}
    	if(tradeType.equals("27") || tradeType.equals("28") || tradeType.equals("37")|| tradeType.equals("38")){//Buy airtiem
    		id = R.drawable.table_icon_buyairtime;
    	}
    	if(tradeType.equals("1301") || tradeType.equals("1306") || tradeType.equals("22")){//withdraw money
    		id = R.drawable.table_icon_withdraw;
    	}
    	if(tradeType.equals("29") || tradeType.equals("30") || tradeType.equals("1303") || tradeType.equals("1304")){//payment services
    		id = R.drawable.table_icon_payment;
    	}
    	return id;
    }
    public static String getTypeName(String tradeType){
    	AppParamMap appParam = OMApplication.getInstance().getVal(CommonConsts.APP_PARAM_MAP, null);
    	if(appParam == null)
    		return OMApplication.getInstance().getResources().getString(R.string.select_date_tip004);
    	Map<String, String> trade = appParam.getTradTypes();
    	String name = trade.get(tradeType);
    	return name == null?OMApplication.getInstance().getResources().getString(R.string.select_date_tip004):name;
    }
    
    private boolean isFragmentEndLine(int position) {
    	boolean res = false;
    	if(position<getCount()-1) {
    		Object currentObj = getItem(position);
    		Object nextObj = getItem(position+1);
    		if((currentObj instanceof TradeRecord)&&(nextObj instanceof DateInfo)) {
    			res = true;
    		}
    	}else {
    		res = true;
    	}
    	return res;
    }
    
}
