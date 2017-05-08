import java.util.ArrayList;

import mj.MJCard;

/**
 * Created by Administrator on 2017/5/4.
 */

public class PaiGroup {



    static int sMapIntUseMaxBitLen = 0;

    public CardType mCardType;
    public int mRawMappedInt;
    //public int mMappedInt;
    public int[] mNums;
    public String mPaiGroupStr;


    public String mPaiGroupHumanName;

    public boolean isPackable;
    public boolean isPengPengPackable;
    public int mMinPackableKingCount;

    public PaiGroup(CardType cardType, int rawMappedInt) {
        int[] nums = getCardNums(cardType, rawMappedInt);
        init(cardType, nums);

    }
    public PaiGroup(CardType cardType, String paiGroupStr) {
        int[] nums = getCardNums(cardType, paiGroupStr);
        init(cardType, nums);
    }

    public PaiGroup(CardType cardType, int[] nums) {
        init(cardType, nums);
    }

    void init(CardType cardType, int[] nums){
        mCardType = cardType;
        mNums = new int[nums.length];
        System.arraycopy(nums, 0, mNums, 0, nums.length);

        mPaiGroupStr = getPaiGroupStr(cardType, mNums);
        mRawMappedInt = generateRawMapedInt(cardType, mPaiGroupStr);
        mPaiGroupHumanName = getPaiGroupHumanName(cardType, mNums);
        isPackable = isPackable(mNums, 0);
        if(isPackable){
            isPengPengPackable = isPengPengPackable(mNums);
        }
        if(!isPackable){
            mMinPackableKingCount = getPackableMinKingCount(cardType, mNums);
        }
        //initMappedInt();
    }




//    private void initMappedInt(){
//        int extraInt = 0;
//        if(mCardType == CardType.FENG){
//
//
//            //处理结合成刻子，需要的精数量
//            extraInt |= mMinPackableKingCount;
//
//            //处理原始牌型，结合成刻子的结果
//            extraInt <<= 1;
//            extraInt |= (isPackable ? 1 : 0);
//
//
//            //处理风牌标识位
//            extraInt <<= 1;
//            extraInt |= 1;
//        }else{
//            if(mCardType == CardType.ARROW){
//                extraInt |= 1;
//                extraInt <<= 1;
//            }
//            //处理结合成刻子，需要的精数量
//            extraInt |= mMinPackableKingCount;
//
//
//            //处理原始牌型，结合成刻子的结果
//            extraInt <<= 1;
//            extraInt |= (isPackable ? 1 : 0);
//
//            extraInt <<= 4;
//
//            //处理风牌标识位
//            extraInt <<= 1;
//
//        }
//
//        mMappedInt |= extraInt;
//        mMappedInt <<= 20;
//        mMappedInt |= mRawMappedInt;
//    }






    private int[] getCardNums(CardType cardType, String paiWeaveStr){
        int arrLen = CardType.getCardNum(cardType);
        String[] numStrings = paiWeaveStr.split(AllocUtils.ALLOCATE_THING_SEPARATOR);
        int[] nums = new int[arrLen];
        for (int i = 0; i < arrLen; i++) {
            nums[i] = Integer.valueOf(numStrings[i]);
        }
        return nums;
    }


    public int[] getCardNums(CardType cardType, int mappedInt){
        int arrLen = CardType.getCardNum(cardType);
        int[] nums = new int[arrLen];
        for(int i = 0; i < arrLen; ++i){
            int count = 0;

            while((mappedInt ^ (mappedInt - 1)) == 1){
                count++;
                mappedInt >>= 1;
            }
            nums[i] = count;
            mappedInt >>= 1;
        }
        return nums;
    }


    /** 同一个牌在当前12 字牌的数量的二进制表示*/
    public static final int[] PAI_GROUP_MAP_INT_COUNT_BITS = {
            0x0,    /*  0*/
            0x1,    /*  1*/
            0x3,    /*  11*/
            0x7,    /*  111*/
            0xF,    /*  1111*/
    };
    /**
     * 获取牌型映射的int值
     * @param cardType
     * @param paiWeaveStr
     * @return
     */
    public  int generateRawMapedInt(CardType cardType, String paiWeaveStr) {
        int arrLen = CardType.getCardNum(cardType);

        int[] nums = getCardNums(cardType, paiWeaveStr);


        int returnInt = 0;
        for (int i = (arrLen - 1); i >= 0; --i) {

            int leftShiftCount = nums[i];

            if(leftShiftCount != 0){
                returnInt <<= leftShiftCount;
                returnInt |= PAI_GROUP_MAP_INT_COUNT_BITS[leftShiftCount];
            }

            if (i != 0)
                returnInt <<= 1;

        }

        return returnInt;
    }

    /**
     * 获取牌型映射的int值
     * @param cardType 花色
     * @param cardNums 牌数量
     * @return
     */
    public  int generateRawMapedInt(CardType cardType, int[] cardNums) {
        int arrLen = CardType.getCardNum(cardType);



        int returnInt = 0;
        for (int i = (arrLen - 1); i >= 0; --i) {

            int leftShiftCount = cardNums[i];

            if(leftShiftCount != 0){
                returnInt <<= leftShiftCount;
                returnInt |= PAI_GROUP_MAP_INT_COUNT_BITS[leftShiftCount];
            }

            if (i != 0)
                returnInt <<= 1;

        }

        return returnInt;
    }


    private String getPaiGroupStr(CardType cardType, int[] nums){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < nums.length; ++i){
            sb.append(nums[i]);
            if(i != (nums.length - 1)){
                sb.append(AllocUtils.ALLOCATE_THING_SEPARATOR);
            }
        }
        return sb.toString();
    }
    /**
     * 获取可读的牌面
     * @param cardType 牌的花色
     * @param cardCounts 各牌的数量
     * @return
     */
    private String getPaiGroupHumanName(CardType cardType, int[] cardCounts){
        int startValue = 0;
        int arrLen = CardType.getCardNum(cardType);
        if(cardType == CardType.WAN){
            startValue = 0x01;
        }else if(cardType == CardType.TIAO){
            startValue = 0x11;
        }else if(cardType == CardType.TONG){
            startValue = 0x21;
        }else if(cardType == CardType.FENG){
            startValue = 0x31;
        }else if(cardType == CardType.ARROW){
            startValue = 0x35;
        }

        MJCard mjCard = new MJCard();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < arrLen; i++) {
            if(cardCounts[i] > 0){
                for(int j = 0; j < cardCounts[i]; j++) {
                    mjCard.setCardValue((byte) (startValue + i));
                    sb.append(mjCard.toString() + ";");
                }
            }
        }

        return sb.toString();
    }


    public  String to32BinaryString(){
        String binaryString = Integer.toBinaryString(mRawMappedInt);
        StringBuilder sb = new StringBuilder();
        for (int i = 32 - binaryString.length(); i > 0; i--) {
            sb.append("0");
        }
        sb.append(binaryString);
        return sb.toString();
    }


    @Override
    public String toString() {
        return ("".equals(mPaiGroupHumanName) ? "empty" : mPaiGroupHumanName);
    }

    /**
     * 结合刻子分析
     * @param cardNums
     * @param beginIndex
     * @return 是否能结合成刻子
     */
    public boolean isPackable(int[] cardNums, int beginIndex) {
        if (beginIndex >= cardNums.length) {
            return true;
        }

        int count = 0;
        int gangCount = 0;
        for (int i = 0; i < cardNums.length; ++i) {
            count += cardNums[i];
            if(cardNums[i] == 4){
                ++gangCount;
            }
        }

        if (count == 0) {
            return true;
        }

        if(gangCount == 3){//类似 4444 8888 9999 牌型
            return true;
        }

        if (count % 3 != 0) {//牌数量不能被3整除
            return false;
        }

        int curCardCount = cardNums[beginIndex];//当前位置牌的数量
        if (curCardCount == 0) {//当前位置没有牌
            return isPackable(cardNums, ++beginIndex);
        }


        //curCardCount > 0
        boolean pengResult = false;//碰碰类型的刻子的结果
        boolean shunResult = false;//顺子类型的刻子的结果
        boolean bAnalyzeFeng = (cardNums.length == 4);//是否是风牌

        if (curCardCount >= 3) {//碰碰刻子分析
            int[] tempPengCardNums = new int[cardNums.length];
            System.arraycopy(cardNums, 0, tempPengCardNums, 0, cardNums.length);

            tempPengCardNums[beginIndex] -= 3;
            pengResult = isPackable(tempPengCardNums, tempPengCardNums[beginIndex] == 0 ? beginIndex + 1 : beginIndex);
        }


        //顺子刻子分析
        int[] tempCardNums = new int[cardNums.length];
        System.arraycopy(cardNums, 0, tempCardNums, 0, cardNums.length);

        if (beginIndex >= tempCardNums.length - 2) {//curCardCount > 0      万的7、8位，风的2、3位，箭的1、2位依然有牌
            shunResult = false;
        }else{
            if (!bAnalyzeFeng) {//非风牌
                if (tempCardNums[beginIndex + 1] >= curCardCount &&
                        tempCardNums[beginIndex + 2] >= curCardCount) {

                    int[] tempShunCardNums = new int[cardNums.length];
                    System.arraycopy(cardNums, 0, tempShunCardNums, 0, cardNums.length);
                    tempShunCardNums[beginIndex] -= curCardCount;
                    tempShunCardNums[beginIndex + 1] -= curCardCount;
                    tempShunCardNums[beginIndex + 2] -= curCardCount;
                    shunResult = isPackable(tempShunCardNums, beginIndex + 1);
                }

            } else {//风牌
                if (beginIndex == 0) {//东风位置
                    boolean dnxResult = false;//东南西
                    boolean dnbResult = false;//东南北
                    if (tempCardNums[beginIndex + 1] > 0 &&
                            tempCardNums[beginIndex + 2] > 0) {//东南西位置有牌

                        int[] tempShunCardNums = new int[cardNums.length];
                        System.arraycopy(cardNums, 0, tempShunCardNums, 0, cardNums.length);
                        tempShunCardNums[beginIndex]--;
                        tempShunCardNums[beginIndex + 1]--;
                        tempShunCardNums[beginIndex + 2]--;
                        dnxResult = isPackable(tempShunCardNums, beginIndex);
                    }

                    if (tempCardNums[beginIndex + 2] > 0 &&
                            tempCardNums[beginIndex + 3] > 0) {//东南北位置有牌

                        int[] tempShunCardNums = new int[cardNums.length];
                        System.arraycopy(cardNums, 0, tempShunCardNums, 0, cardNums.length);
                        tempShunCardNums[beginIndex]--;
                        tempShunCardNums[beginIndex + 2]--;
                        tempShunCardNums[beginIndex + 3]--;
                        dnbResult = isPackable(tempShunCardNums, beginIndex);
                    }
                    shunResult = (dnxResult || dnbResult);
                } else if (beginIndex == 1) {//南风位置
                    if (tempCardNums[beginIndex + 1] >= curCardCount &&
                            tempCardNums[beginIndex + 2] >= curCardCount) {

                        int[] tempShunCardNums = new int[cardNums.length];
                        System.arraycopy(cardNums, 0, tempShunCardNums, 0, cardNums.length);
                        tempShunCardNums[beginIndex] -= curCardCount;
                        tempShunCardNums[beginIndex + 1] -= curCardCount;
                        tempShunCardNums[beginIndex + 2] -= curCardCount;
                        shunResult = isPackable(tempShunCardNums, ++beginIndex);
                    }
                }
            }
        }

        return pengResult || shunResult;
    }

    public boolean isPengPengPackable(int[] cardNums){
        if (cardNums == null || cardNums.length <= 0) {
            return true;
        }
        for (int i = 0; i < cardNums.length; i++) {
            if(cardNums[i] % 3 != 0){
                return false;
            }
        }

        return true;
    }

    public boolean isLanPackable(int[] cardNums){
        int lastIndex = -1;
        int count = 0;
        for(int i = 0; i < cardNums.length; i++){
            if(cardNums[i] >= 2){
                return false;
            }
            if(lastIndex < 0 && cardNums[i] == 1){
                lastIndex = i;
            }
            else if(lastIndex >= 0 && cardNums[i] == 1){
                if((i - lastIndex) < 3){
                    return false;
                }
                lastIndex = i;
            }
            if(cardNums[i] == 1){
                count++;
            }

            if(count > 3){
                return false;
            }
        }
        return true;

    }

    /**
     * 获取结合成刻子所需要的最少精的个数
     * @param cardType 牌类型
     * @param cardNums 不能服牌的牌型
     * @return
     */
    public int getPackableMinKingCount(CardType cardType, int[] cardNums){
        int n = CardType.getCardNum(cardType);//此类牌有多少类牌，例如万牌，有九种，风牌，有四种，箭牌，有三种

        int cardCount = Utils.getCardCount(cardNums);//牌型的数量

        int kingCount =(3 - cardCount % 3);

        while (kingCount <= 8){
            ArrayList<String> allocStrs = AllocUtils.alloc(n, kingCount, kingCount);//kingCount个精的所有的分配方式

            for (String allocStr : allocStrs) {

                int[] tempCardNums = new int[cardNums.length];
                System.arraycopy(cardNums, 0, tempCardNums, 0, cardNums.length);

                String[] ownedThings = allocStr.split("&");
                for(int i = 0; i < cardNums.length; i++){
                    tempCardNums[i] += Integer.valueOf(ownedThings[i]);
                }
                if(isPackable(tempCardNums, 0)){

                    return kingCount;
                }
            }

            kingCount += 3;
        }

        return kingCount;
    }
}
