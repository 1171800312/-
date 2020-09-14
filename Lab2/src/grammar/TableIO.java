package grammar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TableIO {
	public static void saveObjToFile(LRTable p){
		try {
			//д�������Ķ���
			ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream("LRtable.bin"));
			oos.writeObject(p);                 //��Person����pд�뵽oos��
			oos.close();                        //�ر��ļ���
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/*
	 * ���ļ��ж������󣬲��ҷ���lrtable����
	 */
	public static LRTable getObjFromFile(){
		try {
			ObjectInputStream ois=new ObjectInputStream(new FileInputStream("LRtable.bin"));
			
			LRTable person=(LRTable)ois.readObject();              //��������
			
			return person;                                       //���ض���
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
