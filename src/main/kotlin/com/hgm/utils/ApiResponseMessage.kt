package com.hgm.utils


/**
 * Api请求响应信息分类
 */
object ApiResponseMessage {
    //注册
    const val FIELDS_BLANK = "您的内容存在空白呢"
    const val REGISTER_SUCCESSFUL = "恭喜您 注册成功啦"
    const val EMAIL_ALREADY_EXIST = "哎呀 邮箱被某个家伙使用了"

    //登录
    const val LOGIN_SUCCESSFUL = "Welcome 登录成功哟"
    const val LOGIN_FAILED = "很遗憾 这是个无效的凭证"

    //关注
    const val USER_NOT_FOUND = "用户不存在"
    const val FOLLOWING_SUCCESSFUL = "关注成功啦"
    const val UNFOLLOWING_SUCCESSFUL = "取关成功"

    //帖子
    const val CREATE_POST_SUCCESSFUL = "您的帖子发布成功"
    const val DELETE_POST_SUCCESSFUL = "帖子删除成功"
    const val CREATE_POST_AUTH_ERROR = "您的身份存在疑问无法进行发帖操作"
    const val LIKE_POST_SUCCESSFUL = "您点赞了这篇帖子"

    //评论
    const val COMMENT_LENGTH_TOO_LONG="内容字数不能超过 ${Constants.MAX_COMMENT_LENGTH} 哟"
    const val ADD_COMMENT_SUCCESSFUL="发表评论成功"
    const val DELETE_COMMENT_SUCCESSFUL="删除评论成功"

    //个人资料
    const val PROFILE_UPDATE_SUCCESSFUL="个人信息更新成功"

}