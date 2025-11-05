package edu.boun.iris;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.io.IOUtils;

public class Html2ProductMatcher {
    DefaultMutableTreeNode root = null;
    public static String next_page;
    public static String page_range;
    public static String base_uri;
    public static String page_uri;
    public static String classToSearch;

    public Html2ProductMatcher(File file) {
        Html2ProductNode uNode = new Html2ProductNode();
        try {
            root = readFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fillPnode(Html2ProductNode pnode) {
        String[] tokens = SplitUsingTokenizer(pnode.fullText, ",");
        String val;

        for (int i = 0; i < tokens.length; i++) {
            val = tokens[i].substring((tokens[i].indexOf('(')) + 1, (tokens[i].indexOf(')')));

            if (tokens[i].toLowerCase().trim().startsWith("select")) {
                pnode.select = val;
            }

            if (tokens[i].toLowerCase().trim().startsWith("attr")) {
                pnode.atrr = val;
            }

            if (tokens[i].toLowerCase().trim().startsWith("value")) {
                pnode.value = val;
            }

            if (tokens[i].toLowerCase().trim().startsWith("as")) {
                pnode.selectAs = val;
            }

            if (tokens[i].toLowerCase().trim().startsWith("getmethod")) {
                pnode.getMethod = val;
            }

            if (tokens[i].toLowerCase().trim().startsWith("order")) {
                pnode.order = Integer.parseInt(val);
            }
        }
    }

    public DefaultMutableTreeNode readFile(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        ArrayList<DefaultMutableTreeNode> nodeList = new ArrayList<DefaultMutableTreeNode>();
        String fString, pString, fileText;
        try {
            fileText = IOUtils.toString(inputStream, "UTF-8");

            int index = fileText.indexOf("NEXT_PAGE");
            fString = fileText.substring(0, index - 1);
            pString = fileText.substring(index);
            buildParameters(pString);
        } finally {
            inputStream.close();
        }
        String[] prenodes = SplitUsingTokenizer(fString, "[];");
        System.out.println("PRENODES: " + prenodes.length);
        Html2ProductNode pnode;
        DefaultMutableTreeNode newnode;
        Html2ProductNode node_i;
        int startindex, stopindex;
        String substring;
        Html2ProductNode node_j;

        for (int i = 0; i < prenodes.length; i++)
            if (!prenodes[i].trim().isEmpty()) {
                pnode = new Html2ProductNode();
                pnode.fullText = prenodes[i];
                fillPnode(pnode);
                newnode = new DefaultMutableTreeNode(pnode);
                nodeList.add(newnode);
            }

        for (int i = 0; i < nodeList.size(); i++) {
            node_i = (Html2ProductNode) nodeList.get(i).getUserObject();
            if (fString.charAt(fString.indexOf(node_i.fullText) + node_i.fullText.length()) == '[') {
                startindex = fString.indexOf(node_i.fullText) + node_i.fullText.length() + 1;
                int o_count = 1;
                stopindex = -1;
                for (int j = startindex; j < fString.length(); j++) {
                    if (fString.charAt(j) == '[')
                        o_count++;
                    if (fString.charAt(j) == ']')
                        o_count--;
                    if (o_count == 0) {
                        stopindex = j;
                        break;
                    }
                }

                if (stopindex == -1)
                    stopindex = fString.length() - 1;
                substring = fString.substring(startindex, stopindex);
                for (int j = 0; j < nodeList.size(); j++) {
                    node_j = (Html2ProductNode) nodeList.get(j).getUserObject();
                    if (substring.contains(node_j.fullText) && !node_j.fullText.isEmpty()) {
                        nodeList.get(i).add(nodeList.get(j));
                        System.out.println(i + "-" + j);
                    }
                }
            }
        }
        return nodeList.get(0);
    }

    public static void buildParameters(String pString) {
        int parameter_counter = 0;
        int index, endIndex;

        while (pString.indexOf(":{") >= 0 && pString.indexOf("}") >= 0
                && pString.indexOf("}") > pString.indexOf(":{")) {

            index = pString.indexOf(":{");
            endIndex = pString.indexOf("}");

            if (parameter_counter == 0)
                next_page = pString.substring(index + 2, endIndex);

            if (parameter_counter == 1)
                page_range = pString.substring(index + 2, endIndex);

            if (parameter_counter == 2)
                base_uri = pString.substring(index + 2, endIndex);

            if (parameter_counter == 3)
                page_uri = pString.substring(index + 2, endIndex);

            if (parameter_counter == 4)
                classToSearch = pString.substring(index + 2, endIndex);

            pString = pString.substring(endIndex + 1);
            parameter_counter++;
        }
    }

    public static String[] SplitUsingTokenizer(String Subject, String Delimiters) {
        StringTokenizer StrTkn = new StringTokenizer(Subject, Delimiters);
        ArrayList<String> ArrLis = new ArrayList<String>(Subject.length());
        while (StrTkn.hasMoreTokens()) {
            ArrLis.add(StrTkn.nextToken());
        }
        return ArrLis.toArray(new String[0]);
    }

    public DefaultMutableTreeNode getRoot() {
        return root;
    }
}
