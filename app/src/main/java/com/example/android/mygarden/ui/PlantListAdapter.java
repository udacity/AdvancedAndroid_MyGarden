package com.example.android.mygarden.ui;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mygarden.R;
import com.example.android.mygarden.provider.PlantContract.PlantEntry;
import com.example.android.mygarden.utils.PlantUtils;

public class PlantListAdapter extends RecyclerView.Adapter<PlantListAdapter.PlantViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    /**
     * Constructor using the context and the db cursor
     *
     * @param context the calling context/activity
     */
    public PlantListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item
     *
     * @param parent   The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new PlantViewHolder that holds a View with the plant_list_item layout
     */
    @Override
    public PlantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.plant_list_item, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlantViewHolder holder, int position) {

        mCursor.moveToPosition(position);
        int idIndex = mCursor.getColumnIndex(PlantEntry._ID);
        int createTimeIndex = mCursor.getColumnIndex(PlantEntry.COLUMN_CREATION_TIME);
        int waterTimeIndex = mCursor.getColumnIndex(PlantEntry.COLUMN_LAST_WATERED_TIME);
        int plantTypeIndex = mCursor.getColumnIndex(PlantEntry.COLUMN_PLANT_TYPE);

        long plantId = mCursor.getLong(idIndex);
        int plantType = mCursor.getInt(plantTypeIndex);
        long createdAt = mCursor.getLong(createTimeIndex);
        long wateredAt = mCursor.getLong(waterTimeIndex);
        long timeNow = System.currentTimeMillis();

        int imgRes = PlantUtils.getPlantImageRes(mContext, timeNow - createdAt, timeNow - wateredAt, plantType);

        holder.plantImageView.setImageResource(imgRes);
        holder.plantNameView.setText(String.valueOf(plantId));
        holder.plantImageView.setTag(plantId);
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (mCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    /**
     * Returns the number of items in the cursor
     *
     * @return Number of items in the cursor, or 0 if null
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    /**
     * PlantViewHolder class for the recycler view item
     */
    class PlantViewHolder extends RecyclerView.ViewHolder {

        ImageView plantImageView;
        TextView plantNameView;

        public PlantViewHolder(View itemView) {
            super(itemView);
            plantImageView = (ImageView) itemView.findViewById(R.id.plant_list_item_image);
            plantNameView = (TextView) itemView.findViewById(R.id.plant_list_item_name);
        }

    }
}
