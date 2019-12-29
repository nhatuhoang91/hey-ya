package com.socialapp.heyya.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public abstract class RecycleViewAdapter <VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{

	private Context mContext;
	private Cursor mCursor;
	private boolean dataValid;
	private int mRowIdColumn;
	public RecycleViewAdapter(Context context,Cursor cursor){
		this.mContext = context;
		this.mCursor = cursor;
		dataValid = cursor!=null;
		mRowIdColumn = dataValid ? mCursor.getColumnIndex("_id"):-1;
	}
	
	public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);
	
	public Cursor getCursor(){
		return mCursor;
	}
	
	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		if(dataValid && mCursor != null){
			return mCursor.getCount();
		}
		return 0;
	}

	 @Override
	    public long getItemId(int position) {
	        if (dataValid && mCursor != null && mCursor.moveToPosition(position)) {
	            return mCursor.getLong(mRowIdColumn);
	        }
	        return 0;
	    }
	 
	@Override
	public void onBindViewHolder(VH viewHolder, int position) {
		// TODO Auto-generated method stub
		if (!dataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(viewHolder, mCursor);
    }
	

	@Override
	public VH onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }
	
	/**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
     
        mCursor = newCursor;
        if (mCursor != null) {
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            dataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            dataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }
    
}
