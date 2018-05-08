package actors

import org.joda.time.DateTime

object Messages {
	trait Command {
		val phoneNumber: String
	}

	trait Event {
		val timestamp: DateTime
	}

	case class RegisterUser(phoneNumber: String, username: String) extends Command
	case class UserRegistred(phoneNumber: String,
													 username: String,
													 timestamp: DateTime = DateTime.now()) extends Event
	case class InvaliCommand(reason: String)
}
