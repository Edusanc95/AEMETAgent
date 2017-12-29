/**
 * 
 */
package domain;

import java.util.Hashtable;

/**
 * @author sergio
 *
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jade.util.leap.LinkedList;
import view.MainFrame;


public class Main_00 {

	private static final String AEMET = "http://www.aemet.es";
	private static final String ID_CC_AA_DIR = "/es/eltiempo/prediccion/espana";
	private static final String ULTIMOS_DATOS = "/es/eltiempo/observacion/ultimosdatos";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Hashtable<String,LinkedList> comunidades_table = new Hashtable<String, LinkedList>();

		comunidades_table= aemetAgent();
		
		for(String comunidad_key : comunidades_table.keySet()) {
			LinkedList stations_list = new LinkedList();
			stations_list = comunidades_table.get(comunidad_key);
			
			for(int i=0; i<stations_list.size(); i++) {
				System.out.println(stations_list.get(i).toString());
			}
				
		} 

		MainFrame mf = new MainFrame(this);
		mf.setInfo(comunidades_table);
	}

	
	private static Hashtable<String, LinkedList> aemetAgent() {
		Hashtable<String, String>comunidades_table = new Hashtable<String, String>();
		Hashtable<String, LinkedList>comunidades_stations_table = new Hashtable<String, LinkedList>();
		System.out.println("\\nAGENTE AEMET\n---------------------");
		try{
			Document doc = Jsoup.connect(AEMET + ID_CC_AA_DIR).get();
			
			Elements form = doc.select("form[name=\"frm1\"]");
			
			Elements comunidades = form.select("option:not(option[selected])");
			for(Element comunidad: comunidades ) {
				//System.out.println(comunidad.attr("value")+" --> " + comunidad.text());
				comunidades_table.put(comunidad.attr("value"), comunidad.text());
			}
			LinkedList weatherStation_list = new LinkedList();
			
			for (String comunidad_key : comunidades_table.keySet()) {
				weatherStation_list = comunidadAutonoma(comunidad_key, comunidades_table.get(comunidad_key));
				comunidades_stations_table.put(comunidad_key, weatherStation_list);
			}
			
			return comunidades_stations_table;
			
		}catch(Exception e){
			System.out.println("Exception "+e);
			return null;
		}
		
	}
	
	private static LinkedList comunidadAutonoma(String comunidad_key, String comunidad_name) {
		String url_enclave = "";
		
		Hashtable<String,String> localidades_table = new Hashtable<String,String>();
		Hashtable<String, Hashtable<String,String>> provincias_table = new Hashtable<String, Hashtable<String,String>>();
		LinkedList weatherStation_list = new LinkedList(); 
	
		String url_comunidad = AEMET + ULTIMOS_DATOS + "?k="+ comunidad_key +"&w=0";
		
//		System.out.println("\nAGENTE C.C.A.A.\n---------------------");
		try{
			Document doc = Jsoup.connect(url_comunidad).get();
			
			provincias_table.clear();
			Elements provincias = doc.select("form[name=\"frm1\"] optgroup");
			
			for(Element elem_provincia: provincias) {
				String provincia = elem_provincia.attr("label");
				
				localidades_table.clear();
				Elements localidades = doc.select("form[name=\"frm1\"] optgroup[label=\"" + provincia + "\"] option");
				
				for(Element elem_localidad: localidades) {	
					String id_localidad = elem_localidad.attr("value");
					String localidad = elem_localidad.text();
		
					localidades_table.put(id_localidad, localidad);
				}
				
				provincias_table.put(provincia, localidades_table);
				
				for(String localidad_key : provincias_table.get(provincia).keySet()) {
					url_enclave = AEMET + ULTIMOS_DATOS + "?k="+ comunidad_key +
										"&l=" + localidad_key + "&w=0&datos=img&x=h24&f=Todas";
					String localidad = provincias_table.get(provincia).get(localidad_key);
//					System.out.println(comunidad_key + ", " + provincia + ", " + localidad);
//					System.out.println(url_enclave);
					Location loc =  new Location();
					loc = enclaveAgent(url_enclave);
//					System.out.println("\t Lat: " + loc.getLatitude() + " / Long: "+ loc.getLongitude());
					weatherStation_list.add(new WeatherStation(comunidad_key, comunidad_name, provincia, localidad_key, localidad, loc));
				}
				
			}
				
		}catch(Exception e){
			System.out.println("Exception "+e);
		}
		
		return weatherStation_list;	
	}
	
	
	private static Location enclaveAgent(String url_enclave) {		
		Double latitude = null;
		Double longitude = null;
		
//		System.out.println("\nAGENTE ENCLAVE\n---------------------");
		try{
			Document doc = Jsoup.connect(url_enclave).get();
			
			Elements latitudes = doc.select(".latitude");
			for(Element elem_latitud: latitudes) {
				latitude = Double.parseDouble(elem_latitud.attr("title"));
			}
			
			
			Elements longitudes = doc.select(".longitude");
			for(Element elem_longitudes: longitudes) {
				longitude = Double.parseDouble(elem_longitudes.attr("title"));
			}
			
		}catch(Exception e){
			System.out.println("Exception "+e);
		}
		
		return new Location(latitude,longitude);
	}
	
}
