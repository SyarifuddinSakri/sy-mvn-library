package com.sy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author SyarifuddinSakri
 *         This class provides Telnet communication if extended.
 */
public abstract class TelnetServer implements Runnable {
	public ServerSocket server;
	public Socket client;
	public HashMap<String, String> clientList = new HashMap<>();
	public List<String> clientIp = new ArrayList<>();
	public PrintWriter writer;
	public BufferedReader br;
	public int port;

	public TelnetServer(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			server = new ServerSocket(port);
			while (true) {
				client = server.accept();
				Thread thread = new Thread(() -> {
					try {
						br = new BufferedReader(new InputStreamReader(client.getInputStream()));
						writer = new PrintWriter(client.getOutputStream());
						runAbstraction();
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				thread.start();
			}
		} catch (IOException e) {
			// if connection is closed this will execute eg: after closeConnection();
		}
	}

	/**
	 * @param message
	 *                Write message to the terminal
	 */
	public void writeMessage(String message) {
		writer.print(message + "\r\n");
		writer.flush();
	}

	public void writeMessageNoLine(String message) {
		writer.print(message);
		writer.flush();
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public String readMessage() throws IOException {
		StringBuilder inputBuffer = new StringBuilder(); // Buffer to accumulate characters

		while (true) {
			char inputChar = (char) br.read(); // Read a single character

			// Handle backspace character
			if (inputChar == '\b') {
				// Handle backspace logic
				writeMessageNoLine("\033[1P");
				// writeMessageNoLine("\033[P");
				if (inputBuffer.length() > 0) {
					inputBuffer.deleteCharAt(inputBuffer.length() - 1);
				}
			} else if (inputChar == '\n' || inputChar == '\r') {
				// Handle newline character - process the complete line
				String inputLine = inputBuffer.toString();
				br.readLine();
				inputBuffer.setLength(0); // Clear the buffer for the next line
				return inputLine;
			} else {
				// Handle other characters
				inputBuffer.append(inputChar);
			}
		}
	}

	public enum TextStyle {
		BOLD("\033[1m"),
		DIM("\033[2m"),
		UNDERLINE("\033[4m"),
		BLINK("\033[5m"),
		REVERSE("\033[7m"),
		HIDDEN("\033[8m");

		private final String code;

		TextStyle(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

	public enum TextColor {
		BLACK("\033[30m"),
		RED("\033[31m"),
		GREEN("\033[32m"),
		YELLOW("\033[33m"),
		BLUE("\033[34m"),
		MAGENTA("\033[35m"),
		CYAN("\033[36m"),
		WHITE("\033[37m"),
		CRIMSONRED("\033[38;5;160m"),
		ORANGE("\033[38;5;208m"),
		DEEPSKYBLUE("\033[38;5;51m"),
		LIMEGREEN("\033[38;5;82m"),
		MAGENTAPINK("\033[38;5;201m"),
		BRIGHTYELLOW("\033[38;5;11m"),
		GRAY("\033[38;5;243m");

		private final String code;

		TextColor(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

	public enum BackGroundColor {
		BLACK("\033[40m"),
		RED("\033[41m"),
		GREEN("\033[42m"),
		YELLOW("\033[43m"),
		BLUE("\033[44m"),
		MAGENTA("\033[45m"),
		CYAN("\033[46m"),
		WHITE("\033[47m"),
		CRIMSONRED("\033[48;5;160m"),
		ORANGE("\033[48;5;208m"),
		DEEPSKYBLUE("\033[48;5;51m"),
		LIMEGREEN("\033[48;5;82m"),
		MAGENTAPINK("\033[48;5;201m"),
		BRIGHTYELLOW("\033[48;5;11m"),
		GRAY("\033[48;5;243m");

		private final String code;

		BackGroundColor(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

	public void setTextStyle(TextStyle style) {
		writeMessageNoLine(style.getCode());
	}

	public void setTextColor(TextColor color) {
		writeMessageNoLine(color.getCode());
	}

	public void setBackGroundColor(BackGroundColor color) {
		writeMessageNoLine(color.getCode());
	}

	public void resetAllStyleAndColor() {
		writeMessageNoLine("\033[0m");
	}

	public void setColorNormal() {
		setTextColor(TextColor.WHITE);
		setBackGroundColor(BackGroundColor.BLACK);
	}

	public void clear() {
		writer.println("\033[2J\033[H");
		writer.flush();
	}

	public void closeConnection() {
		try {
			client.close();
			clientIp.remove(client.getInetAddress().getHostName());
			br.close();
			writer.close();
		} catch (IOException e) {
		}
	}

	public abstract void runAbstraction();
}
