package chat;
import java.io.*;
import java.net.*;
import java.util.*;





public class ChatServer {
	boolean started=false;
	ServerSocket ss=null;
	
	List<Client> clients =new ArrayList<Client>();
	
	public static void main(String[] args) {
		new ChatServer().start();
}
	public void start() {
		try {
			ss=new ServerSocket(8888);
			started=true;
		}catch(BindException e) {
			System.out.println("端口使用中！");
			System.exit(0);
		}
		catch (IOException e1) {
			System.out.println("服务器启动失败！");
		}
		try {
			
			while(started) {
				 Socket s=ss.accept();
				 Client c=new Client(s);
System.out.println("a client connected!");
                  new Thread(c).start();
                  clients.add(c);            }
            
			
		}catch (IOException e) {
			
			e.printStackTrace();
			//System.out.println("client closed!");
		}finally {
			try {
				ss.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	}
	class Client implements Runnable{
         private Socket s;
         private DataInputStream dis=null;
         private DataOutputStream dos=null;
         private boolean bConnected=false;
		 private String name;
         
         public Client(Socket s) {
        	 this.s=s;
        	 try {
				dis=new DataInputStream(s.getInputStream());
				dos=new DataOutputStream(s.getOutputStream());
				bConnected=true;
				this.name=dis.readUTF();
				send(this.name);
				send("欢迎您进入聊天室");
				sendOthers(this.name+"进入聊天室",true);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
        	 
         }
         private String receive() {
 			String msg="";
 			try {
 				msg=dis.readUTF();
 				System.out.println(msg);
 			} catch (IOException e) {
 				// TODO Auto-generated catch block
// 				e.printStackTrace();
 				bConnected=false;
 				clients.remove(this);//异常移除
 				sendOthers(this.name+"离开聊天室",true);
 			}
 			return msg;
 		}
         
        public void send(String str) {
        	if(null==str&&str.equals("")) {
				return;
			}
        	try {
				dos.writeUTF(str);
			} catch (IOException e) {
				clients.remove(this);
				System.out.println("对方退出了");
				//e.printStackTrace();
			}
        }
        private void sendOthers(String msg,boolean sys) {
			//是否为私聊
			if(msg.startsWith("@")&&msg.indexOf(":")>-1) {//私聊
				//获取名称
				String name=msg.substring(1,msg.indexOf(":"));
				String content=msg.substring(msg.indexOf(":")+1);
			    for(Client others:clients) {
			    	if(others.name.equals(name)) {
			    		others.send(this.name+"-->我:"+content);
			    		this.send("我-->"+others.name+":"+content);
			    	}
			    }
			
			}else {
				for(Client others:clients) {
					if(others==this) {
						others.send("我（群）:"+msg);
						continue;
					}
					if(sys) {//系统信息
						others.send("系统信息:"+msg);
					}
					else {
						//发送其它客户端
						others.send(this.name+"（群）:"+msg);
					}
					
			}
			
			}
		}
		
         
		public void run() {
			try {
            while(bConnected) {
//            String str=dis.readUTF();
             sendOthers(receive(),false);
		}
			
			
		}finally {
			try {
				if(dis!=null)
				   dis.close();
				if(dos!=null)
					dos.close();
				if(s!=null)
				    s.close();
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
	}
    	
    }
    }
}

