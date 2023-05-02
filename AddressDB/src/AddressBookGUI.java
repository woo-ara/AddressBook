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

/* ���α׷� �̸� : UI.java
���α׷� ���� : Person, AddressBook ��ü�� �̿��� �ּҷ��� �����ϴ� ���α׷�
�ۼ��� : 2020-10-25
�ۼ��� : ����Ʈ���� ���� ��ƶ� */

public class AddressBookGUI extends JFrame implements ActionListener, MouseListener{

	private JFrame frame;
	private JTextField SearchField, tfName, tfPhoneNum, tfAddress, tfEmail;
	private JTable table;
	private JButton btnSave, btnAdd, btnSearch, btnAllSearch, btnModify, btnDelete, btnClose;	//��ư ����
	private JComboBox searchComboBox;	//�޺��ڽ� ����
	private JTextPane PaneInfo;	//�ȳ�����
	private String[] colNames = {"�̸�", "��ȭ��ȣ", "�ּ�", "�̸���"}; //ǥ ������. ���̺� �ʿ�
	private DefaultTableModel model = new DefaultTableModel(colNames,0); //ó�� �� ���̺� ����� ���� ������ ���� ��ü ����. ���̺� �ʿ�
	private String[]rows = new String[4];	//���� ��Ƴ��� �迭 ����

	int rowCk;	//���̺��� ���콺�� Ŭ������ �� ����

	AddressBook ad = null;
	String name, phoneNum, address, email;

	//���� �ڵ�
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

	//������ �ڵ� �Լ�
	public void initialize() throws Exception{
		String url = "jdbc:mariadb://localhost:3306/addressdb";
		//����̹� �ε�
		Class.forName ("org.mariadb.jdbc.Driver");
		//DB�� ����
		Connection conn = DriverManager.getConnection(url, "root", "1234");
		
		System.out.println("�����ͺ��̽��� �����߽��ϴ�.");

		ad = new AddressBook(conn);	//connection �Ķ���ͷ� �ѱ��

		
		//������ ����
		frame = new JFrame("�ּҷ� ���� ���α׷�");
		frame.setBounds(100, 100, 495, 565);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//�����г� ����
		JPanel mainPanel = new JPanel();
		mainPanel.setForeground(new Color(255, 255, 255));
		mainPanel.setBackground(new Color(255, 255, 255));
		mainPanel.setBorder(new LineBorder(new Color(102, 153, 204)));
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);	
		mainPanel.setLayout(null);

		//Ÿ��Ʋ ��
		JLabel titlelabel = new JLabel("�ּҷ� ����");
		titlelabel.setOpaque(true);
		titlelabel.setBackground(new Color(102, 153, 204));
		titlelabel.setBounds(12, 10, 454, 43);
		titlelabel.setFont(new Font("THE������ƾƮ", Font.BOLD, 30));
		titlelabel.setForeground(new Color(255, 255, 255));
		titlelabel.setHorizontalAlignment(SwingConstants.CENTER);
		mainPanel.add(titlelabel);	//���� �гο� �ֱ�

		//���̺��� �ִ� ��ũ���� ����
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setFont(new Font("���� ��� Semilight", Font.PLAIN, 12));
		scrollPane.setBorder(new LineBorder(new Color(102, 153, 204), 2));
		scrollPane.setBounds(12, 59, 454, 262);
		mainPanel.add(scrollPane);	//���� �гο� �ֱ�

		//���̺� 
		table = new JTable(model);	//model ������ �����ϴ� ���̺� ����
		table.setFont(new Font("���� ��� Semilight", Font.PLAIN, 11));
		table.setBorder(new LineBorder(new Color(102, 153, 204)));
		table.getColumnModel().getColumn(0).setPreferredWidth(65);
		table.getColumnModel().getColumn(1).setPreferredWidth(65);
		table.getColumnModel().getColumn(2).setPreferredWidth(65);
		table.getColumnModel().getColumn(3).setPreferredWidth(65);
		scrollPane.setViewportView(table);
		table.addMouseListener(this);	//	���̺� ���콺 Ŭ���� ó���� �̺�Ʈ ���

		//��ȸ��� �ִ� searchPanel �г�
		JPanel searchPanel = new JPanel();
		searchPanel.setBorder(new LineBorder(new Color(102, 153, 204), 2));
		searchPanel.setBackground(SystemColor.menu);
		searchPanel.setBounds(12, 331, 454, 30);
		mainPanel.add(searchPanel);
		searchPanel.setLayout(null);

		//�̸����� �˻�/ ��ȭ��ȣ�� �˻� �޺��ڽ�
		searchComboBox = new JComboBox();
		searchComboBox.setFont(new Font("���� ��� Semilight", Font.PLAIN, 12));
		searchComboBox.setEditable(true);
		searchComboBox.setBounds(12, 5, 115, 20);
		searchPanel.add(searchComboBox);
		searchComboBox.setForeground(new Color(0, 0, 0));
		searchComboBox.setBackground(new Color(255, 255, 255));
		searchComboBox.setModel(new DefaultComboBoxModel(new String[] {"�̸����� �˻�", "��ȭ��ȣ�� �˻�"}));

		//��ȸ ��ư
		btnSearch = new JButton("��ȸ");
		btnSearch.setForeground(new Color(255, 255, 255));
		btnSearch.setFont(new Font("���� ��� Semilight", Font.BOLD, 12));
		btnSearch.setBorderPainted(false);
		btnSearch.setBackground(new Color(102, 153, 204));
		btnSearch.setAlignmentY(Component.TOP_ALIGNMENT);
		btnSearch.setBounds(280, 5, 68, 20);
		searchPanel.add(btnSearch);
		btnSearch.addActionListener(this);	//��ȸ ��ư Ŭ���� ó���� �̺�Ʈ ���

		//��ü ��ȸ ��ư
		btnAllSearch = new JButton("��ü ��ȸ");
		btnAllSearch.setForeground(new Color(255, 255, 255));
		btnAllSearch.setFont(new Font("���� ��� Semilight", Font.BOLD, 12));
		btnAllSearch.setBorderPainted(false);
		btnAllSearch.setBackground(new Color(102, 153, 204));
		btnAllSearch.setAlignmentY(Component.TOP_ALIGNMENT);
		btnAllSearch.setBounds(360, 5, 85, 20);
		searchPanel.add(btnAllSearch);
		btnAllSearch.addActionListener(this);	//��ü��ȸ ��ư Ŭ���� ó���� �̺�Ʈ ���

		//��ȸ �ؽ�Ʈ�ʵ�
		SearchField = new JTextField();
		SearchField.setBounds(140, 5, 128, 20);
		searchPanel.add(SearchField);
		SearchField.setColumns(10);

		//�߰� ��ư
		btnAdd = new JButton("�߰�");
		btnAdd.setForeground(new Color(255, 255, 255));
		btnAdd.setFont(new Font("���� ��� Semilight", Font.BOLD, 12));
		btnAdd.setBorderPainted(false);
		btnAdd.setBackground(new Color(102, 153, 204));
		btnAdd.setAlignmentY(Component.TOP_ALIGNMENT);
		btnAdd.setBounds(10, 368, 74, 20);
		mainPanel.add(btnAdd);
		btnAdd.addActionListener(this);	//�߰���ư Ŭ���� ó���� �̺�Ʈ ���

		//���� ��ư
		btnModify = new JButton("����");
		btnModify.setForeground(new Color(255, 255, 255));
		btnModify.setFont(new Font("���� ��� Semilight", Font.BOLD, 12));
		btnModify.setBorderPainted(false);
		btnModify.setBackground(new Color(102, 153, 204));
		btnModify.setAlignmentY(Component.TOP_ALIGNMENT);
		btnModify.setBounds(107, 368, 74, 20);
		mainPanel.add(btnModify);
		btnModify.addActionListener(this);	//������ư Ŭ���� ó���� �̺�Ʈ ���

		//���� ��ư
		btnDelete = new JButton("����");
		btnDelete.setForeground(new Color(255, 255, 255));
		btnDelete.setFont(new Font("���� ��� Semilight", Font.BOLD, 12));
		btnDelete.setBorderPainted(false);
		btnDelete.setBackground(new Color(102, 153, 204));
		btnDelete.setAlignmentY(Component.TOP_ALIGNMENT);
		btnDelete.setBounds(202, 368, 74, 20);
		mainPanel.add(btnDelete);	
		btnDelete.addActionListener(this);	//������ư Ŭ���� ó���� �̺�Ʈ ���

//		//���� ��ư
//		btnSave = new JButton("����");
//		btnSave.setForeground(new Color(255, 255, 255));
//		btnSave.setFont(new Font("���� ��� Semilight", Font.BOLD, 12));
//		btnSave.setBorderPainted(false);
//		btnSave.setBackground(new Color(102, 153, 204));
//		btnSave.setAlignmentY(Component.TOP_ALIGNMENT);
//		btnSave.setBounds(298, 368, 74, 20);
//		mainPanel.add(btnSave);
//		btnSave.addActionListener(this);	//�����ư Ŭ���� ó���� �̺�Ʈ ���

		//���� ��ư
		btnClose = new JButton("����");
		btnClose.setForeground(new Color(255, 255, 255));
		btnClose.setFont(new Font("���� ��� Semilight", Font.BOLD, 12));
		btnClose.setBorderPainted(false);
		btnClose.setBackground(new Color(102, 153, 204));
		btnClose.setAlignmentY(Component.TOP_ALIGNMENT);
		btnClose.setBounds(392, 368, 74, 20);
		mainPanel.add(btnClose);
		btnClose.addActionListener(this);	//�����ư Ŭ���� ó���� �̺�Ʈ ���

		//���� �Է�ĭ �ִ� �г�
		JPanel inputpanel = new JPanel();
		inputpanel.setBackground(SystemColor.window);
		inputpanel.setBounds(12, 401, 454, 69);
		mainPanel.add(inputpanel);
		inputpanel.setLayout(null);

		//�̸� �Է� ��
		JLabel NameLabel = new JLabel("�̸�");
		NameLabel.setFont(new Font("���� ��� Semilight", Font.PLAIN, 12));
		NameLabel.setBounds(36, 10, 57, 15);
		inputpanel.add(NameLabel);

		//��ȭ��ȣ �Է� ��
		JLabel PhoneNumLabel = new JLabel("��ȭ��ȣ");
		PhoneNumLabel.setFont(new Font("���� ��� Semilight", Font.PLAIN, 12));
		PhoneNumLabel.setBounds(12, 38, 57, 15);
		inputpanel.add(PhoneNumLabel);

		//�ּ� �Է� ��
		JLabel AddressLabel = new JLabel("�ּ�");
		AddressLabel.setFont(new Font("���� ��� Semilight", Font.PLAIN, 12));
		AddressLabel.setBounds(253, 10, 57, 15);
		inputpanel.add(AddressLabel);


		//�̸��� �Է� ��
		JLabel MailLabel = new JLabel("�̸���");
		MailLabel.setFont(new Font("���� ��� Semilight", Font.PLAIN, 12));
		MailLabel.setBounds(242, 38, 57, 15);
		inputpanel.add(MailLabel);

		//�̸� �Է� TextField
		tfName = new JTextField();
		tfName.setBounds(72, 7, 147, 21);
		inputpanel.add(tfName);
		tfName.setColumns(10);

		//��ȭ��ȣ �Է� TextField
		tfPhoneNum = new JTextField();
		tfPhoneNum.setBounds(72, 35, 147, 21);
		inputpanel.add(tfPhoneNum);
		tfPhoneNum.setColumns(10);

		//�ּ� �Է� TextField
		tfAddress = new JTextField();
		tfAddress.setBounds(295, 7, 147, 21);
		inputpanel.add(tfAddress);
		tfAddress.setColumns(10);

		//�̸��� �Է� TextField
		tfEmail = new JTextField();
		tfEmail.setBounds(295, 35, 147, 21);
		inputpanel.add(tfEmail);
		tfEmail.setColumns(10);

		//�ȳ����� ĭ
		PaneInfo = new JTextPane();
		PaneInfo.setBackground(SystemColor.menu);
		PaneInfo.setBounds(12, 478, 454, 39);
		mainPanel.add(PaneInfo);
	}


	//���콺 Ŭ����  �̺�Ʈ �ڵ� ����
	public void mouseClicked(MouseEvent e) 
	{
		//���̺� Ŭ���ϸ�
		rowCk = table.getSelectedRow();	
		//���̺� �ִ� ���� �ؽ�Ʈ�ʵ忡 ��
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

		//Ŭ�� �� �ȳ��޽��� �ʱ�ȭ
		PaneInfo.setText("");
	}

	//��ư ���� �̺�Ʈ �ڵ� ����
	public void actionPerformed(ActionEvent e) {
		try {
			//�߰���ư �÷��� �� ����
			if (e.getSource() == btnAdd) {
				

				//textfield�� ������ �迭�� ����
				rows[0] = tfName.getText();
				rows[1] = tfPhoneNum.getText();
				rows[2] = tfAddress.getText();
				rows[3] = tfEmail.getText();

				//�迭�� ���� �޾ƿ���
				name = rows[0];
				phoneNum = rows[1];
				address = rows[2];
				email = rows[3];

				//�ؽ�Ʈ�ʵ忡 ������ �������� ���� ���
				if (name.equals("") || phoneNum.equals("") || address.contentEquals("") || email.contentEquals("")) {
					PaneInfo.setText("�Էµ��� �ʾҽ��ϴ�. ��� ĭ�� �ּҷ��� �Է��ϼ���");
					return;
				}
				//�ߺ��� �̸��� ���
				else if (ad.checkName(name)== true) {
					tfName.setText("");	//�̸� ĭ�� �ʵ� �� ���� (�̸��� �ٽ� �Է�)
					PaneInfo.setText("�̹� ��ϵ� �̸��Դϴ�. �ٽ� �Է��ϼ���");
					return;
				}
				//�ߺ��� ��ȭ��ȣ�� ���
				else if (ad.checkPhoneNum(phoneNum)== true) {	
					tfPhoneNum.setText("");	//��ȭ��ȣ ĭ�� �ʵ� �� ���� (��ȭ��ȣ�� �ٽ� �Է�)
					PaneInfo.setText("�̹� ��ϵ� ��ȭ��ȣ�Դϴ�. �ٽ� �Է��ϼ���.");
					return;
				}
				else {
					Person newPerson = new Person(name, phoneNum,address, email);	//���� ������ Person ��ü ����
					model.addRow(rows);	//���̺� ������ �߰�
					ad.add(newPerson);	//Arraylist�� ��� ��ü �߰�
					

					//�Է� �� �ؽ�Ʈ �ʵ� �� ����
					tfName.setText("");
					tfPhoneNum.setText("");
					tfAddress.setText("");
					tfEmail.setText("");
					PaneInfo.setText("�ּҷ��� �߰��Ǿ����ϴ�.");
				}
			}

			//������ư ������ �� ����
			else if (e.getSource() == btnModify) {
				int exitOption = JOptionPane.showConfirmDialog(null, "���� �����Ͻðڽ��ϱ�?", "����", JOptionPane.YES_NO_OPTION);
				// YES_OPTION�� 0, NO_OPTION�� 1, CLOSED_OPTION�� -1�� ��ȯ�Ѵ�
				if (exitOption == JOptionPane.YES_OPTION) {

				//�����ϱ� ���� Ŭ���ߴ� �ּҷ� ������ �迭�� ����
				rows[0] = tfName.getText();
				rows[1] = tfPhoneNum.getText();
				rows[2] = tfAddress.getText();
				rows[3] = tfEmail.getText();

				//�迭�� ���� �޾ƿ���
				name = rows[0];
				phoneNum = rows[1];
				address = rows[2];
				email = rows[3];

				//�ؽ�Ʈ�ʵ尡 ���������
				if (name.equals("") || phoneNum.equals("") || address.contentEquals("") || email.contentEquals("")) {
					PaneInfo.setText("�Էµ��� �ʾҽ��ϴ�. ��� ĭ�� �ּҷ��� �Է��ϼ���");
					return;
				}
				//�̹� ��ϵ� �̸����� �����Ϸ� �� ��� (��,���� �ڽ��� �̸����� �ߺ� ����)
				if (ad.checkName(name)== true) {
					if (!(name.equals(ad.getPerson(rowCk).getName()))) {
						PaneInfo.setText("�̹� ��ϵ� �̸��Դϴ�. �ٽ� �Է��ϼ���");
						tfName.setText("");	//�̸� ĭ�� �ʵ� �� ����
						return;
					}
				}
				//�̹� ��ϵ� ��ȭ��ȣ�� �����Ϸ� �� ��� (��,���� �ڽ��� ��ȭ��ȣ�ʹ� �ߺ� ����)
				if (ad.checkPhoneNum(phoneNum)== true) {
					if (!(phoneNum.equals(ad.getPerson(rowCk).getPhoneNum()))) {
						PaneInfo.setText("�̹� ��ϵ� ��ȭ��ȣ�Դϴ�. �ٽ� �Է��ϼ���");
						tfPhoneNum.setText("");	// ��ȭ��ȣ ĭ�� �ʵ� �� ����
						return;
					}
				}

				Person newPerson = new Person(name, phoneNum,address, email);	//���� ������ Person ��ü ����
				ad.modify(rowCk,newPerson);	//rowCk��° ���� ���ο� ������ ����  
				model.removeRow(rowCk);	//���� �ִ� ���̺��� ���� �����
				model.insertRow(rowCk, rows);	//�� �ڸ��� ���� ���� ���� �ֱ�

				//�Է� �� �ؽ�Ʈ �ʵ� �� ����
				tfName.setText("");
				tfPhoneNum.setText("");
				tfAddress.setText("");
				tfEmail.setText("");
				PaneInfo.setText("�ּҷ��� �����Ǿ����ϴ�.");
				}else if ((exitOption == JOptionPane.NO_OPTION) || (exitOption == JOptionPane.CLOSED_OPTION)) {
					return; // �ƹ� �۾��� ���� �ʰ� ���̾�α� ���ڸ� �ݴ´�
				}
				
				
			}

			//������ư ������ �� ����
			else if (e.getSource() == btnDelete) {	
				int exitOption = JOptionPane.showConfirmDialog(null, "���� �����Ͻðڽ��ϱ�?", "����", JOptionPane.YES_NO_OPTION);
				// YES_OPTION�� 0, NO_OPTION�� 1, CLOSED_OPTION�� -1�� ��ȯ�Ѵ�
				if (exitOption == JOptionPane.YES_OPTION) {
				try {
					ad.delete(rowCk);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}

				model.removeRow(rowCk);
				PaneInfo.setText("�����Ǿ����ϴ�.");

				//�ؽ�Ʈ�ʵ忡 �ִ� ���� �ʱ�ȭ
				tfName.setText("");
				tfPhoneNum.setText("");
				tfAddress.setText("");
				tfEmail.setText("");
				}else if ((exitOption == JOptionPane.NO_OPTION) || (exitOption == JOptionPane.CLOSED_OPTION)) {
					return; // �ƹ� �۾��� ���� �ʰ� ���̾�α� ���ڸ� �ݴ´�
				}
			}

			//�����ư ������ �� ����
			else if (e.getSource() == btnSave) {
/*
				ObjectOutputStream out = null;

				try {
					out = new ObjectOutputStream(new FileOutputStream("addressbook.dat"));	//��ü ����ȭ ���� 
				} 
				catch (Exception ex) {
					System.out.println(ex.getMessage());
				}

				try {
					ad.writeFile(out);	//��ü ����ȭ
					PaneInfo.setText("�ּҷ��� ����Ǿ����ϴ�.");
				} catch (FileNotFoundException fnfe) {
					System.out.println(fnfe.getMessage());
				} catch(IOException ioe) {
					PaneInfo.setText("�ּҷ� ������ �����߽��ϴ�.");
				} catch (Exception ex) {
					PaneInfo.setText(ex.getMessage());
				}

				finally {
					//close()�̿��� out�� ����� �������� ����
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

			//��ȸ��ư ������ �� ����
			else if (e.getSource() == btnSearch) {	//��ȸ ��ư ������

				//��ü �ּҷ� ���� ��ȸ
				model.setNumRows(0);
				int c = ad.getCount();
				if (c == 0) {	//�ּҷϿ� ����� ����� ���� ��
					PaneInfo.setText("��ϵ� �ּҷ��� �����ϴ�.");
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

				String find = SearchField.getText();	//�ؽ�Ʈ�ʵ忡 �Է��� ����
				if (find.equals("")) {	//��ĭ�̸�
					PaneInfo.setText("�˻��� �ּҷ��� �Է��ϼ���.");

				}else {
					try {
						int sidx = 0;	//sidx�� ad�� searchName, searchPhoneNum ��ȯ�� �̿�
						String combo = (String)searchComboBox.getSelectedItem();

						if (combo.contentEquals("�̸����� �˻�") ) {	//"�̸����� �˻�" �������� ���
							String searchName = ad.searchName(find);	//ad���� ã�� �̸��� searchName���� �޾ƿ�
							int cnt = ad.getCount();	//��ü �ּҷ� ��
							for (int i = 0; i < cnt; i++) {
								if (searchName.equals(table.getValueAt(i,0)))	//�˻��ϴ��̸� index ã��
									sidx = i;
							}

							PaneInfo.setText("�̸����� ��ȸ�Ǿ����ϴ�.");	//��ȸ �Ϸ� ��
							SearchField.setText("");	//�ؽ�Ʈ�ʵ忡 �ִ� ������ �ʱ�ȭ
						}
						
						else if (combo.contentEquals("��ȭ��ȣ�� �˻�")) { //"��ȭ��ȣ�� �˻�" �������� ���
							String searchPhoneNum = ad.searchPhoneNum(find);	//ad���� ã�� ��ȭ��ȣ�� searchPhoneNum���� �޾ƿ�
							int cnt = ad.getCount();
							for (int i = 0; i < cnt; i++) {
								if (searchPhoneNum.equals(table.getValueAt(i,0)))	//�˻��ϴ���ȭ��ȣ index ã��
									sidx = i;
							}
							PaneInfo.setText("��ȭ��ȣ�� ��ȸ�Ǿ����ϴ�.");	//��ȸ �Ϸ� ��
							SearchField.setText("");	//�ؽ�Ʈ�ʵ忡 �ִ� ������ �ʱ�ȭ
						}
						
						if (sidx >= 0) {	//sidx�� ��ȯ���� �ԷµǾ��� ��� ���̶���Ʈ�� ǥ�� (�˻� ǥ��)
							table.changeSelection(sidx, 0, false, false);
						}
						
					}catch (Exception ex) {
						ex.getMessage();
						PaneInfo.setText("��ϵ� �ּҷ��� �����ϴ�. �ùٸ� ������ �Է��ϼ���.");
					}

					
				}
			}

			//��ü��ȸ��ư ������ �� ����
			else if (e.getSource() == btnAllSearch) {
				model.setNumRows(0);
				int c = ad.getCount();
				if (c == 0) {	//�ּҷϿ� ����� ����� ���� ��
					PaneInfo.setText("��ϵ� �ּҷ��� �����ϴ�.");
					//System.out.println("��ϵ� �ּҷ��� �����ϴ�.");
				} else {
					for (int i =0; i < c; i++) {
						String[]rows = new String[4];
						rows[0] = ad.getPerson(i).getName();
						rows[1] = ad.getPerson(i).getPhoneNum();
						rows[2] = ad.getPerson(i).getAddress();
						rows[3] = ad.getPerson(i).getEmail();
						model.addRow(rows);
					}

					//�ؽ�Ʈ�ʵ忡 �ִ� ������ �ʱ�ȭ
					tfName.setText("");
					tfPhoneNum.setText("");
					tfAddress.setText("");
					tfEmail.setText("");
					PaneInfo.setText("��ü �ּҷ��� ��ȸ�Ǿ����ϴ�.");
				}
			}

			//�����ư ������ �� ����
			else if (e.getSource() == btnClose) {
				try {
					// db �ݱ�
					   if(ad.rs!=null)ad.rs.close();
					   if(ad.stmt!=null)ad.stmt.close();
					   if(ad.pstmt!=null)ad.pstmt.close();
					   if(ad.conn!=null)ad.conn.close();
				   }catch (Exception ee){
					   ee.printStackTrace();
				   }
				//���α׷� ����
				System.exit(0);
			}
		}catch (Exception ex) {
			PaneInfo.setText("��ư ���� ���� �߻�");
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


