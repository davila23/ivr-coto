package clasesivr;

import java.util.UUID;

import main.Daemon;

import org.asteriskjava.fastagi.AgiChannel;

import condition.condition;
import context.ContextVar;

import step.StepAnswer;
import step.StepConditional;
import step.StepEnd;
import step.StepExecute;
import step.StepFactory;
import step.StepGetAsteriskVariable;
import step.StepPlay;
import step.StepFactory.StepType;
import step.group.PreAtendedorBPI;
import step.group.StepGroupFactory;
import workflow.Task;

public class RutinaBPI extends Rutina {

	protected StepEnd pasoFinal;
	protected int intentos = 3;
	protected String idcrecer;
	protected ContextVar intentosMenuInicialBPIContextVar;
	protected ContextVar intentosMenuClavesContextVar;
	protected ContextVar intentosMenuTransferenciasContextVar;
	protected ContextVar intentosMenuPagosContextVar;
	protected ContextVar intentosMenuOtrasConsultasContextVar;
	protected ContextVar intentosRepetirInformacionSubMenuPagosContextVar;
	protected ContextVar intentosRepetirInformacionMenuPagosContextVar;
	protected ContextVar intentosAsesoramientoAUsuarioContextVar;
	protected ContextVar intentosOlvidoClaveContextVar;
	protected ContextVar intentosRepeticionSubMenuClavesContextVar;
	protected ContextVar menuInicialBPIContextVar;
	protected ContextVar menuClavesContextVar;
	protected ContextVar menuTransferenciasContextVar;
	protected ContextVar menuPagosContextVar;
	protected ContextVar menuOtrasConsultasContextVar;
	protected ContextVar subMenuClavesContextVar;
	protected ContextVar olvidoClaveContextVar;
	protected ContextVar asesoramientoAUsiaroContextVar;
	protected ContextVar servicioIdContextVar;
	protected ContextVar audioFueraHorarioContextVar;
	protected ContextVar subMenuTransferenciaContextVar;
	protected ContextVar subMenuPagosContextVar;
	protected ContextVar empresaIdContextVar;
	protected ContextVar subMenuTarjetaCoordenadasContextVar;
	protected ContextVar menuTarjetaCoordenadasContextVar;
	protected ContextVar intentosTarjetaCoordenadasContextVar;
	protected ContextVar intentosSubTarjetaCoordenadasContextVar;
	protected ContextVar adicionalTarjetaCoordenadasContextVar;
	protected ContextVar adicionalMovilContextVar;
	protected ContextVar confirmaDniContextVar;
	protected ContextVar intentosDniContextVar;
	protected ContextVar intentosRepetirInformacionTransferenciaContextVar;
	protected ContextVar intentosSubMenuPagosContextVar;
	protected ContextVar repetirInformacionTutorialContextVar;
	protected ContextVar evalContadorRepetirMenuTutorial;
	protected ContextVar intentosMenuTutorialContextVar;
	protected ContextVar intentosSubMenuClavesContextVar;
	protected ContextVar intentosMenuSubTarjetaCoordenadasContextVar;
	protected ContextVar scapeDigitContextVar;
	protected ContextVar intentosRepetirTutorialContextVar;

	protected StepExecute stepDerivoLlamada;
	protected StepPlay stepAudioBienvenida;
	protected StepPlay stepAudioPrevioDerivoAsesor;
	protected StepPlay stepAudioSuperoIntentos;
	protected StepPlay stepAudioFinal;
	protected PreAtendedorBPI preAtendedorBPI;

	protected ContextVar resultadoAudioInicioClearKeyContextVar;
	protected ContextVar dniContextVar;
	protected ContextVar fechaContextVar;
	protected ContextVar fdnContextVar;
	protected ContextVar diaContextVar;
	protected ContextVar mesContextVar;
	protected ContextVar anioContextVar;
	protected ContextVar confirmaFechaContextVar;
	protected ContextVar datosCuentaContextVar;
	protected ContextVar intentosFechaContextVar;
	protected ContextVar tarjetaContexVar;
	protected ContextVar repetirPINContextVar;
	protected ContextVar intentosIngresoContextVar;
	protected ContextVar fillerContexVar;
	protected ContextVar claveIngresadaContexVar;
	protected ContextVar idCrecerContextVar;
	protected ContextVar confirmaCuentaContextVar;
	protected ContextVar cuentaContextVar;
	protected ContextVar intentosCuentaContextVar;
	protected ContextVar confirmaTarjetaContextVar;
	protected ContextVar intentosTarjetaContextVar;
	protected ContextVar dnisContextVar;
	protected ContextVar reingresoContextVar;
	protected ContextVar resultadoAudioCambioDeTarjetaContextVar;
	protected ContextVar retornoClearPilContextVar;
	protected ContextVar intentosCuentaPropiaContextVar;
	protected ContextVar intentosInitContextVar;
	protected ContextVar idLlamadaContexVar;
	protected ContextVar claveContextVar;
	protected ContextVar initDbContextVar;
	protected StepConditional evalInit;
	protected StepGetAsteriskVariable obtieneTarjeta;
	protected StepGetAsteriskVariable obtieneStatus;
	protected ContextVar initDb_auxContextVar;
	protected ContextVar claveReingresoContextVar;
	protected ContextVar retornoMsgJPOS;
	protected ContextVar intentosClaveVaciaContextVar;
	protected ContextVar intentosClaveContextVar;
	protected ContextVar intentosReingresoClaveVaciaContextVar;
	protected StepGetAsteriskVariable obtieneDni;

	@Override
	protected void setInitialStep() {
		ctx.setInitialStep(obtieneStatus.GetId());

	}

	@Override
	protected void setSequence() {

		obtieneStatus.setNextstep(obtieneDni.GetId());

		obtieneDni.setNextstep(evalInit.GetId());
		
		evalInit.addCondition(new condition(1, "#{" + initDbContextVar.getVarName() + "} == " + "0",
				preAtendedorBPI.getInitialStep(), stepAudioBienvenida.GetId()));

		stepAudioBienvenida.setNextstep(preAtendedorBPI.getInitialStep());

		//

		preAtendedorBPI.setStepIfTrueUUID(stepAudioPrevioDerivoAsesor.GetId());
		preAtendedorBPI.setStepIfFalseUUID(stepAudioSuperoIntentos.GetId());

		// TRUE

		stepAudioPrevioDerivoAsesor.setNextstep(stepDerivoLlamada.GetId());

		// FALSE

		stepAudioSuperoIntentos.setNextstep(pasoFinal.GetId());

	}

	@Override
	protected void addGroups() {
		for (Task tmpTask : preAtendedorBPI.getSteps().values()) {
			cf.addTask(tmpTask);
		}
		for (Task tmpTask : preAtendedorBPI.getPideDni().getSteps().values()) {
			cf.addTask(tmpTask);
		}
		for (Task tmpTask : preAtendedorBPI.getPideFecha().getSteps().values()) {
			cf.addTask(tmpTask);
		}
		for (Task tmpTask : preAtendedorBPI.getPideTarjeta().getSteps().values()) {
			cf.addTask(tmpTask);
		}
		for (Task tmpTask : preAtendedorBPI.getPideCuenta().getSteps().values()) {
			cf.addTask(tmpTask);
		}
		for (Task tmpTask : preAtendedorBPI.getPideKeyBPI().getSteps().values()) {
			cf.addTask(tmpTask);
		}

	}

	@Override
	protected void createSteps() {
		pasoFinal = (StepEnd) StepFactory.createStep(StepType.End, UUID.randomUUID());
		pasoFinal.setStepDescription("END => FIN COMUNICACION RUTINA BPI");
		/* ---------------- Asterisk -------------- */

		/* ---------------- Grupos -------------- */

		preAtendedorBPI = (PreAtendedorBPI) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.preAtendedorBPI);
		preAtendedorBPI.setContextVar(ctx);

		/* ---------------- Audios -------------- */

		stepAudioFinal = (StepPlay) StepFactory.createStep(StepType.Play, UUID.randomUUID());
		stepAudioFinal.setStepDescription("PLAY => SALUDO FINAL RUTINA BPI");
		stepAudioFinal.setPlayfile("RUTINAPINCOP/RUTINA_PIN032");

		stepAudioBienvenida = (StepPlay) StepFactory.createStep(StepType.Play, UUID.randomUUID());
		stepAudioBienvenida.setStepDescription("PLAY => BIENVENIDA RUTINA BPI");
		stepAudioBienvenida.setPlayfile("PREATENDEDORCABAL/030");

		stepAudioSuperoIntentos = (StepPlay) StepFactory.createStep(StepType.Play, UUID.randomUUID());
		stepAudioSuperoIntentos.setPlayfile("PREATENDEDORCABAL/032");
		stepAudioSuperoIntentos.setStepDescription("PLAY => SUPERO INTENTOS RUTINA BIE");

		stepAudioPrevioDerivoAsesor = (StepPlay) StepFactory.createStep(StepType.Play, UUID.randomUUID());
		stepAudioPrevioDerivoAsesor.setPlayfile("PREATENDEDORCABAL/099");
		stepAudioPrevioDerivoAsesor.setStepDescription("PLAY => AUDIO PREVIO DERIVO ASESOR");

		/* ---------------- Conditional -------------- */

		evalInit = (StepConditional) StepFactory.createStep(StepFactory.StepType.Conditional, UUID.randomUUID());
		evalInit.setStepDescription("CONDITIONAL => EJECUTO INIT DB");

		/* ---------------- Derivo -------------- */

		stepDerivoLlamada = (StepExecute) StepFactory.createStep(StepType.Execute, UUID.randomUUID());
		stepDerivoLlamada.setApp("goto");
		stepDerivoLlamada.setAppOptions(Daemon.getConfig("DERIVOOPERADORBPI"));
		stepDerivoLlamada.setStepDescription("EXECUTE => DESVIO LLAMADA OPERADOR");

		obtieneStatus = (StepGetAsteriskVariable) StepFactory.createStep(StepType.GetAsteriskVariable,
				UUID.randomUUID());
		obtieneStatus.setContextVariableName(initDbContextVar);
		obtieneStatus.setVariableName("initDB");
		obtieneStatus.setStepDescription("GETASTERISKVARIABLE => OBTIENE INITDB BPI");
		
		obtieneDni = (StepGetAsteriskVariable) StepFactory.createStep(StepType.GetAsteriskVariable,
				UUID.randomUUID());
		obtieneDni.setContextVariableName(dniContextVar);
		obtieneDni.setVariableName("dniAux");
		obtieneDni.setStepDescription("GETASTERISKVARIABLE => OBTIENE DNI BPI");

	}

	@Override
	protected void createContextVars(AgiChannel channel) {
		/* Dni */

		dniContextVar = this.getContextVar("dniContextVar", "", "dniContextVar");

		confirmaDniContextVar = this.getContextVar("confirmaDniContextVar", "", "confirmaDniContextVar");

		intentosDniContextVar = this.getContextVar("intentosDniContextVar", "0", "intentosDniContextVar");

		intentosRepetirInformacionTransferenciaContextVar = this.getContextVar(
				"intentosRepetirInformacionTransferenciaContextVar", "0",
				"intentosRepetirInformacionTransferenciaContextVar");

		menuInicialBPIContextVar = this.getContextVar("menuInicialBPIContextVar", "", "menuInicialBPIContextVar");

		menuClavesContextVar = this.getContextVar("menuClavesContextVar", "", "menuClavesContextVar");

		menuTransferenciasContextVar = this.getContextVar("menuTransferenciasContextVar", "",
				"menuTransferenciasContextVar");

		menuPagosContextVar = this.getContextVar("menuPagosContextVar", "", "menuPagosContextVar");

		menuOtrasConsultasContextVar = this.getContextVar("menuOtrasConsultasContextVar", "",
				"menuOtrasConsultasContextVar");

		subMenuClavesContextVar = this.getContextVar("subMenuClavesContextVar", "", "subMenuClavesContextVar");

		olvidoClaveContextVar = this.getContextVar("olvidoClaveContextVar", "", "olvidoClaveContextVar");

		asesoramientoAUsiaroContextVar = this.getContextVar("asesoramientoAUsiaroContextVar", "",
				"asesoramientoAUsiaroContextVar");

		subMenuTransferenciaContextVar = this.getContextVar("subMenuTransferenciaContextVar", "",
				"subMenuTransferenciaContextVar");

		subMenuPagosContextVar = this.getContextVar("subMenuPagosContextVar", "", "subMenuPagosContextVar");

		intentosMenuInicialBPIContextVar = this.getContextVar("intentosMenuInicialBPIContextVar", "0",
				"intentosMenuInicialBPIContextVar");

		intentosMenuClavesContextVar = this.getContextVar("intentosMenuClavesContextVar", "0",
				"intentosMenuClavesContextVar");

		intentosRepeticionSubMenuClavesContextVar = this.getContextVar("intentosRepeticionSubMenuClavesContextVar", "0",
				"intentosRepeticionSubMenuClavesContextVar");

		intentosOlvidoClaveContextVar = this.getContextVar("intentosOlvidoClaveContextVar", "0",
				"intentosOlvidoClaveContextVar");

		intentosSubMenuClavesContextVar = this.getContextVar("intentosSubMenuClavesContextVar", "0",
				"intentosSubMenuClavesContextVar");

		intentosAsesoramientoAUsuarioContextVar = this.getContextVar("intentosAsesoramientoAUsuarioContextVar", "0",
				"intentosAsesoramientoAUsuarioContextVar");

		intentosMenuTransferenciasContextVar = this.getContextVar("intentosMenuTransferenciasContextVar", "0",
				"intentosMenuTransferenciasContextVar");

		intentosMenuPagosContextVar = this.getContextVar("intentosMenuPagosContextVar", "0",
				"intentosMenuPagosContextVar");

		intentosRepetirInformacionMenuPagosContextVar = this.getContextVar(
				"intentosRepetirInformacionMenuPagosContextVar", "0", "intentosRepetirInformacionMenuPagosContextVar");

		intentosRepetirInformacionSubMenuPagosContextVar = this.getContextVar(
				"intentosRepetirInformacionSubMenuPagosContextVar", "0",
				"intentosRepetirInformacionSubMenuPagosContextVar");

		intentosMenuOtrasConsultasContextVar = this.getContextVar("intentosMenuOtrasConsultasContextVar", "0",
				"intentosMenuOtrasConsultasContextVar");

		empresaIdContextVar = this.getContextVar("empresaIdContextVar", Daemon.getConfig("EMPRESAIDBPI"),
				"empresaIdContextVar");

		servicioIdContextVar = this.getContextVar("servicioIdContextVar", Daemon.getConfig("SERVICIOIDBPI"),
				"servicioIdContextVar");

		audioFueraHorarioContextVar = this.getContextVar("audioFueraHorarioContextVar", "",
				"audioFueraHorarioContextVar");

		subMenuTarjetaCoordenadasContextVar = this.getContextVar("subMenuTarjetaCoordenadasContextVar", "",
				"subMenuTarjetaCoordenadasContextVar");

		menuTarjetaCoordenadasContextVar = this.getContextVar("menuTarjetaCoordenadasContextVar", "",
				"menuTarjetaCoordenadasContextVar");

		intentosTarjetaCoordenadasContextVar = this.getContextVar("intentosTarjetaCoordenadasContextVar", "0",
				"intentosTarjetaCoordenadasContextVar");

		intentosSubTarjetaCoordenadasContextVar = this.getContextVar("intentosSubTarjetaCoordenadasContextVar", "0",
				"intentosSubTarjetaCoordenadasContextVar");

		intentosSubMenuPagosContextVar = this.getContextVar("intentosSubMenuPagosContextVar", "0",
				"intentosSubMenuPagosContextVar");

		repetirInformacionTutorialContextVar = this.getContextVar("repetirInformacionTutorialContextVar", "0",
				"repetirInformacionTutorialContextVar");

		intentosMenuTutorialContextVar = this.getContextVar("intentosMenuTutorialContextVar", "0",
				"intentosMenuTutorialContextVar");

		evalContadorRepetirMenuTutorial = this.getContextVar("evalContadorRepetirMenuTutorial", "0",
				"evalContadorRepetirMenuTutorial");

		intentosMenuSubTarjetaCoordenadasContextVar = this.getContextVar("intentosMenuSubTarjetaCoordenadasContextVar",
				"0", "intentosMenuSubTarjetaCoordenadasContextVar");

		adicionalTarjetaCoordenadasContextVar = this.getContextVar("adicionalTarjetaCoordenadasContextVar", "",
				"adicionalTarjetaCoordenadasContextVar");

		adicionalMovilContextVar = this.getContextVar("adicionalMovilContextVar", "", "adicionalMovilContextVar");

		scapeDigitContextVar = this.getContextVar("scapeDigitContextVar", "", "scapeDigitContextVar");

		intentosRepetirTutorialContextVar = this.getContextVar("intentosRepetirTutorialContextVar", "0",
				"intentosRepetirTutorialContextVar");

		claveContextVar = this.getContextVar("claveContextVar", "125800", "claveContextVar");

		idLlamadaContexVar = this.getContextVar("idLlamadaContexVar", super.ast_uid, "idLlamadaContexVar");

		intentosInitContextVar = this.getContextVar("intentosInitContextVar", "0", "intentosInitContextVar");

		intentosCuentaPropiaContextVar = this.getContextVar("intentosCuentaPropiaContextVar", "0",
				"intentosCuentaPropiaContextVar");

		resultadoAudioInicioClearKeyContextVar = this.getContextVar("resultadoAudioInicioClearKeyContextVar", "",
				"resultadoAudioInicioClearKeyContextVar");

		fechaContextVar = this.getContextVar("fechaContextVar", "", "fechaContextVar");

		fdnContextVar = this.getContextVar("fdnContextVar", "", "fdnContextVar");

		diaContextVar = this.getContextVar("diaContextVar", "", "diaContextVar");

		mesContextVar = this.getContextVar("mesContextVar", "", "mesContextVar");

		anioContextVar = this.getContextVar("anioContextVar", "", "anioContextVar");

		confirmaFechaContextVar = this.getContextVar("confirmaFechaContextVar", "", "confirmaFechaContextVar");

		datosCuentaContextVar = this.getContextVar("datosCuentaContextVar", "", "datosCuentaContextVar");

		intentosFechaContextVar = this.getContextVar("intentosFechaContextVar", "0", "intentosFechaContextVar");

		tarjetaContexVar = this.getContextVar("tarjetaContexVar", "", "tarjetaContexVar");

		repetirPINContextVar = this.getContextVar("repetirPINContextVar", "", "repetirPINContextVar");

		intentosIngresoContextVar = this.getContextVar("intentosIngresoContextVar", "0", "intentosIngresoContextVar");

		fillerContexVar = this.getContextVar("fillerContexVar", "", "fillerContexVar");

		claveIngresadaContexVar = this.getContextVar("claveIngresadaContexVar", "", "claveIngresadaContexVar");

		idCrecerContextVar = this.getContextVar("idCrecerContextVar", "", "idCrecerContextVar");

		confirmaCuentaContextVar = this.getContextVar("confirmaCuentaContextVar", "", "confirmaCuentaContextVar");

		cuentaContextVar = this.getContextVar("cuentaContextVar", "", "cuentaContextVar");

		intentosCuentaContextVar = this.getContextVar("intentosCuentaContextVar", "0", "intentosCuentaContextVar");

		confirmaTarjetaContextVar = this.getContextVar("confirmaTarjetaContextVar", "", "confirmaTarjetaContextVar");

		intentosTarjetaContextVar = this.getContextVar("intentosTarjetaContextVar", "0", "intentosTarjetaContextVar");

		dnisContextVar = this.getContextVar("dnisContextVar", "", "dnisContextVar");

		reingresoContextVar = this.getContextVar("reingresoContextVar", "", "reingresoContextVar");

		resultadoAudioCambioDeTarjetaContextVar = this.getContextVar("resultadoAudioCambioDeTarjetaContextVar", "",
				"resultadoAudioCambioDeTarjetaContextVar");

		retornoClearPilContextVar = this.getContextVar("retornoClearPilContextVar", "", "retornoClearPilContextVar");

		intentosCuentaPropiaContextVar = this.getContextVar("intentosCuentaPropiaContextVar", "0",
				"intentosCuentaPropiaContextVar");

		intentosInitContextVar = this.getContextVar("intentosInitContextVar", "0", "intentosInitContextVar");

		claveContextVar = this.getContextVar("claveContextVar", "", "claveContextVar");

		claveReingresoContextVar = this.getContextVar("claveReingresoContextVar", "", "claveReingresoContextVar");

		initDbContextVar = this.getContextVar("initDbContextVar", "", "initDbContextVar");

		initDb_auxContextVar = this.getContextVar("initDb_auxContextVar", "0", "initDb_auxContextVar");

		intentosClaveVaciaContextVar = this.getContextVar("intentosClaveVaciaContextVar", "0", "intentosClaveVaciaContextVar");
	
		intentosClaveContextVar  = this.getContextVar("intentosClaveContextVar", "0", "intentosClaveContextVar");
		
		intentosReingresoClaveVaciaContextVar = this.getContextVar("intentosReingresoClaveVaciaContextVar", "0", "intentosReingresoClaveVaciaContextVar");
	}

	@Override
	protected String getClassNameChild() {
		return this.getClass().getName();
	}

}
