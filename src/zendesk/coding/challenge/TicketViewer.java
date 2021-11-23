package zendesk.coding.challenge;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;
import java.util.Scanner;

public class TicketViewer
{
	public TicketViewer()
	{
		//Displaying welcome message and options!
		System.out.println("Welcome to the Zendesk Coding Challenge Ticket Viewer!");

		while (true)
		{
			showOptions();
			Optional<String> optional = getInput();
			if (optional.isPresent())
			{
				String input = optional.get();
				switch (input)
				{
					case "1" ->
					{

					}
				}
			} else
				System.out.println("Invalid Input - Please try again!");
		}
	}

	/**
	 * Displays menu options to user!
	 */
	public void showOptions()
	{
		System.out.println("Menu Options:");
		System.out.println("\tPress 1 - View All Tickets");
		System.out.println("\tPress 2 - View Individual Ticket");
		System.out.println("\tPress 3 - Exit Application");
		System.out.print("Option: ");
	}

	/**
	 * @return {@code Optional<String>} of the users input!
	 */
	public Optional<String> getInput()
	{
		Scanner scanner = new Scanner(System.in);
		if (scanner.hasNextLine())
			return Optional.of(scanner.nextLine());
		return Optional.empty();
	}

	/**
	 *
	 * @return {@code Optional<String>} of HTTPS Json Response from Zendesk API
	 */
	public Optional<String> getHttps(String address)
	{
		try
		{
			URL url = new URL(address);
			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
			https.setRequestMethod("GET");
			https.setRequestProperty("Content-Type", "application/json");
			https.setRequestProperty("Authorization", "Basic "+getBase64("MyEmail", "MyPassword"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public String getBase64(String email, String password)
	{
		final String credentials = email+':'+password;
		return Base64.getEncoder().encodeToString(credentials.getBytes());
	}
}
