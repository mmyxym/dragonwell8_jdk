/*
 * Copyright (c) 2020 Alibaba Group Holding Limited. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Alibaba designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

/*
 * @test
 * @summary Test AQS: CountDownLatch is implement by AQS
 * @requires os.family == "linux"
 * @run main/othervm -XX:+UnlockExperimentalVMOptions -XX:+EnableCoroutine -Dcom.alibaba.wisp.transparentWispSwitch=true AQSTest
 */


import com.alibaba.wisp.engine.WispEngine;
import sun.misc.SharedSecrets;

import java.util.concurrent.CountDownLatch;

public class AQSTest {
    static CountDownLatch cd = new CountDownLatch(1);
    static CountDownLatch cd2 = new CountDownLatch(1);
    public static void main(String[] args) {
        WispEngine.dispatch(() -> {
            long start = System.currentTimeMillis();
            try {
                cd.await();
                assertInterval(start, 100, 5);
            } catch (InterruptedException e) {
                throw new Error();
            }
        });
        WispEngine.dispatch(() -> {
            long start = System.currentTimeMillis();
            try {
                cd2.await();
                assertInterval(start, 200, 5);
            } catch (InterruptedException e) {
                throw new Error();
            }
        });


        SharedSecrets.getWispEngineAccess().sleep(100);
        cd.countDown();
        SharedSecrets.getWispEngineAccess().sleep(100);
        cd2.countDown();
        SharedSecrets.getWispEngineAccess().sleep(5);
    }

    public static void assertInterval(long start, int diff, int bias) {
        if (Math.abs(System.currentTimeMillis() - start - diff) > bias)
            throw new Error("not wakeup expected");
    }
}
