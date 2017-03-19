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
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mygarden.R;
import com.example.android.mygarden.utils.PlantUtils;

public class PlantTypesAdapter extends RecyclerView.Adapter<PlantTypesAdapter.PlantViewHolder> {

    Context mContext;
    TypedArray mPlantTypes;

    /**
     * Constructor using the context and the db cursor
     *
     * @param context the calling context/activity
     */
    public PlantTypesAdapter(Context context) {
        mContext = context;
        Resources res = mContext.getResources();
        mPlantTypes = res.obtainTypedArray(R.array.plant_types);
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
        View view = inflater.inflate(R.layout.plant_types_list_item, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlantViewHolder holder, int position) {
        int imgRes = PlantUtils.getPlantImgRes(
                mContext, position,
                PlantUtils.PlantStatus.ALIVE,
                PlantUtils.PlantSize.FULLY_GROWN);
        holder.plantImageView.setImageResource(imgRes);
        holder.plantTypeText.setText(PlantUtils.getPlantTypeName(mContext, position));
        holder.plantImageView.setTag(position);
    }

    /**
     * Returns the number of items in the cursor
     *
     * @return Number of items in the cursor, or 0 if null
     */
    @Override
    public int getItemCount() {
        if (mPlantTypes == null) return 0;
        return mPlantTypes.length();
    }

    /**
     * PlantViewHolder class for the recycler view item
     */
    class PlantViewHolder extends RecyclerView.ViewHolder {

        ImageView plantImageView;
        TextView plantTypeText;

        public PlantViewHolder(View itemView) {
            super(itemView);
            plantImageView = (ImageView) itemView.findViewById(R.id.plant_type_image);
            plantTypeText = (TextView) itemView.findViewById(R.id.plant_type_text);
        }

    }
}
