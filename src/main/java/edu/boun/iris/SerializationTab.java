package edu.boun.iris;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.model.OWLOntology;

public class SerializationTab extends JDialog implements ActionListener {
    OWLOntology owlOntology;
    OutputStream output;

    private JTabbedPane tabbedPane1;
    private JScrollPane scrollPane1;;
    private JSplitPane splitPane1;
    private JButton button1;
    private JScrollPane scrollPane2;
    private JScrollPane scrollPane2_1;
    private JScrollPane scrollPane2_2;
    private JScrollPane scrollPane2_3;
    private JScrollPane scrollPane2_4;
    private JScrollPane scrollPane3;
    private JScrollPane scrollPane4;
    private JScrollPane scrollPane5;
    private JScrollPane scrollPane6;
    private JScrollPane scrollPane7;
    private File savedFile;

    public SerializationTab(OWLOntology owlOntology, File savedFile) {
        this.owlOntology = owlOntology;
        this.savedFile = savedFile;

        output = new OutputStream() {

            @Override
            public void write(int arg0) throws IOException {

            }
        };

        initialize();
    }

    public void initialize() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle("Export to a Serialization Format");
        setIconImage(new ImageIcon("DoesNotExist.gif").getImage());

        tabbedPane1 = new JTabbedPane();
        splitPane1 = new JSplitPane();
        button1 = new JButton();
        button1.addActionListener(this);

        scrollPane2 = new JScrollPane();
        scrollPane2_1 = new JScrollPane();
        scrollPane2_2 = new JScrollPane();
        scrollPane2_3 = new JScrollPane();
        scrollPane2_4 = new JScrollPane();
        scrollPane3 = new JScrollPane();
        scrollPane4 = new JScrollPane();
        scrollPane5 = new JScrollPane();
        scrollPane6 = new JScrollPane();
        scrollPane7 = new JScrollPane();

        String htmlText = RDF2RDFa();
        JEditorPane p1 = htmlViewer(htmlText);
        scrollPane1 = new JScrollPane(p1);
        tabbedPane1.addTab("RDFa", scrollPane1);
        tabbedPane1.setSelectedIndex(0);

        htmlText = RDF2Microdata();
        JEditorPane p2 = htmlViewer(htmlText);
        scrollPane2 = new JScrollPane(p2);
        tabbedPane1.addTab("Microdata", scrollPane2);

        htmlText = RDF2XML();
        JEditorPane p2_2 = htmlViewer(htmlText);
        scrollPane2_2 = new JScrollPane(p2_2);
        tabbedPane1.addTab("XML", scrollPane2_2);

        htmlText = RDF2N3();
        JEditorPane p2_3 = htmlViewer(htmlText);
        scrollPane2_3 = new JScrollPane(p2_3);
        tabbedPane1.addTab("n3", scrollPane2_3);

        htmlText = RDF2NT();
        JEditorPane p2_4 = htmlViewer(htmlText);
        scrollPane2_4 = new JScrollPane(p2_4);
        tabbedPane1.addTab("nt", scrollPane2_4);

        htmlText = RDF2rdfJsonPretty();
        JEditorPane p3 = htmlViewer(htmlText);
        scrollPane3 = new JScrollPane(p3);
        tabbedPane1.addTab("RDF-Json-Pretty", scrollPane3);

        htmlText = RDF2rdfJson();
        JEditorPane p4 = htmlViewer(htmlText);
        scrollPane4 = new JScrollPane(p4);
        tabbedPane1.addTab("RDF-Json", scrollPane4);

        htmlText = RDF2Jsonld();
        JEditorPane p5 = htmlViewer(htmlText);
        scrollPane5 = new JScrollPane(p5);
        tabbedPane1.addTab("Json-ld", scrollPane5);

        {
            splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
            splitPane1.setDividerLocation(330);

            splitPane1.setTopComponent(tabbedPane1);

            button1.setText("Close");
            button1.setPreferredSize(new Dimension(100, 23));
            button1.setMaximumSize(new Dimension(100, 23));
            button1.setMinimumSize(new Dimension(100, 23));
            splitPane1.setBottomComponent(button1);
            contentPane.add(splitPane1);
        }

        pack();
        setLocationRelativeTo(getOwner());
        setSize(600, 400);
    }

    protected JComponent makeTextPanel(String text) {
        JScrollPane scp = new JScrollPane();

        JTextPane filler = new JTextPane();
        filler.setText(text);
        scp.add(filler);
        return scp;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            setVisible(false);
            this.dispose();
        }
    }

    public static String fileToString(String file) {
        String result = null;
        DataInputStream in = null;

        try {
            File f = new File(file);
            byte[] buffer = new byte[(int) f.length()];
            in = new DataInputStream(new FileInputStream(f));
            in.readFully(buffer);
            result = new String(buffer);
        } catch (IOException e) {
            throw new RuntimeException("IO problem in fileToString", e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
        return result;
    }

    public String RDF2Microdata() {
        String encodedCode = "";
        try {
            encodedCode = URLEncoder.encode(fileToString(savedFile.getPath().toString()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String myURL = "http://rdf-translator.appspot.com/convert/xml/microdata/pygmentize/content";
        String bodyText = "content=" + encodedCode;
        String result = post(myURL, bodyText);
        return result;
    }

    public String RDF2RDFa() {
        String encodedCode = "";
        try {
            encodedCode = URLEncoder.encode(fileToString(savedFile.getPath().toString()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String myURL = "http://rdf-translator.appspot.com/convert/xml/rdfa/pygmentize/content";
        String bodyText = "content=" + encodedCode;
        String result = post(myURL, bodyText);
        return result;
    }

    private String RDF2NT() {
        String encodedCode = "";
        try {
            encodedCode = URLEncoder.encode(fileToString(savedFile.getPath().toString()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String myURL = "http://rdf-translator.appspot.com/convert/xml/nt/pygmentize/content";
        String bodyText = "content=" + encodedCode;
        String result = post(myURL, bodyText);
        return result;
    }

    private String RDF2XML() {
        String encodedCode = "";
        try {
            encodedCode = URLEncoder.encode(fileToString(savedFile.getPath().toString()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String myURL = "http://rdf-translator.appspot.com/convert/xml/xml/pygmentize/content";
        String bodyText = "content=" + encodedCode;
        String result = post(myURL, bodyText);
        return result;
    }

    private String RDF2N3() {
        String encodedCode = "";
        try {
            encodedCode = URLEncoder.encode(fileToString(savedFile.getPath().toString()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String myURL = "http://rdf-translator.appspot.com/convert/xml/n3/pygmentize/content";
        String bodyText = "content=" + encodedCode;
        String result = post(myURL, bodyText);
        return result;
    }

    private String RDF2PrettyXML() {
        String encodedCode = "";
        try {
            encodedCode = URLEncoder.encode(fileToString(savedFile.getPath().toString()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String myURL = "http://rdf-translator.appspot.com/convert/pretty-xml/rdfa/pygmentize/content";
        String bodyText = "content=" + encodedCode;
        String result = post(myURL, bodyText);
        return result;
    }

    public String RDF2rdfJson() {
        String encodedCode = "";
        try {
            encodedCode = URLEncoder.encode(fileToString(savedFile.getPath().toString()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String myURL = "http://rdf-translator.appspot.com/convert/xml/rdf-json/pygmentize/content";
        String bodyText = "content=" + encodedCode;
        String result = post(myURL, bodyText);
        return result;
    }

    public String RDF2Jsonld() {
        String encodedCode = "";
        try {
            encodedCode = URLEncoder.encode(fileToString(savedFile.getPath().toString()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String myURL = "http://rdf-translator.appspot.com/convert/xml/json-ld/pygmentize/content";
        String bodyText = "content=" + encodedCode;
        String result = post(myURL, bodyText);
        return result;
    }

    public String RDF2rdfJsonPretty() {
        String encodedCode = "";
        try {
            encodedCode = URLEncoder.encode(fileToString(savedFile.getPath().toString()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String myURL = "http://rdf-translator.appspot.com/convert/xml/rdf-json-pretty/pygmentize/content";
        String bodyText = "content=" + encodedCode;
        String result = post(myURL, bodyText);
        return result;
    }

    public String getHTML(String urlToRead) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String post(String myURL, String bodyText) {
        try {

            URL url = new URL(myURL);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);

            urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(bodyText);
            out.flush();
            out.close();

            InputStream inputStream = urlConnection.getInputStream();
            String encoding = urlConnection.getContentEncoding();
            String body = IOUtils.toString(inputStream, encoding);
            return body;
        } catch (IOException ex) {

        }
        return "";
    }

    public static JEditorPane htmlViewer(String htmlString) {
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setEditable(false);
        HTMLEditorKit kit = new HTMLEditorKit();
        jEditorPane.setEditorKit(kit);

        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
        styleSheet.addRule("h1 {color: blue;}");
        styleSheet.addRule("h2 {color: #ff0000;}");
        styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");

        Document doc = kit.createDefaultDocument();
        jEditorPane.setDocument(doc);
        jEditorPane.setText(htmlString);

        return jEditorPane;

    }

}
