import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


/* 프로그램 이름 : AddressBook.java
프로그램 설명 : 클래스 AddressBook 정의 프로그램
작성일 : 2020-11-20
작성자 : 소프트웨어융합 우아라*/

public class AddressBook{
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	PreparedStatement pstmt = null;
	
	public AddressBook(Connection conn) throws Exception {//UI에서 생성할 사람의 수 가져와서 객체 생성 (UI 에서 제일 처음 시작할 때)	

		 stmt = conn.createStatement();	//Statement 여기서 한번만 만들기
		 
         //테이블이 없다면 테이블 생성	     
	     String tableSQL = "CREATE TABLE IF NOT EXISTS ad_table"
	    		 +" (name varchar(20) not null , " + "phoneNum varchar(20) primary key, " 
	    		 + "address varchar(20), " + "email varchar(20)" + ")";

	    rs = stmt.executeQuery(tableSQL);	//명령문 실행	    	    
	}

	public int getCount() throws SQLException{	//등록된 사람 수 접근자

			int result = 0;
			String sql = "SELECT COUNT (*) AS COUNT FROM ad_table";	//테이블 수 count
			rs = stmt.executeQuery(sql);	//명령문 실행
			
			while (rs.next())	//존재하면 result에 count 넣기
				result = rs.getInt("COUNT");
			return result;
	}

	//동명이인 확인 메소드
	public boolean checkName(String name) throws Exception {
		rs = stmt.executeQuery("select * from ad_table where name = '" + name + "';" ); // 일치하는 전화번호 검색

		if (rs.next())
			return true;	//동명이인 있으면 true
		return false;	//동명이인 없으면 false
		
	}

	//등록된 전화번호가 있는지 확인 메소드
	public boolean checkPhoneNum(String phoneNum) throws SQLException {
		rs = stmt.executeQuery("select * from ad_table where phoneNum = '" + phoneNum + "';" ); // 일치하는 전화번호 검색	
		if (rs.next())
			return true;	//등록된 전화번호 있으면 true
		return false;	//등록된 전화번호 없으면 false
	}	

	//주소록 등록 메소드
	public void add(Person ps)throws Exception{ 
		String addsql = "INSERT INTO ad_table (name, phoneNum, address, email) values "
				+ "('" + ps.getName() + "','" + ps.getPhoneNum() + "','" 
				+ ps.getAddress() + "','" + ps.getEmail() + "')";
		
		stmt.executeUpdate(addsql);
	}	

	//이름으로 주소록 번호 검색 메소드, 등록된 이름 없을 경우 익셉션 
	public String searchName(String name) throws SQLException{
		
		String search = "";
		// 받아온 name 을 db에서 찾는다.
		rs = stmt.executeQuery("select * from ad_table where name='" + name + "'");
		while (rs.next()) {
			search = rs.getString("name"); 		//찾은 name 의 데이터를 search로 받아옴
		}
		return search;

	}

	//전화번호로 주소록 번호 검색 메소드, 등록된 전화번호 없을 경우 익셉션 
	public String searchPhoneNum(String phoneNum)throws Exception{	

		String search = "";
		// 받아온 phoneNum 을 db에서 찾는다.
		rs = stmt.executeQuery("select * from ad_table where phoneNum='" + phoneNum + "'");
		while (rs.next()) {
			search = rs.getString("phoneNum"); 			//찾은 phoneNum 의 데이터를 search로 받아옴
		}
		return search; 		
	}

	//주소록 수정 메소드
	public void modify(int index, Person ps) throws Exception{	//int index, Person ps
				
		rs.absolute(index + 1);	//커서 이동
		String modName = rs.getString("name");	//커서 있는 곳의 name을 받아오기

		String sql = "UPDATE ad_table SET name =" + ps.getName() + ", phoneNum= " 
		+ ps.getPhoneNum() + ", address= " + ps.getAddress()+ ", email =" +ps.getEmail()+ " WHERE name = '" + modName + "'";	//수정명령문
		stmt.executeUpdate(sql);	//명령어 실행
		System.out.println("수정되었습니다.");
	}

	//주소록 삭제 메소드
	public void delete(int index) throws Exception{ 		
		rs.absolute(index + 1);	//커서 이동

		String delName = rs.getString("name");
		String sql = "DELETE FROM ad_table WHERE name = '" + delName + "'";	
		stmt.executeUpdate(sql);
		System.out.println("삭제되었습니다.");		
	}	
	
	//Person 객체 넘겨주는 메소드
	public Person getPerson(int index) throws SQLException{
		String sql = "SELECT * FROM ad_table";
		rs = stmt.executeQuery(sql);	//명령문 실행

		rs.absolute(index +1); // 커서 이동
		
		//커서 있는 곳의 값을 객체 p로 받아오기
		String gName = rs.getString("name");
		String gPhone = rs.getString("phoneNum");
		String gAdr = rs.getString("address");
		String gEmail = rs.getString("email");
		Person p = new Person (gName,gPhone,gAdr,gEmail);

		return p;	//Person 객체 반환
	}

	//오브젝트를  파일에 저장하는 메소드
	public void writeFile(ObjectOutputStream fn) throws Exception{ //직렬화
		/*
		try {
			//fn.writeInt(plist.size()); //등록된 사람 수 파일에 저장
			//for(Person person : plist){ //주소록 크기만큼 저장하기
				try {
					fn.writeObject(person); //person 정보를 fn에 저장. 직렬화
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

	//오브젝트를 파일에서 가져오는 메소드
	//자료형에 따라 read함수를 다르게 사용
	public void readFile(ObjectInputStream fn) throws Exception{ //역직렬화
		/*
		try {

			int count=fn.readInt(); //등록된 사람 수 가져와서 count 변수에 넣기
			for(int i = 0; i<count; i++) {

				Person p = (Person) fn.readObject(); //새로운 객체 p에 fn 정보 넣음. 역직렬화
				//plist.add(p);	//plist에 정보 추가

			}
			fn.close(); //파일 닫기
		}catch (EOFException eofe) {
		}catch(Exception e) {
			e.printStackTrace();
			throw new Exception("readFile Exception");
		}
		*/
	} 
}