package com.infy.MQSenderGUI;

import java.awt.*; // Using AWT containers and components
import java.awt.event.*; // Using AWT events and listener interfaces
import javax.swing.*; // Using Swing components and containers
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import com.ibm.jms.JMSMessage;
import com.ibm.jms.JMSTextMessage;
import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQQueueReceiver;
import com.ibm.mq.jms.MQQueueSender;
import com.ibm.mq.jms.MQQueueSession;
public class MQSenderGUI extends JFrame {
	private JTextField tfInput, tfInputIP, tfInputChannel, tfInputQMgr,
			tfInputMQ, tfOutput,tfInputPort;
	private JTextArea textAreaMessage = new JTextArea();
	private JScrollPane jScrollPane1;

	/** Constructor to setup the GUI */
	public MQSenderGUI() {
		// Retrieve the content-pane of the top-level container JFrame
		// All operations done on the content-pane
		JPanel panelHeader = new JPanel(new FlowLayout());
		panelHeader.setLayout(new BorderLayout());
		panelHeader.add(new JLabel("Configuration Settings",
				SwingConstants.LEFT));
		JPanel panelConfig = new JPanel(new GridLayout(5, 2, 5, 10));
		panelConfig.add(new JLabel("Server IP: "));
		tfInputIP = new JTextField(10);
		panelConfig.add(tfInputIP);
		panelConfig.add(new JLabel("Sender Channel: "));
		tfInputChannel = new JTextField(10);
		panelConfig.add(tfInputChannel);
		panelConfig.add(new JLabel("Queue Manager: "));
		tfInputQMgr = new JTextField(10);
		panelConfig.add(tfInputQMgr);
		panelConfig.add(new JLabel("MQ Queue: "));
		tfInputMQ = new JTextField(10);
		panelConfig.add(tfInputMQ);
		tfInputPort = new JTextField(10);
		panelConfig.add(tfInputPort);		
		JPanel panelMessage = new JPanel(new FlowLayout());

		panelMessage.add(new JLabel("Message: "));
		textAreaMessage = new JTextArea();
		textAreaMessage.setColumns(30);
		textAreaMessage.setLineWrap(false);
		textAreaMessage.setRows(10);
		textAreaMessage.setWrapStyleWord(false);
		textAreaMessage.setEditable(true);
		jScrollPane1 = new JScrollPane(textAreaMessage);
		panelMessage.add(textAreaMessage);
		JButton btnSend = new JButton("SEND");
		panelMessage.add(btnSend);
		JButton btnReset = new JButton("CLEAR");
		panelMessage.add(btnReset);
		this.setLayout(new BorderLayout());
		panelHeader.add(panelConfig, BorderLayout.SOUTH);
		this.add(panelHeader, BorderLayout.NORTH);
		// this.add(panelConfig, BorderLayout.NORTH);
		this.add(panelMessage, BorderLayout.CENTER);

		/*
		 * add(new JLabel("The Accumulated Sum is: ")); tfOutput = new
		 * JTextField(10); tfOutput.setEditable(false); // read-only
		 * add(tfOutput);
		 */

		// Allocate an anonymous instance of an anonymous inner class that
		// implements ActionListener as ActionEvent listener
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Get the String entered into the input TextField, convert to
				// int
				MQSenderDAO dao = new MQSenderDAO();
				dao.setInputIP(tfInputIP.getText());
				dao.setInputChannel(tfInputChannel.getText());
				dao.setInputQMgr(tfInputQMgr.getText());
				dao.setInputMQ(tfInputMQ.getText());
				dao.setInputPort(tfInputPort.getText());
				dao.setInputMessage(textAreaMessage.getText());
				SendMessage(dao);
			}
		});
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textAreaMessage.setText("");
			}
		});

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit program if
														// close-window button
														// clicked
		setTitle("MQ Sender Tool"); // "this" Frame sets title
		setSize(350, 480); // "this" Frame sets initial size
		setVisible(true); // "this" Frame shows
	}

	public String SendMessage(MQSenderDAO dao) {
		String InputIP = dao.getInputIP();
		String InputChannel = dao.getInputChannel();
		String InputQMgr = dao.getInputQMgr();
		String InputMQ = dao.getInputMQ();
		String InputMessage = dao.getInputMessage();
		String InputPort = dao.getInputPort();
		/* Write Code to Send Message here */
		try {
		MQQueueConnectionFactory cf = new MQQueueConnectionFactory();
		// Config
	      cf.setHostName(InputIP);
	      cf.setPort(1414);
	      cf.setTransportType(1);
	      cf.setQueueManager(InputQMgr);
	      cf.setChannel(InputChannel);
	      String QueueAddr = "queue:///"+InputMQ+"?targetClient=1";
	      javax.jms.QueueSession session = null;
	      javax.jms.QueueSender sender = null;
	      javax.jms.QueueConnection connection = cf.createQueueConnection();
	      session = connection.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
	      Queue queue =  session.createQueue(QueueAddr);
	      sender =  session.createSender(queue);
	      MQQueueReceiver receiver = (MQQueueReceiver) session.createReceiver(queue); 
	      javax.jms.Message  message = session.createTextMessage();
	      ((javax.jms.TextMessage)message).setText(InputMessage);
	   // Start the connection
	      connection.start();
	      sender.send(message);
	      System.out.println("Sent message:\\n" + message);

	      JMSMessage receivedMessage = (JMSMessage) receiver.receive(10000);
	      System.out.println("\\nReceived message:\\n" + receivedMessage);

	      sender.close();
	      receiver.close();
	      session.close();
	      connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** The entry main() method */
	public static void main(String[] args) {
		// Run the GUI construction in the Event-Dispatching thread for
		// thread-safety
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MQSenderGUI();
				// Let the constructor do the job
			}
		});
	}
}
