import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class NumberGuesserHW {
	private int level = 1;
	private int strikes = 0;
	private int maxStrikes = 5;
	private int number = 0;
	private boolean isRunning = false;
	final String saveFile = "numberGuesserSave.txt";

	/***
	 * Gets a random number between 1 and level.
	 * 
	 * @param level (level to use as upper bounds)
	 * @return number between bounds
	 */
	public static int getNumber(int level) {
		int range = 9 + ((level - 1) * 5);
		System.out.println("I picked a random number between 1-" + (range + 1) + ", let's see if you can guess.");
		return new Random().nextInt(range) + 1;
	}

	private void win() {
		System.out.println("That's right!");
		level++;// level up!
		strikes = 0;
		System.out.println("Welcome to level " + level);
		number = getNumber(level);
		saveLevel(); // moved this down at the end to keep a more accurate save state after each
						// action
	}

	private void lose() {
		System.out.println("Uh oh, looks like you need to get some more practice.");
		System.out.println("The correct number was " + number);
		strikes = 0;
		level--;
		if (level < 1) {
			level = 1;
		}
		number = getNumber(level);
		saveLevel(); // moved this down at the end to keep a more accurate save state after each
						// action
	}

	// i think i could solve the hw by just modifying this function so that it saves
	// when we quit from the session but i want to make it so that if the user
	// closes the terminal improperly that it saves automatically like any video
	// game would now
	private void processCommands(String message) {
		if (message.equalsIgnoreCase("quit")) {
			System.out.println("Tired of playing? No problem, see you next time.");
			isRunning = false;
		}
	}

	private void processGuess(int guess) {
		if (guess < 0) {
			return;
		}
		System.out.println("You guessed " + guess);
		if (guess == number) {
			win();
		} else {
			System.out.println("That's wrong");
			strikes++;
			if (strikes >= maxStrikes) {
				lose();
			} else {
				int remainder = maxStrikes - strikes;
				System.out.println("You have " + remainder + "/" + maxStrikes + " attempts remaining");
				if (guess > number) {
					System.out.println("Lower");
				} else if (guess < number) {
					System.out.println("Higher");
				}
			}
		}
		saveLevel(); // need to record strikes and number here
	}

	private int getGuess(String message) {
		int guess = -1;
		try {
			guess = Integer.parseInt(message);
		} catch (NumberFormatException e) {
			System.out.println("You didn't enter a number, please try again");

		}
		return guess;
	}

	private void saveLevel() {
		try (FileWriter fw = new FileWriter(saveFile)) {
			fw.write("" + level);// here we need to convert it to a String to record correctly
			fw.write("\n" + strikes); // added a new line to record the number of strikes
			fw.write("\n" + number); // added a new line to record what the number was
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean loadLevel() {
		File file = new File(saveFile);
		if (!file.exists()) {
			return false;
		}
		try (Scanner reader = new Scanner(file)) {
			while (reader.hasNextLine()) {
				int _level = reader.nextInt();
				int _strikes = reader.nextInt(); // added a way to read the file to get the number of strikes
				strikes = _strikes; // sets strikes after loading from save file
				int _number = reader.nextInt(); // added a way to read the file to get the number
				number = _number; // sets number after loading from save file
				if (_level > 1) {
					level = _level;
					break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e2) {
			e2.printStackTrace();
			return false;
		}
		return level > 1;
	}

	// making a reset function here that is prompted when the program runs
	private void reset() {
		System.out.println("Do you want to start at level 1 (Y/N)?");
		Scanner inp = new Scanner(System.in);
		String msg = inp.nextLine();
		if (msg.equalsIgnoreCase("y")) {
			try (FileWriter rst = new FileWriter(saveFile)) {
				rst.write("" + 1);
				rst.write("\n" + 0);
				number = new Random().nextInt(9) + 1;
				rst.write("\n" + number);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	void run() {
		try (Scanner input = new Scanner(System.in);) {
			System.out.println("Welcome to Number Guesser 4.0!");
			reset(); // added the reset function
			loadLevel(); // need to use here to show the correct amount of guesses left
			System.out.println("I'll ask you to guess a number between a range, and you'll have "
					+ (maxStrikes - strikes) + " attempts to guess."); // updated string to output the correct amount of
																		// strikes left
			if (loadLevel()) {
				System.out.println("Successfully loaded level " + level + ", let's continue then");
			}
			// added this from the getNumber function to make it easier to understand when
			// the program runs the first time
			System.out.println("Welcome to level " + level);
			int rangez = 9 + ((level - 1) * 5);
			System.out.println("I picked a random number between 1-" + (rangez + 1) + ", let's see if you can guess.");
			// number = getNumber(level); this is not necessary anymore i think since you
			// are picking up from the last session
			isRunning = true;
			while (input.hasNext()) {
				String message = input.nextLine();
				processCommands(message);
				if (!isRunning) {
					break;
				}
				int guess = getGuess(message);
				processGuess(guess);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		NumberGuesserHW guesser = new NumberGuesserHW();
		guesser.run();
	}
}