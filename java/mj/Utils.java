package mj;

public class Utils {
    @SuppressWarnings("unused")
    public static byte[] initArray(byte[] bytes){
        for(int i = 0; i < bytes.length; i++){
            bytes[i] = 0x0;
        }
        return bytes;
    }

    @SuppressWarnings("unused")
    public static MJCard[] initArray(MJCard[] mjCards){
        for(int i = 0; i < mjCards.length; i++){
            mjCards[i] = new MJCard();
        }
        return mjCards;
    }

    public static int getCardIndex(byte cardByte){
        int colorIndex = (cardByte & MJCard.CARD_TYPE_MASK) >> 4;
        int valueIndex = (cardByte & MJCard.CARD_VALUE_MASK) - 1;

        return colorIndex * 9 + valueIndex;
    }

    public static byte getCardByte(int index){
        int colorIndex = index / 9;
        int valueIndex = index % 9 + 1;
        int cardByte = (colorIndex << 4) + valueIndex;
        return (byte)(cardByte);
    }

    public static int getCardColorIndexByIndex(int index){
        return index / 9;
    }

    public static int getCardValueIndexByIndex(int index){
        return index % 9 + 1;
    }


    public static int getCardCount(int[] cardNums) {
        if (cardNums == null || cardNums.length <= 0) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < cardNums.length; i++) {
            count += cardNums[i];
        }
        return count;
    }

    public static int getGangCount(int[] cardNums){
        if (cardNums == null || cardNums.length <= 0) {
            return 0;
        }
        int gangCount = 0;
        for (int i = 0; i < cardNums.length; i++) {
            if(cardNums[i] == 4){
                gangCount++;
            }
        }
        return gangCount;
    }

    public static byte getCardByteBySomeColorIndex(int index, int color){
        int cardByte = (color << 4) + index + 1;
        return (byte)(cardByte);
    }

    public static int[] getSomeColorCardNums(int[] cardNums, int cardType) {
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

}
