package mj;


public final class MJCard implements Comparable<MJCard>, Cloneable{

    public static final int WAN = 0X00;
    public static final int TIAO = 0X01;
    public static final int TONG = 0X02;
    public static final int ZI = 0X03;
    public static final int FENG = 0x04;
    public static final int ARROW = 0x05;

    public static final int CARD_TYPE_MASK = 0xF0;
    public static final int CARD_VALUE_MASK = 0x0F;

    private byte mValue;
    public MJCard(){

    }
    public MJCard(byte value){
        this.mValue = value;
        checkCard();
    }
    private void checkCard(){
        boolean right = false;
        int cardType = getCardType();
        int cardValue = getCardValue();
        if(cardType == ZI){
            right = cardValue >= 1 && cardValue <= 7;
        }
        else if(cardType >= WAN && cardType <= TONG){
            right = cardValue >= 1 && cardValue <= 9;
        }
        if(!right){
            throw new IllegalArgumentException("card illegal");
        }
    }

    public void setCardValue(byte value){
        this.mValue = value;
        checkCard();
    }
    public int getCardType(){
        return (mValue&CARD_TYPE_MASK) >> 4;
    }
    public int getCardValue(){
        return mValue&CARD_VALUE_MASK;
    }

    public byte getCardByte(){
        return mValue;
    }
    @Override
    public String toString() {
        int cardType = getCardType();
        int cardValue = getCardValue();
        if(cardType == ZI){
            switch (cardValue){
                case 1:
                    return "东风";
                case 2:
                    return "南风";
                case 3:
                    return "西风";
                case 4:
                    return "北风";
                case 5:
                    return "红中";
                case 6:
                    return "发财";
                case 7:
                    return "白板";
            }
        }

        else if(cardType >= WAN && cardType <= TONG){
            String cardTypeLabel = "";
            String cardValueLable = "";
            switch (cardType){
                case 0:
                    cardTypeLabel = "万";
                    break;
                case 1:
                    cardTypeLabel = "条";
                    break;
                case 2:
                    cardTypeLabel = "筒";
                    break;

            }
            switch (cardValue){
                case 1:
                    cardValueLable = "一";
                    break;
                case 2:
                    cardValueLable = "二";
                    break;
                case 3:
                    cardValueLable = "三";
                    break;
                case 4:
                    cardValueLable = "四";
                    break;
                case 5:
                    cardValueLable = "五";
                    break;
                case 6:
                    cardValueLable = "六";
                    break;
                case 7:
                    cardValueLable = "七";
                    break;
                case 8:
                    cardValueLable = "八";
                    break;
                case 9:
                    cardValueLable = "九";
                    break;
            }
            return cardValueLable + cardTypeLabel;
        }
        return "";
    }
    public static void main(String[] args) {
        MJCard mjCard = new MJCard((byte)0x18);
        System.out.println(mjCard);
    }

    @Override
    public int compareTo( MJCard mjCard) {
        return this.mValue - mjCard.getCardByte();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        MJCard mjCard = new MJCard(this.getCardByte());
        return mjCard;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }

        if(!(obj instanceof MJCard)){
            return false;
        }
        MJCard mjCard = (MJCard)obj;

        return this.mValue == mjCard.getCardValue();
    }
}
