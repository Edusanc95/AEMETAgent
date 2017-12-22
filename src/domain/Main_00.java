/**
 * 
 */
package domain;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author sergio
 *
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Main_00 {

	private static final String AEMET = "http://www.aemet.es";
	private static final String ID_CC_AA_DIR = "/es/eltiempo/prediccion/espana";
	private static final String ULTIMOS_DATOS = "/es/eltiempo/observacion/ultimosdatos";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> id_comunidades = new ArrayList<String>();
		
		 id_comunidades= aemetAgent();
		 enclaveAgent(comunidadAutonoma(id_comunidades));


	}

	
	private static ArrayList<String> aemetAgent() {
		ArrayList <String> id_comunidad = new ArrayList<String>();
		System.out.println("\\nAGENTE AEMET\n---------------------");
		try{
			Document doc = Jsoup.connect(AEMET + ID_CC_AA_DIR).get();
			
			Elements form = doc.select("form[name=\"frm1\"]");
			
			Elements comunidades = doc.select("option:not(option[selected])");
			for(Element comunidad: comunidades ) {
				System.out.println(comunidad.attr("value"));
				id_comunidad.add(comunidad.attr("value"));
			}

			
			return id_comunidad;
			
		}catch(Exception e){
			System.out.println("Exception "+e);
			return null;
		}
		
	}
	
	private static String comunidadAutonoma(ArrayList<String> comunidadees_list) {
		String provincia = "";
		String localidad = "";
		String url_localidad = "";
		ArrayList<String> localidad_list = new ArrayList<String>();
		Hashtable<String, ArrayList<String>> provicias_table = new Hashtable<String, ArrayList<String>>();
		
		
		String url_comunidad = AEMET + ULTIMOS_DATOS + "?k="+ comunidadees_list.get(0)+"&w=0";
		
		System.out.println("\nAGENTE C.C.A.A.\n---------------------");
		System.out.println(url_comunidad);
		try{
			Document doc = Jsoup.connect(url_comunidad).get();
			
			Elements provincias = doc.select("form[name=\"frm1\"] optgroup");
			for(Element elem_provincia: provincias) {
				provincia = elem_provincia.attr("label");
				System.out.println(provincia);
				
				localidad_list.clear();
				Elements localidades = doc.select("form[name=\"frm1\"] optgroup[label=\"" + provincia + "\"] option");
				for(Element elem_localidad: localidades) {
					localidad = elem_localidad.attr("value");
					System.out.println("\t" + localidad);
					localidad_list.add(localidad);
				}
				provicias_table.put(provincia, localidad_list);
			}
			
			url_localidad = AEMET + ULTIMOS_DATOS + "?k="+ comunidadees_list.get(0) +
										"&l=" + provicias_table.get(provincia).get(0) + "&w=0&datos=img&x=h24&f=temperatura";
			System.out.println(url_localidad);
					
		}catch(Exception e){
			System.out.println("Exception "+e);
		}
		return url_localidad;
	}
	
	
	private static void enclaveAgent(String url_link) {
		
		/**
		String url_localidad = AEMET + ULTIMOS_DATOS + "?k="+ comunidadees_list.get(0) +
				"&l=" + provicias_table.get(provincia).get(0) + "&w=0&datos=img&x=h24&f=temperatura";
		**/
		
		String latitud = "";
		String longitude = "";
		
		
		String url_enclave = url_link;
		
		System.out.println("\nAGENTE ENCLAVE\n---------------------");
		System.out.println(url_enclave);
		try{
			Document doc = Jsoup.connect(url_enclave).get();
			
			Elements latitudes = doc.select(".latitude");
			for(Element elem_latitud: latitudes) {
				latitud = elem_latitud.attr("title");
				System.out.println("\t\tLatitud: " + latitud);
			}
			
			
			Elements longitudes = doc.select(".longitude");
			for(Element elem_longitudes: longitudes) {
				longitude = elem_longitudes.attr("title");
				System.out.println("\t\tLongitud: " + longitude);
			}
			
		}catch(Exception e){
			System.out.println("Exception "+e);
		}
	}
	
}
