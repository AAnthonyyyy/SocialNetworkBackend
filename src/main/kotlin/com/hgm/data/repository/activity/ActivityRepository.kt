package com.hgm.data.repository.activity

import com.hgm.data.model.Activity
import com.hgm.data.responses.ActivityResponse
import com.hgm.utils.Constants

interface ActivityRepository {

    suspend fun createActivity(activity: Activity)

    suspend fun deleteActivity(activityId: String):Boolean

    suspend fun getActivitiesForUser(
        userId: String,
        page: Int = Constants.DEFAULT_ACTIVITY_PAGE,
        pageSize: Int = Constants.DEFAULT_ACTIVITY_PAGE_SIZE
    ):List<ActivityResponse>
}