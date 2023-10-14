package com.hgm.util


/**
 * Api请求响应信息分类
 */
object ApiMessage {
    //注册
    const val FIELDS_BLANK = "您的注册信息存在空白呢"
    const val REGISTER_SUCCESSFUL = "恭喜您，注册成功啦"
    const val EMAIL_ALREADY_EXIST = "哎呀，邮箱被某个家伙使用了"
    //登录
    const val LOGIN_SUCCESSFUL = "Welcome 登录成功哟"
    const val LOGIN_FAILED = "很遗憾，这是个无效的凭证"
    //关注
    const val USER_NOT_FOUND = "用户不存在"
    const val FOLLOWING_SUCCESSFUL = "关注成功啦"
    const val UNFOLLOWING_SUCCESSFUL = "取关成功"
    //帖子
    const val CREATE_POST_SUCCESSFUL="您的帖子发布成功"
}