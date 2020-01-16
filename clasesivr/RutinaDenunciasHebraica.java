package clasesivr;

import ivr.CallContext;
import ivr.CallFlow;
import ivr.IvrExceptionHandler;
import java.util.UUID;
import main.Daemon;
import net.sourceforge.jeval.function.string.Substring;

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
import step.StepLimitPlayRead;
import step.StepMenu;
import step.StepParseDenunciasTarjeta;
import step.StepSayMonth;
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
import step.group.PideDni;
import step.group.PideFecha;
import step.group.PideFechaDenuncia;
import step.group.PideTarjeta;
import step.group.PrecargadasCabalDenunciaIvr;
import step.group.StepGroupFactory;
import workflow.Handler;
import workflow.Task;
import condition.condition;
import context.ContextVar;

public class RutinaDenunciasHebraica extends BaseAgiScript {

	protected long idContextVar = 1;
	CallContext ctx;
	CallFlow cf;
	protected StepAnswer inicial;
	protected StepEnd pasoFinal;
	protected int intentos = 3;
	protected String idcrecer;
	protected StepPlay stepAudioFinal;
	protected ContextVar retornoMsgJPOS;
	protected ContextVar sucursalContextVar;
	protected ContextVar disponibleDeComprasContextVar;
	protected ContextVar disponibleDeComprasDecimalContextVar;
	protected ContextVar numeroDeDenunciaContextVar;
	protected ContextVar fechaDeEntregaContextVar;
	protected PrecargadasCabalDenunciaIvr PdenunciaCabalGgp;
	protected StepPlay stepAudioSuperoIntentos;
	protected StepPlay stepAudioBienvenida;
	protected ContextVar subMenuDenunciaContextVar;
	protected ContextVar menuDenunciaContextVar;
	protected ContextVar dniContextVar;
	protected ContextVar intentosDniContextVar;
	protected ContextVar confirmaDniContextVar;
	protected ContextVar tarjetaContexVar;
	protected ContextVar fechaContextVar;
	protected ContextVar diaContextVar;
	protected ContextVar mesContextVar;
	protected ContextVar anioContextVar;
	protected ContextVar confirmaFechaContextVar;
	protected ContextVar intentosFechaContextVar;
	protected ContextVar audioFueraHorarioContextVar;
	protected ContextVar servicioIdContextVar;
	protected ContextVar empresaIdContextVar;
	protected ContextVar retornoJPOS;
	protected ContextVar fillerParaDenunciaContexVar;
	protected ContextVar idLlamadaContexVar;
	protected ContextVar whisperContextVar;
	protected ContextVar codigoOperacionDenunciaContextVar;
	protected ContextVar menuDenunciasContextVar;
	protected ContextVar envioServerJposConsultasContexVar;
	protected ContextVar envioServerJposPrecargadasContexVar;
	protected ContextVar envioServerJposAutorizacionesContexVar;
	protected ContextVar dniContexVar;
	protected ContextVar tarjetaContextVar;
	protected StepSwitch evalRetJPOS;
	protected StepSendJPOS enviaTramaJpos;
	protected StepLimitPlayRead stepAudioMenuDenuncias;
	protected StepPlay stepAudioIngresoIncorrectoMenuDenuncias;
	protected StepMenu stepMenuDenuncias;
	protected StepPlay stepConectorParteDecimal;
	protected StepPlay stepAudioDireccionReposicion;
	protected StepPlay stepAudioFechaReposicion;
	protected StepPlay stepAudioNumeroDenuncia;
	protected StepPlay stepAudioSeraTransladado;
	protected StepPlay stepAudioDisuadeDerivoAsesor;
	protected StepPlay stepAudioPrevioDerivoAsesor;
	protected StepPlay stepAudioServNoDisponible;
	protected StepPlay stepAudioIngresoIncorrectoSubMenuDenuncias;
	protected StepPlay stepAudioVerifiqueNumeroDni;
	protected StepPlay stepAudioVerifiqueFechaNacimiento;
	protected StepPlay stepAudioNroDocumentoInexistente;
	protected StepPlay stepAudioTarjetaBloqueada;
	protected StepPlay stepAudioDatosNoCoinciden;
	protected StepPlay stepAudioNroTarjVencida;
	protected StepPlay stepAudioDatosIncorrectos;
	protected StepPlay stepAudioNroTarjIncorrecto;
	protected StepSayMonth stepFechaReposicion;
	protected StepSayNumber stepNumeroDenuncia;
	protected StepSayNumber stepNumberSaldo;
	protected StepSayNumber stepNumberoSaldoDecimalTransladado;
	protected StepLimitPlayRead stepAudioSubMenuDenuncias;
	protected PideFechaDenuncia pideFechaGrp;
	protected PideDni pideDniGrp;
	protected StepTimeConditionDB obtieneHorario;
	protected StepParseDenunciasTarjeta parserDenunciaTarjeta;
	protected StepExecute stepDerivoLlamada;
	protected StepMenu stepSubMenuDenuncias;
	protected ContextVar anioDenunciaContextVar;
	protected StepCounter contadorIntentosDNIJPOS;
	protected StepCounter contadorIntentosFechaJPOS;
	protected StepConditional evalContadorDNIJPOS;
	protected StepConditional evalContadorFechaJPOS;
	protected StepPlay stepAudioDniIncorrecto;
	protected StepPlay stepAudioFechaIncorrecta;
	protected ContextVar intentosJPOSContextVar;
	protected StepCounter contadorIntentosPOS;
	protected StepConditional evalContadorJPOS;

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

		stepAudioBienvenida.setNextstep(pideDniGrp.getInitialStep());

		/* Pide DNI */

		pideDniGrp.setStepIfTrue(pideFechaGrp.getInitialStep());
		pideDniGrp.setStepIfFalse(stepAudioVerifiqueNumeroDni.GetId());

		/* Pide Fecha */

		pideFechaGrp.setStepIfTrue(stepAudioMenuDenuncias.GetId());
		pideFechaGrp.setStepIfFalse(stepAudioVerifiqueFechaNacimiento.GetId());

		/* Menu Denuncia Por IVR */

		stepAudioMenuDenuncias.setNextstep(stepMenuDenuncias.GetId());
		stepAudioMenuDenuncias
				.setNextStepIfAttemptLimit(stepAudioFinal.GetId());

		stepMenuDenuncias.addSteps("1", enviaTramaJpos.GetId());
		stepMenuDenuncias.addSteps("2", stepAudioFinal.GetId());
		stepMenuDenuncias
				.setInvalidOption(stepAudioIngresoIncorrectoMenuDenuncias
						.GetId());

		stepAudioIngresoIncorrectoMenuDenuncias
				.setNextstep(stepAudioMenuDenuncias.GetId());

		/* Retorno 00 */

		parserDenunciaTarjeta.setNextstep(stepAudioTarjetaBloqueada.GetId());

		stepAudioTarjetaBloqueada.setNextstep(stepNumberSaldo.GetId());

		stepNumberSaldo.setNextstep(stepAudioSeraTransladado.GetId());

		stepAudioSeraTransladado.setNextstep(stepAudioNumeroDenuncia.GetId());

		stepAudioNumeroDenuncia.setNextstep(stepNumeroDenuncia.GetId());

		stepNumeroDenuncia.setNextstep(stepAudioDireccionReposicion.GetId());

		stepAudioDireccionReposicion.setNextstep(stepAudioSubMenuDenuncias
				.GetId());

		stepAudioSubMenuDenuncias.setNextstep(stepSubMenuDenuncias.GetId());

		stepAudioSubMenuDenuncias.setNextStepIfAttemptLimit(stepAudioFinal
				.GetId());

		stepSubMenuDenuncias.addSteps("1", stepAudioTarjetaBloqueada.GetId());
		stepSubMenuDenuncias.addSteps("9", stepAudioFinal.GetId());
		stepSubMenuDenuncias
				.setInvalidOption(stepAudioIngresoIncorrectoSubMenuDenuncias
						.GetId());

		stepAudioIngresoIncorrectoSubMenuDenuncias
				.setNextstep(stepAudioSubMenuDenuncias.GetId());

		/* JPOS */

		// stepAudioDniIncorrecto.setNextstep(contadorIntentosDNIJPOS.GetId());
		//
		// contadorIntentosDNIJPOS.setNextstep(evalContadorDNIJPOS.GetId());
		// evalContadorDNIJPOS
		// .addCondition(new condition(1, "#{"
		// + intentosDniContextVar.getVarName() + "} < "
		// + intentos, pideDniGrp.getInitialStep(),
		// stepAudioDatosNoCoinciden.GetId()));
		//
		// stepAudioFechaIncorrecta.setNextstep(contadorIntentosFechaJPOS.GetId());
		//
		// contadorIntentosFechaJPOS.setNextstep(evalContadorFechaJPOS.GetId());
		// evalContadorFechaJPOS.addCondition(new condition(1, "#{"
		// + intentosFechaContextVar.getVarName() + "} < " + intentos,
		// pideFechaGrp.getInitialStep(), stepAudioDatosNoCoinciden
		// .GetId()));

		contadorIntentosPOS.setNextstep(evalContadorJPOS.GetId());
		evalContadorJPOS.addCondition(new condition(1, "#{"
				+ intentosJPOSContextVar.getVarName() + "} < " + intentos,
				stepAudioDatosIncorrectos.GetId(), stepAudioDatosNoCoinciden
						.GetId()));

		stepAudioDatosIncorrectos.setNextstep(pideDniGrp.getInitialStep());

		/* Secuencias comunes */

		enviaTramaJpos.setNextstep(evalRetJPOS.GetId());

		stepAudioVerifiqueNumeroDni.setNextstep(stepAudioFinal.GetId());

		stepAudioVerifiqueFechaNacimiento.setNextstep(stepAudioFinal.GetId());

		stepAudioDatosNoCoinciden.setNextstep(stepAudioFinal.GetId());
		stepAudioServNoDisponible.setNextstep(stepAudioFinal.GetId());
		stepAudioFinal.setNextstep(pasoFinal.GetId());

		// stepAudioSuperoIntentos.setNextstep(pasoFinal.GetId());
		stepAudioFinal.setNextstep(pasoFinal.GetId());

	}

	@Override
	public void service(AgiRequest request, AgiChannel channel) {
		Daemon.getDbLog().addCallFlowToLog(channel.getUniqueId(),
				RutinaDenunciasHebraica.class.getName(),
				request.getCallerIdNumber());
		this.initialize(request, channel);
		this.createContextVars(channel);
		this.createSteps();
		this.setSequence();

		for (Task tmpTask : pideDniGrp.getSteps().values()) {
			cf.addTask(tmpTask);
		}

		for (Task tmpTask : pideFechaGrp.getSteps().values()) {
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

		/* FIN */

		pasoFinal = (StepEnd) StepFactory.createStep(StepType.End,
				UUID.randomUUID());
		pasoFinal
				.setStepDescription("END => FIN COMUNICACION RUTINA TARJETA HEBRAICA");
		cf.addTask(pasoFinal);

		/* Limit play Read */

		stepAudioSubMenuDenuncias = (StepLimitPlayRead) StepFactory.createStep(
				StepType.LimitPlayRead, UUID.randomUUID());
		stepAudioSubMenuDenuncias.setPlayFile("HEBRAICA/H000013");
		stepAudioSubMenuDenuncias
				.setContextVariableName(subMenuDenunciaContextVar);
		stepAudioSubMenuDenuncias.setIntentos(intentos);
		stepAudioSubMenuDenuncias
				.setStepDescription("LIMITPLAYREAD => MENU DENUNCIA");
		cf.addTask(stepAudioSubMenuDenuncias);

		stepAudioMenuDenuncias = (StepLimitPlayRead) StepFactory.createStep(
				StepType.LimitPlayRead, UUID.randomUUID());
		stepAudioMenuDenuncias.setPlayFile("HEBRAICA/H000007");
		stepAudioMenuDenuncias.setContextVariableName(menuDenunciasContextVar);
		stepAudioMenuDenuncias.setIntentos(intentos);
		stepAudioMenuDenuncias
				.setStepDescription("LIMITPLAYREAD => MENU DENUNCIA");
		cf.addTask(stepAudioMenuDenuncias);

		/* Menu */

		stepSubMenuDenuncias = (StepMenu) StepFactory.createStep(StepType.Menu,
				UUID.randomUUID());
		stepSubMenuDenuncias.setStepDescription("MENU => SUB MENU DENUNCIA");
		stepSubMenuDenuncias.setContextVariableName(subMenuDenunciaContextVar);
		cf.addTask(stepSubMenuDenuncias);

		stepMenuDenuncias = (StepMenu) StepFactory.createStep(StepType.Menu,
				UUID.randomUUID());
		stepMenuDenuncias.setContextVariableName(menuDenunciasContextVar);
		stepMenuDenuncias.setStepDescription("MENU => MENU DENUNCIA");
		cf.addTask(stepMenuDenuncias);

		/* Play */

		stepAudioDniIncorrecto = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioDniIncorrecto.setPlayfile("RUTINAPIN/RUTINA_PIN011");
		stepAudioDniIncorrecto.setStepDescription("PLAY => DNI INCORRECTO");
		cf.addTask(stepAudioDniIncorrecto);

		stepAudioFechaIncorrecta = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioFechaIncorrecta.setPlayfile("RUTINAPIN/RUTINA_PIN013");
		stepAudioFechaIncorrecta.setStepDescription("PLAY => FECHA INCORRECTA");
		cf.addTask(stepAudioFechaIncorrecta);

		stepAudioIngresoIncorrectoMenuDenuncias = (StepPlay) StepFactory
				.createStep(StepType.Play, UUID.randomUUID());
		stepAudioIngresoIncorrectoMenuDenuncias
				.setPlayfile("RUTINAPIN/RUTINA_PIN021");
		stepAudioIngresoIncorrectoMenuDenuncias
				.setStepDescription("PLAY => INGRESO NULO O INCORRECTO - MENU DENUNCIA");
		cf.addTask(stepAudioIngresoIncorrectoMenuDenuncias);

		stepAudioFinal = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioFinal.setPlayfile("HEBRAICA/D000001");
		stepAudioFinal
				.setStepDescription("PLAY => SALUDO FINAL RUTINA HEBRAICA");
		cf.addTask(stepAudioFinal);

		stepConectorParteDecimal = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepConectorParteDecimal.setStepDescription("PLAY => CON");
		stepConectorParteDecimal.setPlayfile("coto/CON");
		cf.addTask(stepConectorParteDecimal);

		stepAudioDireccionReposicion = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioDireccionReposicion
				.setStepDescription("PLAY => DIRECCION REPOSICION");
		stepAudioDireccionReposicion.setPlayfile("HEBRAICA/H000012");
		cf.addTask(stepAudioDireccionReposicion);

		stepAudioFechaReposicion = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioFechaReposicion.setPlayfile("PREATENDEDORCABAL/099");
		stepAudioFechaReposicion.setStepDescription("PLAY => FECHA REPOSICION");
		cf.addTask(stepAudioFechaReposicion);

		stepAudioNumeroDenuncia = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioNumeroDenuncia.setPlayfile("HEBRAICA/H000011");
		stepAudioNumeroDenuncia
				.setStepDescription("PLAY => NUMERO DE DENUNCIA");
		cf.addTask(stepAudioNumeroDenuncia);

		stepAudioBienvenida = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioBienvenida.setPlayfile("HEBRAICA/B000001");
		stepAudioBienvenida
				.setStepDescription("PLAY => BIENVENIDA DENUNCIA HEBRAICA");
		cf.addTask(stepAudioBienvenida);

		stepAudioSeraTransladado = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioSeraTransladado.setPlayfile("HEBRAICA/H000010");
		stepAudioSeraTransladado
				.setStepDescription("PLAY => SALDO SERA TRANSLADADO A NUEVA TARJETA");
		cf.addTask(stepAudioSeraTransladado);

		stepAudioDisuadeDerivoAsesor = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioDisuadeDerivoAsesor.setPlayfile("PREATENDEDORCABAL/099");
		stepAudioDisuadeDerivoAsesor
				.setStepDescription("PLAY => DISUADE DERIVO ASESOR");
		cf.addTask(stepAudioDisuadeDerivoAsesor);

		stepAudioPrevioDerivoAsesor = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioPrevioDerivoAsesor.setPlayfile("PREATENDEDORCABAL/099");
		stepAudioPrevioDerivoAsesor
				.setStepDescription("PLAY => PREVIO DERIVO ASESOR");
		cf.addTask(stepAudioPrevioDerivoAsesor);

		stepAudioServNoDisponible = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioServNoDisponible.setPlayfile("RUTINAPIN/RUTINA_PIN021");
		stepAudioServNoDisponible
				.setStepDescription("PLAY => SERVICIO NO DISPONIBLE");
		cf.addTask(stepAudioServNoDisponible);

		stepAudioIngresoIncorrectoSubMenuDenuncias = (StepPlay) StepFactory
				.createStep(StepType.Play, UUID.randomUUID());
		stepAudioIngresoIncorrectoSubMenuDenuncias
				.setPlayfile("RUTINAPIN/RUTINA_PIN021");
		stepAudioIngresoIncorrectoSubMenuDenuncias
				.setStepDescription("PLAY => INGRESO NULO O INCORRECTO - SUB MENU DENUNCIA");
		cf.addTask(stepAudioIngresoIncorrectoSubMenuDenuncias);

		stepAudioVerifiqueNumeroDni = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioVerifiqueNumeroDni.setPlayfile("RUTINAPIN/RUTINA_PIN012");
		stepAudioVerifiqueNumeroDni
				.setStepDescription("PLAY => VERIFICAR NUMERO DE DNI");
		cf.addTask(stepAudioVerifiqueNumeroDni);

		stepAudioVerifiqueFechaNacimiento = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioVerifiqueFechaNacimiento
				.setPlayfile("RUTINAPIN/RUTINA_PIN012");
		stepAudioVerifiqueFechaNacimiento
				.setStepDescription("PLAY => FECHA INCORRECTA");
		cf.addTask(stepAudioVerifiqueFechaNacimiento);

		stepAudioNroDocumentoInexistente = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioNroDocumentoInexistente.setPlayfile("RUTINAPIN/RUTINA_PIN012");
		stepAudioNroDocumentoInexistente
				.setStepDescription("PLAY => DOCUMENTO INEXISTENTE");
		cf.addTask(stepAudioNroDocumentoInexistente);

		stepAudioTarjetaBloqueada = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioTarjetaBloqueada.setPlayfile("HEBRAICA/H000009");
		stepAudioTarjetaBloqueada
				.setStepDescription("PLAY => TARJETA BLOQUEADA");
		cf.addTask(stepAudioTarjetaBloqueada);

		stepAudioDatosNoCoinciden = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioDatosNoCoinciden.setPlayfile("HEBRAICA/H000014");
		stepAudioDatosNoCoinciden
				.setStepDescription("PLAY => DATOS NO COINCIDEN");
		cf.addTask(stepAudioDatosNoCoinciden);

		stepAudioNroTarjVencida = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioNroTarjVencida.setPlayfile("RUTINAPIN/RUTINA_PIN021");
		stepAudioNroTarjVencida.setStepDescription("PLAY => TARJETA VENCIDA");
		cf.addTask(stepAudioNroTarjVencida);

		stepAudioDatosIncorrectos = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioDatosIncorrectos.setPlayfile("HEBRAICA/H000008");
		stepAudioDatosIncorrectos
				.setStepDescription("PLAY => DATOS INCORRECTOS");
		cf.addTask(stepAudioDatosIncorrectos);

		stepAudioNroTarjIncorrecto = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioNroTarjIncorrecto.setPlayfile("RUTINAPIN/RUTINA_PIN021");
		stepAudioNroTarjIncorrecto
				.setStepDescription("PLAY => TARJETA INCORRECTA");
		cf.addTask(stepAudioNroTarjIncorrecto);

		/* Say Number */

		stepNumeroDenuncia = (StepSayNumber) StepFactory.createStep(
				StepType.SayNumber, UUID.randomUUID());
		stepNumeroDenuncia.setContextVariableName(numeroDeDenunciaContextVar);
		stepNumeroDenuncia.setStepDescription("SAYNUMBER => NUMERO DENUNCIA");
		cf.addTask(stepNumeroDenuncia);

		stepNumberSaldo = (StepSayNumber) StepFactory.createStep(
				StepType.SayNumber, UUID.randomUUID());
		stepNumberSaldo.setContextVariableName(disponibleDeComprasContextVar);
		stepNumberSaldo
				.setStepDescription("SAYNUMBER => CONSUMOS TRANSLADADOS POR DENUNCA");
		cf.addTask(stepNumberSaldo);

		/* GRUPOS */

		pideFechaGrp = (PideFechaDenuncia) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.pideFechaDenuncia);
		pideFechaGrp.setAudioFecha("HEBRAICA/H000005");
		pideFechaGrp.setAudioValidateFecha("HEBRAICA/H000004");
		pideFechaGrp.setAudioFechaInvalida("coto/A000056");
		pideFechaGrp.setAudioSuFechaEs("HEBRAICA/H000006");
		pideFechaGrp.setAudioAnio("coto/A900012");
		pideFechaGrp.setAudioMes("coto/A900011");
		pideFechaGrp.setAudioDia("coto/A900010");
		pideFechaGrp.setfechaContextVar(fechaContextVar);
		pideFechaGrp.setContextVarDia(diaContextVar);
		pideFechaGrp.setContextVarMes(mesContextVar);
		pideFechaGrp.setContextVarAnio(anioContextVar);
		pideFechaGrp.setConfirmaFechaContextVar(confirmaFechaContextVar);
		pideFechaGrp.setIntentosFechaContextVar(intentosFechaContextVar);
		pideFechaGrp.setStepIfTrue(stepAudioMenuDenuncias.GetId());
		pideFechaGrp.setStepIfFalse(stepAudioVerifiqueFechaNacimiento.GetId());

		pideDniGrp = (PideDni) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.pideDni);
		pideDniGrp.setAudioDni("HEBRAICA/H000002");
		pideDniGrp.setAudioValidateDni("HEBRAICA/H000004");
		pideDniGrp.setAudioDniInvalido("coto/A000009");
		pideDniGrp.setAudioSuDniEs("HEBRAICA/H000003");
		pideDniGrp.setDniContextVar(dniContextVar);
		pideDniGrp.setIntentosDniContextVar(intentosDniContextVar);
		pideDniGrp.setConfirmaDniContextVar(confirmaDniContextVar);
		pideDniGrp.setStepIfTrue(pideFechaGrp.getInitialStep());
		pideDniGrp.setStepIfFalse(stepAudioVerifiqueNumeroDni.GetId());

		/* JPOS */

		enviaTramaJpos = (StepSendJPOS) StepFactory.createStep(
				StepType.SendJPOS, UUID.randomUUID());
		enviaTramaJpos
				.setContextVariableTipoMensaje(envioServerJposPrecargadasContexVar);
		enviaTramaJpos.setContextVariableName(retornoJPOS);
		enviaTramaJpos.setContextVariableRspJpos(retornoMsgJPOS);
		enviaTramaJpos
				.addformatoVariables(0, codigoOperacionDenunciaContextVar);
		enviaTramaJpos.addformatoVariables(1, tarjetaContextVar);
		enviaTramaJpos.addformatoVariables(2, dniContextVar);
		enviaTramaJpos.addformatoVariables(3, anioContextVar);
		enviaTramaJpos.addformatoVariables(4, mesContextVar);
		enviaTramaJpos.addformatoVariables(5, diaContextVar);
		enviaTramaJpos.addformatoVariables(6, fillerParaDenunciaContexVar);
		enviaTramaJpos.addformatoVariables(7, idLlamadaContexVar);
		enviaTramaJpos.addformatoVariables(8, whisperContextVar);
		enviaTramaJpos.setStepDescription("SENDJPOS => ENVIA TRAMA JPOS");
		cf.addTask(enviaTramaJpos);

		evalRetJPOS = (StepSwitch) StepFactory.createStep(StepType.Switch,
				UUID.randomUUID());
		evalRetJPOS.setContextVariableName(retornoJPOS);
		evalRetJPOS.setStepDescription("SWITCH => CODIGO RETORNO JPOS");
		cf.addTask(evalRetJPOS);

		/* Contador */

		contadorIntentosDNIJPOS = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosDNIJPOS.setContextVariableName(intentosDniContextVar);
		contadorIntentosDNIJPOS
				.setStepDescription("COUNTER => INTENTOS DNI JPOS , HEBRAICA");
		cf.addTask(contadorIntentosDNIJPOS);

		contadorIntentosFechaJPOS = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosFechaJPOS
				.setContextVariableName(intentosFechaContextVar);
		contadorIntentosFechaJPOS
				.setStepDescription("COUNTER => INTENTOS FECHA JPOS , HEBRAICA");
		cf.addTask(contadorIntentosFechaJPOS);

		contadorIntentosPOS = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosPOS.setContextVariableName(intentosJPOSContextVar);
		contadorIntentosPOS
				.setStepDescription("COUNTER => INTENTOS JPOS , HEBRAICA");
		cf.addTask(contadorIntentosPOS);

		evalContadorDNIJPOS = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorDNIJPOS
				.setStepDescription("CONDITIONAL => INTENTOS DNI JPOS, HEBRAICA");
		cf.addTask(evalContadorDNIJPOS);

		evalContadorFechaJPOS = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorFechaJPOS
				.setStepDescription("CONDITIONAL => INTENTOS FECHA JPOS, HEBRAICA");
		cf.addTask(evalContadorFechaJPOS);

		evalContadorJPOS = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorJPOS
				.setStepDescription("CONDITIONAL => INTENTOS JPOS, HEBRAICA");
		cf.addTask(evalContadorJPOS);
		/* Parse Denuncia tarjeta */

		parserDenunciaTarjeta = (StepParseDenunciasTarjeta) StepFactory
				.createStep(StepFactory.StepType.ParseDenunciasTarjeta,
						UUID.randomUUID());
		parserDenunciaTarjeta.setRetornoMsgJPOS(retornoMsgJPOS);
		parserDenunciaTarjeta
				.setDisponibleDeComprasContextVar(disponibleDeComprasContextVar);
		parserDenunciaTarjeta
				.setDisponibleDeComprasDecimalContextVar(disponibleDeComprasDecimalContextVar);
		parserDenunciaTarjeta
				.setFechaDeEntregaContextVar(fechaDeEntregaContextVar);
		parserDenunciaTarjeta
				.setNumeroDeDenunciaContextVar(numeroDeDenunciaContextVar);
		parserDenunciaTarjeta.setSucursalContextVar(sucursalContextVar);
		parserDenunciaTarjeta
				.setStepDescription("PARSEDENUNCIATARJETA => PARSEO DENUNCIA DE TARJETA");
		cf.addTask(parserDenunciaTarjeta);

		this.evalRetJPOS();
	}

	private void evalRetJPOS() {

		/*-------------------------------------------------------------------------------
		 * ret =>  99    ||   "Tarjeta vencida / No está permitida la transición de estados en la parametría de la MEP"
		 * ret =>  98    ||   "Error en la longitud del mensaje / Código de identificación del mensaje erróneo"    
		 * ret =>  96    ||   "Tarjeta/documento inexistente "
		 * ret =>  94    ||   "Más de una tarjeta para ser bloqueada para el mismo documento"
		 * ret =>  03    ||   "Fecha de nacimiento ingresada difiere con el Archivo "
		 * ret =>  00    ||   "Ok"
		 *    
		-------------------------------------------------------------------------------	*/

		evalRetJPOS.addSwitchValue("99", contadorIntentosPOS.GetId());
		evalRetJPOS.addSwitchValue("98", contadorIntentosPOS.GetId());
		evalRetJPOS.addSwitchValue("96", contadorIntentosPOS.GetId());
		evalRetJPOS.addSwitchValue("94", stepAudioDatosNoCoinciden.GetId());
		evalRetJPOS.addSwitchValue("03", contadorIntentosPOS.GetId());
		evalRetJPOS.addSwitchValue("02", contadorIntentosPOS.GetId());
		evalRetJPOS.addSwitchValue("00", parserDenunciaTarjeta.GetId());
		evalRetJPOS.addSwitchValue("50", stepAudioServNoDisponible.GetId());
		evalRetJPOS.addSwitchValue("05", stepAudioServNoDisponible.GetId());
		evalRetJPOS.addSwitchValue("EE", stepAudioServNoDisponible.GetId());

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

		envioServerJposConsultasContexVar = this.getContextVar(
				"envioServerJposConsultasContexVar", "consultas", AstUid,
				"envioServerJposConsultasContexVar");

		envioServerJposPrecargadasContexVar = this.getContextVar(
				"envioServerJposPrecargadasContexVar", "precargada", AstUid,
				"envioServerJposPrecargadasContexVar");

		envioServerJposAutorizacionesContexVar = this.getContextVar(
				"envioServerJposAutorizacionesContexVar", "autorizaciones",
				AstUid, "envioServerJposAutorizacionesContexVar");

		menuDenunciasContextVar = this.getContextVar("menuDenunciasContextVar",
				"", AstUid, "menuDenunciasContextVar");

		fechaDeEntregaContextVar = this.getContextVar(
				"fechaDeEntregaContextVar", "", AstUid,
				"fechaDeEntregaContextVar");

		numeroDeDenunciaContextVar = this.getContextVar(
				"numeroDeDenunciaContextVar", "", AstUid,
				"numeroDeDenunciaContextVar");

		disponibleDeComprasDecimalContextVar = this.getContextVar(
				"disponibleDeComprasDecimalContextVar", "", AstUid,
				"disponibleDeComprasDecimalContextVar");

		disponibleDeComprasContextVar = this.getContextVar(
				"disponibleDeComprasContextVar", "", AstUid,
				"disponibleDeComprasContextVar");

		sucursalContextVar = this.getContextVar("sucursalContextVar", "",
				AstUid, "sucursalContextVar");

		retornoMsgJPOS = this.getContextVar("retornoMsgJPOS", "", AstUid,
				"retornoMsgJPOS");

		// Menu 's Denuncia

		subMenuDenunciaContextVar = this.getContextVar(
				"subMenuDenunciaContextVar", "", AstUid,
				"subMenuDenunciaContextVar");

		// Variables para grupos de denuncia

		dniContextVar = this.getContextVar("dniContextVar", "", AstUid,
				"dniContextVar");
		dniContextVar.setStringFormat("%08d");

		intentosDniContextVar = this.getContextVar("intentosDniContextVar",
				"0", AstUid, "intentosDniContextVar");

		confirmaDniContextVar = this.getContextVar("confirmaDniContextVar", "",
				AstUid, "confirmaDniContextVar");

		tarjetaContextVar = this.getContextVar("tarjetaContextVar",
				"0000000000000000", AstUid, "tarjetaContextVar");
		// tarjetaContextVar.setStringFormat("%16d");

		fechaContextVar = this.getContextVar("fechaContextVar", "", AstUid,
				"fechaContextVar");

		diaContextVar = this.getContextVar("diaContextVar", "", AstUid,
				"diaContextVar");

		mesContextVar = this.getContextVar("mesContextVar", "", AstUid,
				"mesContextVar");

		anioContextVar = this.getContextVar("anioContextVar", "", AstUid,
				"anioContextVar");

		// anioDenunciaContextVar = this.getContextVar("anioDenunciaContextVar",
		// "19"
		// +ctx.getContextVarByName("anioContextVar").getVarValue().substring(2,
		// 4), AstUid,
		// "anioDenunciaContextVar");

		confirmaFechaContextVar = this.getContextVar("confirmaFechaContextVar",
				"", AstUid, "confirmaFechaContextVar");

		intentosFechaContextVar = this.getContextVar("intentosFechaContextVar",
				"0", AstUid, "intentosFechaContextVar");

		intentosJPOSContextVar = this.getContextVar("intentosJPOSContextVar",
				"0", AstUid, "intentosJPOSContextVar");

		// JPOS

		retornoJPOS = this.getContextVar("retornoJPOS", "", AstUid,
				"retornoJPOS");

		retornoMsgJPOS = this.getContextVar("retornoMsgJPOS", "", AstUid,
				"retornoMsgJPOS");

		fillerParaDenunciaContexVar = this.getContextVar(
				"fillerParaDenunciaContexVar", "", AstUid,
				"fillerParaDenunciaContexVar");

		fillerParaDenunciaContexVar.setStringFormat("%67s");

		idLlamadaContexVar = this.getContextVar("idLlamadaContexVar",
				ast_uid.substring(ast_uid.length() - 29), AstUid,
				"idLlamadaContexVar");

		whisperContextVar = this.getContextVar("whisperContextVar", "0",
				AstUid, "whisperContextVar");

		codigoOperacionDenunciaContextVar = this.getContextVar(
				"codigoOperacionDenunciaContextVar", "1", AstUid,
				"codigoOperacionDenunciaContextVar");

	}
}
