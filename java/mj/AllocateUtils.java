package mj;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/3.
 */

public class AllocateUtils {
    /** 获取分配东西的分隔符*/
    public static final String ALLOCATE_THING_SEPARATOR = "&";


    /**
     * 分配规则：thingCount个东西分配给personCount个人，可以有人一个也没分配到
     * 返回上面规则定义的所有分配组合
     * @param personCount  人数
     * @param thingCount   待分配数
     * @return 如果personCount==0 || thingCount ==0 返回空
     */
    public static ArrayList<String> allocateThing(int personCount, int thingCount) {
        ArrayList<String> strings = new ArrayList<>();
        if (personCount == 1) {
            strings.add(thingCount + "");
            return strings;
        }
        if (personCount == 0) {
            return strings;
        }
        for (int i = 0; i <= thingCount; i++) {
            int nextPersonCount = personCount - 1;
            int nextThingCount = thingCount - i;

            if (nextPersonCount <= 0 || nextThingCount < 0) {
                continue;
            }

            ArrayList<String> nextStrings = allocateThing(personCount - 1, thingCount - i);
            for (int j = 0; j < nextStrings.size(); j++) {
                strings.add(nextStrings.get(j) + ALLOCATE_THING_SEPARATOR + i);
            }
        }
        return strings;
    }

    /**
     * 分配规则：thingCount个东西分配给personCount个人，可以有人一个也没分配到
     * 返回上面规则定义的所有分配组合
     * @param personCount  人数
     * @param thingCount   待分配数
     * @return 如果personCount==0 || thingCount ==0 返回空
     */
    public static ArrayList<String> allocateThing(int personCount, int thingCount, int maxOwnedCount) {
        ArrayList<String> strings = new ArrayList<>();
        if (personCount == 0) {
            return strings;
        }
        if (personCount == 1) {
            strings.add(thingCount + "");
            return strings;
        }

        for (int i = 0; i <= thingCount; i++) {
            int nextPersonCount = personCount - 1;
            int nextThingCount = thingCount - i;

            if (nextPersonCount <= 0 || nextThingCount < 0 || i > maxOwnedCount) {
                continue;
            }

            ArrayList<String> nextStrings = allocateThing(personCount - 1, thingCount - i);
            for (int j = 0; j < nextStrings.size(); j++) {
                strings.add(nextStrings.get(j) + ALLOCATE_THING_SEPARATOR + i);
            }
        }
        return strings;
    }
}
