package xmlParse;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by wangqchf on 2016/8/12.
 */
public class ParseTest {
    public XMLStreamReader getReader(String inputFile){
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(new File(inputFile)));
            return reader;
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void parse(XMLStreamReader reader)
    {
        try {
            while(reader.hasNext())
            {
                if(reader.isStartElement())
                {
                    System.out.println("Start Element!");
                    System.out.print("         localName:" + reader.getLocalName() + " namespaceURI:"  + reader.getNamespaceURI() + " Prefix:"  + reader.getPrefix());
                    System.out.println();
                }
                else if (reader.isCharacters() && !reader.isWhiteSpace())
                    System.out.println("                    text:" + reader.getText());
                else if(reader.isEndElement())
                    System.out.println("end of one element!" + reader.getLocalName());
                reader.next();
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
}
