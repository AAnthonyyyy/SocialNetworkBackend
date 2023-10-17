package com.hgm.routes

import com.hgm.service.ActivityService
import com.hgm.utils.Constants
import com.hgm.utils.QueryParams
import com.hgm.utils.userId
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*



fun Route.getActivities(
    service: ActivityService
) {
    authenticate {
        get("/api/activity/get") {
            val page =
                call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: Constants.DEFAULT_ACTIVITY_PAGE
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_ACTIVITY_PAGE_SIZE

            val activities = service.getActivitiesForUser(call.userId, page, pageSize)
            call.respond(
                HttpStatusCode.OK,
                activities
            )
        }
    }
}