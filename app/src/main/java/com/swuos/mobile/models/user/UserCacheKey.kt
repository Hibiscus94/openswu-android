package com.swuos.mobile.models.user

/**
 * 缓存数据key
 * Created by wangyu on 2017/12/7.
 */

enum class UserCacheKey(val key: String) {
    /**
     * 上次登录账号信息
     */
    LAST_ACCOUNT("last_account"),
    /**
     * 当前登录用户
     */
    CURRENT_USER("current_user");
}
