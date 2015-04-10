package edu.pitt.dbmi.deepphe.i2b2;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import static org.semanticweb.owlapi.search.Searcher.annotations;


/**
 *
 * @author kjm84
 */
public class I2b2OntologyBuilder {

    public static final String pizza_iri_path = "http://protege.stanford.edu/ontologies/pizza/pizza.owl";
    public static final String owl_iri_path = "http://www.semanticweb.org/ontologies/ont.owl";

    public static final IRI pizza_iri = IRI
            .create(pizza_iri_path);
    public static final IRI example_iri = IRI
            .create(owl_iri_path);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        I2b2OntologyBuilder ontologyBuilder = new I2b2OntologyBuilder();
        try {
            ontologyBuilder.execute();
        } catch (OWLOntologyCreationException | OWLOntologyStorageException | IOException ex) {
            Logger.getLogger(I2b2OntologyBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void execute() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
        // examineDeepPheOntology();
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = loadDeepPheOntology(m);
        System.out.println("\n\nTrace Carboplatin super classes...");
        traceSuperClses(o, IRI.create("http://slidetutor.upmc.edu/deepPhe/BreastCancer.owl#Carboplatin"));
        System.out.println("\n\nTrace Medication sub classes...");
        traceSubClses(o, IRI.create("http://blulab.chpc.utah.edu/ontologies/SchemaOntology.owl#Medication"));
        System.out.println("\n\nTrace I_03074 sub classes...");
        traceSubClses(o, IRI.create("http://blulab.chpc.utah.edu/ontologies/SchemaOntology.owl#I_03074"));
        System.out.println("\n\nTrace Entity sub classes...");
        traceSubClses(o, IRI.create("http://blulab.chpc.utah.edu/ontologies/SchemaOntology.owl#Entity"));
        System.out.println("\n\nTrace Patient super classes...");
        traceSubClses(o, IRI.create("http://blulab.chpc.utah.edu/ontologies/SchemaOntology.owl#Patient"));
    }

    private void doRun() throws OWLOntologyCreationException, IOException {
        final TreeSet<PartialPath> partialPaths = extractOntologyPartialPaths();
        displayPaths(partialPaths);
        loadI2B2(partialPaths);
    }

    private TreeSet<PartialPath> extractOntologyPartialPaths() throws OWLOntologyCreationException, IOException {

        final TreeSet<PartialPath> partialPaths = new TreeSet<PartialPath>();

        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        //OWLOntology o = m.loadOntologyFromOntologyDocument(pizza_iri);
        OWLOntology o = loadDeepPheOntology(m);
        OWLReasonerFactory reasonerFactory;
        reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(o);
        OWLDataFactory fac = m.getOWLDataFactory();
        OWLClass domainConcept = fac.getOWLClass(IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#DomainConcept"));
        OWLClass elementConcept = fac.getOWLClass(IRI.create("http://blulab.chpc.utah.edu/ontologies/SchemaOntology.owl#Element"));

        final Queue<PartialPath> partialPathQueue = new LinkedList<PartialPath>();
        NodeSet<OWLClass> subClses = reasoner.getSubClasses(elementConcept, true);
        for (Node<OWLClass> subCls : subClses) {
            PartialPath path = new PartialPath();
            path.setCls(subCls.getRepresentativeElement());
            path.setReasoner(reasoner);
            path.setLevel(1);
            partialPathQueue.add(path);
        }

        while (true) {
            PartialPath path;
            path = partialPathQueue.poll();
            if (path == null) {
                break;
            } else if (path.isExpandable()) {
                partialPathQueue.addAll(path.expand());
            }
            partialPaths.add(path);
        }

        PartialPath topLevel = new PartialPath();
        topLevel.setPath("\\DEEPPHE");
        topLevel.setLevel(0);
        partialPaths.add(topLevel);

        return partialPaths;
    }

    private void examineDeepPheOntology() throws IOException, OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        //OWLOntology o = m.loadOntologyFromOntologyDocument(pizza_iri);
        OWLOntology o = loadDeepPheOntology(m);
        OWLReasonerFactory reasonerFactory;
        reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(o);
        Node<OWLClass> topNode = reasoner.getTopClassNode();
        Queue<OWLClass> clsQueue = new LinkedList<OWLClass>();
        clsQueue.add(topNode.getRepresentativeElement());
        while (true) {
            OWLClass cls = clsQueue.poll();
            if (cls == null) {
                break;
            } else {
                System.out.println(cls.getIRI().toString());
                for (Node<OWLClass> subClsNode : reasoner.getSubClasses(cls, true)) {
                    clsQueue.add(subClsNode.getRepresentativeElement());
                }
            }
        }
    }

    private void traceSuperClses(OWLOntology o, IRI iri) throws IOException, OWLOntologyCreationException {
        OWLReasonerFactory reasonerFactory;
        reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(o);
        OWLDataFactory fac = o.getOWLOntologyManager().getOWLDataFactory();
        OWLClass tgtCls = fac.getOWLClass(iri);
        Queue<OWLClass> clsQueue = new LinkedList<OWLClass>();
        clsQueue.add(tgtCls);
        while (true) {
            OWLClass cls = clsQueue.poll();
            if (cls == null) {
                break;
            } else {
                System.out.println("Class: " + cls.getIRI().toString());
                System.out.println("rdfs:Label " + labelFor(o, cls));
                reasoner.getSuperClasses(cls, true).getFlattened().stream().forEach((superCls) -> {
                    clsQueue.add(superCls);
                });
            }
        }
    }

    private String labelFor(OWLOntology ontology, OWLClass clazz) {
        /*
         * Use a visitor to extract label annotations
         */
        LabelExtractor le = new LabelExtractor();
        for (OWLAnnotation anno : annotations(ontology
                .getAnnotationAssertionAxioms(clazz.getIRI()))) {
            anno.accept(le);
        }
        /* Print out the label if there is one. If not, just use the class URI */
        if (le.getResult() != null) {
            return le.getResult();
        } else {
            return clazz.getIRI().toString();
        }
    }

    private void traceSubClses(OWLOntology o, IRI iri) throws IOException, OWLOntologyCreationException {
        OWLReasonerFactory reasonerFactory;
        reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(o);
        OWLDataFactory fac = o.getOWLOntologyManager().getOWLDataFactory();
        OWLClass tgtCls = fac.getOWLClass(iri);
        Queue<OWLClass> clsQueue = new LinkedList<>();
        clsQueue.add(tgtCls);
        while (true) {
            OWLClass cls = clsQueue.poll();
            if (cls == null) {
                break;
            } else {
                System.out.println("Class: " + cls.getIRI().toString());
                System.out.println("rdfs:Label " + labelFor(o, cls));
                reasoner.getSubClasses(cls, true).getFlattened().stream().forEach((subCls) -> {
                    clsQueue.add(subCls);
                });
            }
        }
    }

    private OWLOntology loadDeepPheOntology(OWLOntologyManager manager) throws IOException, OWLOntologyCreationException {
        File f = new File("C:\\Users\\kjm84\\Desktop\\DeepPheOntologies\\BreastCancer.owl");
        String fText = FileUtils.readFileToString(f, "UTF-8");
        OWLOntology o = manager
                .loadOntologyFromOntologyDocument(new StringDocumentSource(
                                fText));
        return o;
    }

    private void displaySignatureClses(OWLOntology o) {
        Set<OWLClass> clses = o.getClassesInSignature();
        clses.stream().forEach((cls) -> {
            System.out.println(cls.getIRI().toString());
        });
    }

    private void loadI2B2(TreeSet<PartialPath> nonExpandablePaths) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection connection;
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@dbmi-i2b2-dev05.dbmi.pitt.edu:1521:xe", "i2b2metadata",
                    "demouser");

            String sql = "SELECT count(*) FROM all_tables where owner = ? and table_name = ?";
            PreparedStatement pStmt;
            pStmt = connection.prepareStatement(sql);
            pStmt.setString(1, "I2B2METADATA");
            pStmt.setString(2, "DEEPPHE_ONTOLOGY");
            int count;
            try (ResultSet rs = pStmt.executeQuery()) {
                count = 0;
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
            pStmt.close();

            if (count == 1) {
                sql = "drop table DEEPPHE_ONTOLOGY";
                pStmt = connection.prepareStatement(sql);
                pStmt.executeUpdate();
                pStmt.close();
            }

            sql = "create table DEEPPHE_ONTOLOGY as select * from COHORT_ONTOLOGY";
            pStmt = connection.prepareStatement(sql);
            pStmt.executeUpdate();
            pStmt.close();

            sql = "delete from DEEPPHE_ONTOLOGY";
            pStmt = connection.prepareStatement(sql);
            pStmt.executeUpdate();
            pStmt.close();

            sql = "insert into DEEPPHE_ONTOLOGY (";
            sql += "C_HLEVEL,";
            sql += "C_FULLNAME,";
            sql += "C_NAME,";
            sql += "C_SYNONYM_CD,";
            sql += "C_VISUALATTRIBUTES,\n";
            sql += "C_TOTALNUM,";
            sql += "C_BASECODE,";
            sql += "C_METADATAXML,";
            sql += "C_FACTTABLECOLUMN,";
            sql += "C_TABLENAME,\n";
            sql += "C_COLUMNNAME,";
            sql += "C_COLUMNDATATYPE,";
            sql += "C_OPERATOR,";
            sql += "C_DIMCODE,";
            sql += "C_COMMENT,\n";
            sql += "C_TOOLTIP,";
            sql += "M_APPLIED_PATH,";
            sql += "UPDATE_DATE,";
            sql += "DOWNLOAD_DATE,";
            sql += "IMPORT_DATE,\n";
            sql += "SOURCESYSTEM_CD,";
            sql += "VALUETYPE_CD,";
            sql += "M_EXCLUSION_CD,";
            sql += "C_PATH,";
            sql += "C_SYMBOL";
            sql += ") values (";
            for (int idx = 0; idx < 24; idx++) {
                sql += "?,";
            }
            sql += "?)";
            System.out.println(sql);
            pStmt = connection.prepareStatement(sql);

//            select C_FACTTABLECOLUMN
//                   from C_TABLENAME
//                    where C_COLUMNNAME C_OPERATOR C_DIMCODE
//
//            select CONCEPT_CD
//                   from CONCEPT_DIMENSION
//                     where CONCEPT_PATH LIKE '\Diagnosis\Circulatory system\%'
            for (PartialPath partialPath : nonExpandablePaths) {
                pStmt.setInt(1, partialPath.getLevel());  // C_HLEVEL
                pStmt.setString(2, partialPath.getPath() + "\\");  // C_FULLNAME
                pStmt.setString(3, extractCname(partialPath));  // C_NAME
                pStmt.setString(4, "N");  // C_SYNONYM_CD
                pStmt.setString(5, "FA ");  // C_VISUALATTRIBUTES
                pStmt.setInt(6, 0);  // C_TOTALNUM
                pStmt.setString(7, null);  // C_BASECODE
                pStmt.setString(8, null);  // C_METADATAXML
                pStmt.setString(9, "concept_cd");  // C_FACTTABLECOLUMN
                pStmt.setString(10, "concept_dimension");  // C_TABLENAME
                pStmt.setString(11, "concept_path");  // C_COLUMNNAME
                pStmt.setString(12, "T");  // C_COLUMNDATATYPE
                pStmt.setString(13, "LIKE");  // C_OPERATOR
                pStmt.setString(14, partialPath.getPath() + "\\");  // C_DIMCODE
                pStmt.setString(15, null);  // C_COMMENT
                pStmt.setString(16, partialPath.getPath().replaceAll("\\\\", " \\\\ ").substring(1) + "\\");  // C_TOOLTIP
                pStmt.setString(17, "@");  // M_APPLIED_PATH
                java.util.Date timeNow = new java.util.Date();
                pStmt.setDate(18, new Date(timeNow.getTime()));  // UPDATE_DATE
                pStmt.setDate(19, new Date(timeNow.getTime()));  // DOWNLOAD_DATE
                pStmt.setDate(20, new Date(timeNow.getTime()));  // IMPORT_DATE
                pStmt.setString(21, "DEEPPHE");  // SOURCESYSTEM_CD
                pStmt.setString(22, null);  // VALUETYPE_CD
                pStmt.setString(23, null);  // M_EXCLUSION_CD
                pStmt.setString(24, null);  // C_PATH
                pStmt.setString(25, null);  // C_SYMBOL
                pStmt.executeUpdate();
            }
            pStmt.close();

            // Delete the old DEEPPHE_ONTOLOGY record from TABLE_ACCESS
            sql = "delete from TABLE_ACCESS where C_TABLE_CD = ?";
            pStmt = connection.prepareStatement(sql);
            pStmt.setString(1, "DEEPPHE"); // C_TABLE_CD
            pStmt.executeUpdate();
            pStmt.close();

            // Fill in the TABLE_ACCESS table
            sql = "insert into TABLE_ACCESS (";
            sql += "C_TABLE_CD,";
            sql += "C_TABLE_NAME,";
            sql += "C_PROTECTED_ACCESS,";
            sql += "C_HLEVEL,";
            sql += "C_FULLNAME,";
            sql += "C_NAME,";
            sql += "C_SYNONYM_CD,";
            sql += "C_VISUALATTRIBUTES,";
            sql += "C_TOTALNUM,";
            sql += "C_BASECODE,";
            sql += "C_METADATAXML,";
            sql += "C_FACTTABLECOLUMN,";
            sql += "C_DIMTABLENAME,";
            sql += "C_COLUMNNAME,";
            sql += "C_COLUMNDATATYPE,";
            sql += "C_OPERATOR,";
            sql += "C_DIMCODE,";
            sql += "C_COMMENT,";
            sql += "C_TOOLTIP,";
            sql += "C_ENTRY_DATE,";
            sql += "C_CHANGE_DATE,";
            sql += "C_STATUS_CD,";
            sql += "VALUETYPE_CD";
            sql += ") values (";
            for (int idx = 0; idx < 22; idx++) {
                sql += "?,";
            }
            sql += "?)";
            pStmt = connection.prepareStatement(sql);
            pStmt.setString(1, "DEEPPHE"); // C_TABLE_CD
            pStmt.setString(2, "DEEPPHE_ONTOLOGY"); // C_TABLE_NAME
            pStmt.setString(3, "N"); // C_PROTECTED_ACCESS
            pStmt.setInt(4, 1); // C_HLEVEL
            pStmt.setString(5, "\\DEEPPHE\\"); // C_FULLNAME
            pStmt.setString(6, "DeepPhe Ontology"); // C_NAME
            pStmt.setString(7, "N"); // C_SYNONYM_CD
            pStmt.setString(8, "FA "); // C_VISUALATTRIBUTES
            pStmt.setString(9, null); // C_TOTALNUM
            pStmt.setString(10, null); // C_BASECODE
            pStmt.setString(11, null); // C_METADATAXML
            pStmt.setString(12, "concept_cd"); // C_FACTTABLECOLUMN
            pStmt.setString(13, "concept_dimension"); // C_DIMTABLENAME
            pStmt.setString(14, "concept_path"); // C_COLUMNNAME
            pStmt.setString(15, "T"); // C_COLUMNDATATYPE
            pStmt.setString(16, "LIKE"); // C_OPERATOR
            pStmt.setString(17, "\\DEEPPHE\\"); // C_DIMCODE
            pStmt.setString(18, null); // C_COMMENT
            pStmt.setString(19, "DeepPhe Ontology"); // C_TOOLTIP
            pStmt.setString(20, null); // C_ENTRY_DATE
            pStmt.setString(21, null); // C_CHANGE_DATE
            pStmt.setString(22, null); // C_STATUS_CD
            pStmt.setString(23, null); // VALUETYPE_CD
            pStmt.executeUpdate();
            pStmt.close();

            connection.close();

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(I2b2OntologyBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String extractCname(PartialPath partialPath) {
        int lastSlashPos;
        lastSlashPos = partialPath.getPath().lastIndexOf("\\");
        return partialPath.getPath().substring(lastSlashPos + 1);
    }

    private void displayPaths(TreeSet<PartialPath> paths) {
        paths.stream().forEach((path) -> {
            System.out.println(path);
        });
    }

}
