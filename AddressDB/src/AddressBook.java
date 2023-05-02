import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


/* ���α׷� �̸� : AddressBook.java
���α׷� ���� : Ŭ���� AddressBook ���� ���α׷�
�ۼ��� : 2020-11-20
�ۼ��� : ����Ʈ�������� ��ƶ�*/

public class AddressBook{
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	PreparedStatement pstmt = null;
	
	public AddressBook(Connection conn) throws Exception {//UI���� ������ ����� �� �����ͼ� ��ü ���� (UI ���� ���� ó�� ������ ��)	

		 stmt = conn.createStatement();	//Statement ���⼭ �ѹ��� �����
		 
         //���̺��� ���ٸ� ���̺� ����	     
	     String tableSQL = "CREATE TABLE IF NOT EXISTS ad_table"
	    		 +" (name varchar(20) not null , " + "phoneNum varchar(20) primary key, " 
	    		 + "address varchar(20), " + "email varchar(20)" + ")";

	    rs = stmt.executeQuery(tableSQL);	//��ɹ� ����	    	    
	}

	public int getCount() throws SQLException{	//��ϵ� ��� �� ������

			int result = 0;
			String sql = "SELECT COUNT (*) AS COUNT FROM ad_table";	//���̺� �� count
			rs = stmt.executeQuery(sql);	//��ɹ� ����
			
			while (rs.next())	//�����ϸ� result�� count �ֱ�
				result = rs.getInt("COUNT");
			return result;
	}

	//�������� Ȯ�� �޼ҵ�
	public boolean checkName(String name) throws Exception {
		rs = stmt.executeQuery("select * from ad_table where name = '" + name + "';" ); // ��ġ�ϴ� ��ȭ��ȣ �˻�

		if (rs.next())
			return true;	//�������� ������ true
		return false;	//�������� ������ false
		
	}

	//��ϵ� ��ȭ��ȣ�� �ִ��� Ȯ�� �޼ҵ�
	public boolean checkPhoneNum(String phoneNum) throws SQLException {
		rs = stmt.executeQuery("select * from ad_table where phoneNum = '" + phoneNum + "';" ); // ��ġ�ϴ� ��ȭ��ȣ �˻�	
		if (rs.next())
			return true;	//��ϵ� ��ȭ��ȣ ������ true
		return false;	//��ϵ� ��ȭ��ȣ ������ false
	}	

	//�ּҷ� ��� �޼ҵ�
	public void add(Person ps)throws Exception{ 
		String addsql = "INSERT INTO ad_table (name, phoneNum, address, email) values "
				+ "('" + ps.getName() + "','" + ps.getPhoneNum() + "','" 
				+ ps.getAddress() + "','" + ps.getEmail() + "')";
		
		stmt.executeUpdate(addsql);
	}	

	//�̸����� �ּҷ� ��ȣ �˻� �޼ҵ�, ��ϵ� �̸� ���� ��� �ͼ��� 
	public String searchName(String name) throws SQLException{
		
		String search = "";
		// �޾ƿ� name �� db���� ã�´�.
		rs = stmt.executeQuery("select * from ad_table where name='" + name + "'");
		while (rs.next()) {
			search = rs.getString("name"); 		//ã�� name �� �����͸� search�� �޾ƿ�
		}
		return search;

	}

	//��ȭ��ȣ�� �ּҷ� ��ȣ �˻� �޼ҵ�, ��ϵ� ��ȭ��ȣ ���� ��� �ͼ��� 
	public String searchPhoneNum(String phoneNum)throws Exception{	

		String search = "";
		// �޾ƿ� phoneNum �� db���� ã�´�.
		rs = stmt.executeQuery("select * from ad_table where phoneNum='" + phoneNum + "'");
		while (rs.next()) {
			search = rs.getString("phoneNum"); 			//ã�� phoneNum �� �����͸� search�� �޾ƿ�
		}
		return search; 		
	}

	//�ּҷ� ���� �޼ҵ�
	public void modify(int index, Person ps) throws Exception{	//int index, Person ps
				
		rs.absolute(index + 1);	//Ŀ�� �̵�
		String modName = rs.getString("name");	//Ŀ�� �ִ� ���� name�� �޾ƿ���

		String sql = "UPDATE ad_table SET name =" + ps.getName() + ", phoneNum= " 
		+ ps.getPhoneNum() + ", address= " + ps.getAddress()+ ", email =" +ps.getEmail()+ " WHERE name = '" + modName + "'";	//������ɹ�
		stmt.executeUpdate(sql);	//��ɾ� ����
		System.out.println("�����Ǿ����ϴ�.");
	}

	//�ּҷ� ���� �޼ҵ�
	public void delete(int index) throws Exception{ 		
		rs.absolute(index + 1);	//Ŀ�� �̵�

		String delName = rs.getString("name");
		String sql = "DELETE FROM ad_table WHERE name = '" + delName + "'";	
		stmt.executeUpdate(sql);
		System.out.println("�����Ǿ����ϴ�.");		
	}	
	
	//Person ��ü �Ѱ��ִ� �޼ҵ�
	public Person getPerson(int index) throws SQLException{
		String sql = "SELECT * FROM ad_table";
		rs = stmt.executeQuery(sql);	//��ɹ� ����

		rs.absolute(index +1); // Ŀ�� �̵�
		
		//Ŀ�� �ִ� ���� ���� ��ü p�� �޾ƿ���
		String gName = rs.getString("name");
		String gPhone = rs.getString("phoneNum");
		String gAdr = rs.getString("address");
		String gEmail = rs.getString("email");
		Person p = new Person (gName,gPhone,gAdr,gEmail);

		return p;	//Person ��ü ��ȯ
	}

	//������Ʈ��  ���Ͽ� �����ϴ� �޼ҵ�
	public void writeFile(ObjectOutputStream fn) throws Exception{ //����ȭ
		/*
		try {
			//fn.writeInt(plist.size()); //��ϵ� ��� �� ���Ͽ� ����
			//for(Person person : plist){ //�ּҷ� ũ�⸸ŭ �����ϱ�
				try {
					fn.writeObject(person); //person ������ fn�� ����. ����ȭ
				}catch(Exception e) {
					throw new Exception("writeFile Exception");
				}
			}

		} catch (IOException ioe) {
			throw new IOException("IOException");
		}catch(Exception e) {
			throw new Exception("writeFile Exception");
		}
		*/		
	}	

	//������Ʈ�� ���Ͽ��� �������� �޼ҵ�
	//�ڷ����� ���� read�Լ��� �ٸ��� ���
	public void readFile(ObjectInputStream fn) throws Exception{ //������ȭ
		/*
		try {

			int count=fn.readInt(); //��ϵ� ��� �� �����ͼ� count ������ �ֱ�
			for(int i = 0; i<count; i++) {

				Person p = (Person) fn.readObject(); //���ο� ��ü p�� fn ���� ����. ������ȭ
				//plist.add(p);	//plist�� ���� �߰�

			}
			fn.close(); //���� �ݱ�
		}catch (EOFException eofe) {
		}catch(Exception e) {
			e.printStackTrace();
			throw new Exception("readFile Exception");
		}
		*/
	} 
}