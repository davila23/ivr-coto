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
import step.group.PideDni;
import step.group.PideDniCredicoop;
import step.group.PideFecha;
import step.group.PideFechaBCCL;
import step.group.PideFechaCredicoop;
import step.group.PideTarjetaCredicoop;
import step.group.PreAtendedorBIE;
import step.group.StepGroupFactory;
import workflow.Handler;
import workflow.Task;
import condition.condition;
import context.ContextVar;

public class RutinaPideFecha extends BaseAgiScript {

	private long idContextVar = 1;
	CallContext ctx;
	CallFlow cf;
	private StepEnd pasoFinal;
	private StepPlay stepAudioSuperoIntentos;
	private PideFechaBCCL pideFechaGrp;
	private ContextVar fechaContextVar;
	private ContextVar diaContextVar;
	private ContextVar mesContextVar;
	private ContextVar anioContextVar;
	private ContextVar confirmaFechaContextVar;
	private ContextVar intentosFechaContextVar;
	private StepContinueOnDialPlan continuaDialPLan;
	private StepSetAsteriskVariable stepSetFecha;

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

		pideFechaGrp.setStepIfTrue(stepSetFecha.GetId());
		pideFechaGrp.setStepIfFalse(pasoFinal.GetId());

		stepAudioSuperoIntentos.setNextstep(pasoFinal.GetId());

		stepSetFecha.setNextstep(continuaDialPLan.GetId());

	}

	@Override
	public void service(AgiRequest request, AgiChannel channel) {
		Daemon.getDbLog().addCallFlowToLog(channel.getUniqueId(),
				RutinaPideFecha.class.getName(), request.getCallerIdNumber());
		this.initialize(request, channel);
		this.createContextVars(channel);
		this.createSteps();
		this.setSequence();

		cf.addTask(stepAudioSuperoIntentos);
		cf.addTask(stepSetFecha);
		cf.addTask(continuaDialPLan);
		cf.addTask(pasoFinal);

		for (Task tmpTask : pideFechaGrp.getSteps().values()) {
			cf.addTask(tmpTask);
		}

		ctx.setInitialStep(pideFechaGrp.getInitialStep());

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

		pideFechaGrp = (PideFechaBCCL) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.pideFechaBCCL);
		pideFechaGrp.setAudioFecha("RUTINAPINCOP/RUTINA_PIN010");
		pideFechaGrp.setAudioValidateFecha("RUTINAPINCOP/RUTINA_PIN009");
		pideFechaGrp.setAudioSuFechaEs("RUTINAPINCOP/RUTINA_PIN015");
		pideFechaGrp.setAudioAnio("RUTINAPINCOP/RUTINA_PIN008");
		pideFechaGrp.setAudioMes("RUTINAPINCOP/RUTINA_PIN007");
		pideFechaGrp.setAudioDia("RUTINAPINCOP/RUTINA_PIN006");
		pideFechaGrp.setAudioFechaInvalida("RUTINAPINCOP/RUTINA_PIN013");
		pideFechaGrp.setfechaContextVar(fechaContextVar);
		pideFechaGrp.setContextVarDia(diaContextVar);
		pideFechaGrp.setContextVarMes(mesContextVar);
		pideFechaGrp.setContextVarAnio(anioContextVar);
		pideFechaGrp.setConfirmaFechaContextVar(confirmaFechaContextVar);
		pideFechaGrp.setIntentosFechaContextVar(intentosFechaContextVar);

		pasoFinal = (StepEnd) StepFactory.createStep(StepType.End,
				UUID.randomUUID());
		pasoFinal.setStepDescription("END => FIN PIDE FECHA");

		stepAudioSuperoIntentos = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioSuperoIntentos.setPlayfile("RUTINAPINCOP/RUTINA_PIN024");
		stepAudioSuperoIntentos
				.setStepDescription("PLAY => SUPERO INTENTOS FECHA");

		continuaDialPLan = (StepContinueOnDialPlan) StepFactory.createStep(
				StepType.ContinueOnDialPlan, UUID.randomUUID());
		continuaDialPLan
				.setStepDescription("CONTINUEONDIALPLAN => CONTINUA EN DIALPLAN");

		stepSetFecha = (StepSetAsteriskVariable) StepFactory.createStep(
				StepType.SetAsteriskVariable, UUID.randomUUID());
		stepSetFecha
				.setStepDescription("SETASTERISKVARIABLE => SET FECHA DE NACIMIENTO");
		stepSetFecha.setContextVariableName(fechaContextVar);
		stepSetFecha.setVariableName("macrovv");
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

		fechaContextVar = this.getContextVar("fechaContextVar", "", AstUid);

		diaContextVar = this.getContextVar("diaContextVar", "", AstUid);

		mesContextVar = this.getContextVar("fechaContextVar", "", AstUid);

		anioContextVar = this.getContextVar("fechaContextVar", "", AstUid);

		confirmaFechaContextVar = this.getContextVar("Confirma Fecha", "",
				AstUid);

		intentosFechaContextVar = this.getContextVar("Intentos Fecha", "0",
				AstUid);

	}

}
