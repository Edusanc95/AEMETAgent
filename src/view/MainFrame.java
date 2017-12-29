package view;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import domain.AemetAgent;
import domain.WeatherStation;
import jade.util.leap.LinkedList;

import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.ComponentOrientation;
import javax.swing.DefaultComboBoxModel;
import java.awt.Dimension;

public class MainFrame {
	private Hashtable<String, LinkedList> comunidades_table;

	public static final int MIN_ZOOM = 0;
	public static final int MAX_ZOOM = 21;

	/**
	 * In map.html file default zoom value is set to 4.
	 */
	private static int zoomValue = 4;
	private JButton setMarkerButton;
	private JButton zoomOutButton;
	private JButton zoomInButton;
	private BrowserView view;
	private JFrame frmInformacinEstacionesAemet;
	private JPanel toolBar;
	private JLabel lblZona;
	private JComboBox comboBox;
	private JSeparator separator;

	public MainFrame(AemetAgent aemetAgent) {
		comunidades_table = new Hashtable<String,LinkedList>();

		final Browser browser = new Browser();
		view = new BrowserView(browser);
		view.setEnabled(false);

		zoomInButton = new JButton("Zoom In");
		zoomInButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (zoomValue < MAX_ZOOM) {
					browser.executeJavaScript("map.setZoom(" + ++zoomValue + ")");
				}
			}
		});

		zoomOutButton = new JButton("Zoom Out");
		zoomOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (zoomValue > MIN_ZOOM) {
					browser.executeJavaScript("map.setZoom(" + --zoomValue + ")");
				}
			}
		});

		toolBar = new JPanel();
		
		lblZona = new JLabel("Zona: ");
		toolBar.add(lblZona);
		
		comboBox = new JComboBox();
		comboBox.setMinimumSize(new Dimension(70, 24));
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Todo"}));
		comboBox.setEditable(true);
		comboBox.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		toolBar.add(comboBox);
		
				setMarkerButton = new JButton("Set Marker");
				setMarkerButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						LinkedList marcadores = new LinkedList();
						marcadores = generarMarcas(comunidades_table);
						String[] campos = new String[4];

						for(int i= 0; i < marcadores.size();i++) {
							campos = (String[]) marcadores.get(i);
							browser.executeJavaScript(
									"var myLatlng = new google.maps.LatLng(" + campos[1] + "," + campos[2] +");\n" +
											"var marker = new google.maps.Marker({\n" +
											"    position: myLatlng,\n" +
											"    map: map,\n" +
											"    title: '"+ campos[0] +"'\n" +
									"});");
						}
					}
				});
				toolBar.add(setMarkerButton);
		
		separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator);
		toolBar.add(zoomInButton);
		toolBar.add(zoomOutButton);

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
		String html = null;
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
	
	public void setInfo(Hashtable<String, LinkedList> info_table) {
		comunidades_table = info_table; 
		view.setEnabled(true);
		frmInformacinEstacionesAemet.setEnabled(true);
		
	}

	private LinkedList generarMarcas(Hashtable<String, LinkedList> comunidades_table) {
		// TODO Auto-generated method stub
		LinkedList marcas = new LinkedList();
		long cont = 0L;
		for(String comunidad_key : comunidades_table.keySet()) {
			LinkedList stations_list = new LinkedList();
			stations_list = comunidades_table.get(comunidad_key);

			for(int i=0; i<stations_list.size(); i++) {
				WeatherStation ws = new WeatherStation();
				ws = (WeatherStation)stations_list.get(i);

				String[] campos = new String[4];
				campos[0] = ws.getEnclave_name();
				campos[1] = String.valueOf(ws.getLocation().getLatitude());
				campos[2] = String.valueOf(ws.getLocation().getLongitude());
				campos[3] = String.valueOf(cont);
				marcas.add(campos);
				cont++;
			}	
		}
		return marcas;
	}
}