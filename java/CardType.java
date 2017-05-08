
public enum CardType {
    WAN, TIAO, TONG, FENG, ARROW;

    public static int getCardNum(CardType cardType){
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
        return personCount;
    }

    public static String getCardTypeName(CardType cardType){
        if(cardType == CardType.WAN){
            return "WAN";
        }else if(cardType == CardType.TIAO){
            return "TIAO";
        }else if(cardType == CardType.TONG){
            return "TONG";
        }else if(cardType == CardType.FENG){
            return "FENG";
        }else if(cardType == CardType.ARROW){
            return "ARROW";
        }
        return "";
    }
}
