package bit.minisys.minicc.scanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import bit.minisys.minicc.xmlout.*;

public class MiniCCScanner implements IMiniCCScanner{
	private XmlOut fileout = new XmlOut();
	private static final int START = 1;
	private static final int NUM = 2;
	private static final int ID = 3;
	private static final int EQ = 4;
	private static final int NE = 5;
	private static final int NM = 6;
	private static final int NL = 7;
	private static final int COMS = 8;
	private static final int LINECOMS = 9;
	private static final int MULCOM1 = 10;
	private static final int MULCOM2 = 11;
	private static final int SPECIAL = 12;
	private static final int DONE = 13;
	private static final int OX = 15;//十六进制
	private static final int OXTHEN = 16;//十六进制0x后
	private static final int NUMFLOAT = 17;//float
	private static final int NUME = 18;// 指数
	private static final int FU = 19;// -
	private static final int SPECIAL2 = 20;
	private static final int SPECIAL3 = 21;
	private static final int TWOALTH = 22;
	private static final int AND = 23;
	private static final int OR = 24;
	private static final int FU2 = 25;

	
	private String[] KeyWords = new String[]{
			"auto","if","unsigned","break","inline","void","case","int","volatile","char","long",
			"while","const","register","_Alignas","continue","restrict","_Alignof","default","return",
			"_Atomic","do","short","_Bool","double","signed","_Complex","else","sizeof","_Generic","enum",
			"static","_Imaginary","extern","struct","_Noreturn","float","switch","_Static_assert","for","typedef",
			"_Thread_local","goto","union"
	}; 	// key
	
	private String[] Special = {"{","}","[","]","(",")","#",",",".",";",":"}; // 特殊字符
	
	private String[] Arithemic = {"+","-","-","^","~","&","|","&&","&=","|=","||","?","/","%",":","\\","'","\"",">>"
			,"<<","!=","=","==","<=",">=","++","--","*","<",">"}; //算数运算符

	private BufferedReader sourceFile;
	
	private static final int BUF_SIZE = 1024*1024; // 行内最大字符数
	private int bufSize = 0;
	private String eachLine;
	private char[] LineBuf = new char[BUF_SIZE];
	private int lineNum = 0;//当前行数
	private int charPos = 0;//当前行字符下表
	private boolean isEOF = false;//行尾？
	
	public MiniCCScanner() {}
	
	private char getNextChar() throws Exception{
		try{	//忽略空行错误
			char nextChar = 0;
			if(charPos>=bufSize){
				if((eachLine = sourceFile.readLine())!= null){
					lineNum++;
					LineBuf = eachLine.toCharArray();
					bufSize = eachLine.length();
					charPos = 0;
					nextChar = LineBuf[charPos++];
				}
				else{
					isEOF = true;
					nextChar = '@';
				}
			}
			else{
				nextChar = LineBuf[charPos++];
			}
			return nextChar;
			}catch (Exception e) {
		//		System.out.println(charPos);
			// TODO: handle exception
			}
			return 0;
	}
	
	private boolean isDec(char c){
		if(('a'<=c&&c<='f') || ('A'<=c && c<='F'))
			return true;
		return false;
	}
	
	private boolean isLetter(char c){
		if(('a'<=c && c<='z') || ('A'<=c && c<= 'Z') || c == '_'){
			return true;
		}
		return false;
	}
	
	private boolean isSeparator(String c){
		if(isSpecial(c)){
			return true;
		}
		return false;
	}
	
	private boolean isDigit(char c){
		if('0'<=c && c<='9'){
			return true;
		}
		return false;
	}
	
	private boolean isTwoArith(char c){
		if(c == '+'|| c == '*' || c == '/'|| c == '%'|| c =='^'||c == '|'){
			return true;
		}
		return false;
	}
	
	private boolean isNum(String token){
		boolean flag = true;
		char [] chs = token.toCharArray();
		int len = chs.length;
		for(int i = 0;i<len;i++){
			if(!isDigit(chs[i]))
				flag = false;
			if( (chs[i] == 'e' || chs[i] == 'E') && i != 0 && isDigit(chs[i - 1]))
				flag = true;
		}
		if(chs[0] == '-'&&isDigit(chs[1])) flag = true;
		return flag;
	}
	
	private boolean isSpecial(String token){
		int len = Special.length;
		for(int i = 0;i<len;i++){
			if(token.equals(Special[i])){
				return true;
			}
		}
		return false;
	}
	
	private boolean isArithmetic(String token){
		int len = Arithemic.length;
		for(int i = 0;i<len;i++){
			if(token.equals(Arithemic[i])){
				return true;
			}
		}
		return false;
	}
	
	private boolean isKeyWord(String token){
		int len = KeyWords.length;
		for(int i = 0;i<len;i++){
			if(token.equals(KeyWords[i])){
				return true;
			}
		}
		return false;
	}
	
	private void GetLastChar(int step){
		if(!isEOF){
			charPos -= step;
		}
	}
	
	private String Scanner() throws Exception{
		String tokenStr = "";
		String currentToken = "";
		int currentState = START;
		boolean isSave = true;
		char c;
		while( (c=getNextChar()) != -1 &&currentState!=DONE&&!isEOF){
			isSave = true;
			switch (currentState) {
			case START:
				if(c == '0'){
					currentState = OX;
				}else if(isDigit(c)){
					currentState = NUM;
				}else if(isLetter(c)){
					currentState = ID;
				}else if(c == ' '|| c == '\t'|| c == '\n'|| c == '\r'){
					isSave = false;
				}else if(c == '!'){
					currentState = NE;
				}else if(c == '='){
					currentState = EQ;
				}else if(c == '-'){
					currentState = FU;
				}else if (c == '<') {
					currentState = NM;
				}else if (c == '>') {
					currentState = NL;
				}else if (c == '/') {
					currentState = COMS;
				}else if(c == '&'){
					currentState = AND;
				}else if(c == '|'){
					currentState = OR;
				}else if (isTwoArith(c)){
					currentState = TWOALTH;
				}
				else {
					currentState = DONE;
				}
				break;
			case AND:
				if(c == '&'){
					currentState = DONE;
				}
				else{
					currentState =  TWOALTH;
					GetLastChar(1);
					isSave = false;
				}
				break;
			case OR:
				if(c == '|'){
					currentState = DONE;
				}
				else{
					currentState =  TWOALTH;
					GetLastChar(1);
					isSave = false;
				}
				break;
			case TWOALTH:
				if(c == '='){
					currentState = DONE;
				}
				else{
					currentState = DONE;
					GetLastChar(1);
					isSave = false;
				}
				break;
			case FU:
				if(!isDigit(c)){
					currentState = FU2;
					GetLastChar(1);
					isSave = false;
				}else{
					currentState = NUM;
				}
				break;
			case FU2:
				if(c == '=' || c=='>'){
					currentState = DONE;
				}
				else{
					currentState = DONE;
					GetLastChar(1);
					isSave = false;
				}
				break;
			case OX:
				if(c == 'x'||c == 'X'){
					currentState = OXTHEN;
				}else{
					currentState = DONE;
					GetLastChar(1);
					isSave = false;
				}
				break;
			case OXTHEN:
				if(!isDigit(c)&&!isDec(c)){
					currentState = DONE;
					GetLastChar(1);
					isSave = false;
				}
				break;
			case NUM:
				if (c == 'l' && c == 'L') {
					currentState = DONE; 
				}else if(c == '.'){
					currentState = NUMFLOAT;
				}else if(c == 'e'|| c =='E'){
					currentState = NUME;
				}else if(!isDigit(c)&&c!='l'&&c!='L'){
					currentState = DONE;
					GetLastChar(1);
					isSave = false;
				}
				break;
			case NUMFLOAT:
				if(!isDigit(c)){
					currentState = DONE;
					GetLastChar(2);
					isSave = false;
				}
				break;
			case NUME:
				if(!isDigit(c)){
					currentState = DONE;
					GetLastChar(2);
					isSave = false;
				}
				break;
			case ID:
				if(!isLetter(c)&&!isDigit(c)){
					currentState = DONE;
					GetLastChar(1);
					isSave = false;
				}
				break;
			case NE:
				if(c != '='){
					currentState = SPECIAL;
					GetLastChar(1);
					isSave = false;
				}
				break;
			case NM:
				if(c == '<'){
					currentState = SPECIAL2;
				}else if(c!='='&&c!='<'){
					currentState = SPECIAL;
					GetLastChar(2);
					isSave = false;
				} 
				else{
					currentState = DONE;
				}
				break;
			case NL:
				if(c == '>'){
					currentState = SPECIAL3;
				}
				if(c!='='&&c!='>'){
					currentState = SPECIAL;
					GetLastChar(2);
					isSave = false;
				}
				else if(c!='='&&c!='>'){
					currentState = SPECIAL;
					GetLastChar(2);
					isSave = false;
				}
				else{
					currentState = DONE;
				}
				break;
			case SPECIAL2:
				if(c!='='){
					currentState = DONE;
					GetLastChar(1);
					isSave = false;
				}
				else{
					currentState = DONE;
				}
				break;
			case SPECIAL3:
				if(c!='='){
					currentState = DONE;
					GetLastChar(1);
					isSave = false;
				}
				else{
					currentState = DONE;
				}
				break;
			case EQ:
				if(c!='='){
					currentState = DONE;
					GetLastChar(1);
					isSave = false;
				}else{
					currentState = DONE;
				}
				break;
			case COMS:
				if(c == '/'){
					isSave = false;
					currentState = LINECOMS;
				}else if (c == '*') {
					isSave = false;
					currentState = MULCOM1;
				}else if (c == '='){
					currentState = DONE;
				}else{
					isSave = false;
					GetLastChar(1);
					currentState = DONE;
				}
				break;
			case LINECOMS:
				isSave = false;
				if(c == '\n'){
					currentState = DONE;
				}
				break;
			case MULCOM1:
				isSave = false;
				if(c == '*'){
					currentState = MULCOM2;
				}
				break;
			case MULCOM2:
				isSave = false;
				if(c == '/'){
					currentState = DONE;
				}else if (c == '*') {
					currentState = MULCOM2;
				}else{
					currentState = MULCOM1;
				}
				break;
			case SPECIAL:
				if(c == '!'|| c =='='||c == '<' || c == '>'){
					currentToken = "" + c;
					currentState = DONE;
					isSave = false;
				}
				else{
					currentToken = "Error";
					currentState = DONE;
				}
				break;
			default:
				currentState = DONE;
				currentToken = "Error";
				break;
			}
			if(isSave){
				tokenStr += c;
			}
			if(currentState == DONE && tokenStr.equals("") == false){
				currentToken = tokenStr;
				fileout.value.add(currentToken);
				fileout.line.add(lineNum);

				if(isKeyWord(currentToken)){
					fileout.type.add("keyword");
				}else if(isSeparator(currentToken)){
					fileout.type.add("separator");
				}else if(isArithmetic(currentToken)){
					fileout.type.add("operator");	
				}else if(isNum(currentToken)){
					fileout.type.add("const");
				}else{
					fileout.type.add("identifier");
				}
				fileout.valid.add("true");
				fileout.tokensNumber++;
			}
		}
		return currentToken;
	}
	@Override
	public void run(String iFile, String oFile) throws IOException, Exception {
		// TODO Auto-generated method stub
		this.sourceFile = new BufferedReader(new FileReader(iFile));
		while(!isEOF){
			Scanner();
		}
		fileout.tokensNumber++;
		fileout.line.add(lineNum + 1);
		fileout.value.add("#");
		fileout.valid.add("true");
		fileout.type.add("#");
		fileout.printXml(iFile,oFile);
		System.out.println("2. Scanner finished!");
	}
	
}