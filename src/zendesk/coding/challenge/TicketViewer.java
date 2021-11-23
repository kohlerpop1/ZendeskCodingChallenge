package zendesk.coding.challenge;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.*;

public class TicketViewer
{
	public TicketViewer()
	{
		//Displaying welcome message and options!
		System.out.println("Welcome to the Zendesk Coding Challenge Ticket Viewer!");

		while (true)
		{
			showOptions();
			Optional<String> inputOption = getInput();
			if (inputOption.isPresent())
			{
				String input = inputOption.get();
				switch (input)
				{
					case "1" ->
					{
						Optional<String> httpsOption = getResponse("https://zccdavidwkohler.zendesk.com/api/v2/tickets.json");
						if (httpsOption.isPresent())
						{
							String json = httpsOption.get();
						} else
							System.out.println("The Zendesk API is currently unavailable! Please try again later!");
					}
				}
			} else
				System.out.println("Invalid Response! Please try again!");
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
	 * @return {@code Optional<String>} of HTTPS Json Response from Zendesk API
	 */
	public Optional<String> getResponse(String url)
	{
		try
		{
			HttpsURLConnection https = (HttpsURLConnection) new URL(url).openConnection();

			https.setRequestMethod("GET");
			https.setRequestProperty("Content-Type", "application/json");
			https.setRequestProperty("Accept", "application/json");
			https.setRequestProperty("Authorization", "Basic "+getBase64("MyEmail", "MyPassword"));
			if (https.getResponseCode() == 200)
			{
				https.disconnect();
				return Optional.of(https.getResponseMessage());
			}
		} catch (Exception e)
		{
			System.out.println("There was an error in parsing the url request.");
		}
		return Optional.empty();
	}

	/**
	 * @return Base64 encoding of entered email and password
	 */
	public String getBase64(String email, String password)
	{
		final String credentials = email+':'+password;
		return Base64.getEncoder().encodeToString(credentials.getBytes());
	}
}
