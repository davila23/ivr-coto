package clasesivr;

import java.lang.reflect.Field;
import java.util.UUID;

import ivr.CallContext;
import ivr.CallFlow;
import ivr.IvrExceptionHandler;

import main.Daemon;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;

import step.Step;

import workflow.Handler;
import workflow.Task;
import context.ContextVar;

public abstract class Rutina extends BaseAgiScript {

	protected CallContext ctx;
	protected long idContextVar = 1;
	protected CallFlow cf;
	protected String AstUid = "";
	protected String ast_uid = "";

	@Override
	public void service(AgiRequest request, AgiChannel channel) {
		Daemon.getDbLog().addCallFlowToLog(channel.getUniqueId(),
				this.getClassNameChild(), request.getCallerIdNumber());
		this.initialize(request, channel);
		this.createContextVars(channel);
		this.createSteps();
		this.addSteps();
		this.setSequence();
		this.addGroups();
		this.setInitialStep();
		try {
			cf.execute(ctx);
		} catch (Exception ex) {
			Logger.getLogger(TestIvr.class.getName())
					.log(Level.FATAL, null, ex);
		}
	}

	private void addSteps() {
		Field properties[] = this.getClass().getDeclaredFields();
		for (Field field : properties) {
			if (Task.class.isAssignableFrom(field.getType())) {
				try {
					cf.addTask(((Step) field.get(this)));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected abstract void addGroups();

	protected abstract String getClassNameChild();

	protected abstract void createSteps();

	protected abstract void createContextVars(AgiChannel channel);

	private void initialize(AgiRequest request, AgiChannel channel) {
		cf = new CallFlow();
		ctx = new CallContext();
		Handler manejoErrores = new IvrExceptionHandler();
		manejoErrores.setId(UUID.randomUUID());
		cf.addTask(manejoErrores);
		ctx.setChannel(channel);
		ctx.setRequest(request);
		this.setAstUid(channel.getUniqueId());
	}

	protected abstract void setSequence();

	@SuppressWarnings("unused")
	protected ContextVar getContextVar(String descrip, String initialValue,
			String ctxVarName) {
		ContextVar tmpCtxVar = new ContextVar(ctx);
		tmpCtxVar.setId(this.idContextVar++);
		tmpCtxVar.setVarDescrip(descrip);
		tmpCtxVar.setAstUid(AstUid);
		tmpCtxVar.setVarValue(initialValue);
		tmpCtxVar.setCtxVarName(ctxVarName);
		ctx.put(tmpCtxVar.getId(), tmpCtxVar);
		return tmpCtxVar;
	}

	protected abstract void setInitialStep();

	private void setAstUid(String lAstUid) {

		if (lAstUid.contains("-")) {
			ast_uid = lAstUid.split("-")[1];
		} else {
			ast_uid = lAstUid;
		}

		ast_uid = String.format("%030d", 0) + ast_uid.replaceAll("\\.", "");

		AstUid = lAstUid.trim();

	}
}
