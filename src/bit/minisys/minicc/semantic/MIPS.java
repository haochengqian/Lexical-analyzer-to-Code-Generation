package bit.minisys.minicc.semantic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.naming.spi.DirStateFactory.Result;
import javax.swing.RootPaneContainer;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;

import org.python.antlr.PythonParser.else_clause_return;
import org.python.antlr.PythonParser.if_stmt_return;
import org.python.constantine.platform.darwin.OpenFlags;
import org.python.core.exceptions;
import org.python.tests.Child;

import com.kenai.jaffl.mapper.TypeMapper;

import bit.minisys.minicc.parser.MiniCCParser.RecordTree;

public class MIPS {
	public static boolean createFile(String FileName,String con){
		 try {

			   String content = con;

			   File file = new File(FileName);
			   
			   if (!file.exists()) {
			    file.createNewFile();
			   }

			   FileWriter fw = new FileWriter(file.getAbsoluteFile());
			   BufferedWriter bw = new BufferedWriter(fw);
			   bw.write(content);
			   bw.close();

			   System.out.println("Done");

			  } catch (IOException e) {
			   e.printStackTrace();
			  }
		return false;
	}
	
	public RecordTree easyTrees;
	public String result;
	public String[] MIPSWords= new String[]{"add","addu","sub","subu","and","or","xor","nor","slt"
											,"sltu","sll","srl","sra","sllv","srlv","srav","jr"
											,"l-type","addi","addiu","andi","ori","xori","lui"
											,"lw","sw","beq","bne","slti","sltiu","J-type","j","jal"};
	public String[] Registers = new String[]{"$zero","$at","$v0","$v1","$a0","$a1","$a2","$a3","$t0",
											 "$t1","$t2","$t3","$t4","$t5","$t6","$t7","$s0","$s1","$s2",
											 "$s3","$s4","$s5","$s6","$s7","$t8","$t9","$k0","$k1","$gp",
											 "$sp","$fp","$ra"};
	public int RegisterGlobe = 16;
	public int RegisterTemp = 8;
	public String[] TempRegister = new String[3];
	public String[] TempOper = new String[2];
	public int OperFlag = 0;
	public int TempRegisterNum = 0;
	public int CodeSkipNum = 1;
	public String JumpFucName = "";
	public String[] Non_terminator = new String[]{"PROGRAM","FUNCTIONS","FUNCTION","FLIST","ARGS_DECL","ARGS_NDECL","FUNC_BODY",
			"STMTS","STMT","EXPR_STMT","DEF_STMT","IF_STMT","RET_STMT","CODE_BLOCK","ELSE_STMT","WHILE_STMT","WHILE_BODY","WHILE_BLOCK",
			"EXPR","ETERM","FA_ARGS","ARGS_REF","ARGS_NREF","ARRAY_REF","ARRAY_NREF","ETLIST1","ETLIST1_C","ETLIST2","ETLIST2_C",
			"ETLIST3","ETLIST3_C","ETLIST4","ETLIST4_C","DEF","DTERM","DTLIST","ARRAY_DECL","ARRAY_NDEM","TYPE","CONST","BRE_CONTI",
			"OPTP1","OPTP2","OPTP3","OPTP4"};
	public HashMap<String, String> Re2Identify = new HashMap<>();
	public Set<String> Identify = new HashSet<>();
	public void FunctionName(RecordTree root){
		result += "__" + root.children.get(0).value + ":\r\n";
	}
	public String GetID(RecordTree root){
		if(root.children.size() == 0){
			return root.value;
		}
		else{
			return GetID(root.children.get(0));
		}
	}
	public void GetOp(String arg1,String arg2,String op,int step){
		if(op.equals("Terminator_GE")){
			result += "\t SUB " + Re2Identify.get(arg1) + ","+ Re2Identify.get(arg1) + "," + Re2Identify.get(arg2) + "\r\n"
					+ "\t BGEZ "+ Re2Identify.get(arg1) + "," + "CODE" + String.valueOf(CodeSkipNum) + "\r\n"
					+ "\t J "+ "CODE" + String.valueOf(CodeSkipNum + 1) + "\r\n";
		}
		else if(op.equals("Terminator_LE")){
			result += "\t SUB " + Re2Identify.get(arg1) + ","+ Re2Identify.get(arg1) + "," + Re2Identify.get(arg2) + "\r\n"
					+ "\t BLEZ "+ Re2Identify.get(arg1) + "," + "CODE" + String.valueOf(CodeSkipNum) + "\r\n"
					+ "\t J "+ "CODE" + String.valueOf(CodeSkipNum + 1) + "\r\n";
		}	
		else if(op.equals("Terminator_ASIGN")){
			result += "\t MOVE " + Re2Identify.get(arg1) + "," + Re2Identify.get(arg2) + "\r\n";
			result.length();
		}
		else if(op.equals("Terminator_ADD")){
			result += "\t ADD " + Re2Identify.get(arg1) + "," + Re2Identify.get(arg1) + "," + Re2Identify.get(arg2) + "\r\n";
		}
		else if(op.equals("Terminator_SUB")){
			result += "\t SUB " + Re2Identify.get(arg1) + ","+ Re2Identify.get(arg1) + "," + Re2Identify.get(arg2) + "\r\n";
		}
		else if(op.equals("Terminator_MUL")){
			result += "\t MUL " + Re2Identify.get(arg1) + "," + Re2Identify.get(arg1) + "," + Re2Identify.get(arg2) + "\r\n";
		}
		else if(op.equals("Terminator_DIV")){
			result += "\t DIV " + Re2Identify.get(arg1) + "," + Re2Identify.get(arg2) + "\r\n";
			result += "\t MFLO "+ Re2Identify.get(arg1) + "\r\n";
		}			
		else if(op.equals("Terminator_EQU")){
			result += "\t SUB " + Re2Identify.get(arg1) + ","+ Re2Identify.get(arg1) + "," + Re2Identify.get(arg2) + "\r\n"
					+ "\t BEQZ "+ Re2Identify.get(arg1) + "," + "CODE" + String.valueOf(CodeSkipNum) + "\r\n"
					+ "\t J "+ "CODE" + String.valueOf(CodeSkipNum + 1) + "\r\n";
		}
		else if(op.equals("Terminator_NEQU")){
			result += "\t SUB " + Re2Identify.get(arg1) + ","+ Re2Identify.get(arg1) + "," + Re2Identify.get(arg2) + "\r\n"
					+ "\t BNEZ "+ Re2Identify.get(arg1) + "," + "CODE" + String.valueOf(CodeSkipNum) + "\r\n"
					+ "\t J "+ "CODE" + String.valueOf(CodeSkipNum + 1) + "\r\n";
		}
		else if(op.equals("Terminator_G")){
			result += "\t SUB " + Re2Identify.get(arg1) + ","+ Re2Identify.get(arg1) + "," + Re2Identify.get(arg2) + "\r\n"
					+ "\t BGTZ "+ Re2Identify.get(arg1) + "," + "CODE" + String.valueOf(CodeSkipNum) + "\r\n"
					+ "\t J "+ "CODE" + String.valueOf(CodeSkipNum + 1) + "\r\n";
		}
		else if(op.equals("Terminator_L")){
			result += "\t SUB " + Re2Identify.get(arg1) + ","+ Re2Identify.get(arg1) + "," + Re2Identify.get(arg2) + "\r\n"
					+ "\t BLTZ "+ Re2Identify.get(arg1) + "," + "CODE" + String.valueOf(CodeSkipNum) + "\r\n"
					+ "\t J "+ "CODE" + String.valueOf(CodeSkipNum + 1) + "\r\n";
		}
	}
	public void DistributeReigester(RecordTree root,int flag){
		if(flag == 1){
			Re2Identify.put(root.children.get(0).value, Registers[RegisterGlobe++]);
			return;
		}
		else{
			Re2Identify.put(root.children.get(0).value, Registers[RegisterTemp++]);
			return;
		}
	}
	public void TempVarDecl(RecordTree root){
		for(int i= root.children.size() - 1;i>=0;i--){
			if(root.children.get(i).value.equals("Terminator_ID")){
				DistributeReigester(root.children.get(i),0);
			}
			else if(root.children.get(i).value.equals("ARGS_NDECL")){
				TempVarDecl(root.children.get(i));
			}
		}
	}
	public void DEF_STMT(RecordTree root){
		if(root.children.size() == 1 && root.value.equals("Terminator_ID")){
			DistributeReigester(root, 0);
			return;
		}
		for(int i= root.children.size() - 1;i>=0;i--){
			DEF_STMT(root.children.get(i));
		}
	}
	public void ETLIST(RecordTree root){
		for(int i= root.children.size() - 1;i>=0;i--){
			if(root.children.get(i).value.equals("ETLIST2")){
				ETLIST2(root.children.get(i));
			}
			else if(root.children.get(i).value.equals("ETLIST1_C")){
				ETLIST_C(root.children.get(i), result,2);
			}
		}
	}
	public void ETLIST2(RecordTree root){
		for(int i= root.children.size() - 1;i>=0;i--){
			if(root.children.get(i).value.equals("ETLIST3")){
				ETLIST3(root.children.get(i));
			}
			else if(root.children.get(i).value.equals("ETLIST2_C")){
				ETLIST_C(root.children.get(i), result,3);
			}
		}
	}
	public void ETLIST3(RecordTree root){
		for(int i= root.children.size() - 1;i>=0;i--){
			if(root.children.get(i).value.equals("ETLIST4")){
				ETLIST4(root.children.get(i));
			}
			else if(root.children.get(i).value.equals("ETLIST3_C")){
				ETLIST_C(root.children.get(i), result,4);
			}
		}
	}
	public void ARGS_NREF(RecordTree root){
		for(int i= root.children.size() - 1;i>=0;i--){
			if(root.children.get(i).value.equals("ETLIST1")){
				result += "\t MOVE $a2," + Re2Identify.get(GetID(root)) + "\r\n";
			}
		}
	}
	public void ARGS_REF(RecordTree root){
		for(int i= root.children.size() - 1;i>=0;i--){
			if(root.children.get(i).value.equals("ARGS_NREF")){
				ARGS_NREF(root.children.get(i));
			}
			else{
				result += "\t MOVE $a1," + Re2Identify.get(GetID(root.children.get(i))) + "\r\n";
			}
		}
	}
	public void FA_ARGS(RecordTree root){
		for(int i= root.children.size() - 1;i>=0;i--){
			if(root.children.get(i).value.equals("ARGS_REF")){
				ARGS_REF(root.children.get(i));
			}
		}
	}
	public void ETERM(RecordTree root){
		for(int i= root.children.size() - 1;i>=0;i--){
			if(root.children.size() == 1){
				TempRegister[TempRegisterNum++] = GetID(root);
			}
			if(root.children.get(i).value.equals("FA_ARGS")){
				FA_ARGS(root.children.get(i));
				result += "\t ADDIU $sp,$sp,-256 \r\n"
						+ "\t SW $ra, 0($sp) \r\n"
						+ "\t JAL __" + JumpFucName + "\r\n";		
			}
			if(root.children.get(i).value.equals("Terminator_ID")){
				JumpFucName = GetID(root.children.get(i));
			}
		}
	}
	public void ETLIST4(RecordTree root){
		for(int i= root.children.size() - 1;i>=0;i--){
			if(root.children.get(i).value.equals("ETERM")){
				ETERM(root.children.get(i));
			}
			else if(root.children.get(i).value.equals("Terminator_CONSTI")){
				result += "\t LI " + Registers[RegisterGlobe] + "," + GetID(root.children.get(i)) + "\r\n";
				Re2Identify.put(GetID(root.children.get(i)), Registers[RegisterGlobe++]);
				TempRegister[TempRegisterNum++] = GetID(root.children.get(i));
			}
			else if(root.children.get(i).value.equals("ETLIST4_C")){
				ETLIST_C(root.children.get(i), result, 5);
			}
		}
	}
	public void ETLIST_C(RecordTree root,String result,int depth){
		switch (depth){
		case 2:
			TempOper[OperFlag++] = root.children.get(1).value;
			ETLIST(root);
			break;
		case 3:
			TempOper[OperFlag++] = root.children.get(1).value;
			ETLIST2(root);
			break;
		case 4:
			TempOper[OperFlag++] = root.children.get(1).value;
			ETLIST3(root);
			break;
		case 5:
			TempOper[OperFlag++] = root.children.get(1).value;
			ETLIST4(root);
			break;
		}
	}
	public void EXPR_STMT(RecordTree root){
		if(root.value.equals("EXPR_STMT"))
			ETLIST(root.children.get(0));
		else if(root.value.equals("ETLIST1"))
			ETLIST(root);
		if(OperFlag == 2){
			GetOp(TempRegister[1],TempRegister[2],TempOper[1], 2);
			GetOp(TempRegister[0],TempRegister[1],TempOper[0], 1);
		}
		else if(OperFlag == 1){
			GetOp(TempRegister[0],TempRegister[1],TempOper[0], 1);
		}
		TempRegisterNum = 0;
		OperFlag = 0;
	}
	public void IF_STMT(RecordTree root){
		for(int i= root.children.size() - 1;i>=0;i--){
			if(root.children.get(i).value.equals("ETLIST1")){
				EXPR_STMT(root.children.get(i));
				result += "CODE" + String.valueOf(CodeSkipNum) + ":" + "\r\n";
			}
			if(root.children.get(i).value.equals("FUNC_BODY")){
				FunctionDeel(root.children.get(i));
				result += "CODE" + String.valueOf(++CodeSkipNum) + ":" + "\r\n";
				CodeSkipNum++;
			}
		}
		
	}
	public void WHILE_BLOCK(RecordTree root){
		for(int i= root.children.size() - 1;i>=0;i--){
			if(root.children.get(i).value.equals("STMTS"))
				STMTS(root.children.get(i));
		}
	}
	public void WHILE_BODY(RecordTree root){
		for(int i= root.children.size() - 1;i>=0;i--){
			if(root.children.get(i).value.equals("WHILE_BLOCK"))
				WHILE_BLOCK(root.children.get(i));
		}
	}
	public void WHILE_STMT(RecordTree root){
		for(int i= root.children.size() - 1;i>=0;i--){
			if(root.children.get(i).value.equals("WHILE_BODY")){
				WHILE_BODY(root.children.get(i));
				result += "CODE" + String.valueOf(++CodeSkipNum) + ":" + "\r\n";
				CodeSkipNum++;
			}
			else if(root.children.get(i).value.equals("ETLIST1")){
//				result += "CODE" + String.valueOf(CodeSkipNum++) + ":" + "\r\n";
				EXPR_STMT(root.children.get(i));
				result += "CODE" + String.valueOf(CodeSkipNum) + ":" + "\r\n";
			}
		}
	}
	public void STMTS(RecordTree root){
		for(int i= root.children.size() - 1;i>=0;i--){
			if(root.children.get(i).value.equals("DEF_STMT")){
				DEF_STMT(root.children.get(i));
			}
			else if(root.children.get(i).value.equals("EXPR_STMT")){
				EXPR_STMT(root.children.get(i));
			}
			else if(root.children.get(i).value.equals("IF_STMT")){
				IF_STMT(root.children.get(i));
			}
			else if(root.children.get(i).value.equals("WHILE_STMT")){
				WHILE_STMT(root.children.get(i));
			}
			else if(root.children.get(i).value.equals("STMTS")){
				STMTS(root.children.get(i));
			}
		}
	}
	public void FunctionDeel(RecordTree root){
		for(int i= root.children.size() - 1;i>=0;i--){
			if(root.children.get(i).value.equals("STMTS")){
				STMTS(root.children.get(i));
			}
		}
	}
	public void Function(RecordTree root){
		for(int i= root.children.size() - 1;i>=0;i--){
			if(root.children.get(i).value.equals("Terminator_ID")){
				FunctionName(root.children.get(i));
			}
			else if(root.children.get(i).value.equals("ARGS_DECL")){
				TempVarDecl(root.children.get(i));
			}
			else if(root.children.get(i).value.equals("FUNC_BODY")){
				FunctionDeel(root.children.get(i));
			}
		}	
	}
	public void Tree2Code(RecordTree root){
		if(root.value.equals("FUNCTIONS")){
			result += "\t\t.data\r\n\r\n\t\t.text\r\n";
			for(int i= root.children.size() - 1;i>=0;i--){
				Tree2Code(root.children.get(i));
			}	
		}
		else if(root.value.equals("FLIST")){
			for(int i= root.children.size() - 1;i>=0;i--){
				Tree2Code(root.children.get(i));
			}	
		}
		else if(root.value.equals("FUNCTION")){
			Function(root);
			result += "\t JR $ra \r\n";
			result += "\r\n";
		}
		
	}
	public void MIPS(String outFile,RecordTree root){
		result = "";
		easyTrees = root;
		Tree2Code(easyTrees.children.get(0));
		createFile(outFile, result);
		System.out.println("MIPS finished!");
	}
}
