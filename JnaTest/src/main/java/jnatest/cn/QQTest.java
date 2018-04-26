package jnatest.cn;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;

/**
 * Created by lenovo on 2017/4/27. 使用winID来获得窗口的类型和标题，然后发送消息或者其他操作
 *
 */
public class QQTest {
	public static void main(String[] args) {
		//test.txt - 记事本

		HWND hwnd = User32.INSTANCE.FindWindow(null, "test.txt - 记事本"); // 第一个参数是Windows窗体的窗体类，第二个参数是窗体的标题。不熟悉windows编程的需要先找一些Windows窗体数据结构的知识来看看，还有windows消息循环处理，其他的东西不用看太多。
		if (hwnd == null) {
			System.out.println("QQ is not running");
		} else {
			User32.INSTANCE.ShowWindow(hwnd, 9); // SW_RESTORE
			User32.INSTANCE.SetForegroundWindow(hwnd); // bring to front

			// User32.INSTANCE.GetForegroundWindow() //获取现在前台窗口
			WinDef.RECT qqwin_rect = new WinDef.RECT();
			User32.INSTANCE.GetWindowRect(hwnd, qqwin_rect);
			int qqwin_width = qqwin_rect.right - qqwin_rect.left;
			int qqwin_height = qqwin_rect.bottom - qqwin_rect.top;

			User32.INSTANCE.MoveWindow(hwnd, 700, 100, qqwin_width, qqwin_height, true);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			
			printWord("xiamianzheduanhua shi zidongshurude ");
			sendKey(KeyEvent.VK_ENTER);

			// for (int i = 700; i > 100; i -= 10) {
			// User32.INSTANCE.MoveWindow(hwnd, i, 100, qqwin_width, qqwin_height, true); //
			// bring to front
			// try {
			// Thread.sleep(80);
			// } catch (Exception e) {
			// }
			// }
			// User32.INSTANCE.PostMessage(hwnd, WinUser.WM_CLOSE, null, null); // can be
			// WM_QUIT in some occasio
		}
	}

	public static void sendKey(int key) {

		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(key);
			Thread.sleep(1);
			robot.keyRelease(key);
		} catch (AWTException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	public static void printWord(String line) {
		char[] chs=line.toCharArray();
		for(char c :chs) {
			sendKey(getKey(c));
		}
	}

	public static int getKey(char c) {
		switch (c) {
		case 'a':
			return KeyEvent.VK_A;
		case 'b':
			return KeyEvent.VK_B;
		case 'c':
			return KeyEvent.VK_C;
		case 'd':
			return KeyEvent.VK_D;
		case 'e':
			return KeyEvent.VK_E;
		case 'f':
			return KeyEvent.VK_F;
		case 'g':
			return KeyEvent.VK_G;
		case 'h':
			return KeyEvent.VK_H;
		case 'i':
			return KeyEvent.VK_I;
		case 'j':
			return KeyEvent.VK_J;
		case 'k':
			return KeyEvent.VK_K;
		case 'l':
			return KeyEvent.VK_L;
		case 'm':
			return KeyEvent.VK_M;
		case 'n':
			return KeyEvent.VK_N;
		case 'o':
			return KeyEvent.VK_O;
		case 'p':
			return KeyEvent.VK_P;
		case 'q':
			return KeyEvent.VK_Q;
		case 'r':
			return KeyEvent.VK_R;
		case 's':
			return KeyEvent.VK_S;
		case 't':
			return KeyEvent.VK_T;
		case 'u':
			return KeyEvent.VK_U;
		case 'v':
			return KeyEvent.VK_V;
		case 'w':
			return KeyEvent.VK_W;
		case 'x':
			return KeyEvent.VK_X;
		case 'y':
			return KeyEvent.VK_Y;
		case 'z':
			return KeyEvent.VK_Z;

		}
		return KeyEvent.VK_SPACE;

	}
}
