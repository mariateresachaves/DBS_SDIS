package Service;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;

public class XMLDatabase {

	private File fXmlFile;
	private DocumentBuilderFactory dbFactory;
	private DocumentBuilder dBuilder;
	private Document doc;

	public XMLDatabase(String databaseFile) throws ParserConfigurationException, SAXException, IOException {
		this.fXmlFile = new File(databaseFile);

		// Create DB if not exists
		if (!this.fXmlFile.exists()) {
			createDBfile();
		}

		dbFactory = DocumentBuilderFactory.newInstance();
		dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse(fXmlFile);
	}

	private void createDBfile() throws FileNotFoundException {
		Formatter ft = new Formatter(fXmlFile);
		ft.format("%s\n", "<?xml version=\"1.0\"?>");
		ft.format("<database>");
		ft.format("<files>");
		ft.format("</files>");
		ft.format("<chunks>");
		ft.format("</chunks>");
		ft.format("</database>");
		ft.flush();
		ft.close();

		String PathofNewFile = this.fXmlFile.getPath();
		this.fXmlFile = new File(PathofNewFile);
	}

	public void addFile(String ifilePath, String iFileId, String idesiredRD, String ird) {
		NodeList nList = doc.getElementsByTagName("files");

		// Adicionar novo nó, Item 0 é os ficheiros
		Node add = nList.item(0);
		Element file = doc.createElement("file");

		Element filepath = doc.createElement("filepath");
		filepath.appendChild(doc.createTextNode(ifilePath));

		Element fileId = doc.createElement("fileId");
		fileId.appendChild(doc.createTextNode(iFileId));

		Element drd = doc.createElement("desiredreplicationdegree");
		drd.appendChild(doc.createTextNode(idesiredRD));

		Element rd = doc.createElement("RD");
		rd.appendChild(doc.createTextNode(ird));

		file.appendChild(filepath);
		file.appendChild(fileId);
		file.appendChild(drd);
		file.appendChild(rd);

		add.appendChild(file);
	}

	public void saveDatabase() throws TransformerException {
		DOMSource source = new DOMSource(doc);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StreamResult result = new StreamResult(fXmlFile.getPath());
		transformer.transform(source, result);
	}

	public ArrayList<String> getFiles() {
		ArrayList<String> files = new ArrayList<>();

		NodeList nList = doc.getElementsByTagName("file");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			String line;
			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				line = String.format("%s\t%s\t%s\t%s\n",
						eElement.getElementsByTagName("filepath").item(0).getTextContent(),
						eElement.getElementsByTagName("fileId").item(0).getTextContent(),
						eElement.getElementsByTagName("desiredreplicationdegree").item(0).getTextContent(),
						eElement.getElementsByTagName("RD").item(0).getTextContent());

				NodeList parts = eElement.getElementsByTagName("part");
				for (int i = 0; i < parts.getLength(); i++) {
					Node partNode = parts.item(i);

					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element part = (Element) partNode;
						line = String.format("\n%s\tPart %s\t RD->%s\n", line, part.getAttribute("pid"),
								part.getElementsByTagName("partRD").item(0).getTextContent());
					}
				}

				files.add(line);
			}
		}
		return files;
	}

	public boolean isChunkPresent(String senderId, String fileId, String chunkNo) {
		NodeList nList = doc.getElementsByTagName("chunk");

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				try {
					Element eElement = (Element) nNode;

					String sID = eElement.getElementsByTagName("senderId").item(0).getTextContent();
					String fID = eElement.getElementsByTagName("fileId").item(0).getTextContent();
					String cNo = eElement.getElementsByTagName("chunkNo").item(0).getTextContent();

					if (sID.equalsIgnoreCase(senderId) && fID.equalsIgnoreCase(fileId)
							&& cNo.equalsIgnoreCase(chunkNo)) {
						return true;
					}
				} catch (NullPointerException e) {
					// Hammer TIME! if something gives null it means that there
					// is no record
					continue;
				}
			}
		}
		return false;
	}

	public void addToChunkRD(int valueToAdd, String fileid, String chunkNo) {
		NodeList nList = doc.getElementsByTagName("chunk");

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				try {
					Element eElement = (Element) nNode;

					String fID = eElement.getElementsByTagName("fileId").item(0).getTextContent();
					String cNo = eElement.getElementsByTagName("chunkNo").item(0).getTextContent();

					if (fID.equalsIgnoreCase(fileid) && cNo.equalsIgnoreCase(chunkNo)) {

						int rd = Integer.parseInt(eElement.getElementsByTagName("RD").item(0).getTextContent().trim());
						eElement.getElementsByTagName("RD").item(0).setTextContent((rd + valueToAdd) + "");

					}
				} catch (NullPointerException e) {
					// Hammer TIME! if something gives null it means that there
					// is no record
					continue;
				}
			}
		}
	}

	public boolean isFilePresent(String filePath, String fileId) {
		NodeList nList = doc.getElementsByTagName("file");

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				try {
					Element eElement = (Element) nNode;

					String sID = eElement.getElementsByTagName("filepath").item(0).getTextContent();
					String fID = eElement.getElementsByTagName("fileId").item(0).getTextContent();

					if (sID.equalsIgnoreCase(filePath) && fID.equalsIgnoreCase(fileId)) {
						return true;
					}
				} catch (NullPointerException e) {
					// Hammer TIME! if something gives null it means that there
					// is no record
					continue;
				}
			}
		}
		return false;
	}

	public ArrayList<String> getChunks() {
		ArrayList<String> files = new ArrayList<>();

		NodeList nList = doc.getElementsByTagName("chunk");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			String line;
			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				line = String.format("%s\t%s\t%s\t%s\t%s\n",

						eElement.getElementsByTagName("senderId").item(0).getTextContent(),
						eElement.getElementsByTagName("fileId").item(0).getTextContent(),
						eElement.getElementsByTagName("chunkNo").item(0).getTextContent(),
						eElement.getElementsByTagName("desiredreplicationdegree").item(0).getTextContent(),
						eElement.getElementsByTagName("RD").item(0).getTextContent());
				files.add(line);
			}
		}
		return files;
	}

	public void addChunk(String isenderId, String iFileId, String ichunkNo, String idesiredRD, String ird) {
		NodeList nList = doc.getElementsByTagName("chunks");

		// Adicionar novo nó, Item 0 é os ficheiros
		Node add = nList.item(0);
		Element file = doc.createElement("chunk");

		Element filepath = doc.createElement("senderId");
		filepath.appendChild(doc.createTextNode(isenderId));

		Element fileId = doc.createElement("fileId");
		fileId.appendChild(doc.createTextNode(iFileId));

		Element chunkNo = doc.createElement("chunkNo");
		chunkNo.appendChild(doc.createTextNode(ichunkNo));

		Element drd = doc.createElement("desiredreplicationdegree");
		drd.appendChild(doc.createTextNode(idesiredRD));

		Element rd = doc.createElement("RD");
		rd.appendChild(doc.createTextNode(ird));

		file.appendChild(filepath);
		file.appendChild(fileId);
		file.appendChild(chunkNo);
		file.appendChild(drd);
		file.appendChild(rd);

		System.out.println(filepath);
		System.out.println(fileId);
		System.out.println(chunkNo);
		System.out.println(rd);

		add.appendChild(file);
	}

	public void addFilePart(String filePath, String fileID, int chunkNo) {
		NodeList nList = doc.getElementsByTagName("file");

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				try {
					Element eElement = (Element) nNode;

					String sID = eElement.getElementsByTagName("filepath").item(0).getTextContent();
					String fID = eElement.getElementsByTagName("fileId").item(0).getTextContent();

					if (sID.equalsIgnoreCase(filePath) && fID.equalsIgnoreCase(fileID)) {
						// ADICIONAR PART!!!
						Element file = doc.createElement("part");

						file.setAttribute("pid", chunkNo + "");

						Element prd = doc.createElement("partRD");
						prd.appendChild(doc.createTextNode("0"));

						file.appendChild(prd);

						nNode.appendChild(file);

					}
				} catch (NullPointerException e) {
					// Hammer TIME! if something gives null it means that there
					// is no record
					continue;
				}
			}
		}
	}

	public void updateFilePart(String filePath, String fileID, int chunkNo, int valorAAdicionar) {
		NodeList nList = doc.getElementsByTagName("file");

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				try {
					Element eElement = (Element) nNode;

					String sID = eElement.getElementsByTagName("filepath").item(0).getTextContent();
					String fID = eElement.getElementsByTagName("fileId").item(0).getTextContent();

					if (sID.equalsIgnoreCase(filePath) && fID.equalsIgnoreCase(fileID)) {

						// Estou no ficheiro certo vou iterar agora as parts
						NodeList parts = eElement.getElementsByTagName("part");
						for (int i = 0; i < parts.getLength(); i++) {
							Node partNode = parts.item(temp);
							if (nNode.getNodeType() == Node.ELEMENT_NODE) {

								try {
									Element part = (Element) partNode;
									if (part.getAttribute("pid").equalsIgnoreCase(chunkNo + "")) {
										int valor = Integer.parseInt(
												eElement.getElementsByTagName("partRD").item(0).getTextContent());

										eElement.getElementsByTagName("partRD").item(0)
												.setTextContent((valor + valorAAdicionar) + "");
									}
								} catch (Exception e) {
									// NOPE
								}
							}
						}
					}
				} catch (NullPointerException e) {
					// Hammer TIME! if something gives null it means that there
					// is no record
					continue;
				}
			}
		}
	}

	public boolean isPartPresent(String filePath, String fileID, int chunkNo) {
		NodeList nList = doc.getElementsByTagName("file");

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				try {
					Element eElement = (Element) nNode;

					String sID = eElement.getElementsByTagName("filepath").item(0).getTextContent();
					String fID = eElement.getElementsByTagName("fileId").item(0).getTextContent();

					if (sID.equalsIgnoreCase(filePath) && fID.equalsIgnoreCase(fileID)) {

						// Estou no ficheiro certo vou iterar agora as parts
						NodeList parts = eElement.getElementsByTagName("part");
						for (int i = 0; i < parts.getLength(); i++) {
							Node partNode = parts.item(i);
							if (nNode.getNodeType() == Node.ELEMENT_NODE) {

								try {
									Element part = (Element) partNode;

									if (part.getAttribute("pid").trim().equalsIgnoreCase(chunkNo + "")) {
										return true;
									}
								} catch (Exception e) {
									// NOPE
								}
							}
						}
					}
				} catch (NullPointerException e) {
					// Hammer TIME! if something gives null it means that there
					// is no record
					continue;
				}
			}

		}
		return false;
	}

}