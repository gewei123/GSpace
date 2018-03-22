package cn.jedi.nio.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private Socket socket;
	
	public Client() throws Exception{
		try{
			this.socket=new Socket("localhost",8088);
		}catch(Exception e){
			throw e;
		}
	}
	public void start(){
		Scanner scan=new Scanner(System.in);
		String line=null;
		ClientHandler handler=new ClientHandler(this.socket);
		Thread t=new Thread(handler);
		t.start();
		try{
			OutputStream os=this.socket.getOutputStream();
			OutputStreamWriter osw=new OutputStreamWriter(os,"utf-8");
			PrintWriter pw=new PrintWriter(osw,true);
			while(true){
				line=scan.nextLine();
				pw.println(line);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try{
			System.out.println("开始连接服务器...");
			Client client=new Client();
			System.out.println("成功连接服务器");
			client.start();
		}catch(Exception e){
			System.out.println("客户端连接失败");
		}
	}
	private class ClientHandler implements Runnable{
		private Socket socket;
		public ClientHandler(Socket socket){
			this.socket=socket;
		}
		public void run(){
			String line=null;
			try{
				InputStream is=socket.getInputStream();
				BufferedReader br=new BufferedReader(new InputStreamReader(is,"utf-8"));
				while((line=br.readLine())!=null){
					System.out.println(line);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
