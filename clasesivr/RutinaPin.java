package clasesivr;

import ivr.CallContext;
import ivr.CallFlow;
import ivr.IvrExceptionHandler;
import java.util.UUID;
import main.Daemon;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;
import step.StepAnswer;
import step.StepConditional;
import step.StepCounter;
import step.StepEnd;
import step.StepExecute;
import step.StepFactory;
import step.StepGenerateKeyFromDni;
import step.StepGetAsteriskVariable;
import step.StepMenu;
import step.StepSayDigits;
import step.StepFactory.StepType;
import step.StepPlay;
import step.StepPlayRead;
import step.StepSendJPOS;
import step.StepSetVariable;
import step.StepSwitch;
import step.group.PideDni;
import step.group.PideFecha;
import step.group.PideTarjeta;
import step.group.StepGroupFactory;
import workflow.Handler;
import workflow.Task;
import condition.condition;
import context.ContextVar;

public class RutinaPin extends BaseAgiScript {

	private long idContextVar = 1;
	CallContext ctx;
	CallFlow cf;
	private ContextVar resultadoAudioInicio;
	private ContextVar dniContextVar;
	private ContextVar confirmaDniContextVar;
	private ContextVar intentosDniContextVar;
	private ContextVar fechaContextVar;
	private ContextVar diaContextVar;
	private ContextVar mesContextVar;
	private ContextVar anioContextVar;
	private ContextVar confirmaFechaContextVar;
	private ContextVar intentosFechaContextVar;
	private ContextVar tarjetaContexVar;
	private ContextVar confirmaTarjetaContextVar;
	private ContextVar intentosTarjetaContextVar;
	private ContextVar tipoMensajeJposPin;
	private ContextVar repetirPINContextVar;
	private ContextVar ejecutoJPOSContextVar;
	private ContextVar cambiaEjecutoJPOSContextVar;
	private ContextVar intentosIngresoContextVar;
	private StepEnd pasoFinal;
	private StepAnswer inicial;
	private StepPlayRead stepAudioInicio;
	private StepPlay stepAudioFinal;
	private StepPlay stepAudioFinal1;
	private StepPlay stepAudioFinal2;
	private PideDni pideDniGrp;
	private PideFecha pideFechaGrp;
	private StepMenu stepMenuConfirmacionIngresoRutina;
	private StepSwitch evalRetJPOS;
	private StepCounter contadorIntentosIngresoRutina;
	private StepConditional evalContadorIngresoRutina;
	private StepSendJPOS enviaTramaJpos;
	private int intentos = 3;
	private ContextVar retornoJPOS;
	private PideTarjeta pideTarjetaGrp;
	private StepSayDigits stepAudioDecirPIN;
	private StepPlay stepAudioSuPIN;
	private StepPlay stepAudioFechaIncorrecta;
	private StepPlay stepAudioDerivoAsesor;
	private StepPlay stepAudioTarjetaNoVigente;
	private StepPlay stepAudioNroTarjIncorrecto;
	private StepPlay stepAudioNoEsPosibleGestPIN;
	private StepPlay stepAudioServNoDisponible;
	private StepPlay stepAudioDniIncorrecto;
	private StepPlay stepAudioVerificarDatos;
	private StepMenu stepMenuRepetirPIN;
	private StepPlayRead stepAudioRepetirPIN;
	private StepGetAsteriskVariable obtieneTarjeta;
	private StepGetAsteriskVariable obtieneDni;
	private StepGenerateKeyFromDni clavePIN;
	private ContextVar clavePINContextVar;
	private ContextVar clavePINRandomContextVar;
	private ContextVar intentosRepetirPinContextVar;
	private StepCounter contadorIntentosRepetirPIN;
	private StepConditional evalIntentosRepetirPIN;
	private StepExecute stepVolverAlMenu;
	private StepExecute stepDerivoLlamada;
	private StepConditional evalSiEjecutoJPOS;
	private StepSetVariable cambiaEjecutoJPOS;
	private StepCounter contadorIntentosFechaJPOS;
	private StepConditional evalContadorFechaJPOS;
	private StepConditional evalContadorDNIJPOS;
	private StepCounter contadorIntentosDNIJPOS;
	private StepConditional evalContadorTarjetaJPOS;
	private StepCounter contadorIntentosTarjetaJPOS;
	private StepConditional evalAudioFinal;
	private ContextVar nroCuentaContexVar;
	private ContextVar componenteContexVar;
	private ContextVar fillerContexVar;
	private ContextVar idLlamadaContexVar;
	private ContextVar whisperContextVar;
	private ContextVar envioServerJposPrecargadasContexVar;
	private ContextVar fillerClavePINContextVar;
	private StepPlay stepAudioPinEntregado;
	private StepPlay stepAudioFinalCorreo;
	private ContextVar dnisContextVar;
	private StepGetAsteriskVariable obtieneDNIS;

	private void initialize(AgiRequest request, AgiChannel channel) {
		cf = new CallFlow();
		ctx = new CallContext();
		Handler manejoErrores = new IvrExceptionHandler();
		manejoErrores.setId(UUID.randomUUID());
		cf.addTask(manejoErrores);
		ctx.setChannel(channel);
		ctx.setRequest(request);
	}

	/* -------------------------- Defino Secuencia -------------------------- */

	private void setSequence() {

		/* --- Atiendo y obtengo Nro Tarjeta--- */

		inicial.setNextstep(obtieneDNIS.GetId());

		obtieneDNIS.setNextstep(obtieneTarjeta.GetId());

		if (Daemon.getConfig("PIDEDNI").equals("1")) {
			obtieneTarjeta.setNextstep(stepAudioInicio.GetId());
			stepMenuConfirmacionIngresoRutina.addSteps("1",
					pideDniGrp.getInitialStep());
		} else {
			obtieneTarjeta.setNextstep(obtieneDni.GetId());
			obtieneDni.setNextstep(stepAudioInicio.GetId());
			stepMenuConfirmacionIngresoRutina.addSteps("1",
					evalSiEjecutoJPOS.GetId());
		}

		/* --- Secuencia de Ingreso a la Rutina--- */

		stepAudioInicio.setNextstep(stepMenuConfirmacionIngresoRutina.GetId());

		stepMenuConfirmacionIngresoRutina.addSteps("2",
				stepVolverAlMenu.GetId());
		// stepMenuConfirmacionIngresoRutina.addSteps("3",
		// stepAudioDerivoAsesor.GetId());
		stepMenuConfirmacionIngresoRutina
				.setInvalidOption(contadorIntentosIngresoRutina.GetId());

		contadorIntentosIngresoRutina.setNextstep(evalContadorIngresoRutina
				.GetId());

		evalContadorIngresoRutina.addCondition(new condition(1, "#{"
				+ intentosIngresoContextVar.getVarName() + "} < " + intentos,
				stepAudioInicio.GetId(), evalAudioFinal.GetId()));

		/* --- Secuencia de Ingreso Datos--- */

		pideDniGrp.setStepIfTrue(evalSiEjecutoJPOS.GetId());
		pideDniGrp.setStepIfFalse(stepAudioVerificarDatos.GetId());

		evalSiEjecutoJPOS.addCondition(new condition(1, "#{"
				+ ejecutoJPOSContextVar.getVarName() + "} == 1", clavePIN
				.GetId(), pideFechaGrp.getInitialStep()));

		pideFechaGrp.setStepIfTrue(clavePIN.GetId());
		pideFechaGrp.setStepIfFalse(stepAudioVerificarDatos.GetId());

		pideTarjetaGrp.setStepIfTrue(clavePIN.GetId());
		pideTarjetaGrp.setStepIfFalse(evalAudioFinal.GetId());

		clavePIN.setNextstep(enviaTramaJpos.GetId());

		enviaTramaJpos.setNextstep(cambiaEjecutoJPOS.GetId());

		cambiaEjecutoJPOS.setNextstep(evalRetJPOS.GetId());

		evalRetJPOS.setNextstep(stepAudioServNoDisponible.GetId());

		/* --- Secuencia de Informacion PIN --- */

		stepAudioDecirPIN.setNextstep(stepAudioPinEntregado.GetId());
		stepAudioPinEntregado.setNextstep(contadorIntentosRepetirPIN.GetId());
		stepAudioRepetirPIN.setNextstep(stepMenuRepetirPIN.GetId());

		stepMenuRepetirPIN.addSteps("1", stepAudioSuPIN.GetId());
		stepMenuRepetirPIN.addSteps("9", evalAudioFinal.GetId());
		stepMenuRepetirPIN.setInvalidOption(contadorIntentosRepetirPIN.GetId());

		contadorIntentosRepetirPIN.setNextstep(evalIntentosRepetirPIN.GetId());

		evalIntentosRepetirPIN.addCondition((new condition(1,
				"#{" + intentosRepetirPinContextVar.getVarName() + "} < "
						+ intentos, stepAudioRepetirPIN.GetId(), evalAudioFinal
						.GetId())));

		/* --- Cont y Eval . Cod = 02, 03 , 96 y 99 --- */

		contadorIntentosFechaJPOS.setNextstep(evalContadorFechaJPOS.GetId());
		evalContadorFechaJPOS.addCondition(new condition(1, "#{"
				+ intentosFechaContextVar.getVarName() + "} < " + intentos,
				pideFechaGrp.getInitialStep(), stepAudioDerivoAsesor.GetId()));

		contadorIntentosDNIJPOS.setNextstep(evalContadorDNIJPOS.GetId());
		evalContadorDNIJPOS.addCondition(new condition(1, "#{"
				+ intentosDniContextVar.getVarName() + "} < " + intentos,
				pideDniGrp.getInitialStep(), stepAudioDerivoAsesor.GetId()));

		contadorIntentosTarjetaJPOS
				.setNextstep(evalContadorTarjetaJPOS.GetId());
		evalContadorTarjetaJPOS.addCondition(new condition(1, "#{"
				+ intentosTarjetaContextVar.getVarName() + "} < " + intentos,
				pideTarjetaGrp.getInitialStep(), evalAudioFinal.GetId()));

		evalAudioFinal.addCondition(new condition(1, "substring('#{"
				+ tarjetaContexVar.getVarName() + "}',0,8) == '60426063'",
				stepAudioFinal2.GetId(), stepAudioFinal.GetId()));

		evalAudioFinal.addCondition(new condition(2, "substring('#{"
				+ tarjetaContexVar.getVarName() + "}',0,8) == '60420971'",
				stepAudioFinal1.GetId(), stepAudioFinal.GetId()));

		evalAudioFinal.addCondition(new condition(3, "substring('#{"
				+ tarjetaContexVar.getVarName() + "}',0,8) == '60420972'",
				stepAudioFinal1.GetId(), stepAudioFinal.GetId()));

		evalAudioFinal.addCondition(new condition(4, "substring('#{"
				+ tarjetaContexVar.getVarName() + "}',0,8) == '60420963'",
				stepAudioFinal1.GetId(), stepAudioFinal.GetId()));

		evalAudioFinal.addCondition(new condition(5, "substring('#{"
				+ tarjetaContexVar.getVarName() + "}',0,8) == '60420964'",
				stepAudioFinal1.GetId(), stepAudioFinal.GetId()));

		evalAudioFinal.addCondition(new condition(6, "substring('#{"
				+ tarjetaContexVar.getVarName() + "}',0,8) == '60420965'",
				stepAudioFinal1.GetId(), stepAudioFinal.GetId()));

		evalAudioFinal.addCondition(new condition(7, "substring('#{"
				+ tarjetaContexVar.getVarName() + "}',0,8) == '60420966'",
				stepAudioFinal1.GetId(), stepAudioFinal.GetId()));

		evalAudioFinal.addCondition(new condition(8, "substring('#{"
				+ tarjetaContexVar.getVarName() + "}',0,8) == '60420967'",
				stepAudioFinal1.GetId(), stepAudioFinal.GetId()));

		evalAudioFinal.addCondition(new condition(9, "substring('#{"
				+ tarjetaContexVar.getVarName() + "}',0,8) == '60420968'",
				stepAudioFinal1.GetId(), stepAudioFinal.GetId()));

		evalAudioFinal.addCondition(new condition(10, "substring('#{"
				+ tarjetaContexVar.getVarName() + "}',0,8) == '60420969'",
				stepAudioFinal1.GetId(), stepAudioFinal.GetId()));

		evalAudioFinal.addCondition(new condition(11, "#{"
				+ dnisContextVar.getVarName() + "} == " + "2582",
				stepAudioFinalCorreo.GetId(), stepAudioFinal.GetId()));

		/* --- Fin de llamada --- */

		stepAudioFinal.setNextstep(pasoFinal.GetId());
		stepAudioFinal1.setNextstep(pasoFinal.GetId());
		stepAudioFinal2.setNextstep(pasoFinal.GetId());
		stepAudioFinalCorreo.setNextstep(pasoFinal.GetId());
		stepAudioDerivoAsesor.setNextstep(stepDerivoLlamada.GetId());
	}

	@Override
	public void service(AgiRequest request, AgiChannel channel) {
		Daemon.getDbLog().addCallFlowToLog(channel.getUniqueId(),
				RutinaPin.class.getName(), request.getCallerIdNumber());
		this.initialize(request, channel);
		this.createContextVars(channel);
		this.createSteps();
		this.setSequence();

		for (Task tmpTask : pideDniGrp.getSteps().values()) {
			cf.addTask(tmpTask);
		}

		for (Task tmpTask : pideFechaGrp.getSteps().values()) {
			cf.addTask(tmpTask);
		}
		for (Task tmpTask : pideTarjetaGrp.getSteps().values()) {
			cf.addTask(tmpTask);
		}

		ctx.setInitialStep(inicial.GetId());

		try {
			cf.execute(ctx);
		} catch (Exception ex) {
			Logger.getLogger(TestIvr.class.getName())
					.log(Level.FATAL, null, ex);
		}
	}

	/* -------------------------- Creo Steps -------------------------- */

	private void createSteps() {

		/* --- Inicio|Fin --- */

		inicial = (StepAnswer) StepFactory.createStep(StepType.Answer,
				UUID.randomUUID());
		cf.addTask(inicial);

		stepAudioInicio = (StepPlayRead) StepFactory.createStep(
				StepType.PlayRead, UUID.randomUUID());
		stepAudioInicio.setStepDescription("PLAYREAD => INICIO RUTINA");
		stepAudioInicio.setPlayFile("RUTINAPIN/RUTINA_PIN001");
		stepAudioInicio.setPlayMaxDigits(1);
		stepAudioInicio.setContextVariableName(resultadoAudioInicio);
		stepAudioInicio.setPlayTimeout(5000L);
		cf.addTask(stepAudioInicio);

		evalAudioFinal = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalAudioFinal.setStepDescription("CONDITIONAL => AUDIO FINAL");
		cf.addTask(evalAudioFinal);

		pasoFinal = (StepEnd) StepFactory.createStep(StepType.End,
				UUID.randomUUID());
		pasoFinal.setStepDescription("END => FIN DE COMUNICACION");
		cf.addTask(pasoFinal);

		stepAudioFinal = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioFinal.setStepDescription("PLAY => SALUDO CABAL RESPONDE");
		stepAudioFinal.setPlayfile("RUTINAPIN/RUTINA_PIN027");
		cf.addTask(stepAudioFinal);

		stepAudioFinal1 = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioFinal1
				.setStepDescription("PLAY => SALUDO GOBIERNO DE LA CIUDAD DE BUENOS AIRES");
		stepAudioFinal1.setPlayfile("RUTINAPIN/RUTINA_PIN020");
		cf.addTask(stepAudioFinal1);

		stepAudioFinal2 = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioFinal2.setStepDescription("PLAY => SALUDO RIO NEGRO");
		stepAudioFinal2.setPlayfile("RUTINAPIN/RUTINA_PIN028");
		cf.addTask(stepAudioFinal2);

		stepAudioFinalCorreo = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioFinalCorreo
				.setStepDescription("PLAY => SALUDO CORREO ARGENTINO");
		stepAudioFinalCorreo.setPlayfile("nosocial/despedidacorreo");
		cf.addTask(stepAudioFinalCorreo);

		/* --- Menu Ingreso --- */

		stepVolverAlMenu = (StepExecute) StepFactory.createStep(
				StepType.Execute, UUID.randomUUID());
		stepVolverAlMenu.setApp("goto");
		stepVolverAlMenu.setAppOptions(Daemon.getConfig("DERIVOMENUPRINCIPAL"));
		stepVolverAlMenu.setStepDescription("EXECUTE => DERIVO MENU PRINCIPAL");
		cf.addTask(stepVolverAlMenu);

		stepMenuConfirmacionIngresoRutina = (StepMenu) StepFactory.createStep(
				StepType.Menu, UUID.randomUUID());
		stepMenuConfirmacionIngresoRutina
				.setStepDescription("MENU => INGRESO RUTINA");
		stepMenuConfirmacionIngresoRutina
				.setContextVariableName(resultadoAudioInicio);
		cf.addTask(stepMenuConfirmacionIngresoRutina);

		/* --- Contador Ingreso Rutina --- */

		contadorIntentosIngresoRutina = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosIngresoRutina
				.setStepDescription("COUNTER => INGRESO RUTINA");
		contadorIntentosIngresoRutina
				.setContextVariableName(intentosIngresoContextVar);
		cf.addTask(contadorIntentosIngresoRutina);

		/* --- Evaluador Ingreso Rutina --- */

		evalContadorIngresoRutina = (StepConditional) StepFactory.createStep(
				StepType.Conditional, UUID.randomUUID());
		evalContadorIngresoRutina
				.setStepDescription("CONDITIONAL => INGRESO RUTINA");
		cf.addTask(evalContadorIngresoRutina);

		/* --- Set DNI --- */

		pideDniGrp = (PideDni) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.pideDni);
		pideDniGrp.setAudioDni("RUTINAPIN/RUTINA_PIN002");
		pideDniGrp.setAudioValidateDni("RUTINAPIN/RUTINA_PIN010");
		pideDniGrp.setAudioDniInvalido("RUTINAPIN/RUTINA_PIN011");
		pideDniGrp.setAudioSuDniEs("RUTINAPIN/RUTINA_PIN003");
		pideDniGrp.setDniContextVar(dniContextVar);
		pideDniGrp.setConfirmaDniContextVar(confirmaDniContextVar);
		pideDniGrp.setIntentosDniContextVar(intentosDniContextVar);

		/*--- Generar Clave --- */

		clavePIN = (StepGenerateKeyFromDni) StepFactory.createStep(
				StepType.GenerateKeyFromDni, UUID.randomUUID());
		clavePIN.setContextVariableClaveDni(clavePINContextVar);
		clavePIN.setContextVariableClaveRandom(clavePINRandomContextVar);
		clavePIN.setContextVariableDni(dniContextVar);
		clavePIN.setStepDescription("GENERATEKEYFROMDNI => GENERA PIN");
		cf.addTask(clavePIN);

		/* --- Set Fecha --- */

		pideFechaGrp = (PideFecha) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.pideFecha);
		pideFechaGrp.setAudioFecha("RUTINAPIN/RUTINA_PIN005");
		pideFechaGrp.setAudioValidateFecha("RUTINAPIN/RUTINA_PIN010");
		pideFechaGrp.setAudioFechaInvalida("RUTINAPIN/RUTINA_PIN013");
		pideFechaGrp.setAudioSuFechaEs("RUTINAPIN/RUTINA_PIN006");
		pideFechaGrp.setAudioAnio("RUTINAPIN/RUTINA_PIN009");
		pideFechaGrp.setAudioMes("RUTINAPIN/RUTINA_PIN008");
		pideFechaGrp.setAudioDia("RUTINAPIN/RUTINA_PIN007");
		pideFechaGrp.setfechaContextVar(fechaContextVar);
		pideFechaGrp.setContextVarDia(diaContextVar);
		pideFechaGrp.setContextVarMes(mesContextVar);
		pideFechaGrp.setContextVarAnio(anioContextVar);
		pideFechaGrp.setConfirmaFechaContextVar(confirmaFechaContextVar);
		pideFechaGrp.setIntentosFechaContextVar(intentosFechaContextVar);

		/* --- JPOS --- */

		enviaTramaJpos = (StepSendJPOS) StepFactory.createStep(
				StepType.SendJPOS, UUID.randomUUID());
		enviaTramaJpos
				.setContextVariableTipoMensaje(envioServerJposPrecargadasContexVar);
		enviaTramaJpos.setContextVariableName(retornoJPOS);
		enviaTramaJpos.addformatoVariables(0, tipoMensajeJposPin);
		enviaTramaJpos.addformatoVariables(1, tarjetaContexVar);
		enviaTramaJpos.addformatoVariables(2, dniContextVar);
		enviaTramaJpos.addformatoVariables(3, anioContextVar);
		enviaTramaJpos.addformatoVariables(4, mesContextVar);
		enviaTramaJpos.addformatoVariables(5, diaContextVar);
		enviaTramaJpos.addformatoVariables(6, nroCuentaContexVar);
		enviaTramaJpos.addformatoVariables(7, clavePINRandomContextVar);
		enviaTramaJpos.addformatoVariables(8, fillerClavePINContextVar);
		enviaTramaJpos.addformatoVariables(9, componenteContexVar);
		enviaTramaJpos.addformatoVariables(10, fillerContexVar);
		enviaTramaJpos.addformatoVariables(11, idLlamadaContexVar);
		enviaTramaJpos.addformatoVariables(12, whisperContextVar);
		enviaTramaJpos.setStepDescription("SENDJPOS => ENVIA TRAMA JPOS");
		cf.addTask(enviaTramaJpos);

		evalRetJPOS = (StepSwitch) StepFactory.createStep(StepType.Switch,
				UUID.randomUUID());
		evalRetJPOS.setContextVariableName(retornoJPOS);
		evalRetJPOS.setStepDescription("CONDITIONAL => CODIGO DE RETORNO JPOS");
		cf.addTask(evalRetJPOS);

		evalSiEjecutoJPOS = (StepConditional) StepFactory.createStep(
				StepType.Conditional, UUID.randomUUID());
		evalSiEjecutoJPOS.setStepDescription("CONDITIONAL => EJECUTO JPOS");
		cf.addTask(evalSiEjecutoJPOS);

		contadorIntentosRepetirPIN = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosRepetirPIN
				.setStepDescription("COUNTER => INTENTOS REPETIR PIN");
		contadorIntentosRepetirPIN
				.setContextVariableName(intentosRepetirPinContextVar);
		cf.addTask(contadorIntentosRepetirPIN);

		evalIntentosRepetirPIN = (StepConditional) StepFactory.createStep(
				StepType.Conditional, UUID.randomUUID());
		evalIntentosRepetirPIN
				.setStepDescription("CONDITIONAL => INTENTOS REPETIR PIN");
		cf.addTask(evalIntentosRepetirPIN);

		cambiaEjecutoJPOS = (StepSetVariable) StepFactory.createStep(
				StepType.SetVariable, UUID.randomUUID());
		cambiaEjecutoJPOS.setContextVariableOrigen(cambiaEjecutoJPOSContextVar);
		cambiaEjecutoJPOS.setContextVariableDestino(ejecutoJPOSContextVar);
		cambiaEjecutoJPOS
				.setStepDescription("SETVARIABLE => CAMBIA A 1 SI EJECUTO JPOS");
		cf.addTask(cambiaEjecutoJPOS);

		evalContadorDNIJPOS = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorDNIJPOS
				.setStepDescription("CONDITIONAL => INTENTOS DNI CONTRA JPOS");
		cf.addTask(evalContadorDNIJPOS);

		contadorIntentosDNIJPOS = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosDNIJPOS.setContextVariableName(intentosDniContextVar);
		contadorIntentosDNIJPOS
				.setStepDescription("COUNTER => INTENTOS DNI CONTRA JPOS");
		cf.addTask(contadorIntentosDNIJPOS);

		evalContadorFechaJPOS = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorFechaJPOS
				.setStepDescription("CONDITIONAL => INTENTOS FECHA CONTRA JPOS");
		cf.addTask(evalContadorFechaJPOS);

		contadorIntentosFechaJPOS = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosFechaJPOS
				.setContextVariableName(intentosFechaContextVar);
		contadorIntentosFechaJPOS
				.setStepDescription("COUNTER => INTENTOS FECHA CONTRA JPOS");
		cf.addTask(contadorIntentosFechaJPOS);

		evalContadorTarjetaJPOS = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorTarjetaJPOS
				.setStepDescription("CONDITIONAL => INTENTOS TARJETA CONTRA JPOS");
		cf.addTask(evalContadorTarjetaJPOS);

		contadorIntentosTarjetaJPOS = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosTarjetaJPOS
				.setContextVariableName(intentosTarjetaContextVar);
		contadorIntentosTarjetaJPOS
				.setStepDescription("COUNTER => INTENTOS TARJETA CONTRA JPOS");
		cf.addTask(contadorIntentosTarjetaJPOS);

		/* --- Set Tarjeta --- */

		pideTarjetaGrp = (PideTarjeta) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.pideTarjeta);
		pideTarjetaGrp.setAudioTarjeta("RUTINAPIN/RUTINA_PIN025");
		pideTarjetaGrp.setAudioSuTarjetaEs("RUTINAPIN/RUTINA_PIN026");
		pideTarjetaGrp.setAudioTarjetaInvalido("RUTINAPIN/RUTINA_PIN016");
		pideTarjetaGrp.setConfirmaTarjetaContextVar(confirmaTarjetaContextVar);
		pideTarjetaGrp.setIntentosTarjetaContextVar(intentosTarjetaContextVar);
		pideTarjetaGrp.setTarjetaContextVar(tarjetaContexVar);
		pideTarjetaGrp.setStepIfFalse(evalAudioFinal.GetId());
		pideTarjetaGrp.setStepIfTrue(enviaTramaJpos.GetId());

		obtieneTarjeta = (StepGetAsteriskVariable) StepFactory.createStep(
				StepType.GetAsteriskVariable, UUID.randomUUID());
		obtieneTarjeta.setContextVariableName(tarjetaContexVar);
		if (Daemon.getConfig("PIDEDNI").equals("1")) {
			obtieneTarjeta.setVariableName("numtarj");
		} else {
			obtieneTarjeta.setVariableName("plastico");
		}
		obtieneTarjeta
				.setStepDescription("GETASTERISKVARIABLE => OBTIENE TARJETA");
		cf.addTask(obtieneTarjeta);

		obtieneDni = (StepGetAsteriskVariable) StepFactory.createStep(
				StepType.GetAsteriskVariable, UUID.randomUUID());
		obtieneDni.setContextVariableName(dniContextVar);
		obtieneDni.setVariableName("dni");
		obtieneDni.setStepDescription("GETASTERISKVARIABLE => OBTIENE DNI");
		cf.addTask(obtieneDni);

		obtieneDNIS = (StepGetAsteriskVariable) StepFactory.createStep(
				StepType.GetAsteriskVariable, UUID.randomUUID());
		obtieneDNIS.setContextVariableName(dnisContextVar);
		obtieneDNIS.setVariableName("DNIS");
		obtieneDNIS.setStepDescription("GETASTERISKVARIABLE => OBTIENE DNIS");
		cf.addTask(obtieneDNIS);

		/* --- Set Audios Informa PIN --- */

		stepMenuRepetirPIN = (StepMenu) StepFactory.createStep(StepType.Menu,
				UUID.randomUUID());
		stepMenuRepetirPIN.setContextVariableName(repetirPINContextVar);
		stepMenuRepetirPIN.setStepDescription("MENU => REPETIR PIN");
		cf.addTask(stepMenuRepetirPIN);

		stepAudioDecirPIN = (StepSayDigits) StepFactory.createStep(
				StepType.SayDigits, UUID.randomUUID());
		stepAudioDecirPIN.setContextVariableName(clavePINContextVar);
		stepAudioDecirPIN.setStepDescription("SAYDIGITS => DICE PIN");
		cf.addTask(stepAudioDecirPIN);

		stepAudioRepetirPIN = (StepPlayRead) StepFactory.createStep(
				StepType.PlayRead, UUID.randomUUID());
		stepAudioRepetirPIN.setPlayFile("RUTINAPIN/RUTINA_PIN014");
		stepAudioRepetirPIN.setContextVariableName(repetirPINContextVar);
		stepAudioRepetirPIN.setStepDescription("PLAYREAD => REPETIR PIN ");
		cf.addTask(stepAudioRepetirPIN);

		stepAudioVerificarDatos = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioVerificarDatos.setPlayfile("RUTINAPIN/RUTINA_PIN012");
		stepAudioVerificarDatos
				.setStepDescription("PLAY => VERIFIQUE DATOS Y VUELVA A LLAMAR");
		cf.addTask(stepAudioVerificarDatos);

		/* --- Derivo LLamada --- */

		stepDerivoLlamada = (StepExecute) StepFactory.createStep(
				StepType.Execute, UUID.randomUUID());
		stepDerivoLlamada.setApp("goto");
		stepDerivoLlamada.setAppOptions(Daemon.getConfig("DERIVOOPERADOR"));
		stepDerivoLlamada.setStepDescription("EXECUTE => DERIVO ASESOR");
		cf.addTask(stepDerivoLlamada);

		/* --- Set Audios Retorno Jpos --- */

		stepAudioSuPIN = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioSuPIN.setPlayfile("RUTINAPIN/RUTINA_PIN017");
		stepAudioSuPIN.setStepDescription("PLAY => DICE PIN. COD : 00 ");
		cf.addTask(stepAudioSuPIN);

		stepAudioDniIncorrecto = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioDniIncorrecto.setPlayfile("RUTINAPIN/RUTINA_PIN011");
		stepAudioDniIncorrecto
				.setStepDescription("PLAY => DNI INCORRECTO . COD : 02");
		cf.addTask(stepAudioDniIncorrecto);

		stepAudioFechaIncorrecta = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioFechaIncorrecta.setPlayfile("RUTINAPIN/RUTINA_PIN013");
		stepAudioFechaIncorrecta
				.setStepDescription("PLAY => FECHA INCORRECTA . COD : 03");
		cf.addTask(stepAudioFechaIncorrecta);

		stepAudioNoEsPosibleGestPIN = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioNoEsPosibleGestPIN.setPlayfile("RUTINAPIN/RUTINA_PIN022");
		stepAudioNoEsPosibleGestPIN
				.setStepDescription("PLAY => NO SE PUEDE GESTIONAR EL PIN . COD : 83 / 84");
		cf.addTask(stepAudioNoEsPosibleGestPIN);

		stepAudioDerivoAsesor = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioDerivoAsesor.setPlayfile("RUTINAPIN/RUTINA_PIN023");
		stepAudioDerivoAsesor
				.setStepDescription("PLAY => DERIVO ASESOR . COD : 85");
		cf.addTask(stepAudioDerivoAsesor);

		stepAudioNroTarjIncorrecto = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioNroTarjIncorrecto.setPlayfile("RUTINAPIN/RUTINA_PIN016");
		stepAudioNroTarjIncorrecto
				.setStepDescription("PLAY => TARJETA INCORRECTA . COD : 96");
		cf.addTask(stepAudioNroTarjIncorrecto);

		stepAudioTarjetaNoVigente = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioTarjetaNoVigente.setPlayfile("RUTINAPIN/RUTINA_PIN024");
		stepAudioTarjetaNoVigente
				.setStepDescription("PLAY => TARJETA VENCIDA. COD : 99");
		cf.addTask(stepAudioTarjetaNoVigente);

		stepAudioServNoDisponible = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioServNoDisponible.setPlayfile("RUTINAPIN/RUTINA_PIN021");
		stepAudioServNoDisponible
				.setStepDescription("PLAY => SERVICIO NO DISPONIBLE. COD : 98");
		cf.addTask(stepAudioServNoDisponible);

		stepAudioPinEntregado = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioPinEntregado.setPlayfile("RUTINAPIN/RUTINA_PIN018");
		stepAudioPinEntregado.setStepDescription("PLAY => PIN ENTREGADO");
		cf.addTask(stepAudioPinEntregado);

		this.evalRetJPOS();
	}

	private void evalRetJPOS() {

		/*-------------------------------------------------------------------------------
		 * ret =>  00    ||   "Informa PIN"    
		 * ret =>  02    ||   "Nro de Documento erróneo"    
		 * ret =>  03    ||   "Fecha de nacimiento errónea"   
		 * ret =>  83    ||   "No es posible emitir PIN"   
		 * ret =>  84    ||   "No es posible emitir PIN"   
		 * ret =>  85    ||   "Falta PIN"   
		 * ret =>  96    ||   "Tarjeta inexistente"   
		 * ret =>  98    ||   "Error mensaje"   
		 * ret =>  99    ||   "Causa dif a 00 o Tj Vencida"   
		-------------------------------------------------------------------------------	*/

		evalRetJPOS.addSwitchValue("00", stepAudioSuPIN.GetId());
		evalRetJPOS.addSwitchValue("02", stepAudioDniIncorrecto.GetId());
		evalRetJPOS.addSwitchValue("03", stepAudioFechaIncorrecta.GetId());

		evalRetJPOS.addSwitchValue("83", stepAudioNoEsPosibleGestPIN.GetId());
		evalRetJPOS.addSwitchValue("84", stepAudioNoEsPosibleGestPIN.GetId());
		evalRetJPOS.addSwitchValue("85", stepAudioDerivoAsesor.GetId());

		evalRetJPOS.addSwitchValue("EE", stepAudioServNoDisponible.GetId());
		evalRetJPOS.addSwitchValue("98", stepAudioServNoDisponible.GetId());

		evalRetJPOS.addSwitchValue("96", stepAudioNroTarjIncorrecto.GetId());
		evalRetJPOS.addSwitchValue("99", stepAudioTarjetaNoVigente.GetId());

	}

	/* ---------------- Inicializo las Variables De Contexto -------------- */

	@SuppressWarnings("unchecked")
	private ContextVar getContextVar(String descrip, String initialValue,
			String astUid) {
		ContextVar tmpCtxVar = new ContextVar(ctx);
		tmpCtxVar.setId(this.idContextVar++);
		tmpCtxVar.setVarDescrip(descrip);
		tmpCtxVar.setAstUid(astUid);
		tmpCtxVar.setVarValue(initialValue);
		ctx.put(tmpCtxVar.getId(), tmpCtxVar);
		return tmpCtxVar;
	}

	@SuppressWarnings("unused")
	private void createContextVars(AgiChannel channel) {
		/* --- Inicio --- */

		String AstUid = channel.getUniqueId();

		resultadoAudioInicio = this.getContextVar("resultadoAudioInicio", "",
				AstUid);

		dniContextVar = this.getContextVar("dniContextVar", "", AstUid);

		dniContextVar.setStringFormat("%08d");

		confirmaDniContextVar = this.getContextVar("confirmaDniContextVar", "",
				AstUid);

		intentosDniContextVar = this.getContextVar("intentosDniContextVar",
				"0", AstUid);

		fechaContextVar = this.getContextVar("fechaContextVar", "", AstUid);

		diaContextVar = this.getContextVar("diaContextVar", "", AstUid);

		mesContextVar = this.getContextVar("mesContextVar", "", AstUid);

		anioContextVar = this.getContextVar("anioContextVar", "", AstUid);

		confirmaFechaContextVar = this.getContextVar("confirmaFechaContextVar",
				"", AstUid);

		intentosFechaContextVar = this.getContextVar("intentosFechaContextVar",
				"0", AstUid);

		intentosIngresoContextVar = this.getContextVar(
				"intentosIngresoContextVar", "0", AstUid);

		tarjetaContexVar = this.getContextVar("tarjetaContexVar", "", AstUid);

		nroCuentaContexVar = this.getContextVar("nroCuentaContexVar",
				String.format("%07d", 0), AstUid);

		componenteContexVar = this.getContextVar("componenteContexVar", " ",
				AstUid);

		fillerContexVar = this.getContextVar("fillerContexVar", " ", AstUid);

		fillerContexVar.setStringFormat("%51s");

		String ast_uid = "";

		if (AstUid.contains("-")) {
			ast_uid = AstUid.split("-")[1];
		} else {
			ast_uid = AstUid;
		}

		ast_uid = String.format("%030d", 0) + ast_uid.replaceAll("\\.", "");

		idLlamadaContexVar = this.getContextVar("idLlamadaContexVar",
				ast_uid.substring(ast_uid.length() - 29), AstUid);

		confirmaTarjetaContextVar = this.getContextVar(
				"confirmaTarjetaContextVar", "", AstUid);

		whisperContextVar = this
				.getContextVar("whisperContextVar", "0", AstUid);

		intentosTarjetaContextVar = this.getContextVar(
				"intentosTarjetaContextVar", "0", AstUid);

		tipoMensajeJposPin = this.getContextVar("tipoMensajeJposPin", "4",
				AstUid);

		tipoMensajeJposPin.setStringFormat("%01d");

		retornoJPOS = this.getContextVar("retornoJPOS", "98", AstUid);

		repetirPINContextVar = this.getContextVar("repetirPINContextVar", "",
				AstUid);

		clavePINContextVar = this.getContextVar("clavePINContextVar", "",
				AstUid);

		clavePINContextVar.setStringFormat("%04d");

		clavePINRandomContextVar = this.getContextVar(
				"clavePINRandomContextVar", String.format("%04d", 0), AstUid);

		fillerClavePINContextVar = this.getContextVar(
				"fillerClavePINContextVar", String.format("%04d", 0), AstUid);

		intentosRepetirPinContextVar = this.getContextVar(
				"intentosRepetirPinContextVar", "0", AstUid);

		cambiaEjecutoJPOSContextVar = this.getContextVar(
				"cambiaEjecutoJPOSContextVar", "1", AstUid);

		ejecutoJPOSContextVar = this.getContextVar("ejecutoJPOSContextVar",
				"0", AstUid);

		envioServerJposPrecargadasContexVar = this.getContextVar(
				"envioServerJposPrecargadasContexVar", "precargada", AstUid);

		dnisContextVar = this.getContextVar("dnisContextVar", "", AstUid);
	}
}
