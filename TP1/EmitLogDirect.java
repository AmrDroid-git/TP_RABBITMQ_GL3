import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLogDirect {

    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

            String severity = getSeverity(argv);
            String message = getMessage(argv);

            channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + severity + "':'" + message + "'");
        }
    }

    private static String getSeverity(String[] argv) {
        if (argv.length < 1) {
            return "info"; // default severity
        }
        return argv[0];
    }

    private static String getMessage(String[] argv) {
        if (argv.length < 2) {
            return "Hello World!";
        }
        return joinStrings(argv, " ", 1);
    }

    private static String joinStrings(String[] strings, String delimiter, int startIndex) {
        StringBuilder result = new StringBuilder();
        for (int i = startIndex; i < strings.length; i++) {
            if (i > startIndex) {
                result.append(delimiter);
            }
            result.append(strings[i]);
        }
        return result.toString();
    }
}