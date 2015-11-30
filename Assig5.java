import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.io.*;

/*
	Stephen Kandrack
	CS 0401, Monday/Wednesday 3:00-4:15pm
	This program will allow the user to place a vote if they are a registered
	voter by typing in his or her voter ID, making any selections on the particular ballot
	that they'd like to vote on, and submit that vote. Each voter may only vote once.
	Any ballot can be used in this program, it is up to whoever decides to initially run the program.
*/

public class Assig5 {
	private JFrame mainWindow;		// declare many of the private variables that will be used throughout the program
	private String userID = "1";
	private JButton login;
	private JButton castVote;
	private Ballot[] questions;
	private ArrayList<Voter> voters;
	private JPanel loginPanel;
	private JPanel votePanel;

	public Assig5(String fileName) {
		File ballotsFile = new File(fileName);	// Takes the name of the file read in from the command line
		try {
			Scanner readFile = new Scanner(ballotsFile);	// Read in data from the file containing the ballots
			String numQuestionsString = readFile.nextLine();
			int numQuestionsInt = Integer.parseInt(numQuestionsString);		// Determine the number of ballots in the file.
			mainWindow = new JFrame("BALLOT");								// Create the main window for the program.
			mainWindow.setLayout(new GridLayout(1, (numQuestionsInt + 2)));	// Set the format so that there is room for all of the ballots, a login button, and a vote button.
			questions = new Ballot[numQuestionsInt];	// An array of type ballot containing all of the ballots.
			String question;
			for (int i=0; i < questions.length; i++) {	// Fill the array.
				question = readFile.nextLine();
				questions[i] = new Ballot(question);
				mainWindow.add(questions[i]);
			}
		}
		catch (FileNotFoundException g){				// The program should run properly so I didn't include anything but printing the exception. same goes for all catch blocks.
			System.out.println(g);
		}
		File voterFile = new File("voters.txt");
		try {
			Scanner readVoterFile = new Scanner(voterFile);	// Start reading from the list of voters and make an array list of them.
			voters = new ArrayList<Voter>();				// Creates an arraylist containing all of the voters read in from the voters.txt file
			while (readVoterFile.hasNext()) {				//  and stores them in an object of type Voter
				String voterInfo = readVoterFile.nextLine();
				voters.add(new Voter(voterInfo));
			}
		}
		catch (FileNotFoundException i) {
			System.out.println(i);
		}
		login = new JButton("Login to Vote");			// Add the login to vote button.
		login.addActionListener(new LoginListener());	// add the login listener to the button.
		loginPanel = new JPanel();						// make a panel dedicated to that button.
		loginPanel.setLayout(new BorderLayout());		// make a border layout and put it in the center.
		loginPanel.add(login, BorderLayout.CENTER);
		mainWindow.add(loginPanel);						// Add it to the main window.
		castVote = new JButton("Cast your Vote");		// do the same thing with the voting button.
		castVote.addActionListener(new VoteListener());
		votePanel = new JPanel();
		votePanel.setLayout(new BorderLayout());
		votePanel.add(castVote, BorderLayout.CENTER);
		castVote.setEnabled(false);		// Do not allow the user access to this button until they log in.
		mainWindow.add(votePanel);
		mainWindow.pack();
		mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);	// Do not allow the user to exit the program via the window itself.
		mainWindow.setVisible(true);
	}
	
	private class LoginListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			boolean found = false;		// set a boolean expression for whether or not the voter ID entered actually exists in the voters.txt file
			boolean voted = false;		// Set a boolean expression for whether or not that voter has voted yet or not.
			String name = "default";	// sets a default expression for the name of the user; only used if the id is found
			do {
				userID = JOptionPane.showInputDialog("Please enter your voter ID.");
				for (int i = 0; i < voters.size(); i++){
					Voter instanceVoter = voters.get(i);	// searches for the id entered in the voters arraylist, and returns true if the id is found, false if it's not.
					found = instanceVoter.checkVoterID(userID);
					if (found) {
						if (!instanceVoter.hasVoted()) {
							name = instanceVoter.getName();	// store that user's name.
							break;	// Stop the loop if the ID is found.
						}
						else {	// If they have already voted, don't let them vote.
							JOptionPane.showMessageDialog(null, "You have already voted.");
							voted = true;
							break;
						}
					}
				}
			} while (!found);
			if (!voted) {	// Only do this if they haven't voted.
				JOptionPane.showMessageDialog(null, name + ", please make your choices.");
				login.setEnabled(false);	// No longer allow the user to login
				for (int n = 0; n < questions.length; n++) {
					questions[n].makeVisible();
				}
				votePanel.setEnabled(true);	// ALlow the user to choose what to vote for
				castVote.setEnabled(true);	// ALlow the user to cast his or her vote
			}
		}
	}
	
	private class VoteListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Ask the user if he or she wants to confirm their vote; they can still go back and change it if they hit no or cancel.
			int confirmation = JOptionPane.showConfirmDialog(null, "Confirm vote?", "Confirm vote?", JOptionPane.YES_NO_CANCEL_OPTION);
			if (confirmation == JOptionPane.YES_OPTION) {	// Only occurs if the user presses the yes button.
				JOptionPane.showMessageDialog(null, "Thanks for your vote!");
				for (int n = 0; n < questions.length; n++) {
					questions[n].changeToBlack();	// Resets all of the questions back so that they cannot be chosen
					questions[n].makeInvisible();	//  AND so that they are all unchosen for the next user.
				}
				castVote.setEnabled(false);	// No longer allows the user to cast a vote.
				login.setEnabled(true);		// Allows the user to log in.
				boolean found = false;
				do {
					for ( int i = 0; i < voters.size(); i++) {
						Voter instanceVoter = voters.get(i);
						found = instanceVoter.checkVoterID(userID);
						if (found) {
							instanceVoter.changeStatus();	// Changes the status of the voter from false to true so they cannot vote twice.
							break;	// ends the loop once the appropriate voter has been found.
						}
					}
				} while (!found);
				File votersFile = new File("voters.txt");	// Open the voters.txt file
				PrintWriter tempVoterFile;
				try {
					Scanner readVotersFile = new Scanner(votersFile);	// Read the voters.txt file using a Scanner
					tempVoterFile = new PrintWriter("tempVoters.txt");	// Begin to write to a new temporary file.
					while (readVotersFile.hasNext()) {
						for (int i = 0; i < voters.size(); i++) {
							Voter instanceVoter = voters.get(i);		// Create an instance of each voter from the voters arraylist
							String oldInfo = readVotersFile.nextLine();	// This will be the old info for the same voter. it may not be different from the new info
							String newInfo = "";						// if he hasn't voted yet.
							if (instanceVoter.hasVoted()) {
								newInfo = oldInfo.replace("false", "true");	// If this person had not voted yet and now voted, then their status should change to true.
							}
							else {
								newInfo = oldInfo;	// If not, their status should remain the same.
							}
							tempVoterFile.println(newInfo);	// Write this new information to the new, temporary file. it will stay the same for all voters but the one who voted.
						}
						tempVoterFile.close();
					}
					readVotersFile.close();
				}
				catch (FileNotFoundException r) {
					System.out.println(r);
				}
				try {
					File newVotersFile = new File("tempVoters.txt");			// This whole block is dedicated to writing everything from the temporary file into
					Scanner newVotersFileSC = new Scanner(newVotersFile);		// a new file with the same name as the original fle. I attempt to delete the temporary file
					PrintWriter oldVotersFile = new PrintWriter("voters.txt");	// but it doesn't work.
					while (newVotersFileSC.hasNext()) {							// The voters.txt file will now contain all updated information.
						String lineOfInfo = newVotersFileSC.nextLine();
						oldVotersFile.println(lineOfInfo);
					}
					oldVotersFile.close();
					newVotersFileSC.close();
					newVotersFile.delete();
				}
				catch (FileNotFoundException f) {
					System.out.println(f);
				}
				try {
					// This for loop reads from the array containing each of the individual ballots of type Ballot.
					for (int j = 0; j < questions.length; j++) {							// Similar to the previous voters.txt file, this block is used to
						String singleBallotID = questions[j].getBallotID();					// make a temporary file for each ballot results file that will include the new,
						File singleBallotFile = new File(singleBallotID + ".txt");			// updated info. Then the info from the temporary file will be written to a file
						Scanner readBallotFile = new Scanner(singleBallotFile);				// with the same name as the original file for each ballot, and the temporary file
						PrintWriter tempBallotFile = new PrintWriter("tempBallotFile.txt");	// will hopefully be deleted.
						String singleUserResponse = questions[j].getUserResponse();	// Retrieves the response the user gave for the specific ballot.
						while (readBallotFile.hasNext()) {
							String responseInfo = readBallotFile.nextLine();	// Retrieves info from the old ballot file and makes changes if necessary.
							String newResponseInfo = responseInfo;
							if (singleUserResponse != null) {		// Only make changes if the user actually gave a response.
								if (responseInfo.startsWith(singleUserResponse)) {							// If the user gave a response, this if statement
									int responseInfoLength = responseInfo.length();							// will take the number of votes for that particular "candidate,"
									String numVotesString = responseInfo.substring(responseInfoLength-1);	// convert it to an int, add one to it because the user voted for
									// System.out.println(numVotesString);									// it, and then change it back to a string and replace the original
									int numVotesInt = Integer.parseInt(numVotesString);						// value.
									numVotesInt++;
									String newNumVotesString = Integer.toString(numVotesInt);
									newResponseInfo = responseInfo.replace(numVotesString, newNumVotesString);
								}
							}
							tempBallotFile.println(newResponseInfo);	// This new info is then written to the temporary ballot file.
						}
						tempBallotFile.close();	// PrintWriter
						File newBallotFile = new File("tempBallotFile.txt");	// Contains the updated ballot info
						Scanner newBallotFileSC = new Scanner(newBallotFile);
						PrintWriter sBFile = new PrintWriter(singleBallotID + ".txt");	// Deletes the old ballot file with the old info and makes a new empty one for writing
						while (newBallotFileSC.hasNext()) {	// This will just copy the new info into the new file created with the printwriter above.
							String lineOfInfo = newBallotFileSC.nextLine();
							sBFile.println(lineOfInfo);
						}
						sBFile.close();				// close printwriters and scanners and attempt to delete the temporary file.
						newBallotFileSC.close();
						newBallotFile.delete();
					}
				}
				catch (FileNotFoundException a) {
					System.out.println(a);
				}
			}
		}
	}
	
	private class Ballot extends JPanel {
		public JButton[] choices;				// declare all of the variables within the ballot class.
		private JLabel category;
		private MyListener theListener;
		private String userResponse = null;
		private String ballotID = "0";
		
		public Ballot(String questionInfo) {
			String[] tokens = questionInfo.split(":");	// This will split the info regarding the question into the ID, the name of the ballot, and then the responses
			ballotID = tokens[0];
			String ballotName = tokens[1];
			category = new JLabel(ballotName);
			String responses = tokens[2];
			String[] indivResponses = responses.split(",");	// The responses will then be split themselves into individual responses.
			setLayout(new GridLayout(indivResponses.length + 1,1));	// The panel containing this particular ballot will have space for all the responses plus the name
			add(category);											// of the ballot (category) all in one big column.
			choices = new JButton[indivResponses.length];			// Creates JButton array for all of the possible rsponses.
			theListener = new MyListener();
			for (int k = 0; k < indivResponses.length; k++) {		// makes each response its own jbutton and adds the appropriate listener to all of them.
				choices[k] = new JButton(indivResponses[k]);		// They all use the same listener because they all need to be able to interact with each other.
				choices[k].addActionListener(theListener);
				choices[k].setForeground(Color.BLACK);
				choices[k].setEnabled(false);	// They should not be able to be accessed by the user until they log in.
				add(choices[k]);				// Add them to the panel.
			}
			File responseFile = new File(ballotID + ".txt");	// If the response file does not yet exist, create one for the appropriate ballot to record results.
			boolean doesFileExist = responseFile.exists();
			if (doesFileExist == false) {
				try {
					PrintWriter responseFilePrint = new PrintWriter(responseFile);
					for (int j=0; j<indivResponses.length; j++) {
						responseFilePrint.println(indivResponses[j] + ":0");
					}
					responseFilePrint.close();
				}
				catch (FileNotFoundException h) {
					System.out.println(h);
				}
			}
		}
		
		public void changeToBlack(){		// Changes all of the response jbuttons to black, so they appear unselected.
			for (int i = 0; i < choices.length; i++) {
				choices[i].setForeground(Color.BLACK);
			}
		}
		public void makeVisible() {			// enables thee jbuttons to be pressed.
			for (int k = 0; k < choices.length; k++) {
				choices[k].setEnabled(true);
			}
		}
		public void makeInvisible() 		// sets the jbuttons for the choices so that they are unable to be selected.
			for (int k = 0; k < choices.length; k++) {
				choices[k].setEnabled(false);
			}			
		}
		public String getUserResponse() {	// Returns the selection the user has made.
			return userResponse;
		}
		public void setUserResponseNull() {	// Sets the user's response to null.
			userResponse = null;
		}
		public String getBallotID() {		// returns the ID of that particular ballot.
			return ballotID;
		}
		
		private class MyListener implements ActionListener {	// This is the listener for the collection of responses for the jbuttons.
			public void actionPerformed(ActionEvent e) {
				Component theEventer = (Component) e.getSource();
				String theAction = e.getActionCommand();
				if (theEventer.getForeground() == Color.BLACK) {	// If the user clicks on an item, it changes to red, and any other option that may be red changes to black.
					theEventer.setForeground(Color.RED);
					userResponse = theAction;
					// System.out.println("CHANGED TO RED!");
					// System.out.println(userResponse);
					for (int i = 0; i < choices.length; i++){
						if (choices[i] != theEventer) {
							choices[i].setForeground(Color.BLACK);
						}
					}
				}
				else if (theEventer.getForeground() == Color.RED) {	// If a user decides to unselect their option, their option will change from red to black
					theEventer.setForeground(Color.BLACK);			// and they will no longer have a response stored.
					userResponse = null;
					// System.out.println("CHANGED BACK TO BLACK!");
					userResponse = null;
					// System.out.println("NOTHING!");
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		String ballots;
		ballots = args[0];	// read in whatever file name was typed in on the command line, set it to the variable ballots, and use that ballot file in a new instance of Assig5
		new Assig5(ballots);
	}
}