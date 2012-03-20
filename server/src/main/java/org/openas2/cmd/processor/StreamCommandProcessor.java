package org.openas2.cmd.processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.openas2.OpenAS2Exception;
import org.openas2.WrappedException;
import org.openas2.cmd.Command;
import org.openas2.cmd.CommandResult;
import org.openas2.util.CommandTokenizer;

/**
 * original author unknown
 * 
 * in this release made the process a thread so it can be shared with other command processors like
 * the SocketCommandProcessor
 * created innerclass CommandTokenizer so it could handle quotes and spaces within quotes
 * @author joseph mcverry
 *
 */
public class StreamCommandProcessor extends BaseCommandProcessor
		implements
			Runnable {
	public static final String COMMAND_NOT_FOUND = "Error: command not found";
	public static final String COMMAND_ERROR = "Error executing command";
	public static final String EXIT_COMMAND = "exit";
	public static final String PROMPT = "#>";
	private BufferedReader reader = null;
	private BufferedWriter writer = null;

	public StreamCommandProcessor() {
		reader = new BufferedReader(new InputStreamReader(System.in));
		writer = new BufferedWriter(new OutputStreamWriter(System.out));
	}

	public BufferedReader getReader() {
		return reader;
	}

	public BufferedWriter getWriter() {
		return writer;
	}

	public void deInit() throws OpenAS2Exception {
	}

	public void init() throws OpenAS2Exception {
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			while (true)
				processCommand();

		} catch (OpenAS2Exception e) {
			e.printStackTrace();
		}

	}

	public void processCommand() throws OpenAS2Exception {
		try {

			String str = readLine();

			if (str != null) {
				CommandTokenizer strTkn = new CommandTokenizer(str);

				if (strTkn.hasMoreTokens()) {
					String commandName = strTkn.nextToken().toLowerCase();

					if (commandName.equals(EXIT_COMMAND)) {
						terminate();
					} else {
						List params = new ArrayList();

						while (strTkn.hasMoreTokens()) {

							params.add(strTkn.nextToken());
						}

						Command cmd = getCommand(commandName);

						if (cmd != null) {
							CommandResult result = cmd
									.execute(params.toArray());

							if (result.getType() == CommandResult.TYPE_OK) {
								writeLine(result.toString());
							} else {
								writeLine(COMMAND_ERROR);
								writeLine(result.getResult());
							}
						} else {
							writeLine(COMMAND_NOT_FOUND + "> " + commandName);
							List l = getCommands();
							writeLine("List of commands:");
							writeLine(EXIT_COMMAND);
							for (int i = 0; i < l.size(); i++) {
								cmd = (Command) l.get(i);
								writeLine(cmd.getName());
							}
						}
					}
				}

				write(PROMPT);
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}

		} catch (IOException ioe) {
			throw new WrappedException(ioe);
		}
	}

	public String readLine() throws java.io.IOException {
		BufferedReader rd = getReader();

		return rd.readLine().trim();
	}

	public void write(String text) throws java.io.IOException {
		BufferedWriter wr = getWriter();
		wr.write(text);
		wr.flush();
	}

	public void writeLine(String line) throws java.io.IOException {
		BufferedWriter wr = getWriter();
		wr.write(line + "\r\n");
		wr.flush();
	}

}
