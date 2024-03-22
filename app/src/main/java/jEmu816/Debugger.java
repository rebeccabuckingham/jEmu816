package jEmu816;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Debugger {
	private static final Logger logger = LoggerFactory.getLogger("Debugger.class");

	private int port = 6502;
	private boolean shutdown = false;

	private final App app;

	public Debugger(App app, int port) {
		this.app = app;
		this.port = port;
	}

	public void shutDown() {
		shutdown = true;
	}

	public void startDebugger() throws Exception {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(port));
		serverSocketChannel.configureBlocking(false);

		logger.debug("debugger started, listening on port: " + port);

		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			while (!shutdown) {
				try {
					SocketChannel clientChannel = serverSocketChannel.accept();

					if (clientChannel != null) {
						logger.debug("Accepted connection from: " + clientChannel.getRemoteAddress());

						handleClient(clientChannel);
					}
				} catch (Exception e) {
					logger.error("Debugger failed", e);
					shutdown = true;
				}
			}
		});
	}

	private void handleClient(SocketChannel clientChannel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		while (clientChannel.read(buffer) != -1) {
			buffer.flip();
			byte[] bytes = new byte[buffer.remaining()];
			buffer.get(bytes);
			String receivedData = new String(bytes, StandardCharsets.UTF_8);
			String responseData = processCommand(receivedData);
			buffer.clear();
			buffer.put(responseData.getBytes(StandardCharsets.UTF_8));
			buffer.flip();
			while (buffer.hasRemaining()) {
				clientChannel.write(buffer);
			}
			buffer.clear();
		}

		System.out.println("Connection closed by client: " + clientChannel.getRemoteAddress());
		clientChannel.close();
	}

	private String getHelp() {
		return "we're not playing get help!\n";
	}

	private String disassemble(String[] args) {
		return "";
	}

	private String repeatCommand() {
		return "";
	}

	private String breakpointsList() {
		return "";
	}

	private String breakpointsEnable() {
		return "";
	}

	private String breakpointsDisable() {
		return "";
	}

	private String breakpointClear() {
		return "";
	}

	private String addBreakpoint() {
		return "";
	}

	private String go() {
		return "";
	}

	private String hexdump(String[] args) {
		return "";
	}

	private String processCommand(String cmdline) {
		if (cmdline == null || cmdline.isBlank()) {
			return "";
		}

		var args = cmdline.split("\\s+");

		switch(args[0]) {
		case "q", "quit" -> {app.shutdown = true;}
		case "h", "help" -> {return getHelp();}
		case "d", "disassemble" -> {return disassemble(args);}
		case "x", "hexdump" -> {return hexdump(args);}
		case "s", "step" -> {app.cpu.step(); return app.cpu.toString();}
		case "g", "go" -> {return go();}
		case "t", "tell" -> {return app.cpu.toString();}
		case "ba", "breakpointAdd" -> {return addBreakpoint();}
		case "bc", "breakpointClear" -> {return breakpointClear();}
		case "bd", "breakpointsDisable" -> {return breakpointsDisable();}
		case "be", "breakpointsEnable" -> {return breakpointsEnable();}
		case "bl", "breakpointsList" -> {return breakpointsList();}
		case ".", "repeatCommand" -> {return repeatCommand();}
		}

		return "unknown command.\n";
	}

}
