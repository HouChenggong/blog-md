package org.xiyou.search;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiyouyan
 * @date 2020-08-06 16:10
 * @description
 */
public class RedPacketUtil {
    //微信红包的最大值和最小值
    private static final float MIN_MONEY = 0.01f;
    private static final float MAX_MONEY = 200.00f;
    //最大红包金额系数
    private static final float TIMES = 2.1f;

    //判断当前金额和数量是否正确
    public boolean isRight(float money, int count) {
        //计算当前平均值
        float ave = (float) money / count;
        if (ave < MIN_MONEY) {
            return false;
        } else if (ave > MAX_MONEY) {
            return false;
        }
        return true;
    }

    //生成每个具体红包的金额
    public float redPacket(float money, float min, float maxs, int count) {
        //判断当前人数
        if (count == 1) {
            //确保红包不小于0.01元
            money = money > MIN_MONEY ? money : MIN_MONEY;
            return (float) (Math.round(money * 100)) / 100;
        }
        float max = maxs > money ? money : maxs;
        //生成单个红包数量,且保证红包的精度
        float one = (float) (Math.random() * (max - min) + min);
        one = (float) (Math.round(one * 100)) / 100;

        float moneyRest = (money - one);

        //判断当前红包数量是否合理
        if (isRight(moneyRest, count - 1)) {
            return one;
        } else {
            //重新分配红包
            float ave = (float) moneyRest / (count - 1);
            if (ave < MIN_MONEY) {
                return redPacket(money, min, one, count);
            } else if (ave > MAX_MONEY) {
                return redPacket(money, one, max, count);
            }
        }
        return one;
    }

    //拆分红包，生成具体的红包数
    public List<Float> splitRedPacket(float money, int count) {
        //判断当前金额和数量是否正确
        if (!isRight(money, count)) {
            return null;
        }
        //记录每个红包的数量
        List<Float> rpList = new ArrayList<Float>();
        //单个红包的最大金额
        float max = (float) (money * TIMES) / count;
        max = max > MAX_MONEY ? MAX_MONEY : max;

        float one = 0;
        //开始记录每个红包的数量
        for (int i = 0; i < count; i++) {
            one = redPacket(money, MIN_MONEY, max, count - i);
            rpList.add(one);
            money = money - one;
        }
        return rpList;
    }

        public  static int countBinarySubstrings(String s) {
            List<Integer> counts = new ArrayList<Integer>();
            int ptr = 0, n = s.length();
            while (ptr < n) {
                char c = s.charAt(ptr);
                int count = 0;
                while (ptr < n && s.charAt(ptr) == c) {
                    ++ptr;
                    ++count;
                }
                counts.add(count);
            }
            int ans = 0;
            for (int i = 1; i < counts.size(); ++i) {
                ans += Math.min(counts.get(i), counts.get(i - 1));
            }
            return ans;
        }


    public static void main(String[] args) {
        System.out.println(countBinarySubstrings("00111011"));
    }
}


