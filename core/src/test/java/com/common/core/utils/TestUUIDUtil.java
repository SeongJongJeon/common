package com.common.core.utils;

import org.junit.Test;

import java.util.UUID;

/**
 * Created by alex.
 * Date: 2018-12-06
 */
public class TestUUIDUtil {
    @Test
    public void generateUUIDV5() {
        UUID uuid = UUIDUtil.generateUUIDV5(UUIDUtil.NAMESPACE_DNS, "com.common");
        System.out.println(uuid.version());
        System.out.println(uuid.toString());
    }

    @Test
    public void generateUUIDV4() {
        UUID uuid = UUIDUtil.generateUUIDV4();
        System.out.println(uuid.version());
        System.out.println(uuid.toString());
    }
}
