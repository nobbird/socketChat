package chat;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.JFrame;
@SuppressWarnings("serial")
public class ChatClient extends JFrame{
    
	Socket s=null;
	DataOutputStream dos=null;
	DataInputStream dis=null;
	private boolean beConnected=false;
	
	TextField sendWord=new TextField();
	TextArea  acceptWord=new TextArea();
	Thread tRecv = new Thread(new RecvThread());
	
	public static void main(String[] args) {
		new ChatClient().launchFrame();
   }
	
	public void launchFrame() {
		setLocation(100,100);
		this.setSize(400, 400);
		add(sendWord,BorderLayout.SOUTH);
		add(acceptWord,BorderLayout.NORTH);
		pack();
		sendWord.addActionListener(new TextFieldListener());
		setVisible(true);
		connect();
		new Thread (new RecvThread()).start();
		acceptWord.setText("请输入昵称:\n");
		
		
		
		this.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent arg0) {
				disconnect();
				System.exit(0);
			}
		});
	}
	
	private class TextFieldListener implements ActionListener{
		
		public void actionPerformed(ActionEvent e) {
			String str=sendWord.getText().trim();//取出文本框里面的内容并去除两边空格
			sendWord.setText("");
			
			
			try {				
                dos.writeUTF(str);
				dos.flush();
				
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			}
		
	}
	
	public void connect() {
		try {
			s=new Socket("127.0.0.1",8888);
			dos=new DataOutputStream(s.getOutputStream());
			dis=new DataInputStream(s.getInputStream());
System.out.println("connected!");
             beConnected=true;

		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			dos.close();
			dis.close();
			s.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
    
	private class RecvThread implements Runnable{

		
		public void run() {
			try {
			while(beConnected) {
				String str = dis.readUTF();
				acceptWord.setText(acceptWord.getText()+str+"\n");
			}
		} catch(SocketException e){
			System.out.println("已退出！");
		}
			
			catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}
}
