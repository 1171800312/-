package grammar;

public class test {
	
	public static void main(String args[]) 
	{
       Production prod = new Production("A -> B C D");
       System.out.println("����ʽ�󲿣�"+prod.left);
       System.out.println("����ʽ�Ҳ���"+prod.list);
       LRItem item = new LRItem(prod,"a",2);
       System.out.println("һ��LR��1����Ŀ��"+item);
       LRState state = new LRState(0);
       state.addNewDerivation(item);
       System.out.println("һ��LR��1���Զ�����״̬��"+state);
       System.out.println("������з��ţ�"+state.getGotoPath());
	}
}
