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
import step.StepFileExist;
import step.StepGetCheckPCT;
import step.StepLimitPlayRead;
import step.StepMenu;
import step.StepSayFiliales;
import step.StepSayNumber;
import step.StepSetAsteriskVariable;
import step.StepFactory.StepType;
import step.StepParseSaldosTarjeta;
import step.StepPlay;
import step.StepPlayFromVar;
import step.StepPlayRead;
import step.StepSendJPOS;
import step.StepSwitch;
import step.StepTimeConditionDB;
import step.group.ActivacionTarjetaCOTO;
import step.group.ArmaSaldoTarjetaCoto;
import step.group.BajaCompaniaAseguradoraCOTO;
import step.group.PideCuitCredicoop;
import step.group.PideDni;
import step.group.PideFecha;
import step.group.PideTarjeta;
import step.group.StepGroupFactory;
import workflow.Handler;
import workflow.Task;
import condition.condition;
import context.ContextVar;

public class RutinaPCT extends BaseAgiScript {

	private long idContextVar = 1;
	CallContext ctx;
	CallFlow cf;
	protected StepEnd pasoFinal;
	protected int intentos = 3;
	protected StepAnswer inicial;
	protected StepPlay stepAudioFinal;
	protected StepPlay stepAudioSuperoIntentos;
	protected StepPlay stepAudioBienvenida;
	protected StepPlay stepAudioTienePendientes;
	protected StepPlay stepAudioNoTienePendientes;
	protected StepLimitPlayRead stepAudioIngreseCuit;
	protected StepConditional evalTienePendientes;
	protected ContextVar scapeDigitContextVar;
	protected ContextVar chequesPendientesContextVar;
	protected ContextVar cuitContextVar;
	protected StepPlay stepAudioDespedida;
	protected StepMenu menuConfirmacionCuit;
	protected ContextVar confirmacionCuitContextVar;
	protected StepGetCheckPCT stepCheckPCT;
	protected PideCuitCredicoop pideCuitGrp;
	protected ContextVar confirmaCuitContextVar;
	protected ContextVar intentosCuitContextVar;
	protected StepPlay stepAudioValoresEnlaFilial;
	protected StepSayNumber stepNumberCantidadCheques;
	protected StepSayNumber stepNumberNumeroDeFilial;
	protected ContextVar numeroDeFilialContextVar;
	protected ContextVar cantidadDechequesPendientesContextVar;
	protected StepPlay stepAudioPrevioFinal;
	protected StepPlay stepAudioEspera;
	protected StepConditional evalBeneficiarioMultiple;
	protected ContextVar listaDeFilialesContextVar;
	protected StepSayFiliales stepSayFiliales;
	protected String tmp = "";
	protected StepPlay stepAudioValoresEnlaFilialMultilple;
	protected ContextVar menuRepetirInfoContextVar;
	protected StepLimitPlayRead stepAudioRepetirInfo;
	protected StepMenu stepMenuRepetirInfo;
	protected StepPlay stepAudioIngresoIncorrectoMenuRepetirInfo;
	protected StepFileExist stepFileExist;
	protected StepPlay stepAudioDireccionFIlial;
	protected StepLimitPlayRead stepAudioQuiereMasInfo;
	protected StepMenu stepMenuQuiereMasInfo;
	protected ContextVar menuInfoFilialContextVar;
	protected StepPlay stepAudioIngresoIncorrectoMenuQuiereMasInfo;
	protected StepPlay stepAudioIngresoIncorrectoMenuRepetirInfoMultiple;
	protected StepPlay stepAudioIngresoIncorrectoMenuQuiereMasInfoMultiple;
	protected StepMenu stepMenuQuiereMasInfoMultiple;
	protected StepLimitPlayRead stepAudioQuiereMasInfoMultiple;
	protected ContextVar menuInfoFilialMultipleContextVar;
	protected StepPlayRead stepAudioIngresoFilial;
	protected ContextVar filialInfoContextVar;
	protected StepFileExist stepFileExistMultiple;
	protected StepPlay stepAudioDireccionSucursalWeb;

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

		/* Hola */

		stepAudioBienvenida.setNextstep(pideCuitGrp.getInitialStep());

		/* Ingreso Cuit */

		pideCuitGrp.setStepIfTrue(stepAudioEspera.GetId());
		pideCuitGrp.setStepIfFalse(stepAudioSuperoIntentos.GetId());

		/* Consulto Servicio PCT */

		stepAudioEspera.setNextstep(stepCheckPCT.GetId());

		stepCheckPCT.setNextStepIsTrue(stepAudioTienePendientes.GetId());
		stepCheckPCT.setNextStepIsFalse(stepAudioNoTienePendientes.GetId());

		/* Tiene Pendientes de retirar */

		stepAudioTienePendientes.setNextstep(stepNumberCantidadCheques.GetId());

		stepNumberCantidadCheques.setNextstep(evalBeneficiarioMultiple.GetId());

		evalBeneficiarioMultiple.addCondition(new condition(1, "#{"
				+ numeroDeFilialContextVar.getVarName() + "} == " + "0",
				stepAudioValoresEnlaFilialMultilple.GetId(),
				stepAudioValoresEnlaFilial.GetId()));

		/* Una Sucursal */

		stepAudioValoresEnlaFilial
				.setNextstep(stepNumberNumeroDeFilial.GetId());

		stepNumberNumeroDeFilial.setNextstep(stepAudioDireccionSucursalWeb.GetId());

		/*
		 * Quiere la Direccion de la Filial -- Anulado
		 * 
		 * stepAudioQuiereMasInfo.setNextstep(stepMenuQuiereMasInfo.GetId());
		 * 
		 * stepAudioQuiereMasInfo
		 * .setNextStepIfAttemptLimit(stepAudioFinal.GetId());
		 * 
		 * stepMenuQuiereMasInfo.addSteps("1", stepFileExist.GetId());
		 * stepMenuQuiereMasInfo.addSteps("2", stepAudioPrevioFinal.GetId());
		 * stepMenuQuiereMasInfo
		 * .setInvalidOption(stepAudioIngresoIncorrectoMenuRepetirInfo
		 * .GetId());
		 * 
		 * stepAudioIngresoIncorrectoMenuQuiereMasInfo
		 * .setNextstep(stepAudioQuiereMasInfo.GetId());
		 * 
		 * stepFileExist.setNextStepIsTrue(stepAudioRepetirInfo.GetId());
		 * stepFileExist.setNextStepIsFalse(stepAudioPrevioFinal.GetId());/*
		 * 
		 * 
		 * /* Varias Sucursales
		 */

		stepAudioValoresEnlaFilialMultilple
				.setNextstep(stepSayFiliales.GetId());

		stepSayFiliales.setNextstep(stepAudioDireccionSucursalWeb.GetId());

		/*
		 * Anulado
		 * 
		 * stepAudioQuiereMasInfoMultiple.setNextstep(stepMenuQuiereMasInfoMultiple
		 * .GetId());
		 * 
		 * stepAudioQuiereMasInfoMultiple.setNextStepIfAttemptLimit(stepAudioFinal
		 * .GetId());
		 * 
		 * stepMenuQuiereMasInfoMultiple.addSteps("1",
		 * stepAudioIngresoFilial.GetId());
		 * stepMenuQuiereMasInfoMultiple.addSteps("2",
		 * stepAudioPrevioFinal.GetId()); stepMenuQuiereMasInfoMultiple
		 * .setInvalidOption(stepAudioIngresoIncorrectoMenuRepetirInfoMultiple
		 * .GetId());
		 * 
		 * stepAudioIngresoIncorrectoMenuRepetirInfoMultiple
		 * .setNextstep(stepAudioQuiereMasInfoMultiple.GetId());
		 * 
		 * stepAudioIngresoFilial.setNextstep(stepFileExistMultiple.GetId());
		 * 
		 * stepFileExistMultiple.setNextStepIsTrue(stepAudioRepetirInfo.GetId());
		 * stepFileExistMultiple
		 * .setNextStepIsFalse(stepAudioPrevioFinal.GetId()); /*
		 * 
		 * /* Repite Info
		 */

		stepAudioRepetirInfo.setNextstep(stepMenuRepetirInfo.GetId());

		stepAudioRepetirInfo.setNextStepIfAttemptLimit(stepAudioSuperoIntentos
				.GetId());

		stepMenuRepetirInfo.addSteps("1", stepAudioTienePendientes.GetId());
		stepMenuRepetirInfo.addSteps("2", stepAudioPrevioFinal.GetId());
		stepMenuRepetirInfo
				.setInvalidOption(stepAudioIngresoIncorrectoMenuRepetirInfo
						.GetId());

		stepAudioIngresoIncorrectoMenuRepetirInfo
				.setNextstep(stepAudioRepetirInfo.GetId());

		/* NO Tiene Pendientes de retirar */

		stepAudioNoTienePendientes.setNextstep(stepAudioPrevioFinal.GetId());

		
		stepAudioDireccionSucursalWeb.setNextstep(stepAudioRepetirInfo.GetId());
		
		/* Chau */

		stepAudioSuperoIntentos.setNextstep(stepAudioFinal.GetId());
		stepAudioPrevioFinal.setNextstep(stepAudioFinal.GetId());
		stepAudioFinal.setNextstep(pasoFinal.GetId());

	}

	@Override
	public void service(AgiRequest request, AgiChannel channel) {
		Daemon.getDbLog().addCallFlowToLog(channel.getUniqueId(),
				RutinaPCT.class.getName(), request.getCallerIdNumber());
		this.initialize(request, channel);
		this.createContextVars(channel);
		this.createSteps();
		this.setSequence();

		for (Task tmpTask : pideCuitGrp.getSteps().values()) {
			cf.addTask(tmpTask);
		}

		ctx.setInitialStep(stepAudioBienvenida.GetId());
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
		pasoFinal.setStepDescription("END => FIN COMUNICACION RUTINA PCT");
		cf.addTask(pasoFinal);

		inicial = (StepAnswer) StepFactory.createStep(StepType.Answer,
				UUID.randomUUID());
		inicial.setStepDescription("ANSWER => INICIO COMUNICACION RUTINA PCT ");
		cf.addTask(inicial);

		/* ---------------- Grupos -------------- */

		pideCuitGrp = (PideCuitCredicoop) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.pideCuit);
		pideCuitGrp.setAudioCuit("PCT/P001");
		pideCuitGrp.setAudioSuCuitEs("PCT/P002");
		pideCuitGrp.setAudioValidateCuit("PCT/P003");
		pideCuitGrp.setAudioCuitInvalido("PCT/P004");
		pideCuitGrp.setCuitContextVar(cuitContextVar);
		pideCuitGrp.setConfirmaCuitContextVar(confirmaCuitContextVar);
		pideCuitGrp.setIntentosCuitContextVar(intentosCuitContextVar);

		/* ---------------- Limit Play Read -------------- */

		stepAudioQuiereMasInfo = (StepLimitPlayRead) StepFactory.createStep(
				StepType.LimitPlayRead, UUID.randomUUID());
		stepAudioQuiereMasInfo.setPlayFile("PCT/P011");
		stepAudioQuiereMasInfo.setContextVariableName(menuInfoFilialContextVar);
		stepAudioQuiereMasInfo.setIntentos(intentos);
		stepAudioQuiereMasInfo
				.setStepDescription("LIMITPLAYREAD => MENU INFORMACION FILIAL");
		cf.addTask(stepAudioQuiereMasInfo);

		stepAudioQuiereMasInfoMultiple = (StepLimitPlayRead) StepFactory
				.createStep(StepType.LimitPlayRead, UUID.randomUUID());
		stepAudioQuiereMasInfoMultiple.setPlayFile("PCT/P011");
		stepAudioQuiereMasInfoMultiple
				.setContextVariableName(menuInfoFilialMultipleContextVar);
		stepAudioQuiereMasInfoMultiple.setIntentos(intentos);
		stepAudioQuiereMasInfoMultiple
				.setStepDescription("LIMITPLAYREAD => MENU INFORMACION FILIAL MULTIPLE");
		cf.addTask(stepAudioQuiereMasInfoMultiple);

		stepAudioRepetirInfo = (StepLimitPlayRead) StepFactory.createStep(
				StepType.LimitPlayRead, UUID.randomUUID());
		stepAudioRepetirInfo.setPlayFile("PCT/P012");
		stepAudioRepetirInfo.setContextVariableName(menuRepetirInfoContextVar);
		stepAudioRepetirInfo.setIntentos(intentos);
		stepAudioRepetirInfo
				.setStepDescription("LIMITPLAYREAD => MENU REPETIR INFO PCT");
		cf.addTask(stepAudioRepetirInfo);

		/* ---------------- Menu -------------- */

		stepMenuQuiereMasInfoMultiple = (StepMenu) StepFactory.createStep(
				StepType.Menu, UUID.randomUUID());
		stepMenuQuiereMasInfoMultiple
				.setStepDescription("MENU => INFORMACION FILIAL MULTIPLE");
		stepMenuQuiereMasInfoMultiple
				.setContextVariableName(menuInfoFilialMultipleContextVar);
		cf.addTask(stepMenuQuiereMasInfoMultiple);

		stepMenuQuiereMasInfo = (StepMenu) StepFactory.createStep(
				StepType.Menu, UUID.randomUUID());
		stepMenuQuiereMasInfo.setStepDescription("MENU => INFORMACION FILIAL");
		stepMenuQuiereMasInfo.setContextVariableName(menuInfoFilialContextVar);
		cf.addTask(stepMenuQuiereMasInfo);

		stepMenuRepetirInfo = (StepMenu) StepFactory.createStep(StepType.Menu,
				UUID.randomUUID());
		stepMenuRepetirInfo.setStepDescription("MENU => MENU REPETIR INFO PCT");
		stepMenuRepetirInfo.setContextVariableName(menuRepetirInfoContextVar);
		cf.addTask(stepMenuRepetirInfo);

		/* ---------------- Audios -------------- */

		stepAudioIngresoFilial = (StepPlayRead) StepFactory.createStep(
				StepType.PlayRead, UUID.randomUUID());
		stepAudioIngresoFilial.setPlayFile("PCT/P013");
		stepAudioIngresoFilial.setContextVariableName(filialInfoContextVar);
		stepAudioIngresoFilial
				.setStepDescription("PLAYREAD => INGRESO FILIAL A CONSULTAR");
		cf.addTask(stepAudioIngresoFilial);

		/* ---------------- Audios -------------- */

		stepAudioBienvenida = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioBienvenida.setStepDescription("PLAY => BIENVENIDA RUTINA PCT");
		stepAudioBienvenida.setPlayfile("PCT/B001");
		cf.addTask(stepAudioBienvenida);

		stepAudioFinal = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioFinal.setStepDescription("PLAY => SALUDO FINAL RUTINA PCT");
		stepAudioFinal.setPlayfile("PCT/D002");
		cf.addTask(stepAudioFinal);

		stepAudioPrevioFinal = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioPrevioFinal
				.setStepDescription("PLAY => AUDIO PREVIO A SALUDO FINAL RUTINA PCT");
		stepAudioPrevioFinal.setPlayfile("PCT/D001");
		cf.addTask(stepAudioPrevioFinal);

		stepAudioTienePendientes = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioTienePendientes
				.setStepDescription("PLAY => TIENE CHEQUES PENDIENTES DE RETIRAR PCT");
		stepAudioTienePendientes.setPlayfile("PCT/P005");
		cf.addTask(stepAudioTienePendientes);

		stepAudioEspera = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioEspera.setStepDescription("PLAY => UN MOMENTO POR FAVOR PCT");
		stepAudioEspera.setPlayfile("PCT/P009");
		cf.addTask(stepAudioEspera);

		stepAudioValoresEnlaFilial = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioValoresEnlaFilial
				.setStepDescription("PLAY => SUCURSAL DONDE RETIRAR PCT");
		stepAudioValoresEnlaFilial.setPlayfile("PCT/P006");
		cf.addTask(stepAudioValoresEnlaFilial);

		stepAudioIngresoIncorrectoMenuQuiereMasInfo = (StepPlay) StepFactory
				.createStep(StepType.Play, UUID.randomUUID());
		stepAudioIngresoIncorrectoMenuQuiereMasInfo.setPlayfile("PCT/P014");
		stepAudioIngresoIncorrectoMenuQuiereMasInfo
				.setStepDescription("PLAY => INGRESO NULO O INCORRECTO - MENU REPETIR INFO PCT");
		cf.addTask(stepAudioIngresoIncorrectoMenuQuiereMasInfo);

		stepAudioIngresoIncorrectoMenuQuiereMasInfoMultiple = (StepPlay) StepFactory
				.createStep(StepType.Play, UUID.randomUUID());
		stepAudioIngresoIncorrectoMenuQuiereMasInfoMultiple
				.setPlayfile("PCT/P014");
		stepAudioIngresoIncorrectoMenuQuiereMasInfoMultiple
				.setStepDescription("PLAY => INGRESO NULO O INCORRECTO - MENU REPETIR INFO PCT");
		cf.addTask(stepAudioIngresoIncorrectoMenuQuiereMasInfoMultiple);

		stepAudioIngresoIncorrectoMenuRepetirInfo = (StepPlay) StepFactory
				.createStep(StepType.Play, UUID.randomUUID());
		stepAudioIngresoIncorrectoMenuRepetirInfo.setPlayfile("PCT/P014");
		stepAudioIngresoIncorrectoMenuRepetirInfo
				.setStepDescription("PLAY => INGRESO NULO O INCORRECTO - MENU REPETIR INFO PCT");
		cf.addTask(stepAudioIngresoIncorrectoMenuRepetirInfo);

		stepAudioIngresoIncorrectoMenuRepetirInfoMultiple = (StepPlay) StepFactory
				.createStep(StepType.Play, UUID.randomUUID());
		stepAudioIngresoIncorrectoMenuRepetirInfoMultiple
				.setPlayfile("PCT/P014");
		stepAudioIngresoIncorrectoMenuRepetirInfoMultiple
				.setStepDescription("PLAY => INGRESO NULO O INCORRECTO - MENU REPETIR INFO MULTIPLE PCT");
		cf.addTask(stepAudioIngresoIncorrectoMenuRepetirInfoMultiple);

		stepAudioValoresEnlaFilialMultilple = (StepPlay) StepFactory
				.createStep(StepType.Play, UUID.randomUUID());
		stepAudioValoresEnlaFilialMultilple
				.setStepDescription("PLAY => SUCURSALES DONDE RETIRAR PCT");
		stepAudioValoresEnlaFilialMultilple.setPlayfile("PCT/P007");
		cf.addTask(stepAudioValoresEnlaFilialMultilple);

		
		stepAudioDireccionSucursalWeb = (StepPlay) StepFactory
				.createStep(StepType.Play, UUID.randomUUID());
		stepAudioDireccionSucursalWeb
				.setStepDescription("PLAY => DIRECCIONES SUCURSALES EN LA WEB");
		stepAudioDireccionSucursalWeb.setPlayfile("PCT/P010");
		cf.addTask(stepAudioDireccionSucursalWeb);
		
		stepAudioNoTienePendientes = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioNoTienePendientes
				.setStepDescription("PLAY => NO TIENE CHEQUES PENDIENTES DE RETIRAR PCT");
		stepAudioNoTienePendientes.setPlayfile("PCT/P008");
		cf.addTask(stepAudioNoTienePendientes);

		stepAudioSuperoIntentos = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioSuperoIntentos.setPlayfile("PCT/P015");
		stepAudioSuperoIntentos
				.setStepDescription("PLAY => SUPERO INTENTOS RUTINA PCT");
		cf.addTask(stepAudioSuperoIntentos);

		/* ---------------- Say Filiales -------------- */

		stepSayFiliales = (StepSayFiliales) StepFactory.createStep(
				StepType.SayFiliales, UUID.randomUUID());
		stepSayFiliales.setListaDeFilialesContextVar(listaDeFilialesContextVar);
		stepSayFiliales
				.setStepDescription("SAYFILIALES => FILIALES BENEFICIARIO MULTIPLE PCT");
		cf.addTask(stepSayFiliales);

		/* ---------------- Say Number -------------- */

		stepNumberCantidadCheques = (StepSayNumber) StepFactory.createStep(
				StepType.SayNumber, UUID.randomUUID());
		stepNumberCantidadCheques
				.setStepDescription("SAYNUMBER => CANTIDAD DE CHEQUES PARA RETIRAR PCT");
		stepNumberCantidadCheques
				.setContextVariableName(cantidadDechequesPendientesContextVar);
		cf.addTask(stepNumberCantidadCheques);

		stepNumberNumeroDeFilial = (StepSayNumber) StepFactory.createStep(
				StepType.SayNumber, UUID.randomUUID());
		stepNumberNumeroDeFilial
				.setStepDescription("SAYNUMBER => NUMERO DE FILIAL PARA RETIRAR PCT");
		stepNumberNumeroDeFilial
				.setContextVariableName(numeroDeFilialContextVar);
		cf.addTask(stepNumberNumeroDeFilial);

		/* ---------------- File Exist -------------- */

		stepFileExist = (StepFileExist) StepFactory.createStep(
				StepType.FileExist, UUID.randomUUID());
		stepFileExist.setNumeroDeFilialContextVar(numeroDeFilialContextVar);
		stepFileExist.setDirectorio("/home/davila/PCT/");
		// stepFileExist.setDirectorio("/opt/asterisk/var/lib/asterisk/sounds/PCT/");
		stepFileExist
				.setStepDescription("FILEEXIT => VERIFICO QUE EXISTA EL AUDIO Y LOCUCIONO DIRECCION");
		cf.addTask(stepFileExist);

		stepFileExistMultiple = (StepFileExist) StepFactory.createStep(
				StepType.FileExist, UUID.randomUUID());
		stepFileExistMultiple.setNumeroDeFilialContextVar(filialInfoContextVar);
		stepFileExistMultiple.setDirectorio("/home/davila/PCT/");
		// stepFileExist.setDirectorio("/opt/asterisk/var/lib/asterisk/sounds/PCT/");
		stepFileExistMultiple
				.setStepDescription("FILEEXIT => VERIFICO QUE EXISTA EL AUDIO Y LOCUCIONO DIRECCION");
		cf.addTask(stepFileExistMultiple);

		/* ---------------- Conditional -------------- */

		evalBeneficiarioMultiple = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalBeneficiarioMultiple
				.setStepDescription("CONDITIONAL => AUDIO FINAL");
		cf.addTask(evalBeneficiarioMultiple);

		/* ---------------- Obtiene Cheques Pendientes -------------- */

		stepCheckPCT = (StepGetCheckPCT) StepFactory.createStep(
				StepType.GetCheckPct, UUID.randomUUID());
		stepCheckPCT.setCuitContextVar(cuitContextVar);
		stepCheckPCT
				.setCantidacantidadDechequesPendientesContextVar(cantidadDechequesPendientesContextVar);
		stepCheckPCT.setNumeroDeFilialContextVar(numeroDeFilialContextVar);
		stepCheckPCT.setListaDeFilialesContextVar(listaDeFilialesContextVar);
		stepCheckPCT
				.setStepDescription("GETCHEACKPCT => OBTENGO CHEQUES DE PCT");
		cf.addTask(stepCheckPCT);

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
		/* --- Inicio --- */

		String AstUid = channel.getUniqueId();

		String ast_uid = "";

		if (AstUid.contains("-")) {
			ast_uid = AstUid.split("-")[1];
		} else {
			ast_uid = AstUid;
		}

		ast_uid = String.format("%030d", 0) + ast_uid.replaceAll("\\.", "");

		confirmacionCuitContextVar = this.getContextVar(
				"confirmacionCuitContextVar", "", AstUid,
				"confirmacionCuitContextVar");

		cuitContextVar = this.getContextVar("cuitContextVar", "", AstUid,
				"cuitContextVar");
		cuitContextVar.setStringFormat("%11d");

		scapeDigitContextVar = this.getContextVar("scapeDigitContextVar", "",
				AstUid, "scapeDigitContextVar");

		intentosCuitContextVar = this.getContextVar("intentosCuitContextVar",
				"0", AstUid, "intentosCuitContextVar");

		confirmaCuitContextVar = this.getContextVar("confirmaCuitContextVar",
				"", AstUid, "confirmaCuitContextVar");

		numeroDeFilialContextVar = this.getContextVar(
				"numeroDeFilialContextVar", "0", AstUid,
				"numeroDeFilialContextVar");

		listaDeFilialesContextVar = this.getContextVar(
				"listaDeFilialesContextVar", "", AstUid,
				"listaDeFilialesContextVar");

		cantidadDechequesPendientesContextVar = this.getContextVar(
				"cantidadDechequesPendientesContextVar", "", AstUid,
				"cantidadDechequesPendientesContextVar");

		menuRepetirInfoContextVar = this.getContextVar(
				"menuRepetirInfoContextVar", "", AstUid,
				"menuRepetirInfoContextVar");

		menuInfoFilialContextVar = this.getContextVar(
				"menuInfoFilialContextVar", "", AstUid,
				"menuInfoFilialContextVar");

		menuInfoFilialMultipleContextVar = this.getContextVar(
				"menuInfoFilialMultipleContextVar", "", AstUid,
				"menuInfoFilialMultipleContextVar");

		filialInfoContextVar = this.getContextVar("filialInfoContextVar", "",
				AstUid, "filialInfoContextVar");
	}
}
