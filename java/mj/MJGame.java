package mj;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MJGame {


    private final byte[] mMJCardBytes = new byte[Config.CARD_NUM];

    private int[] mCardNums = new int[Config.CARD_INDEX_MAX_LENGTH];
    private int mCurCardIndex = -1;

    private boolean bSupportKing;
    private int mPositiveKingByte;
    private int mNegativeKingByte;
    private int mPositiveKingIndex;
    private int mNegativeKingIndex;
    private String mPositiveKingName;
    private String mNegativeKingName;
    public MJGame(boolean bSupportKing) {
        this.bSupportKing = bSupportKing;
        if (bSupportKing) {
            setKing();
        }
    }


    private void ShuffleMJ() {
        System.arraycopy(Config.MJ_CARD_BYTES, 0, mMJCardBytes, 0, Config.CARD_NUM);
        Random random = new Random(new Date().getTime());
        Byte temp;
        for (int i = mMJCardBytes.length - 1, j; i > 0; i--) {
            j = random.nextInt(i + 1);
            temp = mMJCardBytes[j];
            mMJCardBytes[j] = mMJCardBytes[i];
            mMJCardBytes[i] = temp;
        }
    }

    public void setKing() {
        Random random = new Random(new Date().getTime());
        mPositiveKingIndex = 2;//random.nextInt(34);//33;
        mPositiveKingByte = Utils.getCardByte(mPositiveKingIndex);
        mPositiveKingName = new MJCard((byte)mPositiveKingByte).toString();

        int kingColorIndex = Utils.getCardColorIndexByIndex(mPositiveKingIndex);
        int kingValueIndex = Utils.getCardValueIndexByIndex(mPositiveKingIndex);

        boolean bKingIsZiPai = (kingColorIndex >= 0 && kingColorIndex <= 2);
        boolean bKingIsFengPai = bKingIsZiPai ? false : (kingValueIndex >= 1 && kingValueIndex <= 4);
        boolean bKingIsArrowPai = bKingIsZiPai ? false : (kingValueIndex >= 5 && kingValueIndex <= 7);

        if (bKingIsZiPai) {
            mNegativeKingIndex = kingColorIndex * 9 + (kingValueIndex) % 9;
        } else if (bKingIsFengPai) {
            mNegativeKingIndex = 27 + (kingValueIndex) % 4;
        } else if (bKingIsArrowPai) {
            mNegativeKingIndex = 31 + (kingValueIndex - 4) % 3;
        }

        mNegativeKingByte = Utils.getCardByte(mNegativeKingIndex);
        mNegativeKingName = new MJCard((byte)mNegativeKingByte).toString();
    }

    private void faCard() {
        byte[] testBytes = {
//                 0x01, 0x02, 0x03,
//                0x13, 0x13, 0x13,
//                0x14, 0x15, 0x16,
//                0x27, 0x27,
//                0x35, 0x36, 0x37
//                0x31, 0x32, 0x33,
//                0x31, 0x33, 0x34,
//                0x32, 0x33, 0x34,
//                0x27, 0x27,
//                0x35, 0x36, 0x37

//                0x07, 0x08, 0x37,
//                0x31, 0x32, 0x33,
//                0x31, 0x33, 0x34,
//                0x35, 0x36, 0x37,
//                0x11, 0x11
//                0x08, 0x09, 0x35,
//                0x24, 0x25, 0x26,
//                0x27, 0x28, 0x29,
//
//                0x11, 0x12, 0x13,
//                0x27, 0x27,

                0x03, 0x03, 0x03,
                0x13, 0x13, 0x13,
                0x14, 0x14, 0x14,
                0x27, 0x27,
                0x35, 0x35, 0x35

        };
        byte[] handCardBytes = new byte[14];
        System.arraycopy(testBytes, 0, handCardBytes, 0, 14);
        //System.arraycopy(mMJCardBytes, 0, handCardBytes, 0, 14);

        mCurCardIndex = 14;


        for (int i = 0; i < handCardBytes.length; i++) {
            mCardNums[Utils.getCardIndex(handCardBytes[i])]++;
        }
    }

    private void init() {
        ShuffleMJ();
        faCard();
    }

    private void printAllHandMJCard() {
        StringBuilder[] Sbs = {new StringBuilder(), new StringBuilder(), new StringBuilder(), new StringBuilder(), new StringBuilder()};
        String emptyStr = "--------";
        String formartStr = "%-7s";
        int index = 0;
        for (int i = 0; i < mCardNums.length; i++) {
            int count = mCardNums[i];
            if (count > 0) {
                MJCard mjCard = new MJCard(Utils.getCardByte(i));
                for (int j = count; j > 0; j--) {
                    boolean isKing = (i == mPositiveKingIndex || i == mNegativeKingIndex);
                    if(bSupportKing && isKing){
                        Sbs[4].append(String.format(Locale.CHINESE, formartStr, mjCard.toString() + index));
                    }
                    else{
                        Sbs[4].append(String.format(Locale.CHINESE, formartStr, emptyStr));
                        switch (mjCard.getCardType()) {
                            case MJCard.WAN:
                                Sbs[0].append(String.format(Locale.CHINESE, formartStr, mjCard.toString() + index));
                                Sbs[1].append(String.format(Locale.CHINESE, formartStr, emptyStr));
                                Sbs[2].append(String.format(Locale.CHINESE, formartStr, emptyStr));
                                Sbs[3].append(String.format(Locale.CHINESE, formartStr, emptyStr));
                                break;
                            case MJCard.TIAO:
                                Sbs[1].append(String.format(Locale.CHINESE, formartStr, mjCard.toString() + index));
                                Sbs[2].append(String.format(Locale.CHINESE, formartStr, emptyStr));
                                Sbs[3].append(String.format(Locale.CHINESE, formartStr, emptyStr));
                                break;
                            case MJCard.TONG:
                                Sbs[2].append(String.format(Locale.CHINESE, formartStr, mjCard.toString() + index));
                                Sbs[3].append(String.format(Locale.CHINESE, formartStr, emptyStr));
                                break;
                            case MJCard.ZI:
                                Sbs[3].append(String.format(Locale.CHINESE, formartStr, mjCard.toString() + index));
                                break;
                        }
                    }

                    index++;
                }
            }
        }

        if(bSupportKing){
            System.out.println("----------当前牌---------:" + "上精：" + mPositiveKingName + "-----下精：" + mNegativeKingName);
        }
        else{
            System.out.println("----------当前牌---------");
        }

        for (int i = 0; i < Sbs.length - 1; i++) {
            if (Sbs[i].length() > 0) {
                System.out.println(Sbs[i].toString());
            }
        }
        if(bSupportKing){
            if(Sbs[4].length() > 0){
                System.out.println(Sbs[4].toString());
            }
        }
    }


    public int[] getSomeColorCardNums(int[] cardNums, int cardType) {
        switch (cardType) {
            case MJCard.WAN:
                int[] wanCardNums = new int[9];
                System.arraycopy(cardNums, 0, wanCardNums, 0, 9);//[0-8]
                return wanCardNums;
            case MJCard.TIAO:
                int[] tiaoCardNums = new int[9];
                System.arraycopy(cardNums, 9, tiaoCardNums, 0, 9);//[9-17]
                return tiaoCardNums;
            case MJCard.TONG:
                int[] tongCardNums = new int[9];
                System.arraycopy(cardNums, 18, tongCardNums, 0, 9);//[18-26]
                return tongCardNums;
            case MJCard.FENG:
                int[] fengCardNums = new int[4];
                System.arraycopy(cardNums, 27, fengCardNums, 0, 4);//[27-30]
                return fengCardNums;
            case MJCard.ARROW:
                int[] arrowCardNums = new int[3];
                System.arraycopy(cardNums, 31, arrowCardNums, 0, 3);//[31-33]
                return arrowCardNums;
        }
        return null;
    }

    public int[] findTouIndex() {
        if (bSupportKing) {
            ArrayList<Integer> touIndexs = new ArrayList<>();

            int kingCount = 0;
            for (int i = 0; i < mCardNums.length; i++) {
                boolean isKing = (i == mPositiveKingIndex || i == mNegativeKingIndex);
                if (isKing) {
                    kingCount += mCardNums[i];
                } else if (mCardNums[i] >= 2) {
                    touIndexs.add(i);
                }
            }

            if (kingCount >= 2) {
                touIndexs.add(mPositiveKingIndex);
            }
            int [] tous = new int[touIndexs.size()];
            for(int i = 0; i < tous.length; ++i){
                tous[i] = touIndexs.get(i);
            }
            return tous;

        } else {
            ArrayList<Integer> touIndexs = new ArrayList<>();
            for (int i = 0; i < mCardNums.length; i++) {
                if (mCardNums[i] >= 2) {
                    touIndexs.add(i);
                }
            }
            int[] tous = new int[touIndexs.size()];
            for (int i = 0; i < tous.length; i++) {
                tous[i] = touIndexs.get(i);
            }
            return tous;
        }

    }


    public boolean isKingIndex(int[] cardNums, int index) {

        boolean bZiPai = (cardNums.length == 9);
        boolean bFengPai = (cardNums.length == 4);
        boolean bArrowPai = (cardNums.length == 3);


        int kingColorIndex = Utils.getCardColorIndexByIndex(mPositiveKingIndex);
        int kingValueIndex = Utils.getCardValueIndexByIndex(mPositiveKingIndex);

        boolean bKingIsZiPai = (kingColorIndex >= 0 && kingColorIndex <= 2);
        boolean bKingIsFengPai = bKingIsZiPai ? false : (kingValueIndex >= 1 && kingValueIndex <= 4);
        boolean bKingIsArrowPai = bKingIsZiPai ? false : (kingValueIndex >= 5 && kingValueIndex <= 7);
        if (bKingIsZiPai && bZiPai) {
            return index == (kingValueIndex) || index == (kingValueIndex + 1) % 9;
        } else if (bKingIsFengPai && bFengPai) {
            return index == (kingValueIndex) || index == (kingValueIndex + 1 - 1) % 4;
        } else if (bKingIsArrowPai && bArrowPai) {
            return index == (kingValueIndex - 4) || index == (kingValueIndex - 4 + 1) % 3;
        }
        return false;

    }

    //分配精 personCount个人分析kingCount个精的所有组合
    public static ArrayList<String> divKing(int personCount, int kingCount) {
        ArrayList<String> strings = new ArrayList<>();
        if (personCount == 1) {
            strings.add(kingCount + "");
            return strings;
        }
        if (personCount == 0) {
            return strings;
        }
        for (int i = 0; i <= kingCount; i++) {
            int nextN = personCount - 1;
            int nextM = kingCount - i;

            if (nextN <= 0 || nextM < 0) {
                continue;
            }

            ArrayList<String> nextStrings = divKing(personCount - 1, kingCount - i);
            for (int j = 0; j < nextStrings.size(); j++) {
                strings.add(nextStrings.get(j) + "&" + i);
            }
        }
        return strings;
    }


    //无精情况分析
    public boolean someColorPaiHuAnalyzeNoKing(int[] cardNums, int beginIndex) {
        if (beginIndex >= cardNums.length) {
            return true;
        }

        int count = getCardCount(cardNums);
        if (count == 0) {
            return true;
        }

        if (count % 3 != 0) {
            return false;
        }

        int[] tempCardNums = new int[cardNums.length];
        System.arraycopy(cardNums, 0, tempCardNums, 0, cardNums.length);

        int curCardCount = tempCardNums[beginIndex];
        if (curCardCount == 0) {
            return someColorPaiHuAnalyzeNoKing(cardNums, ++beginIndex);
        }

        //curCardCount > 0
        boolean pengResult = false;
        boolean shunResult = false;
        boolean bAnalyzeFeng = (cardNums.length == 4);

        if (curCardCount >= 3) {//试碰的分析
            int[] tempPengCardNums = new int[cardNums.length];
            System.arraycopy(tempCardNums, 0, tempPengCardNums, 0, cardNums.length);
            tempPengCardNums[beginIndex] -= 3;
            pengResult = someColorPaiHuAnalyzeNoKing(tempPengCardNums, tempPengCardNums[beginIndex] == 0 ? beginIndex + 1 : beginIndex);
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
                    shunResult = someColorPaiHuAnalyzeNoKing(tempShunCardNums, beginIndex + 1);
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
                        choiceOneResult = someColorPaiHuAnalyzeNoKing(tempShunCardNums, beginIndex);
                    }

                    if (tempCardNums[beginIndex + 2] > 0 && tempCardNums[beginIndex + 3] > 0) {
                        int[] tempShunCardNums = new int[cardNums.length];
                        System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                        tempShunCardNums[beginIndex]--;
                        tempShunCardNums[beginIndex + 2]--;
                        tempShunCardNums[beginIndex + 3]--;
                        choiceTwoResult = someColorPaiHuAnalyzeNoKing(tempShunCardNums, beginIndex);
                    }
                    shunResult = (choiceOneResult || choiceTwoResult);
                } else if (beginIndex == 1) {
                    if (tempCardNums[beginIndex + 1] >= curCardCount && tempCardNums[beginIndex + 2] >= curCardCount) {
                        int[] tempShunCardNums = new int[cardNums.length];
                        System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                        tempShunCardNums[beginIndex] -= curCardCount;
                        tempShunCardNums[beginIndex + 1] -= curCardCount;
                        tempShunCardNums[beginIndex + 2] -= curCardCount;
                        shunResult = someColorPaiHuAnalyzeNoKing(tempShunCardNums, ++beginIndex);
                    }
                }

            }
        }


        return pengResult || shunResult;
    }

    //当前beginIndex位置用精的胡牌分析
    public boolean someColorPaiHuAnalyzeUseKing(int[] cardNums, int beginIndex, int kingCount) {
        if (kingCount <= 0) {
            return false;
        }


        if (beginIndex >= cardNums.length) {
            return kingCount % 3 == 0;
        }


        int cardCount = getCardCount(cardNums) + kingCount;
        if (cardCount % 3 != 0) {
            return false;
        }


        int[] tempCardNums = new int[cardNums.length];
        System.arraycopy(cardNums, 0, tempCardNums, 0, cardNums.length);

        boolean bAnalyzeFeng = (cardNums.length == 4);

        boolean huResult = false;

        boolean noUseKingResult = false;
        boolean useKingResult = false;
        //kingCount > 0

        /*
            0   1   1
            0   1   0
            0   0   1
            0   0   0

            1   1   1
            1   0   1
            1   1   0
            1   0   0
         */
        int curCardCount = tempCardNums[beginIndex];
        if (curCardCount == 0) {
            if(beginIndex >= cardNums.length - 2){
                int[] tempShunCardNums = new int[cardNums.length];
                System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                if(kingCount >= 3){
                    return someColorPaiHuAnalyze(tempShunCardNums, ++beginIndex, kingCount - 3);
                }else{
                    return false;
                }
            }


            if (tempCardNums[beginIndex + 1] > 0 && tempCardNums[beginIndex + 2] > 0) {// 0   1   1
                int[] tempShunCardNums = new int[cardNums.length];
                System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                tempShunCardNums[beginIndex + 1]--;
                tempShunCardNums[beginIndex + 2]--;
                huResult = someColorPaiHuAnalyze(tempShunCardNums, ++beginIndex, kingCount - 1);
            } else if (tempCardNums[beginIndex + 1] > 0 && tempCardNums[beginIndex + 2] <= 0) {// 0     1    0
                int[] tempShunCardNums = new int[cardNums.length];
                System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                tempShunCardNums[beginIndex + 1]--;
                huResult = someColorPaiHuAnalyze(tempShunCardNums, ++beginIndex, kingCount - 2);
            } else if (tempCardNums[beginIndex + 1] < 0 && tempCardNums[beginIndex + 2] > 0) {//0   0   1
                int[] tempShunCardNums = new int[cardNums.length];
                System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                tempShunCardNums[beginIndex + 2]--;
                huResult = someColorPaiHuAnalyze(tempShunCardNums, ++beginIndex, kingCount - 2);
            } else if (tempCardNums[beginIndex + 1] < 0 && tempCardNums[beginIndex + 2] < 0) {//0   0   0
                int[] tempShunCardNums = new int[cardNums.length];
                System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                huResult = someColorPaiHuAnalyze(tempShunCardNums, ++beginIndex, kingCount - 3);
            }
        } else {
            if(beginIndex >= cardNums.length - 2){
                int[] tempShunCardNums = new int[cardNums.length];
                System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                //
                if(curCardCount + kingCount >= 3){
                    tempShunCardNums[beginIndex] = 0;
                    return someColorPaiHuAnalyze(tempShunCardNums, ++beginIndex, kingCount - (3 - curCardCount));
                }else{
                    return false;
                }
            }
            /*if(tempCardNums[beginIndex + 1] > 0 && tempCardNums[beginIndex + 2] > 0){// 1   1   1
                int[] tempShunCardNums = new int[cardNums.length];
                System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                tempShunCardNums[beginIndex]--;
                tempShunCardNums[beginIndex + 1]--;
                tempShunCardNums[beginIndex + 2]--;
                huResult = someColorPaiHuAnalyze(tempShunCardNums, beginIndex, kingCount);
            }
            else */
            if (tempCardNums[beginIndex + 1] > 0 && tempCardNums[beginIndex + 2] <= 0) {// 1     1    0
                int[] tempShunCardNums = new int[cardNums.length];
                System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                tempShunCardNums[beginIndex]--;
                tempShunCardNums[beginIndex + 1]--;
                huResult = someColorPaiHuAnalyze(tempShunCardNums, beginIndex, kingCount - 1);
            } else if (tempCardNums[beginIndex + 1] < 0 && tempCardNums[beginIndex + 2] > 0) {//1   0   1
                int[] tempShunCardNums = new int[cardNums.length];
                System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                tempShunCardNums[beginIndex]--;
                tempShunCardNums[beginIndex + 2]--;
                huResult = someColorPaiHuAnalyze(tempShunCardNums, beginIndex, kingCount - 1);
            } else if (tempCardNums[beginIndex + 1] < 0 && tempCardNums[beginIndex + 2] < 0) {//1   0   0
                int[] tempShunCardNums = new int[cardNums.length];
                tempShunCardNums[beginIndex]--;
                System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                huResult = someColorPaiHuAnalyze(tempShunCardNums, beginIndex, kingCount - 2);
            }
        }

        return huResult;
    }


    //当前beginIndex位置不用精的胡牌分析
    public boolean someColorPaiHuAnalyZeNoUseKing(int[] cardNums, int beginIndex, int kingCount) {
        if (kingCount <= 0) {//无精情况
            return someColorPaiHuAnalyzeNoKing(cardNums, beginIndex);
        }

        if (beginIndex >= cardNums.length) {
            return kingCount % 3 == 0;
        }


        int cardCount = getCardCount(cardNums) + kingCount;
        if (cardCount % 3 != 0) {
            return false;
        }


        int[] tempCardNums = new int[cardNums.length];
        System.arraycopy(cardNums, 0, tempCardNums, 0, cardNums.length);

        boolean bAnalyzeFeng = (cardNums.length == 4);


        //kingCount > 0
        int curCardCount = tempCardNums[beginIndex];

        if (curCardCount == 0) {
            return someColorPaiHuAnalyze(tempCardNums, beginIndex + 1, kingCount);
        } else {
            //curCardCount > 0
            boolean noUseKingPengResult = false;
            boolean noUseKingShunResult = false;

            if (curCardCount >= 3) {//试碰的分析
                int[] tempPengCardNums = new int[cardNums.length];
                System.arraycopy(tempCardNums, 0, tempPengCardNums, 0, cardNums.length);
                tempPengCardNums[beginIndex] -= 3;
                noUseKingPengResult = someColorPaiHuAnalyze(tempPengCardNums, tempPengCardNums[beginIndex] == 0 ? beginIndex + 1 : beginIndex, kingCount);
            }

            //curCardCount > 0 //顺子分析
            if (!bAnalyzeFeng) {
                if (beginIndex >= tempCardNums.length - 2) {
                    noUseKingShunResult = false;
                } else {
                    if (tempCardNums[beginIndex + 1] >= curCardCount && tempCardNums[beginIndex + 2] >= curCardCount) {
                        int[] tempShunCardNums = new int[cardNums.length];
                        System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                        tempShunCardNums[beginIndex] -= curCardCount;
                        tempShunCardNums[beginIndex + 1] -= curCardCount;
                        tempShunCardNums[beginIndex + 2] -= curCardCount;
                        noUseKingShunResult = someColorPaiHuAnalyze(tempShunCardNums, beginIndex + 1, kingCount);
                    } else {
                        noUseKingShunResult = false;
                    }
                }
            } else {
                if (beginIndex >= tempCardNums.length - 2) {
                    noUseKingShunResult = false;
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
                            choiceOneResult = someColorPaiHuAnalyze(tempShunCardNums, beginIndex, kingCount);
                        }

                        if (tempCardNums[beginIndex + 2] > 0 && tempCardNums[beginIndex + 3] > 0) {
                            int[] tempShunCardNums = new int[cardNums.length];
                            System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                            tempShunCardNums[beginIndex]--;
                            tempShunCardNums[beginIndex + 2]--;
                            tempShunCardNums[beginIndex + 3]--;
                            choiceTwoResult = someColorPaiHuAnalyze(tempShunCardNums, beginIndex, kingCount);
                        }
                        noUseKingShunResult = (choiceOneResult || choiceTwoResult);
                    } else if (beginIndex == 1) {
                        if (tempCardNums[beginIndex + 1] >= curCardCount && tempCardNums[beginIndex + 2] >= curCardCount) {
                            int[] tempShunCardNums = new int[cardNums.length];
                            System.arraycopy(tempCardNums, 0, tempShunCardNums, 0, cardNums.length);
                            tempShunCardNums[beginIndex] -= curCardCount;
                            tempShunCardNums[beginIndex + 1] -= curCardCount;
                            tempShunCardNums[beginIndex + 2] -= curCardCount;
                            noUseKingShunResult = someColorPaiHuAnalyze(tempShunCardNums, ++beginIndex, kingCount);
                        }
                    }

                }
            }
            return noUseKingPengResult | noUseKingShunResult;
        }
    }


    //需要保证精从cardNums中删除了
    public boolean someColorPaiHuAnalyze(int[] cardNums, int beginIndex, int kingCount) {
        if (kingCount < 0) {
            return false;
        }

        int cardCount = getCardCount(cardNums) + kingCount;
        if (cardCount % 3 != 0) {
            return false;
        }

        if (beginIndex >= cardNums.length) {
            return (kingCount % 3 == 0);
        }

        if (kingCount == 0) {//无精情况
            return someColorPaiHuAnalyzeNoKing(cardNums, beginIndex);
        }

        //if (isKingIndex(cardNums, beginIndex)) {//精位略过
        //    return someColorPaiHuAnalyze(cardNums, beginIndex + 1, kingCount);
        //}


        return someColorPaiHuAnalyZeNoUseKing(cardNums, beginIndex, kingCount) | someColorPaiHuAnalyzeUseKing(cardNums, beginIndex, kingCount);
    }


    private int removeAllKingNums(int[] cardNums) {


        int kingCount = cardNums[mPositiveKingIndex] + cardNums[mNegativeKingIndex];
        cardNums[mPositiveKingIndex] = 0;
        cardNums[mNegativeKingIndex] = 0;
        return kingCount;

    }

    private int getKingCount(int[] cardNums) {
        if (!bSupportKing) {
            return 0;
        }
        return cardNums[mPositiveKingIndex] + cardNums[mNegativeKingIndex];
    }



    public boolean huAnalyze() {
        int kingCount = getKingCount(mCardNums);
        int[] touIndexs = findTouIndex();
        if (touIndexs.length <= 0) {
            return false;
        }
        if (bSupportKing && kingCount > 0) {

            for (int i = 0; i < touIndexs.length; i++) {
                int[] tempCardNums = new int[Config.CARD_INDEX_MAX_LENGTH];
                System.arraycopy(mCardNums, 0, tempCardNums, 0, mCardNums.length);
                removeAllKingNums(tempCardNums);
                boolean isKing = (touIndexs[i] == mPositiveKingIndex || touIndexs[i] == mNegativeKingIndex);
                int curKingCount = kingCount;
                if(!isKing){
                    tempCardNums[touIndexs[i]] -= 2;
                }
                else{//精当头
                    curKingCount -= 2;
                }
                int[] tempWanCardNums = getSomeColorCardNums(tempCardNums, MJCard.WAN);
                int[] tempTiaoCardNums = getSomeColorCardNums(tempCardNums, MJCard.TIAO);
                int[] tempTongCardNums = getSomeColorCardNums(tempCardNums, MJCard.TONG);
                int[] tempFengCardNums = getSomeColorCardNums(tempCardNums, MJCard.FENG);
                int[] tempArrowCardNums = getSomeColorCardNums(tempCardNums, MJCard.ARROW);

                ArrayList<String> divKings = divKing(5, curKingCount);
                if(divKings.size() == 0){
                    boolean wanResult = someColorPaiHuAnalyzeNoKing(tempWanCardNums, 0);
                    if(!wanResult){
                        continue;
                    }
                    boolean tiaoResult = someColorPaiHuAnalyzeNoKing(tempTiaoCardNums, 0);
                    if(!tiaoResult){
                        continue;
                    }
                    boolean tongResult = someColorPaiHuAnalyzeNoKing(tempTongCardNums, 0);
                    if(!tongResult){
                        continue;
                    }
                    boolean fengResult = someColorPaiHuAnalyzeNoKing(tempFengCardNums, 0);
                    if(!fengResult){
                        continue;
                    }
                    boolean arrowResult = someColorPaiHuAnalyzeNoKing(tempArrowCardNums, 0);
                    if(!arrowResult){
                        continue;
                    }


                    return true;

                }else{
                    for (String divKingString: divKings) {
                        String[] divKingStrings = divKingString.split("&");
                        //String[] divKingStrings = "1&0&0&0&2".split("&");
                        boolean wanResult = someColorPaiHuAnalyze(tempWanCardNums, 0, Integer.valueOf(divKingStrings[0]));
                        if(!wanResult){
                            continue;
                        }
                        boolean tiaoResult = someColorPaiHuAnalyze(tempTiaoCardNums, 0, Integer.valueOf(divKingStrings[1]));
                        if(!tiaoResult){
                            continue;
                        }
                        boolean tongResult = someColorPaiHuAnalyze(tempTongCardNums, 0,Integer.valueOf(divKingStrings[2]));
                        if(!tongResult){
                            continue;
                        }
                        boolean fengResult = someColorPaiHuAnalyze(tempFengCardNums, 0, Integer.valueOf(divKingStrings[3]));
                        if(!fengResult){
                            continue;
                        }
                        boolean arrowResult = someColorPaiHuAnalyze(tempArrowCardNums, 0, Integer.valueOf(divKingStrings[4]));
                        if(!arrowResult){
                            continue;
                        }


                        return true;
                    }
                }


            }
            return false;
        } else {
            for (int i = 0; i < touIndexs.length; i++) {
                int[] tempCardNums = new int[Config.CARD_INDEX_MAX_LENGTH];
                System.arraycopy(mCardNums, 0, tempCardNums, 0, mCardNums.length);
                tempCardNums[touIndexs[i]] -= 2;
                int[] tempWanCardNums = getSomeColorCardNums(tempCardNums, MJCard.WAN);
                int[] tempTiaoCardNums = getSomeColorCardNums(tempCardNums, MJCard.TIAO);
                int[] tempTongCardNums = getSomeColorCardNums(tempCardNums, MJCard.TONG);
                int[] tempFengCardNums = getSomeColorCardNums(tempCardNums, MJCard.FENG);
                int[] tempArrowCardNums = getSomeColorCardNums(tempCardNums, MJCard.ARROW);

                boolean wanResult = someColorPaiHuAnalyzeNoKing(tempWanCardNums, 0);
                boolean tiaoResult = someColorPaiHuAnalyzeNoKing(tempTiaoCardNums, 0);
                boolean tongResult = someColorPaiHuAnalyzeNoKing(tempTongCardNums, 0);
                boolean fengResult = someColorPaiHuAnalyzeNoKing(tempFengCardNums, 0);
                boolean arrowResult = someColorPaiHuAnalyzeNoKing(tempArrowCardNums, 0);

                boolean result = wanResult && tiaoResult && tongResult && fengResult && arrowResult;
                if (result) {
                    return true;
                }
            }
            return false;
        }


    }

    private void arrangeHandCard() {

    }

    private ChiResult chiAnalyze() {
        return null;
    }

    public int getCardCount(int[] cardNums) {
        if (cardNums == null || cardNums.length <= 0) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < cardNums.length; i++) {
            count += cardNums[i];
        }
        return count;
    }

    private void daPai() {
        boolean exit = false;
        while (!exit) {
            Scanner in = new Scanner(System.in);
            System.out.print("请输入要打的牌的索引:");
            int indexStr = Integer.valueOf(in.nextLine());
            if (indexStr > getCardCount(mCardNums)) {
                System.out.println("输入的索引有错误");
                continue;
            }

            for (int i = 0; i <= mCardNums.length; i++) {
                if (mCardNums[i] > 0) {
                    indexStr -= mCardNums[i];
                    if (indexStr < 0) {
                        mCardNums[i]--;
                        exit = true;
                        break;
                    }
                }
            }
            System.out.println("---------当前牌-----------------");
            printAllHandMJCard();

        }
    }

    private void getPai() {
        byte getPaiByte = mMJCardBytes[mCurCardIndex];
        System.out.println("拿的牌：" + new MJCard(getPaiByte));
        mCardNums[Utils.getCardIndex(getPaiByte)]++;
        System.out.println("---------当前牌-----------------");
        printAllHandMJCard();
    }

    private void testHu() {
        System.out.println("begin:" + System.currentTimeMillis());
        boolean hu = huAnalyze();
        System.out.println("end:" + System.currentTimeMillis());
        System.out.println("胡牌?" + hu);
    }

    private void startGame() {
        printAllHandMJCard();
        while (mCurCardIndex < Config.CARD_NUM) {
            if (huAnalyze()) {
                System.out.println("***胡牌了****");
                break;
            }
            daPai();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getPai();
            mCurCardIndex++;
        }


    }

    public static void main(String[] args) {
        MJGame mjGame = new MJGame(true);
        mjGame.init();
        mjGame.testHu();
        //mjGame.startGame();

    }
}
