package edu.boun.iris.ui;

import edu.boun.iris.ontology.OntologyRetriever;
import edu.boun.iris.ontology.OntologySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OntologyPreferencesPanel extends JDialog {
    
    private static final Logger logger = LoggerFactory.getLogger(OntologyPreferencesPanel.class);
    private static final long serialVersionUID = 1L;
    
    private List<OntologySource> ontologySources;
    private OntologyRetriever ontologyRetriever;
    private JTable sourcesTable;
    private OntologySourceTableModel tableModel;
    private JButton updateButton;
    private JButton closeButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    
    public OntologyPreferencesPanel(Frame parent, List<OntologySource> sources, OntologyRetriever retriever) {
        super(parent, "Ontology Sources Preferences", true);
        this.ontologySources = sources;
        this.ontologyRetriever = retriever;
        
        initializeUI();
        setSize(800, 500);
        setLocationRelativeTo(parent);
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Manage Ontology Sources");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(topPanel, BorderLayout.NORTH);
        
        tableModel = new OntologySourceTableModel(ontologySources, ontologyRetriever);
        sourcesTable = new JTable(tableModel);
        sourcesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sourcesTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        sourcesTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        sourcesTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        sourcesTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        sourcesTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(sourcesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
        statusLabel = new JLabel("Ready");
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        statusPanel.add(statusLabel, BorderLayout.NORTH);
        statusPanel.add(progressBar, BorderLayout.CENTER);
        bottomPanel.add(statusPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        updateButton = new JButton("Update Selected");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSelectedOntology();
            }
        });
        buttonPanel.add(updateButton);
        
        closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(closeButton);
        
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void updateSelectedOntology() {
        int selectedRow = sourcesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select an ontology source to update.", 
                "No Selection", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        OntologySource source = ontologySources.get(selectedRow);
        
        updateButton.setEnabled(false);
        closeButton.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        statusLabel.setText("Updating " + source.getName() + "...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private String errorMessage = null;
            
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    ontologyRetriever.clearCache(source);
                    ontologyRetriever.retrieveOntology(source);
                } catch (Exception e) {
                    logger.error("Failed to update ontology: " + source.getName(), e);
                    errorMessage = e.getMessage();
                }
                return null;
            }
            
            @Override
            protected void done() {
                progressBar.setVisible(false);
                progressBar.setIndeterminate(false);
                updateButton.setEnabled(true);
                closeButton.setEnabled(true);
                
                if (errorMessage != null) {
                    statusLabel.setText("Update failed: " + errorMessage);
                    JOptionPane.showMessageDialog(OntologyPreferencesPanel.this, 
                        "Failed to update " + source.getName() + ": " + errorMessage, 
                        "Update Failed", 
                        JOptionPane.ERROR_MESSAGE);
                } else {
                    statusLabel.setText("Successfully updated " + source.getName());
                    tableModel.fireTableDataChanged();
                    JOptionPane.showMessageDialog(OntologyPreferencesPanel.this, 
                        "Successfully updated " + source.getName(), 
                        "Update Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private static class OntologySourceTableModel extends AbstractTableModel {
        
        private static final long serialVersionUID = 1L;
        private final String[] columnNames = {"Name", "URL", "Enabled", "Last Updated", "Format"};
        private final List<OntologySource> sources;
        private final OntologyRetriever retriever;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        public OntologySourceTableModel(List<OntologySource> sources, OntologyRetriever retriever) {
            this.sources = sources;
            this.retriever = retriever;
        }
        
        @Override
        public int getRowCount() {
            return sources.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 2) {
                return Boolean.class;
            }
            return String.class;
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 2;
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            OntologySource source = sources.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return source.getName();
                case 1:
                    return source.getUrl();
                case 2:
                    return source.isEnabled();
                case 3:
                    long lastRetrievalTime = retriever.getLastRetrievalTime(source);
                    if (lastRetrievalTime > 0) {
                        return dateFormat.format(new Date(lastRetrievalTime));
                    }
                    return "Never";
                case 4:
                    return source.getFormat();
                default:
                    return null;
            }
        }
        
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            if (columnIndex == 2) {
                OntologySource source = sources.get(rowIndex);
                source.setEnabled((Boolean) value);
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }
}
