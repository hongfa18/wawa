package com.wawa.arm.utile.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;

public class PullToRefreshListView extends PullToRefreshBase<ListView> implements
		OnScrollListener {
	
	/**
	 * Interface that allows PullToRefreshBase to hijack the call to
	 * AdapterView.setEmptyView()
	 * 
	 * @author chris
	 */
	public interface EmptyViewMethodAccessor {

		/**
		 * Calls upto AdapterView.setEmptyView()
		 * 
		 * @param View
		 *            to set as Empty View
		 */
		public void setEmptyViewInternal(View emptyView);

		/**
		 * Should call PullToRefreshBase.setEmptyView() which will then
		 * automatically call through to setEmptyViewInternal()
		 * 
		 * @param View
		 *            to set as Empty View
		 */
		public void setEmptyView(View emptyView);
		
	}

	class InternalListView extends ListView implements EmptyViewMethodAccessor {

		public InternalListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public void setEmptyView(View emptyView) {
			PullToRefreshListView.this.setEmptyView(emptyView);
		}
		
		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}

		public ContextMenuInfo getContextMenuInfo() {
			return super.getContextMenuInfo();
		}
	}

	public PullToRefreshListView(Context context) {
		super(context);
		refreshableView.setOnScrollListener(this);
		this.setDisableScrollingWhileRefreshing(false);
	}
	
	public PullToRefreshListView(Context context, int mode) {
		super(context, mode);
		refreshableView.setOnScrollListener(this);
		this.setDisableScrollingWhileRefreshing(false);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		refreshableView.setOnScrollListener(this);
		this.setDisableScrollingWhileRefreshing(false);
	}

	@Override
	public ContextMenuInfo getContextMenuInfo() {
		return ((InternalListView) getRefreshableView()).getContextMenuInfo();
	}

	@Override
	protected final ListView createRefreshableView(Context context, AttributeSet attrs) {
		ListView lv = new InternalListView(context, attrs);

		// Set it to this so it can be used in ListActivity/ListFragment
		lv.setId(android.R.id.list);
		return lv;
	}
	
	private int lastSavedFirstVisibleItem = -1;
	private OnScrollListener onScrollListener;
	private OnLastItemVisibleListener onLastItemVisibleListener;
	private View emptyView;
	private FrameLayout refreshableViewHolder;
	private ImageView mTopImageView;

	public final void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
			final int totalItemCount) {

		if (null != onLastItemVisibleListener) {
			// detect if last item is visible
			if (visibleItemCount > 0 && (firstVisibleItem + visibleItemCount == totalItemCount)) {
				// only process first event
				if (firstVisibleItem != lastSavedFirstVisibleItem) {
					lastSavedFirstVisibleItem = firstVisibleItem;
					onLastItemVisibleListener.onLastItemVisible();
				}
			}
		}

		if (null != onScrollListener) {
			onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	public final void onScrollStateChanged(final AbsListView view, final int scrollState) {
		if (null != onScrollListener) {
			onScrollListener.onScrollStateChanged(view, scrollState);
		}
	}
	
	public void setBackToTopView(ImageView mTopImageView){
		this.mTopImageView = mTopImageView;
		this.mTopImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (refreshableView instanceof ListView ) {
					((ListView)refreshableView).setSelection(0);
				}
			}
		});
	}

	/**
	 * Sets the Empty View to be used by the Adapter View.
	 * 
	 * We need it handle it ourselves so that we can Pull-to-Refresh when the
	 * Empty View is shown.
	 * 
	 * Please note, you do <strong>not</strong> usually need to call this method
	 * yourself. Calling setEmptyView on the AdapterView will automatically call
	 * this method and set everything up. This includes when the Android
	 * Framework automatically sets the Empty View based on it's ID.
	 * 
	 * @param newEmptyView
	 *            - Empty View to be used
	 */
	public final void setEmptyView(View newEmptyView) {
		// If we already have an Empty View, remove it
		if (null != emptyView) {
			refreshableViewHolder.removeView(emptyView);
		}

		if (null != newEmptyView) {
			ViewParent newEmptyViewParent = newEmptyView.getParent();
			if (null != newEmptyViewParent && newEmptyViewParent instanceof ViewGroup) {
				((ViewGroup) newEmptyViewParent).removeView(newEmptyView);
			}

			this.refreshableViewHolder.addView(newEmptyView, ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT);
		}

		if (refreshableView instanceof EmptyViewMethodAccessor) {
			((EmptyViewMethodAccessor) refreshableView).setEmptyViewInternal(newEmptyView);
		} else {
			this.refreshableView.setEmptyView(newEmptyView);
		}
	}

	public final void setOnLastItemVisibleListener(OnLastItemVisibleListener listener) {
		onLastItemVisibleListener = listener;
	}

	public final void setOnScrollListener(OnScrollListener listener) {
		onScrollListener = listener;
	}

	protected void addRefreshableView(Context context, ListView refreshableView) {
		refreshableViewHolder = new FrameLayout(context);
		refreshableViewHolder.addView(refreshableView, ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		addView(refreshableViewHolder, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.0f));
	};

	protected boolean isReadyForPullDown() {
		return isFirstItemVisible();
	}

	protected boolean isReadyForPullUp() {
		return isLastItemVisible();
	}

	private boolean isFirstItemVisible() {
		if (this.refreshableView.getCount() == 0) {
			return true;
		} else if (refreshableView.getFirstVisiblePosition() == 0) {
			
			final View firstVisibleChild = refreshableView.getChildAt(0);
			
			if (firstVisibleChild != null) {
				return firstVisibleChild.getTop() >= refreshableView.getTop();
			}
		}
		
		return false;
	}

	private boolean isLastItemVisible() {
		final int count = this.refreshableView.getCount();
		final int lastVisiblePosition = refreshableView.getLastVisiblePosition();

		if (count == 0 || count < OMApplication.getInstance().getVal(CommonConsts.QUERY_PAGE_SIZE, 30)) {
			return false;
		} else if (lastVisiblePosition == count - 1) {

			final int childIndex = lastVisiblePosition - refreshableView.getFirstVisiblePosition();
			final View lastVisibleChild = refreshableView.getChildAt(childIndex);

			if (lastVisibleChild != null) {
				return lastVisibleChild.getBottom() <= refreshableView.getBottom();
			}
		}
		return false;
	}

}
