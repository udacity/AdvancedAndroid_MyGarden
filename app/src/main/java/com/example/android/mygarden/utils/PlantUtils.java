package com.example.android.mygarden.utils;

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

import com.example.android.mygarden.R;

public class PlantUtils {

    private static final long MINUTE_MILLISECONDS = 1000 * 60;
    private static final long HOUR_MILLISECONDS = MINUTE_MILLISECONDS * 60;
    private static final long DAY_MILLISECONDS = HOUR_MILLISECONDS * 24;

    public static final long MIN_AGE_BETWEEN_WATER = HOUR_MILLISECONDS * 2; // can water every 2 hours
    static final long DANGER_AGE_WITHOUT_WATER = HOUR_MILLISECONDS * 6; // in danger after 6 hours
    public static final long MAX_AGE_WITHOUT_WATER = HOUR_MILLISECONDS * 12; // plants die after 12 hours
    static final long TINY_AGE = DAY_MILLISECONDS * 0; // plants start tiny
    static final long JUVENILE_AGE = DAY_MILLISECONDS * 1; // 1 day old
    static final long FULLY_GROWN_AGE = DAY_MILLISECONDS * 2; // 2 days old


    public enum PlantStatus {ALIVE, DYING, DEAD}

    ;

    public enum PlantSize {TINY, JUVENILE, FULLY_GROWN}

    ;

    /**
     * Returns the corresponding image resource of the plant given the plant's age and
     * time since it was last watered
     *
     * @param plantAge Time (in milliseconds) the plant has been alive
     * @param waterAge Time (in milliseconds) since it was last watered
     * @return Image Resource to the correct plant image
     */
    public static int getPlantImageRes(Context context, long plantAge, long waterAge, int type) {
        //check if plant is dead first
        PlantStatus status = PlantStatus.ALIVE;
        if (waterAge > MAX_AGE_WITHOUT_WATER) status = PlantStatus.DEAD;
        else if (waterAge > DANGER_AGE_WITHOUT_WATER) status = PlantStatus.DYING;

        //Update image if old enough
        if (plantAge > FULLY_GROWN_AGE) {
            return getPlantImgRes(context, type, status, PlantSize.FULLY_GROWN);
        } else if (plantAge > JUVENILE_AGE) {
            return getPlantImgRes(context, type, status, PlantSize.JUVENILE);
        } else if (plantAge > TINY_AGE) {
            return getPlantImgRes(context, type, status, PlantSize.TINY);
        } else {
            return R.drawable.empty_pot;
        }
    }

    /**
     * Returns the corresponding image resource of the plant given the plant's type, status and
     * size (age category)
     *
     * @param context The context
     * @param type    The plant type (starts from 0 and corresponds to the index to the item in arrays.xml)
     * @param status  The PlantStatus
     * @param size    The PlantSize
     * @return Image Resource to the correct plant image
     */
    public static int getPlantImgRes(Context context, int type, PlantStatus status, PlantSize size) {
        Resources res = context.getResources();
        TypedArray plantTypes = res.obtainTypedArray(R.array.plant_types);
        String resName = plantTypes.getString(type);
        if (status == PlantStatus.DYING) resName += "_danger";
        else if (status == PlantStatus.DEAD) resName += "_dead";
        if (size == PlantSize.TINY) resName += "_1";
        else if (size == PlantSize.JUVENILE) resName += "_2";
        else if (size == PlantSize.FULLY_GROWN) resName += "_3";
        return context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
    }

    /**
     * Returns the plant type display name based on the type index from the string resources
     *
     * @param context The context
     * @param type    The plant type (starts from 0 and corresponds to the index to the item in arrays.xml)
     * @return The plant type display name
     */
    public static String getPlantTypeName(Context context, int type) {
        Resources res = context.getResources();
        TypedArray plantTypes = res.obtainTypedArray(R.array.plant_types);
        String resName = plantTypes.getString(type);
        int resId = context.getResources().getIdentifier(resName, "string", context.getPackageName());
        try {
            return context.getResources().getString(resId);
        } catch (Resources.NotFoundException ex) {
            return context.getResources().getString(R.string.unknown_type);
        }
    }

    /**
     * Converts the age in milli seconds to a displayable format (days, hours or minutes)
     *
     * @param milliSeconds The age in milli seconds
     * @return The value of either days, hours or minutes
     */
    public static int getDisplayAgeInt(long milliSeconds) {
        int days = (int) (milliSeconds / DAY_MILLISECONDS);
        if (days >= 1) return days;
        int hours = (int) (milliSeconds / HOUR_MILLISECONDS);
        if (hours >= 1) return hours;
        return (int) (milliSeconds / MINUTE_MILLISECONDS);
    }

    /**
     * Converts the age in milli seconds to a displayable format (days, hours or minutes)
     *
     * @param context      The context
     * @param milliSeconds The age in milli seconds
     * @return The unit of either days, hours or minutes
     */
    public static String getDisplayAgeUnit(Context context, long milliSeconds) {
        int days = (int) (milliSeconds / DAY_MILLISECONDS);
        if (days >= 1) return context.getString(R.string.days);
        int hours = (int) (milliSeconds / HOUR_MILLISECONDS);
        if (hours >= 1) return context.getString(R.string.hours);
        return context.getString(R.string.minutes);
    }
}
