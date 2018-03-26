package cn.judi.arrange;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Arrange {
	
	private File srcFolder;
	private Map<String,String> map;
	private File othersFolder;
	
	
	public static boolean canUse(String keyword,String location) {
		if(keyword==null || "".equals(keyword)) {
			return false;
		}
		if(location==null || "".equals(location)) {
			return false;
		}
		return true;
	}
	
	
	public void init() {
		
		SAXReader reader=new SAXReader();
		try {
			Document doc=reader.read("config.xml");
			Element root = doc.getRootElement();
			Element src=root.element("src");
			String srcPath=src.element("srcPath").getText();
			File srcFolder=new File(srcPath);
			if(!srcFolder.exists() || !srcFolder.isDirectory()) {
				throw new RuntimeException("源文件夹不存在或不是一个文件夹,请检查路径");
			}
			this.srcFolder=srcFolder;
			Map<String,String> map=new HashMap<String,String>();
			
			List<Element> elements = root.elements("folder");
			
			int count = 1;
			if(elements.size()!=0) {
				for(Element e : elements) {
					String keyword=e.element("keyword").getText();
					String location=e.element("location").getText();
					if(canUse(keyword,location)) {
						map.put(keyword, location);
					}else {
						throw new RuntimeException("第"+count+"关键字文件夹配置有误,请检查");
					}
					count++;
				}
			}
			this.map=map;
			
			Element others=root.element("others");
			String othersPath=others.element("othersPath").getText();
			File othersFolder=new File(othersPath);
			if(othersFolder.exists()) {
				if(!othersFolder.isDirectory()) {
					throw new RuntimeException("其他文件文件夹不是一个文件夹,请检查路径");
				}
			}else {
				othersFolder.mkdir();
			}
			this.othersFolder=othersFolder;
		} catch (DocumentException e) {
			System.out.println("没有找到配置文件");
			e.printStackTrace();
		}
	}
	
	public void doArrange() {
		File[] files = this.srcFolder.listFiles();
		boolean flag=false;
		for(File f:files) {//获取源文件夹下面的文件
			flag=false;
			String name=f.getName();
			for(String keyword:this.map.keySet()) {//关键字遍历
				if(name.contains(keyword)) {//判断文件名是否包含关键字
					flag=true;
					String location = this.map.get(keyword);
					File targetFolder=new File(location);
					if(!targetFolder.exists()) {
						targetFolder.mkdir();
					}
					this.copy(f, targetFolder);
				}
			}
			if(!flag) {
				this.copy(f, this.othersFolder);
			}
		}
	}
	
	public void copy(File f,File folder) {
		String path = folder.getPath();
		File newFile=new File(path+""+File.separator+f.getName());
		int count=1;
		while(newFile.exists()) {
			count++;
			newFile = new File(path+""+File.separator+f.getName()+"_"+count);
		}
		RandomAccessFile raf1=null;
		RandomAccessFile raf2=null;
		
		try {
			raf1=new RandomAccessFile(f,"rw");
			raf2=new RandomAccessFile(newFile,"rw");
			byte[] data=new byte[1024*1024];
			int len=0;
			while((len=raf1.read(data))!=-1) {
				raf2.write(data, 0, len);
			}
		}catch(Exception e) {
			throw new RuntimeException("文件复制的时候发生异常");
		}finally {
			if(raf1!=null) {
				try {
					raf1.close();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
			if(raf2!=null) {
				try {
					raf2.close();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		}
		
		
		
	}
	
	
	
	public static void main(String[] args) {
		Arrange arrange=new Arrange();
		try {
			arrange.init();
			arrange.doArrange();
		}catch(Exception e) {
			System.out.println("出错了=.="+e.getMessage());			
		}
		
		
		
	}

}
