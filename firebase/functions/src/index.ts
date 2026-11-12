// Punto de entrada: cada export es una Cloud Function que se despliega.
export { completarPerfil } from "./perfil";
export { solicitarInscripcion, resolverSolicitud, darseDeBaja } from "./inscripciones";
export { alCrearPartido, cancelarPartido, finalizarPartido } from "./partidos";
export { alCrearCalificacion } from "./reputacion";
export { tareasCadaQuinceMinutos } from "./programadas";
export { webhookPago } from "./pagos";
export { borrarCuenta } from "./cuenta";
