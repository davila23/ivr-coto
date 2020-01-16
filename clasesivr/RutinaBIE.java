package clasesivr;

import java.util.UUID;

import main.Daemon;

import org.asteriskjava.fastagi.AgiChannel;

import context.ContextVar;

import step.StepAnswer;
import step.StepEnd;
import step.StepExecute;
import step.StepFactory;
import step.StepPlay;
import step.StepFactory.StepType;
import step.group.PreAtendedorBIE;
import step.group.PreAtendedorBIE;
import step.group.StepGroupFactory;
import workflow.Task;

public class RutinaBIE extends Rutina {

	protected StepEnd pasoFinal;
	private int intentos = 3;
	private String idcrecer;
	protected StepPlay stepAudioFinal;
	private ContextVar dniContextVar;
	private ContextVar intentosMenuInicialBIEContextVar;
	private ContextVar intentosMenuClavesContextVar;
	private ContextVar intentosMenuTransferenciasContextVar;
	private ContextVar intentosMenuPagosContextVar;
	private ContextVar intentosMenuOtrasConsultasContextVar;
	protected PreAtendedorBIE preAtendedorBIE;
	protected StepPlay stepAudioSuperoIntentos;
	private ContextVar intentosRepetirInformacionSubMenuPagosContextVar;
	private ContextVar intentosRepetirInformacionMenuPagosContextVar;
	private ContextVar intentosAsesoramientoAUsuarioContextVar;
	private ContextVar intentosOlvidoClaveContextVar;
	private ContextVar intentosRepeticionSubMenuClavesContextVar;
	private ContextVar menuInicialBIEContextVar;
	private ContextVar menuClavesContextVar;
	private ContextVar menuTransferenciasContextVar;
	private ContextVar menuPagosContextVar;
	private ContextVar menuOtrasConsultasContextVar;
	private ContextVar subMenuClavesContextVar;
	private ContextVar olvidoClaveContextVar;
	private ContextVar asesoramientoAUsiaroContextVar;
	private ContextVar servicioIdContextVar;
	private ContextVar audioFueraHorarioContextVar;
	private ContextVar subMenuTransferenciaContextVar;
	private ContextVar subMenuPagosContextVar;
	private ContextVar empresaIdContextVar;
	private ContextVar subMenuTarjetaCoordenadasContextVar;
	private ContextVar menuTarjetaCoordenadasContextVar;
	private ContextVar intentosTarjetaCoordenadasContextVar;
	private ContextVar intentosSubTarjetaCoordenadasContextVar;
	private ContextVar adicionalTarjetaCoordenadasContextVar;
	private ContextVar adicionalMovilContextVar;
	private ContextVar confirmaDniContextVar;
	private ContextVar intentosDniContextVar;
	private ContextVar intentosRepetirInformacionTransferenciaContextVar;
	private ContextVar intentosSubMenuPagosContextVar;
	private ContextVar repetirInformacionTutorialContextVar;
	private ContextVar evalContadorRepetirMenuTutorial;
	private ContextVar intentosMenuTutorialContextVar;
	private ContextVar intentosSubMenuClavesContextVar;
	private ContextVar certificarPCContextVar;
	private ContextVar intentosCertificarPCContextVar;
	private ContextVar intentosRepeticionClaveEmpresaContextVar;
	protected StepPlay stepAudioBienvenida;
	private ContextVar intentosMenuSubTarjetaCoordenadasContextVar;
	private ContextVar intentosRepetirOlvidoClaveContextVar;
	private ContextVar scapeDigitContextVar;
	private ContextVar intentosMenuRepetirTutorialContextVar;

	@Override
	protected void setInitialStep() {
		ctx.setInitialStep(stepAudioBienvenida.GetId());

	}

	@Override
	protected void setSequence() {

		stepAudioBienvenida.setNextstep(preAtendedorBIE.getInitialStep());
		preAtendedorBIE.setStepIfTrueUUID(pasoFinal.GetId());
		preAtendedorBIE.setStepIfFalseUUID(stepAudioSuperoIntentos.GetId());
		stepAudioSuperoIntentos.setNextstep(pasoFinal.GetId());

	}

	@Override
	protected void addGroups() {
		for (Task tmpTask : preAtendedorBIE.getSteps().values()) {
			cf.addTask(tmpTask);
		}
		for (Task tmpTask : preAtendedorBIE.getPideDni().getSteps().values()) {
			cf.addTask(tmpTask);
		}
	}

	@Override
	protected void createSteps() {

		pasoFinal = (StepEnd) StepFactory.createStep(StepType.End,
				UUID.randomUUID());
		pasoFinal.setStepDescription("END => FIN COMUNICACION RUTINA BIE");

		/* ---------------- Asterisk -------------- */

		/* ---------------- Grupos -------------- */

		preAtendedorBIE = (PreAtendedorBIE) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.preAtendedorBIE);
		preAtendedorBIE.setContextVar(ctx);

		/* ---------------- Audios -------------- */

		stepAudioFinal = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioFinal.setStepDescription("PLAY => SALUDO FINAL RUTINA BIE");
		stepAudioFinal.setPlayfile("RUTINAPINCOP/RUTINA_PIN032");

		stepAudioBienvenida = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioBienvenida.setStepDescription("PLAY => BIENVENIDA RUTINA BIE");
		stepAudioBienvenida.setPlayfile("PREATENDEDORCABAL/031");

		stepAudioSuperoIntentos = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioSuperoIntentos.setPlayfile("PREATENDEDORCABAL/032");
		stepAudioSuperoIntentos
				.setStepDescription("PLAY => SUPERO INTENTOS RUTINA BIE");

	}

	@Override
	protected void createContextVars(AgiChannel channel) {
		/* Dni */

		dniContextVar = this.getContextVar("Dni", "", "dniContextVar");
		dniContextVar.setStringFormat("%08d");

		confirmaDniContextVar = this.getContextVar("confirmaDniContextVar", "",
				"confirmaDniContextVar");

		intentosDniContextVar = this.getContextVar("intentosDniContextVar",
				"0", "intentosDniContextVar");

		intentosRepetirInformacionTransferenciaContextVar = this.getContextVar(
				"intentosRepetirInformacionTransferenciaContextVar", "0",
				"intentosRepetirInformacionTransferenciaContextVar");

		menuInicialBIEContextVar = this.getContextVar(
				"menuInicialBIEContextVar", "", "menuInicialBIEContextVar");

		menuClavesContextVar = this.getContextVar("menuClavesContextVar", "",
				"menuClavesContextVar");

		menuTransferenciasContextVar = this.getContextVar(
				"menuTransferenciasContextVar", "",
				"menuTransferenciasContextVar");

		menuPagosContextVar = this.getContextVar("menuPagosContextVar", "",
				"menuPagosContextVar");

		menuOtrasConsultasContextVar = this.getContextVar(
				"menuOtrasConsultasContextVar", "",
				"menuOtrasConsultasContextVar");

		subMenuClavesContextVar = this.getContextVar("subMenuClavesContextVar",
				"", "subMenuClavesContextVar");

		olvidoClaveContextVar = this.getContextVar("olvidoClaveContextVar", "",
				"olvidoClaveContextVar");

		asesoramientoAUsiaroContextVar = this.getContextVar(
				"asesoramientoAUsiaroContextVar", "",
				"asesoramientoAUsiaroContextVar");

		subMenuTransferenciaContextVar = this.getContextVar(
				"subMenuTransferenciaContextVar", "",
				"subMenuTransferenciaContextVar");

		subMenuPagosContextVar = this.getContextVar("subMenuPagosContextVar",
				"", "subMenuPagosContextVar");

		intentosMenuInicialBIEContextVar = this.getContextVar(
				"intentosMenuInicialBIEContextVar", "0",
				"intentosMenuInicialBIEContextVar");

		intentosMenuClavesContextVar = this.getContextVar(
				"intentosMenuClavesContextVar", "0",
				"intentosMenuClavesContextVar");

		intentosRepeticionSubMenuClavesContextVar = this.getContextVar(
				"intentosRepeticionSubMenuClavesContextVar", "0",
				"intentosRepeticionSubMenuClavesContextVar");

		intentosOlvidoClaveContextVar = this.getContextVar(
				"intentosOlvidoClaveContextVar", "0",
				"intentosOlvidoClaveContextVar");

		intentosSubMenuClavesContextVar = this.getContextVar(
				"intentosSubMenuClavesContextVar", "0",
				"intentosSubMenuClavesContextVar");

		intentosAsesoramientoAUsuarioContextVar = this.getContextVar(
				"intentosAsesoramientoAUsuarioContextVar", "0",
				"intentosAsesoramientoAUsuarioContextVar");

		intentosMenuTransferenciasContextVar = this.getContextVar(
				"intentosMenuTransferenciasContextVar", "0",
				"intentosMenuTransferenciasContextVar");

		intentosMenuPagosContextVar = this.getContextVar(
				"intentosMenuPagosContextVar", "0",
				"intentosMenuPagosContextVar");

		intentosRepetirInformacionMenuPagosContextVar = this.getContextVar(
				"intentosRepetirInformacionMenuPagosContextVar", "0",
				"intentosRepetirInformacionMenuPagosContextVar");

		intentosRepetirInformacionSubMenuPagosContextVar = this.getContextVar(
				"intentosRepetirInformacionSubMenuPagosContextVar", "0",
				"intentosRepetirInformacionSubMenuPagosContextVar");

		intentosMenuOtrasConsultasContextVar = this.getContextVar(
				"intentosMenuOtrasConsultasContextVar", "0",
				"intentosMenuOtrasConsultasContextVar");

		empresaIdContextVar = this.getContextVar("empresaIdContextVar",
				Daemon.getConfig("EMPRESAIDBIE"), "empresaIdContextVar");

		servicioIdContextVar = this.getContextVar("servicioIdContextVar",
				Daemon.getConfig("SERVICIOIDBIE"), "servicioIdContextVar");

		audioFueraHorarioContextVar = this.getContextVar(
				"audioFueraHorarioContextVar", "",
				"audioFueraHorarioContextVar");

		subMenuTarjetaCoordenadasContextVar = this.getContextVar(
				"subMenuTarjetaCoordenadasContextVar", "",
				"subMenuTarjetaCoordenadasContextVar");

		menuTarjetaCoordenadasContextVar = this.getContextVar(
				"menuTarjetaCoordenadasContextVar", "",
				"menuTarjetaCoordenadasContextVar");

		intentosTarjetaCoordenadasContextVar = this.getContextVar(
				"intentosTarjetaCoordenadasContextVar", "0",
				"intentosTarjetaCoordenadasContextVar");

		intentosRepetirOlvidoClaveContextVar = this.getContextVar(
				"intentosRepetirOlvidoClaveContextVar", "0",
				"intentosRepetirOlvidoClaveContextVar");

		intentosMenuSubTarjetaCoordenadasContextVar = this.getContextVar(
				"intentosMenuSubTarjetaCoordenadasContextVar", "0",
				"intentosMenuSubTarjetaCoordenadasContextVar");

		intentosSubTarjetaCoordenadasContextVar = this.getContextVar(
				"intentosSubTarjetaCoordenadasContextVar", "0",
				"intentosSubTarjetaCoordenadasContextVar");

		intentosSubMenuPagosContextVar = this.getContextVar(
				"intentosSubMenuPagosContextVar", "0",
				"intentosSubMenuPagosContextVar");

		repetirInformacionTutorialContextVar = this.getContextVar(
				"repetirInformacionTutorialContextVar", "",
				"repetirInformacionTutorialContextVar");

		intentosMenuTutorialContextVar = this.getContextVar(
				"intentosMenuTutorialContextVar", "0",
				"intentosMenuTutorialContextVar");

		evalContadorRepetirMenuTutorial = this.getContextVar(
				"evalContadorRepetirMenuTutorial", "",
				"evalContadorRepetirMenuTutorial");

		intentosCertificarPCContextVar = this.getContextVar(
				"intentosCertificarPCContextVar", "0",
				"intentosCertificarPCContextVar");

		intentosRepeticionClaveEmpresaContextVar = this.getContextVar(
				"intentosRepeticionClaveEmpresaContextVar", "0",
				"intentosRepeticionClaveEmpresaContextVar");

		intentosMenuRepetirTutorialContextVar = this.getContextVar(
				"intentosMenuRepetirTutorialContextVar", "0",
				"intentosMenuRepetirTutorialContextVar");

		certificarPCContextVar = this.getContextVar("certificarPCContextVar",
				"", "certificarPCContextVar");

		adicionalTarjetaCoordenadasContextVar = this.getContextVar(
				"adicionalTarjetaCoordenadasContextVar", "",
				"adicionalTarjetaCoordenadasContextVar");

		adicionalMovilContextVar = this.getContextVar(
				"adicionalMovilContextVar", "", "adicionalMovilContextVar");

		scapeDigitContextVar = this.getContextVar("scapeDigitContextVar", "",
				"scapeDigitContextVar");
	}

	@Override
	protected String getClassNameChild() {
		return this.getClass().getName();
	}

}
