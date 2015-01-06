package com.wawa.arm.adapter;

import java.util.List;
import java.util.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.wawa.arm.DeviceListActivity;
import com.wawa.arm.R;
import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.entity.AppParamMap;
import com.wawa.arm.entity.DateInfo;
import com.wawa.arm.entity.TradeRecord;

public class DeivceListAdapter extends BaseAdapter {
	private DeviceListActivity context;
	private List<String> mItems;
	private int type;

	public DeivceListAdapter(DeviceListActivity context, List<String> items,int type) {
		this.context = context;
		this.mItems = items;
		this.type = type;
	}
    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public String getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder hodler = null;
        final String item = getItem(position);

    	if (convertView == null) {
    		hodler = new ViewHolder();
    		convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_device, parent, false);
    		hodler.clear = (Button)convertView.findViewById(R.id.item_clear_btn);
    		hodler.link = (Button)convertView.findViewById(R.id.item_link_btn);
    		hodler.nameaddr = (TextView)convertView.findViewById(R.id.item_namedaar_txv);
    		convertView.setTag(hodler);
    	}else{
    		hodler = (ViewHolder) convertView.getTag();
    	}
    	hodler.nameaddr.setText(item);
    	
    	Button clear = (Button)convertView.findViewById(R.id.item_clear_btn);
    	clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(item.length() < 17){
					return;
				}
				String address = item.substring(item.length() - 17);
				context.clearDeivce(address);
			}
		});
    	Button link = (Button)convertView.findViewById(R.id.item_link_btn);
    	link.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(item.length() < 17){
					return;
				}
				String address = item.substring(item.length() - 17);
				context.linkDeivce(address);
			}
		});
    	if(type == CommonConsts.NO_PAIR_DEVICE){
    		clear.setVisibility(View.GONE);
    		link.setText("配对连接");
    	}else if(type == CommonConsts.PAIR_DEVICE){
    		link.setText("连接");
    	}
    	
    	if(item.equals(context.getResources().getText(R.string.none_found).toString()) || item.equals(context.getResources().getText(R.string.none_paired).toString())){
    		clear.setVisibility(View.GONE);
    		link.setVisibility(View.GONE);
    	}
        return convertView;
    }
    private class ViewHolder {
		TextView nameaddr;
		Button clear;
		Button link;
	}
    
}
