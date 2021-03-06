package server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//imported random
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Room implements AutoCloseable {
	private static SocketServer server;// used to refer to accessible server functions
	private String name;
	private final static Logger log = Logger.getLogger(Room.class.getName());

	// Commands
	private final static String COMMAND_TRIGGER = "/";
	private final static String CREATE_ROOM = "createroom";
	private final static String JOIN_ROOM = "joinroom";
	// adding commands for flip, roll, @, mute, unmute, and html
	private final static String FLIP = "flip";
	private final static String ROLL = "roll";
	private final static String HTML = "html";
	private final static String COLOR = "color";
	private final static String AT = "@";
	private final static String MUTE = "mute";
	private final static String UNMUTE = "unmute";

	public Room(String name) {
		this.name = name;
	}

	public static void setServer(SocketServer server) {
		Room.server = server;
	}

	public String getName() {
		return name;
	}

	private List<ServerThread> clients = new ArrayList<ServerThread>();

	protected synchronized void addClient(ServerThread client) {
		client.setCurrentRoom(this);
		client.loadMute();
		if (clients.indexOf(client) > -1) {
			log.log(Level.INFO, "Attempting to add a client that already exists");
		} else {
			clients.add(client);
			if (client.getClientName() != null) {
				client.sendClearList();
				sendConnectionStatus(client, true, "joined the room " + getName());
				updateClientList(client);
			}
		}
	}

	private void updateClientList(ServerThread client) {
		Iterator<ServerThread> iter = clients.iterator();
		while (iter.hasNext()) {
			ServerThread c = iter.next();
			if (c != client) {
				boolean messageSent = client.sendConnectionStatus(c.getClientName(), true, null);
			}
		}
	}

	protected synchronized void removeClient(ServerThread client) {
		clients.remove(client);
		if (clients.size() > 0) {
			// sendMessage(client, "left the room");
			sendConnectionStatus(client, false, "left the room " + getName());
		} else {
			cleanupEmptyRoom();
		}
	}

	private void cleanupEmptyRoom() {
		// If name is null it's already been closed. And don't close the Lobby
		if (name == null || name.equalsIgnoreCase(SocketServer.LOBBY)) {
			return;
		}
		try {
			log.log(Level.INFO, "Closing empty room: " + name);
			close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void joinRoom(String room, ServerThread client) {
		server.joinRoom(room, client);
	}

	protected void joinLobby(ServerThread client) {
		server.joinLobby(client);
	}

	/***
	 * Helper function to process messages to trigger different functionality.
	 * 
	 * @param message The original message being sent
	 * @param client  The sender of the message (since they'll be the ones
	 *                triggering the actions)
	 */
	private boolean processCommands(String message, ServerThread client) {
		boolean wasCommand = false;
		try {
			if (message.indexOf(COMMAND_TRIGGER) > -1) {
				String[] comm = message.split(COMMAND_TRIGGER);
				log.log(Level.INFO, message);
				String part1 = comm[1];
				String[] comm2 = part1.split(" ");
				String command = comm2[0];
				if (command != null) {
					command = command.toLowerCase();
				}
				String roomName;
				switch (command) {
				case CREATE_ROOM:
					roomName = comm2[1];
					if (server.createNewRoom(roomName)) {
						joinRoom(roomName, client);
					}
					wasCommand = true;
					break;
				case JOIN_ROOM:
					roomName = comm2[1];
					joinRoom(roomName, client);
					wasCommand = true;
					break;
				// adding /roll command
				case ROLL:
					String[] dice = new String[] { "1", "2", "3", "4", "5", "6" };
					Random random = new Random();
					int index = random.nextInt(dice.length);
					sendCommand(client, "<b>rolled " + "<font color=\"red\">" + dice[index] + "</font></b>");
					wasCommand = true;
					break;
				// adding /flip command
				case FLIP:
					String[] coin = new String[] { "heads", "tails" };
					Random random2 = new Random();
					int index2 = random2.nextInt(coin.length);
					sendCommand(client, "<b>flipped " + "<font color=\"red\">" + coin[index2] + "</font></b>");
					wasCommand = true;
					break;
				// adding html command
				case HTML:
					String erased = message.replaceAll("/html", "");
					// replacing b tags here for bold
					erased = erased.replaceAll("!b", "<b>");
					erased = erased.replaceAll("/b", "</b>");
					// replacing u tags for underline
					erased = erased.replaceAll("!u", "<u>");
					erased = erased.replaceAll("/u", "</u>");
					// replacing i tags for italicize
					erased = erased.replaceAll("!i", "<i>");
					erased = erased.replaceAll("/i", "</i>");
					sendCommand(client, erased);
					wasCommand = true;
					break;
				case COLOR:
					String fontColor = comm2[1];
					String eraseCommand = message.replaceAll("/color " + fontColor, "");
					eraseCommand = eraseCommand.replaceAll("!c", "<font color=" + "\"" + fontColor + "\"" + ">");
					eraseCommand = eraseCommand.replaceAll("/c", "</font>");
					// replacing b tags for bold
					eraseCommand = eraseCommand.replaceAll("!b", "<b>");
					eraseCommand = eraseCommand.replaceAll("/b", "</b>");
					// replacing u tags for underline
					eraseCommand = eraseCommand.replaceAll("!u", "<u>");
					eraseCommand = eraseCommand.replaceAll("/u", "</u>");
					// replacing i tags for italicize
					eraseCommand = eraseCommand.replaceAll("!i", "<i>");
					eraseCommand = eraseCommand.replaceAll("/i", "</i>");
					sendCommand(client, eraseCommand);
					wasCommand = true;
					break;
				case MUTE:
					String[] MUser = comm2[1].split(AT);
					String MUser1 = MUser[1];
					// added if statement to prevent duplicate issues
					if (!client.mutedList.contains(MUser1)) {
						client.mutedList.add(MUser1);
					}
					// add a notification that the user was muted
					muteStatus(client, MUser1);
					client.saveMutes();
					wasCommand = true;
					break;
				case UNMUTE:
					String[] UMUser = comm2[1].split(AT);
					String UMUser1 = UMUser[1];
					// added if statement to prevent duplicate issues
					if (client.mutedList.contains(UMUser1)) {
						client.mutedList.remove(UMUser1);
					}
					// add a notification that the user was unmuted
					unmuteStatus(client, UMUser1);
					client.saveMutes();
					wasCommand = true;
					break;
				/*
				 * @username private message command experiment case AT_SIGN: String uName =
				 * comm2[1]; String deleteAT = message.replaceAll("/dm " + uName, "");
				 * sendPrivate(client, uName, deleteAT); wasCommand = true; break;
				 */
				}
			}

			// added @user message private dm feature here
			if (message.indexOf(AT) == 0) {
				String[] trigger = message.split(AT);
				log.log(Level.INFO, message);
				String part1 = trigger[1];
				String[] comm2 = part1.split(" ");
				String uName = comm2[0];
				if (uName != null) {
					uName = uName.toLowerCase();
				}
				String delUName = message.replaceAll("@" + uName, "");
				sendPrivate(client, uName, delUName);
				wasCommand = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return wasCommand;
	}

	// TODO changed from string to ServerThread
	protected void sendConnectionStatus(ServerThread client, boolean isConnect, String message) {
		Iterator<ServerThread> iter = clients.iterator();
		while (iter.hasNext()) {
			ServerThread c = iter.next();
			boolean messageSent = c.sendConnectionStatus(client.getClientName(), isConnect, message);
			if (!messageSent) {
				iter.remove();
				log.log(Level.INFO, "Removed client " + c.getId());
			}
		}
	}

	// edited sendconnection status to get rid of bug with user list ui
	protected void sendCommand(ServerThread client, String message) {
		Iterator<ServerThread> iter = clients.iterator();
		while (iter.hasNext()) {
			ServerThread c = iter.next();
			c.send(client.getClientName(), message);
		}
	}

	// creating a function for privately sending messages to a user
	protected void sendPrivate(ServerThread client, String recipient, String message) {
		Iterator<ServerThread> iter = clients.iterator();
		while (iter.hasNext()) {
			ServerThread c = iter.next();
			if (c.getClientName().equals(recipient) || c.getClientName() == client.getClientName()) {
				c.send(client.getClientName(), message);
			}
		}
	}

	protected void muteStatus(ServerThread client, String recipient) {
		Iterator<ServerThread> iter = clients.iterator();
		while (iter.hasNext()) {
			ServerThread c = iter.next();
			if (c.getClientName().equals(recipient)) {
				c.send(client.getClientName(), "<b><i>has muted you</b></i>");
			}
		}
	}

	protected void unmuteStatus(ServerThread client, String recipient) {
		Iterator<ServerThread> iter = clients.iterator();
		while (iter.hasNext()) {
			ServerThread c = iter.next();
			if (c.getClientName().equals(recipient)) {
				c.send(client.getClientName(), "<b><i>has unmuted you</b></i>");
			}
		}
	}

	/***
	 * Takes a sender and a message and broadcasts the message to all clients in
	 * this room. Client is mostly passed for command purposes but we can also use
	 * it to extract other client info.
	 * 
	 * @param sender  The client sending the message
	 * @param message The message to broadcast inside the room
	 */
	protected void sendMessage(ServerThread sender, String message) {
		log.log(Level.INFO, getName() + ": Sending message to " + clients.size() + " clients");
		if (processCommands(message, sender)) {
			// it was a command, don't broadcast
			return;
		}
		Iterator<ServerThread> iter = clients.iterator();
		while (iter.hasNext()) {
			ServerThread client = iter.next();
			if (!client.isMuted(sender.getClientName())) {
				boolean messageSent = client.send(sender.getClientName(), message);
				if (!messageSent) {
					iter.remove();
					log.log(Level.INFO, "Removed client " + client.getId());
				}
			}
		}
	}

	/***
	 * Will attempt to migrate any remaining clients to the Lobby room. Will then
	 * set references to null and should be eligible for garbage collection
	 */
	@Override
	public void close() throws Exception {
		int clientCount = clients.size();
		if (clientCount > 0) {
			log.log(Level.INFO, "Migrating " + clients.size() + " to Lobby");
			Iterator<ServerThread> iter = clients.iterator();
			Room lobby = server.getLobby();
			while (iter.hasNext()) {
				ServerThread client = iter.next();
				lobby.addClient(client);
				iter.remove();
			}
			log.log(Level.INFO, "Done Migrating " + clients.size() + " to Lobby");
		}
		server.cleanupRoom(this);
		name = null;
		// should be eligible for garbage collection now
	}

}