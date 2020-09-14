package main;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.*;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.awt.event.ActionEvent;
import javax.swing.table.DefaultTableModel;

import grammar.SyntaxParser;
import grammar.Pretreat;

import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class Gui extends JFrame
{
	public static int iscached = 1;
	//����Ϊ1������ļ����ж�ȡ����������Ϊ0�����½��������½�������ʱ�������н����������ĵȴ���
	private static String file_name = "test2.txt";
	//�˱����Ƕ����ļ������ƣ����Ը�����Ҫ�޸ġ�
	private JTable table;
	public Gui()
	{
		setTitle("Grammar analysis by A25"); 
		setSize(760,760);    //���ô�����ʾ�ߴ�
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);//���þ��Բ���
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(15, 25, 300,600);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane);
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setBounds(15, 650, 300,50);
		scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane2);
		JTextArea textArea2 = new JTextArea();
		scrollPane2.setViewportView(textArea2);
		JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane1.setToolTipText("");
		scrollPane1.setBounds(344, 25, 400, 600);
		scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane1);
		String[] name1 = new String[] {"Grammar Tree"};
		JTable table1 = new JTable(new DefaultTableModel(new Object[][] {}, name1));
		table1.setFillsViewportHeight(true);
		scrollPane1.setViewportView(table1);
		JButton button1 = new JButton("open");
		button1.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
					try
					{
						file_name = textArea2.getText();
						FileReader reader = new FileReader(file_name);
			            BufferedReader br = new BufferedReader(reader);
			            String line;
			            textArea.setText(" ");
			            while ((line = br.readLine()) != null) 
			            {
							textArea.append(line);
							textArea.append("\n");
			            }
						reader.close();
	                    for (String k : grammar.Pretreat.firstMap.keySet())
	                    {
	                    	TreeSet<String> v = grammar.Pretreat.firstMap.get(k);
	                    	System.out.println("first("+k+")"+"="+v);
	                    }
					}
					catch(Exception event)
					{
						event.printStackTrace();
					}
			}
		});
		button1.setBounds(340, 640, 190, 50);
		getContentPane().add(button1);
		JButton button3 = new JButton("analysis");
		button3.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{		
				List<String> result2 = new ArrayList<String>(); 
				List<String> table2 = new ArrayList<String>(); 
				List<String> errors = new ArrayList<String>(); 
				SyntaxParser se = new SyntaxParser(file_name,result2,errors,iscached);//0��ʾ�½�������
				DefaultTableModel model1 = new DefaultTableModel(gui_table(result2),name1);
				table1.setModel(model1);
				if (table1.getRowCount() == 0)
				{
					JOptionPane.showMessageDialog(null, "open file first", "Warning", JOptionPane.DEFAULT_OPTION);
				}		
			}
		});
		button3.setBounds(540, 640, 190, 50);
		getContentPane().add(button3);
		setVisible(true); 
	}
	public static Object[][] gui_table(List<String> e)
	{
		int le = e.size();
		Object[][] t = new Object[le][1];
		for (int i=0; i<le; i++)
		{
			t[i][0] = e.get(i);
		}	
		return t;		
	}
    public static void main(String[] agrs)
    {    	
        new Gui();    //����һ��ʵ��������
    }
}