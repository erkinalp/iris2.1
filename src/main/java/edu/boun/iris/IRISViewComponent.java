package edu.boun.iris;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

import com.jidesoft.swing.*;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.event.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.boun.iris.ontology.GenericOntologyRetriever;
import edu.boun.iris.ontology.OntologyRetriever;
import edu.boun.iris.ontology.OntologyRetrievalException;
import edu.boun.iris.ontology.OntologySource;
import edu.boun.iris.ontology.OntologySourceLoader;

public class IRISViewComponent extends AbstractOWLViewComponent implements ActionListener, MouseListener {

    private static final Logger logger = LoggerFactory.getLogger(IRISViewComponent.class);
    private static final long serialVersionUID = 1L;
    protected JTextField textField;
    protected JTextArea textArea;

    ArrayList<Product> productList;
    int lastProductCount = 0;
    int rowsFilled;
    DefaultMutableTreeNode root;

    CheckBoxTree checkBoxTree_individuals;
    CheckBoxTree checkBoxTree_properties;
    ArrayList<DefaultMutableTreeNode> selectedNodes;
    private JSplitPane splitPane9;
    private JSplitPane splitPane10;
    private JSplitPane splitPane12;
    private JToolBar toolBar1;
    private JButton button16;
    private JButton button17;
    private JButton button18;
    private JScrollPane scrollPane2;
    private JSplitPane splitPane11;

    private JToolBar toolBar2;
    private JButton openButton;
    private JButton button19;
    private JButton button20;
    private JButton button21;
    private JButton button22;
    private JButton sourceCodeButton;
    private JButton saveButton;
    private JButton setPropertyPreferences;
    private JButton goodRelations;
    private JScrollPane scrollPane3;
    private JScrollPane scrollPane4;

    private boolean goodRelationsVocabulary_opened;
    private String RANGE_DELIMETER;

    ArrayList<String> slots, Grslots;
    ArrayList<String> individuals;
    ArrayList<String> datatypeProperties;
    ArrayList<String> objectProperties;
    ArrayList<Result> DialogBoxResults;
    ArrayList<MatchingResult> GoodRelationsMatchingResults;
    DefaultMutableTreeNode root_individuals, root_properties;
    UnitsofMeasurementManager um_manager;
    File savedFile;

    private OWLOntology ontology;
    private OWLOntologyManager ontologyManager;
    private OWLDataFactory dataFactory;
    private String defaultNamespace;
    
    private OntologyRetriever ontologyRetriever;
    private java.util.List<OntologySource> ontologySources;

    @Override
    protected void initialiseOWLView() throws Exception {
        org.apache.log4j.Logger.getLogger("com.gargoylesoftware.htmlunit")
                .setLevel(org.apache.log4j.Level.FATAL);
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit")
                .setLevel(java.util.logging.Level.SEVERE);

        BorderLayout border = new BorderLayout();
        setLayout(border);

        ontology = getOWLModelManager().getActiveOntology();
        ontologyManager = getOWLModelManager().getOWLOntologyManager();
        dataFactory = ontologyManager.getOWLDataFactory();
        
        com.google.common.base.Optional<IRI> ontologyIRI = ontology.getOntologyID().getOntologyIRI();
        if (ontologyIRI.isPresent()) {
            defaultNamespace = ontologyIRI.get().toString() + "#";
        } else {
            defaultNamespace = "http://www.example.org/ontology#";
        }

        productList = new ArrayList<Product>();
        slots = new ArrayList<String>();
        individuals = new ArrayList<String>();
        datatypeProperties = new ArrayList<String>();
        objectProperties = new ArrayList<String>();
        Grslots = new ArrayList<String>();
        GoodRelationsMatchingResults = new ArrayList<MatchingResult>();
        goodRelationsVocabulary_opened = false;
        RANGE_DELIMETER = "-";

        splitPane9 = new JSplitPane();
        splitPane10 = new JSplitPane();
        splitPane12 = new JSplitPane();
        toolBar1 = new JToolBar();
        button16 = new JButton();
        button17 = new JButton();
        button18 = new JButton();
        scrollPane2 = new JScrollPane();
        splitPane11 = new JSplitPane();
        splitPane12 = new JSplitPane();
        toolBar2 = new JToolBar();
        openButton = new JButton();
        button19 = new JButton();
        button20 = new JButton();
        button21 = new JButton();
        button22 = new JButton();
        saveButton = new JButton();
        sourceCodeButton = new JButton();
        scrollPane3 = new JScrollPane();
        scrollPane4 = new JScrollPane();
        setPropertyPreferences = new JButton();
        goodRelations = new JButton();

        {
            splitPane9.setDividerLocation(300);

            {
                splitPane10.setOrientation(JSplitPane.VERTICAL_SPLIT);

                {

                    URL fileUri = IRISViewComponent.class.getResource("/resources/SubClass.gif");
                    if (fileUri != null) {
                        button16.setIcon(new ImageIcon(fileUri));
                    }
                    button16.setToolTipText("Create subclass");
                    toolBar1.add(button16);

                    fileUri = IRISViewComponent.class.getResource("/resources/SiblingClass.gif");
                    if (fileUri != null) {
                        button17.setIcon(new ImageIcon(fileUri));
                    }
                    button17.setToolTipText("Create sibling class");
                    toolBar1.add(button17);

                    fileUri = IRISViewComponent.class.getResource("/resources/Project8.gif");
                    if (fileUri != null) {
                        button18.setIcon(new ImageIcon(fileUri));
                    }
                    button18.setToolTipText("Remove class");
                    toolBar1.add(button18);
                }
                splitPane10.setTopComponent(toolBar1);
                splitPane10.setBottomComponent(scrollPane2);
            }

            splitPane9.setLeftComponent(splitPane10);

            {
                splitPane11.setOrientation(JSplitPane.VERTICAL_SPLIT);

                {

                    URL fileUri = IRISViewComponent.class.getResource("/resources/Open16.gif");
                    if (fileUri != null) {
                        openButton.setIcon(new ImageIcon(fileUri));
                    }
                    openButton.addActionListener(this);
                    toolBar2.add(openButton);

                    toolBar2.addSeparator();

                    fileUri = IRISViewComponent.class.getResource("/resources/Properties16.gif");
                    if (fileUri != null) {
                        button21.setIcon(new ImageIcon(fileUri));
                    }
                    button21.setToolTipText("Select all");
                    toolBar2.add(button21);

                    fileUri = IRISViewComponent.class.getResource("/resources/New16.gif");
                    if (fileUri != null) {
                        button22.setIcon(new ImageIcon(fileUri));
                    }
                    button22.setToolTipText("Deselect all");
                    toolBar2.add(button22);
                    toolBar2.addSeparator();

                    fileUri = IRISViewComponent.class.getResource("/resources/PropertyMatrix.gif");
                    if (fileUri != null) {
                        setPropertyPreferences.setIcon(new ImageIcon(fileUri));
                    }
                    setPropertyPreferences.addActionListener(this);
                    setPropertyPreferences.setToolTipText("Set type/units for property");
                    toolBar2.add(setPropertyPreferences);

                    fileUri = IRISViewComponent.class.getResource("/resources/GR_16_16_32.gif");
                    if (fileUri != null) {
                        goodRelations.setIcon(new ImageIcon(fileUri));
                    }
                    goodRelations.addActionListener(this);
                    goodRelations.setToolTipText("Use Good Relations Vocabulary");
                    toolBar2.add(goodRelations);
                    toolBar2.addSeparator();

                    fileUri = IRISViewComponent.class.getResource("/resources/SourceCode.gif");
                    if (fileUri != null) {
                        sourceCodeButton.setIcon(new ImageIcon(fileUri));
                    }
                    sourceCodeButton.addActionListener(this);
                    sourceCodeButton.setToolTipText("Export to a serialization format");
                    toolBar2.add(sourceCodeButton);

                    fileUri = IRISViewComponent.class.getResource("/resources/Save16.gif");
                    if (fileUri != null) {
                        saveButton.setIcon(new ImageIcon(fileUri));
                    }
                    saveButton.addActionListener(this);
                    saveButton.setToolTipText("Save");
                    toolBar2.add(saveButton);
                }
                splitPane11.setTopComponent(toolBar2);
                splitPane11.setBottomComponent(splitPane12);
                splitPane12.setDividerLocation(450);
                splitPane12.setLeftComponent(scrollPane3);
                splitPane12.setRightComponent(scrollPane4);

            }
            splitPane9.setRightComponent(splitPane11);
        }
        add(splitPane9, BorderLayout.CENTER);
        um_manager = new UnitsofMeasurementManager();
        
        ontologyRetriever = new GenericOntologyRetriever();
        ontologySources = OntologySourceLoader.loadOntologySources();
        logger.info("Loaded {} ontology sources", ontologySources.size());
    }

    @Override
    protected void disposeOWLView() {
    }

    public void drawTree(ArrayList<Product> productList) {
        drawProductsTree(productList);
        drawPropertiesTree(productList);
    }

    public void drawPropertiesTree(ArrayList<Product> productList) {
        root_properties = new DefaultMutableTreeNode("PRODUCT PROPERTIES");
        ArrayList<String> allProperties = new ArrayList<String>();
        for (int j = 0; j < productList.size(); j++) {
            Product p = productList.get(j);
            for (int y = 0; y < p.propertyName.size() && y < p.propertyValue.size(); y++) {
                if (!allProperties.contains(p.propertyName.get(y))) {
                    allProperties.add(p.propertyName.get(y));
                    root_properties.add(new DefaultMutableTreeNode(p.propertyName.get(y)));
                }
            }
        }

        checkBoxTree_properties = new CheckBoxTree(root_properties) {
            @Override
            public boolean isCheckBoxVisible(TreePath path) {
                if (((DefaultMutableTreeNode) path.getLastPathComponent()).isLeaf())
                    return true;
                else
                    return false;

            }
        };

        URL fileUri = IRISViewComponent.class.getResource("/resources/RDFPropertyInherited.gif");
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) checkBoxTree_properties.getActualCellRenderer();
        if (fileUri != null) {
            renderer.setClosedIcon(new ImageIcon(fileUri));
            renderer.setOpenIcon(new ImageIcon(fileUri));
            renderer.setLeafIcon(new ImageIcon(fileUri));
        }
        checkBoxTree_properties.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent arg0) {

            }
        });

        checkBoxTree_properties.getCheckBoxTreeSelectionModel().setDigIn(false);
        scrollPane4.setViewportView(checkBoxTree_properties);
        scrollPane4.revalidate();
        scrollPane4.repaint();
    }

    public void drawProductProperties(DefaultMutableTreeNode cp, String propertyName, String propertyValue) {

        if ((propertyName != null && !propertyName.isEmpty()) && (propertyValue != null && !propertyValue.isEmpty())) {
            cp.add(new DefaultMutableTreeNode(propertyName + "= " + propertyValue));
        }
    }

    public void drawProductsTree(ArrayList<Product> productList) {
        root_individuals = new DefaultMutableTreeNode("PRODUCTS");
        Product p;
        DefaultMutableTreeNode cp;
        for (int i = 0; i < productList.size(); i++) {
            p = productList.get(i);
            cp = new DefaultMutableTreeNode("Product_" + i);

            root_individuals.add(cp);
            drawProductProperties(cp, "Title", p.title);
            drawProductProperties(cp, "Brand", p.brand);
            drawProductProperties(cp, "ProductID", p.productID);
            drawProductProperties(cp, "Description", p.description);
            drawProductProperties(cp, "Image Link", p.imgLink);
            for (int y = 0; y < p.features.size(); y++)
                drawProductProperties(cp, "Feature", p.features.get(y));
            for (int y = 0; y < p.propertyName.size() && y < p.propertyValue.size(); y++)
                drawProductProperties(cp, p.propertyName.get(y), p.propertyValue.get(y));
            for (int y = 0; y < p.includes.size(); y++)
                drawProductProperties(cp, "Includes", p.includes.get(y));

        }

        checkBoxTree_individuals = new CheckBoxTree(root_individuals) {
            @Override
            public boolean isCheckBoxVisible(TreePath path) {
                if (!((DefaultMutableTreeNode) path.getLastPathComponent()).isLeaf()
                        && !((DefaultMutableTreeNode) path.getLastPathComponent()).isRoot())
                    return true;
                else
                    return false;

            }
        };

        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) checkBoxTree_individuals.getActualCellRenderer();

        checkBoxTree_individuals.getCheckBoxTreeSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent arg0) {
            }

        });

        checkBoxTree_individuals.getCheckBoxTreeSelectionModel().setDigIn(false);

        scrollPane3.setViewportView(checkBoxTree_individuals);
        scrollPane3.revalidate();
        scrollPane3.repaint();
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        JFileChooser fc1 = new JFileChooser();

        if (e.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                fillProductTree(file);
            }
            openButton.setEnabled(false);
        }

        if (e.getSource() == saveButton) {
            int returnVal = fc1.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc1.getSelectedFile();
                savedFile = file;
                if (file.getPath().toString().endsWith("myOwl.owl")) {
                    JOptionPane.showMessageDialog(null,
                            "Save your ontology model to a file other than \"myOwl.owl\"");
                } else {
                    try {
                        ontologyManager.saveOntology(ontology, IRI.create(file.toURI()));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

        if (e.getSource() == goodRelations) {
            if (goodRelationsVocabulary_opened == false) {
                try {
                    openGoodRelationsVocabulary();
                } catch (Exception e1) {
                }
            }
            System.out.println("GRD1");
            getSelectionData();
            System.out.println("GRD2");
            getGRDMatchings();
            System.out.println("GRD3");
            saveOntology(null);
            setPropertyPreferences.setEnabled(false);
            goodRelations.setEnabled(false);
        }

        if (e.getSource() == setPropertyPreferences) {
            getSelectionData();
            ArrayList<Result> DialogBoxResults = getPropertyPreferences();
            saveOntology(DialogBoxResults);
            goodRelations.setEnabled(false);
            setPropertyPreferences.setEnabled(false);
        }

        if (e.getSource() == sourceCodeButton) {
            SerializationTab st = new SerializationTab(ontology, savedFile);
            st.setVisible(true);
        }

    }

    public void getGRDMatchings() {
        Set<OWLDataProperty> dataProperties = ontology.getDataPropertiesInSignature();
        Set<OWLObjectProperty> objProperties = ontology.getObjectPropertiesInSignature();
        System.out.println("ABC1");

        for (OWLDataProperty prop : dataProperties) {
            String namespace = prop.getIRI().getNamespace();
            if (namespace.contains("http://purl.org/goodrelations/v1#") || namespace.contains("http://schema.org")
                    || namespace.contains("http://xmlns.com/foaf/0.1/")) {
                Grslots.add(prop.getIRI().getShortForm());
            }
        }

        for (OWLObjectProperty prop : objProperties) {
            String namespace = prop.getIRI().getNamespace();
            if (namespace.contains("http://purl.org/goodrelations/v1#") || namespace.contains("http://schema.org")
                    || namespace.contains("http://xmlns.com/foaf/0.1/")) {
                Grslots.add(prop.getIRI().getShortForm());
            }
        }

        System.out.println("ABC3");
        URL fileUri1 = GoodRelationsDialog.class.getResource("/resources/GR_16_16_32.gif");
        URL fileUri2 = GoodRelationsDialog.class.getResource("/resources/cancel.gif");
        URL fileUri3 = GoodRelationsDialog.class.getResource("/resources/yes.gif");

        GoodRelationsDialog grd = new GoodRelationsDialog(slots, Grslots, um_manager.getUnitsofMeasurement(),
                ModalityType.APPLICATION_MODAL, new ImageIcon(fileUri1), new ImageIcon(fileUri2),
                new ImageIcon(fileUri3));
        grd.setVisible(true);
        GoodRelationsMatchingResults = grd.getMatchingResults();
    }

    public void openGoodRelationsVocabulary() throws Exception {
        InputStream is = null;
        
        OntologySource goodRelationsSource = null;
        for (OntologySource source : ontologySources) {
            if ("GoodRelations".equals(source.getName())) {
                goodRelationsSource = source;
                break;
            }
        }
        
        if (goodRelationsSource != null && goodRelationsSource.isEnabled()) {
            try {
                logger.info("Attempting to retrieve GoodRelations vocabulary from network");
                is = ontologyRetriever.retrieveOntology(goodRelationsSource);
                if (is != null) {
                    ontologyManager.loadOntologyFromOntologyDocument(is);
                    goodRelationsVocabulary_opened = true;
                    logger.info("Successfully loaded GoodRelations vocabulary from network");
                    if (is != null) {
                        is.close();
                    }
                    return;
                }
            } catch (OntologyRetrievalException e) {
                logger.warn("Failed to retrieve GoodRelations from network, falling back to bundled version", e);
            } catch (Exception e) {
                logger.warn("Error loading GoodRelations from network, falling back to bundled version", e);
            }
        }
        
        try {
            logger.info("Loading bundled GoodRelations vocabulary");
            is = IRISViewComponent.class.getResourceAsStream("/resources/GoodRelations_v1.owl");
            if (is != null) {
                ontologyManager.loadOntologyFromOntologyDocument(is);
                goodRelationsVocabulary_opened = true;
                logger.info("Successfully loaded bundled GoodRelations vocabulary");
            }
            if (is != null) {
                is.close();
            }
        } catch (FileNotFoundException e) {
            logger.error("Bundled GoodRelations vocabulary not found", e);
        } catch (IOException e) {
            logger.error("Error loading bundled GoodRelations vocabulary", e);
        }
    }

    public ArrayList<Result> getPropertyPreferences() {

        SetPropertyPreferences sp = new SetPropertyPreferences(slots, um_manager.getUnitsofMeasurement(),
                ModalityType.APPLICATION_MODAL);
        sp.setVisible(true);
        ArrayList<Result> DialogBoxResults = sp.getObjectProperties();

        for (int i = 0; i < DialogBoxResults.size(); i++) {
            Result r = DialogBoxResults.get(i);
            if (r.isObjectType)
                objectProperties.add(r.pname);
        }

        for (int i = 0; i < objectProperties.size(); i++) {
            String co = objectProperties.get(i);
            if (datatypeProperties.contains(co))
                datatypeProperties.remove(co);
        }
        return DialogBoxResults;
    }

    public void getSelectionData() {
        TreePath[] selectedPaths_individuals = checkBoxTree_individuals.getCheckBoxTreeSelectionModel()
                .getSelectionPaths();
        TreePath[] selectedPaths_properties = checkBoxTree_properties.getCheckBoxTreeSelectionModel()
                .getSelectionPaths();

        for (int i = 0; i < selectedPaths_individuals.length; i++) {
            Object[] path = selectedPaths_individuals[i].getPath();
            String indNode = "";
            if (path.length == 2)
                indNode = path[path.length - 1].toString();

            if (!indNode.isEmpty() && !individuals.contains(indNode))
                individuals.add(indNode);
        }

        for (int i = 0; i < selectedPaths_properties.length; i++) {
            Object[] path = selectedPaths_properties[i].getPath();
            String lastNode = "";
            String propertyName = "";

            if (path.length == 2)
                lastNode = path[path.length - 1].toString();

            if (!lastNode.isEmpty()) {
                propertyName = normalize(lastNode);
                if (!propertyName.isEmpty() && !slots.contains(propertyName))
                    slots.add(propertyName);
            }

        }

        for (int i = 0; i < slots.size(); i++)
            datatypeProperties.add(slots.get(i));
    }

    public void saveOntology(ArrayList<Result> DialogBoxResults) {
        IRI laptopIRI = IRI.create(defaultNamespace + "Laptop");
        OWLClass laptopClass = dataFactory.getOWLClass(laptopIRI);
        OWLDeclarationAxiom declareClass = dataFactory.getOWLDeclarationAxiom(laptopClass);
        ontologyManager.addAxiom(ontology, declareClass);

        for (int i = 0; i < individuals.size(); i++)
            createOWLIndividual(laptopClass, individuals.get(i));

        if (goodRelationsVocabulary_opened)
            saveGoodRelationsVocabulary(laptopClass);
        else
            saveOntologyModel(laptopClass, DialogBoxResults);
    }

    public void saveGoodRelationsVocabulary(OWLClass c) {

        String quantitativeProductOrServiceProperty = "http://purl.org/goodrelations/v1#quantitativeProductOrServiceProperty";
        String qualitativeProductOrServiceProperty = "http://purl.org/goodrelations/v1#qualitativeProductOrServiceProperty";
        String datatypeProductOrServiceProperty = "http://purl.org/goodrelations/v1#datatypeProductOrServiceProperty";

        TreePath[] selectedPaths_individuals = checkBoxTree_individuals.getCheckBoxTreeSelectionModel()
                .getSelectionPaths();
        TreePath[] selectedPaths_properties = checkBoxTree_properties.getCheckBoxTreeSelectionModel()
                .getSelectionPaths();
        Object[] path;
        DefaultMutableTreeNode indTreeNode;
        String indNode, lastNode, propertyName, propertyValue;
        String[] propValue;
        OWLNamedIndividual ind;
        MatchingResult mr;

        for (int i = 0; i < selectedPaths_individuals.length; i++) {
            path = selectedPaths_individuals[i].getPath();
            if (path.length == 2) {
                indTreeNode = (DefaultMutableTreeNode) path[path.length - 1];
                indNode = path[path.length - 1].toString();

                for (int m = 0; m < indTreeNode.getChildCount(); m++) {
                    lastNode = indTreeNode.getChildAt(m).toString();
                    propValue = SplitUsingTokenizer(lastNode, "=");
                    propertyName = "";
                    if (propValue.length > 1) {
                        propertyName = normalize(propValue[0]);
                        propertyValue = normalize(propValue[1]);

                        if (!indNode.isEmpty() && slots.contains(propertyName)) {

                            IRI individualIRI = IRI.create(defaultNamespace + indNode);
                            ind = dataFactory.getOWLNamedIndividual(individualIRI);

                            for (int j = 0; j < GoodRelationsMatchingResults.size(); j++) {
                                mr = GoodRelationsMatchingResults.get(j);
                                if (mr.slotName.contains(propertyName) && mr.slotName.length() == propertyName.length()) {

                                    if (mr.GR_slotName.endsWith("quantitativeProductOrServiceProperty"))
                                        createQuantitativeProperty(mr, propertyValue, c, ind);
                                    else if (mr.GR_slotName.endsWith("qualitativeProductOrServiceProperty"))
                                        createQualitativeProperty(mr, propertyValue, c, ind);
                                    else if (mr.GR_slotName.endsWith("datatypeProductOrServiceProperty")) {
                                        OWLDataProperty datatypeProperty = createOWLDatatypeProperty(mr.slotName);
                                        OWLLiteral literal = dataFactory.getOWLLiteral(propertyValue, "en");
                                        OWLDataPropertyAssertionAxiom assertion = dataFactory
                                                .getOWLDataPropertyAssertionAxiom(datatypeProperty, ind, literal);
                                        ontologyManager.addAxiom(ontology, assertion);
                                    } else {
                                        String GRPropertyUri = "";

                                        if (!mr.GR_slotName.contains("/")) {
                                            GRPropertyUri = "http://purl.org/goodrelations/v1#" + mr.GR_slotName;
                                        } else
                                            GRPropertyUri = mr.GR_slotName;

                                        IRI grPropertyIRI = IRI.create(GRPropertyUri);
                                        OWLDataProperty grDataProp = dataFactory.getOWLDataProperty(grPropertyIRI);
                                        OWLLiteral literal = dataFactory.getOWLLiteral(propertyValue);
                                        OWLDataPropertyAssertionAxiom assertion = dataFactory
                                                .getOWLDataPropertyAssertionAxiom(grDataProp, ind, literal);
                                        ontologyManager.addAxiom(ontology, assertion);
                                    }

                                }
                            }

                        }
                    }
                }
            }
        }

    }

    public void createQuantitativeProperty(MatchingResult mr, String propertyValue, OWLClass c,
            OWLNamedIndividual ind) {

        IRI quantitativeValueIRI = IRI.create("http://purl.org/goodrelations/v1#QuantitativeValueFloat");
        OWLClass QuantitativeValue = dataFactory.getOWLClass(quantitativeValueIRI);

        OWLObjectProperty owlProperty = createOWLObjectProperty(mr.slotName);

        String s = "QuantitativeValue_" + System.currentTimeMillis();
        OWLNamedIndividual qind = createOWLIndividual(QuantitativeValue, s);

        OWLObjectPropertyAssertionAxiom assertion = dataFactory.getOWLObjectPropertyAssertionAxiom(owlProperty, ind,
                qind);
        ontologyManager.addAxiom(ontology, assertion);

        if (propertyValue.contains(RANGE_DELIMETER)) {
            String[] values = SplitUsingTokenizer(propertyValue, RANGE_DELIMETER);

            OWLDataProperty hasMinValueFloat = dataFactory
                    .getOWLDataProperty(IRI.create("http://purl.org/goodrelations/v1#hasMinValueFloat"));
            OWLDataProperty hasMaxValueFloat = dataFactory
                    .getOWLDataProperty(IRI.create("http://purl.org/goodrelations/v1#hasMaxValueFloat"));
            OWLDataProperty hasUnitOfMeasurement = dataFactory
                    .getOWLDataProperty(IRI.create("http://purl.org/goodrelations/v1#hasUnitOfMeasurement"));

            OWLLiteral minValue = dataFactory.getOWLLiteral(values[0], OWL2Datatype.XSD_FLOAT);
            OWLLiteral maxValue = dataFactory.getOWLLiteral(values[1], OWL2Datatype.XSD_FLOAT);

            ontologyManager.addAxiom(ontology,
                    dataFactory.getOWLDataPropertyAssertionAxiom(hasMinValueFloat, qind, minValue));
            ontologyManager.addAxiom(ontology,
                    dataFactory.getOWLDataPropertyAssertionAxiom(hasMaxValueFloat, qind, maxValue));

            if (!mr.unit_commonCode.isEmpty()) {
                OWLLiteral unitOfMeasurement = dataFactory.getOWLLiteral(mr.unit_commonCode,
                        OWL2Datatype.XSD_STRING);
                ontologyManager.addAxiom(ontology,
                        dataFactory.getOWLDataPropertyAssertionAxiom(hasUnitOfMeasurement, qind, unitOfMeasurement));
            }

        } else {
            String processedValue = propertyValue;
            Pattern pattern = Pattern.compile("[0123456789]*[.]?[0123456789]{0,4}");
            Matcher matcher = pattern.matcher(propertyValue);
            if (matcher.find()) {

                processedValue = matcher.group(0);
                OWLDataProperty hasValueFloat = dataFactory
                        .getOWLDataProperty(IRI.create("http://purl.org/goodrelations/v1#hasValueFloat"));
                OWLLiteral value = dataFactory.getOWLLiteral(processedValue, OWL2Datatype.XSD_FLOAT);

                ontologyManager.addAxiom(ontology,
                        dataFactory.getOWLDataPropertyAssertionAxiom(hasValueFloat, qind, value));

                if (mr.unit_commonCode != null)
                    if (!mr.unit_commonCode.isEmpty()) {
                        OWLDataProperty hasUnitOfMeasurement = dataFactory
                                .getOWLDataProperty(IRI.create("http://purl.org/goodrelations/v1#hasUnitOfMeasurement"));
                        OWLLiteral unitOfMeasurement = dataFactory.getOWLLiteral(mr.unit_commonCode,
                                OWL2Datatype.XSD_STRING);
                        ontologyManager.addAxiom(ontology, dataFactory.getOWLDataPropertyAssertionAxiom(
                                hasUnitOfMeasurement, qind, unitOfMeasurement));
                    }

            }

        }
    }

    public void createQualitativeProperty(MatchingResult mr, String propertyValue, OWLClass c,
            OWLNamedIndividual ind) {
        IRI qualitativeValueIRI = IRI.create("http://purl.org/goodrelations/v1#QualitativeValue");
        OWLClass QualitativeValue = dataFactory.getOWLClass(qualitativeValueIRI);

        OWLObjectProperty owlProperty = createOWLObjectProperty(mr.slotName);
        OWLClass rangeClass = createOWLNamedClass("ValuesFor_" + mr.slotName);

        OWLSubClassOfAxiom subClassAxiom = dataFactory.getOWLSubClassOfAxiom(rangeClass, QualitativeValue);
        ontologyManager.addAxiom(ontology, subClassAxiom);

        OWLNamedIndividual rind = createOWLIndividual(rangeClass, propertyValue);

        OWLObjectPropertyAssertionAxiom assertion = dataFactory.getOWLObjectPropertyAssertionAxiom(owlProperty, ind,
                rind);
        ontologyManager.addAxiom(ontology, assertion);
    }

    public void saveOntologyModel(OWLClass c, ArrayList<Result> DialogBoxResults) {

        for (int i = 0; i < datatypeProperties.size(); i++)
            createOWLDatatypeProperty(datatypeProperties.get(i));
        String newClass;
        for (int i = 0; i < objectProperties.size(); i++) {
            createOWLObjectProperty(objectProperties.get(i));
            newClass = capitalize(objectProperties.get(i));
            createOWLNamedClass(newClass);
        }

        TreePath[] selectedPaths_individuals = checkBoxTree_individuals.getCheckBoxTreeSelectionModel()
                .getSelectionPaths();
        Object[] path;
        DefaultMutableTreeNode indTreeNode;
        String indNode, lastNode, propertyName, propertyValue;
        String[] propValue;
        OWLNamedIndividual ind;
        Result r;
        Pattern pattern;
        Matcher matcher;

        for (int i = 0; i < selectedPaths_individuals.length; i++) {
            path = selectedPaths_individuals[i].getPath();
            if (path.length == 2) {
                indTreeNode = (DefaultMutableTreeNode) path[path.length - 1];
                indNode = path[path.length - 1].toString();

                for (int m = 0; m < indTreeNode.getChildCount(); m++) {
                    lastNode = indTreeNode.getChildAt(m).toString();
                    propValue = SplitUsingTokenizer(lastNode, "=");
                    propertyName = "";
                    if (propValue.length > 1) {
                        propertyName = normalize(propValue[0]);
                        propertyValue = normalize(propValue[1]);

                        if (!indNode.isEmpty() && slots.contains(propertyName)) {
                            IRI individualIRI = IRI.create(defaultNamespace + indNode);
                            ind = dataFactory.getOWLNamedIndividual(individualIRI);

                            if (propertyName != null && ind != null) {
                                for (int d = 0; d < DialogBoxResults.size(); d++) {
                                    r = DialogBoxResults.get(d);

                                    if (r.pname.contains(propertyName) && r.pname.length() == propertyName.length()
                                            && r.removeUnit) {
                                        pattern = Pattern.compile("[0123456789]*[.]?[0123456789]{0,4}");
                                        matcher = pattern.matcher(propertyValue);
                                        if (matcher.find())
                                            propertyValue = matcher.group(0);
                                    }
                                }

                                if (datatypeProperties.contains(propertyName)) {
                                    OWLDataProperty prop = createOWLDatatypeProperty(propertyName);
                                    OWLLiteral literal = dataFactory.getOWLLiteral(propertyValue, "en");
                                    OWLDataPropertyAssertionAxiom assertion = dataFactory
                                            .getOWLDataPropertyAssertionAxiom(prop, ind, literal);
                                    ontologyManager.addAxiom(ontology, assertion);
                                }

                                if (objectProperties.contains(propertyName)) {
                                    OWLObjectProperty propo = createOWLObjectProperty(propertyName);

                                    if (!individuals.contains(propertyValue)) {
                                        individuals.add(propertyValue);
                                        OWLClass rc = createOWLNamedClass(capitalize(propertyName));
                                        OWLNamedIndividual valueInd = createOWLIndividual(rc, propertyValue);
                                        OWLObjectPropertyAssertionAxiom assertion = dataFactory
                                                .getOWLObjectPropertyAssertionAxiom(propo, ind, valueInd);
                                        ontologyManager.addAxiom(ontology, assertion);
                                    }
                                }

                            }

                        }

                    }

                }

            }
        }
    }

    public OWLNamedIndividual createOWLIndividual(OWLClass c, String i) {
        IRI individualIRI = IRI.create(defaultNamespace + i);
        OWLNamedIndividual individual = dataFactory.getOWLNamedIndividual(individualIRI);
        OWLClassAssertionAxiom classAssertion = dataFactory.getOWLClassAssertionAxiom(c, individual);
        ontologyManager.addAxiom(ontology, classAssertion);
        return individual;
    }

    public OWLClass createOWLNamedClass(String c) {
        IRI classIRI = IRI.create(defaultNamespace + c);
        OWLClass owlClass = dataFactory.getOWLClass(classIRI);
        OWLDeclarationAxiom declareClass = dataFactory.getOWLDeclarationAxiom(owlClass);
        ontologyManager.addAxiom(ontology, declareClass);
        return owlClass;
    }

    public OWLObjectProperty createOWLObjectProperty(String p) {
        IRI propertyIRI = IRI.create(defaultNamespace + p);
        OWLObjectProperty property = dataFactory.getOWLObjectProperty(propertyIRI);
        OWLDeclarationAxiom declareProperty = dataFactory.getOWLDeclarationAxiom(property);
        ontologyManager.addAxiom(ontology, declareProperty);
        return property;
    }

    public OWLDataProperty createOWLDatatypeProperty(String p) {
        IRI propertyIRI = IRI.create(defaultNamespace + p);
        OWLDataProperty property = dataFactory.getOWLDataProperty(propertyIRI);
        OWLDeclarationAxiom declareProperty = dataFactory.getOWLDeclarationAxiom(property);
        ontologyManager.addAxiom(ontology, declareProperty);
        return property;
    }

    public String capitalize(String s) {
        String newFirst = s.substring(0, 1).toUpperCase();
        String newName = newFirst + s.substring(1);
        return newName;
    }

    public String normalize(String preValue) {
        String processedValue = preValue;
        processedValue = processedValue.trim();
        String firstChar = processedValue.substring(0, 1);
        if (firstChar.contains("I") || firstChar.contains("�"))
            firstChar = "i";
        else
            firstChar = firstChar.toLowerCase();
        processedValue = firstChar + processedValue.substring(1);
        processedValue = processedValue.replaceAll("\\s", "");
        return processedValue;

    }

    public void fillProductTree(File file) {
        Html2ProductMatcher matcher = new Html2ProductMatcher(file);
        DefaultMutableTreeNode root = matcher.getRoot();
        productList = HtmlUnitDefault.parse(root, matcher.next_page, matcher.page_range, matcher.base_uri,
                matcher.page_uri, matcher.classToSearch);
        drawTree(productList);
    }

    public static String[] SplitUsingTokenizer(String Subject, String Delimiters) {
        StringTokenizer StrTkn = new StringTokenizer(Subject, Delimiters);
        ArrayList<String> ArrLis = new ArrayList<String>(Subject.length());
        while (StrTkn.hasMoreTokens()) {
            ArrLis.add(StrTkn.nextToken());
        }
        return ArrLis.toArray(new String[0]);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent arg0) {

    }

    @Override
    public void mouseExited(MouseEvent arg0) {

    }

    @Override
    public void mousePressed(MouseEvent arg0) {

    }

    @Override
    public void mouseReleased(MouseEvent arg0) {

    }

}
