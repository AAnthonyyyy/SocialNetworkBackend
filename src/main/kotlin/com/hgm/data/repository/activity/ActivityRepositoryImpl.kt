package com.hgm.data.repository.activity

import com.hgm.data.model.Activity
import com.hgm.data.model.User
import com.hgm.data.responses.ActivityResponse
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class ActivityRepositoryImpl(
    db: CoroutineDatabase
) : ActivityRepository {

    private val activities = db.getCollection<Activity>()
    private val users = db.getCollection<User>()

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
    ): List<ActivityResponse> {
        //获取关于我的所有动态
        val activities = activities.find(Activity::toUserId eq userId)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Activity::timestamp)
            .toList()

        //查询所有发起者的用户名
        val usersId = activities.map { it.byUserId }
        val users = users.find(User::id `in` usersId).toList()

        return activities.mapIndexed { index, activity ->
            ActivityResponse(
                id = activity.id,
                type = activity.type,
                userId = activity.byUserId,
                parentId = activity.parentId,
                timestamp = activity.timestamp,
                username = users[index].username
            )
        }
    }
}