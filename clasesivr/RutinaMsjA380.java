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
import step.StepSendA380Message;
import step.StepSetAsteriskVariable;
import step.StepSetVariable;
import step.StepFactory.StepType;
import step.StepTimeConditionDB;

import context.ContextVar;

import workflow.Handler;

public class RutinaMsjA380 extends BaseAgiScript {
	private long idContextVar = 1;
	CallContext ctx;
	CallFlow cf;

	private StepGetAsteriskVariable obtieneFuncion;
	private StepSendA380Message enviaMensaje;
	private StepContinueOnDialPlan pasofinal;
	private ContextVar funcionContextVar;

	@Override
	public void service(AgiRequest request, AgiChannel channel)
			throws AgiException {
		this.initialize(request, channel);
		this.createContextVars(channel);
		this.createSteps();
		this.setSequence();
		ctx.setInitialStep(obtieneFuncion.GetId());

		try {
			cf.execute(ctx);
		} catch (Exception ex) {
			Logger.getLogger(RutinaMsjA380.class.getName()).log(Level.FATAL,
					null, ex);
		}
	}

	private void setSequence() {
		obtieneFuncion.setNextstep(enviaMensaje.GetId());
		enviaMensaje.setNextstep(pasofinal.GetId());
	}

	private void createSteps() {
		obtieneFuncion = (StepGetAsteriskVariable) StepFactory.createStep(
				StepType.GetAsteriskVariable, UUID.randomUUID());
		obtieneFuncion
				.setStepDescription("GETASTERISKVARIABLE => OBTIENE VARIABLE FUNCION");
		obtieneFuncion.setContextVariableName(funcionContextVar);
		obtieneFuncion.setVariableName("funcion");
		cf.addTask(obtieneFuncion);

		enviaMensaje = (StepSendA380Message) StepFactory.createStep(
				StepType.SendA380Message, UUID.randomUUID());
		enviaMensaje
				.setStepDescription("SENDA380MESSAGE => ENVIA MENSAJE A380");
		enviaMensaje.setContextVariableFuncion(funcionContextVar);
		cf.addTask(enviaMensaje);

		pasofinal = (StepContinueOnDialPlan) StepFactory.createStep(
				StepType.ContinueOnDialPlan, UUID.randomUUID());
		pasofinal
				.setStepDescription("CONTINUEONDIALPLAN => CONTINUA EN DIAL PLAN");
		cf.addTask(pasofinal);
	}

	private void createContextVars(AgiChannel channel) {
		String astUid = channel.getUniqueId();
		funcionContextVar = this.getContextVar("Variable funcion exE352", "0",
				astUid);
	}

	private void initialize(AgiRequest request, AgiChannel channel) {
		Daemon.getDbLog().addCallFlowToLog(channel.getUniqueId(),
				RutinaMsjA380.class.getName(), request.getCallerIdNumber());
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
