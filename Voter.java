public class Voter {
	private String voterInfoString;	// declare the variables used in the class Voter
	private String voterID;
	private String name;
	private String voterStatus;
	
	public Voter(String voterInfo) {	// Split the information for the voter to be used individually.
		voterInfoString = voterInfo;
		String[] voterInfoSplit = voterInfo.split(":");
		int firstColon = voterInfo.indexOf(":");
		voterID = voterInfo.substring(0, firstColon);
		name = voterInfoSplit[1];
		voterStatus = voterInfoSplit[2];
	}
	public boolean checkVoterID(String input) {		// I had trouble using the actual string voterID, so this works just as well.
		if (voterInfoString.startsWith(input)) {
			return true;
		}
		else {
			return false;
		}
	}
	public String getName() {
		return name;
	}
	public boolean hasVoted() {
		if (voterInfoString.endsWith("true")) {		// same issue with using voterID, I had trouble using the voterStatus variable, so this works too.
			return true;
		}
		else {
			return false;
		}
	}
	public void changeStatus() {			// After the user votes, their status changes to true.
		voterInfoString = voterInfoString.replace("false", "true");
	}
}