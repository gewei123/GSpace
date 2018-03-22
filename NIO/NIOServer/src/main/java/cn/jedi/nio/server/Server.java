package cn.jedi.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Server {
	private static final int defautPort = 8088;
	private Map<SelectionKey,Client> map=new HashMap<SelectionKey,Client>();
	private static final int BUFFERSIZE=1024;
	
	public void start(String[] args) {
		int port;
		if (args.length == 0) {
			port = defautPort;
		} else {
			port = Integer.parseInt(args[0]);
		}

		ServerSocketChannel channel = null;
		Selector selector = null;
		try {
			channel = ServerSocketChannel.open();
			ServerSocket serverSocket = channel.socket();
			InetSocketAddress address = new InetSocketAddress(port);
			serverSocket.bind(address);
			channel.configureBlocking(false);
			selector = null;
			selector = Selector.open();
			channel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			System.out.println("端口被占用或者无法绑定端口");
			e.printStackTrace();
			return;
		}
		System.out.println("服务器启动成功,等待客户端链接..");

		while (true) {
			try {
				selector.select();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				break;
			}
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> it = keys.iterator();

			while (it.hasNext()) {
				SelectionKey key = it.next();
				it.remove();
				try {
					if (key.isAcceptable()) {
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel client = server.accept();
						System.out.println("有个客户端连上了:" + client);
						client.configureBlocking(false);
						SelectionKey key1 = client.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
						key1.attach("请输入昵称:");
						Client c=new Client();
						c.setFirstTime(true);
						c.setKey(key1);
						map.put(key1, c);
					}
					if (key.isReadable()) {
						System.out.println("接收时间:"+System.currentTimeMillis()/1000);
						SocketChannel client = (SocketChannel) key.channel();
						ByteBuffer buf = ByteBuffer.allocate(BUFFERSIZE);
						if (client.read(buf)>0) {
							buf.flip();
							byte[] bytes = new byte[buf.limit()];
							buf.get(bytes);
							String line=new String(bytes,"utf-8").trim();
							Client c=map.get(key);
							String lineToAll=null;
							if(c.isFirstTime()) {
								c.setFirstTime(false);
								System.out.println(line+"上线了");
								c.setUsername(line);
								lineToAll=line+"上线了";
							}else {
								String username=map.get(key).getUsername();
								System.out.println(username+"说:"+line);
								lineToAll=username+"说:"+line;
							}
							if(lineToAll!=null) {
								Iterator<Client> iter=map.values().iterator();
								while(iter.hasNext()) {
									Client cc=iter.next();
									if(!cc.isFirstTime) {
										String attachment = (String) cc.getKey().attachment();
										if(attachment!=null) {
											cc.getKey().attach(attachment+"\r\n"+lineToAll);
										}else {
											cc.getKey().attach(lineToAll);
										}
										
									}
								}
							}
							
							buf.clear();
							System.out.println("写入缓冲区时间:"+System.currentTimeMillis()/1000);
						}
					}
					if(key.isWritable()) {
						if((String)key.attachment()!=null) {
							SocketChannel client=(SocketChannel) key.channel();
							String line=((String)key.attachment())+"\r\n";
							key.attach(null);
							ByteBuffer buf=ByteBuffer.wrap(line.getBytes("utf-8"));
							client.write(buf);
							System.out.println("写出时间:"+System.currentTimeMillis()/1000);
						}
					}

				} catch (IOException e) {
					Client c=map.get(key);
					this.map.remove(key);
					String lineToAll=c.getUsername()+"下线了";
					Iterator<Client> iter=map.values().iterator();
					while(iter.hasNext()) {
						Client cc=iter.next();
						if(!cc.isFirstTime) {
							String attachment = (String) cc.getKey().attachment();
							if(attachment!=null) {
								cc.getKey().attach(attachment+"\n"+lineToAll);
							}else {
								cc.getKey().attach(lineToAll);
							}
						}
					}
					key.cancel();
					System.out.println(c.getUsername()+"用户下线了");
					try {
						key.channel().close();
					} catch (IOException e1) {
						// TODO 自动生成的 catch 块
						e1.printStackTrace();
					}

				}

			}

		}
		
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.start(args);
	}
	
	public class Client{
		private String username;
		private SelectionKey key;
		private boolean isFirstTime;
		public boolean isFirstTime() {
			return isFirstTime;
		}
		
		public SelectionKey getKey() {
			return key;
		}

		public void setKey(SelectionKey key) {
			this.key = key;
		}

		public void setFirstTime(boolean isFirstTime) {
			this.isFirstTime = isFirstTime;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		
	}

}
