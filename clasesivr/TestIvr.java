package clasesivr;

import ivr.CallContext;
import ivr.CallFlow;
import ivr.IvrExceptionHandler;

import java.util.UUID;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;

import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;

import condition.condition;
import context.ContextVar;
import step.*;
import step.StepFactory.StepType;
import workflow.Handler;

public class TestIvr extends BaseAgiScript {

	@Override
	public void service(AgiRequest request, AgiChannel channel) {
		CallContext ctx = new CallContext();

		ContextVar resultadoMenuInicial = new ContextVar(ctx);
		resultadoMenuInicial.setId(1L);
		resultadoMenuInicial.setVarDescrip("MenuInicial");

		ContextVar IntentosInicial = new ContextVar(ctx);
		IntentosInicial.setId(2L);
		IntentosInicial.setVarDescrip("IntentosMenuInicial");
		IntentosInicial.setVarValue("0");

		StepEnd pasoFinal = (StepEnd) StepFactory.createStep(StepType.End,
				UUID.randomUUID());

		StepAnswer Inicial = (StepAnswer) StepFactory.createStep(
				StepType.Answer, UUID.randomUUID());

		StepPlayRead AudioMenuInicial = (StepPlayRead) StepFactory.createStep(
				StepType.PlayRead, UUID.randomUUID());
		AudioMenuInicial.setContextVariableName(resultadoMenuInicial);
		AudioMenuInicial.setPlayFile("demo-congrats");
		AudioMenuInicial.setPlayMaxDigits(1);

		StepCounter contadorMenuInicial = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorMenuInicial.setContextVariableName(IntentosInicial);

		StepMenu MenuInicial = (StepMenu) StepFactory.createStep(StepType.Menu,
				UUID.randomUUID());
		MenuInicial.addSteps("1", AudioMenuInicial.GetId());
		MenuInicial.addSteps("2", AudioMenuInicial.GetId());
		MenuInicial.addSteps("3", pasoFinal.GetId());

		MenuInicial.setContextVariableName(resultadoMenuInicial);

		StepConditional evalContadorInicial = (StepConditional) StepFactory
				.createStep(StepFactory.StepType.Conditional, UUID.randomUUID());

		evalContadorInicial.addCondition(new condition(1, "#{"
				+ IntentosInicial.getVarName() + "} <= 2", AudioMenuInicial
				.GetId(), pasoFinal.GetId()));

		/* Sequencia */
		Inicial.setNextstep(AudioMenuInicial.GetId());
		AudioMenuInicial.setNextstep(MenuInicial.GetId());
		contadorMenuInicial.setNextstep(evalContadorInicial.GetId());
		MenuInicial.setInvalidOption(contadorMenuInicial.GetId());
		evalContadorInicial.setNextstep(pasoFinal.GetId());

		ctx.put(resultadoMenuInicial.getId(), resultadoMenuInicial);
		ctx.put(IntentosInicial.getId(), IntentosInicial);
		ctx.setChannel(channel);
		ctx.setRequest(request);
		ctx.setInitialStep(Inicial.GetId());

		Handler manejoErrores = new IvrExceptionHandler();
		manejoErrores.setId(UUID.randomUUID());

		CallFlow cf = new CallFlow();
		cf.addTask(manejoErrores);
		cf.addTask(Inicial);
		cf.addTask(AudioMenuInicial);
		cf.addTask(MenuInicial);
		cf.addTask(pasoFinal);
		cf.addTask(evalContadorInicial);
		cf.addTask(contadorMenuInicial);

		try {
			cf.execute(ctx);
		} catch (Exception ex) {
			Logger.getLogger(TestIvr.class.getName())
					.log(Level.FATAL, null, ex);
		}
	}

}
