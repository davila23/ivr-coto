package clasesivr;

import java.util.HashMap;
import java.util.Map;

import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

public class Menu {
	private String audioOpcion;
	private Map<String, String> opciones = new HashMap<String, String>();
	private long segsTimeOut = 3;
	private Integer maxLargo = 1;
	private Integer maxIntentos = 3;

	public String getAudioOpcion() {
		return audioOpcion;
	}

	public void setAudioOpcion(String audioOpcion) {
		this.audioOpcion = audioOpcion;
	}

	public Map<String, String> getOpciones() {
		return opciones;
	}

	public void agregarOpcion(String opcion, String descript) {
		this.opciones.put(opcion, descript);
	}

	public long getSegsTimeOut() {
		return segsTimeOut;
	}

	public void setSegsTimeOut(long segsTimeOut) {
		this.segsTimeOut = segsTimeOut;
	}

	public Integer getMaxLargo() {
		return maxLargo;
	}

	public void setMaxLargo(Integer maxLargo) {
		this.maxLargo = maxLargo;
	}

	public Integer getMaxIntentos() {
		return maxIntentos;
	}

	public void setMaxIntentos(Integer maxIntentos) {
		this.maxIntentos = maxIntentos;
	}

	public String ejecutarMenu(AgiRequest aRequest, AgiChannel aChannel) {
		int trys = 0;
		String opcionElegida = "-1";
		while (trys < this.maxIntentos
				&& !this.opciones.containsKey(opcionElegida)) {
			try {
				aChannel.verbose("Ingrese Opcion", 1);
				opcionElegida = aChannel.getData(this.audioOpcion,
						this.getSegsTimeOut() * 1000L, this.maxLargo);
			} catch (AgiException e) {
				e.printStackTrace();
			}

			trys++;
		}

		return opcionElegida;

	}
}
