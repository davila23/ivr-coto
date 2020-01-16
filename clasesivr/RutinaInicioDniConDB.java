package clasesivr;

import java.util.UUID;

import ivr.CallContext;
import ivr.CallFlow;
import ivr.IvrExceptionHandler;

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
import step.StepFactory.StepType;
import step.StepInitDniDB;

import workflow.Handler;

import context.ContextVar;

public class RutinaInicioDniConDB extends BaseAgiScript {
	private long idContextVar = 1;
	CallContext ctx;
	CallFlow cf;

	private StepGetAsteriskVariable obtieneDni;
	private StepInitDniDB obtieneDatos;
	private StepContinueOnDialPlan pasoFinal;
	private ContextVar dniContextVar;

	@Override
	public void service(AgiRequest request, AgiChannel channel)
			throws AgiException {
		this.initialize(request, channel);
		this.createContextVars(channel);
		this.createSteps();
		this.setSequence();
		ctx.setInitialStep(obtieneDni.GetId());

		try {
			cf.execute(ctx);
		} catch (Exception ex) {
			Logger.getLogger(RutinaInicioDniConDB.class.getName()).log(
					Level.FATAL, null, ex);
		}
	}

	private void initialize(AgiRequest request, AgiChannel channel) {
		Daemon.getDbLog().addCallFlowToLog(channel.getUniqueId(),
				RutinaInicioDniConDB.class.getName(),
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

	private void createContextVars(AgiChannel channel) {
		String astUid = channel.getUniqueId();
		dniContextVar = this.getContextVar("dniContextVar", "", astUid);
	}

	private void createSteps() {

		obtieneDni = (StepGetAsteriskVariable) StepFactory.createStep(
				StepType.GetAsteriskVariable, UUID.randomUUID());
		obtieneDni.setContextVariableName(dniContextVar);
		obtieneDni.setVariableName("dni");
		obtieneDni.setStepDescription("GETASTERISKVARIABLE => OBTIENE DNI");
		cf.addTask(obtieneDni);

		obtieneDatos = (StepInitDniDB) StepFactory.createStep(
				StepType.InitDniDB, UUID.randomUUID());
		obtieneDatos.setContextVarDni(dniContextVar);
		obtieneDatos
				.setStepDescription("INITDNIDB => OBTIENE DATOS A PARTIR DEL DNI");
		cf.addTask(obtieneDatos);

		pasoFinal = (StepContinueOnDialPlan) StepFactory.createStep(
				StepType.ContinueOnDialPlan, UUID.randomUUID());
		pasoFinal
				.setStepDescription("CONTINUEONDIALPLAN => CONTINUA CON DATOS DEL USUARIO");
		cf.addTask(pasoFinal);
	}

	private void setSequence() {
		obtieneDni.setNextstep(obtieneDatos.GetId());
		obtieneDatos.setNextstep(pasoFinal.GetId());

	}
}
