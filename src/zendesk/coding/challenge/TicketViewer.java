package zendesk.coding.challenge;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.*;

public class TicketViewer
{
	private List<Ticket> tickets;
	public TicketViewer()
	{
		//Displaying welcome message and options!
		System.out.println("Welcome to the Zendesk Coding Challenge Ticket Viewer!");

		//Upon startup of the program, we know the user will be requesting tickets, so we shall cache them immediately in the background.
		tickets = new ArrayList<>();
		cacheTickets();

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
						if (!tickets.isEmpty())
						{

						} else
							System.out.println("The Zendesk API is currently unavailable! Please try again later!");
					}

					case "2" ->
					{
						Optional<String> ticketNumber = getInput();
						if (ticketNumber.isPresent())
						{

						}
					}

					case "3" ->
					{
						System.out.println("Thanks for using our service. Have a nice day!");
						System.exit(0);
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
			https.setRequestProperty("Authorization", "Basic "+getBase64("", ""));
			if (https.getResponseCode() == 200)
			{
				String response = new String(https.getInputStream().readAllBytes());
				https.disconnect();
				return Optional.of(response);
			}
		} catch (Exception e)
		{
			System.out.println("There was an error in parsing the url request.");
			e.printStackTrace();
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

	/**
	 * Caching tickets in background for smoother user experience
	 */
	private void cacheTickets()
	{
		new Thread(()->
		{
			Optional<String> httpsOption = getResponse("https://zccdavidwkohler.zendesk.com/api/v2/tickets/count");
			if (httpsOption.isPresent())
			{
				String json = httpsOption.get();
				LinkedTreeMap<Object, LinkedTreeMap> map = new Gson().fromJson(json, LinkedTreeMap.class);
				double ticketCount = (double) map.get("count").get("value");
				for (int i = 1; i <= ticketCount; i++)
				{
					int finalI = i;
					new Thread(()-> getResponse("https://zccdavidwkohler.zendesk.com/api/v2/tickets/"+finalI).ifPresent(s -> tickets.add(new Ticket(s)))).start();
				}

				try {
					//Sleeping for 1/10th of a second in the background thread to allow all tickets to load
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

//				tickets.sort();
			}
		}).start();
	}
}
