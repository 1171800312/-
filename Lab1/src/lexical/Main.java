package lexical;

import java.util.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class Main
{
	private String text;  // ����Ĳ��������ı�
	private JTable jtable1;  // ����-Token-�ֱ���-�������
	private JTable jtable2;  // ����-��������-������Ϣ
	public Main(String text, JTable jtable1, JTable jtable2)
	{
		this.text = text;
		this.jtable1 = jtable1;
		this.jtable2 = jtable2;
	}
	public static int symbol_pos = 0;  // ��¼���ű�λ��
	public static Map<String, Integer> symbol = new HashMap<String, Integer>();  // ���ű�HashMap
	
	public static int constant_pos = 0;  // ��¼����λ��
	public static Map<String, Integer> constant = new HashMap<String, Integer>();  // ������HashMap
	public void lex()
	{
		String[] texts = text.split("\n");
		//���зָ�
		symbol.clear();
		//������ű�
		symbol_pos = 0;
		constant.clear();
		//���������
		constant_pos = 0;
		for(int m = 0; m < texts.length; m++)
		{
			String str = texts[m];
			if (str.equals(""))
				continue;
			//���Կ���
			else 
			{
				char[] strline = str.toCharArray();
				for(int i = 0; i < strline.length; i++) 
				{
					//����ַ��ķ�������
					char ch = strline[i];
					if (ch == ' ')
						continue;	
					
					String token = "";  
					
					if (util.isAlpha(ch)) // ʶ��ؼ��ֺͱ�ʶ��  
                    {  
                        do 
                        {  
                            token += ch;  
                            i++;  
                            if(i >= strline.length) 
                            	break;  
                            ch = strline[i];  
                        } while (ch != '\0' && (util.isAlpha(ch) || util.isDigit(ch)));  
                        i--; 
                        if (util.isKeyword(token))  // ʶ��ؼ���
                        {  
                            DefaultTableModel tableModel = (DefaultTableModel)jtable1.getModel();
                            tableModel.addRow(new Object[] {m+1, token, token.toUpperCase(), "-"});
                            jtable1.invalidate();
                        }                      
                        else  // ʶ���ʶ��
                        {
                        	if (symbol.isEmpty() || (!symbol.isEmpty() && !symbol.containsKey(token))) 
                        		//��ǰʶ�𵽵�tokenû���ظ�����
                        	{  
                                symbol.put(token, symbol_pos);   
                                symbol_pos++;
                            }
                        	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {m+1, token, "IDN", token});
                            jtable1.invalidate();
                        }
                        token = "";
                    }
					
					else if(util.isDigit(ch))  // ʶ���޷�����
					{
						int state = 1;
						int k;
                        Boolean isfloat = false;  
                        Boolean isSci_not = false;  
                        while ( (ch != '\0') && (util.isDigit(ch) || ch == '.' || ch == 'e' 
                        		|| ch == '-' || ch == 'E' || ch == '+'))
                        {
                        	if (ch == '.') 
                        		isfloat = true;
                        	if (ch == 'e' || ch == 'E')  
                        	{
                        		isfloat = false;
                        		isSci_not = true;
                        	}
                        	
                            for (k = 0; k <= 6; k++) 
                            {                             	
                                char tmpstr[] = util.digitDFA[state].toCharArray(); 
                                //���ݵ�ǰ״̬����DFAת����
                                if (ch != '#' && util.is_digit_state(ch, tmpstr[k]) == 1) 
                                {  
                                    token += ch;  
                                    state = k;  
                                    break;  
                                }  
                            }
                            if (k > 6) 
                            	break;
                            i++;
                            if (i >= strline.length) 
                            	break;  
                            ch = strline[i]; 
                        }         
                        Boolean haveMistake = false;  
                        if (state == 2 || state == 4 || state == 5)  // ����̬
                        {  
                            haveMistake = true;  
                        }                     
                        else  // �޷�������������ķ��Ŵ���
                        {  
                            if ((ch == '.') || (!util.isOperator(String.valueOf(ch)) 
                            		&& !util.isDigit(ch) && !util.isDelimiter(String.valueOf(ch))
                            		&& ch != ' ')) 
                                haveMistake = true;  
                        }  
                        if (haveMistake)   // �����������ֱ�Ӷ�ȡ����һ�����
                        {  
                        	while (ch != '\0' && ch != ',' && ch != ';' && ch != ' ')
                            {  
                                token += ch;  
                                i++;
                                if (i >= strline.length) 
                                	break;  
                                ch = strline[i];  
                            }  
                        	DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                            tableModel2.addRow(new Object[] {m+1, token, "unsigned int error","ERROR"});
                            jtable2.invalidate();
                        }
                        else 
                        {  
                        	if (constant.isEmpty() || (!constant.isEmpty() && !constant.containsKey(token))) 
                        	{  
                        		constant.put(token, constant_pos);   
                                constant_pos++;
                            }
                        	if (isSci_not)
                        	{  
                            	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                                tableModel1.addRow(new Object[] {m+1, token, "SCONST", token});
                                jtable1.invalidate();    
                            } 
                        	else if (isfloat) 
                            {  
                            	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                                tableModel1.addRow(new Object[] {m+1, token, "FCONST", token});
                                jtable1.invalidate();    
                            } 
                            else
                            {   
                            	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                                tableModel1.addRow(new Object[] {m+1, token, "CONST", token});
                                jtable1.invalidate();   
                            }  
                        }
                        i--;
                        token = "";
                    }
					else if(ch == '\'')  // ʶ���ַ�����
					{
						int state = 0;				        
                        token += ch;                    
                        while (state != 3) 
                        {  
                            i++;
                            if (i >= strline.length) 
                            	break;
                            ch = strline[i]; 
                            Boolean flag = false;
                            for (int k = 0; k < 4; k++) 
                            {  
                                char tmpstr[] = util.charDFA[state].toCharArray();  
                                if (util.is_char_state(ch, tmpstr[k])) 
                                {            
                                    token += ch;
                                    state = k; 
                                    flag = true;
                                    break;  
                                }  
                            }  
                            if (flag == false)
                            	break;
                        }
                        if (state != 3) 
                        {  
                        	DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                            tableModel2.addRow(new Object[] {m+1, token, "String error","ERROR"});
                            jtable2.invalidate();
                            i--;  
                        } 
                        else 
                        {  
                        	if (constant.isEmpty() || (!constant.isEmpty() && !constant.containsKey(token))) 
                        	{  
                        		constant.put(token, constant_pos);   
                                constant_pos++;
                            }
                        	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {m+1, token, "CCONST", token});
                            jtable1.invalidate(); 
                        }
                        token = "";
					}
					else if (ch == '"')  // ʶ���ַ�������
					{
						Boolean haveMistake = false;
						String str1 = "";  
						str1 += ch;  
                        int state = 0;  
                        while (state != 3) 
                        {  
                            i++;                             
                            if (i>=strline.length-1) 
                            {  
                                haveMistake = true;  
                                break;  
                            }                              
                            ch = strline[i]; 
                            if (ch == '\0') 
                            {  
                                haveMistake = true;  
                                break;  
                            }
                            for (int k = 0; k < 4; k++) 
                            {  
                                char tmpstr[] = util.stringDFA[state].toCharArray();  
                                if (util.is_string_state(ch, tmpstr[k])) 
                                {  
                                	str1 += ch;  
                                    if (k == 2 && state == 1)  // ת���ַ�  
                                    {  
                                        if (util.isEsSt(ch))
                                            token = token + '\\' + ch;  
                                        else  
                                            token += ch;  
                                    } 
                                    else if (k != 3 && k != 1)  
                                        token += ch;  
                                    state = k;  
                                    break;  
                                }  
                            }  
                        }
                        if (haveMistake) 
                        {   
                        	DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                            tableModel2.addRow(new Object[] {m+1, str1, "String error"});
                            jtable2.invalidate();  
                            i--;  
                        } 
                        else 
                        {  
                        	if (constant.isEmpty() || (!constant.isEmpty() && !constant.containsKey(token))) 
                        	{  
                        		constant.put(token, constant_pos);   
                                constant_pos++;
                            }
                        	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {m+1, str1, "STRCONST", str1});
                            jtable1.invalidate();  
                        }  
                        token = "";		
					}
					else if (ch == '/')  //  ʶ��ע��//
					{
						token += ch;  
                        i++;
                        if (i>=strline.length) 
                        	break;  
                        ch = strline[i];
                        
						//���Ƕ���ע�ͼ�����ע��
                        if (ch != '*' && ch != '/')   
                        {  
                            if (ch == '=')  
                                token += ch; // /=  
                            else 
                            {  
                                i--; // ָ����� 
                            }  
                            DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {m+1, token, "OP", token});
                            jtable1.invalidate();    
                            token = "";  
                        }
                        // ע�Ϳ����ǡ�//��Ҳ�����ǡ�/*��
                        else 
                        {
                        	Boolean haveMistake = false;
                        	int State = 0;
                        	if (ch == '*') 
                        	{  
                        		// ch == '*'
                        		token += ch;  
                                int state = 2;  

                                while (state != 4) 
                                {                                      
                                    if (i == strline.length-1) 
                                    {  
                                    	token += '\n';  
                                    	m++;
                                    	if (m >= texts.length)
                                    	{
                                    		haveMistake = true;  
                                            break;  
                                    	}
                                		str = texts[m];
                                		if (str.equals(""))
                                			continue;
                                		else 
                                		{
                                			strline = str.toCharArray();
                                			i=0;
                                			ch = strline[i];
                                		}
                                    }  
                                    else
                                    {
                                    	i++;
	                                    ch = strline[i];
                                    }
                               
                                    for (int k = 2; k <= 4; k++) 
                                    {  
                                        char tmpstr[] = util.noteDFA[state].toCharArray();  
                                        if (util.is_note_state(ch, tmpstr[k], state)) 
                                        {  
                                            token += ch;  
                                            state = k;  
                                            break;  
                                        }  
                                    }  
                                }
                                State = state;
                            }
                        	else if(ch == '/')
                        	{
                        		//����ע�Ͷ�ȡ�����ַ�
                        		int index = str.lastIndexOf("//");  
                                
                                String tmpstr = str.substring(index);  
                                int tmpint = tmpstr.length();  
                                for(int k=0;k<tmpint;k++)                                     
                                  i++;    
                                token = tmpstr;
                                State = 4;
                        	}
                        	if(haveMistake || State != 4)
                        	{
                        		DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                                tableModel2.addRow(new Object[] {m+1, token, "Note error","ERROR"});
                                jtable2.invalidate();  
                                --i;
                        	}
                        	else
                        	{
                        		DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                                tableModel1.addRow(new Object[] {m+1, token, "NOTE", "-"});
                                jtable1.invalidate();
                        	}
                        	token = "";
                        }	
					}
					else if (util.isOperator(String.valueOf(ch)) || util.isDelimiter(String.valueOf(ch)))  // ������ͽ��
                    {  
						token += ch; 						
                        if (util.isPlusEqu(ch))  // ���������һ��"="
                        {  
                            i++;
                            if (i>=strline.length) 
                            	break;  
                            ch = strline[i];  
                            if (ch == '=')  
                                token += ch;  
                            else 
                            {                              	
                            	if (util.isPlusSame(strline[i-1]) && ch == strline[i-1])  // ���������һ�����Լ�һ����
                                    token += ch;  
                                else  
                                    i--;   
                            }  
                        }                  
                        if(token.length() == 1)  //�ж��Ƿ�Ϊ���
                        {
                        	String signal = token;
                        	if(util.isDelimiter(signal))
                        	{
                        		DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                                tableModel1.addRow(new Object[] {m+1, token,util.getName(token), "-"});
                                jtable1.invalidate();
                        	}                        
                        	else
                        	{
                        		DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                                tableModel1.addRow(new Object[] {m+1, token, "OP", token});
                                jtable1.invalidate();
                        	}
                        }
                        else
                        {
                        	DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
                            tableModel1.addRow(new Object[] {m+1, token, "OP", token});
                            jtable1.invalidate();
                        }                        
                        token = "";		
                    }
					else  //���Ϸ��ַ�
                    {  
                        if(ch != ' ' && ch != '\t' && ch != '\0' && ch != '\n' && ch != '\r')  
                        {                         	
                        	DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                            tableModel2.addRow(new Object[] {m+1, token, "Unknown char"});
                            jtable2.invalidate();
                            System.out.println(ch);
                        }  
                    }				
				}
			} 
		}
    }
}
