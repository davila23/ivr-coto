package clasesivr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lanzador.Ivrlanzador;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;

import utils.txtlog;

public class IvrRutinaPin extends BaseAgiScript {

	private String dniIngresado;
	private String fechaIngresada;
	private int gIntentosTarj = 0;
	private int gIntentosDNI = 0;
	private int gIntentosFecha = 0;

	public void service(AgiRequest aRequest, AgiChannel aChannel) {

		ClaveDni clDni;
		try {
			aChannel.exec("SetLanguage", "es");
		} catch (AgiException e1) {
			Logger.getLogger(Ivrlanzador.class.getName()).log(Level.WARN, null,
					e1);
		}

		Menu menuInicial = new Menu();
		menuInicial.agregarOpcion("1", "Si desea continuar");
		menuInicial.agregarOpcion("2", "Menu anterior");
		menuInicial.setAudioOpcion("RUTINAPIN/RUTINA_PIN001");
		menuInicial.setMaxIntentos(2);
		menuInicial.setMaxLargo(1);
		menuInicial.setSegsTimeOut(5);
		String resultado = menuInicial.ejecutarMenu(aRequest, aChannel);
		try {
			switch (Integer.parseInt(resultado)) {
			case 1:
				txtlog.txtLog("IvrRutinaPin", "PASOINICIAL", "1",
						aRequest.getUniqueId());
				if (menuValidarDNI(aRequest, aChannel)) {
					if (menuValidarFecha(aRequest, aChannel)) {
						clDni = new ClaveDni(dniIngresado);
						String respAGI = validarPINJpos(aRequest, aChannel,
								clDni);
						retornoJPOS(aRequest, aChannel, respAGI, clDni);
					} else {
						cortaLlamadaConMsg(aRequest, aChannel,
								"RUTINAPIN/RUTINA_PIN020");

					}
				} else {
					cortaLlamadaConMsg(aRequest, aChannel,
							"RUTINAPIN/RUTINA_PIN020");
				}
				break;
			case 2:
				txtlog.txtLog("IvrRutinaPin", "PASOINICIAL", "2",
						aRequest.getUniqueId());
				break;
			default:
				txtlog.txtLog("IvrRutinaPin", "PASOINICIAL", "otro",
						aRequest.getUniqueId());
				break;
			}
		} catch (NumberFormatException e) {
			txtlog.txtLog("IvrRutinaPin", "PASOINICIAL", "corta_con_error: "
					+ e.getMessage(), aRequest.getUniqueId());
			cortaLlamadaConMsg(aRequest, aChannel, "");
		}
	}

	private boolean menuValidarDNI(AgiRequest aRequest, AgiChannel aChannel) {
		boolean retval = false;
		try {
			int intentos = 1;
			String dniEsCorrecto = "0";
			dniIngresado = "";
			while (intentos <= 3 && !dniEsCorrecto.equals("1")) {
				aChannel.verbose("Ingrese DNI", 1);
				dniIngresado = aChannel.getData("RUTINAPIN/RUTINA_PIN002",
						3000L, 8); // ingrese_dni
				if (validarDNI(dniIngresado)) {
					aChannel.verbose("Su DNI es", 1);
					aChannel.streamFile("RUTINAPIN/RUTINA_PIN003");// dni_ingresado_es
					aChannel.sayDigits(dniIngresado);
					txtlog.txtLog("IvrRutinaPin", "INGRESODNI", dniIngresado
							+ "|intento=" + intentos, aRequest.getUniqueId());
					aChannel.verbose("DNI correcto ingrese 1", 1);
					dniEsCorrecto = aChannel.getData("RUTINAPIN/RUTINA_PIN010",
							2000L, 1);// si_es_correcto_presione_1
					if (dniEsCorrecto.equals("1") && validarDNI(dniIngresado)) {
						retval = true;
						break;
					} else {
						intentos++;
					}
				} else {
					intentos++;
					aChannel.streamFile("RUTINAPIN/RUTINA_PIN011");
				}
			}
			if (intentos == 4) {
				txtlog.txtLog("IvrRutinaPin", "INGRESODNI",
						"maximo_de_intentos", aRequest.getUniqueId());
				aChannel.streamFile("RUTINAPIN/RUTINA_PIN012");
				cortaLlamadaConMsg(aRequest, aChannel,
						"RUTINAPIN/RUTINA_PIN020");
				retval = false;
			}
		} catch (AgiException e) {
			Logger.getLogger(Ivrlanzador.class.getName()).log(Level.WARN, null,
					e);
		}
		txtlog.txtLog("IvrRutinaPin", "INGRESODNI", "salida=" + retval,
				aRequest.getUniqueId());
		return retval;
	}

	private boolean validarDNI(String DNI) {

		boolean res = false;

		Pattern r = Pattern.compile("^[0-9]{5,8}$");
		Matcher m = r.matcher(DNI.trim());
		if (m.find()) {
			res = true;
		} else {
			res = false;
		}

		return res;
	}

	private boolean validarFecha(String Fecha) {
		boolean res = false;

		Pattern r = Pattern.compile("^[0-9]{6}$");
		Matcher m = r.matcher(Fecha.trim());
		if (m.find()) {
			res = true;
		} else {
			res = false;
		}

		return res;
	}

	private boolean menuValidarFecha(AgiRequest aRequest, AgiChannel aChannel) {
		boolean retval = false;
		String day = "0";
		String month = "0";
		String year = "0";
		try {
			int intentos = 1;
			String fechaEsCorrecta = "0";
			String lfechaIngresada = "";
			while (intentos <= 3 && !fechaEsCorrecta.equals("1")) {
				aChannel.verbose("Ingrese Fecha Nac", 1);
				lfechaIngresada = aChannel.getData("RUTINAPIN/RUTINA_PIN005",
						3000L, 6); // ingrese_fecha
				if (validarFecha(lfechaIngresada)) {

					day = lfechaIngresada.substring(0, 2);
					month = lfechaIngresada.substring(2, 4);
					year = lfechaIngresada.substring(4, 6);

					aChannel.verbose("Usted ingreso", 1);
					aChannel.streamFile("RUTINAPIN/RUTINA_PIN006");// fecha_ingresado_es

					aChannel.verbose("Dia", 1);
					aChannel.streamFile("RUTINAPIN/RUTINA_PIN007");// dia
					aChannel.sayNumber(day);

					aChannel.verbose("Mes", 1);
					aChannel.streamFile("RUTINAPIN/RUTINA_PIN008");// mes
					aChannel.sayNumber(month);

					aChannel.verbose("Anio", 1);
					aChannel.streamFile("RUTINAPIN/RUTINA_PIN009");// anio
					aChannel.sayNumber(year);

					aChannel.verbose("Fecha correcta ingrese 1", 1);
					fechaEsCorrecta = aChannel.getData(
							"RUTINAPIN/RUTINA_PIN010", 2000L, 1);// si_la_fecha_es_correcta_
					txtlog.txtLog("IvrRutinaPin", "INGRESOFECHA",
							lfechaIngresada + "|intento=" + intentos,
							aRequest.getUniqueId());
					if (fechaEsCorrecta.equals("1")
							&& esFechaValida(day, month, year)) {
						retval = true;
						break;
					} else {
						fechaEsCorrecta = "0";
						intentos++;
						aChannel.streamFile("RUTINAPIN/RUTINA_PIN013");
					}
				} else {
					intentos++;
					aChannel.streamFile("RUTINAPIN/RUTINA_PIN013");

				}
			}
			if (intentos == 4) {
				txtlog.txtLog("IvrRutinaPin", "INGRESOFECHA",
						"maximo_de_intentos", aRequest.getUniqueId());
				aChannel.streamFile("RUTINAPIN/RUTINA_PIN012");
				cortaLlamadaConMsg(aRequest, aChannel,
						"RUTINAPIN/RUTINA_PIN020");
				retval = false;
			}

		} catch (AgiException e) {
			Logger.getLogger(Ivrlanzador.class.getName()).log(Level.WARN, null,
					e);
		}
		if (retval) {
			fechaIngresada = "00" + year.trim() + month.trim() + day.trim();
		}
		txtlog.txtLog("IvrRutinaPin", "INGRESOFECHA", "salida=" + retval,
				aRequest.getUniqueId());
		return retval;
	}

	private boolean esFechaValida(String day, String month, String year) {
		boolean retval = false;
		int anio = 0;
		int mes = 0;
		int dia = 0;
		try {
			anio = Integer.parseInt(year);
			mes = Integer.parseInt(month);
			dia = Integer.parseInt(day);
		} catch (NumberFormatException e) {
			Logger.getLogger(Ivrlanzador.class.getName()).log(Level.WARN, null,
					e);
		}

		if (dia <= (getcantidadDiasDelMes(anio, mes)) && dia != 0) {
			retval = true;
		} else
			retval = false;

		return retval;
	}

	private int getcantidadDiasDelMes(int anio, int mes) {
		int diasMes = 0;

		switch (mes) {

		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			diasMes = 31;
			break;

		case 4:
		case 6:
		case 9:
		case 11:
			diasMes = 30;
			break;

		case 2:
			if (esBisiesto(anio)) {
				diasMes = 29;
			} else
				diasMes = 28;
			break;

		default:
		}

		return diasMes;

	}

	private boolean esBisiesto(int anio) {
		return ((((anio % 4) == 0) && ((anio % 100) != 0)) || ((anio % 400) == 0));
	}

	@SuppressWarnings("static-access")
	private String validarPINJpos(AgiRequest aRequest, AgiChannel aChannel,
			ClaveDni clDni) {
		String respagi = "";

		try {
			String trama = "4";// código operacion
			String numtarj = aChannel.getVariable("numtarj");
			trama += numtarj;
			trama += clDni.getDni();
			trama += fechaIngresada;
			trama += String.format("%07d", 0);// para generar 7 ceros
			trama += clDni.getClaveRandom();
			trama += String.format("%04d", 0);
			trama += "T"; // preguntar que se envia
			trama += String.format("%51s", "");
			String ast_uid = String.format("%030d", 0)
					+ aChannel.getUniqueId().replaceAll("\\.", "");
			trama += ast_uid.substring(ast_uid.length() - 29);
			trama += "0";
			txtlog.txtLog("IvrRutinaPin", "VALIDACIONJPOS", "tramaEnviada="
					+ trama, aRequest.getUniqueId());
			aChannel.setVariable("TRAMAPIN", trama);
			aChannel.verbose("Trama a Enviar :" + trama, 1);
			aChannel.exec("agi", "cbl/cbl_rutinapin.agi");
			int intentosLecturaAgi = 1;
			respagi = aChannel.getVariable("AGITRAMA");
			while ((respagi == null || respagi.isEmpty())
					&& intentosLecturaAgi <= 15) {
				aChannel.verbose("Esperando variable AGITRAMA:" + trama, 1);
				respagi = aChannel.getVariable("AGITRAMA");
				Thread.currentThread().sleep(10L);
				intentosLecturaAgi++;

			}

			if (intentosLecturaAgi == 15)
				respagi = "98";

			if (respagi != null && respagi.length() > 50)
				respagi = respagi.substring(48, 50);

			aChannel.verbose("Respuesta de JPOS :" + respagi, 1);
		} catch (AgiException e) {
			System.out.println(e.getMessage());
			Logger.getLogger(Ivrlanzador.class.getName()).log(Level.WARN, null,
					e);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Logger.getLogger(Ivrlanzador.class.getName()).log(Level.WARN, null,
					e);
		}
		txtlog.txtLog("IvrRutinaPin", "VALIDACIONJPOS", "respuestasJPOS="
				+ respagi, aRequest.getUniqueId());
		return respagi;

	}

	private void retornoJPOS(AgiRequest aRequest, AgiChannel aChannel,
			String respAgi, ClaveDni clDni) {
		Integer opcion = 85;
		try {

			opcion = Integer.parseInt(respAgi);
		} catch (NumberFormatException e) {
			txtlog.txtLog("IvrRutinaPin", "RETORNOJPOS",
					"respuestasJPOSnoNumerico=" + e.getMessage(),
					aRequest.getUniqueId());
		}
		boolean resp96 = false;
		switch (opcion) {
		case 0:
			informarPin(aRequest, aChannel, clDni);
			break;
		case 2:
			try {
				aChannel.streamFile("RUTINAPIN/RUTINA_PIN011");
			} catch (AgiException e) {
				Logger.getLogger(Ivrlanzador.class.getName()).log(Level.WARN,
						null, e);
			}
			pideDNIReenvia(aRequest, aChannel);
			break;
		case 3:
			try {
				aChannel.streamFile("RUTINAPIN/RUTINA_PIN015");
			} catch (AgiException e) {
				Logger.getLogger(Ivrlanzador.class.getName()).log(Level.WARN,
						null, e);
			}
			pideFechaReenvia(aRequest, aChannel);
			break;
		case 83:
		case 84:
			imposibleGestionarPin(aRequest, aChannel);
			break;
		case 98:
			servicioNoDisponible(aRequest, aChannel);
			break;
		case 96:

			resp96 = true;
			try {
				aChannel.streamFile("RUTINAPIN/RUTINA_PIN016");
			} catch (AgiException e) {
				Logger.getLogger(Ivrlanzador.class.getName()).log(Level.WARN,
						null, e);
			}
		case 99:
			if (!resp96) {
				try {
					aChannel.streamFile("RUTINAPIN/RUTINA_PIN024");
				} catch (AgiException e) {
					Logger.getLogger(Ivrlanzador.class.getName()).log(
							Level.WARN, null, e);
				}
			}
			pideTarjetaReenvia(aRequest, aChannel, clDni);
			break;
		default:
		case 85:
			derivaLlamada(aRequest, aChannel, "salir|talleres|operador", "");
			break;
		}

	}

	private void pideFechaReenvia(AgiRequest aRequest, AgiChannel aChannel) {
		txtlog.txtLog("IvrRutinaPin", "FECHAINVALIDAJPOS", "intento="
				+ gIntentosFecha, aRequest.getUniqueId());
		if (gIntentosFecha < 2) {
			if (menuValidarFecha(aRequest, aChannel)) {
				ClaveDni clDni = new ClaveDni(dniIngresado);
				String respAGI = validarPINJpos(aRequest, aChannel, clDni);

				gIntentosFecha++;
				retornoJPOS(aRequest, aChannel, respAGI, clDni);
			}
		} else {
			derivaLlamada(aRequest, aChannel, "salir|talleres|operador", "");
		}
	}

	private void pideDNIReenvia(AgiRequest aRequest, AgiChannel aChannel) {
		txtlog.txtLog("IvrRutinaPin", "TARJETAINVALIDAJPOS", "intento="
				+ gIntentosDNI, aRequest.getUniqueId());
		if (gIntentosDNI < 2) {
			if (menuValidarDNI(aRequest, aChannel)) {
				ClaveDni clDni = new ClaveDni(dniIngresado);
				String respAGI = validarPINJpos(aRequest, aChannel, clDni);

				gIntentosDNI++;
				retornoJPOS(aRequest, aChannel, respAGI, clDni);
			}
		} else {
			derivaLlamada(aRequest, aChannel, "salir|talleres|operador", "");

		}
	}

	private void pideTarjetaReenvia(AgiRequest aRequest, AgiChannel aChannel,
			ClaveDni clDni) {
		txtlog.txtLog("IvrRutinaPin", "TARJETAINVALIDAJPOS", "intento="
				+ gIntentosTarj, aRequest.getUniqueId());
		try {
			int lIntentos = 1;
			String tarjetaIngresada = "";
			while (lIntentos <= 2) {
				aChannel.verbose("Ingrese Tarjeta", 1);
				tarjetaIngresada = aChannel.getData("RUTINAPIN/RUTINA_PIN025",
						4000L, 16);
				if (tarjetaIngresada.length() == 16) {
					break;
				} else {
					lIntentos++;
				}
			}

			if (gIntentosTarj < 1) {
				String respAGI = validarPINJpos(aRequest, aChannel, clDni);
				gIntentosTarj++;
				retornoJPOS(aRequest, aChannel, respAGI, clDni);
			} else {
				cortaLlamadaConMsg(aRequest, aChannel,
						"RUTINAPIN/RUTINA_PIN020");
			}

		} catch (AgiException e) {
			Logger.getLogger(Ivrlanzador.class.getName()).log(Level.WARN, null,
					e);
		}

	}

	private void imposibleGestionarPin(AgiRequest aRequest, AgiChannel aChannel) {
		txtlog.txtLog("IvrRutinaPin", "IMPOSIBLEGESTIONAPIN", "",
				aRequest.getUniqueId());
		try {
			aChannel.streamFile("RUTINAPIN/RUTINA_PIN022");
		} catch (AgiException e) {
			Logger.getLogger(Ivrlanzador.class.getName()).log(Level.WARN, null,
					e);
		}
		cortaLlamadaConMsg(aRequest, aChannel, "RUTINAPIN/RUTINA_PIN020");
	}

	private void servicioNoDisponible(AgiRequest aRequest, AgiChannel aChannel) {
		txtlog.txtLog("IvrRutinaPin", "SERVICIONODISPONIBLE", "",
				aRequest.getUniqueId());
		try {
			aChannel.streamFile("RUTINAPIN/RUTINA_PIN021");
		} catch (AgiException e) {
			Logger.getLogger(Ivrlanzador.class.getName()).log(Level.WARN, null,
					e);
		}
		cortaLlamadaConMsg(aRequest, aChannel, "RUTINAPIN/RUTINA_PIN020");
	}

	private void informarPin(AgiRequest aRequest, AgiChannel aChannel,
			ClaveDni clDni) {
		txtlog.txtLog("IvrRutinaPin", "INFORMOPIN", "", aRequest.getUniqueId());
		String opcionElegida = "1";
		try {
			while (opcionElegida.equals("1")) {

				aChannel.verbose("Su Pin es", 1);
				aChannel.streamFile("RUTINAPIN/RUTINA_PIN017");
				aChannel.sayDigits(clDni.getClaveDni());
				aChannel.streamFile("RUTINAPIN/RUTINA_PIN018");
				opcionElegida = aChannel.getData("RUTINAPIN/RUTINA_PIN014",
						5000L, 1);

			}
		} catch (AgiException e) {
			Logger.getLogger(Ivrlanzador.class.getName()).log(Level.WARN, null,
					e);
		}
		cortaLlamadaConMsg(aRequest, aChannel, "RUTINAPIN/RUTINA_PIN020");

	}

	private void cortaLlamadaConMsg(AgiRequest aRequest, AgiChannel aChannel,
			String audio) {
		txtlog.txtLog("IvrRutinaPin", "CORTALLAMADA", audio,
				aRequest.getUniqueId());
		try {
			if (!audio.isEmpty())
				aChannel.streamFile(audio);

			aChannel.hangup();
		} catch (AgiException e) {
			Logger.getLogger(Ivrlanzador.class.getName()).log(Level.WARN, null,
					e);
		}

	}

	private void derivaLlamada(AgiRequest aRequest, AgiChannel aChannel,
			String destinoDerivo, String audio) {
		txtlog.txtLog("IvrRutinaPin", "DERIVALLAMADA", destinoDerivo,
				aRequest.getUniqueId());
		try {
			if (!audio.isEmpty())
				aChannel.streamFile(audio);

			aChannel.exec("Goto", destinoDerivo);
		} catch (AgiException e) {
			Logger.getLogger(Ivrlanzador.class.getName()).log(Level.WARN, null,
					e);
		}

	}

}
