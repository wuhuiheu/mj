package mj;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Test {

    /** 同一个牌在当前12 字牌的数量的二进制表示*/
    public static final int[] PAI_GROUP_MAP_INT_COUNT_BITS = {  0x1,    /*  1*/
                                                                0x3,    /*  11*/
                                                                0x7,    /*  111*/
                                                                0xF,    /*  1111*/
                                                                0x1F,   /*  11111*/
                                                                0x3F,   /*  111111*/
                                                                0x7F,   /*  1111111*/
                                                                0xFF,   /*  11111111*/
                                                                0x1FF,  /*  111111111*/
                                                                0x3FF,  /*  1111111111*/
                                                                0x7FF,  /*  11111111111*/
                                                                0xFFF,  /*  111111111111*/

    };


    public static final CardType[] ALL_CARD_TYPE={
            CardType.WAN,
            CardType.FENG,
            CardType.FENG
    };


    /**
     * 分配规则：thingCount个东西分配给personCount个人，可以有人一个也没分配到
     * 返回上面规则定义的所有分配组合
     * @param personCount  人数
     * @param thingCount   待分配数
     * @return 如果personCount==0 || thingCount ==0 返回0
     */
    public static ArrayList<String> getAllocatePaiGroup(int personCount, int thingCount) {

        ArrayList<String> pais = new ArrayList<>();
            ArrayList<String> strings = mj.AllocateUtils.allocateThing(personCount, thingCount);
            for (String string : strings) {
                if (string.indexOf("5") >= 0 || string.indexOf("6") >= 0 || string.indexOf("7") >= 0
                        || string.indexOf("8") >= 0 || string.indexOf("9") >= 0 || string.indexOf("10") >= 0
                        || string.indexOf("11") >= 0 || string.indexOf("12") >= 0) {
                }
                else{
                    pais.add(string);
                }

            }
        return pais;
    }

    /**
     * 获取玩家可能的所有麻将牌
     * 玩家的牌数[0, 12]去掉了头
     * @param cardType 花色
     * @param outputFile 是否输出到文件中
     * @return
     */
    public static ArrayList<String> getAllPaiGroup(CardType cardType, boolean outputFile) {
        int personCount = 0;
        if(cardType == CardType.WAN){
            personCount = 9;
        }else if(cardType == CardType.TIAO){
            personCount = 9;
        }else if(cardType == CardType.TONG){
            personCount = 9;
        }else if(cardType == CardType.FENG){
            personCount = 4;
        }else if(cardType == CardType.ARROW){
            personCount = 3;
        }


        ArrayList<String> pais = new ArrayList<>();
        for (int i = 0; i <= 12; i++) {
            ArrayList<String> strings = AllocateUtils.allocateThing(personCount, i);
            for (String string : strings) {
                if (string.indexOf("5") >= 0 || string.indexOf("6") >= 0 || string.indexOf("7") >= 0
                        || string.indexOf("8") >= 0 || string.indexOf("9") >= 0 || string.indexOf("10") >= 0
                        || string.indexOf("11") >= 0 || string.indexOf("12") >= 0) {
                }
                else{
                    pais.add(string);
                }

            }

        }

        if (outputFile) {
            try {
                FileWriter fileWriter = new FileWriter("getAllPaiGroup.txt");
                for (String string : pais) {
                    fileWriter.write(string + "\n");
                }
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pais;
    }



    public static int[] getPaiNums(String paiWeaveStr){
        String[] numStrings = paiWeaveStr.split("&");
        int[] nums = new int[9];
        for (int i = 0; i < 9; i++) {
            nums[i] = Integer.valueOf(numStrings[i]);
        }
        return nums;
    }

    public static int[] getPaiNums(CardType cardType, int mappInt){
        int arrLen = 0;
        if(cardType == CardType.WAN){
            arrLen = 9;
        }else if(cardType == CardType.TIAO){
            arrLen = 9;
        }else if(cardType == CardType.TONG){
            arrLen = 9;
        }else if(cardType == CardType.FENG){
            arrLen = 4;
        }else if(cardType == CardType.ARROW){
            arrLen = 3;
        }
        int[] nums = new int[arrLen];
        for(int i = (arrLen - 1); i >= 0; i--){
            int count = 0;

            while((mappInt ^ (mappInt - 1)) == 1){
                count++;
                mappInt >>= 1;
            }
            nums[i] = count;
            mappInt >>= 1;
        }
        return nums;
    }

    /**
     * 获取牌型映射的int值
     * @param paiWeaveStr
     * @return
     */
    public static int generateMapedInt(CardType cardType, String paiWeaveStr) {
        int arrLen = 0;
        if(cardType == CardType.WAN){
            arrLen = 9;
        }else if(cardType == CardType.TIAO){
            arrLen = 9;
        }else if(cardType == CardType.TONG){
            arrLen = 9;
        }else if(cardType == CardType.FENG){
            arrLen = 4;
        }else if(cardType == CardType.ARROW){
            arrLen = 3;
        }
        String[] numStrings = paiWeaveStr.split("&");
        int[] nums = new int[arrLen];
        for (int i = 0; i < arrLen; i++) {
            nums[i] = Integer.valueOf(numStrings[i]);
        }


        int useBitCount = 0;
        int returnInt = 0;
        for (int i = 0; i < arrLen; i++) {
            useBitCount += (nums[i] + 1);
            if (nums[i] == 0) {
                if (i != (arrLen - 1))
                    returnInt <<= 1;
            } else {
                returnInt <<= (nums[i]);
                returnInt |= PAI_GROUP_MAP_INT_COUNT_BITS[nums[i] - 1];
                if (i != (arrLen - 1))
                    returnInt <<= 1;
            }

        }

        if(useBitCount > sMapIntUseMaxBitLen){
            sMapIntUseMaxBitLen = useBitCount;
        }
        return returnInt;
    }

    /**
     * 测试所有的牌型映射的int值是否唯一
     * @return
     */
    public static boolean testAllIntUnique(){
        ArrayList<String> paiWeaveStrs = getAllPaiGroup(CardType.ARROW, false);
        Map<Integer, Integer> paiWeaveMapToInt = new HashMap<>();
        for (String paiWeaveStr : paiWeaveStrs) {
            int mapedInt = generateMapedInt(CardType.ARROW,paiWeaveStr);
            if(paiWeaveMapToInt.containsKey(mapedInt)){
                return false;
            }
            paiWeaveMapToInt.put(mapedInt, 1);

        }
        return true;
    }


    static int sMapIntUseMaxBitLen = 0;
    /**
     * 获取版型映射成int需要的最多位数
     * @return
     */
    public static int getMapIntUseMaxLen(){
        if(sMapIntUseMaxBitLen == 0){
            ArrayList<String> paiWeaveStrs = getAllPaiGroup(CardType.ARROW, false);
            for (String paiWeaveStr : paiWeaveStrs) {
                generateMapedInt(CardType.ARROW,paiWeaveStr);
            }
        }
        return sMapIntUseMaxBitLen;
    }

    public static void outputPaiMap(){
        ArrayList<String> paiWeaves = getAllPaiGroup(CardType.ARROW, false);

        try {
            FileWriter fileWriter = new FileWriter("paiMap.txt");
            for (String str : paiWeaves) {
                int weaveInt = generateMapedInt(CardType.ARROW,str);
                String binaryString = Integer.toBinaryString(weaveInt);
                StringBuilder sb = new StringBuilder();
                for (int i = 32 - binaryString.length(); i > 0; i--) {
                    sb.append("0");
                }
                sb.append(binaryString);
                fileWriter.write(str + "    ->    " + sb.toString() + "\n");
                fileWriter.write(getPaiGroupHumanName(CardType.ARROW, str) + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 字符型表示的牌型转成int[]表示的牌型
     * @param paiGroupStr
     * @return
     */
    public static int[] getPaiCardNums(CardType cardType, String paiGroupStr){
        int arrLen = 0;
        if(cardType == CardType.WAN){
            arrLen = 9;
        }else if(cardType == CardType.TIAO){
            arrLen = 9;
        }else if(cardType == CardType.TONG){
            arrLen = 9;
        }else if(cardType == CardType.FENG){
            arrLen = 4;
        }else if(cardType == CardType.ARROW){
            arrLen = 3;
        }
        String[] numStrings = paiGroupStr.split("&");
        int[] nums = new int[arrLen];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arrLen; i++) {
            nums[i] = Integer.valueOf(numStrings[i]);
        }
        return nums;
    }


    public static String to32BinaryString(int number){
        String binaryString = Integer.toBinaryString(number);
        StringBuilder sb = new StringBuilder();
        for (int i = 32 - binaryString.length(); i > 0; i--) {
            sb.append("0");
        }
        sb.append(binaryString);
        return sb.toString();
    }

    /**
     * 获取可读的牌面
     * @param mappInt
     * @return
     */
    public static String getPaiGroupHumanName(CardType cardType, int mappInt){
        int arrLen = 0;
        if(cardType == CardType.WAN){
            arrLen = 9;
        }else if(cardType == CardType.TIAO){
            arrLen = 9;
        }else if(cardType == CardType.TONG){
            arrLen = 9;
        }else if(cardType == CardType.FENG){
            arrLen = 4;
        }else if(cardType == CardType.ARROW){
            arrLen = 3;
        }
        int[] nums = new int[9];
        for(int i = (arrLen - 1); i >= 0; i--){
            int count = 0;

            while((mappInt ^ (mappInt - 1)) == 1){
                count++;
                mappInt >>= 1;
            }
            nums[i] = count;
            mappInt >>= 1;
        }
        return getPaiGroupHumanName(cardType, nums);
    }


    /**
     * 获取可读的牌面
     * @param cardType 牌的花色
     * @param cardCounts 各牌的数量
     * @return
     */
    public static String getPaiGroupHumanName(CardType cardType, int[] cardCounts){

        int startValue = 0;
        int arrLen = 0;
        if(cardType == CardType.WAN){
            startValue = 0x01;
            arrLen = 9;
        }else if(cardType == CardType.TIAO){
            startValue = 0x11;
            arrLen = 9;
        }else if(cardType == CardType.TONG){
            startValue = 0x21;
            arrLen = 9;
        }else if(cardType == CardType.FENG){
            startValue = 0x31;
            arrLen = 4;
        }else if(cardType == CardType.ARROW){
            startValue = 0x35;
            arrLen = 3;
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
    /**
     * 获取可读的牌面
     * @param paiWeaveStr
     * @return
     */
    public static String getPaiGroupHumanName(CardType cardType, String paiWeaveStr){
        int arrLen = 0;
        if(cardType == CardType.WAN){
            arrLen = 9;
        }else if(cardType == CardType.TIAO){
            arrLen = 9;
        }else if(cardType == CardType.TONG){
            arrLen = 9;
        }else if(cardType == CardType.FENG){
            arrLen = 4;
        }else if(cardType == CardType.ARROW){
            arrLen = 3;
        }
        String[] numStrings = paiWeaveStr.split("&");
        int[] nums = new int[arrLen];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arrLen; i++) {
            nums[i] = Integer.valueOf(numStrings[i]);
        }

        return getPaiGroupHumanName(cardType, nums);

    }

    public static void TestAllPai(){

        int paiGroupCount = 0;
        int huCount = 0;
        int pengHuCount = 0;
        try {
            for(CardType cardType : ALL_CARD_TYPE){
                String fileName = "TestAllPai";
                if(cardType == CardType.WAN){
                    fileName += "-WAN";
                }else if(cardType == CardType.TIAO){
                    fileName += "-TIAO";
                }else if(cardType == CardType.TONG){
                    fileName += "-TONG";
                }else if(cardType == CardType.FENG){
                    fileName += "-FENG";
                }else if(cardType == CardType.ARROW){
                    fileName += "-ARROW";
                }
                fileName += ".txt";


                FileWriter fileWriter = new FileWriter(fileName);
                ArrayList<String> paiWeaves = getAllPaiGroup(cardType, false);
                paiGroupCount += paiWeaves.size();
                for (String str : paiWeaves) {
                    int weaveInt = generateMapedInt(cardType, str);
                    String binaryString = to32BinaryString(weaveInt);
                    fileWriter.write(str + "    ->    " + binaryString + "\n");
                    fileWriter.write(getPaiGroupHumanName(cardType, str) + "\n");
                    //fileWriter.write(getPaiGroupHumanName(cardType, weaveInt) + "\n");
                    boolean huResult = someColorPackableAnalyze(getPaiCardNums(cardType, str), 0);
                    if(huResult){
                        huCount++;
                    }
                    boolean bPengPengHu = isPengPengHu(getPaiNums(cardType, weaveInt));
                    if(huResult){
                        fileWriter.write(getPacksName(cardType, weaveInt) + "\n");
                        if(bPengPengHu){
                            fileWriter.write("YES碰碰胡\n");
                            pengHuCount++;
                        }
                        else{
                            fileWriter.write("YES胡\n");
                        }

                    }
                    else{
                        int[] nums = getPaiNums(cardType, weaveInt);
                        fileWriter.write("NO胡\n");

                        int minKingCount = getCanPackMinKingCount(cardType, nums);
                        if(minKingCount > 8){
                            fileWriter.write("NO胡---怎么都不能糊\n");
                        }
                        else{
                            fileWriter.write("NO胡---最小糊精:" + minKingCount + "\n");
                        }

                    }

                }
                fileWriter.close();
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("总共牌型:" + paiGroupCount + "可胡的结果:" + huCount + "---碰碰胡：" + pengHuCount);
    }

    /**
     * 获取组成完整幅牌需要的最少的精的个数
     * @param cardNums 不能服牌的牌型
     * @return
     */
    public static int getCanPackMinKingCount(CardType cardType, int[] cardNums){
        int personCount = 0;
        if(cardType == CardType.WAN){
            personCount = 9;
        }else if(cardType == CardType.TIAO){
            personCount = 9;
        }else if(cardType == CardType.TONG){
            personCount = 9;
        }else if(cardType == CardType.FENG){
            personCount = 4;
        }else if(cardType == CardType.ARROW){
            personCount = 3;
        }

        int cardCount = Utils.getCardCount(cardNums);

        int kingCount =(3 - cardCount % 3);

        while (kingCount <= 8){
            ArrayList<String> allocateThingGroups = AllocateUtils.allocateThing(personCount, kingCount);

            for (String allocateThingGroup : allocateThingGroups) {
                int[] tempCardNums = new int[cardNums.length];
                System.arraycopy(cardNums, 0, tempCardNums, 0, cardNums.length);
                String[] ownedThings = allocateThingGroup.split(AllocateUtils.ALLOCATE_THING_SEPARATOR);
                for(int i = 0; i < cardNums.length; i++){
                    tempCardNums[i] += Integer.valueOf(ownedThings[i]);
                }
                if(someColorPackableAnalyze(tempCardNums, 0)){
                    return kingCount;
                }
            }

            kingCount += 3;
        }
        return kingCount;
    }



    public static boolean isPengPengHu(int[] cardNums){
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


    /**
     * 手牌是否能成组
     * @param cardNums
     * @return
     */
    public static boolean isPackable(int[] cardNums){
        int gangCount = Utils.getGangCount(cardNums);
        if(gangCount == 3){
            return true;
        }

        int count = Utils.getCardCount(cardNums);
        int[] wanCardNums = Utils.getSomeColorCardNums(cardNums, MJCard.WAN);
        int[] tiaoCardNums = Utils.getSomeColorCardNums(cardNums, MJCard.TIAO);
        int[] tongCardNums = Utils.getSomeColorCardNums(cardNums, MJCard.TONG);
        int[] fengCardNums = Utils.getSomeColorCardNums(cardNums, MJCard.FENG);
        int[] arrowCardNums = Utils.getSomeColorCardNums(cardNums, MJCard.ARROW);

        return false;
    }

    /**
     * 同一种花色牌是否能成组
     * @param cardNums
     * @param beginIndex
     * @return
     */
    public static boolean someColorPackableAnalyze(int[] cardNums, int beginIndex) {
        if (beginIndex >= cardNums.length) {
            return true;
        }

        int count = 0;
        int gangCount = 0;
        for (int i = 0; i < cardNums.length; i++) {
            count += cardNums[i];
            if(cardNums[i] == 4){
                gangCount++;
            }
        }
        if (count == 0) {
            return true;
        }

        if(gangCount == 3){//类似 4444 8888 9999 牌型
            return true;
        }

        if (count % 3 != 0) {
            return false;
        }




        int curCardCount = cardNums[beginIndex];
        if (curCardCount == 0) {
            return someColorPackableAnalyze(cardNums, ++beginIndex);
        }

        int[] tempCardNums = new int[cardNums.length];
        System.arraycopy(cardNums, 0, tempCardNums, 0, cardNums.length);
        //curCardCount > 0
        boolean pengResult = false;
        boolean shunResult = false;
        boolean bAnalyzeFeng = (cardNums.length == 4);

        if (curCardCount >= 3) {//试碰的分析
            int[] tempPengCardNums = new int[cardNums.length];
            System.arraycopy(tempCardNums, 0, tempPengCardNums, 0, cardNums.length);
            tempPengCardNums[beginIndex] -= 3;
            pengResult = someColorPackableAnalyze(tempPengCardNums, tempPengCardNums[beginIndex] == 0 ? beginIndex + 1 : beginIndex);
        }

        //顺子分析
        if (!bAnalyzeFeng) {//非风分析
            if (beginIndex >= tempCardNums.length - 2) {//curCardCount > 0  7、8位或2、3位或1、2位
                shunResult = false;
            } else {
                if (tempCardNums[beginIndex + 1] >= curCardCount && tempCardNums[beginIndex + 2] >= curCardCount) {
                    int[] tempShunCardNums = new int[cardNums.length];
                    System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                    tempShunCardNums[beginIndex] -= curCardCount;
                    tempShunCardNums[beginIndex + 1] -= curCardCount;
                    tempShunCardNums[beginIndex + 2] -= curCardCount;
                    shunResult = someColorPackableAnalyze(tempShunCardNums, beginIndex + 1);
                } else {
                    shunResult = false;
                }
            }
        } else {//风分析
            if (beginIndex >= tempCardNums.length - 2) {
                shunResult = false;
            } else {
                //beginIndex 0, 1
                if (beginIndex == 0) {
                    boolean choiceOneResult = false;
                    boolean choiceTwoResult = false;
                    if (tempCardNums[beginIndex + 1] > 0 && tempCardNums[beginIndex + 2] > 0) {
                        int[] tempShunCardNums = new int[cardNums.length];
                        System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                        tempShunCardNums[beginIndex]--;
                        tempShunCardNums[beginIndex + 1]--;
                        tempShunCardNums[beginIndex + 2]--;
                        choiceOneResult = someColorPackableAnalyze(tempShunCardNums, beginIndex);
                    }

                    if (tempCardNums[beginIndex + 2] > 0 && tempCardNums[beginIndex + 3] > 0) {
                        int[] tempShunCardNums = new int[cardNums.length];
                        System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                        tempShunCardNums[beginIndex]--;
                        tempShunCardNums[beginIndex + 2]--;
                        tempShunCardNums[beginIndex + 3]--;
                        choiceTwoResult = someColorPackableAnalyze(tempShunCardNums, beginIndex);
                    }
                    shunResult = (choiceOneResult || choiceTwoResult);
                } else if (beginIndex == 1) {
                    if (tempCardNums[beginIndex + 1] >= curCardCount && tempCardNums[beginIndex + 2] >= curCardCount) {
                        int[] tempShunCardNums = new int[cardNums.length];
                        System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                        tempShunCardNums[beginIndex] -= curCardCount;
                        tempShunCardNums[beginIndex + 1] -= curCardCount;
                        tempShunCardNums[beginIndex + 2] -= curCardCount;
                        shunResult = someColorPackableAnalyze(tempShunCardNums, ++beginIndex);
                    }
                }

            }
        }


        return pengResult || shunResult;
    }


    public static boolean isLanPackable(int[] cardNums){
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

    public static void testAllLan(){
        int lanCount = 0;
        try {
            FileWriter fileWriter = new FileWriter("testAllLan.txt");
            for(int i = 0; i < 4; i++){
                ArrayList<String> paiGroups = AllocateUtils.allocateThing(9, i);
                for(String paiGroup : paiGroups){
                    int weaveInt = generateMapedInt(CardType.WAN,paiGroup);
                    String binaryString = to32BinaryString(weaveInt);
                    //fileWriter.write(paiGroup + "    ->    " + binaryString + "\n");
                    boolean lan = isLanPackable(getPaiCardNums(CardType.WAN, paiGroup));
                    if(lan){
                        fileWriter.write(getPaiGroupHumanName(CardType.WAN,paiGroup) + "\n");
                        fileWriter.write(lan ? "YES烂\n" : "NO烂\n");
                    }



                    if(lan){
                        lanCount++;
                    }
                }
            }

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("可烂的结果:" + lanCount);
    }

    public static int getLanMaxKingCount(int[] cardNums){
        int count = Utils.getCardCount(cardNums);
        if(count == 2){
            if((cardNums[1] == 1 && cardNums[7] == 1) ||
                    (cardNums[2] == 1 && cardNums[6] == 1) ||
                    (cardNums[2] == 1 && cardNums[7] == 1) ||
                    (cardNums[3] == 1 && cardNums[7] == 1)){
                return 10;
            }
            else{
                return 1;
            }
        }
        else{
            return 3 - count;
        }
    }

    public static void main(String[] args) {
        TestAllPai();
        long totalCount = 0;
        ArrayList<String> allocateStrs = AllocateUtils.allocateThing(5, 14);
        for (String allocateStr : allocateStrs) {
            int[] nums = new int[5];
            String[] numStrs = allocateStr.split(AllocateUtils.ALLOCATE_THING_SEPARATOR);
            for(int i = 0; i < 5; i++){
                nums[i] = Integer.valueOf(numStrs[i]);
            }

            if(nums[4] >= 13){
                continue;
            }
            int wanSize = getAllocatePaiGroup(9, nums[0]).size();
            int tiaoSize = getAllocatePaiGroup(9, nums[1]).size();
            int tongSize = getAllocatePaiGroup(9, nums[2]).size();
            int fengSize = getAllocatePaiGroup(4, nums[3]).size();
            int arrowSize = getAllocatePaiGroup(3, nums[4]).size();

            wanSize = (wanSize == 0 ? 1 : wanSize);
            tiaoSize = (tiaoSize == 0 ? 1 : tiaoSize);
            tongSize = (tongSize == 0 ? 1 : tongSize);
            fengSize = (fengSize == 0 ? 1 : fengSize);
            arrowSize = (arrowSize == 0 ? 1 : arrowSize);
            totalCount += (wanSize * tiaoSize * tongSize * fengSize * arrowSize);
        }
        System.out.println("count:" + totalCount);
        //System.out.println(getMapIntUseMaxLen());

        //System.out.println(getAllocateThingGroup(5, 8).size());
        //System.out.println(getCanHuMinKingCount(getPaiCardNums("0&1&1&1&1&1&0&0&1")));
        //testGetPacks();
        //testAllLan();
        //System.out.println(generateMapedInt("1&2&0&1&0&0&0&0&0"));
    }

    public static void testGetPacks(){
        ArrayList<Pack> packs = someColorPaiGetPacks(getPaiNums(CardType.WAN,346991), 0, CardType.WAN);
        for (Pack pack: packs) {
            System.out.println(pack.getPaiGroupHumanName());
        }
    }

    public static String getPacksName(CardType cardType, int mappedInt){
        ArrayList<Pack> packs = someColorPaiGetPacks(getPaiNums(cardType, mappedInt), 0, cardType);
        StringBuilder sb = new StringBuilder();
        for (Pack pack: packs) {
            sb.append(pack.getPaiGroupHumanName());
        }
        return sb.toString();
    }

    //这里的牌都是能糊的,且没有那种(4444 8888 9999)牌型
    public static ArrayList<Pack> someColorPaiGetPacksNoGang(int[] cardNums, int beginIndex, int color) {
        if (beginIndex >= cardNums.length) {
            return new ArrayList<Pack>();
        }

        int count = Utils.getCardCount(cardNums);
        if (count == 0) {
            return new ArrayList<Pack>();
        }

        if (count % 3 != 0) {
            return null;
        }



        int curCardCount = cardNums[beginIndex];
        if (curCardCount == 0) {
            return someColorPaiGetPacksNoGang(cardNums, ++beginIndex, color);
        }

        int[] tempCardNums = new int[cardNums.length];
        System.arraycopy(cardNums, 0, tempCardNums, 0, cardNums.length);

        //curCardCount > 0
        ArrayList<Pack> pengResult = null;
        ArrayList<Pack> shunResult = null;
        boolean bAnalyzeFeng = (cardNums.length == 4);

        if (curCardCount >= 3) {//试碰的分析
            int[] tempPengCardNums = new int[cardNums.length];
            System.arraycopy(tempCardNums, 0, tempPengCardNums, 0, cardNums.length);
            tempPengCardNums[beginIndex] -= 3;
            pengResult = someColorPaiGetPacksNoGang(tempPengCardNums, tempPengCardNums[beginIndex] == 0 ? beginIndex + 1 : beginIndex, color);
            if(pengResult != null){
                byte cardByte = Utils.getCardByteBySomeColorIndex(beginIndex, color);
                pengResult.add(new Pack(Pack.TRIPLET, cardByte, false));
            }
        }

        //顺子分析
        if (!bAnalyzeFeng) {//非风分析
            if (beginIndex >= tempCardNums.length - 2) {//curCardCount > 0  7、8位或2、3位或1、2位
                shunResult = null;
            } else {
                if (tempCardNums[beginIndex + 1] >= curCardCount && tempCardNums[beginIndex + 2] >= curCardCount) {
                    int[] tempShunCardNums = new int[cardNums.length];
                    System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                    tempShunCardNums[beginIndex] -= curCardCount;
                    tempShunCardNums[beginIndex + 1] -= curCardCount;
                    tempShunCardNums[beginIndex + 2] -= curCardCount;
                    shunResult = someColorPaiGetPacksNoGang(tempShunCardNums, beginIndex + 1, color);
                    if(shunResult != null){
                        for(int i = 0; i < curCardCount; i++){
                            byte cardByte = Utils.getCardByteBySomeColorIndex(beginIndex, color);
                            shunResult.add(new Pack(Pack.SHUN, cardByte, false));
                        }
                    }
                } else {
                    shunResult = null;
                }
            }
        } else {//风分析
            if (beginIndex >= tempCardNums.length - 2) {
                shunResult = null;
            } else {
                //beginIndex 0, 1
                if (beginIndex == 0) {
                    ArrayList<Pack> choiceOneResult = null;
                    ArrayList<Pack> choiceTwoResult = null;
                    if (tempCardNums[beginIndex + 1] > 0 && tempCardNums[beginIndex + 2] > 0) {
                        int[] tempShunCardNums = new int[cardNums.length];
                        System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                        tempShunCardNums[beginIndex]--;
                        tempShunCardNums[beginIndex + 1]--;
                        tempShunCardNums[beginIndex + 2]--;
                        choiceOneResult = someColorPaiGetPacksNoGang(tempShunCardNums, beginIndex, color);
                        if(choiceOneResult != null){
                            for(int i = 0; i < curCardCount; i++){
                                byte cardByte = Utils.getCardByteBySomeColorIndex(beginIndex, color);
                                choiceOneResult.add(new Pack(Pack.SHUN, cardByte, false));
                            }
                        }
                    }

                    if (tempCardNums[beginIndex + 2] > 0 && tempCardNums[beginIndex + 3] > 0) {
                        int[] tempShunCardNums = new int[cardNums.length];
                        System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                        tempShunCardNums[beginIndex]--;
                        tempShunCardNums[beginIndex + 2]--;
                        tempShunCardNums[beginIndex + 3]--;
                        choiceTwoResult = someColorPaiGetPacksNoGang(tempShunCardNums, beginIndex, color);
                        if(choiceTwoResult != null){
                            for(int i = 0; i < curCardCount; i++){
                                byte cardByte = Utils.getCardByteBySomeColorIndex(beginIndex, color);
                                choiceTwoResult.add(new Pack(Pack.SHUN, cardByte, true));
                            }
                        }
                    }
                    shunResult = choiceOneResult;
                    if(choiceTwoResult != null && choiceTwoResult.size() > 0){
                        shunResult = choiceTwoResult;
                    }
                } else if (beginIndex == 1) {
                    if (tempCardNums[beginIndex + 1] >= curCardCount && tempCardNums[beginIndex + 2] >= curCardCount) {
                        int[] tempShunCardNums = new int[cardNums.length];
                        System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                        tempShunCardNums[beginIndex] -= curCardCount;
                        tempShunCardNums[beginIndex + 1] -= curCardCount;
                        tempShunCardNums[beginIndex + 2] -= curCardCount;
                        shunResult = someColorPaiGetPacksNoGang(tempShunCardNums, ++beginIndex,color);
                        if(shunResult != null){
                            for(int i = 0; i < curCardCount; i++){
                                byte cardByte = Utils.getCardByteBySomeColorIndex(beginIndex, color);
                                shunResult.add(new Pack(Pack.SHUN, cardByte, false));
                            }
                        }
                    }
                }

            }
        }


        int pengPriority = -1;
        int shunPriority = -1;
        if(pengResult != null  && shunResult != null ){
            for (Pack pack : pengResult) {
                pengPriority += pack.getPriority();
            }

            for (Pack pack : shunResult) {
                shunPriority += pack.getPriority();
            }
            if(pengPriority > shunPriority){
                return pengResult;
            }
            else{
                return shunResult;
            }
        }

        else{
            if(pengResult != null && pengResult.size() > 0){
                return pengResult;
            }
            else return shunResult;
        }
    }


    //可胡牌的
    public static ArrayList<Pack> someColorPaiGetPacks(int[] cardNums, int beginIndex, CardType cardType) {
        int color = 0;
        if(cardType == CardType.WAN){
            color = 0x0;
        }else if(cardType == CardType.TIAO){
            color = 0x1;
        }else if(cardType == CardType.TONG){
            color = 0x2;
        }else if(cardType == CardType.FENG){
            color = 0x3;
        }else if(cardType == CardType.ARROW){
            color = 0x3;
        }
        if(Utils.getGangCount(cardNums) == 3){
            ArrayList<Pack> packs = new ArrayList<>();
            for(int i = 0; i < cardNums.length; i++){
                if(cardNums[i] == 4){
                    byte cardByte;
                    if(cardType == CardType.ARROW)
                    {
                        cardByte = Utils.getCardByteBySomeColorIndex(i + 4, color);
                    }else{
                        cardByte = Utils.getCardByteBySomeColorIndex(i, color);
                    }

                    packs.add(new Pack(Pack.QUARTETTE, cardByte, false));
                }
            }
            return packs;
        }
        else{
            return someColorPaiGetPacksNoGang(cardNums, beginIndex,color);
        }
    }


    /**
     * 分配规则：thingCount个东西分配给personCount个人，可以有人一个也没分配到
     * 返回上面规则定义的所有分配组合
     * @param personCount  人数
     * @param thingCount   待分配数
     * @return 如果personCount==0 || thingCount ==0 返回0
     */
    public static ArrayList<String> getAllocateThingGroup1(int personCount, int thingCount) {
        ArrayList<String> strings = new ArrayList<>();
        if (personCount == 1) {
            strings.add(thingCount + "");
            return strings;
        }
        if (personCount == 0) {
            return strings;
        }
        for (int i = 0; i <= thingCount; i++) {
            int nextN = personCount - 1;
            int nextM = thingCount - i;

            if (nextN <= 0 || nextM < 0) {
                continue;
            }

            ArrayList<String> nextStrings = getAllocateThingGroup1(personCount - 1, thingCount - i);
            for (int j = 0; j < nextStrings.size(); j++) {
                strings.add(nextStrings.get(j) + AllocateUtils.ALLOCATE_THING_SEPARATOR + i);
            }
        }
        return strings;
    }

}
