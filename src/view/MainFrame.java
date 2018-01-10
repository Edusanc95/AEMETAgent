package view;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import domain.AemetAgent;
import domain.WeatherInformation;
import domain.WeatherStation;
import jade.util.leap.Collection;
import jade.util.leap.LinkedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import java.awt.Component;
import java.awt.ComponentOrientation;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.Dialog.ModalityType;

public class MainFrame {
	private Hashtable<String, LinkedList> local_comunidades_table;
	protected Hashtable<String,String> local_comunidades_name_table;
	protected String seleccion;
	final Browser browser;
	private String html;
	private JButton setMarkerButton;
	private BrowserView view;
	private JFrame frmInformacinEstacionesAemet;
	private JPanel toolBar;
	private JLabel lblZona;
	private JComboBox<String> comboBox;
	private AemetAgent aemetAgent;
	private ShowWaitAction showWaitAction;
    private JDialog dialog;

	public MainFrame(AemetAgent agent) {
		aemetAgent = agent;
		local_comunidades_table = new Hashtable<String,LinkedList>();
		local_comunidades_name_table = new Hashtable<String, String>();
		seleccion = "Todo";
		
		browser = new Browser();
		view = new BrowserView(browser);
		view.setEnabled(false);

		toolBar = new JPanel();
		
		lblZona = new JLabel("Zona: ");
		toolBar.add(lblZona);
		
		comboBox = new JComboBox<String>();
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				seleccion = (String)comboBox.getSelectedItem();
				System.out.println("Selección del combo: "+ seleccion);
			}
		});;
		comboBox.setMinimumSize(new Dimension(70, 24));
		comboBox.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		toolBar.add(comboBox);
		
		setMarkerButton = new JButton(new ShowWaitAction("Mostrar", aemetAgent));				
		toolBar.add(setMarkerButton);

		frmInformacinEstacionesAemet = new JFrame();
		frmInformacinEstacionesAemet.setResizable(false);
		frmInformacinEstacionesAemet.setTitle("Información Estaciones AEMET");
		frmInformacinEstacionesAemet.setEnabled(false);
		frmInformacinEstacionesAemet.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frmInformacinEstacionesAemet.getContentPane().add(view, BorderLayout.CENTER);
		frmInformacinEstacionesAemet.getContentPane().add(toolBar, BorderLayout.SOUTH);
		frmInformacinEstacionesAemet.setSize(900, 500);
		frmInformacinEstacionesAemet.setLocationRelativeTo(null);
		frmInformacinEstacionesAemet.setVisible(true);

		// Provide the correct full path to the map.html file, e.g. D:\\map.html
		InputStream urlStream = getClass().getResourceAsStream("../view/map.html");
		html = null;
		try (BufferedReader urlReader = new BufferedReader(new InputStreamReader (urlStream))) {
			StringBuilder builder = new StringBuilder();
			String row;
			while ((row = urlReader.readLine()) != null) {
				builder.append(row);
			}
			html = builder.toString(); 
		}  catch (IOException e) {
			throw new RuntimeException(e);
		}

		browser.loadHTML(html);
		
	}
	
	protected String seleccionCombo() {
		for(String key: local_comunidades_name_table.keySet()) {
			if(seleccion.equals(local_comunidades_name_table.get(key))){
				return key;
			}
		}
		return null;
	}

	public void setInfo(Hashtable<String, LinkedList> info_table) {
		local_comunidades_table = info_table; 
		generarMarcas(local_comunidades_table);
        dialog.dispose();
		view.setEnabled(true);
		frmInformacinEstacionesAemet.setEnabled(true);
		

		
		
	}

	private void generarMarcas(Hashtable<String, LinkedList> comunidades_table) {
		for(String comunidad_key : comunidades_table.keySet()) {
			LinkedList stations_list = new LinkedList();
			stations_list = comunidades_table.get(comunidad_key);
	
			for(int i=0; i<stations_list.size(); i++) {
				WeatherStation ws = new WeatherStation();
				ws = (WeatherStation)stations_list.get(i);
				
				if (ws.getWeather_information()==null) {
					WeatherInformation wi = new WeatherInformation("N/D", 0, 0, "N/D", "", 0, "N/D", "", 0, 0, 0, 0);
					ws.setWeather_information(wi);
				}
				
				browser.executeJavaScript("var contentString_"+ ws.getId_enclave() +" = '<div id=\"content\">'+\n" + 
						"      '<div id=\"siteNotice\">'+\n" + 
						"      '</div>'+\n" + 
						"      '<h1 id=\"firstHeading\" class=\"firstHeading\">" + ws.getComunidad_autonoma() + ", " + ws.getProvincia() + ", " + ws.getEnclave_name() + "</h1>'+\n" + 
						"      '<div id=\"bodyContent\">'+\n" + 
						"      '<table><tr><td><p><b>Fecha y hora: </b></td><td>"+ ws.getWeather_information().getFecha_hora() + "</td></tr>'+\n" + 
						"      '<tr><td><p><b>Temperatura ºC: </b></td><td>"+ ws.getWeather_information().getTemperatura_c() + "</td></tr>'+\n" + 
						"      '<tr><td><p><b>Velocidad viento: </b></td><td>" + ws.getWeather_information().getVelocidad_viento() + "</td></tr>'+\n" + 
						"      '<tr><td><p><b>Dirección viento: </b></td><td>"+ ws.getWeather_information().getDir_viento() + " <img src=\""+ ws.getWeather_information().getDir_viento_img() + "\" /></td></tr>'+\n" +
						"      '<tr><td><p><b>Racha: </b></td><td>"+ ws.getWeather_information().getRacha()+"</td></tr>'+\n" + 
						"      '<tr><td><p><b>Dirección racha: </b></td><td>"+ ws.getWeather_information().getDir_racha()+" <img src=\""+ ws.getWeather_information().getDir_racha_img() + "\" /></td></tr>'+\n" +
						"      '<tr><td><p><b>Precipitaciones (mm): </b></td><td>"+ ws.getWeather_information().getPrecipitacion()+"</td></tr>'+\n" +
						"      '<tr><td><p><b>Presión (hPa): </b></td><td>"+ ws.getWeather_information().getPresion()+"</td></tr>'+\n" + 
						"      '<tr><td><p><b>Tendencia (hPa): </b></td><td>"+ ws.getWeather_information().getTendencia()+"</td></tr>'+\n" + 
						"      '<tr><td><p><b>Humedad (%): </b></td><td>"+ ws.getWeather_information().getHumedad()+"</td></tr>'+\n" + 
						"      '<table>'+\n" + 
						"      '</div>'+\n" + 
						"      '</div>';\n" + 
						"\n" + 
						"  var infowindow_"+ ws.getId_enclave() +" = new google.maps.InfoWindow({\n" + 
						"    content: contentString_"+ ws.getId_enclave() +",\n" + 
						"    maxWidth: 350\n" + 
						"  });\n" + 
						"var myLatlng = new google.maps.LatLng(" + String.valueOf(ws.getLocation().getLatitude()) + "," + String.valueOf(ws.getLocation().getLongitude()) +");\n" +
						"var marker_"+ ws.getId_enclave() +" = new google.maps.Marker({\n" +
						"    position: myLatlng,\n" +
						"    map: map,\n" +
						"    title: '"+  ws.getEnclave_name() +"'\n" +
						"  });\n" + 
						
						"marker_"+ ws.getId_enclave() +".addListener('click', function() {\n" +
						"    infowindow_"+ ws.getId_enclave() +".open(map, marker_"+ ws.getId_enclave() +");\n" + 
						"});");
			}
		}
	}

	public void setComunidades(Hashtable<String,String> comunidades_table) {
		local_comunidades_name_table = comunidades_table;
		comboBox.addItem("Todo");
		
		for(String value: comunidades_table.keySet()) {
			comboBox.addItem(comunidades_table.get(value));
		}
		
		local_comunidades_name_table.put("all", "Todo");
		view.setEnabled(true);
		frmInformacinEstacionesAemet.setEnabled(true);
	}


	class ShowWaitAction extends AbstractAction {
		   protected Object obj;
		   
		   public ShowWaitAction(String name, Object obj) {
		      super(name);
		      this.obj = obj;
		   }
	
		   @Override
		   public void actionPerformed(ActionEvent evt) {
		      SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>(){
		         @Override
		         protected Void doInBackground() throws Exception {
					browser.loadHTML(html); 	
		        	aemetAgent.BuscarInformacionEstaciones(seleccionCombo());
		            return null;
		         }
		      };
	
		      Window win = SwingUtilities.getWindowAncestor((AbstractButton)evt.getSource());
		      dialog = new JDialog(win, "Recopilando datos", ModalityType.APPLICATION_MODAL);
	
		      mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {
	
		         @Override
		         public void propertyChange(PropertyChangeEvent evt) {
		            if (evt.getPropertyName().equals("state")) {
		               if (evt.getNewValue() == SwingWorker.StateValue.DONE) {

		               }
		            }
		         }
		      });
		      mySwingWorker.execute();
	
		      JProgressBar progressBar = new JProgressBar();
		      progressBar.setIndeterminate(true);
		      JPanel panel = new JPanel(new BorderLayout());
		      panel.add(progressBar, BorderLayout.CENTER);
		      panel.add(new JLabel("Recopilando datos ..."), BorderLayout.PAGE_START);
		      dialog.getContentPane().add(panel);
		      dialog.pack();
		      dialog.setLocationRelativeTo(win);
		      dialog.setVisible(true);
		   }
	}


	public void mostrarMensaje(String mensaje, boolean exito) {
		if (exito) {
			dialog.setTitle(mensaje);
		}
		else {
			JOptionPane.showMessageDialog(null, mensaje, "No se pudo obrtener la informacióin", JOptionPane.ERROR_MESSAGE);
			browser.loadHTML(html);
			dialog.dispose();
		}
		
	}
}