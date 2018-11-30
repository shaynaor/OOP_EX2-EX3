package File_format;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import GIS.Element;
import GIS.GIS_element;
import GIS.GIS_layer;
import GIS.Layer;
import GIS.Mdata;
import GIS.Project;


/**
 * This class does multiple actions on files. such as: read csv file and write
 * kml file.
 * 
 * @author Shay Naor and Alex Vaisman.
 *
 */
public class MyFileUtils {

	/**
	 * This function recived a path to a csv file and create a ArrayList<String[]>
	 * that contains the csv file data.
	 * 
	 * @param path      location of csv file.
	 * @param startLine from which line to read.
	 * @return ArrayList<String[]> contains the csv file data.
	 * @throws IOException if the path is incorrect the function throws IOException.
	 */
	public static ArrayList<String[]> readCSVFile(String path, int startLine) throws IOException {
		int counter = 0;
		String line = "";
		String cvsSplitBy = ",";
		String[] userInfo = {};
		ArrayList<String[]> container = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(path));

		while ((line = br.readLine()) != null) {
			userInfo = line.split(cvsSplitBy);
			if (counter >= startLine)
				container.add(userInfo);
			counter++;

		}
		br.close();
		return container;
	}

	/**
	 * This function recived a path and ArrayList<String[]> that contains the csv
	 * file data and write a kml file.
	 * 
	 * @param path      location of kml file.
	 * @param container contains the csv file data that we want to convert to kml
	 *                  file.
	 * @throws IOException if the path is incorrect the function throws IOException.
	 */
	public static void writeKMLFile(String path, Project project) throws IOException {
		StringBuilder sb = new StringBuilder();
		FileWriter fw = new FileWriter(path);
		BufferedWriter bw = new BufferedWriter(fw);
		Iterator<GIS_layer> itLayer = project.iterator();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		sb.append(
				"<kml xmlns=\"http://www.opengis.net/kml/2.2\"><Document><Style id=\"red\"><IconStyle><Icon><href>http://maps.google.com/mapfiles/ms/icons/red-dot.png</href></Icon></IconStyle></Style><Style id=\"yellow\"><IconStyle><Icon><href>http://maps.google.com/mapfiles/ms/icons/yellow-dot.png</href></Icon></IconStyle></Style><Style id=\"green\"><IconStyle><Icon><href>http://maps.google.com/mapfiles/ms/icons/green-dot.png</href></Icon></IconStyle></Style><Folder><name>Wifi Networks</name>\n");
		while (itLayer.hasNext()) {
			Layer temp = (Layer) itLayer.next();
			Iterator<GIS_element> itElement = temp.iterator();
			while (itElement.hasNext()) {
				Element element = (Element) itElement.next();
				sb.append("<Placemark>\n");
				sb.append("<name>" + "<![CDATA[" +  element.getMetaData().getSSID() + "]]>" + "</name>\n");
				sb.append("<description>" + "<![CDATA[BSSID: <b>" + element.getMetaData().getMAC() + "</b><br/>Capabilities: <b>" + element.getMetaData().getAuthMode() + "</b><br/>Timestamp: <b>" + element.getData().getUTC() + "</b><br/>Channel: <b>" + element.getMetaData().getChannel() + "</b><br/>RSSI: <b>" + element.getMetaData().getRSSI() + "</b><br/>AltitudeMeters: <b>" + element.getGps().z() + "</b><br/>AccuracyMeters: <b>" + element.getMetaData().getAccuracyMeters() + "</b><br/>Type: <b>" + element.getMetaData().getType() + "</b><br/>Date: <b>" + element.getMetaData().getFirstSeen() + "</b>]]>" + "</description><styleUrl>#red</styleUrl>\n");
				sb.append("<Point>\n");
				sb.append("<coordinates>" + element.getGps().y() + "," + element.getGps().x() + "</coordinates></Point>\n");
				sb.append("</Placemark>");
			}

		}
		sb.append("\n</Folder>\n");
		sb.append("</Document></kml>") ;
		bw.write(sb.toString());
		bw.close();

	}

	/**
	 * This function recives folder path and return a ArrayList<String> that
	 * contains all the paths for csv files in folder. The algorithem taken from :
	 * https://stackoverflow.com/questions/1844688/how-to-read-all-files-in-a-folder-from-java.
	 * 
	 * @param folder that contains the csv files.
	 * @return ArrayList<String> that contains all the paths for csv files in
	 *         folder.
	 */
	public static ArrayList<String> listFilesForFolder(final File folder) throws NullPointerException {
		ArrayList<String> listFiles = new ArrayList<>();
		String path = "";
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if (fileEntry.isFile()) {
					String temp = fileEntry.getName();
					if ((temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase()).equals("csv")) {
						path = folder.getAbsolutePath() + "\\" + fileEntry.getName();
						listFiles.add(path);
					}
				}
			}
		}
		return listFiles;
	}

}