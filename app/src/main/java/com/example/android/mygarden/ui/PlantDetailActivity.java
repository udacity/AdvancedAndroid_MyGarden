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

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mygarden.PlantWateringService;
import com.example.android.mygarden.R;
import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.utils.PlantUtils;

import static com.example.android.mygarden.provider.PlantContract.BASE_CONTENT_URI;
import static com.example.android.mygarden.provider.PlantContract.PATH_PLANTS;

public class PlantDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SINGLE_LOADER_ID = 200;
    public static final String EXTRA_PLANT_ID = "com.example.android.mygarden.extra.PLANT_ID";
    long mPlantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_detail);
        mPlantId = getIntent().getLongExtra(EXTRA_PLANT_ID, PlantContract.INVALID_PLANT_ID);
        // This activity displays single plant information that is loaded using a cursor loader
        getSupportLoaderManager().initLoader(SINGLE_LOADER_ID, null, this);
    }

    public void onBackButtonClick(View view) {
        finish();
    }

    public void onWaterButtonClick(View view) {
        //check if already dead then can't water
        Uri SINGLE_PLANT_URI = ContentUris.withAppendedId(
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build(), mPlantId);
        Cursor cursor = getContentResolver().query(SINGLE_PLANT_URI, null, null, null, null);
        if (cursor == null || cursor.getCount() < 1) return; //can't find this plant!
        cursor.moveToFirst();
        long lastWatered = cursor.getLong(cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME));
        long timeNow = System.currentTimeMillis();
        if ((timeNow - lastWatered) > PlantUtils.MAX_AGE_WITHOUT_WATER)
            return; // plant already dead

        ContentValues contentValues = new ContentValues();
        // Update the watered timestamp
        contentValues.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        getContentResolver().update(SINGLE_PLANT_URI, contentValues, null, null);
        cursor.close();
        PlantWateringService.startActionUpdatePlantWidgets(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri SINGLE_PLANT_URI = ContentUris.withAppendedId(
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build(), mPlantId);
        return new CursorLoader(this, SINGLE_PLANT_URI, null,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) return;
        cursor.moveToFirst();
        int createTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
        int waterTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
        int planTypeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);

        int plantType = cursor.getInt(planTypeIndex);
        long createdAt = cursor.getLong(createTimeIndex);
        long wateredAt = cursor.getLong(waterTimeIndex);
        long timeNow = System.currentTimeMillis();

        int plantImgRes = PlantUtils.getPlantImageRes(this, timeNow - createdAt, timeNow - wateredAt, plantType);

        ((ImageView) findViewById(R.id.plant_detail_image)).setImageResource(plantImgRes);
        ((TextView) findViewById(R.id.plant_detail_name)).setText(String.valueOf(mPlantId));
        ((TextView) findViewById(R.id.plant_age_number)).setText(
                String.valueOf(PlantUtils.getDisplayAgeInt(timeNow - createdAt))
        );
        ((TextView) findViewById(R.id.plant_age_unit)).setText(
                PlantUtils.getDisplayAgeUnit(this, timeNow - createdAt)
        );
        ((TextView) findViewById(R.id.last_watered_number)).setText(
                String.valueOf(PlantUtils.getDisplayAgeInt(timeNow - wateredAt))
        );
        ((TextView) findViewById(R.id.last_watered_unit)).setText(
                PlantUtils.getDisplayAgeUnit(this, timeNow - wateredAt)
        );
        int waterPercent = 100 - ((int) (100 * (timeNow - wateredAt) / PlantUtils.MAX_AGE_WITHOUT_WATER));
        ((WaterLevelView) findViewById(R.id.water_level)).setValue(waterPercent);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onCutButtonClick(View view) {
        Uri SINGLE_PLANT_URI = ContentUris.withAppendedId(
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build(), mPlantId);
        getContentResolver().delete(SINGLE_PLANT_URI, null, null);
        PlantWateringService.startActionUpdatePlantWidgets(this);
        finish();
    }
}
