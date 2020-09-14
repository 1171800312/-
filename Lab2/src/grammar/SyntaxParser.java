package grammar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import grammar.TableIO;
import main.Gui;


public class SyntaxParser 
{
	private Lexical lex;  // ʵ��1���еĴʷ�����
	private ArrayList<TokenNode> tokenList = new ArrayList<TokenNode>();  // �Ӵʷ���������õ�����token
	private int length;  // tokenlist�ĳ���
	private int index;  // �﷨�������е���λ��
	private LRTable table;  //������﷨������
	private Stack<Integer> stateStack;  //���ڴ洢��Ӧ��DFA״̬��
	private Stack<TokenNode> tokenStack;
	private static List<String> tree = new ArrayList<String>();
	private static List<String> errors = new ArrayList<String>();  // �����Լ���
	public static int indent = 0;//��ǰ�������ֵ
	
	//���캯��,ָ���������λ��
	public SyntaxParser(String filename, List<String> tree, List<String> errors,int cached)
	{
		this.lex = new grammar.Lexical(readfile(filename), this.tokenList);
		this.lex.analyze();  // �ʷ�����
		int last = tokenList.get(tokenList.size()-1).line + 1;
		this.tokenList.add(new TokenNode(last,"#",-1));	//���һ�м���һ��#
		this.length = this.tokenList.size();
		
		this.index = 0;
		if(cached == 0)
		{
			this.table = new LRTable();  // ���ɷ�����
		   grammar.TableIO.saveObjToFile(this.table);
		}
		else
		{
		    this.table = grammar.TableIO.getObjFromFile();
		}
		this.stateStack = new Stack<Integer>();  // ״̬ջ
		this.stateStack.push(0);  // ��ʼΪ0״̬
		this.tokenStack = new Stack<TokenNode>();
		this.tokenStack.push(new TokenNode(-1,"#",-1));//˳�����кţ�ֵ���ֱ���
		
		this.table.dfa.writefile();  // д���ļ�"DFA_state_set.txt"		
		this.table.print();  // д���ļ�"LR_analysis_table.txt"
		SyntaxParser.tree = tree;
		SyntaxParser.errors = errors;
		TokenNode root = analyze();
        printTree(root);
	}
	
	/**
	 * ��������ļ��������ַ���
	 * @param filename �ļ���
	 * @return �ļ�����
	 */
	
	public static String readfile(String filename)
	{
		StringBuffer result = new StringBuffer();
		File file = new File(filename);
		try
		{			
			InputStream in = new FileInputStream(file);
			int tempbyte;
			while ((tempbyte=in.read()) != -1) 
			{
				result.append(""+(char)tempbyte);
			}
			in.close();
		}
		catch(Exception event)
		{
			event.printStackTrace();
		}
		return result.toString();
	}
	
	public void printTree(TokenNode root)
	{
		String output = "";
		for(int i=1;i<=this.indent;i++)
			output+=" ";
		if(root.code==1)
			tree.add(output+"id: "+root.value+" ("+root.line+")");
		else if(root.code==2||root.code==3||root.code==4)
			tree.add(output+"digit: "+root.value+" ("+root.line+")");
		else
		    tree.add(output+root.value+" ("+root.line+")");
		indent=indent+2;
		Collections.reverse(root.children);
		for (TokenNode child:root.children)
			printTree(child);
		indent=indent-2;
	}
	public TokenNode readToken()
	{
		if(index < length)
		{
			return tokenList.get(index++);
		} 
		else 
		{
			return null;
		}
	}
	
	/**
	 * �����ֱ����Ӧ���ķ�����
	 * @param valueType
	 * @return
	 */
	private String getValue(TokenNode valueType)
	{
		try
		{
			int code = valueType.code;
			if(code == 1)
				return "id";
			else if(code == 2)
				return "num";
			else if(code < 400 && code >=101)
				return valueType.value;
			else if(valueType.value.equals("#"))
				return "#";
			else
				return " ";
		}
		catch(Exception NullPointerException)
		{
			return "";	
		}
	}
	
	/**
	 * ���岿�� �﷨����
	 * @return 
	 */
	public TokenNode analyze()
	{
		while(true)
		{			
			TokenNode tokenNode = readToken();
			String value = getValue(tokenNode);
			if(value.equals(" "))
				continue;
	
			int state = stateStack.lastElement();
			String action = table.ACTION(state, value);	//��action��
			//System.out.println(action);
			if(action.startsWith("s"))
			{
				int newState = Integer.parseInt(action.substring(1));
				stateStack.push(newState);
				tokenStack.push(tokenNode);
			} 
			else if(action.startsWith("r"))
			{
				Production derivation = Pretreat.F.get(Integer.parseInt(action.substring(1)));
				//���Ҷ�Ӧ�Ĳ���ʽ������ʽ�������󲿺��Ҳ�����
				System.out.println(derivation);
				int r = derivation.list.size();
				index--;
				TokenNode temptoken = new TokenNode(tokenNode.line,derivation.left,-10);
				if(!derivation.list.get(0).equals("��"))
				{
					for(int i = 0;i < r;i++)
					{
						stateStack.pop();
						TokenNode tobeinserted = tokenStack.pop();
						if(tobeinserted.line<temptoken.line)
							temptoken.line = tobeinserted.line;
						temptoken.children.add(tobeinserted);
					}
				}
				int s = table.GOTO(stateStack.lastElement(), derivation.left);
				stateStack.push(s);
				tokenStack.push(temptoken);
			} 
			else if(action.equals("acc"))
			{
				System.out.print("Accepted"+"\t");
				return tokenStack.get(1);//�����﷨���ĸ��ڵ�
			} 
			else 
			{
				error();
				while(action.startsWith("r"))
				{
					index = index - 1;
					TokenNode token1 = readToken();
					tokenList.remove(token1);
					index = index - 1;
					String value1 = getValue(token1);
					stateStack.pop();
					tokenStack.pop();
					if(value.equals(""))
					{
						error();
						continue;
					}
					if(value.equals(" "))
						continue;
					
					int state1 = stateStack.lastElement();
					action = table.ACTION(state1, value1);				
				}
			}	
		}
	}
	/**
	 * ����
	 */
	public void error()
	{
		String s = "Error at Line[" + tokenList.get(index-1).line + "]:  \""+
				tokenList.get(index-1).value + "\" found an error";
		errors.add(s);
		System.out.println(s);
	}
	
	/**
	 * ������
	 */
	private static void writefile(StringBuffer str)
	{
        String path = "LR_Analysis_Result.txt";
        try 
        {
            File file = new File(path);
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(str.toString()); 
            bw.close(); 
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
	}
	
	
	/**
	 * ��ӡ��������ĳ���
	 */
	private void printInput()
	{
		String output = "";
		for(int i = index;i < tokenList.size();i++)
		{
			output += tokenList.get(i).value;
			output += " ";
		}
		System.out.print(output);
//		result.append(output);
	}
	
}
