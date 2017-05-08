package mj;

/**
 * 一副牌
 */
public class Pack {
    public static final int SHUN = 1;
    public static final int TRIPLET = 2;//碰
    public static final int QUARTETTE = 3;//杠
    public static final int UNKNOWN = -1;

    public int type = UNKNOWN;
    public byte mKeyCardByte = (byte) 0xFF;//当前这副牌值最小的牌面byte
    public boolean mIsDNB = false;//是否是东南北

    public Pack(int type, byte keyCardByte, boolean isDNB) {
        this.type = type;
        mKeyCardByte = keyCardByte;
        mIsDNB = isDNB;
    }

    public byte[] getCardBytes() {

        boolean available = (type == SHUN || type == TRIPLET || type == QUARTETTE);
        if (!available) {
            throw new IllegalStateException("type is illegal value");
        }

        if (type == SHUN) {
            byte[] returyBytes = new byte[3];
            if (mIsDNB) {
                returyBytes[0] = mKeyCardByte;
                returyBytes[1] = (byte) (mKeyCardByte + 2);
                returyBytes[2] = (byte) (mKeyCardByte + 3);
            } else {
                returyBytes[0] = mKeyCardByte;
                returyBytes[1] = (byte) (mKeyCardByte + 1);
                returyBytes[2] = (byte) (mKeyCardByte + 2);
            }
            return returyBytes;
        } else if (type == QUARTETTE) {
            byte[] returyBytes = new byte[4];
            returyBytes[0] = mKeyCardByte;
            returyBytes[1] = mKeyCardByte;
            returyBytes[2] = mKeyCardByte;
            returyBytes[3] = mKeyCardByte;
            return returyBytes;
        } else {
            byte[] returyBytes = new byte[3];
            returyBytes[0] = mKeyCardByte;
            returyBytes[1] = mKeyCardByte;
            returyBytes[2] = mKeyCardByte;
            return returyBytes;
        }

    }

    public String getPaiGroupHumanName(){
        MJCard mjCard = new MJCard();
        StringBuilder sb = new StringBuilder();
        byte[] cardBytes = getCardBytes();
        for (int i = 0; i < cardBytes.length; i++) {
            mjCard.setCardValue(cardBytes[i]);
            sb.append(mjCard.toString() + ";");
        }

        return sb.toString();
    }


    public int getPriority(){
        boolean available = (type == SHUN || type == TRIPLET || type == QUARTETTE);
        if (!available) {
            throw new IllegalStateException("type is illegal value");
        }
        if (type == SHUN) {
            return 1;
        } else if (type == QUARTETTE) {
            return 10000;
        } else {
            return 100;
        }
    }
}
