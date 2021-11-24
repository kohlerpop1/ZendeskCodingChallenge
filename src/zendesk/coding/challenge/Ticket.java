package zendesk.coding.challenge;

import com.google.gson.internal.LinkedTreeMap;

public class Ticket
{
	private final int id;
	private final String date, status, subject, desc;

	public Ticket(LinkedTreeMap<Object, Object> ticket)
	{
		id = (int) Double.parseDouble(ticket.get("id").toString());
		date = ticket.get("created_at").toString();
		status = ticket.get("status").toString();
		subject = ticket.get("subject").toString();
		desc = ticket.get("description").toString();
	}

	public int getID()
	{
		return id;
	}

	public String getDate()
	{
		return date;
	}

	public String getStatus()
	{
		return status;
	}

	public String getSubject()
	{
		return subject;
	}

	public String getDesc()
	{
		return desc;
	}

	@Override
	public String toString()
	{
		return "Ticket #"+id+":\n\tStatus: "+status+"\n\tDate: "+date+"\n\tSubject: "+subject+"\n\tDescription: "+desc+'\n';
	}
}
