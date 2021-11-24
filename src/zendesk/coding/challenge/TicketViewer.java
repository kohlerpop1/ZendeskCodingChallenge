package zendesk.coding.challenge;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.*;

public class TicketViewer
{
	private final List<Ticket> tickets;

	public TicketViewer()
	{
		//Displaying welcome message and options!
		System.out.println();
		System.out.println("Welcome to the Zendesk Coding Challenge Ticket Viewer!");
		System.out.println();

		//Upon startup of the program, we know the user will be requesting tickets, so we shall cache them immediately in the background.
		tickets = new ArrayList<>();
		cacheTickets("https://zccdavidwkohler.zendesk.com/api/v2/tickets");

		while (true)
		{
			System.out.println("Menu Options:");
			System.out.println("\tPress 1 - View All Tickets");
			System.out.println("\tPress 2 - View Individual Ticket");
			System.out.println("\tPress 3 - Exit Application");
			System.out.print("Option: ");

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
							openAllTickets(0);
						} else
							System.out.println("The Zendesk API is currently unavailable! Please try again later!");
					}

					case "2" ->
					{
						System.out.print("Enter Ticket ID: ");
						Optional<String> ticketID = getInput();
						if (ticketID.isPresent())
						{
							try {
								int number = Integer.parseInt(ticketID.get());
								Optional<Ticket> ticketOption = tickets.stream().filter(ticket -> ticket.getID() == number).findAny();
								if (ticketOption.isPresent())
								{
									System.out.println();
									System.out.println(ticketOption.get());
								}
								else
									System.out.println("Invalid Response! Please enter a number between 1 and "+tickets.size()+"!");
							} catch (NumberFormatException e) {
								System.out.println("Invalid Response! Please enter a number between 1 and "+tickets.size()+"!");
							}
						} else
							System.out.println("Invalid Response! Please enter a number between 1 and "+tickets.size()+"!");
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
		if (!url.isEmpty())
		{
			try
			{
				HttpsURLConnection https = (HttpsURLConnection) new URL(url).openConnection();

				https.setRequestMethod("GET");
				https.setRequestProperty("Content-Type", "application/json");
				https.setRequestProperty("Accept", "application/json");
				https.setRequestProperty("Authorization", "Basic " + getBase64("MyEmail", "MyPassword"));
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
	private void cacheTickets(String url)
	{
		new Thread(()->
		{
			Optional<String> httpsOption = getResponse(url);
			if (httpsOption.isPresent())
			{
				String json = httpsOption.get();
				LinkedTreeMap<Object, Object> map = new Gson().fromJson(json, LinkedTreeMap.class);

				for (LinkedTreeMap ticket : (List<LinkedTreeMap>) map.get("tickets"))
					tickets.add(new Ticket(ticket));

				if (map.containsKey("next_page"))
					cacheTickets(map.get("next_page") instanceof String next ? next : "");
			}
		}).start();
	}

	private void openAllTickets(int offset)
	{
		for (int i = 0; i < 25 && tickets.size()>i+offset; i++)
			System.out.println(tickets.get(i+offset));
		System.out.println("N: Next Page - B: Previous Page - Q: Main Menu");
		System.out.print("Selection: ");
		Optional<String> input = getInput();
		if (input.isPresent())
		{
			switch (input.get().toLowerCase())
			{
				case "n" -> offset=Math.min(tickets.size(), offset+25);
				case "b" -> offset=Math.max(0, offset-25);
				default -> offset = -1;
			}

			if (offset >= 0)
				openAllTickets(offset);
		}
	}
}
