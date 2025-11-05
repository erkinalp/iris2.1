IRIS Protege Plug-in
====

IRIS is a declarative and manual tree wrapper that extracts the semi-structured product information from online shops and presents this information in a GoodRelations-compliant ontology. IRIS also automatically marks up your products using RDFa or Microdata. Creating GoodRelations snippets in RDFa or Microdata using the product information extracted from Web is a business value, especially when you consider most of the popular search engines including Google and Yahoo recommend the use of these standards to provide rich site data for their index. Finally, IRIS is a Protégé plug-in which is developed as an open source Java Application.

## Version 2.1 - Protege 5.6.8 Port

This version has been ported to work with Protege 5.6.8, featuring:
- Migration from frame-based API to OWL API
- Modern Maven-based build system with OSGi bundle configuration
- Support for both Excel (.xls) and ODF (.ods) spreadsheet formats for units of measurement
- All original functionality preserved including web scraping, GoodRelations integration, and ontology creation

### Requirements

- Java 11 or higher
- Protege 5.6.8
- Maven 3.6+ (for building from source)

### Spreadsheet Format Support

Units of measurement files (annex1 and annex2) can be provided in either:
- Excel format (.xls) - legacy support using JXL library
- ODF format (.ods) - modern open format using SODS library

The plugin automatically detects and uses the appropriate format. ODF files are preferred if both formats are present.

<h2>How can I use IRIS?</h2>
It is simple:
<ol>
<li>Create a Template File</li>
<li>Open the Ontology and the Template</li>
<li>Create a GoodRelations-compliant Ontology
        or
    	Create a Custom Ontology from Scratch</li>
<li>Save and Serialize the Ontology</li>
</ol>

<h2>1. Create a Template File</h2>
Creating a template file is the basic step for using the IRIS plug-in. The information collected using the template is mapped to the attributes of the Product object. Title, brand, productID, description, and imgLink are single-valued attributes of the Product object whereas feature, includes, propertyName and propertyValue are multi-valued attributes. 

A template has two parts; the first part contains the tree that specifies the paths of HTML tags around the product attribute names and product attribute values. The second part contains the information on how the HTML documents should be acquired.

The product information is extracted using the tree in the first part of the template. The tree is created manually by the user therefore the user should analyze the HTML code of the Web page carefully. “amazon.com” pages that contain information about laptops are used as an example (amazon.txt). 

<h2>2. Open the ontology and the Template</h2>
The next step is opening an empty ontology to store the extracted information. IRIS is developed as a plug-in for Protégé Ontology Editor therefore the user opens an empty ontology in Protégé Editor preceding the following steps:
<ol>
<li>From the welcome dialog, select “Create New Project...” then “The Create New Project” wizard appears. Select “OWL/RDF Files” then in the next step select “OWL Files”.</li>
<li>Go to the next step and select “myOwl.owl” file by clicking the “Browse for File” button. In the next step, select “OWL DL” dialect. Finally, choose “Properties View”. The Protégé window opens and the standard tabs become visible.</li>
<li>Choose Project - Configure... to bring up the “Configure” dialog. The new IRIS tab will be listed on the TabWidgets panel. Check the “Visible” checkbox and click the OK button. The system will now display the IRIS tab. </li>
</ol>

After opening the ontology, you select the template that is be used to import product information from the Web. In my example, I choose the amazon.txt by clicking the “Open template” button. The “Properties Window” lists all properties of the individuals in “Individuals Window”. 

<h2>3. Create the Ontology</h2>
The next step is creating the ontology that stores the product information. Here the plug-in offers two ways:
<ul>
<li>Create an ontology that is compatible with GoodRelations Vocabulary</li>
<li>Create a custom ontology from scratch</li>
</ul>

<h3>3.1. Create an Ontology that is Compatible with GoodRelations Vocabulary</h3>

For creating a GoodRelations-compliant ontology using IRIS plug-in, user selects the individuals and properties that will reside in the ontology. Then she clicks the “Use GoodRelations Vocabulary” button and “Use GoodRelations Vocabulary” wizard appears. She selects the corresponding GoodRelations property type and respective unit of measurement for each selected property. 

<h3>3.2. Create a Custom Ontology from Scratch</h3>
In order to create a custom ontology, user selects the individuals and properties that will reside in the ontology. Then she clicks the “Set types/units for properties” button and “Set types/Units for properties” wizard appears. User specifies whether the property is OWLObjectProperty. Otherwise the property is defined as OWLDatatypeProperty. This version of the tool does not support units in custom ontologies. Therefore, if there is a unit value in the imported data, user chooses the “Remove Unit” field.
 
<h2>4. Save and Serialize the Ontology</h2>
The user clicks the “Save” button and saves the ontology in an owl file. Then she clicks the “Export to a serialization format” button and views the ontology in one of the structured data markup standards.



