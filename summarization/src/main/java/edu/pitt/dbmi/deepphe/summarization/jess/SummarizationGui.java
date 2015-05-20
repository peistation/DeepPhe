package edu.pitt.dbmi.deepphe.summarization.jess;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import jess.JessException;
import jess.Rete;
import edu.pitt.dbmi.deepphe.i2b2.I2B2DataDataWriter;
import edu.pitt.dbmi.deepphe.i2b2.PartialPath;
import edu.pitt.dbmi.deepphe.summarization.jess.kb.Patient;
import edu.pitt.dbmi.deepphe.summarization.ontology.OntologyCleaner;
import edu.pitt.dbmi.deepphe.summarization.orm.i2b2data.I2b2DataDataSourceManager;

public class SummarizationGui extends JFrame implements ActionListener, PropertyChangeListener {
	public static String PROJECT_LOCATION = "/home/tseytlin/Work/DeepPhe/";
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		if(args.length > 0)
			PROJECT_LOCATION = args[0];
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		new SummarizationGui("DeepPhe Summarization");
	}
	
	private final TreeSet<PartialPath> partialPathTreeSet = new TreeSet<PartialPath>();
	private final HashMap<String, PartialPath> partialPathMap = new HashMap<>();
	private final Rete engine = new Rete();
	private final PatientKnowledgeExtractor patientKnowledgeExtractor = new JessPatientKnowledgeExtractor();
	private final JessTextInputer jessInputer = new JessTextInputer();
	private final JessTextOutputer jessOutputer = new JessTextOutputer();
	private final PatientListReader patientListReader = new PatientListReader();
	private final List<Patient> patients = new ArrayList<>();

	private WindowAdapter windowAdapter;

	private JPanel mainPanel;
	private ImageIcon iconOne =  new ImageIcon(
			SummarizationGui.class.getResource("/images/24/dashboardico.gif"));
	private JTabbedPane mainTabbedPane = new JTabbedPane();
	private AnnotationTabPanel annotationTabPanel;
	
	private OntologyCleaner ontologyCleaner;
	private PatientExtractionPipeDialog patientExtractor;
	
	public SummarizationGui(String title) {
		super(title);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		SummarizationMenu mainMenu = new SummarizationMenu();
		mainMenu.setActionListener(this);
		mainMenu.injectActionListener();
		setJMenuBar(mainMenu);
		
		establishWindowControls();
		
		buildMainPanel();
		JPanel mainPanel = getMainPanel();
		
		getContentPane().add(mainPanel);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void establishWindowControls() {
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
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				System.exit(0);
			}
		};

		addWindowListener(windowAdapter);
	}

	private void buildMainPanel() {
		mainPanel = new JPanel();
		
		annotationTabPanel = new AnnotationTabPanel();
		mainTabbedPane.addTab("Annotation", iconOne, annotationTabPanel, "AnnotateTab");
		mainTabbedPane.setSelectedIndex(0);
		
		annotationTabPanel.setPatients(patients);
		annotationTabPanel.setPatientListReader(patientListReader);
		annotationTabPanel.build();
			
		InferenceTabPanel inferenceTabPanel = new InferenceTabPanel();
		mainTabbedPane.addTab("Inference", iconOne, inferenceTabPanel, "InferenceTab");
		
		establishExtractor();
		patientKnowledgeExtractor.setJessTextOutputer(jessOutputer);
		jessInputer.setJessTextOutputer(jessOutputer);
		jessInputer.setKnowledgeExtractor(patientKnowledgeExtractor);
		inferenceTabPanel.setJessInputer(jessInputer);
		inferenceTabPanel.setJessOutputer(jessOutputer);
		inferenceTabPanel.setKnowledgeExtractor(patientKnowledgeExtractor);
		inferenceTabPanel.setEngine(engine);
		inferenceTabPanel.setPatients(patients);
		inferenceTabPanel.build();
		
		mainPanel.setLayout(new GridLayout(1, 1));
		mainPanel.add(mainTabbedPane);
		mainPanel.setPreferredSize(new Dimension(1200, 900));
	}
	
	private void establishExtractor() {
		try {
			engine.reset();
			patientKnowledgeExtractor.setJessEngine(engine);
			patientKnowledgeExtractor.loadProductionClipsFiles();
			patientKnowledgeExtractor.setPatients(patients);	
			
			EncounterKnowlegeExractorFactory.setEncounterKnowledgeExtractor(new FhirEncounterKnowledgeExtractor());
		} catch (JessException e) {
			e.printStackTrace();
		}
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
		} else if (actionCommand.equals("Summarize All Patients")) {
			processExtractPatient();
		} else if (actionCommand.equals("Load Single Patient To KB")) {
			processLoadSinglePatientToKb();
		}  else if (actionCommand.equals("Extract Encounters")) {
			processExtractEncounters();
		} else if (actionCommand.equals("Ontology Clean")) {
			processOntologyClean();
		} else if (actionCommand.equals("Patient Clean")) {
			processPatientClean();
		} else if (actionCommand.equals("Reset")) {
			patientKnowledgeExtractor.executeJess("(reset)");
		} else if (actionCommand.equals("Clear")) {
			patientKnowledgeExtractor.clearJess();
		} else if (actionCommand.equals("Eval")) {
			patientKnowledgeExtractor.executeJess(jessInputer.geSelectedText());
		} else if (actionCommand.equals("Run")) {
			patientKnowledgeExtractor.executeJess("(run)");
		} else if (e.getActionCommand().equals("Templates")) {
			patientKnowledgeExtractor.displayDeftemplates();
		} else if (e.getActionCommand().equals("Facts")) {
			patientKnowledgeExtractor.displayFacts();
		}
	}
	
	private void processExtractEncounters() {
		EncounterKnowledgeExtractor encounterKnowledgeExtractor = new
				CtakesEncounterKnowledgeExtractor();
		for (Patient patient : patients) {
			encounterKnowledgeExtractor.setPatient(patient);
			encounterKnowledgeExtractor.execute();
		}		
	}

	private void processExtractPatient() {	
		patientExtractor = new PatientExtractionPipeDialog(this);
		patientExtractor.setAnnotationTabPanel(annotationTabPanel);
		patientExtractor.setKnowledgeExtractor(patientKnowledgeExtractor);
		patientExtractor.setPatients(patients);
		patientExtractor.setPartialPathTreeSet(partialPathTreeSet);
		patientExtractor.setPartialPathMap(partialPathMap);
		patientExtractor.addPropertyChangeListener(this);
		patientExtractor.setVisible(true);
		(new Thread(patientExtractor)).start();
	}
	
	private void processLoadSinglePatientToKb() {
		EncounterKnowledgeExtractor encounterKnowledgeExtractor = EncounterKnowlegeExractorFactory.getEncounterKnowledgeExtractor();
		for (Patient patient : patients) {
			encounterKnowledgeExtractor.setPatient(patient);
			encounterKnowledgeExtractor.execute();
			break;
		}
		patientKnowledgeExtractor.setPatients(patients);
		patientKnowledgeExtractor.iteratePatients();
		if (patientKnowledgeExtractor.hasMorePatients()) {
			patientKnowledgeExtractor.nextPatient();
			patientKnowledgeExtractor.loadSinglePatient();
		}
	}

	private void processPatientClean() {
		try {
			final I2b2DataDataSourceManager i2b2DataDataSourceManager = new I2b2DataDataSourceManager();
			final I2B2DataDataWriter i2b2DataDataWriter = new I2B2DataDataWriter();
			i2b2DataDataWriter.setDataSourceMgr(i2b2DataDataSourceManager);
			i2b2DataDataWriter.setSourceSystemCd("DEEPPHE2");
			i2b2DataDataWriter.execute();
			i2b2DataDataSourceManager.destroy();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void processOntologyClean() {		
		partialPathTreeSet.clear();
		partialPathMap.clear();
		ontologyCleaner = new OntologyCleaner(this);
		ontologyCleaner.setPartialPathTreeSet(partialPathTreeSet);
		ontologyCleaner.setPartialPathMap(partialPathMap);
		ontologyCleaner.addPropertyChangeListener(this);
		ontologyCleaner.setVisible(true);
		(new Thread(ontologyCleaner)).start();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == ontologyCleaner && evt.getNewValue().equals("Finished")) {
			ontologyCleaner.dispose();
		}
		else if (evt.getSource() == patientExtractor && evt.getNewValue().equals("Finished")) {
			patientExtractor.dispose();
		}
	}

	private void closeWindow() {
		WindowEvent closingEvent = new WindowEvent(this,
				WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue()
				.postEvent(closingEvent);
	}
}