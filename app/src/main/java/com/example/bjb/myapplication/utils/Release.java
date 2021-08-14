package com.example.bjb.myapplication.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Release {

	public static void release(ServerSocket server) {
		release(server, null, null, null);
	}

	public static void release(Socket socket) {
		release(null, socket, null, null);
	}

	public static void release(BufferedReader br) {
		release(null, null, br, null);
	}

	public static void release(BufferedWriter bw) {
		release(null, null, null, bw);
	}

	public static void release(Socket socket, BufferedReader br) {
		release(null, socket, br, null);
	}

	public static void release(Socket socket, BufferedWriter bw) {
		release(null, socket, null, bw);
	}


	public static void release(Socket socket, InputStream inputStream){
		release(socket,inputStream,null);
	}
	public static void release(Socket socket, OutputStream outputStream){
		release(socket,null,outputStream);
	}
	public static void release(ServerSocket server, InputStream inputStream){
		release(server,inputStream,null);
	}
	public static void release(ServerSocket server, OutputStream outputStream){
		release(server,null,outputStream);
	}
	public static void release(Socket socket, InputStream inputStream, OutputStream outputStream){
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			if(inputStream!=null){
				inputStream.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			if(outputStream!=null){
				outputStream.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void release(ServerSocket server, InputStream inputStream, OutputStream outputStream){
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			if(inputStream!=null){
				inputStream.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			if(outputStream!=null){
				outputStream.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}


	public static void release(ServerSocket server, Socket socket, BufferedReader br, BufferedWriter bw) {
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (bw != null) {
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
