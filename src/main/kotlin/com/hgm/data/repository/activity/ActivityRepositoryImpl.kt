package com.hgm.data.repository.activity

import com.hgm.data.models.Activity
import com.hgm.data.models.Following
import com.hgm.data.models.Post
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class ActivityRepositoryImpl(
    db: CoroutineDatabase
) : ActivityRepository {

    private val activities = db.getCollection<Activity>()

    override suspend fun createActivity(activity: Activity) {
        activities.insertOne(activity)
    }

    override suspend fun deleteActivity(activityId: String): Boolean {
        return activities.deleteOneById(activityId).wasAcknowledged()
    }

    override suspend fun getActivitiesForUser(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<Activity> {
        //获取关于我的所有活动
        return activities.find(Activity::toUserId eq userId)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Activity::timestamp)
            .toList()
    }
}