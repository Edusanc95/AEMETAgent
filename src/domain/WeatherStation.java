package domain;

import java.io.Serializable;

public class WeatherStation implements Serializable{

	private String id_comnunidad_autonoma;
	private String comunidad_autonoma;
	private String provincia;
	private String id_enclave;
	private String enclave_name;
	private Location location;
	
	public WeatherStation() {
		
	}
	/**
	 * @param id_comnunidad_autonoma
	 * @param comunidad_autonoma
	 * @param provincia
	 * @param id_enclave
	 * @param enclave_name
	 * @param location
	 */
	public WeatherStation(String id_comnunidad_autonoma, String comunidad_autonoma, String provincia, String id_enclave,
			String enclave_name, Location location) {
		super();
		this.id_comnunidad_autonoma = id_comnunidad_autonoma;
		this.comunidad_autonoma = comunidad_autonoma;
		this.provincia = provincia;
		this.id_enclave = id_enclave;
		this.enclave_name = enclave_name;
		this.location = location;
	}


	// --- Getters and Setters ---

	/**
	 * @return the id_comnunidad_autonoma
	 */
	public String getId_comnunidad_autonoma() {
		return id_comnunidad_autonoma;
	}

	/**
	 * @param id_comnunidad_autonoma the id_comnunidad_autonoma to set
	 */
	public void setId_comnunidad_autonoma(String id_comnunidad_autonoma) {
		this.id_comnunidad_autonoma = id_comnunidad_autonoma;
	}


	
	/**
	 * @return the comunidad_autonoma
	 */
	public String getComunidad_autonoma() {
		return comunidad_autonoma;
	}

	/**
	 * @param comunidad_autonoma the comunidad_autonoma to set
	 */
	public void setComunidad_autonoma(String comunidad_autonoma) {
		this.comunidad_autonoma = comunidad_autonoma;
	}



	/**
	 * @return the provincia
	 */
	public String getProvincia() {
		return provincia;
	}

	/**
	 * @param provincia the provincia to set
	 */
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}



	/**
	 * @return the id_enclave
	 */
	public String getId_enclave() {
		return id_enclave;
	}

	/**
	 * @param id_enclave the id_enclave to set
	 */
	public void setId_enclave(String id_enclave) {
		this.id_enclave = id_enclave;
	}



	/**
	 * @return the enclave_name
	 */
	public String getEnclave_name() {
		return enclave_name;
	}

	/**
	 * @param enclave_name the enclave_name to set
	 */
	public void setEnclave_name(String enclave_name) {
		this.enclave_name = enclave_name;
	}



	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WeatherStation [id_comnunidad_autonoma=" + id_comnunidad_autonoma + ", comunidad_autonoma="
				+ comunidad_autonoma + ", provincia=" + provincia + ", id_enclave=" + id_enclave + ", enclave_name="
				+ enclave_name + ", location=" + location + "]";
	}
	 
}
