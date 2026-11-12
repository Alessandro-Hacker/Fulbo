// Ciclo de vida del partido: alta, cancelacion y cierre (diagramas 4, 5 y 8).
import { onCall, HttpsError } from "firebase-functions/v2/https";
import { onDocumentCreated } from "firebase-functions/v2/firestore";
import { geohashForLocation } from "geofire-common";
import {
  db, FieldValue, Timestamp, HORAS_AVISO_CANCELACION, HORAS_VENTANA_CALIFICACION,
  exigirSesion, notificar, refInscripcion, refPartido, refUsuario,
} from "./comun";

/**
 * Cuando la app crea un partido (las reglas ya validaron los campos):
 *  - inscribe al organizador como primer jugador,
 *  - calcula el geohash para la busqueda por cercania,
 *  - copia alias y reputacion del organizador para pintar la tarjeta sin
 *    leer otro documento.
 */
export const alCrearPartido = onDocumentCreated("partidos/{partidoId}", async (event) => {
  const snap = event.data;
  if (!snap) return;
  const partido = snap.data();
  const partidoId = event.params.partidoId;
  const org = (await refUsuario(partido.organizador_uid).get()).data();
  if (!org) return;

  const batch = db.batch();
  batch.update(snap.ref, {
    geohash: geohashForLocation([partido.ubicacion.latitude, partido.ubicacion.longitude]),
    organizador_alias: org.alias,
    organizador_rep: org.rep_global,
  });
  batch.set(refInscripcion(partidoId, partido.organizador_uid), {
    usuario_uid: partido.organizador_uid,
    partido_id: partidoId,
    alias: org.alias,
    rep_global: org.rep_global,
    estado: "aceptado",
    asistio: null,
    pago: null,
    fecha_hora: partido.fecha_hora,
    creado_en: FieldValue.serverTimestamp(),
  });
  await batch.commit();
  // TODO: notificar a jugadores cercanos del nivel buscado (consulta por geohash
  // sobre usuarios con ubicacion). Conviene hacerlo con un tema FCM por ciudad.
});

export const cancelarPartido = onCall(async (req) => {
  const uid = exigirSesion(req);
  const { partidoId } = req.data as { partidoId: string };
  const pRef = refPartido(partidoId);

  const inscritos: string[] = [];
  let penalizado = false;
  await db.runTransaction(async (tx) => {
    const p = await tx.get(pRef);
    if (!p.exists) throw new HttpsError("not-found", "No existe.");
    const partido = p.data()!;
    if (partido.organizador_uid !== uid) throw new HttpsError("permission-denied", "No eres el organizador.");
    if (!["publicado", "completo"].includes(partido.estado)) {
      throw new HttpsError("failed-precondition", "Ya no se puede cancelar.");
    }
    const insc = await tx.get(pRef.collection("inscripciones").where("estado", "==", "aceptado"));
    insc.forEach((d) => { if (d.id !== uid) inscritos.push(d.id); });

    // Cancelar con menos de 3 h de aviso suma una falta al organizador.
    const horas = (partido.fecha_hora.toMillis() - Date.now()) / 3_600_000;
    penalizado = horas < HORAS_AVISO_CANCELACION;
    tx.update(pRef, { estado: "cancelado", cancelado_en: FieldValue.serverTimestamp() });
    if (penalizado) tx.update(refUsuario(uid), { faltas: FieldValue.increment(1) });
  });

  await notificar(inscritos, "Partido cancelado", "El organizador cancelo el partido.",
    { partidoId, tipo: "cancelado" });
  return { penalizado };
});

/**
 * El organizador marca quien asistio y quien pago. Abre la ventana de 48 h
 * para calificar (estado en_calificacion).
 */
export const finalizarPartido = onCall(async (req) => {
  const uid = exigirSesion(req);
  const { partidoId, asistencias } = req.data as {
    partidoId: string;
    asistencias: Record<string, { asistio: boolean; pago: boolean }>;
  };
  const pRef = refPartido(partidoId);
  const presentes: string[] = [];

  await db.runTransaction(async (tx) => {
    const p = await tx.get(pRef);
    if (!p.exists) throw new HttpsError("not-found", "No existe.");
    const partido = p.data()!;
    if (partido.organizador_uid !== uid) throw new HttpsError("permission-denied", "No eres el organizador.");
    if (partido.estado !== "en_juego") throw new HttpsError("failed-precondition", "El partido no esta en juego.");

    const insc = await tx.get(pRef.collection("inscripciones").where("estado", "==", "aceptado"));
    insc.forEach((d) => {
      const a = asistencias[d.id] ?? { asistio: d.id === uid, pago: false };
      tx.update(d.ref, { asistio: a.asistio, pago: a.pago });
      tx.update(refUsuario(d.id), a.asistio
        ? { partidos_jugados: FieldValue.increment(1) }
        : { faltas: FieldValue.increment(1) });
      if (a.asistio) presentes.push(d.id);
    });
    tx.update(pRef, {
      estado: "en_calificacion",
      calificacion_cierra_en: Timestamp.fromMillis(Date.now() + HORAS_VENTANA_CALIFICACION * 3_600_000),
    });
  });

  await notificar(presentes, "Califica el partido", "Tienes 48 horas para calificar a los participantes.",
    { partidoId, tipo: "calificar" });
  return { ok: true };
});
