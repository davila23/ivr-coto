package clasesivr;

import ivr.CallContext;
import ivr.CallFlow;
import ivr.IvrExceptionHandler;

import java.util.UUID;

import main.Daemon;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;

import step.StepContinueOnDialPlan;
import step.StepFactory;
import step.StepGetAsteriskVariable;
import step.StepSetAsteriskVariable;
import step.StepSetVariable;
import step.StepFactory.StepType;
import step.StepTimeConditionDB;

import context.ContextVar;

import workflow.Handler;

public class RutinaHorariosFeriados extends BaseAgiScript {
	private long idContextVar = 1;
	CallContext ctx;
	CallFlow cf;

	private StepGetAsteriskVariable obtieneEmpresaIdHorario;
	private StepGetAsteriskVariable obtieneServicioIdHorario;
	private StepSetAsteriskVariable seteaFueraDeHorarioFalso;
	private StepSetAsteriskVariable seteaFueraDeHorarioVerdadero;
	private StepSetAsteriskVariable seteaAudioFueraDeHorario;
	private StepTimeConditionDB obtieneHorario;
	private StepContinueOnDialPlan pasofinal;

	private ContextVar empresaIdContextVar;
	private ContextVar servicioIdContextVar;
	private ContextVar audioFueraHorarioContextVar;
	private ContextVar fueraHorarioVerdaderoContextVar;
	private ContextVar fueraHorarioFalsoContextVar;

	@Override
	public void service(AgiRequest request, AgiChannel channel)
			throws AgiException {
		this.initialize(request, channel);
		this.createContextVars(channel);
		this.createSteps();
		this.setSequence();
		ctx.setInitialStep(obtieneEmpresaIdHorario.GetId());

		try {
			cf.execute(ctx);
		} catch (Exception ex) {
			Logger.getLogger(RutinaHorariosFeriados.class.getName()).log(
					Level.FATAL, null, ex);
		}

	}

	private void setSequence() {
		obtieneEmpresaIdHorario.setNextstep(obtieneServicioIdHorario.GetId());
		obtieneServicioIdHorario.setNextstep(obtieneHorario.GetId());
		obtieneHorario.setNextstep(seteaFueraDeHorarioVerdadero.GetId());
		obtieneHorario.setNextStepIsFalse(seteaFueraDeHorarioFalso.GetId());
		obtieneHorario.setNextStepIsTrue(seteaFueraDeHorarioVerdadero.GetId());
		seteaFueraDeHorarioVerdadero.setNextstep(seteaAudioFueraDeHorario
				.GetId());
		seteaAudioFueraDeHorario.setNextstep(pasofinal.GetId());
		seteaFueraDeHorarioFalso.setNextstep(pasofinal.GetId());

	}

	private void createSteps() {
		obtieneEmpresaIdHorario = (StepGetAsteriskVariable) StepFactory
				.createStep(StepType.GetAsteriskVariable, UUID.randomUUID());
		obtieneEmpresaIdHorario
				.setStepDescription("GETASTERISKVARIABLE => OBTIENE EMPRESAIDHORARIO");
		obtieneEmpresaIdHorario.setContextVariableName(empresaIdContextVar);
		obtieneEmpresaIdHorario.setVariableName("EMPRESAIDHORARIO");
		cf.addTask(obtieneEmpresaIdHorario);

		obtieneServicioIdHorario = (StepGetAsteriskVariable) StepFactory
				.createStep(StepType.GetAsteriskVariable, UUID.randomUUID());
		obtieneServicioIdHorario
				.setStepDescription("GETASTERISKVARIABLE => OBTIENE SERVICIOIDHORARIO");
		obtieneServicioIdHorario.setContextVariableName(servicioIdContextVar);
		obtieneServicioIdHorario.setVariableName("SERVICIOIDHORARIO");
		cf.addTask(obtieneServicioIdHorario);

		seteaAudioFueraDeHorario = (StepSetAsteriskVariable) StepFactory
				.createStep(StepType.SetAsteriskVariable, UUID.randomUUID());
		seteaAudioFueraDeHorario
				.setStepDescription("SETASTERISKVARIABLE => SETEA AUDIOFUERAHORARIO");
		seteaAudioFueraDeHorario
				.setContextVariableName(audioFueraHorarioContextVar);
		seteaAudioFueraDeHorario.setVariableName("AUDIOFUERAHORARIO");
		cf.addTask(seteaAudioFueraDeHorario);

		seteaFueraDeHorarioVerdadero = (StepSetAsteriskVariable) StepFactory
				.createStep(StepType.SetAsteriskVariable, UUID.randomUUID());
		seteaFueraDeHorarioVerdadero
				.setStepDescription("SETASTERISKVARIABLE => FUERA DE HORARIO , VERDADERO");
		seteaFueraDeHorarioVerdadero
				.setContextVariableName(fueraHorarioVerdaderoContextVar);
		seteaFueraDeHorarioVerdadero.setVariableName("FUERAHORARIO");
		cf.addTask(seteaFueraDeHorarioVerdadero);

		seteaFueraDeHorarioFalso = (StepSetAsteriskVariable) StepFactory
				.createStep(StepType.SetAsteriskVariable, UUID.randomUUID());
		seteaFueraDeHorarioFalso
				.setStepDescription("SETASTERISKVARIABLE => FUERA DE HORARIO , FALSO");
		seteaFueraDeHorarioFalso
				.setContextVariableName(fueraHorarioFalsoContextVar);
		seteaFueraDeHorarioFalso.setVariableName("FUERAHORARIO");
		cf.addTask(seteaFueraDeHorarioFalso);

		obtieneHorario = (StepTimeConditionDB) StepFactory.createStep(
				StepType.TimeConditionDB, UUID.randomUUID());
		obtieneHorario
				.setStepDescription("TIMECONDITIONDB => OBTIENE HORARIO DE LA BASE");
		obtieneHorario.setContextVarEmpresa(empresaIdContextVar);
		obtieneHorario.setContextVarServicio(servicioIdContextVar);
		obtieneHorario.setContextVarAudio(audioFueraHorarioContextVar);
		cf.addTask(obtieneHorario);

		pasofinal = (StepContinueOnDialPlan) StepFactory.createStep(
				StepType.ContinueOnDialPlan, UUID.randomUUID());
		pasofinal.setStepDescription("Final de rutina Horario");
		cf.addTask(pasofinal);
	}

	private void createContextVars(AgiChannel channel) {
		String astUid = channel.getUniqueId();
		empresaIdContextVar = this.getContextVar("Id Empresa Horario", "0",
				astUid);
		servicioIdContextVar = this.getContextVar("Id Servicio Horario", "0",
				astUid);
		audioFueraHorarioContextVar = this.getContextVar("Audio Fuera Horario",
				"", astUid);
		fueraHorarioVerdaderoContextVar = this.getContextVar(
				"Fuera Horario verdadero", "1", astUid);
		fueraHorarioFalsoContextVar = this.getContextVar("Fuera Horario falso",
				"0", astUid);

	}

	private void initialize(AgiRequest request, AgiChannel channel) {
		Daemon.getDbLog().addCallFlowToLog(channel.getUniqueId(),
				RutinaHorariosFeriados.class.getName(),
				request.getCallerIdNumber());
		cf = new CallFlow();
		ctx = new CallContext();
		Handler manejoErrores = new IvrExceptionHandler();
		manejoErrores.setId(UUID.randomUUID());
		cf.addTask(manejoErrores);
		ctx.setChannel(channel);
		ctx.setRequest(request);
	}

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

}
