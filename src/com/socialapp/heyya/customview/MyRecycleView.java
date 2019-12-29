package com.socialapp.heyya.customview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;

public class MyRecycleView extends RecyclerView{

	private RecyclerContextMenuInfo recyclerContextMenuInfo;
	public MyRecycleView(Context arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	public MyRecycleView(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	public MyRecycleView(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	  protected ContextMenuInfo getContextMenuInfo() {
	    return recyclerContextMenuInfo;
	  }

	  @Override
	  public boolean showContextMenuForChild(View originalView) {
	    final int longPressPosition = getChildAdapterPosition(originalView);
	    if (longPressPosition >= 0) {
	        final long longPressId = getAdapter().getItemId(longPressPosition);
	        recyclerContextMenuInfo = new RecyclerContextMenuInfo(longPressPosition, longPressId);
	        return super.showContextMenuForChild(originalView);
	    }
	    return false;
	  }


	public static class RecyclerContextMenuInfo implements ContextMenuInfo{
		public RecyclerContextMenuInfo(int position, long id) {
	        this.position = position;
	        this.id = id;
	    }

	    final public int position;
	    final public long id;
	}
}
