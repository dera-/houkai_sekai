package sim.game.data;

import java.util.ArrayList;

public class Sentence {
    private static ArrayList<Sentence> sentenceList = new ArrayList<Sentence>();
    private static int sentenceNumbers = 0;
    private final int id;
    private ArrayList<String> content = new ArrayList<String>();
    private static final String END_FLAG = "END";
    
    public Sentence(String data){
        String[] elems = data.split(",");
        id = sentenceNumbers;
        for(int i=0;i<elems.length;i++){
            if(elems[i].equals(END_FLAG))break;
            content.add(elems[i]);
        }
        sentenceNumbers++;
    }
    
    public boolean isFinished(int index){
        return index >= content.size();
    }
    
    public String getOneSentence(int index){
        return content.get(index);
    }
    
    public static void setSentence(Sentence sent){
        sentenceList.add(sent);
    }
    
    public static void clearList(){
        sentenceList.clear();
    }
    
    public static Sentence getSentence(int num){
        if(num<0 || sentenceList.size()<=num)return null;
        return sentenceList.get(num);
    }
    
}
