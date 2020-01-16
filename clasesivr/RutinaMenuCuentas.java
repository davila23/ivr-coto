package clasesivr;

import ivr.CallContext;
import ivr.CallFlow;
import ivr.IvrExceptionHandler;

import java.beans.FeatureDescriptor;
import java.util.HashMap;
import java.util.UUID;

import main.Daemon;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;

import step.Step;
import step.StepAnswer;
import step.StepConditional;
import step.StepContinueOnDialPlan;
import step.StepCounter;
import step.StepEnd;
import step.StepExecute;
import step.StepFactory;
import step.StepGenerateKeyFromDni;
import step.StepGetAsteriskVariable;
import step.StepMenu;
import step.StepSayDigits;
import step.StepSendA380Message;
import step.StepSetAsteriskVariable;
import step.StepFactory.StepType;
import step.StepPlay;
import step.StepPlayRead;
import step.StepSendJPOS;
import step.StepSetVariable;
import step.StepSwitch;
import step.group.GeneracionDeClave;
import step.group.IngresoDeClave;
import step.group.MenuCuentas;
import step.group.PideDni;
import step.group.PideFecha;
import step.group.PideTarjeta;
import step.group.StepGroupFactory;
import workflow.Handler;
import workflow.Task;
import condition.condition;
import context.ContextVar;

public class RutinaMenuCuentas extends BaseAgiScript {

	private long idContextVar = 1;
	CallContext ctx;
	CallFlow cf;
	private StepEnd pasoFinal;
	private int intentos = 3;
	private StepContinueOnDialPlan continuaDialPlan;
	private StepExecute stepDerivoAlMenuPrincipal;
	private StepPlay stepAudioVerificarDatos;
	private StepGetAsteriskVariable stepGetIdCrecer;
	private StepGetAsteriskVariable stepSetSoapFdn;
	private StepPlay stepAudioFinal;
	private ContextVar fechaDeVencimientoClaveContextVar;
	private ContextVar intentosClaveContextVar;
	private ContextVar claveContextVar;
	private ContextVar tarjetaContexVar;
	private ContextVar confirmaTarjetaContextVar;
	private ContextVar intentosTarjetaContextVar;
	private ContextVar dniContextVar;
	private ContextVar confirmaDniContextVar;
	private ContextVar intentosDniContextVar;
	private ContextVar fechaContextVar;
	private ContextVar confirmaFechaContextVar;
	private ContextVar intentosFechaContextVar;
	private ContextVar intentosIngresoContextVar;
	private ContextVar intentosMenuInicialContextVar;
	private ContextVar resultadoAudioInicio;
	private ContextVar scapeDigitContextVar;
	private ContextVar menuIngresoContextVar;
	private ContextVar menuFinalContextVar;
	private ContextVar contextVarDia;
	private ContextVar contextVarAnio;
	private ContextVar contextVarMes;
	private ContextVar fdnContexVar;
	private ContextVar intentosPrimerTarjetaContextVar;
	private ContextVar claveCorrectaContextVar;
	private ContextVar cambiarClaveContextVar;
	private ContextVar idCrecerContextVar;
	private ContextVar idCrecerAuxContextVar;
	private ContextVar genClaveContextVar;
	private ContextVar retAuthPasswordcontextVar;
	private ContextVar cantDiasExpiracioncontextVar;
	private ContextVar estadoDelUsuarioContextVar;
	private ContextVar retAutentificacionContextVar;
	private ContextVar retCambioClaveContextVar;
	private ContextVar soapFdnContexVar;
	private ContextVar confirmacionClaveContextVar;
	private StepSetAsteriskVariable salidaValida;
	private MenuCuentas menuCuentas;
	private ContextVar tieneCuentasPredeterminadasContextVar;
	private StepSendA380Message enviaMensaje;
	private ContextVar cuentasPredeterminadasContextVar;
	private ContextVar cuentasContextVar;
	private ContextVar ultimosTresDigitosCuentaContextVar;
	private ContextVar saldoCuentasContextVar;
	private ContextVar tipoDeCuentaContextVar;
	private ContextVar predeterminarOtraCuentaContextVar;
	private StepGetAsteriskVariable obtieneSoapDni;
	private ContextVar repetirPredeterminadasContextVar;
	private ContextVar stepMenuQuierePredeterminar;
	private ContextVar quierePredeterminarContextVar;
	private ContextVar saldosCuentasContextVar;
	private ContextVar cuentaContextVar;
	private ContextVar sucursalContextVar;
	private ContextVar cuentaADesvincularContextVar;
	private ContextVar cuentaAVincularContextVar;
	private ContextVar statusPredeterminadaContexVar;
	private ContextVar esCuentaPredeterminadaContextVar;
	private ContextVar vinculoCuentaContextVar;
	private ContextVar desvinculoCuentaContextVar;
	private ContextVar tipoDeCuentaAAgregarContextVar;
	private ContextVar tipoDeCuentaABorrarContextVar;
	private ContextVar cantidadDeCuentasEncontradasContextVar;
	private ContextVar predeterminarCuentasContextVar;
	private ContextVar intentosCuentaContextVar;
	private ContextVar intentosSucursalContextVar;

	private void initialize(AgiRequest request, AgiChannel channel) {
		cf = new CallFlow();
		ctx = new CallContext();
		Handler manejoErrores = new IvrExceptionHandler();
		manejoErrores.setId(UUID.randomUUID());
		cf.addTask(manejoErrores);
		ctx.setChannel(channel);
		ctx.setRequest(request);
	}

	private void setSequence() {

		/* Obtengo DNI */

		stepGetIdCrecer.setNextstep(obtieneSoapDni.GetId());
		obtieneSoapDni.setNextstep(menuCuentas.getInitialStep());

		/* Menu Cuentas */

		menuCuentas.setStepIfFalseUUID(stepAudioFinal.GetId());
		menuCuentas.setStepIfTrueUUID(stepDerivoAlMenuPrincipal.GetId());

	}

	@Override
	public void service(AgiRequest request, AgiChannel channel) {
		Daemon.getDbLog().addCallFlowToLog(channel.getUniqueId(),
				RutinaMenuCuentas.class.getName(), request.getCallerIdNumber());
		this.initialize(request, channel);
		this.createContextVars(channel);
		this.createSteps();
		this.setSequence();

		for (Task tmpTask : menuCuentas.getSteps().values()) {
			cf.addTask(tmpTask);
		}

		ctx.setInitialStep(stepGetIdCrecer.GetId());

		try {
			cf.execute(ctx);
		} catch (Exception ex) {
			Logger.getLogger(TestIvr.class.getName())
					.log(Level.FATAL, null, ex);
		}
	}

	/* -------------------------- Creo Steps -------------------------- */

	private void createSteps() {

		pasoFinal = (StepEnd) StepFactory.createStep(StepType.End,
				UUID.randomUUID());
		pasoFinal.setStepDescription("END => FIN COMUNICACION");
		cf.addTask(pasoFinal);

		/* ---------------- Grupos -------------- */

		menuCuentas = (MenuCuentas) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.menuCuentas);
		menuCuentas.setContextVar(ctx);

		/* ---------------- Audios -------------- */

		stepAudioFinal = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioFinal.setStepDescription("PLAY => SALUDO FINAL");
		stepAudioFinal.setPlayfile("RUTINAPINCOP/RUTINA_PIN032");
		cf.addTask(stepAudioFinal);

		stepAudioVerificarDatos = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioVerificarDatos.setPlayfile("GENERACION/006");
		stepAudioVerificarDatos.setStepDescription("PLAY => VERIFICAR DATOS");
		cf.addTask(stepAudioVerificarDatos);

		/* ---------------- DERIVO MENU PRINCIPAL -------------- */

		stepDerivoAlMenuPrincipal = (StepExecute) StepFactory.createStep(
				StepType.Execute, UUID.randomUUID());
		stepDerivoAlMenuPrincipal.setApp("goto");
		stepDerivoAlMenuPrincipal.setAppOptions(Daemon
				.getConfig("DERIVOMENUPRINCIPAL"));
		stepDerivoAlMenuPrincipal
				.setStepDescription("EXECUTE => DERIVO MENU PRINCIPAL");
		cf.addTask(stepDerivoAlMenuPrincipal);

		/* ---------------- GET VARIBLES -------------- */

		stepGetIdCrecer = (StepGetAsteriskVariable) StepFactory.createStep(
				StepType.GetAsteriskVariable, UUID.randomUUID());
		stepGetIdCrecer
				.setStepDescription("GETASTERISKVARIABLE => OBTIENE ID CRECER");
		stepGetIdCrecer.setVariableName("idcrecer");
		stepGetIdCrecer.setContextVariableName(idCrecerContextVar);
		cf.addTask(stepGetIdCrecer);

		obtieneSoapDni = (StepGetAsteriskVariable) StepFactory.createStep(
				StepType.GetAsteriskVariable, UUID.randomUUID());
		obtieneSoapDni.setContextVariableName(dniContextVar);
		obtieneSoapDni.setVariableName("SOAPDNI");
		obtieneSoapDni
				.setStepDescription("GETASTERISKVARIABLE => OBTIENE SOAPDNI");
		cf.addTask(obtieneSoapDni);

		continuaDialPlan = (StepContinueOnDialPlan) StepFactory.createStep(
				StepType.ContinueOnDialPlan, UUID.randomUUID());
		continuaDialPlan
				.setStepDescription("CONTINUEONDIALPLAN => INGRESO / GENERACION CLAVE OK, CONTINUA OPERANDO");
		cf.addTask(continuaDialPlan);
	}

	/* ---------------- Inicializo las Variables De Contexto -------------- */

	@SuppressWarnings("unchecked")
	private ContextVar getContextVar(String descrip, String initialValue,
			String astUid, String ctxVarName) {
		ContextVar tmpCtxVar = new ContextVar(ctx);
		tmpCtxVar.setId(this.idContextVar++);
		tmpCtxVar.setVarDescrip(descrip);
		tmpCtxVar.setAstUid(astUid);
		tmpCtxVar.setVarValue(initialValue);
		tmpCtxVar.setCtxVarName(ctxVarName);
		ctx.put(tmpCtxVar.getId(), tmpCtxVar);
		return tmpCtxVar;
	}

	@SuppressWarnings("unused")
	private void createContextVars(AgiChannel channel) {

		String AstUid = channel.getUniqueId();

		String ast_uid = "";

		if (AstUid.contains("-")) {
			ast_uid = AstUid.split("-")[1];
		} else {
			ast_uid = AstUid;
		}

		ast_uid = String.format("%030d", 0) + ast_uid.replaceAll("\\.", "");

		/* Dni */

		dniContextVar = this.getContextVar("Dni", "", AstUid, "dniContextVar");
		dniContextVar.setStringFormat("%08d");

		confirmaDniContextVar = this.getContextVar("ConfirmaDni", "", AstUid,
				"confirmaDniContextVar");

		intentosDniContextVar = this.getContextVar("Intentos Dni", "0", AstUid,
				"intentosDniContextVar");

		/* Fecha */

		fechaContextVar = this.getContextVar("Fecha Formato 6 Digitos", "",
				AstUid, "fechaContextVar");

		fdnContexVar = this.getContextVar("fdnContexVar", "", AstUid,
				"fdnContexVar");

		confirmaFechaContextVar = this.getContextVar(
				"Confirmacion de Fecha de Nacimiento", "", AstUid,
				"confirmaFechaContextVar");

		intentosFechaContextVar = this.getContextVar("intentosFechaContextVar",
				"0", AstUid, "intentosFechaContextVar");

		contextVarDia = this.getContextVar("contextVarDia", "", AstUid,
				"contextVarDia");

		contextVarMes = this.getContextVar("contextVarMes", "", AstUid,
				"contextVarMes");

		contextVarAnio = this.getContextVar("contextVarAnio", "", AstUid,
				"contextVarAnio");
		contextVarAnio.setStringFormat("%02d");

		/* Tarjeta */

		tarjetaContexVar = this.getContextVar("Tarjeta", "", AstUid,
				"tarjetaContexVar");
		tarjetaContexVar.setStringFormat("%16d");

		confirmaTarjetaContextVar = this.getContextVar(
				"confirmaTarjetaContextVar", "", AstUid,
				"confirmaTarjetaContextVar");

		intentosTarjetaContextVar = this.getContextVar(
				"intentosTarjetaContextVar", "0", AstUid,
				"intentosTarjetaContextVar");

		/* Clave */

		genClaveContextVar = this.getContextVar("genClaveContextVar", "",
				AstUid, "genClaveContextVar");

		cambiarClaveContextVar = this.getContextVar("cambiarClaveContextVar",
				"", AstUid, "cambiarClaveContextVar");

		intentosClaveContextVar = this.getContextVar("intentosClaveContextVar",
				"0", AstUid, "intentosClaveContextVar");

		claveContextVar = this.getContextVar("claveContextVar", "", AstUid,
				"claveContextVar");

		retCambioClaveContextVar = this.getContextVar(
				"retCambioClaveContextVar", "", AstUid,
				"retCambioClaveContextVar");

		cantDiasExpiracioncontextVar = this.getContextVar(
				"cantDiasExpiracioncontextVar", "", AstUid,
				"cantDiasExpiracioncontextVar");

		retAuthPasswordcontextVar = this.getContextVar(
				"retAuthPasswordcontextVar", "", AstUid,
				"retAuthPasswordcontextVar");

		retAutentificacionContextVar = this.getContextVar(
				"retAutentificacionContextVar", "", AstUid,
				"retAutentificacionContextVar");

		confirmacionClaveContextVar = this.getContextVar(
				"confirmacionClaveContextVar", "", AstUid,
				"confirmacionClaveContextVar");

		/* Intentos Menues */

		intentosIngresoContextVar = this.getContextVar(
				"intentosIngresoContextVar", "0", AstUid,
				"intentosIngresoContextVar");

		intentosMenuInicialContextVar = this.getContextVar(
				"intentosMenuInicialContextVar", "0", AstUid,
				"intentosMenuInicialContextVar");

		/* Resultado Play Read */

		resultadoAudioInicio = this.getContextVar("resultadoAudioInicio", "",
				AstUid, "resultadoAudioInicio");

		scapeDigitContextVar = this.getContextVar("scapeDigitContextVar", "",
				AstUid, "scapeDigitContextVar");

		/* Menu */

		menuIngresoContextVar = this.getContextVar("Resultado Audio Inicio",
				"", AstUid, "menuIngresoContextVar");

		/* GET ASTERISK */

		idCrecerContextVar = this.getContextVar("idCrecerContextVar", "",
				AstUid, "idCrecerContextVar");

		/* USUARIO NUEVO / BLOQUEADO / OK */

		estadoDelUsuarioContextVar = this.getContextVar(
				"estadoDelUsuarioContextVar", "", AstUid,
				"estadoDelUsuarioContextVar");

		/* C. Predeterminadas */

		tieneCuentasPredeterminadasContextVar = this.getContextVar(
				"tieneCuentasPredeterminadasContextVar", "0", AstUid,
				"tieneCuentasPredeterminadasContextVar");

		cuentasPredeterminadasContextVar = this.getContextVar(
				"cuentasPredeterminadasContextVar", "", AstUid,
				"cuentasPredeterminadasContextVar");

		cuentasContextVar = this.getContextVar("cuentasContextVar", "", AstUid,
				"cuentasContextVar");

		cuentaContextVar = this.getContextVar("cuentaContextVar", "", AstUid,
				"cuentaContextVar");

		tipoDeCuentaContextVar = this.getContextVar("tipoDeCuentaContextVar",
				"", AstUid, "tipoDeCuentaContextVar");

		ultimosTresDigitosCuentaContextVar = this.getContextVar(
				"ultimosTresDigitosCuentaContextVar", "", AstUid,
				"ultimosTresDigitosCuentaContextVar");

		predeterminarOtraCuentaContextVar = this.getContextVar(
				"predeterminarOtraCuentaContextVar", "", AstUid,
				"predeterminarOtraCuentaContextVar");

		repetirPredeterminadasContextVar = this.getContextVar(
				"repetirPredeterminadasContextVar", "", AstUid,
				"repetirPredeterminadasContextVar");

		sucursalContextVar = this.getContextVar("sucursalContextVar", "",
				AstUid, "sucursalContextVar");

		quierePredeterminarContextVar = this.getContextVar(
				"quierePredeterminarContextVar", "", AstUid,
				"quierePredeterminarContextVar");

		saldosCuentasContextVar = this.getContextVar("saldosCuentasContextVar",
				"", AstUid, "saldosCuentasContextVar");

		cuentaADesvincularContextVar = this.getContextVar(
				"cuentaADesvincularContextVar", "", AstUid,
				"cuentaADesvincularContextVar");

		cuentaAVincularContextVar = this.getContextVar(
				"cuentaAVincularContextVar", "", AstUid,
				"cuentaAVincularContextVar");

		statusPredeterminadaContexVar = this.getContextVar(
				"statusPredeterminadaContexVar", "", AstUid,
				"statusPredeterminadaContexVar");

		vinculoCuentaContextVar = this.getContextVar("vinculoCuentaContextVar",
				"1", AstUid, "vinculoCuentaContextVar");

		desvinculoCuentaContextVar = this.getContextVar(
				"desvinculoCuentaContextVar", "0", AstUid,
				"desvinculoCuentaContextVar");

		tipoDeCuentaAAgregarContextVar = this.getContextVar(
				"tipoDeCuentaAAgregarContextVar", "0", AstUid,
				"tipoDeCuentaAAgregarContextVar");

		tipoDeCuentaABorrarContextVar = this.getContextVar(
				"tipoDeCuentaABorrarContextVar", "0", AstUid,
				"tipoDeCuentaABorrarContextVar");

		cantidadDeCuentasEncontradasContextVar = this.getContextVar(
				"cantidadDeCuentasEncontradasContextVar", "3", AstUid,
				"cantidadDeCuentasEncontradasContextVar");

		predeterminarCuentasContextVar = this.getContextVar(
				"predeterminarCuentasContextVar", "3", AstUid,
				"predeterminarCuentasContextVar");

		intentosCuentaContextVar = this.getContextVar(
				"intentosCuentaContextVar", "0", AstUid,
				"intentosCuentaContextVar");

		intentosSucursalContextVar = this.getContextVar(
				"intentosSucursalContextVar", "0", AstUid,
				"intentosSucursalContextVar");
	}
}
