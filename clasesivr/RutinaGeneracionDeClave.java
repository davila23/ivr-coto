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
import step.StepSetAsteriskVariable;
import step.StepFactory.StepType;
import step.StepPlay;
import step.StepPlayRead;
import step.StepSendJPOS;
import step.StepSetVariable;
import step.StepSwitch;
import step.group.GeneracionDeClave;
import step.group.IngresoDeClave;
import step.group.PideDni;
import step.group.PideFecha;
import step.group.PideTarjeta;
import step.group.StepGroupFactory;
import workflow.Handler;
import workflow.Task;
import condition.condition;
import context.ContextVar;

public class RutinaGeneracionDeClave extends BaseAgiScript {

	private long idContextVar = 1;
	CallContext ctx;
	CallFlow cf;
	private StepEnd pasoFinal;
	private StepAnswer inicial;
	private int intentos = 3;
	private String idcrecer;
	private IngresoDeClave ingresoDeClaveGrp;
	private GeneracionDeClave generacionDeClaveGrp;
	private StepContinueOnDialPlan continuaDialPlan;
	private StepCounter contadorIntentosClave;
	private StepExecute stepDerivoAlMenuPrincipal;
	private StepPlay stepAudioVerificarDatos;
	private StepGetAsteriskVariable stepGetIdCrecer;
	private StepGetAsteriskVariable stepSetSoapFdn;
	private StepPlay stepAudioFinal;
	private ContextVar fechaDeVencimientoClaveContextVar;
	private ContextVar intentosClaveContextVar;
	private ContextVar intentosClaveVaciaContextVar;
	private ContextVar intentosClaveVaciaGenContextVar;
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
	private ContextVar salidaValidaContextVar;
	private ContextVar saldoCuentasContextVar;
	private ContextVar predeterminarOtraCuentaContextVar;
	private StepExecute stepDerivoLMenuInicial;
	private ContextVar derivoContextVar;
	private ContextVar menuPrincipalTresContextVar;

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

		/* Obtiene datos */

		stepGetIdCrecer.setNextstep(ingresoDeClaveGrp.getInitialStep());

		/* Ingreso Clave */

		ingresoDeClaveGrp.setStepIfTrueUUID(salidaValida.GetId());
		ingresoDeClaveGrp.setStepIfFalseUUID(generacionDeClaveGrp
				.getInitialStep());

		/* Genero Clave */

		generacionDeClaveGrp.setStepIfFalseUUID(stepDerivoLMenuInicial
				.GetId());
		generacionDeClaveGrp.setStepIfTrueUUID(salidaValida.GetId());

		/* Audio Disuade */
		stepAudioVerificarDatos.setNextstep(stepAudioFinal.GetId());
		stepAudioFinal.setNextstep(pasoFinal.GetId());

		/* Continua Dial plan */
		
		salidaValida.setNextstep(continuaDialPlan.GetId());
		
		
		stepAudioFinal.setNextstep(pasoFinal.GetId());

		
	}

	@Override
	public void service(AgiRequest request, AgiChannel channel) {
		Daemon.getDbLog().addCallFlowToLog(channel.getUniqueId(),
				RutinaGeneracionDeClave.class.getName(),
				request.getCallerIdNumber());
		this.initialize(request, channel);
		this.createContextVars(channel);
		this.createSteps();
		this.setSequence();

		for (Task tmpTask : ingresoDeClaveGrp.getSteps().values()) {
			cf.addTask(tmpTask);
		}

		for (Task tmpTask : generacionDeClaveGrp.getSteps().values()) {
			cf.addTask(tmpTask);
		}

		for (Task tmpTask : generacionDeClaveGrp.getPideFecha().getSteps()
				.values()) {
			cf.addTask(tmpTask);
		}

		for (Task tmpTask : generacionDeClaveGrp.getPideTarjeta().getSteps()
				.values()) {
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

		/* ---------------- Asterisk -------------- */

		/* ---------------- Grupos -------------- */

		ingresoDeClaveGrp = (IngresoDeClave) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.ingresoDeClave);
		ingresoDeClaveGrp.setContextVar(ctx);

		generacionDeClaveGrp = (GeneracionDeClave) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.generacionDeClave);
		generacionDeClaveGrp.setContextVar(ctx);

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

		salidaValida = (StepSetAsteriskVariable) StepFactory.createStep(
				StepType.SetAsteriskVariable, UUID.randomUUID());
		salidaValida
				.setStepDescription("SETASTERISKVARIABLE => SETEA SALIDA VALIDA DE INGRESO DE CLAVE");
		salidaValida.setContextVariableName(salidaValidaContextVar);
		salidaValida.setVariableName("VALIDUSER");
		cf.addTask(salidaValida);

		continuaDialPlan = (StepContinueOnDialPlan) StepFactory.createStep(
				StepType.ContinueOnDialPlan, UUID.randomUUID());
		continuaDialPlan
				.setStepDescription("CONTINUEONDIALPLAN => INGRESO / GENERACION CLAVE OK, CONTINUA OPERANDO");
		cf.addTask(continuaDialPlan);
		
		stepAudioFinal = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioFinal.setStepDescription("PLAY => SALUDO FINAL");
		stepAudioFinal.setPlayfile("RUTINAPINCOP/RUTINA_PIN032");
		cf.addTask(stepAudioFinal);
		
		
		stepDerivoLMenuInicial = (StepExecute) StepFactory.createStep(
				StepType.Execute, UUID.randomUUID());
		stepDerivoLMenuInicial.setApp("goto");
		stepDerivoLMenuInicial.setAppOptions(Daemon
				.getConfig("DERIVOPPALCREDICOOP"));
		stepDerivoLMenuInicial
				.setStepDescription("EXECUTE => DERIVO MENU INICIAL");
		cf.addTask(stepDerivoLMenuInicial);
		
		pasoFinal = (StepEnd) StepFactory.createStep(StepType.End,
				UUID.randomUUID());
		pasoFinal.setStepDescription("END => FIN DE COMUNICACION");
		cf.addTask(pasoFinal);
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

		intentosClaveVaciaContextVar = this.getContextVar(
				"intentosClaveVaciaContextVar", "0", AstUid,
				"intentosClaveVaciaContextVar");

		intentosClaveVaciaGenContextVar = this.getContextVar(
				"intentosClaveVaciaGenContextVar", "0", AstUid,
				"intentosClaveVaciaGenContextVar");

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

		/* SET ASTERISK */

		salidaValidaContextVar = this.getContextVar("salidaValidaContextVar",
				"1", AstUid, "salidaValidaContextVar");

		/* USUARIO NUEVO / BLOQUEADO / OK */

		estadoDelUsuarioContextVar = this.getContextVar(
				"estadoDelUsuarioContextVar", "", AstUid,
				"estadoDelUsuarioContextVar");

		derivoContextVar= this.getContextVar(
				"derivoContextVar", "NO", AstUid,
				"derivoContextVar");
		
		menuPrincipalTresContextVar= this.getContextVar(
				"menuPrincipalTresContextVar", "XXPOCSCYP", AstUid,
				"menuPrincipalTresContextVar");
		
	}
}
