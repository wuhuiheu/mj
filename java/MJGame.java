import java.awt.PageAttributes;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class MJGame {
    public static final CardType[] ALL_CARD_TYPE={


            CardType.WAN,

    };




    /**
     * 获取玩家可能的所有麻将牌
     * 玩家的牌数[0, 12]去掉了头
     * @param cardType 牌类型
     * @param outputFile 是否输出到文件中
     * @return
     */
    public static ArrayList<String> getAllPaiGroup(CardType cardType, boolean outputFile) {
        int personCount = CardType.getCardNum(cardType);


        ArrayList<String> pais = new ArrayList<>();


        for(int i = 0; i <= 12; ++i){
            pais.addAll(AllocUtils.alloc(personCount, i, 4));
        }



        if (outputFile) {
            try {
                File parent = new File("getAllPaiGroup");
                if(!parent.exists()){
                    parent.mkdirs();
                }
                File file = new File(parent, CardType.getCardTypeName(cardType) + ".txt");

                FileWriter fileWriter = new FileWriter(file);
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


    public static class Item implements Comparable<Integer>{
        public int rawInt = -1;

        public int wanInt = -1;
        public PaiGroup wanPaiGroup;

        public int fengInt = -1;
        public PaiGroup fengPaiGroup;

        public int arrowInt = -1;
        public PaiGroup arrowPaiGroup;

        public int resultInt = 0;

        @Override
        public int compareTo(Integer o) {
            return (rawInt - o);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("rsuInt:").append(to32BinaryString(resultInt)).append("\n");
            sb.append("rawInt:").append(to32BinaryString(rawInt)).append("\n");

            int extra = getExtraInt(this);
            sb.append("rsuIn:").append(to32BinaryString(resultInt)).append("\n");
            sb.append("extIn;").append(to12BinaryString(extra)).append("&");
            for(int i = 0; i < 20; i++){
                sb.append("0");
            }
            sb.append("\n");
            sb.append("wanInt:").append(to32BinaryString(wanInt)).append("\n");
            sb.append("wanPia:").append(wanPaiGroup.toString()).append(wanPaiGroup.isPackable ? " YES" : " NO");
            if(!wanPaiGroup.isPackable){
                sb.append(" minKing:").append(wanPaiGroup.mMinPackableKingCount);
            }
            sb.append("\n");

            if(fengInt >= 0){
                sb.append("fenInt:").append(to32BinaryString(fengInt)).append("\n");
                sb.append("fenPia:").append(fengPaiGroup.toString()).append(fengPaiGroup.isPackable ? " YES" : " NO");
                if(!fengPaiGroup.isPackable){
                    sb.append(" minKing:").append(fengPaiGroup.mMinPackableKingCount);
                }
                sb.append("\n");
            }

            if(arrowInt >= 0){
                sb.append("arrInt:").append(to32BinaryString(arrowInt)).append("\n");
                sb.append("arrPia:").append(arrowPaiGroup.toString()).append(arrowPaiGroup.isPackable ? " YES" : " NO");
                if(!arrowPaiGroup.isPackable){
                    sb.append(" minKing:").append(arrowPaiGroup.mMinPackableKingCount);
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    public static void getAllMappedInt() throws IOException {
        Map<Integer, Item> mappedInts = new HashMap<>();

        ArrayList<String> paiWeaves = getAllPaiGroup(CardType.ARROW, false);
        for (String str : paiWeaves) {
            PaiGroup paiGroup = new PaiGroup(CardType.ARROW, str);
            Item item = new Item();

            item.rawInt = paiGroup.mRawMappedInt;
            item.arrowPaiGroup = paiGroup;
            item.arrowInt = paiGroup.mRawMappedInt;
            mappedInts.put(paiGroup.mRawMappedInt, item);
        }

        paiWeaves.clear();
        paiWeaves = getAllPaiGroup(CardType.FENG, false);
        for (String str : paiWeaves) {
            PaiGroup paiGroup = new PaiGroup(CardType.FENG, str);

            Item item = mappedInts.get(paiGroup.mRawMappedInt);
            if(item == null){
                item = new Item();
                item.rawInt = paiGroup.mRawMappedInt;
                item.fengInt = paiGroup.mRawMappedInt;
                item.fengPaiGroup = paiGroup;
                mappedInts.put(paiGroup.mRawMappedInt, item);
            }else{
                item.fengInt = paiGroup.mRawMappedInt;
                item.fengPaiGroup = paiGroup;
            }



        }

        paiWeaves.clear();
        paiWeaves = getAllPaiGroup(CardType.WAN, false);
        for (String str : paiWeaves) {
            PaiGroup paiGroup = new PaiGroup(CardType.WAN, str);

            Item item = mappedInts.get(paiGroup.mRawMappedInt);
            if(item == null){
                item = new Item();
                item.rawInt = paiGroup.mRawMappedInt;
                item.wanInt = paiGroup.mRawMappedInt;
                item.wanPaiGroup = paiGroup;
                mappedInts.put(paiGroup.mRawMappedInt, item);
            }else{
                item.wanInt = paiGroup.mRawMappedInt;
                item.wanPaiGroup = paiGroup;
            }


        }

        Iterator entries = mappedInts.entrySet().iterator();

        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();

            Integer key = (Integer)entry.getKey();

            Item value = (Item)entry.getValue();

            value.resultInt |= getExtraInt(value);
            value.resultInt <<= 20;
            value.resultInt |= value.rawInt;
        }

        ArrayList<Item> sortItems = new ArrayList<>();
        sortItems.addAll(mappedInts.values());
        sortItems.sort(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return (o1.rawInt - o2.rawInt);
            }
        });

        File parent = new File("getAllMappedInt");
        if(!parent.exists()){
            parent.mkdirs();
        }

        File file = new File(parent, "result.txt");


        FileWriter fileWriter = new FileWriter(file);

        for (Item item : sortItems) {
            //fileWriter.write(item.toString());
            fileWriter.write(to32BinaryString(item.rawInt));
            fileWriter.write("\n");
        }
        fileWriter.close();



    }

    public  static String to32BinaryString(int num){
        String binaryString = Integer.toBinaryString(num);
        StringBuilder sb = new StringBuilder();
        for (int i = 32 - binaryString.length(); i > 0; i--) {
            sb.append("0");
        }
        sb.append(binaryString);
        return sb.toString();
    }

    public  static String to12BinaryString(int num){
        String binaryString = Integer.toBinaryString(num);
        StringBuilder sb = new StringBuilder();
        for (int i = 12 - binaryString.length(); i > 0; i--) {
            sb.append("0");
        }
        sb.append(binaryString);
        return sb.toString();
    }

    private static int getExtraInt(Item item){
        int extraInt = 0;
        extraInt <<= 4;
        if(item.wanInt >= 0 && !item.wanPaiGroup.isPackable){
            extraInt |= item.wanPaiGroup.mMinPackableKingCount;

        }

        extraInt <<= 1;
        if(item.wanInt >=0 && item.wanPaiGroup.isPackable){
            extraInt |= 1;
        }

        extraInt <<= 3;
        if(item.fengInt >= 0 && !item.fengPaiGroup.isPackable){
            extraInt |= item.fengPaiGroup.mMinPackableKingCount;
        }

        extraInt <<= 1;
        if(item.fengInt >= 0 && item.fengPaiGroup.isPackable){
            extraInt |= 1;
        }

        extraInt <<= 2;
        if(item.fengInt < 0){
            extraInt |= (0x0);
        }else if(item.fengInt >= 0 && item.arrowInt < 0){
            extraInt |= (0x1);
        }else if(item.fengInt >= 0 && item.arrowInt >= 0){
            extraInt |= (0x2);
        }

        return extraInt;
    }
    public static void TestAllPai(){


        int totalPaiGroupCount = 0;
        int totalPackableCount = 0;
        int totalPengPengPackableCount = 0;
        try {
            for(CardType cardType : ALL_CARD_TYPE){

                File parent = new File("TestAllPai");
                if(!parent.exists()){
                    parent.mkdirs();
                }

                File file = new File(parent, CardType.getCardTypeName(cardType) + ".txt");


                FileWriter fileWriter = new FileWriter(file);
                ArrayList<String> paiWeaves = getAllPaiGroup(cardType, false);
                totalPaiGroupCount += paiWeaves.size();
                for (String str : paiWeaves) {
                    PaiGroup paiGroup = new PaiGroup(cardType, str);

                    fileWriter.write(str + "    ->    " + paiGroup.to32BinaryString() + "\n");
                    fileWriter.write(paiGroup.mPaiGroupHumanName + "\n");

                    if(paiGroup.isPackable){
                        totalPackableCount++;
                    }

                    if(paiGroup.isPackable){

                        if(paiGroup.isPengPengPackable){
                            fileWriter.write("YES碰碰胡\n");
                            totalPengPengPackableCount++;
                        }
                        else{
                            fileWriter.write("YES胡\n");
                        }

                    }else{

//                        if(paiGroup.mMinPackableKingCount > 8){
//                            fileWriter.write("NO胡---怎么都不能糊\n");
//                        }
//                        else{
                            fileWriter.write("NO胡---最小糊精:" + paiGroup.mMinPackableKingCount + "\n");
//                        }

                    }

                }

                fileWriter.close();
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("总共牌型:" + totalPaiGroupCount + "可胡的结果:" + totalPackableCount + "---碰碰胡：" + totalPengPengPackableCount);
    }


    public static void main(String[] args) throws IOException {
        getAllMappedInt();

        PaiGroup arrowPai = new PaiGroup(CardType.ARROW, "0&0&0");
        PaiGroup wanPai = new PaiGroup(CardType.WAN, "0&0&0&0&0&0&0&0&0");
        PaiGroup fengPai = new PaiGroup(CardType.FENG, "0&0&0&0");
        Item item = new Item();
        item.rawInt = 0;
        item.arrowInt = 0;
        item.fengInt = 0;
        item.wanInt = 0;
        item.arrowPaiGroup = arrowPai;
        item.wanPaiGroup = wanPai;
        item.fengPaiGroup = fengPai;

        int extraInt = getExtraInt(item);
        item.resultInt |= extraInt;
        item.resultInt <<= 20;
        item.resultInt |= item.rawInt;
        System.out.println(item.toString());
        int j = 0;

    }



}
