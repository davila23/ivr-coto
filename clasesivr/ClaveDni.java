package clasesivr;

import java.util.Random;

public class ClaveDni {
	private String dni = "";
	private String claveRandom = "";
	private String claveDni = "";

	public ClaveDni(String tmpdni) {
		dni = String.format("%08d", Integer.parseInt(tmpdni));
		this.generaClave();
	}

	private void generaClave() {

		Random rnd = new Random();
		for (int i = 1; i < 5; i++) {
			int pos = 0;
			while (pos == 0) {
				pos = rnd.nextInt(8) + 1;
			}
			claveRandom += pos;
			claveDni += dni.substring(pos - 1, pos);
		}
	}

	public String getDni() {
		return dni;
	}

	public String getClaveRandom() {
		return claveRandom;
	}

	public String getClaveDni() {
		return claveDni;
	}

}
