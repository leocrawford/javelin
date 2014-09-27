package com.crypticbit.diff.demo.swing.contacts;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private ServerSocket ss;
	private StreamCallback callback;

	public Server(StreamCallback callback) {
		this.callback = callback;
		try {
			ss = new ServerSocket(8000);
			for (int i = 0; i < 3; i++) {
				ServerListener pes = new ServerListener();
				pes.start();
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	public void halt() {
		try {
			ss.close();
			ss = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Integer getPort() {
		return ss != null ? ss.getLocalPort() : null;
	}

	public class ServerListener extends Thread {

		@Override
		public void run() {
			try {
				Socket s = ss.accept();
				System.out.println("Got connection");
				callback.callback(s.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public interface StreamCallback {
		public void callback(InputStream is);

		public void callback(OutputStream os);
	}

}
