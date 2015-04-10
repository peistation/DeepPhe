package edu.pitt.dbmi.deepphe.summarization.jess;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class SummarizationGui extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		new SummarizationGui("DeepPhe Summarization");
	}

	private final TnmExtractor tmnExtractor = new TnmExtractor();

	private WindowAdapter windowAdapter;

	private JMenuBar mainMenu = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem fileMenuLoadJcasItem = new JMenuItem("Load JCas");
	private JMenuItem fileMenuDisplayTextItem = new JMenuItem("Display Text");
	private JMenuItem fileMenuExitItem = new JMenuItem("Exit");
	private JMenu jessMenu = new JMenu("Jess");
	private JMenuItem jessMenuResetItem = new JMenuItem("Reset");
	private JMenuItem jessMenuEvalItem = new JMenuItem("Eval");
	private JMenuItem jessMenuRunItem = new JMenuItem("Run");
	private JMenu displayMenu = new JMenu("Display");
	private JMenuItem displayMenuDefTemplatesItem = new JMenuItem("Templates");
	private JMenuItem displayMenuFactsItem = new JMenuItem("Facts");
	private JPanel mainPanel = new JPanel();
	private JPanel fileChooserPanel = new JPanel();

	private JessTextInputer jessInputer;
	private JessTextOutputer jessOutputer;

	public SummarizationGui(String title) {
		super(title);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		buildMainMenu();
		setJMenuBar(mainMenu);
		initFrame();
		composePanels();
		establishExtractor();
		JPanel mainPanel = getMainPanel();
		getContentPane().add(mainPanel);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void initFrame() {

		windowAdapter = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				// You can still stop closing if you want to
				int res = JOptionPane.showConfirmDialog(SummarizationGui.this,
						"Are you sure you want to close?", "Close?",
						JOptionPane.YES_NO_OPTION);
				if (res == 0) {
					// dispose method issues the WINDOW_CLOSED event
					SummarizationGui.this.dispose();
				}
			}

			// WINDOW_CLOSED event handler
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				System.exit(0);
			}
		};

		addWindowListener(windowAdapter);
	}

	public JMenuBar buildMainMenu() {

		mainMenu.add(fileMenu);
		mainMenu.add(jessMenu);
		mainMenu.add(displayMenu);

		fileMenu.add(fileMenuLoadJcasItem);
		fileMenu.add(fileMenuDisplayTextItem);
		fileMenu.add(fileMenuExitItem);

		jessMenu.add(jessMenuResetItem);
		jessMenu.add(jessMenuEvalItem);
		jessMenu.add(jessMenuRunItem);

		displayMenu.add(displayMenuDefTemplatesItem);
		displayMenu.add(displayMenuFactsItem);

		fileMenuLoadJcasItem.addActionListener(this);
		fileMenuDisplayTextItem.addActionListener(this);
		fileMenuExitItem.addActionListener(this);
		jessMenuResetItem.addActionListener(this);
		jessMenuEvalItem.addActionListener(this);
		jessMenuRunItem.addActionListener(this);
		displayMenuDefTemplatesItem.addActionListener(this);
		displayMenuFactsItem.addActionListener(this);

		return mainMenu;
	}

	private void establishExtractor() {
		tmnExtractor.execute();
	}

	public void composePanels() {
		buildMainPanel();

	}

	private void buildMainPanel() {

		mainPanel.setLayout(new BorderLayout());

		Border border = BorderFactory.createTitledBorder("Reports");
		ReportChooserPanel fileChooser = new ReportChooserPanel();
		fileChooser.setGui(this);
		fileChooser.initialize();
		fileChooserPanel = fileChooser.getPanel();
		fileChooserPanel.setBorder(border);
		mainPanel.add(fileChooserPanel, BorderLayout.WEST);

		jessOutputer = new JessTextOutputer();

		jessInputer = new JessTextInputer();

		tmnExtractor.setJessTextOutputer(jessOutputer);
		jessInputer.setJessTextOutputer(jessOutputer);
		jessInputer.setTmnExtractor(tmnExtractor);

		mainPanel.add(jessInputer, BorderLayout.CENTER);
		mainPanel.add(jessOutputer, BorderLayout.SOUTH);

		mainPanel.setPreferredSize(new Dimension(1200, 900));

	}

	public void loadReportWiget(ReportWidget fileWidget) {
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(JPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		System.out.println(actionCommand);
		if (actionCommand.equals("Exit")) {
			closeWindow();
		} else if (actionCommand.equals("Load JCas")) {
			tmnExtractor.loadJCas();
		} else if (actionCommand.equals("Display Text")) {
			jessOutputer.appendText("Display Text needs implemented");
		} else if (actionCommand.equals("Reset")) {
			tmnExtractor.executeJess("(reset)");
		} else if (actionCommand.equals("Clear")) {
			tmnExtractor.clearJess();
		} else if (actionCommand.equals("Eval")) {
			tmnExtractor.executeJess(jessInputer.geSelectedText());
		} else if (actionCommand.equals("Run")) {
			tmnExtractor.executeJess("(run)");
		} else if (e.getActionCommand().equals("Templates")) {
			tmnExtractor.displayDeftemplates();
		} else if (e.getActionCommand().equals("Facts")) {
			tmnExtractor.displayFacts();
		}
	}

	private void closeWindow() {
		WindowEvent closingEvent = new WindowEvent(this,
				WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue()
				.postEvent(closingEvent);
	}

}