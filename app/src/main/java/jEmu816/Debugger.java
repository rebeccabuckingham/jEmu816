package jEmu816;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Debugger {
	Logger logger = LoggerFactory.getLogger("Debugger.class");

	private int port = 6502;
	private boolean shutdown = false;

	public Debugger(int port) {
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

	private static void handleClient(SocketChannel clientChannel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		while (clientChannel.read(buffer) != -1) {
			buffer.flip();
			while (buffer.hasRemaining()) {
				clientChannel.write(buffer);
			}
			buffer.clear();
		}

		System.out.println("Connection closed by client: " + clientChannel.getRemoteAddress());
		clientChannel.close();
	}
}
