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
import step.StepMenu;
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
import step.group.PideTarjeta;
import step.group.StepGroupFactory;
import workflow.Handler;
import workflow.Task;
import condition.condition;
import context.ContextVar;

public class IvrCoto extends BaseAgiScript {

	private long idContextVar = 1;
	CallContext ctx;
	CallFlow cf;
	private StepAnswer inicial;
	private StepEnd pasoFinal;
	private int intentos = 3;
	private ActivacionTarjetaCOTO activacionCotoGrp;
	private ArmaSaldoTarjetaCoto armaSaldoTarjetaCotoGrp;
	private BajaCompaniaAseguradoraCOTO bajaCompaniaAseguradora;
	private PideDni pideDniGrp;
	private PideDni pideDniBloqueoGrp;
	private PideTarjeta pideTarjetaGrp;
	private PideFecha pideFechaGrp;
	private StepSwitch evalRetJPOS;
	private StepSwitch evalCodEstudio;
	private StepSwitch evalIngresoMenuDirecciones;
	private StepSendJPOS enviaTramaJpos;
	private StepPlay stepAudioFechaIncorrecta;
	private StepPlay stepAudioFinal;
	private StepPlay stepAudioDerivoAsesor;
	private StepPlay stepAudioTarjetaNoVigente;
	private StepPlay stepAudioNroTarjIncorrecto;
	private StepPlay stepAudioServNoDisponible;
	private StepPlay stepAudioDniIncorrecto;
	private StepPlay stepAudioDireccionesCapital;
	private StepPlay stepAudioDireccionesBA;
	private StepPlay stepAudioBienvenida;
	private StepPlay stepAudioDireccionesProvincias;
	private StepPlay stepAudioVerificarDatos;
	private StepPlay stepAudioRequisitoNuevoCliente;
	private StepPlay stepAudioInicioMenuMora;
	private StepPlay stepAudioEstudioML;
	private StepPlay stepAudioEstudioRecoveryc;
	private StepPlay stepAudioEstudioDogliani;
	private StepPlay stepAudioEstudioCobranzasTCI;
	private StepPlay stepAudioEstudioPacktar;
	private StepPlay stepAudioEstudioMoyPc;
	private StepPlay stepAudioEstadoCuentaMora;
	private StepCounter contadorIntentosMenuDerivo;
	private StepCounter contadorIntentosFechaJPOS;
	private StepCounter contadorIntentosDNIJPOS;
	private StepCounter contadorIntentosTarjetaJPOS;
	private StepCounter contadorIntentosSubMenu;
	private StepCounter contadorIntentosMenuDirecciones;
	private StepCounter contadorIntentosIngresoRutina;
	private StepCounter contadorIntentosMenuNuevosClientes;
	private StepCounter contadorIntentosMenuDenuncia;
	private StepConditional evalContadorMenuDerivo;
	private StepConditional evalContadorFechaJPOS;
	private StepConditional evalContadorDNIJPOS;
	private StepConditional evalContadorTarjetaJPOS;
	private StepConditional evalContadorSubMenu;
	private StepConditional evalContadorMenuDirecciones;
	private StepConditional evalContadorIngresoRutina;
	private StepConditional evalContadorMenuNuevosClientes;
	private StepConditional evalContadorMenuDenuncia;
	private StepConditional evalBinCoto;
	private StepExecute stepDerivoSubMenu;
	private StepExecute stepVolverAlMenu;
	private StepExecute stepDerivoLlamada;
	private ContextVar menuIngresoContextVar;
	private ContextVar menuFinalContextVar;
	private ContextVar dniContextVar;
	private ContextVar confirmaDniContextVar;
	private ContextVar intentosDniContextVar;
	private ContextVar diaContextVar;
	private ContextVar retornoJPOS;
	private ContextVar mesContextVar;
	private ContextVar anioContextVar;
	private ContextVar intentosFechaContextVar;
	private ContextVar tarjetaContexVar;
	private ContextVar fillerContexVar;
	private ContextVar intentosMenuInicialContextVar;
	private ContextVar fechaContextVar;
	private ContextVar confirmaFechaContextVar;
	private ContextVar idLlamadaContexVar;
	private ContextVar whisperContextVar;
	private ContextVar envioServerConsultasJposContexVar;
	private ContextVar confirmaTarjetaContextVar;
	private ContextVar intentosTarjetaContextVar;
	private ContextVar mensajeActivacionJpos;
	private ContextVar resultadoSubMenuDireccionesContextVar;
	private ContextVar menuDerivoContextVar;
	private ContextVar intentosMenuDerivoContextVar;
	private ContextVar resultadoMenuDireccionesContextVar;
	private ContextVar intentosMenuDireccionesContextVar;
	private ContextVar resultadoAudioInicio;
	private ContextVar intentosIngresoContextVar;
	private ContextVar resultadoSubMenuContextVar;
	private ContextVar intentosSubMenuContextVar;
	private ContextVar resultadoMenuBloqueoContextVar;
	private ContextVar retornoMsgJPOS;
	private ContextVar intentosMenuNuevosClientesContextVar;
	private ContextVar resultadoAudioMenuPrincipalNuevosClientes;
	private ContextVar numeroDeLineaContexVar;
	private ContextVar resultadoAudioMenuNuevosClientes;
	private ContextVar intentosMenuDenunciaContextVar;
	private ContextVar resultadoAudioMenuDenuncia;
	private StepParseSaldosTarjeta parserTarjeta;
	private StepPlayRead stepAudioMenuDenuncias;
	private StepPlayRead stepAudioMenuPrincipalNuevosClientes;
	private StepPlayRead stepAudioMenuBloqueo;
	private StepPlayRead stepAudioMenuNuevosClientes;
	private StepPlayRead stepAudioMenuPrincipal;
	private StepPlayRead stepAudioMenuDerivo;
	private StepPlayRead stepAudioSubMenu;
	private StepPlayRead stepAudioMenuDirecciones;
	private StepPlayRead stepAudioSubMenuDirecciones;
	private StepMenu stepMenuBloqueo;
	private StepMenu stepMenuDenuncia;
	private StepMenu stepMenuPrincipalNuevosClientes;
	private StepMenu stepMenuNuevosClientes;
	private StepMenu stepMenuDirecciones;
	private StepMenu stepSubMenuDirecciones;
	private StepMenu stepSubMenu;
	private StepMenu stepMenuInicial;
	private StepMenu stepMenuDerivo;
	private ContextVar codigoDeEstudioContextVar;
	private StepPlayRead stepAudioMenuIteracionMora;
	private ContextVar resultadoMenuIteracionMenuMoraContextVar;
	private StepMenu stepMenuIteracionMora;
	private ContextVar idTramiteContextVar;
	private ContextVar codJposBajaSegContextVar;
	private ContextVar salEnUnPagoContextVar;
	private ContextVar saldoCuentaContextVar;
	private ContextVar vencimientoProxResumAnioContextVar;
	private ContextVar vencimientoProxResumMesContextVar;
	private ContextVar vencimientoProxResumDiaContextVar;
	private ContextVar cierreProxResumAnioContextVar;
	private ContextVar cierreProxResumMesContextVar;
	private ContextVar cierreProxResumDiaContextVar;
	private ContextVar pagoMinPendienteDeCanceUltResumContextVar;
	private ContextVar pagoMinUltResumContextVar;
	private ContextVar totalPagUltResumContextVar;
	private ContextVar saldoUltResumContextVar;
	private ContextVar salPendienteDeCanceUltResumContextVar;
	private ContextVar salEnCuotasContextVar;
	private ContextVar fechaVencimientoUltResumContextVarDia;
	private ContextVar fechaVencimientoUltResumContextVarMes;
	private ContextVar fechaVencimientoUltResumContextVarAnio;
	private ContextVar saldoUltResumDecimalContextVar;
	private ContextVar totalPagUltResumDecimalContextVar;
	private ContextVar pagoMinUltResumDecimContextVar;
	private ContextVar salEnUnPagoDecimalContextVar;
	private ContextVar salEnCuotasDecimalContextVar;
	private ContextVar pagoMinPendienteDeCanceUltDecimalResumContextVar;
	private ContextVar saldoCuentaDecimalContextVar;
	private ContextVar salPendienteDeCanceUltResumDecimalContextVar;
	private ContextVar primerTarjetaContextVar;
	private ContextVar menuIngresoActivacionContextVar;
	private ContextVar intentosPrimerTarjetaContextVar;
	private ContextVar intentosMenuFinalContextVar;
	private ContextVar fdnContexVar;
	private ContextVar contextVarMes;
	private ContextVar contextVarAnio;
	private ContextVar contextVarDia;
	private ContextVar menuIngresoBajaSeguro;
	private ContextVar nroCuentaContexVar;
	private ContextVar mensajeSaldosJpos;
	private StepPlay stepAudioDniIncorrectoFin;
	private ContextVar envioServerJposConsultasContexVar;
	private ContextVar envioServerJposPrecargadasContexVar;
	private ContextVar envioServerJposAutorizacionesContexVar;
	private StepPlay stepAudioVerificarDatosTarjeta;
	private PideDni pideDniBloqueo2Grp;
	private StepPlayFromVar stepAudioPrevioDerivoAsesor;
	private StepTimeConditionDB stepTimeConditionDB;
	private ContextVar dniParaActivacionContextVar;
	private ContextVar nroCuentaParaActivacionContexVar;
	private ContextVar fillerParaActivacionContexVar;
	private ContextVar cambiaPrimerTarjetaContextVar;
	private ContextVar tarjetaPendienteDeActivacionContextVar;
	private StepConditional evalTarjPendActivacion;
	private StepTimeConditionDB obtieneHorario;
	private ContextVar empresaIdContextVar;
	private ContextVar servicioIdContextVar;
	private ContextVar audioFueraHorarioContextVar;
	private ContextVar ejecutoJPOSFechaContextVar;
	private ContextVar cambiaEjecutoJPOSFechaContextVar;
	private ContextVar ejecutoJPOSDniContextVar;
	private ContextVar cambiaEjecutoJPOSDniContextVar;
	private ContextVar cambiaEjecutoJPOSContextVar;
	private ContextVar ejecutoJPOSContextVar;
	private StepSetAsteriskVariable stepSetDni;
	private StepSetAsteriskVariable stepSetTarjeta;
	private ContextVar scapeDigitContextVar;
	private PideTarjeta pideTarjetaResumenDeCuenta;
	private StepMenu stepMenuResumenCuenta;
	private StepPlayRead stepAudioMenuResumenCuenta;
	private ContextVar resultadoAudioMenuResumenCuenta;
	private StepPlay stepAudioNoEsPosibleConAdicional;
	private StepPlayRead stepAudioIrregularidadCuenta;
	private StepSendJPOS enviaTramaResumenCuentaJpos;
	private StepSwitch evalRetJPOSResumenCuenta;
	private StepConditional evalBinCotoResumenDeCuenta;
	private ContextVar mensajeCambioDeResumenJpos;
	private ContextVar consultaCambioDeResumenContexVar;
	private ContextVar retornoJPOSResumenCuenta;
	private ContextVar retornoMsgJPOSResumenCuenta;
	private ContextVar intentosMenuResumenCuentaContextVar;
	private StepCounter contadorIntentosMenuResumenCuenta;
	private StepConditional evalContadorMenuResumenCuenta;
	private ContextVar confirmacionCambioDeResumenContexVar;
	private StepSendJPOS enviaTramaCambioResumenCuentaJpos;
	private StepPlay stepAudioCambioResemenCuenta;
	private ContextVar resultadoAudioMenuIrregularidadCuenta;
	private StepMenu stepMenuIrregularidadCuenta;
	private StepCounter contadorIntentosMenuIrregularidadCuenta;
	private ContextVar intentosMenuIrregularidadCuentaContextVar;
	private StepConditional evalContadorMenuIrregularidadCuenta;
	private StepPlay stepAudioTarjetaIncorrecta;
	private StepCounter contadorIntentosTarjetaJPOSRCuenta;
	private StepConditional evalContadorTarjetaJPOSRCuenta;
	private StepCounter contadorIntentosBINTarjeta;
	private StepCounter contadorIntentosBINTarjetaRCuenta;
	private StepPlay stepAudioNroTarjIncorrectoRCuenta;
	private StepConditional evalContadorBINTarjeta;
	private StepConditional evalContadorBINTarjetaRCuenta;

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

		/* --- INICIO IVR COTO --- */

		inicial.setNextstep(stepAudioBienvenida.GetId());

		/* --- MENU PRINCIPAL --- */

		stepAudioBienvenida.setNextstep(stepAudioMenuPrincipal.GetId());

		stepAudioMenuPrincipal.setNextstep(stepMenuInicial.GetId());
		stepMenuInicial.addSteps("1", pideTarjetaGrp.getInitialStep());
		stepMenuInicial.addSteps("2", stepAudioMenuDenuncias.GetId());
		stepMenuInicial.addSteps("3", stepAudioMenuNuevosClientes.GetId());
		stepMenuInicial.addSteps("4",
				pideTarjetaResumenDeCuenta.getInitialStep());
		stepMenuInicial.setInvalidOption(contadorIntentosIngresoRutina.GetId());

		contadorIntentosIngresoRutina.setNextstep(evalContadorIngresoRutina
				.GetId());

		evalContadorIngresoRutina.addCondition(new condition(1, "#{"
				+ intentosIngresoContextVar.getVarName() + "} < " + intentos,
				stepAudioMenuPrincipal.GetId(), stepAudioFinal.GetId()));

		/* --- SECUENCIA OPCION 1 --- */

		// Viene de la Rutina de Pide Tarjeta.

		pideTarjetaGrp.setStepIfTrue(evalBinCoto.GetId());
		pideTarjetaGrp.setStepIfFalse(stepAudioVerificarDatosTarjeta.GetId());

		// stepAudioVerificarDatosTarjeta.setNextstep(stepAudioFinal.GetId());

		evalBinCoto.addCondition(new condition(1, "substring('#{"
				+ tarjetaContexVar.getVarName() + "}',0,6) == '603167'",
				enviaTramaJpos.GetId(), stepAudioNroTarjIncorrecto.GetId()));

		stepAudioNroTarjIncorrecto.setNextstep(contadorIntentosBINTarjeta
				.GetId());

		contadorIntentosBINTarjeta.setNextstep(evalContadorBINTarjeta.GetId());
		evalContadorBINTarjeta.addCondition(new condition(1, "#{"
				+ intentosTarjetaContextVar.getVarName() + "} < " + intentos,
				pideTarjetaGrp.getInitialStep(), stepAudioVerificarDatos
						.GetId()));

		/*
		 * stepAudioNroTarjIncorrecto.setNextstep(contadorIntentosTarjetaJPOS.GetId
		 * ());
		 * 
		 * enviaTramaJpos.setNextstep(parserTarjeta.GetId());
		 * parserTarjeta.setNextstep(evalRetJPOS.GetId());
		 * evalRetJPOS.setNextstep(stepAudioServNoDisponible.GetId());
		 */

		/* --- SECUENCIA OPCION 1 --> RET 00 --- */

		armaSaldoTarjetaCotoGrp.setStepIfTrue(evalTarjPendActivacion.GetId());
		armaSaldoTarjetaCotoGrp.setStepIfFalse(stepAudioVerificarDatos.GetId());

		// stepAudioVerificarDatosTarjeta.setNextstep(stepAudioFinal.GetId());

		evalTarjPendActivacion.addCondition(new condition(1, "#{"
				+ tarjetaPendienteDeActivacionContextVar.getVarName() + "} == "
				+ "0", stepAudioSubMenu.GetId(), activacionCotoGrp
				.getInitialStep()));

		// activacionCotoGrp.setStepIfFalseUUID(stepAudioMenuPrincipal.GetId());
		// activacionCotoGrp.setStepIfTrueUUID(stepAudioMenuPrincipal.GetId());

		stepAudioSubMenu.setNextstep(stepSubMenu.GetId());

		stepSubMenu.addSteps("1", parserTarjeta.GetId());
		stepSubMenu.addSteps("2", activacionCotoGrp.getInitialStep());
		stepSubMenu.addSteps("3", obtieneHorario.GetId());
		stepSubMenu.addSteps("4", obtieneHorario.GetId());
		stepSubMenu.addSteps("5", stepAudioMenuDirecciones.GetId());
		stepSubMenu.addSteps("6", obtieneHorario.GetId());
		// stepSubMenu.addSteps("7", bajaCompaniaAseguradora.getInitialStep());
		// ANULADO
		stepSubMenu.addSteps("9", stepAudioFinal.GetId());
		stepSubMenu.addSteps("0", obtieneHorario.GetId());
		stepSubMenu.setInvalidOption(contadorIntentosSubMenu.GetId());

		contadorIntentosSubMenu.setNextstep(evalContadorSubMenu.GetId());

		evalContadorSubMenu.addCondition(new condition(1, "#{"
				+ intentosSubMenuContextVar.getVarName() + "} < " + intentos,
				stepAudioSubMenu.GetId(), stepAudioFinal.GetId()));

		/* --- SECUENCIA OPCION 2 --- */

		stepAudioMenuDenuncias.setNextstep(stepMenuDenuncia.GetId());

		stepMenuDenuncia.addSteps("1", pideDniBloqueoGrp.getInitialStep());
		stepMenuDenuncia.addSteps("2", stepAudioMenuPrincipal.GetId());
		stepMenuDenuncia.setInvalidOption(contadorIntentosMenuDenuncia.GetId());

		contadorIntentosMenuDenuncia.setNextstep(evalContadorMenuDenuncia
				.GetId());

		evalContadorMenuDenuncia.addCondition(new condition(1, "#{"
				+ intentosMenuDenunciaContextVar.getVarName() + "} < "
				+ intentos, stepAudioMenuDenuncias.GetId(), stepAudioFinal
				.GetId()));

		pideDniBloqueoGrp.setStepIfTrue(stepAudioDerivoAsesor.GetId());
		pideDniBloqueoGrp.setStepIfFalse(stepAudioDniIncorrectoFin.GetId());

		stepAudioDniIncorrectoFin.setNextstep(stepAudioFinal.GetId());

		/* --- SECUENCIA OPCION 3 --- */

		stepAudioMenuNuevosClientes.setNextstep(stepMenuNuevosClientes.GetId());

		stepMenuNuevosClientes.addSteps("1",
				pideDniBloqueo2Grp.getInitialStep());
		stepMenuNuevosClientes.addSteps("2",
				stepAudioRequisitoNuevoCliente.GetId());
		stepMenuNuevosClientes.addSteps("0", pideTarjetaGrp.getInitialStep());
		stepMenuNuevosClientes
				.setInvalidOption(contadorIntentosMenuNuevosClientes.GetId());

		contadorIntentosMenuNuevosClientes
				.setNextstep(evalContadorMenuNuevosClientes.GetId());

		evalContadorMenuNuevosClientes.addCondition(new condition(1, "#{"
				+ intentosMenuNuevosClientesContextVar.getVarName() + "} < "
				+ intentos, stepAudioMenuNuevosClientes.GetId(), stepAudioFinal
				.GetId()));

		stepAudioRequisitoNuevoCliente
				.setNextstep(stepAudioMenuPrincipalNuevosClientes.GetId());

		stepAudioMenuPrincipalNuevosClientes
				.setNextstep(stepMenuPrincipalNuevosClientes.GetId());

		stepMenuPrincipalNuevosClientes.addSteps("1",
				stepAudioRequisitoNuevoCliente.GetId());
		stepMenuPrincipalNuevosClientes.addSteps("2",
				stepAudioMenuDirecciones.GetId());
		stepMenuPrincipalNuevosClientes.addSteps("3",
				stepAudioMenuNuevosClientes.GetId());
		stepMenuPrincipalNuevosClientes.addSteps("9", stepAudioFinal.GetId());
		stepMenuPrincipalNuevosClientes
				.setInvalidOption(stepAudioMenuPrincipalNuevosClientes.GetId());

		/* --- SECUENCIA OPCION 3 --> 2 --- */

		stepAudioMenuDirecciones.setNextstep(stepMenuDirecciones.GetId());

		stepMenuDirecciones.addSteps("1", stepAudioDireccionesCapital.GetId());
		stepMenuDirecciones.addSteps("2", stepAudioDireccionesBA.GetId());
		stepMenuDirecciones.addSteps("3",
				stepAudioDireccionesProvincias.GetId());
		stepMenuDirecciones.setInvalidOption(contadorIntentosMenuDirecciones
				.GetId());

		contadorIntentosMenuDirecciones.setNextstep(evalContadorMenuDirecciones
				.GetId());

		evalContadorMenuDirecciones.addCondition(new condition(1, "#{"
				+ intentosMenuDireccionesContextVar.getVarName() + "} < "
				+ intentos, stepAudioMenuDirecciones.GetId(), stepAudioFinal
				.GetId()));

		stepAudioDireccionesCapital.setNextstep(stepAudioSubMenuDirecciones
				.GetId());
		stepAudioDireccionesBA.setNextstep(stepAudioSubMenuDirecciones.GetId());
		stepAudioDireccionesProvincias.setNextstep(stepAudioSubMenuDirecciones
				.GetId());

		stepAudioSubMenuDirecciones.setNextstep(stepSubMenuDirecciones.GetId());

		/* --- TIPO DE DIRECCION --- */

		stepSubMenuDirecciones
				.addSteps("1", evalIngresoMenuDirecciones.GetId());
		stepSubMenuDirecciones.addSteps("2", stepAudioMenuDirecciones.GetId());
		stepSubMenuDirecciones.addSteps("3", stepAudioMenuPrincipal.GetId());
		stepSubMenuDirecciones.addSteps("9", stepAudioFinal.GetId());
		stepSubMenuDirecciones.setInvalidOption(stepAudioSubMenuDirecciones
				.GetId());

		/* --- MENU MORA --- */

		stepAudioInicioMenuMora.setNextstep(evalCodEstudio.GetId());

		evalCodEstudio.setNextstep(stepAudioServNoDisponible.GetId());

		stepAudioEstudioMoyPc.setNextstep(stepAudioEstadoCuentaMora.GetId());
		stepAudioEstudioPacktar.setNextstep(stepAudioEstadoCuentaMora.GetId());
		stepAudioEstudioCobranzasTCI.setNextstep(stepAudioEstadoCuentaMora
				.GetId());
		stepAudioEstudioDogliani.setNextstep(stepAudioEstadoCuentaMora.GetId());
		stepAudioEstudioRecoveryc
				.setNextstep(stepAudioEstadoCuentaMora.GetId());
		stepAudioEstudioML.setNextstep(stepAudioEstadoCuentaMora.GetId());

		stepAudioEstadoCuentaMora.setNextstep(stepAudioMenuIteracionMora
				.GetId());

		stepAudioMenuIteracionMora.setNextstep(stepMenuIteracionMora.GetId());

		stepMenuIteracionMora.addSteps("1", stepAudioInicioMenuMora.GetId());
		stepMenuIteracionMora.addSteps("9", stepAudioFinal.GetId());
		stepMenuIteracionMora.setInvalidOption(stepAudioMenuIteracionMora
				.GetId());

		/* --- SECUENCIA OPCION 4 --- */

		pideTarjetaResumenDeCuenta.setStepIfTrue(evalBinCotoResumenDeCuenta
				.GetId());
		pideTarjetaResumenDeCuenta
				.setStepIfFalse(stepAudioVerificarDatosTarjeta.GetId());

		evalBinCotoResumenDeCuenta.addCondition(new condition(1,
				"substring('#{" + tarjetaContexVar.getVarName()
						+ "}',0,6) == '603167'", enviaTramaResumenCuentaJpos
						.GetId(), stepAudioNroTarjIncorrectoRCuenta.GetId()));

		stepAudioNroTarjIncorrectoRCuenta
				.setNextstep(contadorIntentosBINTarjetaRCuenta.GetId());

		contadorIntentosBINTarjetaRCuenta
				.setNextstep(evalContadorBINTarjetaRCuenta.GetId());
		evalContadorBINTarjetaRCuenta.addCondition(new condition(1, "#{"
				+ intentosTarjetaContextVar.getVarName() + "} < " + intentos,
				pideTarjetaResumenDeCuenta.getInitialStep(),
				stepAudioVerificarDatos.GetId()));

		/* --- SECUENCIA OPCION 4 --> RET R. CUENTA 00 --- */

		stepAudioMenuResumenCuenta.setNextstep(stepMenuResumenCuenta.GetId());

		stepMenuResumenCuenta.addSteps("1",
				enviaTramaCambioResumenCuentaJpos.GetId());
		stepMenuResumenCuenta.addSteps("2", stepAudioMenuPrincipal.GetId());
		stepMenuResumenCuenta.addSteps("9", stepAudioFinal.GetId());
		stepMenuResumenCuenta
				.setInvalidOption(contadorIntentosMenuResumenCuenta.GetId());

		contadorIntentosMenuResumenCuenta
				.setNextstep(evalContadorMenuResumenCuenta.GetId());
		evalContadorMenuResumenCuenta.addCondition(new condition(1, "#{"
				+ intentosMenuResumenCuentaContextVar.getVarName() + "} < "
				+ intentos, stepAudioMenuResumenCuenta.GetId(), stepAudioFinal
				.GetId()));

		stepAudioCambioResemenCuenta.setNextstep(stepAudioFinal.GetId());

		/* --- SECUENCIA OPCION 4 --> RET R. CUENTA 83 --- */

		stepAudioNoEsPosibleConAdicional.setNextstep(stepAudioFinal.GetId());

		/* --- SECUENCIA OPCION 4 --> RET R. CUENTA 96 --- */

		stepAudioNoEsPosibleConAdicional.setNextstep(stepAudioFinal.GetId());

		stepAudioTarjetaIncorrecta
				.setNextstep(contadorIntentosTarjetaJPOSRCuenta.GetId());

		/* --- SECUENCIA OPCION 4 --> RET R. CUENTA 99 --- */

		stepAudioIrregularidadCuenta.setNextstep(stepMenuIrregularidadCuenta
				.GetId());

		stepMenuIrregularidadCuenta.addSteps("2",
				stepAudioMenuPrincipal.GetId());
		stepMenuIrregularidadCuenta.addSteps("9", stepAudioFinal.GetId());
		stepMenuIrregularidadCuenta
				.setInvalidOption(contadorIntentosMenuIrregularidadCuenta
						.GetId());

		contadorIntentosMenuIrregularidadCuenta
				.setNextstep(evalContadorMenuIrregularidadCuenta.GetId());

		evalContadorMenuIrregularidadCuenta.addCondition(new condition(1, "#{"
				+ intentosMenuIrregularidadCuentaContextVar.getVarName()
				+ "} < " + intentos, stepAudioIrregularidadCuenta.GetId(),
				stepAudioFinal.GetId()));

		/* --- GRUPOS --- */

		pideDniGrp.setStepIfTrue(enviaTramaJpos.GetId());
		pideDniGrp.setStepIfFalse(stepAudioDniIncorrectoFin.GetId());

		pideFechaGrp.setStepIfTrue(enviaTramaJpos.GetId());
		pideFechaGrp.setStepIfFalse(stepAudioVerificarDatos.GetId());

		activacionCotoGrp.setStepIfFalseUUID(stepAudioMenuPrincipal.GetId());
		activacionCotoGrp.setStepIfTrueUUID(stepAudioMenuPrincipal.GetId());

		pideDniBloqueo2Grp.setStepIfTrue(obtieneHorario.GetId());
		pideDniBloqueo2Grp.setStepIfFalse(stepAudioDniIncorrectoFin.GetId());

		/* --- DERIVOS --- */

		stepAudioMenuDerivo.setNextstep(stepMenuDerivo.GetId());

		stepMenuDerivo.addSteps("9", obtieneHorario.GetId());
		stepMenuDerivo.addSteps("0", stepAudioFinal.GetId());
		stepMenuDerivo.setInvalidOption(contadorIntentosMenuDerivo.GetId());

		contadorIntentosMenuDerivo.setNextstep(evalContadorMenuDerivo.GetId());

		evalContadorMenuDerivo.addCondition(new condition(1,
				"#{" + intentosMenuDerivoContextVar.getVarName() + "} < "
						+ intentos, obtieneHorario.GetId(), stepAudioFinal
						.GetId()));

		stepAudioPrevioDerivoAsesor.setNextstep(stepAudioFinal.GetId());

		stepAudioDerivoAsesor.setNextstep(stepSetDni.GetId());
		stepSetDni.setNextstep(stepSetTarjeta.GetId());
		stepSetTarjeta.setNextstep(stepDerivoLlamada.GetId());

		/* --- JPOS --- */

		enviaTramaCambioResumenCuentaJpos
				.setNextstep(stepAudioCambioResemenCuenta.GetId());

		enviaTramaResumenCuentaJpos.setNextstep(evalRetJPOSResumenCuenta
				.GetId());
		evalRetJPOSResumenCuenta.setNextstep(stepAudioServNoDisponible.GetId());
		enviaTramaJpos.setNextstep(parserTarjeta.GetId());
		parserTarjeta.setNextstep(evalRetJPOS.GetId());
		evalRetJPOS.setNextstep(stepAudioServNoDisponible.GetId());

		/* --- ITERACIONES JPOS --- */

		stepAudioDniIncorrecto.setNextstep(contadorIntentosDNIJPOS.GetId());

		contadorIntentosDNIJPOS.setNextstep(evalContadorDNIJPOS.GetId());
		evalContadorDNIJPOS.addCondition(new condition(1, "#{"
				+ intentosDniContextVar.getVarName() + "} < " + intentos,
				pideDniGrp.getInitialStep(), obtieneHorario.GetId()));

		stepAudioFechaIncorrecta.setNextstep(contadorIntentosFechaJPOS.GetId());

		contadorIntentosFechaJPOS.setNextstep(evalContadorFechaJPOS.GetId());
		evalContadorFechaJPOS.addCondition(new condition(1, "#{"
				+ intentosFechaContextVar.getVarName() + "} < " + intentos,
				pideFechaGrp.getInitialStep(), obtieneHorario.GetId()));

		stepAudioTarjetaNoVigente.setNextstep(contadorIntentosTarjetaJPOS
				.GetId());

		contadorIntentosTarjetaJPOS
				.setNextstep(evalContadorTarjetaJPOS.GetId());
		evalContadorTarjetaJPOS.addCondition(new condition(1, "#{"
				+ intentosTarjetaContextVar.getVarName() + "} < " + intentos,
				pideTarjetaGrp.getInitialStep(), obtieneHorario.GetId()));

		contadorIntentosTarjetaJPOSRCuenta
				.setNextstep(evalContadorTarjetaJPOSRCuenta.GetId());
		evalContadorTarjetaJPOSRCuenta.addCondition(new condition(1, "#{"
				+ intentosTarjetaContextVar.getVarName() + "} < " + intentos,
				pideTarjetaResumenDeCuenta.getInitialStep(),
				stepAudioVerificarDatos.GetId()));

		/* --- AUDIOS FINALES --- */

		stepAudioFinal.setNextstep(pasoFinal.GetId());
		stepAudioServNoDisponible.setNextstep(stepAudioFinal.GetId());
		stepAudioVerificarDatos.setNextstep(stepAudioFinal.GetId());
		stepAudioVerificarDatosTarjeta.setNextstep(stepAudioFinal.GetId());

		// stepAudioMenuBloqueo.setNextstep(stepMenuBloqueo.GetId());

		/* --- OBTIENE HORARIO DE LA BASE --- */

		obtieneHorario.setNextStepIsTrue(stepAudioPrevioDerivoAsesor.GetId());
		obtieneHorario.setNextStepIsFalse(stepAudioDerivoAsesor.GetId());

	}

	@Override
	public void service(AgiRequest request, AgiChannel channel) {
		Daemon.getDbLog().addCallFlowToLog(channel.getUniqueId(),
				IvrCoto.class.getName(), request.getCallerIdNumber());
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
		for (Task tmpTask : pideTarjetaGrp.getSteps().values()) {
			cf.addTask(tmpTask);
		}
		for (Task tmpTask : pideTarjetaResumenDeCuenta.getSteps().values()) {
			cf.addTask(tmpTask);
		}
		for (Task tmpTask : pideDniBloqueoGrp.getSteps().values()) {
			cf.addTask(tmpTask);
		}
		for (Task tmpTask : pideDniBloqueo2Grp.getSteps().values()) {
			cf.addTask(tmpTask);
		}
		for (Task tmpTask : armaSaldoTarjetaCotoGrp.getSteps().values()) {
			cf.addTask(tmpTask);
		}
		for (Task tmpTask : activacionCotoGrp.getSteps().values()) {
			cf.addTask(tmpTask);
		}
		for (Task tmpTask : bajaCompaniaAseguradora.getSteps().values()) {
			cf.addTask(tmpTask);
		}
		ctx.setInitialStep(inicial.GetId());
		try {
			cf.execute(ctx);
		} catch (Exception ex) {
			Logger.getLogger(TestIvr.class.getName())
					.log(Level.FATAL, null, ex);
		}
	}

	/* -------------------------- Creo Steps -------------------------- */

	private void createSteps() {

		/* --- INICIO - FIN --- */

		inicial = (StepAnswer) StepFactory.createStep(StepType.Answer,
				UUID.randomUUID());
		inicial.setStepDescription("ANSWER => INICIO COMUNICACION IVR COTO ");
		cf.addTask(inicial);

		pasoFinal = (StepEnd) StepFactory.createStep(StepType.End,
				UUID.randomUUID());
		pasoFinal.setStepDescription("END => FIN COMUNICACION IVR COTO");
		cf.addTask(pasoFinal);

		/* --- DERIVO --- */

		stepVolverAlMenu = (StepExecute) StepFactory.createStep(
				StepType.Execute, UUID.randomUUID());
		stepVolverAlMenu.setApp("goto");
		stepVolverAlMenu.setAppOptions(Daemon.getConfig("DERIVOMENUPRINCIPAL"));
		stepVolverAlMenu
				.setStepDescription("EXECUTE => NO INGRESA A LA RUTINA");
		cf.addTask(stepVolverAlMenu);

		stepDerivoSubMenu = (StepExecute) StepFactory.createStep(
				StepType.Execute, UUID.randomUUID());
		stepDerivoSubMenu.setApp("goto");
		stepDerivoSubMenu.setAppOptions(Daemon.getConfig("DERIVOSUBMENU"));
		stepDerivoSubMenu.setStepDescription("EXECUTE => DERIVO SUB MENU");
		cf.addTask(stepDerivoSubMenu);

		stepDerivoLlamada = (StepExecute) StepFactory.createStep(
				StepType.Execute, UUID.randomUUID());
		stepDerivoLlamada.setApp("goto");
		stepDerivoLlamada.setAppOptions(Daemon.getConfig("DERIVOOPERADORCOTO"));
		stepDerivoLlamada.setStepDescription("EXECUTE => DERIVO OPERADOR");
		cf.addTask(stepDerivoLlamada);

		/* --- PLAY --- */

		stepAudioBienvenida = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioBienvenida.setPlayfile("coto/A000001");
		stepAudioBienvenida.setStepDescription("PLAY => BIENVENIDA IVR COTO");
		cf.addTask(stepAudioBienvenida);

		stepAudioFinal = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioFinal.setPlayfile("coto/A000026");
		stepAudioFinal.setStepDescription("PLAY => DESPEDIDA IVR COTO");
		cf.addTask(stepAudioFinal);

		stepAudioRequisitoNuevoCliente = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioRequisitoNuevoCliente.setPlayfile("coto/A000041");
		stepAudioRequisitoNuevoCliente
				.setStepDescription("PLAY => REQUISITOS NUEVOS CLIENTES");
		cf.addTask(stepAudioRequisitoNuevoCliente);

		stepAudioDerivoAsesor = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioDerivoAsesor.setPlayfile("coto/A000013");
		stepAudioDerivoAsesor.setStepDescription("PLAY => DERIVO OPERADOR");
		cf.addTask(stepAudioDerivoAsesor);

		stepAudioDireccionesProvincias = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioDireccionesProvincias.setPlayfile("coto/A000047");
		stepAudioDireccionesProvincias
				.setStepDescription("PLAY => DIRECCIONES PROVINCIAS");
		cf.addTask(stepAudioDireccionesProvincias);

		stepAudioDireccionesBA = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioDireccionesBA.setPlayfile("coto/A000046");
		stepAudioDireccionesBA
				.setStepDescription("PLAY => DIRECCIONES BUENOS AIRES");
		cf.addTask(stepAudioDireccionesBA);

		stepAudioDireccionesCapital = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioDireccionesCapital.setPlayfile("coto/A000045");
		stepAudioDireccionesCapital
				.setStepDescription("PLAY => DIRECCIONES CAPITAL FEDERAL");
		cf.addTask(stepAudioDireccionesCapital);

		stepAudioDniIncorrectoFin = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioDniIncorrectoFin.setPlayfile("coto/A000008");
		stepAudioDniIncorrectoFin
				.setStepDescription("PLAY => DNI INCORRECTO, CORTA");
		cf.addTask(stepAudioDniIncorrectoFin);

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

		stepAudioNroTarjIncorrecto = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioNroTarjIncorrecto.setPlayfile("RUTINAPIN/RUTINA_PIN016");
		stepAudioNroTarjIncorrecto
				.setStepDescription("PLAY => NUMERO DE TARJETA INCORRECTO");
		cf.addTask(stepAudioNroTarjIncorrecto);

		stepAudioNroTarjIncorrectoRCuenta = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioNroTarjIncorrectoRCuenta
				.setPlayfile("RUTINAPIN/RUTINA_PIN016");
		stepAudioNroTarjIncorrectoRCuenta
				.setStepDescription("PLAY => NUMERO DE TARJETA INCORRECTO");
		cf.addTask(stepAudioNroTarjIncorrectoRCuenta);

		stepAudioTarjetaNoVigente = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioTarjetaNoVigente.setPlayfile("RUTINAPIN/RUTINA_PIN024");
		stepAudioTarjetaNoVigente.setStepDescription("TARJETA NO VIGENTE");
		cf.addTask(stepAudioTarjetaNoVigente);

		stepAudioServNoDisponible = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioServNoDisponible.setPlayfile("RUTINAPIN/RUTINA_PIN021");
		stepAudioServNoDisponible
				.setStepDescription("PLAY => SERVICIO NO DISPONIBLE");
		cf.addTask(stepAudioServNoDisponible);

		stepAudioVerificarDatos = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioVerificarDatos.setPlayfile("coto/A000057");
		stepAudioVerificarDatos.setStepDescription("PLAY => VERIFICAR DATOS");
		cf.addTask(stepAudioVerificarDatos);

		stepAudioVerificarDatosTarjeta = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioVerificarDatosTarjeta.setPlayfile("coto/A0000012");
		stepAudioVerificarDatosTarjeta
				.setStepDescription("PLAY => VERIFICAR DATOS TARJETA");
		cf.addTask(stepAudioVerificarDatosTarjeta);

		stepAudioInicioMenuMora = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioInicioMenuMora.setPlayfile("coto/A000067");
		stepAudioInicioMenuMora.setStepDescription("PLAY => INICIO MENU MORA");
		cf.addTask(stepAudioInicioMenuMora);

		stepAudioEstudioMoyPc = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioEstudioMoyPc.setPlayfile("coto/E000001");
		stepAudioEstudioMoyPc
				.setStepDescription("PLAY => DIRECCION Y HORARIO , ESTUDIO MO Y PC");
		cf.addTask(stepAudioEstudioMoyPc);

		stepAudioEstudioPacktar = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioEstudioPacktar.setPlayfile("coto/E000002");
		stepAudioEstudioPacktar
				.setStepDescription("PLAY => DIRECCION Y HORARIO , ESTUDIO PACKTAR");
		cf.addTask(stepAudioEstudioPacktar);

		stepAudioNoEsPosibleConAdicional = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioNoEsPosibleConAdicional.setPlayfile("coto/A000082");
		stepAudioNoEsPosibleConAdicional
				.setStepDescription("PLAY => NO ES POSIBLE MODIFICAR EL RESUMEN DE CUENTA CON UN ADICIONAL");
		cf.addTask(stepAudioNoEsPosibleConAdicional);

		stepAudioEstudioCobranzasTCI = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioEstudioCobranzasTCI.setPlayfile("coto/E000003");
		stepAudioEstudioCobranzasTCI
				.setStepDescription("PLAY => DIRECCION Y HORARIO , ESTUDIO COBRANZAS TCI");
		cf.addTask(stepAudioEstudioCobranzasTCI);

		stepAudioEstudioDogliani = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioEstudioDogliani.setPlayfile("coto/E000004");
		stepAudioEstudioDogliani
				.setStepDescription("PLAY => DIRECCION Y HORARIO , ESTUDIO DOGLIANI");
		cf.addTask(stepAudioEstudioDogliani);

		stepAudioEstudioRecoveryc = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioEstudioRecoveryc.setPlayfile("coto/E000005");
		stepAudioEstudioRecoveryc
				.setStepDescription("PLAY => DIRECCION Y HORARIO , ESTUDIO RECOVERYC");
		cf.addTask(stepAudioEstudioRecoveryc);

		stepAudioEstudioML = (StepPlay) StepFactory.createStep(StepType.Play,
				UUID.randomUUID());
		stepAudioEstudioML.setPlayfile("coto/E000006");
		stepAudioEstudioML
				.setStepDescription("PLAY => DIRECCION Y HORARIO , ESTUDIO ML");
		cf.addTask(stepAudioEstudioML);

		stepAudioEstadoCuentaMora = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioEstadoCuentaMora.setPlayfile("coto/A000079");
		stepAudioEstadoCuentaMora
				.setStepDescription("PLAY => ESTADO CUENTA MORA");
		cf.addTask(stepAudioEstadoCuentaMora);

		stepAudioCambioResemenCuenta = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioCambioResemenCuenta.setPlayfile("coto/A000085");
		stepAudioCambioResemenCuenta
				.setStepDescription("PLAY => CAMBIO RESUMEN CUENTA OK");
		cf.addTask(stepAudioCambioResemenCuenta);

		stepAudioTarjetaIncorrecta = (StepPlay) StepFactory.createStep(
				StepType.Play, UUID.randomUUID());
		stepAudioTarjetaIncorrecta.setPlayfile("coto/A0000011");
		stepAudioTarjetaIncorrecta
				.setStepDescription("PLAY => TARJETA INCORRECTA");
		cf.addTask(stepAudioTarjetaIncorrecta);

		/* --- PLAY READ --- */

		stepAudioIrregularidadCuenta = (StepPlayRead) StepFactory.createStep(
				StepType.PlayRead, UUID.randomUUID());
		stepAudioIrregularidadCuenta.setPlayFile("coto/A000083");
		stepAudioIrregularidadCuenta
				.setContextVariableName(resultadoAudioMenuIrregularidadCuenta);
		stepAudioIrregularidadCuenta
				.setStepDescription("PLAY => NO ES POSIBLE MODIFICAR EL RESUMEN DE CUENTA POR UNA IRREGULARIDAD DE CUENTA");
		cf.addTask(stepAudioIrregularidadCuenta);

		stepAudioMenuPrincipal = (StepPlayRead) StepFactory.createStep(
				StepType.PlayRead, UUID.randomUUID());
		stepAudioMenuPrincipal.setPlayFile("coto/A000002");
		stepAudioMenuPrincipal.setPlayMaxDigits(1);
		stepAudioMenuPrincipal.setContextVariableName(resultadoAudioInicio);
		stepAudioMenuPrincipal.setPlayTimeout(5000L);
		stepAudioMenuPrincipal
				.setStepDescription("PLAYREAD => MENU PRINCIPAL IVR COTO");
		cf.addTask(stepAudioMenuPrincipal);

		stepAudioMenuResumenCuenta = (StepPlayRead) StepFactory.createStep(
				StepType.PlayRead, UUID.randomUUID());
		stepAudioMenuResumenCuenta.setPlayFile("coto/A000084");
		stepAudioMenuResumenCuenta.setPlayMaxDigits(1);
		stepAudioMenuResumenCuenta
				.setContextVariableName(resultadoAudioMenuResumenCuenta);
		stepAudioMenuResumenCuenta.setPlayTimeout(5000L);
		stepAudioMenuResumenCuenta
				.setStepDescription("PLAYREAD => RESUMEN DE CUENTA");
		cf.addTask(stepAudioMenuResumenCuenta);

		stepAudioMenuNuevosClientes = (StepPlayRead) StepFactory.createStep(
				StepType.PlayRead, UUID.randomUUID());
		stepAudioMenuNuevosClientes.setPlayFile("coto/A000003");
		stepAudioMenuNuevosClientes.setPlayMaxDigits(1);
		stepAudioMenuNuevosClientes
				.setContextVariableName(resultadoAudioMenuNuevosClientes);
		stepAudioMenuNuevosClientes.setPlayTimeout(5000L);
		stepAudioMenuNuevosClientes
				.setStepDescription("PLAYREAD => NUEVOS CLIENTES COTO");
		cf.addTask(stepAudioMenuNuevosClientes);

		stepAudioMenuIteracionMora = (StepPlayRead) StepFactory.createStep(
				StepType.PlayRead, UUID.randomUUID());
		stepAudioMenuIteracionMora.setPlayFile("coto/M000119");
		stepAudioMenuIteracionMora
				.setContextVariableName(resultadoMenuIteracionMenuMoraContextVar);
		stepAudioMenuIteracionMora
				.setStepDescription("PLAYREAD => SUB MENU DIRECCIONES");
		cf.addTask(stepAudioMenuIteracionMora);

		stepAudioMenuDirecciones = (StepPlayRead) StepFactory.createStep(
				StepType.PlayRead, UUID.randomUUID());
		stepAudioMenuDirecciones.setPlayFile("coto/A000044");
		stepAudioMenuDirecciones
				.setContextVariableName(resultadoMenuDireccionesContextVar);
		stepAudioMenuDirecciones
				.setStepDescription("PLAYREAD => MENU DIRECCIONES");
		cf.addTask(stepAudioMenuDirecciones);

		stepAudioSubMenuDirecciones = (StepPlayRead) StepFactory.createStep(
				StepType.PlayRead, UUID.randomUUID());
		stepAudioSubMenuDirecciones.setPlayFile("coto/M000029");
		stepAudioSubMenuDirecciones
				.setContextVariableName(resultadoSubMenuDireccionesContextVar);
		stepAudioSubMenuDirecciones
				.setStepDescription("PLAYREAD => SUB MENU DIRECCIONES");
		cf.addTask(stepAudioSubMenuDirecciones);

		stepAudioSubMenu = (StepPlayRead) StepFactory.createStep(
				StepType.PlayRead, UUID.randomUUID());
		stepAudioSubMenu.setPlayFile("coto/M000123");
		stepAudioSubMenu.setContextVariableName(resultadoSubMenuContextVar);
		stepAudioSubMenu
				.setStepDescription("PLAYREAD => SUB MENU, OPCION SALDOS");
		cf.addTask(stepAudioSubMenu);

		stepAudioMenuDerivo = (StepPlayRead) StepFactory.createStep(
				StepType.PlayRead, UUID.randomUUID());
		stepAudioMenuDerivo.setPlayFile("coto/M000090");
		stepAudioMenuDerivo.setContextVariableName(menuDerivoContextVar);
		stepAudioMenuDerivo.setStepDescription("PLAYREAD => MENU DERIVO");
		cf.addTask(stepAudioMenuDerivo);

		stepAudioMenuBloqueo = (StepPlayRead) StepFactory.createStep(
				StepType.PlayRead, UUID.randomUUID());
		stepAudioMenuBloqueo.setPlayFile("coto/M000090");
		stepAudioMenuBloqueo
				.setContextVariableName(resultadoMenuBloqueoContextVar);
		stepAudioMenuBloqueo.setStepDescription("PLAYREAD => MENU BLOQUEO");
		cf.addTask(stepAudioMenuBloqueo);

		stepAudioMenuPrincipalNuevosClientes = (StepPlayRead) StepFactory
				.createStep(StepType.PlayRead, UUID.randomUUID());
		stepAudioMenuPrincipalNuevosClientes.setPlayFile("coto/M000028");
		stepAudioMenuPrincipalNuevosClientes.setPlayMaxDigits(1);
		stepAudioMenuPrincipalNuevosClientes
				.setContextVariableName(resultadoAudioMenuPrincipalNuevosClientes);
		stepAudioMenuPrincipalNuevosClientes.setPlayTimeout(5000L);
		stepAudioMenuPrincipalNuevosClientes
				.setStepDescription("PLAYREAD => MENU PRINCIPAL NUEVOS CLIENTES COTO");
		cf.addTask(stepAudioMenuPrincipalNuevosClientes);

		stepAudioMenuDenuncias = (StepPlayRead) StepFactory.createStep(
				StepType.PlayRead, UUID.randomUUID());
		stepAudioMenuDenuncias.setPlayFile("coto/M0000112");
		stepAudioMenuDenuncias.setPlayMaxDigits(1);
		stepAudioMenuDenuncias
				.setContextVariableName(resultadoAudioMenuDenuncia);
		stepAudioMenuDenuncias.setPlayTimeout(5000L);
		stepAudioMenuDenuncias
				.setStepDescription("PLAYREAD => MENU DENUNCIAS COTO");
		cf.addTask(stepAudioMenuDenuncias);

		/* --- MENU --- */

		stepMenuInicial = (StepMenu) StepFactory.createStep(StepType.Menu,
				UUID.randomUUID());
		stepMenuInicial.setContextVariableName(resultadoAudioInicio);
		stepMenuInicial.setStepDescription("MENU => MENU PRINCIPAL IVR COTO");
		cf.addTask(stepMenuInicial);

		stepMenuResumenCuenta = (StepMenu) StepFactory.createStep(
				StepType.Menu, UUID.randomUUID());
		stepMenuResumenCuenta
				.setContextVariableName(resultadoAudioMenuResumenCuenta);
		stepMenuResumenCuenta.setStepDescription("MENU => RESUMEN DE CUENTA");
		cf.addTask(stepMenuResumenCuenta);

		stepMenuNuevosClientes = (StepMenu) StepFactory.createStep(
				StepType.Menu, UUID.randomUUID());
		stepMenuNuevosClientes
				.setContextVariableName(resultadoAudioMenuNuevosClientes);
		stepMenuNuevosClientes
				.setStepDescription("MENU => NUEVOS CLIENTES COTO");
		cf.addTask(stepMenuNuevosClientes);

		stepMenuPrincipalNuevosClientes = (StepMenu) StepFactory.createStep(
				StepType.Menu, UUID.randomUUID());
		stepMenuPrincipalNuevosClientes
				.setContextVariableName(resultadoAudioMenuPrincipalNuevosClientes);
		stepMenuPrincipalNuevosClientes
				.setStepDescription("MENU => MENU PRINCIPAL NUEVOS CLIENTES COTO");
		cf.addTask(stepMenuPrincipalNuevosClientes);

		stepMenuDenuncia = (StepMenu) StepFactory.createStep(StepType.Menu,
				UUID.randomUUID());
		stepMenuDenuncia.setContextVariableName(resultadoAudioMenuDenuncia);
		stepMenuDenuncia.setStepDescription("MENU => MENU DENUNCIAS COTO");
		cf.addTask(stepMenuDenuncia);

		stepSubMenu = (StepMenu) StepFactory.createStep(StepType.Menu,
				UUID.randomUUID());
		stepSubMenu.setContextVariableName(resultadoSubMenuContextVar);
		stepSubMenu.setStepDescription("MENU => MENU SALDOS");
		cf.addTask(stepSubMenu);

		stepMenuDerivo = (StepMenu) StepFactory.createStep(StepType.Menu,
				UUID.randomUUID());
		stepMenuDerivo.setContextVariableName(menuDerivoContextVar);
		stepMenuDerivo.setStepDescription("MENU => MENU DERIVO");
		cf.addTask(stepMenuDerivo);

		stepMenuBloqueo = (StepMenu) StepFactory.createStep(StepType.Menu,
				UUID.randomUUID());
		stepMenuBloqueo.setStepDescription("MENU => MENU BLOQUEO");
		stepMenuBloqueo.setContextVariableName(resultadoMenuBloqueoContextVar);
		cf.addTask(stepMenuBloqueo);

		stepMenuDirecciones = (StepMenu) StepFactory.createStep(StepType.Menu,
				UUID.randomUUID());
		stepMenuDirecciones
				.setContextVariableName(resultadoMenuDireccionesContextVar);
		stepMenuDirecciones.setStepDescription("MENU => MENU DIRECCIONES");
		cf.addTask(stepMenuDirecciones);

		stepSubMenuDirecciones = (StepMenu) StepFactory.createStep(
				StepType.Menu, UUID.randomUUID());
		stepSubMenuDirecciones
				.setContextVariableName(resultadoSubMenuDireccionesContextVar);
		stepSubMenuDirecciones
				.setStepDescription("MENU => SUB MENU DIRECCIONES");
		cf.addTask(stepSubMenuDirecciones);

		stepMenuIteracionMora = (StepMenu) StepFactory.createStep(
				StepType.Menu, UUID.randomUUID());
		stepMenuIteracionMora
				.setContextVariableName(resultadoMenuIteracionMenuMoraContextVar);
		stepMenuIteracionMora.setStepDescription("MENU => ITERACIONES MORA");
		cf.addTask(stepMenuIteracionMora);

		stepMenuIrregularidadCuenta = (StepMenu) StepFactory.createStep(
				StepType.Menu, UUID.randomUUID());
		stepMenuIrregularidadCuenta
				.setContextVariableName(resultadoAudioMenuIrregularidadCuenta);
		stepMenuIrregularidadCuenta
				.setStepDescription("MENU => IRREGULARIDAD CUENTA");
		cf.addTask(stepMenuIrregularidadCuenta);

		/* --- PLAY FROM VAR --- */

		stepAudioPrevioDerivoAsesor = (StepPlayFromVar) StepFactory.createStep(
				StepType.PlayFromVar, UUID.randomUUID());
		stepAudioPrevioDerivoAsesor.setPlayfile(audioFueraHorarioContextVar);
		stepAudioPrevioDerivoAsesor
				.setStepDescription("PLAYFROMVAR => DISUADE DERIVO OPERADOR COTO");
		cf.addTask(stepAudioPrevioDerivoAsesor);

		/* --- SET VARIABLE --- */

		stepSetDni = (StepSetAsteriskVariable) StepFactory.createStep(
				StepType.SetAsteriskVariable, UUID.randomUUID());
		stepSetDni.setVariableName("numdoc");
		stepSetDni.setContextVariableName(dniContextVar);
		stepSetDni
				.setStepDescription("SETASTERISKVARIABLE => SETEA VARIABLE DNI");
		cf.addTask(stepSetDni);

		stepSetTarjeta = (StepSetAsteriskVariable) StepFactory.createStep(
				StepType.SetAsteriskVariable, UUID.randomUUID());
		stepSetTarjeta.setVariableName("numtarj");
		stepSetTarjeta.setContextVariableName(tarjetaContexVar);
		stepSetTarjeta
				.setStepDescription("SETASTERISKVARIABLE => SETEA VARIABLE TARJETA");
		cf.addTask(stepSetTarjeta);

		/* --- TRAMA JPOS --- */

		enviaTramaResumenCuentaJpos = (StepSendJPOS) StepFactory.createStep(
				StepType.SendJPOS, UUID.randomUUID());
		enviaTramaResumenCuentaJpos
				.setContextVariableTipoMensaje(envioServerJposPrecargadasContexVar);
		enviaTramaResumenCuentaJpos
				.setContextVariableName(retornoJPOSResumenCuenta);
		enviaTramaResumenCuentaJpos
				.setContextVariableRspJpos(retornoMsgJPOSResumenCuenta);
		enviaTramaResumenCuentaJpos.addformatoVariables(0,
				mensajeCambioDeResumenJpos);
		enviaTramaResumenCuentaJpos.addformatoVariables(1, tarjetaContexVar);
		enviaTramaResumenCuentaJpos.addformatoVariables(2,
				consultaCambioDeResumenContexVar);
		enviaTramaResumenCuentaJpos.addformatoVariables(3, fillerContexVar);
		enviaTramaResumenCuentaJpos.addformatoVariables(4, idLlamadaContexVar);
		enviaTramaResumenCuentaJpos.addformatoVariables(5, whisperContextVar);
		enviaTramaResumenCuentaJpos
				.setStepDescription("SENDJPOS => ENVIA TRAMA CONSULTA JPOS RESUMEN DE CUENTA");
		cf.addTask(enviaTramaResumenCuentaJpos);

		enviaTramaCambioResumenCuentaJpos = (StepSendJPOS) StepFactory
				.createStep(StepType.SendJPOS, UUID.randomUUID());
		enviaTramaCambioResumenCuentaJpos
				.setContextVariableTipoMensaje(envioServerJposPrecargadasContexVar);
		enviaTramaCambioResumenCuentaJpos
				.setContextVariableName(retornoJPOSResumenCuenta);
		enviaTramaCambioResumenCuentaJpos
				.setContextVariableRspJpos(retornoMsgJPOSResumenCuenta);
		enviaTramaCambioResumenCuentaJpos.addformatoVariables(0,
				mensajeCambioDeResumenJpos);
		enviaTramaCambioResumenCuentaJpos.addformatoVariables(1,
				tarjetaContexVar);
		enviaTramaCambioResumenCuentaJpos.addformatoVariables(2,
				confirmacionCambioDeResumenContexVar);
		enviaTramaCambioResumenCuentaJpos.addformatoVariables(3,
				fillerContexVar);
		enviaTramaCambioResumenCuentaJpos.addformatoVariables(4,
				idLlamadaContexVar);
		enviaTramaCambioResumenCuentaJpos.addformatoVariables(5,
				whisperContextVar);
		enviaTramaCambioResumenCuentaJpos
				.setStepDescription("SENDJPOS => ENVIA TRAMA CONFIRMACION JPOS RESUMEN DE CUENTA");
		cf.addTask(enviaTramaCambioResumenCuentaJpos);

		enviaTramaJpos = (StepSendJPOS) StepFactory.createStep(
				StepType.SendJPOS, UUID.randomUUID());
		enviaTramaJpos
				.setContextVariableTipoMensaje(envioServerJposConsultasContexVar);
		enviaTramaJpos.setContextVariableName(retornoJPOS);
		enviaTramaJpos.setContextVariableRspJpos(retornoMsgJPOS);
		enviaTramaJpos.addformatoVariables(0, mensajeSaldosJpos);
		enviaTramaJpos.addformatoVariables(1, numeroDeLineaContexVar);
		enviaTramaJpos.addformatoVariables(2, tarjetaContexVar);
		enviaTramaJpos.addformatoVariables(3, fillerContexVar);
		enviaTramaJpos.addformatoVariables(4, idLlamadaContexVar);
		enviaTramaJpos.addformatoVariables(5, whisperContextVar);
		enviaTramaJpos.setStepDescription("SENDJPOS => ENVIA TRAMA JPOS");
		cf.addTask(enviaTramaJpos);

		/* --- CONDITIONAL --- */

		evalBinCoto = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalBinCoto.setStepDescription("CONDITIONAL => BIN COTO");
		cf.addTask(evalBinCoto);

		evalBinCotoResumenDeCuenta = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalBinCotoResumenDeCuenta
				.setStepDescription("CONDITIONAL => BIN COTO");
		cf.addTask(evalBinCotoResumenDeCuenta);

		evalContadorMenuResumenCuenta = (StepConditional) StepFactory
				.createStep(StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorMenuResumenCuenta
				.setStepDescription("CONDITIONAL => INTENTOS INGRESO MENU RESUMEN CUENTA");
		cf.addTask(evalContadorMenuResumenCuenta);

		evalContadorIngresoRutina = (StepConditional) StepFactory.createStep(
				StepType.Conditional, UUID.randomUUID());
		evalContadorIngresoRutina
				.setStepDescription("CONDITIONAL =>  INTENTOS INGRESO RUTINA");
		cf.addTask(evalContadorIngresoRutina);

		evalContadorDNIJPOS = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorDNIJPOS
				.setStepDescription("CONDITIONAL =>  CANTIDAD DE INTENTOS DNI, POR ERROR JPOS: 02");
		cf.addTask(evalContadorDNIJPOS);

		evalContadorMenuNuevosClientes = (StepConditional) StepFactory
				.createStep(StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorMenuNuevosClientes
				.setStepDescription("CONDITIONAL => MENU NUEVOS CLIENTES");
		cf.addTask(evalContadorMenuNuevosClientes);

		evalContadorFechaJPOS = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorFechaJPOS
				.setStepDescription("CONDITIONAL => INTENTOS FECHA CONTRA , JPOS CODIGO 03");
		cf.addTask(evalContadorFechaJPOS);

		evalTarjPendActivacion = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalTarjPendActivacion
				.setStepDescription("CONDITIONAL =>  TARJETAS PENDIENTE DE ACTIVACION");
		cf.addTask(evalTarjPendActivacion);

		evalContadorTarjetaJPOS = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorTarjetaJPOS
				.setStepDescription("CONDITIONAL => INTENTOS TARJETA CONTRA JPOS");
		cf.addTask(evalContadorTarjetaJPOS);

		evalContadorTarjetaJPOSRCuenta = (StepConditional) StepFactory
				.createStep(StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorTarjetaJPOSRCuenta
				.setStepDescription("CONDITIONAL => INTENTOS TARJETA CONTRA JPOS RESUMEN CUENTA");
		cf.addTask(evalContadorTarjetaJPOSRCuenta);

		evalContadorMenuDerivo = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorMenuDerivo
				.setStepDescription("CONDITIONAL => INTENTOS MENU DERIVO OPERADOR");
		cf.addTask(evalContadorMenuDerivo);

		evalContadorMenuDenuncia = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorMenuDenuncia
				.setStepDescription("CONDITIONAL => INTENTOS MENU DENUNCIAS");
		cf.addTask(evalContadorMenuDenuncia);

		evalContadorMenuIrregularidadCuenta = (StepConditional) StepFactory
				.createStep(StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorMenuIrregularidadCuenta
				.setStepDescription("CONDITIONAL => INTENTOS MENU IRREGULARIDAD CUENTA");
		cf.addTask(evalContadorMenuIrregularidadCuenta);

		evalContadorMenuDirecciones = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorMenuDirecciones
				.setStepDescription("CONDITIONAL => INTENTOS MENU DIRECCIONES");
		cf.addTask(evalContadorMenuDirecciones);

		evalContadorSubMenu = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorSubMenu
				.setStepDescription("CONDITIONAL => INTENTOS SUB MENU, OPCION 1");
		cf.addTask(evalContadorSubMenu);

		evalContadorBINTarjeta = (StepConditional) StepFactory.createStep(
				StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorBINTarjeta
				.setStepDescription("CONDITIONAL => INTENTOS TARJETA BIN COTO");
		cf.addTask(evalContadorBINTarjeta);

		evalContadorBINTarjetaRCuenta = (StepConditional) StepFactory
				.createStep(StepFactory.StepType.Conditional, UUID.randomUUID());
		evalContadorBINTarjetaRCuenta
				.setStepDescription("CONDITIONAL => INTENTOS TARJETA BIN COTO");
		cf.addTask(evalContadorBINTarjetaRCuenta);

		/* --- COUNTER --- */

		contadorIntentosIngresoRutina = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosIngresoRutina
				.setContextVariableName(intentosIngresoContextVar);
		contadorIntentosIngresoRutina
				.setStepDescription("COUNTER => INTENTOS INGRESO RUTINA");
		cf.addTask(contadorIntentosIngresoRutina);

		contadorIntentosDNIJPOS = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosDNIJPOS.setContextVariableName(intentosDniContextVar);
		contadorIntentosDNIJPOS
				.setStepDescription("COUNTER => CANTIDAD DE INTENTOS DNI, POR ERROR JPOS: 02");
		cf.addTask(contadorIntentosDNIJPOS);

		contadorIntentosMenuNuevosClientes = (StepCounter) StepFactory
				.createStep(StepType.Counter, UUID.randomUUID());
		contadorIntentosMenuNuevosClientes
				.setContextVariableName(intentosMenuNuevosClientesContextVar);
		contadorIntentosMenuNuevosClientes
				.setStepDescription("COUNTER => MENU NUEVOS CLIENTES");
		cf.addTask(contadorIntentosMenuNuevosClientes);

		contadorIntentosFechaJPOS = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosFechaJPOS
				.setContextVariableName(intentosFechaContextVar);
		contadorIntentosFechaJPOS
				.setStepDescription("COUNTER => INTENTOS FECHA CONTRA , JPOS CODIGO 03");
		cf.addTask(contadorIntentosFechaJPOS);

		contadorIntentosMenuResumenCuenta = (StepCounter) StepFactory
				.createStep(StepType.Counter, UUID.randomUUID());
		contadorIntentosMenuResumenCuenta
				.setContextVariableName(intentosMenuResumenCuentaContextVar);
		contadorIntentosMenuResumenCuenta
				.setStepDescription("COUNTER => INTENTOS MENU RESUMEN CUENTA");
		cf.addTask(contadorIntentosMenuResumenCuenta);

		contadorIntentosTarjetaJPOS = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosTarjetaJPOS
				.setContextVariableName(intentosTarjetaContextVar);
		contadorIntentosTarjetaJPOS
				.setStepDescription("COUNTER => INTENTOS TARJETA CONTRA JPOS");
		cf.addTask(contadorIntentosTarjetaJPOS);

		contadorIntentosTarjetaJPOSRCuenta = (StepCounter) StepFactory
				.createStep(StepType.Counter, UUID.randomUUID());
		contadorIntentosTarjetaJPOSRCuenta
				.setContextVariableName(intentosTarjetaContextVar);
		contadorIntentosTarjetaJPOSRCuenta
				.setStepDescription("COUNTER => INTENTOS TARJETA CONTRA JPOS RESUMEN CUENTA");
		cf.addTask(contadorIntentosTarjetaJPOSRCuenta);

		contadorIntentosMenuDerivo = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosMenuDerivo
				.setContextVariableName(intentosMenuDerivoContextVar);
		contadorIntentosMenuDerivo
				.setStepDescription("COUNTER => INTENTOS MENU DERIVO");
		cf.addTask(contadorIntentosMenuDerivo);

		contadorIntentosMenuIrregularidadCuenta = (StepCounter) StepFactory
				.createStep(StepType.Counter, UUID.randomUUID());
		contadorIntentosMenuIrregularidadCuenta
				.setContextVariableName(intentosMenuIrregularidadCuentaContextVar);
		contadorIntentosMenuIrregularidadCuenta
				.setStepDescription("COUNTER => INTENTOS MENU IRREGULARIDAD CUENTA");
		cf.addTask(contadorIntentosMenuIrregularidadCuenta);

		contadorIntentosMenuDenuncia = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosMenuDenuncia
				.setContextVariableName(intentosMenuDenunciaContextVar);
		contadorIntentosMenuDenuncia
				.setStepDescription("COUNTER => INTENTOS MENU DENUNCIAS");
		cf.addTask(contadorIntentosMenuDenuncia);

		contadorIntentosMenuDirecciones = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosMenuDirecciones
				.setContextVariableName(intentosMenuDireccionesContextVar);
		contadorIntentosMenuDirecciones
				.setStepDescription("COUNTER => INTENTOS MENU DIRECCIONES");
		cf.addTask(contadorIntentosMenuDirecciones);

		contadorIntentosSubMenu = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosSubMenu
				.setContextVariableName(intentosSubMenuContextVar);
		contadorIntentosSubMenu
				.setStepDescription("COUNTER => INTENTOS SUB MENU, OPCION 1");
		cf.addTask(contadorIntentosSubMenu);

		contadorIntentosBINTarjeta = (StepCounter) StepFactory.createStep(
				StepType.Counter, UUID.randomUUID());
		contadorIntentosBINTarjeta
				.setContextVariableName(intentosTarjetaContextVar);
		contadorIntentosBINTarjeta
				.setStepDescription("COUNTER => INTENTOS TARJETA BIN COTO");
		cf.addTask(contadorIntentosBINTarjeta);

		contadorIntentosBINTarjetaRCuenta = (StepCounter) StepFactory
				.createStep(StepType.Counter, UUID.randomUUID());
		contadorIntentosBINTarjetaRCuenta
				.setContextVariableName(intentosTarjetaContextVar);
		contadorIntentosBINTarjetaRCuenta
				.setStepDescription("COUNTER => INTENTOS TARJETA BIN COTO");
		cf.addTask(contadorIntentosBINTarjetaRCuenta);

		/* --- SWITCH --- */

		evalRetJPOS = (StepSwitch) StepFactory.createStep(StepType.Switch,
				UUID.randomUUID());
		evalRetJPOS.setContextVariableName(retornoJPOS);
		evalRetJPOS.setStepDescription("SWITCH => CODIGO RETORNO JPOS");
		cf.addTask(evalRetJPOS);

		evalCodEstudio = (StepSwitch) StepFactory.createStep(StepType.Switch,
				UUID.randomUUID());
		evalCodEstudio.setContextVariableName(codigoDeEstudioContextVar);
		evalCodEstudio.setStepDescription("SWITCH =>  CODIGO DE ESTUDIO");
		cf.addTask(evalCodEstudio);

		evalIngresoMenuDirecciones = (StepSwitch) StepFactory.createStep(
				StepType.Switch, UUID.randomUUID());
		evalIngresoMenuDirecciones
				.setContextVariableName(resultadoMenuDireccionesContextVar);
		evalIngresoMenuDirecciones
				.setStepDescription("SWITCH =>  MENU DIRECCIONES");
		cf.addTask(evalIngresoMenuDirecciones);

		evalRetJPOSResumenCuenta = (StepSwitch) StepFactory.createStep(
				StepType.Switch, UUID.randomUUID());
		evalRetJPOSResumenCuenta
				.setContextVariableName(retornoJPOSResumenCuenta);
		evalRetJPOSResumenCuenta
				.setStepDescription("SWITCH => CODIGO RETORNO JPOS RESUMEN CUENTA");
		cf.addTask(evalRetJPOSResumenCuenta);

		/* --- TIME CONDITION --- */

		obtieneHorario = (StepTimeConditionDB) StepFactory.createStep(
				StepType.TimeConditionDB, UUID.randomUUID());
		obtieneHorario.setContextVarEmpresa(empresaIdContextVar);
		obtieneHorario.setContextVarServicio(servicioIdContextVar);
		obtieneHorario.setContextVarAudio(audioFueraHorarioContextVar);
		obtieneHorario
				.setStepDescription("TIMECONDITIONALDB => OBTIENE HORARIOS BASE DE DATOS");
		cf.addTask(obtieneHorario);

		/* --- PARSER TRAMA --- */

		parserTarjeta = (StepParseSaldosTarjeta) StepFactory.createStep(
				StepFactory.StepType.ParseSaldoTarjeta, UUID.randomUUID());
		parserTarjeta.setRetornoMsgJPOS(retornoMsgJPOS);
		parserTarjeta
				.setCierreProxResumDiaContextVar(cierreProxResumDiaContextVar);
		parserTarjeta.setCodigoDeEstudioContextVar(codigoDeEstudioContextVar);
		parserTarjeta
				.setCierreProxResumMesContextVar(cierreProxResumMesContextVar);
		parserTarjeta
				.setCierreProxResumAnioContextVar(cierreProxResumAnioContextVar);
		parserTarjeta
				.setFechaVencimientoUltResumContextVarDia(fechaVencimientoUltResumContextVarDia);
		parserTarjeta
				.setFechaVencimientoUltResumContextVarMes(fechaVencimientoUltResumContextVarMes);
		parserTarjeta
				.setFechaVencimientoUltResumContextVarAnio(fechaVencimientoUltResumContextVarAnio);
		parserTarjeta
				.setPagoMinPendienteDeCanceUltResumContextVar(pagoMinPendienteDeCanceUltResumContextVar);
		parserTarjeta.setPagoMinUltResumContextVar(pagoMinUltResumContextVar);
		parserTarjeta.setSaldoCuentaContextVar(saldoCuentaContextVar);
		parserTarjeta.setSaldoUltResumContextVar(saldoUltResumContextVar);
		parserTarjeta.setSalEnCuotasContextVar(salEnCuotasContextVar);
		parserTarjeta.setSalEnUnPagoContextVar(salEnUnPagoContextVar);
		parserTarjeta
				.setSalPendienteDeCanceUltResumContextVar(salPendienteDeCanceUltResumContextVar);
		parserTarjeta
				.setVencimientoProxResumAnioContextVar(vencimientoProxResumAnioContextVar);
		parserTarjeta
				.setVencimientoProxResumDiaContextVar(vencimientoProxResumDiaContextVar);
		parserTarjeta
				.setVencimientoProxResumMesContextVar(vencimientoProxResumMesContextVar);
		parserTarjeta.setTotalPagUltResumContextVar(totalPagUltResumContextVar);
		parserTarjeta
				.setPagoMinPendienteDeCanceUltDecimalResumContextVar(pagoMinPendienteDeCanceUltDecimalResumContextVar);
		parserTarjeta
				.setPagoMinUltResumDecimContextVar(pagoMinUltResumDecimContextVar);
		parserTarjeta
				.setSaldoCuentaDecimalContextVar(saldoCuentaDecimalContextVar);
		parserTarjeta
				.setSaldoUltResumDecimalContextVar(saldoUltResumDecimalContextVar);
		parserTarjeta
				.setSalEnCuotasDecimalContextVar(salEnCuotasDecimalContextVar);
		parserTarjeta
				.setSalEnUnPagoDecimalContextVar(salEnUnPagoDecimalContextVar);
		parserTarjeta
				.setTotalPagUltResumDecimalContextVar(totalPagUltResumDecimalContextVar);
		parserTarjeta
				.setSalPendienteDeCanceUltResumDecimalContextVar(salPendienteDeCanceUltResumDecimalContextVar);
		parserTarjeta
				.setTarjetaPendienteDeActivacionContextVar(tarjetaPendienteDeActivacionContextVar);
		parserTarjeta
				.setStepDescription("PARSESALDOTARJETA => PARSEO SALDO DE TARJETA");
		cf.addTask(parserTarjeta);

		/* --- GRUPOS --- */

		// RETORNOS DE JPOS, PARA ITERACIONES//

		pideTarjetaGrp = (PideTarjeta) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.pideTarjeta);
		pideTarjetaGrp.setAudioTarjeta("coto/A0000010");
		pideTarjetaGrp.setAudioSuTarjetaEs("RUTINAPIN/RUTINA_PIN026");
		pideTarjetaGrp.setAudioTarjetaInvalido("coto/A0000011");
		pideTarjetaGrp.setConfirmaTarjetaContextVar(confirmaTarjetaContextVar);
		pideTarjetaGrp.setIntentosTarjetaContextVar(intentosTarjetaContextVar);
		pideTarjetaGrp.setTarjetaContextVar(tarjetaContexVar);

		pideTarjetaResumenDeCuenta = (PideTarjeta) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.pideTarjeta);
		pideTarjetaResumenDeCuenta.setAudioTarjeta("coto/A0000010");
		pideTarjetaResumenDeCuenta
				.setAudioSuTarjetaEs("RUTINAPIN/RUTINA_PIN026");
		pideTarjetaResumenDeCuenta.setAudioTarjetaInvalido("coto/A0000011");
		pideTarjetaResumenDeCuenta
				.setConfirmaTarjetaContextVar(confirmaTarjetaContextVar);
		pideTarjetaResumenDeCuenta
				.setIntentosTarjetaContextVar(intentosTarjetaContextVar);
		pideTarjetaResumenDeCuenta.setTarjetaContextVar(tarjetaContexVar);

		pideDniGrp = (PideDni) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.pideDni);
		pideDniGrp.setAudioDni("coto/A000007");
		pideDniGrp.setAudioValidateDni("RUTINAPIN/RUTINA_PIN010");
		pideDniGrp.setAudioDniInvalido("coto/A000009");
		pideDniGrp.setAudioSuDniEs("coto/A000052");
		pideDniGrp.setDniContextVar(dniContextVar);
		pideDniGrp.setConfirmaDniContextVar(confirmaDniContextVar);
		pideDniGrp.setIntentosDniContextVar(intentosDniContextVar);

		pideFechaGrp = (PideFecha) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.pideFecha);
		pideFechaGrp.setAudioFecha("coto/A000055");
		pideFechaGrp.setAudioValidateFecha("RUTINAPIN/RUTINA_PIN010");
		pideFechaGrp.setAudioFechaInvalida("coto/A000056");
		pideFechaGrp.setAudioSuFechaEs("coto/A000058");
		pideFechaGrp.setAudioAnio("coto/A900012");
		pideFechaGrp.setAudioMes("coto/A900011");
		pideFechaGrp.setAudioDia("coto/A900010");
		pideFechaGrp.setfechaContextVar(fechaContextVar);
		pideFechaGrp.setContextVarDia(contextVarDia);
		pideFechaGrp.setContextVarMes(contextVarMes);
		pideFechaGrp.setContextVarAnio(contextVarAnio);
		pideFechaGrp.setConfirmaFechaContextVar(confirmaFechaContextVar);
		pideFechaGrp.setIntentosFechaContextVar(intentosFechaContextVar);

		// PIDE DNI PROPIO DEL IVR COTO//

		pideDniBloqueoGrp = (PideDni) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.pideDni);
		pideDniBloqueoGrp.setAudioDni("coto/A000007");
		pideDniBloqueoGrp.setAudioValidateDni("RUTINAPIN/RUTINA_PIN010");
		pideDniBloqueoGrp.setAudioDniInvalido("coto/A000009");
		pideDniBloqueoGrp.setAudioSuDniEs("coto/A000052");
		pideDniBloqueoGrp.setDniContextVar(dniContextVar);
		pideDniBloqueoGrp.setConfirmaDniContextVar(confirmaDniContextVar);
		pideDniBloqueoGrp.setIntentosDniContextVar(intentosDniContextVar);

		pideDniBloqueo2Grp = (PideDni) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.pideDni);
		pideDniBloqueo2Grp.setAudioDni("coto/A000007");
		pideDniBloqueo2Grp.setAudioValidateDni("RUTINAPIN/RUTINA_PIN010");
		pideDniBloqueo2Grp.setAudioDniInvalido("coto/A000009");
		pideDniBloqueo2Grp.setAudioSuDniEs("coto/A000052");
		pideDniBloqueo2Grp.setDniContextVar(dniContextVar);
		pideDniBloqueo2Grp.setConfirmaDniContextVar(confirmaDniContextVar);
		pideDniBloqueo2Grp.setIntentosDniContextVar(intentosDniContextVar);

		/* --- ARMA SALDO --- */

		armaSaldoTarjetaCotoGrp = (ArmaSaldoTarjetaCoto) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.armaSaldoTarjetaCOTO);
		armaSaldoTarjetaCotoGrp.setRetornoMsgJPOSContextVar(retornoMsgJPOS);
		armaSaldoTarjetaCotoGrp.setSalEnUnPagoContextVar(salEnUnPagoContextVar);
		armaSaldoTarjetaCotoGrp.setSalEnCuotasContextVar(salEnCuotasContextVar);
		armaSaldoTarjetaCotoGrp
				.setFechaVencimientoUltResumContextVarDia(fechaVencimientoUltResumContextVarDia);
		armaSaldoTarjetaCotoGrp
				.setFechaVencimientoUltResumContextVarMes(fechaVencimientoUltResumContextVarMes);
		armaSaldoTarjetaCotoGrp
				.setFechaVencimientoUltResumContextVarAnio(fechaVencimientoUltResumContextVarAnio);
		armaSaldoTarjetaCotoGrp
				.setSaldoUltResumContextVar(saldoUltResumContextVar);
		armaSaldoTarjetaCotoGrp
				.setTotalPagUltResumContextVar(totalPagUltResumContextVar);
		armaSaldoTarjetaCotoGrp
				.setPagoMinUltResumContextVar(pagoMinUltResumContextVar);
		armaSaldoTarjetaCotoGrp
				.setCierreProxResumDiaContextVar(cierreProxResumDiaContextVar);
		armaSaldoTarjetaCotoGrp
				.setCierreProxResumMesContextVar(cierreProxResumMesContextVar);
		armaSaldoTarjetaCotoGrp
				.setCierreProxResumAnioContextVar(cierreProxResumAnioContextVar);
		armaSaldoTarjetaCotoGrp
				.setPagoMinPendienteDeCanceUltResumContextVar(pagoMinPendienteDeCanceUltResumContextVar);
		armaSaldoTarjetaCotoGrp.setSaldoCuentaContextVar(saldoCuentaContextVar);
		armaSaldoTarjetaCotoGrp
				.setSalPendienteDeCanceUltResumContextVar(salPendienteDeCanceUltResumContextVar);
		armaSaldoTarjetaCotoGrp
				.setSaldoUltResumDecimalContextVar(saldoUltResumDecimalContextVar);
		armaSaldoTarjetaCotoGrp
				.setSaldoCuentaDecimalContextVar(saldoCuentaDecimalContextVar);
		armaSaldoTarjetaCotoGrp
				.setPagoMinPendienteDeCanceUltDecimalResumContextVar(pagoMinPendienteDeCanceUltDecimalResumContextVar);
		armaSaldoTarjetaCotoGrp
				.setPagoMinUltResumDecimContextVar(pagoMinUltResumDecimContextVar);
		armaSaldoTarjetaCotoGrp
				.setSaldoTotalPagUltResumDecimalContextVar(totalPagUltResumDecimalContextVar);
		armaSaldoTarjetaCotoGrp
				.setSalEnCuotasDecimalContextVar(salEnCuotasDecimalContextVar);
		armaSaldoTarjetaCotoGrp
				.setSalEnUnPagoDecimalContextVar(salEnUnPagoDecimalContextVar);
		armaSaldoTarjetaCotoGrp
				.setVencimientoProxResumDiaContextVar(vencimientoProxResumDiaContextVar);
		armaSaldoTarjetaCotoGrp
				.setVencimientoProxResumMesContextVar(vencimientoProxResumMesContextVar);
		armaSaldoTarjetaCotoGrp
				.setVencimientoProxResumAnioContextVar(vencimientoProxResumAnioContextVar);
		armaSaldoTarjetaCotoGrp
				.setSalPendienteDeCanceUltResumDecimalContextVar(salPendienteDeCanceUltResumDecimalContextVar);
		armaSaldoTarjetaCotoGrp.setScapeDigitContextVar(scapeDigitContextVar);

		// GRUPOS VARIOS

		bajaCompaniaAseguradora = (BajaCompaniaAseguradoraCOTO) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.bajaCompaniaAseguradoraCoto);
		bajaCompaniaAseguradora.setContextVar(ctx);
		// bajaCompaniaAseguradora.setStepIfFalseUUID("stepIfFalseUUID");

		activacionCotoGrp = (ActivacionTarjetaCOTO) StepGroupFactory
				.createStepGroup(StepGroupFactory.StepGroupType.activacionCoto);
		activacionCotoGrp.setContextVar(ctx);

		/* --- Set Audios Informa PIN --- */
		this.evalRetJPOSResumenCuenta();
		this.evalRetJPOS();
		this.evalCodEstudio();
		this.evalIngresoMenuDirecciones();
	}

	private void evalRetJPOS() {

		/*-------------------------------------------------------------------------------
		 * ret =>  00    ||   "Informa PIN"    
		 * ret =>  02    ||   "Nro de Documento erróneo"    
		 * ret =>  03    ||   "Fecha de nacimiento errónea"   
		 * ret =>  83    ||   "No es posible emitir PIN"   
		 * ret =>  84    ||   "No es posible emitir PIN"   
		 * ret =>  85    ||   "Falta PIN"   
		 * ret =>  96    ||   "Tarjeta inexistente"   
		 * ret =>  98    ||   "Error mensaje"   
		 * ret =>  99    ||   "Causa dif a 00 o Tj Vencida"   
		-------------------------------------------------------------------------------	*/

		evalRetJPOS.addSwitchValue("00",
				armaSaldoTarjetaCotoGrp.getInitialStep());

		evalRetJPOS.addSwitchValue("93", stepAudioInicioMenuMora.GetId());

		evalRetJPOS.addSwitchValue("96", obtieneHorario.GetId());
		evalRetJPOS.addSwitchValue("99", obtieneHorario.GetId());

		evalRetJPOS.addSwitchValue("98", stepAudioServNoDisponible.GetId());
		evalRetJPOS.addSwitchValue("EE", stepAudioServNoDisponible.GetId());

	}

	private void evalRetJPOSResumenCuenta() {

		/*-------------------------------------------------------------------------------
		 * ret =>  00    ||   "Informa PIN"    
		 * ret =>  02    ||   "Nro de Documento erróneo"    
		 * ret =>  03    ||   "Fecha de nacimiento errónea"   
		 * ret =>  83    ||   "No es posible emitir PIN"   
		 * ret =>  84    ||   "No es posible emitir PIN"   
		 * ret =>  85    ||   "Falta PIN"   
		 * ret =>  96    ||   "Tarjeta inexistente"   
		 * ret =>  98    ||   "Error mensaje"   
		 * ret =>  99    ||   "Causa dif a 00 o Tj Vencida"   
		-------------------------------------------------------------------------------	*/

		evalRetJPOSResumenCuenta.addSwitchValue("00",
				stepAudioMenuResumenCuenta.GetId());

		evalRetJPOSResumenCuenta.addSwitchValue("83",
				stepAudioNoEsPosibleConAdicional.GetId());

		evalRetJPOSResumenCuenta.addSwitchValue("96",
				stepAudioTarjetaIncorrecta.GetId());

		evalRetJPOSResumenCuenta.addSwitchValue("99",
				stepAudioIrregularidadCuenta.GetId());

		evalRetJPOSResumenCuenta.addSwitchValue("EE",
				stepAudioServNoDisponible.GetId());
	}

	private void evalCodEstudio() {

		/*-------------------------------------------------------------------------------
		 * ret =>  01    ||   "Estudio MoyPC"    
		 * ret =>  02    ||   "Estudio Packtar"    
		 * ret =>  03    ||   "Estudio Cobranzas TCI"   
		 * ret =>  04    ||   "Estudio Dogliani"   
		 * ret =>  05    ||   "Estudio Recovery"   
		 * ret =>  06    ||   "Estudio ML"   
		-------------------------------------------------------------------------------	*/

		evalCodEstudio.addSwitchValue("00", stepAudioEstudioMoyPc.GetId());
		evalCodEstudio.addSwitchValue("01", stepAudioEstudioMoyPc.GetId());
		evalCodEstudio.addSwitchValue("02", stepAudioEstudioPacktar.GetId());
		evalCodEstudio.addSwitchValue("03",
				stepAudioEstudioCobranzasTCI.GetId());
		evalCodEstudio.addSwitchValue("04", stepAudioEstudioDogliani.GetId());
		evalCodEstudio.addSwitchValue("05", stepAudioEstudioRecoveryc.GetId());
		evalCodEstudio.addSwitchValue("06", stepAudioEstudioML.GetId());
	}

	private void evalIngresoMenuDirecciones() {

		/*-------------------------------------------------------------------------------
		 * ret =>  1    ||   "Estudio MoyPC"    
		 * ret =>  2    ||   "Estudio Packtar"    
		 * ret =>  3    ||   "Estudio Cobranzas TCI"   
		
		-------------------------------------------------------------------------------	*/

		evalIngresoMenuDirecciones.addSwitchValue("1",
				stepAudioDireccionesCapital.GetId());
		evalIngresoMenuDirecciones.addSwitchValue("2",
				stepAudioDireccionesBA.GetId());
		evalIngresoMenuDirecciones.addSwitchValue("3",
				stepAudioDireccionesProvincias.GetId());

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

		// --CONTEXT GENERALES--//

		mesContextVar = this.getContextVar("mesContextVar", "", AstUid,
				"mesContextVar");

		anioContextVar = this.getContextVar("anioContextVar", "", AstUid,
				"anioContextVar");
		anioContextVar.setStringFormat("%02d");

		dniContextVar = this.getContextVar("dniContextVar", "", AstUid,
				"dniContextVar");
		dniContextVar.setStringFormat("%08d");

		dniParaActivacionContextVar = this.getContextVar(
				"dniParaActivacionContextVar", "", AstUid,
				"dniParaActivacionContextVar");
		dniParaActivacionContextVar.setStringFormat("%011d");

		tarjetaContexVar = this.getContextVar("tarjetaContexVar", "", AstUid,
				"tarjetaContexVar");

		tarjetaContexVar.setStringFormat("%16d");

		fechaContextVar = this.getContextVar("fechaContextVar", "", AstUid,
				"fechaContextVar");

		fdnContexVar = this.getContextVar("fdnContexVar", "", AstUid,
				"fdnContexVar");

		contextVarMes = this.getContextVar("contextVarMes", "", AstUid,
				"contextVarMes");

		contextVarAnio = this.getContextVar("contextVarAnio", "", AstUid,
				"contextVarAnio");
		contextVarAnio.setStringFormat("%02d");

		contextVarDia = this.getContextVar("contextVarDia", "", AstUid,
				"contextVarDia");

		// ---CONFIRMA---//

		confirmaDniContextVar = this.getContextVar("confirmaDniContextVar", "",
				AstUid, "confirmaDniContextVar");

		confirmaTarjetaContextVar = this.getContextVar(
				"confirmaTarjetaContextVar", "", AstUid,
				"confirmaTarjetaContextVar");

		confirmaFechaContextVar = this.getContextVar("confirmaFechaContextVar",
				"", AstUid, "confirmaFechaContextVar");

		confirmacionCambioDeResumenContexVar = this.getContextVar(
				"confirmacionCambioDeResumenContexVar", "92", AstUid,
				"confirmacionCambioDeResumenContexVar");

		consultaCambioDeResumenContexVar = this.getContextVar(
				"consultaCambioDeResumenContexVar", "02", AstUid,
				"consultaCambioDeResumenContexVar");

		// ---INTENTOS---//

		intentosDniContextVar = this.getContextVar("intentosDniContextVar",
				"0", AstUid, "intentosDniContextVar");

		intentosSubMenuContextVar = this.getContextVar(
				"intentosSubMenuContextVar", "0", AstUid,
				"intentosSubMenuContextVar");

		intentosMenuNuevosClientesContextVar = this.getContextVar(
				"intentosMenuNuevosClientesContextVar", "0", AstUid,
				"intentosMenuNuevosClientesContextVar");

		intentosMenuDerivoContextVar = this.getContextVar(
				"intentosMenuDerivoContextVar", "0", AstUid,
				"intentosMenuDerivoContextVar");

		intentosFechaContextVar = this.getContextVar("intentosFechaContextVar",
				"0", AstUid, "intentosFechaContextVar");

		intentosIngresoContextVar = this.getContextVar(
				"intentosIngresoContextVar", "0", AstUid,
				"intentosIngresoContextVar");

		intentosMenuDireccionesContextVar = this.getContextVar(
				"intentosMenuDireccionesContextVar", "0", AstUid,
				"intentosMenuDireccionesContextVar");

		intentosTarjetaContextVar = this.getContextVar(
				"intentosTarjetaContextVar", "0", AstUid,
				"intentosTarjetaContextVar");

		intentosMenuDenunciaContextVar = this.getContextVar(
				"intentosMenuDenunciaContextVar", "0", AstUid,
				"intentosMenuDenunciaContextVar");

		intentosMenuInicialContextVar = this.getContextVar(
				"intentosMenuInicialContextVar", "0", AstUid,
				"intentosMenuInicialContextVar");

		intentosPrimerTarjetaContextVar = this.getContextVar(
				"intentosPrimerTarjetaContextVar", "", AstUid,
				"intentosPrimerTarjetaContextVar");

		intentosMenuResumenCuentaContextVar = this.getContextVar(
				"intentosMenuResumenCuentaContextVar", "0", AstUid,
				"intentosMenuResumenCuentaContextVar");

		intentosMenuFinalContextVar = this.getContextVar(
				"intentosMenuFinalContextVar", "", AstUid,
				"intentosMenuFinalContextVar");

		intentosMenuIrregularidadCuentaContextVar = this.getContextVar(
				"intentosMenuIrregularidadCuentaContextVar", "", AstUid,
				"intentosMenuIrregularidadCuentaContextVar");

		// ---RESULTADO DE PLAYREAD ---//

		resultadoSubMenuContextVar = this.getContextVar(
				"resultadoSubMenuContextVar", "", AstUid,
				"resultadoSubMenuContextVar");

		resultadoAudioMenuDenuncia = this.getContextVar(
				"resultadoAudioMenuDenuncia", "", AstUid,
				"resultadoAudioMenuDenuncia");

		resultadoMenuDireccionesContextVar = this.getContextVar(
				"resultadoMenuDireccionesContextVar", "", AstUid,
				"resultadoMenuDireccionesContextVar");

		resultadoSubMenuDireccionesContextVar = this.getContextVar(
				"resultadoSubMenuDireccionesContextVar", "", AstUid,
				"resultadoSubMenuDireccionesContextVar");

		resultadoMenuBloqueoContextVar = this.getContextVar(
				"resultadoMenuBloqueoContextVar", "", AstUid,
				"resultadoMenuBloqueoContextVar");

		resultadoAudioMenuNuevosClientes = this.getContextVar(
				"resultadoAudioMenuNuevosClientes", "", AstUid,
				"resultadoAudioMenuNuevosClientes");

		resultadoAudioMenuPrincipalNuevosClientes = this.getContextVar(
				"resultadoAudioMenuPrincipalNuevosClientes", "", AstUid,
				"resultadoAudioMenuPrincipalNuevosClientes");

		resultadoAudioInicio = this.getContextVar("resultadoAudioInicio", "",
				AstUid, "resultadoAudioInicio");

		resultadoAudioMenuResumenCuenta = this.getContextVar(
				"resultadoAudioMenuResumenCuenta", "", AstUid,
				"resultadoAudioMenuResumenCuenta");

		resultadoAudioMenuIrregularidadCuenta = this.getContextVar(
				"resultadoAudioMenuIrregularidadCuenta", "1", AstUid,
				"resultadoAudioMenuIrregularidadCuenta");

		resultadoMenuIteracionMenuMoraContextVar = this.getContextVar(
				"resultadoMenuIteracionMenuMoraContextVar", "", AstUid,
				"resultadoMenuIteracionMenuMoraContextVar");

		// ---MENUŚ---//

		menuIngresoContextVar = this.getContextVar("menuIngresoContextVar", "",
				AstUid, "menuIngresoContextVar");

		menuFinalContextVar = this.getContextVar("menuFinalContextVar", "",
				AstUid, "menuFinalContextVar");

		menuIngresoActivacionContextVar = this.getContextVar(
				"menuIngresoActivacionContextVar", "", AstUid,
				"menuIngresoActivacionContextVar");

		menuDerivoContextVar = this.getContextVar("menuDerivoContextVar", "",
				AstUid, "menuDerivoContextVar");

		menuIngresoBajaSeguro = this.getContextVar("menuIngresoBajaSeguro", "",
				AstUid, "menuIngresoBajaSeguro");

		// ---JPOS---//

		retornoJPOS = this.getContextVar("retornoJPOS", "", AstUid,
				"retornoJPOS");

		mensajeActivacionJpos = this.getContextVar("mensajeActivacionJpos",
				"6", AstUid, "mensajeActivacionJpos");

		mensajeSaldosJpos = this.getContextVar("mensajeSaldosJpos", "5",
				AstUid, "mensajeSaldosJpos");

		mensajeCambioDeResumenJpos = this.getContextVar(
				"mensajeCambioDeResumenJpos", "7", AstUid,
				"mensajeCambioDeResumenJpos");

		retornoMsgJPOS = this.getContextVar("retornoMsgJPOS", "", AstUid,
				"retornoMsgJPOS");

		envioServerJposConsultasContexVar = this.getContextVar(
				"envioServerJposConsultasContexVar", "consultas", AstUid,
				"envioServerJposConsultasContexVar");

		envioServerJposPrecargadasContexVar = this.getContextVar(
				"envioServerJposPrecargadasContexVar", "precargada", AstUid,
				"envioServerJposPrecargadasContexVar");

		envioServerJposAutorizacionesContexVar = this.getContextVar(
				"envioServerJposAutorizacionesContexVar", "autorizaciones",
				AstUid, "envioServerJposAutorizacionesContexVar");

		cambiaEjecutoJPOSContextVar = this.getContextVar(
				"cambiaEjecutoJPOSContextVar", "1", AstUid,
				"cambiaEjecutoJPOSContextVar");

		ejecutoJPOSContextVar = this.getContextVar("ejecutoJPOSContextVar",
				"0", AstUid, "ejecutoJPOSContextVar");

		retornoJPOSResumenCuenta = this.getContextVar(
				"retornoJPOSResumenCuenta", "", AstUid,
				"retornoJPOSResumenCuenta");

		retornoMsgJPOSResumenCuenta = this.getContextVar(
				"retornoMsgJPOSResumenCuenta", "", AstUid,
				"retornoMsgJPOSResumenCuenta");

		// ---VARIOS | ARMADO DE TRAMA ---//

		fillerContexVar = this.getContextVar("fillerContexVar", " ", AstUid,
				"fillerContexVar");

		fillerContexVar.setStringFormat("%81s");

		fillerParaActivacionContexVar = this.getContextVar(
				"fillerParaActivacionContexVar", " ", AstUid,
				"fillerParaActivacionContexVar");
		fillerParaActivacionContexVar.setStringFormat("%50s");

		idLlamadaContexVar = this.getContextVar("idLlamadaContexVar",
				ast_uid.substring(ast_uid.length() - 29), AstUid,
				"idLlamadaContexVar");

		whisperContextVar = this.getContextVar("whisperContextVar", "0",
				AstUid, "whisperContextVar");

		numeroDeLineaContexVar = this.getContextVar("numeroDeLineaContexVar",
				"01", AstUid, "numeroDeLineaContexVar");

		// ESTUDIO DE COBRANZAS

		codigoDeEstudioContextVar = this.getContextVar(
				"codigoDeEstudioContextVar", "", AstUid,
				"codigoDeEstudioContextVar");

		// idTramiteContextVar = this.getContextVar("idTramiteContextVar", "01",
		// AstUid, "idTramiteContextVar");
		//
		// codJposBajaSegContextVar = this.getContextVar(
		// "codJposBajaSegContextVar", "7", AstUid,
		// "codJposBajaSegContextVar");

		// PARSER SALDO

		salEnUnPagoContextVar = this.getContextVar("salEnUnPagoContextVar", "",
				AstUid, "salEnUnPagoContextVar");

		salEnCuotasContextVar = this.getContextVar("salEnCuotasContextVar", "",
				AstUid, "salEnCuotasContextVar");

		fechaVencimientoUltResumContextVarDia = this.getContextVar(
				"fechaVencimientoUltResumContextVarDia", "", AstUid,
				"fechaVencimientoUltResumContextVarDia");

		fechaVencimientoUltResumContextVarMes = this.getContextVar(
				"fechaVencimientoUltResumContextVarMes", "", AstUid,
				"fechaVencimientoUltResumContextVarMes");

		fechaVencimientoUltResumContextVarAnio = this.getContextVar(
				"fechaVencimientoUltResumContextVarAnio", "", AstUid,
				"fechaVencimientoUltResumContextVarAnio");

		saldoUltResumContextVar = this.getContextVar("saldoUltResumContextVar",
				"", AstUid, "saldoUltResumContextVar");

		pagoMinUltResumContextVar = this.getContextVar(
				"pagoMinUltResumContextVar", "", AstUid,
				"pagoMinUltResumContextVar");

		totalPagUltResumContextVar = this.getContextVar(
				"totalPagUltResumContextVar", "", AstUid,
				"totalPagUltResumContextVar");

		salPendienteDeCanceUltResumContextVar = this.getContextVar(
				"salPendienteDeCanceUltResumContextVar", "", AstUid,
				"salPendienteDeCanceUltResumContextVar");

		pagoMinPendienteDeCanceUltResumContextVar = this.getContextVar(
				"pagoMinPendienteDeCanceUltResumContextVar", "", AstUid,
				"pagoMinPendienteDeCanceUltResumContextVar");

		cierreProxResumDiaContextVar = this.getContextVar(
				"cierreProxResumDiaContextVar", "", AstUid,
				"cierreProxResumDiaContextVar");

		cierreProxResumMesContextVar = this.getContextVar(
				"cierreProxResumMesContextVar", "", AstUid,
				"cierreProxResumMesContextVar");

		cierreProxResumAnioContextVar = this.getContextVar(
				"cierreProxResumAnioContextVar", "", AstUid,
				"cierreProxResumAnioContextVar");

		vencimientoProxResumDiaContextVar = this.getContextVar(
				"vencimientoProxResumDiaContextVar", "", AstUid,
				"vencimientoProxResumDiaContextVar");

		vencimientoProxResumMesContextVar = this.getContextVar(
				"vencimientoProxResumMesContextVar", "", AstUid,
				"vencimientoProxResumMesContextVar");

		vencimientoProxResumAnioContextVar = this.getContextVar(
				"vencimientoProxResumAnioContextVar", "", AstUid,
				"vencimientoProxResumAnioContextVar");

		saldoCuentaContextVar = this.getContextVar("saldoCuentaContextVar", "",
				AstUid, "saldoCuentaContextVar");

		saldoUltResumDecimalContextVar = this.getContextVar(
				"saldoUltResumDecimalContextVar", "", AstUid,
				"saldoUltResumDecimalContextVar");

		totalPagUltResumDecimalContextVar = this.getContextVar(
				"totalPagUltResumDecimalContextVar", "", AstUid,
				"totalPagUltResumDecimalContextVar");

		pagoMinUltResumDecimContextVar = this.getContextVar(
				"pagoMinUltResumDecimContextVar", "", AstUid,
				"pagoMinUltResumDecimContextVar");

		salEnUnPagoDecimalContextVar = this.getContextVar(
				"salEnUnPagoDecimalContextVar", "", AstUid,
				"salEnUnPagoDecimalContextVar");

		salEnCuotasDecimalContextVar = this.getContextVar(
				"salEnCuotasDecimalContextVar", "", AstUid,
				"salEnCuotasDecimalContextVar");

		pagoMinPendienteDeCanceUltDecimalResumContextVar = this.getContextVar(
				"pagoMinPendienteDeCanceUltDecimalResumContextVar", "", AstUid,
				"pagoMinPendienteDeCanceUltDecimalResumContextVar");

		saldoCuentaDecimalContextVar = this.getContextVar(
				"saldoCuentaDecimalContextVar", "", AstUid,
				"saldoCuentaDecimalContextVar");

		salPendienteDeCanceUltResumDecimalContextVar = this.getContextVar(
				"salPendienteDeCanceUltResumDecimalContextVar", "", AstUid,
				"salPendienteDeCanceUltResumDecimalContextVar");

		primerTarjetaContextVar = this.getContextVar("primerTarjetaContextVar",
				"", AstUid, "primerTarjetaContextVar");

		cambiaPrimerTarjetaContextVar = this.getContextVar(
				"cambiaPrimerTarjetaContextVar", "2", AstUid,
				"cambiaPrimerTarjetaContextVar");

		tarjetaPendienteDeActivacionContextVar = this.getContextVar(
				"tarjetaPendienteDeActivacionContextVar", "", AstUid,
				"tarjetaPendienteDeActivacionContextVar");

		// VARIOS

		empresaIdContextVar = this.getContextVar("empresaIdContextVar", "2",
				AstUid, "empresaIdContextVar");

		servicioIdContextVar = this.getContextVar("servicioIdContextVar", "1",
				AstUid, "servicioIdContextVar");

		audioFueraHorarioContextVar = this.getContextVar(
				"audioFueraHorarioContextVar", "", AstUid,
				"audioFueraHorarioContextVar");

		scapeDigitContextVar = this.getContextVar("scapeDigitContextVar", "",
				AstUid, "scapeDigitContextVar");

		nroCuentaContexVar = this.getContextVar("nroCuentaContexVar",
				"0000000", AstUid, "nroCuentaContexVar");

		nroCuentaParaActivacionContexVar = this.getContextVar(
				"nroCuentaParaActivacionContexVar", "0000000000000", AstUid,
				"nroCuentaParaActivacionContexVar");

		diaContextVar = this.getContextVar("diaContextVar", "", AstUid,
				"diaContextVar");

	}
}
