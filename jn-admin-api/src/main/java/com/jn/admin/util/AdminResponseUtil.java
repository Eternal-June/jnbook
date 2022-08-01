package com.jn.admin.util;

import com.jn.core.util.ResponseUtil;

/**
 * 管理后台接口枚举信息的响应
 */
public class AdminResponseUtil extends ResponseUtil {

    /**
     * 按枚举返回错误响应结果
     */
    public static Object fail(AdminResponseCode responseCode) {
        return fail(responseCode.code(), responseCode.desc());
    }
}
