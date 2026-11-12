// Unirse, aprobar y darse de baja de un partido.
// Flujo 3 y diagrama de secuencia 11. Todo dentro de transacciones para que
// dos jugadores no tomen el ultimo cupo en el mismo segundo.
import { onCall, HttpsError } from "firebase-functions/v2/https";
import {
  db, FieldValue, EstadoInscripcion, exigirSesion, notificar,
  refInscripcion, refPartido, refUsuario,
} from "./comun";

type Resultado = "aceptado" | "pendiente" | "lista_espera";

export const solicitarInscripcion = onCall(async (req) => {
  const uid = exigirSesion(req);
  const { partidoId, listaEspera } = req.data as { partidoId: string; listaEspera?: boolean };
  if (typeof partidoId !== "string") throw new HttpsError("invalid-argument", "partidoId");

  let resultado = "aceptado" as Resultado;
  let organizadorUid = "";
  let seLleno = false;

  await db.runTransaction(async (tx) => {
    const pRef = refPartido(partidoId);
    const iRef = refInscripcion(partidoId, uid);
    const [p, u, previa] = await Promise.all([tx.get(pRef), tx.get(refUsuario(uid)), tx.get(iRef)]);

    if (!p.exists) throw new HttpsError("not-found", "El partido no existe.");
    if (!u.exists) throw new HttpsError("failed-precondition", "Completa tu perfil.");
    const partido = p.data()!;
    const usuario = u.data()!;
    organizadorUid = partido.organizador_uid;

    if (organizadorUid === uid) throw new HttpsError("already-exists", "Ya eres el organizador.");
    if (previa.exists && previa.data()!.estado !== "baja") {
      throw new HttpsError("already-exists", "Ya tienes una solicitud en este partido.");
    }
    if (!["publicado", "completo"].includes(partido.estado)) {
      throw new HttpsError("failed-precondition", "Este partido ya no acepta jugadores.");
    }

    // 1) Sin cupos -> lista de espera (solo si el jugador la acepto)
    if (partido.cupos_libres <= 0) {
      if (!listaEspera) throw new HttpsError("resource-exhausted", "partido-lleno");
      resultado = "lista_espera";
    // 2) Reputacion bajo el minimo -> la decide el organizador.
    //    Un jugador nuevo (rep_global null) tambien pasa por aprobacion.
    } else if (partido.rep_minima != null &&
               (usuario.rep_global == null || usuario.rep_global < partido.rep_minima)) {
      resultado = "pendiente";
    } else {
      resultado = "aceptado";
    }

    tx.set(iRef, {
      usuario_uid: uid,
      partido_id: partidoId,
      alias: usuario.alias,
      rep_global: usuario.rep_global,
      estado: resultado as EstadoInscripcion,
      asistio: null,
      pago: null,
      fecha_hora: partido.fecha_hora, // duplicado para ordenar "Mis partidos"
      creado_en: FieldValue.serverTimestamp(),
    });

    if (resultado === "aceptado") {
      const libres = partido.cupos_libres - 1;
      seLleno = libres === 0;
      tx.update(pRef, {
        cupos_libres: libres,
        ...(seLleno ? { estado: "completo" } : {}),
      });
    }
  });

  // Las notificaciones salen del servidor, nunca de la app (decision del diagrama 11).
  if (resultado === "pendiente") {
    await notificar([organizadorUid], "Solicitud nueva", "Un jugador quiere unirse a tu partido.",
      { partidoId, tipo: "solicitud" });
  } else if (resultado === "aceptado") {
    await notificar([organizadorUid], "Se sumo un jugador",
      seLleno ? "Tu partido esta completo." : "Queda un cupo menos.", { partidoId, tipo: "inscripcion" });
  }
  return { resultado };
});

export const resolverSolicitud = onCall(async (req) => {
  const uid = exigirSesion(req);
  const { partidoId, jugadorUid, aceptar } =
    req.data as { partidoId: string; jugadorUid: string; aceptar: boolean };

  let final = "rechazado" as EstadoInscripcion;
  await db.runTransaction(async (tx) => {
    const pRef = refPartido(partidoId);
    const iRef = refInscripcion(partidoId, jugadorUid);
    const [p, i] = await Promise.all([tx.get(pRef), tx.get(iRef)]);
    if (!p.exists || !i.exists) throw new HttpsError("not-found", "No encontrado.");
    const partido = p.data()!;
    if (partido.organizador_uid !== uid) throw new HttpsError("permission-denied", "No eres el organizador.");
    if (i.data()!.estado !== "pendiente") throw new HttpsError("failed-precondition", "Ya fue resuelta.");

    if (aceptar && partido.cupos_libres > 0) {
      final = "aceptado";
      const libres = partido.cupos_libres - 1;
      tx.update(pRef, { cupos_libres: libres, ...(libres === 0 ? { estado: "completo" } : {}) });
    } else {
      final = aceptar ? "lista_espera" : "rechazado";
    }
    tx.update(iRef, { estado: final });
  });

  const msg = final === "aceptado" ? "Estas dentro del partido."
    : final === "lista_espera" ? "Se llenaron los cupos: quedaste en lista de espera."
    : "El organizador no acepto tu solicitud.";
  await notificar([jugadorUid], "Tu solicitud", msg, { partidoId, tipo: "resolucion" });
  return { estado: final };
});

export const darseDeBaja = onCall(async (req) => {
  const uid = exigirSesion(req);
  const { partidoId } = req.data as { partidoId: string };
  let liberoCupo = false;
  let siguiente: string | null = null;

  await db.runTransaction(async (tx) => {
    const pRef = refPartido(partidoId);
    const iRef = refInscripcion(partidoId, uid);
    const espera = await tx.get(
      pRef.collection("inscripciones").where("estado", "==", "lista_espera")
        .orderBy("creado_en").limit(1));
    const [p, i] = await Promise.all([tx.get(pRef), tx.get(iRef)]);
    if (!p.exists || !i.exists) throw new HttpsError("not-found", "No estas inscrito.");
    const partido = p.data()!;
    if (partido.organizador_uid === uid) {
      throw new HttpsError("failed-precondition", "El organizador cancela el partido, no se da de baja.");
    }
    if (!["publicado", "completo"].includes(partido.estado)) {
      throw new HttpsError("failed-precondition", "El partido ya empezo o termino.");
    }

    const estabaAceptado = i.data()!.estado === "aceptado";
    tx.update(iRef, { estado: "baja" });

    if (estabaAceptado) {
      liberoCupo = true;
      if (!espera.empty) {
        // El primero de la lista de espera toma el cupo: cupos_libres no cambia.
        siguiente = espera.docs[0].id;
        tx.update(espera.docs[0].ref, { estado: "aceptado" });
      } else {
        // Completo -> Publicado: el partido vuelve a aparecer en la busqueda.
        tx.update(pRef, { cupos_libres: partido.cupos_libres + 1, estado: "publicado" });
      }
    }
  });

  if (siguiente) {
    await notificar([siguiente], "Se libero un cupo", "Saliste de la lista de espera: estas dentro.",
      { partidoId, tipo: "inscripcion" });
  }
  return { liberoCupo };
});
