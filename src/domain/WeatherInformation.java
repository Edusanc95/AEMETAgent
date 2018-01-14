/**
 * 
 */
package domain;

import java.io.Serializable;

/**
 * @author Sergio Fernandez Garcia
 * @author Eduardo Sanchez Lopez
 *
 */

public class WeatherInformation implements Serializable {
	
	private String fecha_hora;
	private float temperatura_c;
	private float velocidad_viento; // En km/h
	private String dir_viento;
	private String dir_viento_img;
	private float racha; //En km/h
	private String dir_racha;
	private String dir_racha_img;
	private float precipitacion; //En mm
	private float presion; //En hPa
	private float tendencia; // En hPa
	private float humedad; // En %
	
	public WeatherInformation() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param fecha
	 * @param temperatura_c
	 * @param velocidad_viento
	 * @param dir_viento
	 * @param dir_viento_img
	 * @param racha
	 * @param dir_racha
	 * @param dir_racha_img
	 * @param precipitacion
	 * @param presion
	 * @param tendencia
	 * @param humedad
	 */
	public WeatherInformation(String fecha_hora, float temperatura_c, float velocidad_viento, String dir_viento,
			String dir_viento_img, float racha, String dir_racha, String dir_racha_img, float precipitacion,
			float presion, float tendencia, float humedad) {
		super();
		this.fecha_hora = fecha_hora;
		this.temperatura_c = temperatura_c;
		this.velocidad_viento = velocidad_viento;
		this.dir_viento = dir_viento;
		this.dir_viento_img = dir_viento_img;
		this.racha = racha;
		this.dir_racha = dir_racha;
		this.dir_racha_img = dir_racha_img;
		this.precipitacion = precipitacion;
		this.presion = presion;
		this.tendencia = tendencia;
		this.humedad = humedad;
	}


	/**
	 * @return the fecha_hora
	 */
	public String getFecha_hora() {
		
		return (fecha_hora != null) ? fecha_hora : "N/D";
	}

	/**
	 * @param fecha_hora the fecha_hora to set
	 */
	public void setFecha_hora(String fecha_hora) {
		this.fecha_hora = fecha_hora;
	}


	/**
	 * @return the temperatura_c
	 */
	public float getTemperatura_c() {
		return temperatura_c;
	}


	/**
	 * @param temperatura_c the temperatura_c to set
	 */
	public void setTemperatura_c(Float temperatura_c) {
		this.temperatura_c = temperatura_c;
	}


	/**
	 * @return the velocidad_viento
	 */
	public float getVelocidad_viento() {
		return velocidad_viento;
	}


	/**
	 * @param velocidad_viento the velocidad_viento to set
	 */
	public void setVelocidad_viento(float velocidad_viento) {
		this.velocidad_viento = velocidad_viento;
	}


	/**
	 * @return the dir_viento
	 */
	public String getDir_viento() {
		return (dir_viento  != null) ? dir_viento : "N/D";
	}


	/**
	 * @param dir_viento the dir_viento to set
	 */
	public void setDir_viento(String dir_viento) {
		this.dir_viento = dir_viento;
	}


	/**
	 * @return the dir_viento_img
	 */
	public String getDir_viento_img() {
		return (dir_viento_img  != null) ? dir_viento_img : "";
	}


	/**
	 * @param dir_viento_ing the dir_viento_ing to set
	 */
	public void setDir_viento_img(String dir_viento_img) {
		this.dir_viento_img = dir_viento_img;
	}


	/**
	 * @return the racha
	 */
	public float getRacha() {
		return racha;
	}


	/**
	 * @param racha the racha to set
	 */
	public void setRacha(float racha) {
		this.racha = racha;
	}


	/**
	 * @return the dir_racha
	 */
	public String getDir_racha() {
		return (dir_racha != null) ? dir_racha : "N/D";
	}


	/**
	 * @param dir_racha the dir_racha to set
	 */
	public void setDir_racha(String dir_racha) {
		this.dir_racha = dir_racha;
	}


	/**
	 * @return the dir_racha_img
	 */
	public String getDir_racha_img() {
		return (dir_racha_img != null) ? dir_racha_img : "N/D";
	}


	/**
	 * @param dir_racha_img the dir_racha_img to set
	 */
	public void setDir_racha_img(String dir_racha_img) {
		this.dir_racha_img = dir_racha_img;
	}


	/**
	 * @return the precipitacion
	 */
	public float getPrecipitacion() {
		return precipitacion;
	}


	/**
	 * @param precipitacion the precipitacion to set
	 */
	public void setPrecipitacion(float precipitacion) {
		this.precipitacion = precipitacion;
	}


	/**
	 * @return the presion
	 */
	public float getPresion() {
		return presion;
	}


	/**
	 * @param presion the presion to set
	 */
	public void setPresion(float presion) {
		this.presion = presion;
	}


	/**
	 * @return the tendencia
	 */
	public float getTendencia() {
		return tendencia;
	}


	/**
	 * @param tendencia the tendencia to set
	 */
	public void setTendencia(float tendencia) {
		this.tendencia = tendencia;
	}


	/**
	 * @return the humedad
	 */
	public float getHumedad() {
		return humedad;
	}


	/**
	 * @param humedad the humedad to set
	 */
	public void setHumedad(float humedad) {
		this.humedad = humedad;
	}
		
}
