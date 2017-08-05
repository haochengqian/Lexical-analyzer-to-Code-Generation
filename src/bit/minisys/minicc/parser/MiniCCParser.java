package bit.minisys.minicc.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import javax.management.RuntimeErrorException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import bit.minisys.minicc.parser.Syntax.Rule;
import bit.minisys.minicc.parser.Syntax.SymbolType;
import bit.minisys.minicc.xmlout.XmlIn;
import bit.minisys.minicc.xmlout.XmlOutParse;

public class MiniCCParser implements IMiniCCParser{
	public static class RecordTree{
		public String value;
		public String father;
		public RecordTree parent;
		public Object content;
		public boolean processed;
		public ArrayList<RecordTree> children;
		public RecordTree(String key){
			value = key;
			parent = null;
			children = new ArrayList<>();
		}
		public void addChild(RecordTree recordTree){
			children.add(recordTree);
		}
		public String toString(){return "[" + value + "]" ;}
	}
	public static RecordTree nextStep;
	public static class SyntaxTable{
		public String A,a;
		public SyntaxTable(String _a,String b){
			A = _a;
			a = b;
		}
		public String toString(){return "[" + A + "," + a + "]";}
		public boolean equals(Object obj){
			if(!(obj instanceof SyntaxTable))
				return super.equals(obj);
			else if(this == obj) return true;
			else{
				SyntaxTable entry = (SyntaxTable)obj;
				return A.equals(entry.A)&&a.equals(entry.a);
			}
		}
		public int hashCode(){
			return this.toString().hashCode();
		}
	}
	
	
	public XmlIn xmlProcess;
	public Syntax syntax = new Syntax();
	public Map<String, HashSet<String>> allFirst = new HashMap<>(),allFollow = new HashMap<>();
	public Map<SyntaxTable, Rule> table;
	public Map<String, Boolean> vis = new HashMap<>();
	public Map<String,HashSet<String>> graph;
	
	void getFirstCollectionOfTN(String nonterm){
		Boolean flag = vis.get(nonterm);
		if(flag != null && flag) {
			return;
		}
		vis.put(nonterm, true);
		
		HashSet<String> first = allFirst.get(nonterm);
		if(first == null) {
			first = new HashSet<>();
		}
		Rule thisRule = syntax.getSyntaxRule(nonterm);
		for(String rightItem : thisRule.right){
			String[] symArr = Rule.getSymbolsAsArray(rightItem);
			String firstSym = symArr[0];
			boolean containsEmpty = true;
			for(String sym : symArr) {
				if(syntax.getSymbolType(sym).equals(SymbolType.TERMINATOR)) {
					first.add(sym);
					containsEmpty = false;
					break;
				} else {
					if(!nonterm.equals(sym)) {
						getFirstCollectionOfTN(sym);
						boolean symContainsEmpty = false;
						for(String i : allFirst.get(sym)) {
							first.add(i);
							if(i.equals(Syntax.EMPTY_STRING))
								symContainsEmpty = true;
						}
						if(!symContainsEmpty) {
							containsEmpty = false;
							break;
						}
					}
				}
			}
			if(containsEmpty) {
				first.add(Syntax.EMPTY_STRING);
			}
		}
		
		allFirst.put(nonterm, first);
	}
	
	HashSet<String> getFirsrCollectionOfString(String str){
		if(str.length() == 0){
			HashSet<String> first = new HashSet<>();
			first.add(syntax.EMPTY_STRING);
			return first;
		}
		String[] syms = Syntax.Rule.getSymbolsAsArray(str);
		HashSet<String> first = new HashSet<>();
		for(int i=0;i<syms.length;i++){
			if(syntax.getSymbolType(syms[i]).equals(SymbolType.TERMINATOR)){
				first.add(syms[i]);
				return first;
			}else{
				HashSet<String> firstNonTerm = allFirst.get(syms[i]);
				if(firstNonTerm.contains(syntax.EMPTY_STRING)){
					first.addAll(firstNonTerm);
					first.remove(Syntax.EMPTY_STRING);
				}
				else{
					first.addAll(firstNonTerm);
					return first;
				}
			}
		}
		first.add(Syntax.EMPTY_STRING);
		return first;
	}
	
	void getAllFirstCollection(){
		for(String nonterm:syntax.vn){
			getFirstCollectionOfTN(nonterm);
		}
	}
	
	void getAllFollowCollection(){
		for(String nonterm : syntax.vn){
			allFollow.put(nonterm, new HashSet<String>());
		}
		allFollow.get(syntax.beginSym).add(syntax.END_CHAR);
		while(true) {
			int ret = getAllFollowCollectionExec();
			if(ret == 0) break;
		}
	}	
	
	int getAllFollowCollectionExec() {
		int ret = 0;
		
		Set<Entry<String, Rule>> entrySet = syntax.rules.entrySet();
		for(Entry<String, Rule> entry : entrySet) {
			Rule rule = entry.getValue();
			String B = rule.left;
			for(String right : rule.right) {
				String[] syms = Rule.getSymbolsAsArray(right);
				for(int i = 0; i < syms.length; i++) {
					String A = syms[i];
					if(syntax.getSymbolType(A).equals(SymbolType.TERMINATOR)) continue;
					String b = "";
					for(int j = i + 1; j < syms.length; j++) b += ("." + syms[j]);
					
					Set<String> firstB = getFirsrCollectionOfString(b);
					for(String s : firstB) {
						if(s.equals(Syntax.EMPTY_STRING)) {
							Set<String> followB = allFollow.get(B);
							Set<String> followA = allFollow.get(A);
							for(String str : followB) {
								if(!followA.contains(str)){
									followA.add(str);
									ret++;
								}
							}
						} else {
							if(!allFollow.get(A).contains(s)){
								allFollow.get(A).add(s);
								ret++;
							}
						}
					}
				}
			}
		}
		return ret;
	}
	public RecordTree lastTree;
	void buildLL1Table() {
		table = new HashMap<SyntaxTable, Rule>();
		for(Iterator<Entry<String, Rule>> it = syntax.rules.entrySet().iterator(); it.hasNext(); ) {
			Entry<String, Rule> entry = it.next();
			String A = entry.getKey();
			ArrayList<String> right = entry.getValue().right;
			boolean hasEmptyString = false;
			for(String rightItem : right) {
				Set<String> fs = getFirsrCollectionOfString(rightItem);
				for(String s : fs) {
					SyntaxTable tableEntry;
					Rule newRule;
					if(s.equals(Syntax.EMPTY_STRING)) {
						tableEntry = new SyntaxTable(A, Syntax.END_CHAR);
						newRule = new Rule(A, rightItem);
						hasEmptyString = true;
					} else {
						tableEntry = new SyntaxTable(A, s);
						newRule = new Rule(A, rightItem);
						if(table.containsKey(tableEntry) && !table.get(tableEntry).equals(newRule)) {
							
						} else {
							table.put(tableEntry, newRule);
						}
					}
				}
				if(hasEmptyString) {
					Set<String> followA = allFollow.get(A);
					for(String s : followA) {
						SyntaxTable tableEntry = new SyntaxTable(A, s);
						Rule newRule = new Rule(A, Syntax.EMPTY_STRING);
						if(table.containsKey(tableEntry) && !table.get(tableEntry).equals(newRule)) {
		
						} else {
							table.put(tableEntry, newRule);
						}
					}
				}
			}
		}
	}
	public void readFile(String iFile) throws Exception{
		xmlProcess = new XmlIn();
		xmlProcess.readXML(iFile);
		syntax.read();
		getAllFirstCollection();
		getAllFollowCollection();
		buildLL1Table();
	}
	public void GetAnwser(String fileOut) throws ParserConfigurationException, TransformerException{
		Stack<String> stack = new Stack<String>();
		Stack<RecordTree> trees = new Stack<RecordTree>();
		RecordTree root = new RecordTree(syntax.beginSym);
		trees.push(root);
		stack.push(Syntax.END_CHAR);
		stack.push(syntax.beginSym);
		int p = 0;
		boolean success = false;
		while(!stack.isEmpty()){
			String top = stack.peek();
			String tempValue = xmlProcess.value.get(p);
			String tempProp = xmlProcess.type.get(p);
			String thisWord = "";
			if(tempProp.equals("identifier")) thisWord = "Terminator_ID";
			else if(tempProp.equals("keyword")||tempProp.equals("separator")||tempProp.equals("operator")) 
				thisWord = syntax.trans.get(tempValue);
			else if(tempProp.equals("const")) thisWord = "Terminator_CONSTI";
			else if(tempProp.equals("#")) thisWord = "#";
			if(top.equals(Syntax.END_CHAR)||syntax.getSymbolType(top).equals(SymbolType.TERMINATOR)){
				if(top.equals(Syntax.END_CHAR)&&thisWord.equals(Syntax.END_CHAR)){
					success = true;
					break;
				}
				if(top.equals(thisWord)){
					p++;
					stack.pop();
					RecordTree tree = trees.pop();
					RecordTree child = new RecordTree(tempValue);
					child.father = tree.value;
					tree.addChild(child);
					//add tree
				}else{
					System.out.println("Error!");
					throw new RuntimeErrorException(null, "error!");
				}
			}else{
				SyntaxTable entry = new SyntaxTable(top, thisWord);
				Rule rule = table.get(entry);
				if(rule!=null){
					String right = rule.right.get(0);
					stack.pop();
					RecordTree rTree = trees.pop();
					if(!right.equals(Syntax.EMPTY_STRING)){
						String[] syms = Rule.getSymbolsAsArray(right);
						for(int i = syms.length - 1;i >= 0;i--){
							stack.push(syms[i]);
							RecordTree child = new RecordTree(syms[i]);
							child.father = "@";
							rTree.addChild(child);
							trees.push(child);
						}
					}
				}
				else{
					System.out.println("Error!");
					throw new RuntimeErrorException(null);
				}
			}
		}
		this.lastTree = root;
		nextStep = root;
		XmlOutParse xmlOutParse = new XmlOutParse();
		xmlOutParse.root = this.lastTree;
		xmlOutParse.out(fileOut);
 	}
	@Override
	public void run(String iFile, String oFile) {
		try {
			readFile(iFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			GetAnwser(oFile);
		} catch (ParserConfigurationException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("3. Parser finished!");
	}

}
