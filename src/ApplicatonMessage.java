import java.io.Serializable;


@SuppressWarnings("serial")
public class ApplicatonMessage implements Serializable{
	public String msg = "Test Message";
	int nodeId;
	int[] vectorClock;
}