import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/3.
 */

public class AllocUtils {
    /** 获取分配东西的分隔符*/
    public static final String ALLOCATE_THING_SEPARATOR = "&";


    /**
     * 分配规则：n个东西分配给m个人，每人最多分到k个东西
     * 返回上面规则定义的所有分配组合
     * @param n  人数
     * @param m   待分配数
     * @return n==0 || m ==0 返回空
     */
    public static ArrayList<String> alloc(int n, int m, int k) {
        ArrayList<String> allocStrs = new ArrayList<>();
        if (n == 0 || (n == 1 && m > k)) {
            return allocStrs;
        }
        if (n == 1) {
            allocStrs.add(m + "");
            return allocStrs;
        }

        for (int i = 0; i <= m; i++) {
            int nextN = n - 1;
            int nextM = m - i;

            if (nextN <= 0 || nextM < 0 || i > k) {
                break;
            }

            ArrayList<String> nextAllocStrs = alloc(nextN, nextM, k);
            for (int j = 0; j < nextAllocStrs.size(); j++) {
                allocStrs.add(nextAllocStrs.get(j) + ALLOCATE_THING_SEPARATOR + i);
            }
        }
        return allocStrs;
    }
}
