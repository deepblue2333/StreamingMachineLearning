package job;

import java.io.*;
import java.net.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import api.EventCollector;
import api.Source;

class TransactionSource extends Source {
  private static final long serialVersionUID = -1791461650661455535L;

  private int instance = 0;
  private final int portBase;

  private Socket socket;
  private BufferedReader reader;

  /**
   * Construct a transaction source to receive transactions.
   * @param name The name of the source.
   * @param parallelism
   * @param port The base port. Ports from number to base port + parallelism - 1
   *     are used by the instances of this component.
   */
  public TransactionSource(String name, int parallelism, int port) {
    super(name, parallelism);

    this.portBase = port;
  }

  /**
   * Initialize an instance. This function is called from engine after the instance
   * is constructed.
   * @param instance The index of the instance.
   */
  @Override
  public void setupInstance(int instance) {
    this.instance = instance;
    setupSocketReader(portBase + instance);
  }

  @Override
  public void getEvents(EventCollector eventCollector) {
    try {
      String transaction = reader.readLine();
      if (transaction == null) {
        // Exit when user closes the server.
        System.exit(0);
      }

      float amount;
      long timeOffsetSeconds = 0;
      // The input is {amount},{merchandiseId}. For example, 42.00,3.
      try {
        String[] values = transaction.split(",");
        amount = Float.parseFloat(values[0]);
        if (values.length > 1) {
          timeOffsetSeconds = Long.parseLong(values[1]);
        }
      } catch (Exception e) {
        Logger.log("Input needs to be in this format: {amount} or {amount},{time_offset_seconds}. For example: 42.00,-3\n");
        return; // No transaction to emit.
      }

      // Assuming all transactions are from the same user. Transaction id and time are generated automatically.
      int userAccount = 1;
      String transactionId = UUID.randomUUID().toString();
      Instant transactionTime = LocalDateTime.now().plusSeconds(timeOffsetSeconds).atZone(ZoneId.systemDefault()).toInstant();
      TransactionEvent event = new TransactionEvent(transactionId, amount, transactionTime, -1, userAccount);
      eventCollector.add(event);

      Logger.log("\n");  // A empty line before logging new events.
      Logger.log("transaction (" + getName() + ") :: instance " + instance + " --> " + event + "\n");
    } catch (IOException e) {
      Logger.log("Failed to read input: " + e);
    }
  }

  /**
   * Set up a socket based reader object that reads strings from the port.
   * @param port
   */
  private void setupSocketReader(int port) {
    try {
      socket = new Socket("localhost", port);
      InputStream input = socket.getInputStream();
      reader = new BufferedReader(new InputStreamReader(input));
    } catch (UnknownHostException e) {
      e.printStackTrace();
      System.exit(0);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(0);
    }
  }
}
