import java.io.*;
import java.util.*;

@SuppressWarnings({ "unused", "serial" })

/* 프로그램 이름 : Person.java
프로그램 설명 : 클래스 Person 정의 프로그램
작성일 : 2020-09-08
작성자 :  소프트웨어 융합 우아라*/

public class Person implements java.io.Serializable{ //implements Serializable 선언(직렬화)
	//name, phoneNum, address, email이 직렬화되어 자동으로 파일로 들어감
	private String name;		 // 이름 필드
	private String phoneNum;	 // 전화번호 필드
	private String address;	 	 // 집주소 필드
	private String email;		 // 이메일 필드

	public Person(String name, String phoneNum, String address, String email){ 
		this.name=name;
		this.phoneNum = phoneNum;
		this.address = address;
		this.email = email;
	}
	public Person() { // String은 자동으로 null 되기 때문에 없어도 되는 부분
		this.name=null;
		this.phoneNum = null;
		this.address = null;
		this.email = null;
	}
	public void setName(String name){ 		// 이름 설정자
		this.name=name;
	}		
	public void setPhoneNum(String phoneNum){ 	// 전화번호 설정자
		this.phoneNum = phoneNum;
	}
	public void setAddress(String address){ 	// 집주소 설정자
		this.address = address;
	}
	public void setEmail(String email){ 		// 이메일 설정자
		this.email = email;
	}
	public String getEmail(){ 			// 이메일 접근자
		return email;
	}
	public String getName(){ 			// 이름 접근자
		return name;
	}
	public String getPhoneNum(){ 		// 전화번호 접근자
		return phoneNum;
	}
	public String getAddress(){ 		// 집주소 접근자
		return address;
	}

	// 필드 데이터 파일에 쓰는(저장) 메소드
	public void writeMyField(DataOutputStream dos)throws Exception{	//필요 x	
		try{
			dos.writeUTF(name);
			dos.writeUTF(phoneNum);
			dos.writeUTF(address);
			dos.writeUTF(email);
		}catch (IOException ioe) {
			throw new IOException("PersonSaveIOE\"");
		}catch(Exception ex) {
			throw new Exception("PersonSaveEx");
		}
	}

	//파일로부터 데이터 읽고 필드에 넣어주는 메소드
	public void readMyField(DataInputStream dis)throws Exception{ //필요 x
		try {
			name = dis.readUTF();
			phoneNum = dis.readUTF();
			address = dis.readUTF();
			email = dis.readUTF();
		}
		catch (EOFException eofe) {
			throw new EOFException("PersonReadEOFE");
		}catch (IOException ioe) {
			throw new IOException("PersonReadIOE");
		}catch(Exception ex) {
			throw new Exception("PersonReadEx");
		}
	}
}