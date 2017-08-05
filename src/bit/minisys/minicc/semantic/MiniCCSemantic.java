package bit.minisys.minicc.semantic;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import bit.minisys.minicc.parser.MiniCCParser;
import bit.minisys.minicc.parser.MiniCCParser.RecordTree;
import bit.minisys.minicc.xmlout.XmlOutParse;
import bit.minisys.minicc.xmlout.XmlOutSemantic;

public class MiniCCSemantic {
	public RecordTree root = MiniCCParser.nextStep;
	public RecordTree cutRoot,cutRootn;
	public RecordTree FindChild(RecordTree root){
		if(root.children.size() == 0){
//			System.out.println(root.value);
			if(!root.father.equals("@")){
				if(!root.value.equals(";")){
					RecordTree newRoot = new RecordTree(root.value);
					newRoot.father = root.father;
//					System.out.println(root.value);
					return newRoot;
				}
				return null;
			}
			else{
				return null;
			}
		}
		else{
			if(root.children.size() == 1 && !root.value.contains("Terminator")){
//				RecordTree newRoot = new RecordTree(root.value);
				RecordTree flag = FindChild(root.children.get(0));
				return flag;
			}
			else{
				RecordTree flag = null;
				RecordTree newRoot = new RecordTree(root.value);
				newRoot.father = root.father;
				for(int i = 0; i<root.children.size();i++){
					flag = FindChild(root.children.get(i));
					if(flag != null){
						newRoot.addChild(flag);
					}
				}
				if(flag != null){
					return newRoot;
				}
				return null;
			}
		}
	}
	public void run(String inFile,String outFile) throws ParserConfigurationException, TransformerException{
		cutRootn = new RecordTree(root.value);
		cutRootn.father = root.father;
		cutRootn.addChild(FindChild(root));
		XmlOutSemantic xmlOutSemantic = new XmlOutSemantic();
		xmlOutSemantic.root = cutRootn;
		xmlOutSemantic.out(outFile);
		MIPS mips = new MIPS();
		mips.easyTrees = cutRoot;
		mips.MIPS(outFile+".mips",cutRootn);
		System.out.println("4. Semantic finished!");
	}
}
