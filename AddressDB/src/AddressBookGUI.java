import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

/* 프로그램 이름 : UI.java
프로그램 설명 : Person, AddressBook 객체를 이용해 주소록을 관리하는 프로그램
작성일 : 2020-10-25
작성자 : 소프트웨어 융합 우아라 */

public class AddressBookGUI extends JFrame implements ActionListener, MouseListener{

	private JFrame frame;
	private JTextField SearchField, tfName, tfPhoneNum, tfAddress, tfEmail;
	private JTable table;
	private JButton btnSave, btnAdd, btnSearch, btnAllSearch, btnModify, btnDelete, btnClose;	//버튼 변수
	private JComboBox searchComboBox;	//콤보박스 변수
	private JTextPane PaneInfo;	//안내문자
	private String[] colNames = {"이름", "전화번호", "주소", "이메일"}; //표 제목줄. 테이블에 필요
	private DefaultTableModel model = new DefaultTableModel(colNames,0); //처음 빈 테이블 만들기 위해 데이터 관리 객체 생성. 테이블에 필요
	private String[]rows = new String[4];	//정보 담아놓는 배열 생성

	int rowCk;	//테이블을 마우스로 클릭했을 때 변수

	AddressBook ad = null;
	String name, phoneNum, address, email;

	//메인 코드
	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AddressBookGUI window = new AddressBookGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public AddressBookGUI() throws Exception {
		initialize();
	}

	//프레임 코드 함수
	public void initialize() throws Exception{
		String url = "jdbc:mariadb://localhost:3306/addressdb";
		//드라이버 로드
		Class.forName ("org.mariadb.jdbc.Driver");
		//DB에 연결
		Connection conn = DriverManager.getConnection(url, "root", "1234");
		
		System.out.println("데이터베이스에 접속했습니다.");

		ad = new AddressBook(conn);	//connection 파라메터로 넘기기

		
		//프레임 생성
		frame = new JFrame("주소록 관리 프로그램");
		frame.setBounds(100, 100, 495, 565);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//메인패널 생성
		JPanel mainPanel = new JPanel();
		mainPanel.setForeground(new Color(255, 255, 255));
		mainPanel.setBackground(new Color(255, 255, 255));
		mainPanel.setBorder(new LineBorder(new Color(102, 153, 204)));
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);	
		mainPanel.setLayout(null);

		//타이틀 라벨
		JLabel titlelabel = new JLabel("주소록 관리");
		titlelabel.setOpaque(true);
		titlelabel.setBackground(new Color(102, 153, 204));
		titlelabel.setBounds(12, 10, 454, 43);
		titlelabel.setFont(new Font("THE봉숭아틴트", Font.BOLD, 30));
		titlelabel.setForeground(new Color(255, 255, 255));
		titlelabel.setHorizontalAlignment(SwingConstants.CENTER);
		mainPanel.add(titlelabel);	//메인 패널에 넣기

		//테이블이 있는 스크롤팬 생성
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setFont(new Font("맑은 고딕 Semilight", Font.PLAIN, 12));
		scrollPane.setBorder(new LineBorder(new Color(102, 153, 204), 2));
		scrollPane.setBounds(12, 59, 454, 262);
		mainPanel.add(scrollPane);	//메인 패널에 넣기

		//테이블 
		table = new JTable(model);	//model 데이터 저장하는 테이블 생성
		table.setFont(new Font("맑은 고딕 Semilight", Font.PLAIN, 11));
		table.setBorder(new LineBorder(new Color(102, 153, 204)));
		table.getColumnModel().getColumn(0).setPreferredWidth(65);
		table.getColumnModel().getColumn(1).setPreferredWidth(65);
		table.getColumnModel().getColumn(2).setPreferredWidth(65);
		table.getColumnModel().getColumn(3).setPreferredWidth(65);
		scrollPane.setViewportView(table);
		table.addMouseListener(this);	//	테이블에 마우스 클릭시 처리될 이벤트 등록

		//조회기능 있는 searchPanel 패널
		JPanel searchPanel = new JPanel();
		searchPanel.setBorder(new LineBorder(new Color(102, 153, 204), 2));
		searchPanel.setBackground(SystemColor.menu);
		searchPanel.setBounds(12, 331, 454, 30);
		mainPanel.add(searchPanel);
		searchPanel.setLayout(null);

		//이름으로 검색/ 전화번호로 검색 콤보박스
		searchComboBox = new JComboBox();
		searchComboBox.setFont(new Font("맑은 고딕 Semilight", Font.PLAIN, 12));
		searchComboBox.setEditable(true);
		searchComboBox.setBounds(12, 5, 115, 20);
		searchPanel.add(searchComboBox);
		searchComboBox.setForeground(new Color(0, 0, 0));
		searchComboBox.setBackground(new Color(255, 255, 255));
		searchComboBox.setModel(new DefaultComboBoxModel(new String[] {"이름으로 검색", "전화번호로 검색"}));

		//조회 버튼
		btnSearch = new JButton("조회");
		btnSearch.setForeground(new Color(255, 255, 255));
		btnSearch.setFont(new Font("맑은 고딕 Semilight", Font.BOLD, 12));
		btnSearch.setBorderPainted(false);
		btnSearch.setBackground(new Color(102, 153, 204));
		btnSearch.setAlignmentY(Component.TOP_ALIGNMENT);
		btnSearch.setBounds(280, 5, 68, 20);
		searchPanel.add(btnSearch);
		btnSearch.addActionListener(this);	//조회 버튼 클릭시 처리될 이벤트 등록

		//전체 조회 버튼
		btnAllSearch = new JButton("전체 조회");
		btnAllSearch.setForeground(new Color(255, 255, 255));
		btnAllSearch.setFont(new Font("맑은 고딕 Semilight", Font.BOLD, 12));
		btnAllSearch.setBorderPainted(false);
		btnAllSearch.setBackground(new Color(102, 153, 204));
		btnAllSearch.setAlignmentY(Component.TOP_ALIGNMENT);
		btnAllSearch.setBounds(360, 5, 85, 20);
		searchPanel.add(btnAllSearch);
		btnAllSearch.addActionListener(this);	//전체조회 버튼 클릭시 처리될 이벤트 등록

		//조회 텍스트필드
		SearchField = new JTextField();
		SearchField.setBounds(140, 5, 128, 20);
		searchPanel.add(SearchField);
		SearchField.setColumns(10);

		//추가 버튼
		btnAdd = new JButton("추가");
		btnAdd.setForeground(new Color(255, 255, 255));
		btnAdd.setFont(new Font("맑은 고딕 Semilight", Font.BOLD, 12));
		btnAdd.setBorderPainted(false);
		btnAdd.setBackground(new Color(102, 153, 204));
		btnAdd.setAlignmentY(Component.TOP_ALIGNMENT);
		btnAdd.setBounds(10, 368, 74, 20);
		mainPanel.add(btnAdd);
		btnAdd.addActionListener(this);	//추가버튼 클릭시 처리될 이벤트 등록

		//수정 버튼
		btnModify = new JButton("수정");
		btnModify.setForeground(new Color(255, 255, 255));
		btnModify.setFont(new Font("맑은 고딕 Semilight", Font.BOLD, 12));
		btnModify.setBorderPainted(false);
		btnModify.setBackground(new Color(102, 153, 204));
		btnModify.setAlignmentY(Component.TOP_ALIGNMENT);
		btnModify.setBounds(107, 368, 74, 20);
		mainPanel.add(btnModify);
		btnModify.addActionListener(this);	//수정버튼 클릭시 처리될 이벤트 등록

		//삭제 버튼
		btnDelete = new JButton("삭제");
		btnDelete.setForeground(new Color(255, 255, 255));
		btnDelete.setFont(new Font("맑은 고딕 Semilight", Font.BOLD, 12));
		btnDelete.setBorderPainted(false);
		btnDelete.setBackground(new Color(102, 153, 204));
		btnDelete.setAlignmentY(Component.TOP_ALIGNMENT);
		btnDelete.setBounds(202, 368, 74, 20);
		mainPanel.add(btnDelete);	
		btnDelete.addActionListener(this);	//삭제버튼 클릭시 처리될 이벤트 등록

//		//저장 버튼
//		btnSave = new JButton("저장");
//		btnSave.setForeground(new Color(255, 255, 255));
//		btnSave.setFont(new Font("맑은 고딕 Semilight", Font.BOLD, 12));
//		btnSave.setBorderPainted(false);
//		btnSave.setBackground(new Color(102, 153, 204));
//		btnSave.setAlignmentY(Component.TOP_ALIGNMENT);
//		btnSave.setBounds(298, 368, 74, 20);
//		mainPanel.add(btnSave);
//		btnSave.addActionListener(this);	//저장버튼 클릭시 처리될 이벤트 등록

		//종료 버튼
		btnClose = new JButton("종료");
		btnClose.setForeground(new Color(255, 255, 255));
		btnClose.setFont(new Font("맑은 고딕 Semilight", Font.BOLD, 12));
		btnClose.setBorderPainted(false);
		btnClose.setBackground(new Color(102, 153, 204));
		btnClose.setAlignmentY(Component.TOP_ALIGNMENT);
		btnClose.setBounds(392, 368, 74, 20);
		mainPanel.add(btnClose);
		btnClose.addActionListener(this);	//종료버튼 클릭시 처리될 이벤트 등록

		//정보 입력칸 있는 패널
		JPanel inputpanel = new JPanel();
		inputpanel.setBackground(SystemColor.window);
		inputpanel.setBounds(12, 401, 454, 69);
		mainPanel.add(inputpanel);
		inputpanel.setLayout(null);

		//이름 입력 라벨
		JLabel NameLabel = new JLabel("이름");
		NameLabel.setFont(new Font("맑은 고딕 Semilight", Font.PLAIN, 12));
		NameLabel.setBounds(36, 10, 57, 15);
		inputpanel.add(NameLabel);

		//전화번호 입력 라벨
		JLabel PhoneNumLabel = new JLabel("전화번호");
		PhoneNumLabel.setFont(new Font("맑은 고딕 Semilight", Font.PLAIN, 12));
		PhoneNumLabel.setBounds(12, 38, 57, 15);
		inputpanel.add(PhoneNumLabel);

		//주소 입력 라벨
		JLabel AddressLabel = new JLabel("주소");
		AddressLabel.setFont(new Font("맑은 고딕 Semilight", Font.PLAIN, 12));
		AddressLabel.setBounds(253, 10, 57, 15);
		inputpanel.add(AddressLabel);


		//이메일 입력 라벨
		JLabel MailLabel = new JLabel("이메일");
		MailLabel.setFont(new Font("맑은 고딕 Semilight", Font.PLAIN, 12));
		MailLabel.setBounds(242, 38, 57, 15);
		inputpanel.add(MailLabel);

		//이름 입력 TextField
		tfName = new JTextField();
		tfName.setBounds(72, 7, 147, 21);
		inputpanel.add(tfName);
		tfName.setColumns(10);

		//전화번호 입력 TextField
		tfPhoneNum = new JTextField();
		tfPhoneNum.setBounds(72, 35, 147, 21);
		inputpanel.add(tfPhoneNum);
		tfPhoneNum.setColumns(10);

		//주소 입력 TextField
		tfAddress = new JTextField();
		tfAddress.setBounds(295, 7, 147, 21);
		inputpanel.add(tfAddress);
		tfAddress.setColumns(10);

		//이메일 입력 TextField
		tfEmail = new JTextField();
		tfEmail.setBounds(295, 35, 147, 21);
		inputpanel.add(tfEmail);
		tfEmail.setColumns(10);

		//안내문자 칸
		PaneInfo = new JTextPane();
		PaneInfo.setBackground(SystemColor.menu);
		PaneInfo.setBounds(12, 478, 454, 39);
		mainPanel.add(PaneInfo);
	}


	//마우스 클릭시  이벤트 코드 구현
	public void mouseClicked(MouseEvent e) 
	{
		//테이블 클릭하면
		rowCk = table.getSelectedRow();	
		//테이블에 있던 정보 텍스트필드에 뜸
		String mName = null;
		String mPhoneNum = null;
		String mAddress = null;
		String mEmail = null;
		try {
			mName = ad.getPerson(rowCk).getName();
			mPhoneNum = ad.getPerson(rowCk).getPhoneNum();
			mAddress = ad.getPerson(rowCk).getAddress();
			mEmail = ad.getPerson(rowCk).getEmail();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		tfName.setText(mName);
		tfPhoneNum.setText(mPhoneNum);
		tfAddress.setText(mAddress);
		tfEmail.setText(mEmail);

		//클릭 후 안내메시지 초기화
		PaneInfo.setText("");
	}

	//버튼 동작 이벤트 코드 구현
	public void actionPerformed(ActionEvent e) {
		try {
			//추가버튼 늘렀을 때 동작
			if (e.getSource() == btnAdd) {
				

				//textfield의 내용을 배열에 넣음
				rows[0] = tfName.getText();
				rows[1] = tfPhoneNum.getText();
				rows[2] = tfAddress.getText();
				rows[3] = tfEmail.getText();

				//배열의 정보 받아오기
				name = rows[0];
				phoneNum = rows[1];
				address = rows[2];
				email = rows[3];

				//텍스트필드에 정보가 씌여있지 않은 경우
				if (name.equals("") || phoneNum.equals("") || address.contentEquals("") || email.contentEquals("")) {
					PaneInfo.setText("입력되지 않았습니다. 모든 칸에 주소록을 입력하세요");
					return;
				}
				//중복된 이름인 경우
				else if (ad.checkName(name)== true) {
					tfName.setText("");	//이름 칸만 필드 값 제거 (이름만 다시 입력)
					PaneInfo.setText("이미 등록된 이름입니다. 다시 입력하세요");
					return;
				}
				//중복된 전화번호인 경우
				else if (ad.checkPhoneNum(phoneNum)== true) {	
					tfPhoneNum.setText("");	//전화번호 칸만 필드 값 제거 (전화번호만 다시 입력)
					PaneInfo.setText("이미 등록된 전화번호입니다. 다시 입력하세요.");
					return;
				}
				else {
					Person newPerson = new Person(name, phoneNum,address, email);	//받은 정보로 Person 객체 생성
					model.addRow(rows);	//테이블에 데이터 추가
					ad.add(newPerson);	//Arraylist에 멤버 객체 추가
					

					//입력 후 텍스트 필드 값 제거
					tfName.setText("");
					tfPhoneNum.setText("");
					tfAddress.setText("");
					tfEmail.setText("");
					PaneInfo.setText("주소록이 추가되었습니다.");
				}
			}

			//수정버튼 눌렀을 때 동작
			else if (e.getSource() == btnModify) {
				int exitOption = JOptionPane.showConfirmDialog(null, "정말 수정하시겠습니까?", "수정", JOptionPane.YES_NO_OPTION);
				// YES_OPTION은 0, NO_OPTION은 1, CLOSED_OPTION은 -1을 반환한다
				if (exitOption == JOptionPane.YES_OPTION) {

				//수정하기 위해 클릭했던 주소록 내용을 배열에 넣음
				rows[0] = tfName.getText();
				rows[1] = tfPhoneNum.getText();
				rows[2] = tfAddress.getText();
				rows[3] = tfEmail.getText();

				//배열의 정보 받아오기
				name = rows[0];
				phoneNum = rows[1];
				address = rows[2];
				email = rows[3];

				//텍스트필드가 비어있으면
				if (name.equals("") || phoneNum.equals("") || address.contentEquals("") || email.contentEquals("")) {
					PaneInfo.setText("입력되지 않았습니다. 모든 칸에 주소록을 입력하세요");
					return;
				}
				//이미 등록된 이름으로 수정하려 한 경우 (단,원래 자신의 이름과는 중복 가능)
				if (ad.checkName(name)== true) {
					if (!(name.equals(ad.getPerson(rowCk).getName()))) {
						PaneInfo.setText("이미 등록된 이름입니다. 다시 입력하세요");
						tfName.setText("");	//이름 칸만 필드 값 제거
						return;
					}
				}
				//이미 등록된 전화번호로 수정하려 한 경우 (단,원래 자신의 전화번호와는 중복 가능)
				if (ad.checkPhoneNum(phoneNum)== true) {
					if (!(phoneNum.equals(ad.getPerson(rowCk).getPhoneNum()))) {
						PaneInfo.setText("이미 등록된 전화번호입니다. 다시 입력하세요");
						tfPhoneNum.setText("");	// 전화번호 칸만 필드 값 제거
						return;
					}
				}

				Person newPerson = new Person(name, phoneNum,address, email);	//받은 정보로 Person 객체 생성
				ad.modify(rowCk,newPerson);	//rowCk번째 값을 새로운 정보로 수정  
				model.removeRow(rowCk);	//원래 있던 테이블의 정보 지우고
				model.insertRow(rowCk, rows);	//그 자리에 새로 받은 정보 넣기

				//입력 후 텍스트 필드 값 제거
				tfName.setText("");
				tfPhoneNum.setText("");
				tfAddress.setText("");
				tfEmail.setText("");
				PaneInfo.setText("주소록이 수정되었습니다.");
				}else if ((exitOption == JOptionPane.NO_OPTION) || (exitOption == JOptionPane.CLOSED_OPTION)) {
					return; // 아무 작업도 하지 않고 다이얼로그 상자를 닫는다
				}
				
				
			}

			//삭제버튼 눌렀을 때 동작
			else if (e.getSource() == btnDelete) {	
				int exitOption = JOptionPane.showConfirmDialog(null, "정말 삭제하시겠습니까?", "삭제", JOptionPane.YES_NO_OPTION);
				// YES_OPTION은 0, NO_OPTION은 1, CLOSED_OPTION은 -1을 반환한다
				if (exitOption == JOptionPane.YES_OPTION) {
				try {
					ad.delete(rowCk);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}

				model.removeRow(rowCk);
				PaneInfo.setText("삭제되었습니다.");

				//텍스트필드에 있던 정보 초기화
				tfName.setText("");
				tfPhoneNum.setText("");
				tfAddress.setText("");
				tfEmail.setText("");
				}else if ((exitOption == JOptionPane.NO_OPTION) || (exitOption == JOptionPane.CLOSED_OPTION)) {
					return; // 아무 작업도 하지 않고 다이얼로그 상자를 닫는다
				}
			}

			//저장버튼 눌렀을 때 동작
			else if (e.getSource() == btnSave) {
/*
				ObjectOutputStream out = null;

				try {
					out = new ObjectOutputStream(new FileOutputStream("addressbook.dat"));	//객체 직렬화 위해 
				} 
				catch (Exception ex) {
					System.out.println(ex.getMessage());
				}

				try {
					ad.writeFile(out);	//객체 직렬화
					PaneInfo.setText("주소록이 저장되었습니다.");
				} catch (FileNotFoundException fnfe) {
					System.out.println(fnfe.getMessage());
				} catch(IOException ioe) {
					PaneInfo.setText("주소록 저장을 실패했습니다.");
				} catch (Exception ex) {
					PaneInfo.setText(ex.getMessage());
				}

				finally {
					//close()이용해 out에 연결된 참조값을 끊기
					try {
						out.close();
					} catch (IOException ex) {
						PaneInfo.setText(ex.getMessage());
					} catch(Exception ex) {
						PaneInfo.setText(ex.getMessage());
					}
				}
				*/
			}

			//조회버튼 눌렀을 때 동작
			else if (e.getSource() == btnSearch) {	//조회 버튼 누르면

				//전체 주소록 먼저 조회
				model.setNumRows(0);
				int c = ad.getCount();
				if (c == 0) {	//주소록에 저장된 사람이 없을 때
					PaneInfo.setText("등록된 주소록이 없습니다.");
				} else {
					for (int i =0; i < c; i++) {
						String[]rows = new String[4];
						rows[0] = ad.getPerson(i).getName();
						rows[1] = ad.getPerson(i).getPhoneNum();
						rows[2] = ad.getPerson(i).getAddress();
						rows[3] = ad.getPerson(i).getEmail();
						model.addRow(rows);
					}
				}

				String find = SearchField.getText();	//텍스트필드에 입력한 내용
				if (find.equals("")) {	//빈칸이면
					PaneInfo.setText("검색할 주소록을 입력하세요.");

				}else {
					try {
						int sidx = 0;	//sidx로 ad의 searchName, searchPhoneNum 반환값 이용
						String combo = (String)searchComboBox.getSelectedItem();

						if (combo.contentEquals("이름으로 검색") ) {	//"이름으로 검색" 선택했을 경우
							String searchName = ad.searchName(find);	//ad에서 찾는 이름을 searchName으로 받아옴
							int cnt = ad.getCount();	//전체 주소록 수
							for (int i = 0; i < cnt; i++) {
								if (searchName.equals(table.getValueAt(i,0)))	//검색하는이름 index 찾기
									sidx = i;
							}

							PaneInfo.setText("이름으로 조회되었습니다.");	//조회 완료 후
							SearchField.setText("");	//텍스트필드에 있던 정보는 초기화
						}
						
						else if (combo.contentEquals("전화번호로 검색")) { //"전화번호로 검색" 선택했을 경우
							String searchPhoneNum = ad.searchPhoneNum(find);	//ad에서 찾는 전화번호를 searchPhoneNum으로 받아옴
							int cnt = ad.getCount();
							for (int i = 0; i < cnt; i++) {
								if (searchPhoneNum.equals(table.getValueAt(i,0)))	//검색하는전화번호 index 찾기
									sidx = i;
							}
							PaneInfo.setText("전화번호로 조회되었습니다.");	//조회 완료 후
							SearchField.setText("");	//텍스트필드에 있던 정보는 초기화
						}
						
						if (sidx >= 0) {	//sidx에 반환값이 입력되었을 경우 하이라이트로 표시 (검색 표시)
							table.changeSelection(sidx, 0, false, false);
						}
						
					}catch (Exception ex) {
						ex.getMessage();
						PaneInfo.setText("등록된 주소록이 없습니다. 올바른 정보를 입력하세요.");
					}

					
				}
			}

			//전체조회버튼 눌렀을 때 동작
			else if (e.getSource() == btnAllSearch) {
				model.setNumRows(0);
				int c = ad.getCount();
				if (c == 0) {	//주소록에 저장된 사람이 없을 때
					PaneInfo.setText("등록된 주소록이 없습니다.");
					//System.out.println("등록된 주소록이 없습니다.");
				} else {
					for (int i =0; i < c; i++) {
						String[]rows = new String[4];
						rows[0] = ad.getPerson(i).getName();
						rows[1] = ad.getPerson(i).getPhoneNum();
						rows[2] = ad.getPerson(i).getAddress();
						rows[3] = ad.getPerson(i).getEmail();
						model.addRow(rows);
					}

					//텍스트필드에 있던 정보는 초기화
					tfName.setText("");
					tfPhoneNum.setText("");
					tfAddress.setText("");
					tfEmail.setText("");
					PaneInfo.setText("전체 주소록이 조회되었습니다.");
				}
			}

			//종료버튼 눌렀을 때 동작
			else if (e.getSource() == btnClose) {
				try {
					// db 닫기
					   if(ad.rs!=null)ad.rs.close();
					   if(ad.stmt!=null)ad.stmt.close();
					   if(ad.pstmt!=null)ad.pstmt.close();
					   if(ad.conn!=null)ad.conn.close();
				   }catch (Exception ee){
					   ee.printStackTrace();
				   }
				//프로그램 종료
				System.exit(0);
			}
		}catch (Exception ex) {
			PaneInfo.setText("버튼 동작 오류 발생");
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}


