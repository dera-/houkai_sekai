package sim.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import sim.game.Paramaters;
import sim.game.brain.BrainParameter;
import sim.game.data.CharaData;
import sim.game.data.EventData;
import sim.game.data.Gene;
import sim.game.data.Job;
import sim.game.data.Map;
import sim.game.data.MapChip;
import sim.game.data.Sentence;
import sim.game.data.Skill;

/** 各ファイルを読み込むクラス */
public class ReadFile {
	
	/** クラスフィールドの全ての名前リストに文字列データを格納するメソッド */
	public void createAllCharaNameLists(){
		createCharaNameList(Paramaters.getManNameList1(),"ManName1.txt");
		createCharaNameList(Paramaters.getManNameList2(),"ManName2.txt");
		createCharaNameList(Paramaters.getWomanNameList1(),"WomanName1.txt");
		createCharaNameList(Paramaters.getWomanNameList2(),"WomanName2.txt");
	}
	
	/** 引数のリストに、引数ファイルの文字列データを全て格納するメソッド */
	private void createCharaNameList(ArrayList<String> list,String file){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(file)));
			String name;
			while((name=br.readLine())!=null){
				list.add(name);
			}
		}catch(Exception ex){
		    System.exit(0);  //応急処置　TODO　リファクタ
		    ex.printStackTrace();
		    System.out.println("キャラの名前を読み込めませんでした");
		}
	}
	
	/** CharaListを作成するメソッド */
	public void createCharaList(){ 
		ArrayList<CharaData> list = getCharaList("CharaList.csv");
		CharaData.setCharaList(list);
	}
	
	/** キャラクターリストの作成(static)*/
	private ArrayList<CharaData> getCharaList(String fileN){
		try {
			ArrayList<CharaData> list = new ArrayList<CharaData>();
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileN)));
            br.readLine(); //ヘッダの読み飛ばし
            String line;
            while((line=br.readLine())!=null){
            	String strs[]=line.split(",");
            	int start=2;
            	int ss[]=new int[13];
            	int propers[]=new int[10];
            	for(int i=0;i<ss.length;i++){
            		ss[i]=Integer.parseInt(strs[i+start]);
            	}
            	for(int i=0;i<propers.length;i++){
            		propers[i] = Integer.parseInt(strs[i+ss.length+start]);
            	}
            	ArrayList<String> JL=new ArrayList<String>();
            	ArrayList<Integer> levels = new ArrayList<Integer>();
            	ArrayList<String> CL=new ArrayList<String>();
            	int index=ss.length+propers.length+start;
            	for(int i=0;i<2;i++){
            		String S;
            		while( (S=strs[index]).equals("END") == false ){
            			index++;
            			if(i==0){
            				JL.add(S);
            				levels.add(Integer.parseInt(strs[index]));
            				index++;
            			}
            			else CL.add(S);
            		}
            		index++;
            	}
            	list.add(new CharaData(strs[0],strs[1],ss,propers,JL,levels,CL));
            }
            br.close();
            return list;
		}catch(Exception ex){
		    System.exit(0);  //応急処置　TODO　リファクタ
		    ex.printStackTrace();
			return null;
		}	
	}
	
	/** イベントリストの作成メソッド */
	public void createEventList(){
		ArrayList<EventData> EventList = EventData.getEventList();
		EventList.clear();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("EventList.txt")));
            String line;
            ArrayList<String> list = new ArrayList<String>();
            while((line=br.readLine())!=null){
            	if(line.equals("END")){
            		EventList.add(new EventData(list));
            		list.clear();
            	}
            	else list.add(line);
            }
            br.close();
		}catch(Exception ex){
		    System.exit(0);  //応急処置　TODO　リファクタ
		    ex.printStackTrace();
		    System.out.println("イベントの読み込みに失敗しました");
		}
	}
	
	/** GeneListに値を与えるメソッド */
	public void createGeneList(){
		ArrayList<Gene> list = Gene.getGeneList();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("GeneList.csv")));
            br.readLine(); //ヘッダの読み飛ばし
            String line;
            while((line=br.readLine())!=null){
            	String[] strs=line.split(",");
            	list.add( new Gene(strs[0],strs[1],Integer.parseInt(strs[2])) );
            }
            br.close();
		}catch(Exception ex){
		    System.exit(0);  //応急処置　TODO　リファクタ
		    ex.printStackTrace();
		    System.out.println("遺伝リストの読み込みに失敗しました");
		}	
	}
	
	
	/** フィールドJobListの作成 */
	public void createJobList(){
		ArrayList<Job> JobList = Job.getJobList();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("JobList.csv")));
            br.readLine(); //ヘッダの読み飛ばし
            String line;
            JobList.clear();
            int index=0;
            while((line=br.readLine())!=null){
            	String[] strs=line.split(",");
            	String name=strs[0];
            	double[] param = new double[8];
            	double[] levelup = new double[7];
            	String[] skills=new String[6];
            	int first=1;
            	int second=first+param.length;
            	int third=second+levelup.length;
            	for(int i=0;i<param.length;i++){
            		param[i]=Double.parseDouble(strs[i+first]);
            	}
            	for(int i=0;i<levelup.length;i++){
            		levelup[i]=Double.parseDouble(strs[i+second]);
            	}
            	for(int i=0;i<skills.length;i++){
            		skills[i]=strs[i+third];
            	}
            	JobList.add(new Job(name,index,param,levelup,skills));
            	index++;
            }
            br.close();
		}catch(Exception ex){
		    System.exit(0);  //応急処置　TODO　リファクタ
		    ex.printStackTrace();
			System.out.println("脳内パラメーターリストの読み込みエラー");
		}
	}
	
	/** マップリストの作成 */
	public void createMapList(){
		ArrayList<Map> MapList = Map.getMapList();
		MapList.clear();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("Map.txt")));
            String line;
            while((line=br.readLine())!=null){
                int N = Integer.parseInt(line);
                ArrayList<String> list = new ArrayList<String>();
                while((line=br.readLine())!=null){
                    if(line.equals("END"))break;
                    list.add(line);
                }
                char[][] map=new char[list.size()][list.get(0).length()];
                for(int i=0;i<map.length;i++){
                    for(int j=0;j<map[i].length;j++){
                        map[i][j]=list.get(i).charAt(j);
                    }
                }
                MapList.add(new Map(N,map));
            }
            br.close();
		}catch(Exception ex){
		    System.exit(0);  //応急処置　TODO　リファクタ
		    ex.printStackTrace();
			System.out.println("マップ読み込みエラー");
		}
	}
	
	/** マップチップリストの作成 */
	public void createMapChipLIST(){
		ArrayList<MapChip> MC_LIST = MapChip.getMC_LIST();
		MC_LIST.clear();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("NormalMapChip.csv")));
            br.readLine(); //ヘッダの読み飛ばし
            String line;
            while((line=br.readLine())!=null){
            	String strs[]=line.split(",");
            	char C=strs[0].charAt(0);
            	double ds[]=new double[7];
            	for(int i=0;i<ds.length;i++){
            		ds[i]=Double.parseDouble(strs[i+2]);
            	}
            	int I=(int)Integer.parseInt(strs[ds.length+2]);
            	boolean move=(I==1);
            	MC_LIST.add( new MapChip(C,strs[1],ds,move) );
            }
            br.close();
		}catch(Exception ex){
		    System.exit(0);  //応急処置　TODO　リファクタ
		    ex.printStackTrace();
			System.out.println("マップチップ読み込みエラー");
		}
	}
	
	/** SKILL_LISTを作成するメソッド */
	public void createSkillList(){
		ArrayList<Skill> list = Skill.getSKILL_LIST();
		list.clear();
		createNormalSkill(list);
		createRecoverSkill(list);
	}
	
	/** SKILL_LISTに普通のスキルを追加する */
	private void createNormalSkill(ArrayList<Skill> list){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("NormalSkill.csv")));
            br.readLine(); //ヘッダの読み飛ばし
            String line;
            while((line=br.readLine())!=null){
            	String strs[]=line.split(",");
            	double rs[]=new double[4];
            	int start=3;
            	for(int i=0;i<rs.length;i++){
            		rs[i]=Double.parseDouble(strs[i+start]);
            	}
            	int ps[]=new int[7];
            	for(int i=0;i<ps.length;i++){
            		ps[i]=Integer.parseInt(strs[i+rs.length+start]);
            	}
            	ArrayList<String> SL=new ArrayList<String>();
            	int index=ps.length+rs.length+start;
            	while(true){
            		String S=strs[index];
            		index++;
            		if(S.equals("END"))break;
            		SL.add(S);
            	}
            list.add(new Skill(strs[0],strs[1],Integer.parseInt(strs[2]),rs,ps,SL));
            }
            br.close();
		}catch(Exception ex){
		    System.exit(0);  //応急処置　TODO　リファクタ
		    ex.printStackTrace();
		    System.out.println("skillの読み込みに失敗しました");
		}	
	}
	
	/** SKILL_LISTに回復系のスキルを追加する */
	private void createRecoverSkill(ArrayList<Skill> list){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("Recovery.csv")));
            br.readLine(); //ヘッダの読み飛ばし
            String line;
            while((line=br.readLine())!=null){
            	String strs[]=line.split(",");
            	int ps[]=new int[7];
            	int start=3;
            	for(int i=0;i<ps.length;i++){
            		ps[i]=Integer.parseInt(strs[i+start]);
            	}
            	list.add(new Skill(strs[0],strs[1],Integer.parseInt(strs[2]),ps));
            }
            br.close();
		}catch(Exception ex){
		    System.exit(0);  //応急処置　TODO　リファクタ
	        ex.printStackTrace();
	        System.out.println("回復系skillの読み込みに失敗しました");
		}	
	}
	
	/** 脳内パラメーターリストの作成(static)*/
	public void createPrameterList(){
		ArrayList<BrainParameter> ParameterList = BrainParameter.getParameterList();
		try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("BrainParameterList.csv")));
            br.readLine(); //ヘッダの読み飛ばし
            String line;
            ParameterList.clear();
            while((line=br.readLine())!=null){
            	String[] strs=line.split(",");
            	int[] nums=new int[strs.length];
            	for(int i=0;i<nums.length;i++){
            		nums[i]=Integer.parseInt(strs[i]);
            	}
            	ParameterList.add(new BrainParameter(nums));
            }
            br.close();
		}catch(Exception ex){
		    System.exit(0);  //応急処置　TODO　リファクタ
		    ex.printStackTrace();
			System.out.println("脳内パラメーターリストの読み込みエラー");
		}	
	}
	
	   /** 文章クラスリストの作成(static)*/
    public void createSentenceList(){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("SentenceList.csv")));
            String line;
            Sentence.clearList();
            while((line=br.readLine())!=null){
                Sentence sent = new Sentence(line);
                Sentence.setSentence(sent);
            }
            br.close();
        }catch(Exception ex){
            System.exit(0);  //応急処置　TODO　リファクタ
            ex.printStackTrace();
            System.out.println("文章リストの読み込みエラー");
        }   
    }
	
}
